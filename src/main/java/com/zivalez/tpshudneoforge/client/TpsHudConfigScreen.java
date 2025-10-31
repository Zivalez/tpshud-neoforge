package com.zivalez.tpshudneoforge.client;

import com.zivalez.tpshudneoforge.client.hud.TpsOverlay;
import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import com.zivalez.tpshudneoforge.config.TpsHudConfig.Anchor;
import com.zivalez.tpshudneoforge.config.TpsHudConfig.Format;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TpsHudConfigScreen extends Screen {

    private final Screen parent;
    private ModelView mv;
    private boolean preview = false;

    // ---- Layout constants ----
    private int TITLE_Y;
    private int COL_LEFT_X;
    private int LABEL_W;
    private int CTRL_X;
    private int CTRL_W;
    private int CUR_Y;
    private static final int ROW_H = 22;
    private static final int GAP_Y = 6;

    private final List<Label> labels = new ArrayList<>();
    private final List<Tip> tips = new ArrayList<>();

    public TpsHudConfigScreen(Screen parent) {
        super(Component.literal("TPS HUD Config"));
        this.parent = parent;
    }

    // ---------- Helpers ----------
    private void layoutReset() {
        int pad = 16;
        int usableW = Math.max(320, this.width - pad * 2);

        TITLE_Y = 12;

        int formW = Math.min(usableW, 380);
        COL_LEFT_X = (this.width - formW) / 2;
        LABEL_W = 128;
        CTRL_X = COL_LEFT_X + LABEL_W + 12;
        CTRL_W = formW - (LABEL_W + 12);
        CUR_Y  = TITLE_Y + 24 + 10;
    }

    private void addLabel(String text) {
        labels.add(new Label(COL_LEFT_X, CUR_Y + 5, Component.literal(text)));
    }

    private void nextRow() {
        CUR_Y += ROW_H + GAP_Y;
    }

    private void addTip(AbstractWidget w, String text) {
        tips.add(new Tip(w, Component.literal(text)));
    }

    private static String trimDouble(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-6) return String.valueOf((long) Math.round(v));
        return String.valueOf(v);
    }

    private static int clampInt(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private TpsHudConfig toConfigFromMV() {
        TpsHudConfig c = new TpsHudConfig();
        mv.applyTo(c);
        return c;
    }

    private void togglePreview() {
        this.preview = !this.preview;
        this.init();
    }

    // ---------- Screen lifecycle ----------
    @Override
    protected void init() {
        this.clearWidgets();
        this.labels.clear();
        this.tips.clear();

        if (this.mv == null) this.mv = new ModelView(ConfigManager.get());
        layoutReset();

        // ===== Title (centered) ===== (drawn in render())

        if (preview) {
            // -------- PREVIEW MODE --------
            int cx = this.width / 2;

            var exit = Button.builder(Component.literal("Exit Preview"), b -> togglePreview())
                    .bounds(cx - 60, this.height - 28, 120, 20)
                    .build();
            addRenderableWidget(exit);

            return;
        }

        // -------- NORMAL MODE (form) --------

        // Enabled
        addLabel("Enabled");
        var enabledBtn = CycleButton.booleanBuilder(Component.literal("ON"), Component.literal("OFF"))
                .displayOnlyValue()
                .create(CTRL_X, CUR_Y, CTRL_W, 20, Component.empty(),
                        (btn, v) -> mv.enabled = v);
        enabledBtn.setValue(mv.enabled);
        addRenderableWidget(enabledBtn);
        addTip(enabledBtn, "Show or hide the TPS HUD overlay.");
        nextRow();

        // Anchor
        addLabel("Anchor");
        var anchorBtn = CycleButton.<Anchor>builder(a -> Component.literal(a.name().replace('_', '-')))
                .withValues(Anchor.values())
                .displayOnlyValue()
                .create(CTRL_X, CUR_Y, CTRL_W, 20, Component.empty(),
                        (btn, v) -> mv.anchor = v);
        anchorBtn.setValue(mv.anchor);
        addRenderableWidget(anchorBtn);
        addTip(anchorBtn, "Choose which screen corner the HUD attaches to.");
        nextRow();

        // Padding
        addLabel("Padding (px)");
        var padBox = new EditBox(this.font, CTRL_X, CUR_Y, CTRL_W, 20, Component.empty());
        padBox.setValue(String.valueOf(mv.padding));
        padBox.setResponder(s -> {
            try { mv.padding = clampInt(Integer.parseInt(s.trim()), 0, 64); padBox.setTextColor(0xE0E0E0); }
            catch (Exception ex) { padBox.setTextColor(0xFF5555); }
        });
        addRenderableWidget(padBox);
        addTip(padBox, "Margin from the screen edges, in pixels.");
        nextRow();

        // Scale (%)
        addLabel("Scale (%)");
        final int btnW = 22;

        final EditBox scaleBox = new EditBox(this.font,
                CTRL_X + btnW + 4,
                CUR_Y,
                CTRL_W - (btnW + 4) * 2,
                20,
                Component.empty());
        scaleBox.setValue(String.valueOf(Math.round(mv.scale * 100)));
        scaleBox.setResponder(s -> {
            try {
                int p = Integer.parseInt(s.trim());
                mv.scale = Mth.clamp(p / 100f, 0.5f, 2.0f);
                scaleBox.setTextColor(0xE0E0E0);
            } catch (Exception ex) {
                scaleBox.setTextColor(0xFF5555);
            }
        });

        var scaleDec = Button.builder(Component.literal("-"), b -> {
                    mv.scale = Mth.clamp(mv.scale - 0.05f, 0.5f, 2.0f);
                    scaleBox.setValue(String.valueOf(Math.round(mv.scale * 100)));
                })
                .bounds(CTRL_X, CUR_Y, btnW, 20)
                .build();

        var scaleInc = Button.builder(Component.literal("+"), b -> {
                    mv.scale = Mth.clamp(mv.scale + 0.05f, 0.5f, 2.0f);
                    scaleBox.setValue(String.valueOf(Math.round(mv.scale * 100)));
                })
                .bounds(CTRL_X + CTRL_W - btnW, CUR_Y, btnW, 20)
                .build();

        addRenderableWidget(scaleDec);
        addRenderableWidget(scaleBox);
        addRenderableWidget(scaleInc);
        addTip(scaleDec, "Decrease HUD size.");
        addTip(scaleBox, "HUD size multiplier in percent (50%–200%).");
        addTip(scaleInc, "Increase HUD size.");
        nextRow();

        // Precision
        addLabel("Precision (decimals)");
        var precisionBtn = CycleButton.<Integer>builder(v -> Component.literal(String.valueOf(v)))
                .withValues(0, 1, 2, 3)
                .displayOnlyValue()
                .create(CTRL_X, CUR_Y, CTRL_W, 20, Component.empty(),
                        (btn, v) -> mv.precision = v);
        precisionBtn.setValue(mv.precision);
        addRenderableWidget(precisionBtn);
        addTip(precisionBtn, "Number of decimal places for TPS/MSPT values.");
        nextRow();

        // Format
        addLabel("Format");
        var formatBtn = CycleButton.<Format>builder(v -> Component.literal(v.name()))
                .withValues(Format.values())
                .displayOnlyValue()
                .create(CTRL_X, CUR_Y, CTRL_W, 20, Component.empty(),
                        (btn, v) -> mv.format = v);
        formatBtn.setValue(mv.format);
        addRenderableWidget(formatBtn);
        addTip(formatBtn, "Show TPS only, MSPT only, or BOTH (two lines).");
        nextRow();

        // Smoothing
        addLabel("Smoothing Window (samples)");
        var smoothBox = new EditBox(this.font, CTRL_X, CUR_Y, CTRL_W, 20, Component.empty());
        smoothBox.setValue(String.valueOf(mv.smoothingWindow));
        smoothBox.setResponder(s -> {
            try { mv.smoothingWindow = clampInt(Integer.parseInt(s.trim()), 5, 240); smoothBox.setTextColor(0xE0E0E0); }
            catch (Exception ex) { smoothBox.setTextColor(0xFF5555); }
        });
        addRenderableWidget(smoothBox);
        addTip(smoothBox, "Moving average window size. Larger = smoother but slower to react.");
        nextRow();

        // Auto-hide F3
        addLabel("Auto-hide when F3 open");
        var autoHideBtn = CycleButton.booleanBuilder(Component.literal("ON"), Component.literal("OFF"))
                .displayOnlyValue()
                .create(CTRL_X, CUR_Y, CTRL_W, 20, Component.empty(),
                        (btn, v) -> mv.autoHideF3 = v);
        autoHideBtn.setValue(mv.autoHideF3);
        addRenderableWidget(autoHideBtn);
        addTip(autoHideBtn, "Hide overlay while the debug screen (F3) is open.");
        nextRow();

        // Divider
        CUR_Y += 2;
        nextRow();

        // TPS thresholds
        addLabel("TPS Thresholds");
        CUR_Y += 14;

        int halfW = (CTRL_W - 8) / 2;
        var tpsWarn = new EditBox(this.font, CTRL_X, CUR_Y, halfW, 20, Component.empty());
        tpsWarn.setValue(trimDouble(mv.thresh.tpsWarn));
        tpsWarn.setResponder(s -> validateDoubleBox(tpsWarn, s, v -> mv.thresh.tpsWarn = clamp(v, 0, 20)));
        addRenderableWidget(tpsWarn);
        addTip(tpsWarn, "Warn if TPS ≤ this value.");

        var tpsBad = new EditBox(this.font, CTRL_X + halfW + 8, CUR_Y, halfW, 20, Component.empty());
        tpsBad.setValue(trimDouble(mv.thresh.tpsBad));
        tpsBad.setResponder(s -> validateDoubleBox(tpsBad, s, v -> mv.thresh.tpsBad = clamp(v, 0, 20)));
        addRenderableWidget(tpsBad);
        addTip(tpsBad, "Bad if TPS ≤ this value.");
        nextRow();

        var tpsGood = hexBox(CTRL_X, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.tpsGoodColor, v -> mv.thresh.tpsGoodColor = v);
        var tpsWarnC = hexBox(CTRL_X + (CTRL_W - 16) / 3 + 8, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.tpsWarnColor, v -> mv.thresh.tpsWarnColor = v);
        var tpsBadC  = hexBox(CTRL_X + 2 * ((CTRL_W - 16) / 3) + 16, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.tpsBadColor,  v -> mv.thresh.tpsBadColor  = v);
        addRenderableWidget(tpsGood);
        addRenderableWidget(tpsWarnC);
        addRenderableWidget(tpsBadC);
        addTip(tpsGood, "TPS good color (#RRGGBB).");
        addTip(tpsWarnC, "TPS warn color (#RRGGBB).");
        addTip(tpsBadC,  "TPS bad color (#RRGGBB).");
        nextRow();

        // MSPT thresholds
        addLabel("MSPT Thresholds");
        CUR_Y += 14;

        var msptWarn = new EditBox(this.font, CTRL_X, CUR_Y, halfW, 20, Component.empty());
        msptWarn.setValue(trimDouble(mv.thresh.msptWarn));
        msptWarn.setResponder(s -> validateDoubleBox(msptWarn, s, v -> mv.thresh.msptWarn = clamp(v, 0, 200)));
        addRenderableWidget(msptWarn);
        addTip(msptWarn, "Warn if MSPT ≥ this value. (Target is 50 ms)");

        var msptBad = new EditBox(this.font, CTRL_X + halfW + 8, CUR_Y, halfW, 20, Component.empty());
        msptBad.setValue(trimDouble(mv.thresh.msptBad));
        msptBad.setResponder(s -> validateDoubleBox(msptBad, s, v -> mv.thresh.msptBad = clamp(v, 0, 200)));
        addRenderableWidget(msptBad);
        addTip(msptBad, "Bad if MSPT ≥ this value.");
        nextRow();

        var msptGood = hexBox(CTRL_X, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.msptGoodColor, v -> mv.thresh.msptGoodColor = v);
        var msptWarnC = hexBox(CTRL_X + (CTRL_W - 16) / 3 + 8, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.msptWarnColor, v -> mv.thresh.msptWarnColor = v);
        var msptBadC  = hexBox(CTRL_X + 2 * ((CTRL_W - 16) / 3) + 16, CUR_Y, (CTRL_W - 16) / 3, mv.thresh.msptBadColor,  v -> mv.thresh.msptBadColor  = v);
        addRenderableWidget(msptGood);
        addRenderableWidget(msptWarnC);
        addRenderableWidget(msptBadC);
        addTip(msptGood, "MSPT good color (#RRGGBB).");
        addTip(msptWarnC, "MSPT warn color (#RRGGBB).");
        addTip(msptBadC,  "MSPT bad color (#RRGGBB).");
        nextRow();

        // Footer buttons: Done / Apply / Reset / Cancel / Preview
        int cx = this.width / 2;

        var done = Button.builder(Component.literal("Done"), b -> {
                    mv.applyTo(ConfigManager.get());
                    ConfigManager.save();
                    onClose();
                })
                .bounds(cx - 205, this.height - 28, 90, 20)
                .build();

        var apply = Button.builder(Component.literal("Apply"), b -> {
                    mv.applyTo(ConfigManager.get());
                    ConfigManager.save();
                })
                .bounds(cx - 105, this.height - 28, 90, 20)
                .build();

        var reset = Button.builder(Component.literal("Reset"), b -> {
                    this.mv = new ModelView(new TpsHudConfig());
                    this.init(); // full rebuild, avoids overlap
                })
                .bounds(cx - 5, this.height - 28, 90, 20)
                .build();

        var cancel = Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(cx + 95, this.height - 28, 90, 20)
                .build();

        var previewBtn = Button.builder(Component.literal("Preview"), b -> togglePreview())
                .bounds(cx + 195, this.height - 28, 90, 20)
                .build();

        addRenderableWidget(done);
        addRenderableWidget(apply);
        addRenderableWidget(reset);
        addRenderableWidget(cancel);
        addRenderableWidget(previewBtn);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        super.render(gfx, mouseX, mouseY, delta);

        // Title
        String title = preview ? "TPS HUD – Preview" : "TPS HUD – Settings";
        int titleW = this.font.width(title);
        gfx.drawString(this.font, title, (this.width - titleW) / 2, TITLE_Y, 0xFFFFFF, false);

        if (preview) {
            gfx.fill(0, 0, this.width, this.height, 0x66000000);

            TpsHudConfig cfg = toConfigFromMV();

            float sampleTps = 20.0f;
            float sampleMspt = 50.0f;

            TpsOverlay.renderPreview(gfx, cfg, sampleTps, sampleMspt, this.width, this.height);
            return;
        }

        for (var l : labels) {
            gfx.drawString(this.font, l.text(), l.x(), l.y(), 0xE0E0E0, false);
        }

        for (var tip : tips) {
            if (tip.widget.isMouseOver(mouseX, mouseY)) {
                gfx.renderTooltip(this.font, tip.text, mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    // ---------- Small utilities ----------
    private record Label(int x, int y, Component text) {}
    private record Tip(AbstractWidget widget, Component text) {}

    private static void validateDoubleBox(EditBox box, String s, java.util.function.DoubleConsumer onValid) {
        try {
            double v = Double.parseDouble(s.trim());
            box.setTextColor(0xE0E0E0);
            onValid.accept(v);
        } catch (Exception ex) {
            box.setTextColor(0xFF5555);
        }
    }

    private EditBox hexBox(int x, int y, int w, String init, java.util.function.Consumer<String> onValid) {
        var box = new EditBox(this.font, x, y, w, 20, Component.empty());
        box.setValue(init);
        box.setResponder(s -> {
            String t = s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
            if (t.startsWith("#")) t = t.substring(1);
            boolean ok = t.matches("[0-9A-F]{6}");
            box.setTextColor(ok ? 0xE0E0E0 : 0xFF5555);
            if (ok) onValid.accept("#" + t);
        });
        addTip(box, "Hex color (#RRGGBB).");
        return box;
    }

    private static final class ModelView {
        public boolean enabled;
        public Anchor anchor;
        public int padding;
        public float scale;
        public int precision;
        public Format format;
        public int smoothingWindow;
        public boolean autoHideF3;
        public final TpsHudConfig.Thresholds thresh = new TpsHudConfig.Thresholds();

        ModelView(TpsHudConfig src) {
            this.enabled = src.enabled;
            this.anchor = src.anchor;
            this.padding = src.padding;
            this.scale = src.scale;
            this.precision = src.precision;
            this.format = src.format;
            this.smoothingWindow = src.smoothingWindow;
            this.autoHideF3 = src.autoHideF3;

            this.thresh.tpsWarn = src.thresholds.tpsWarn;
            this.thresh.tpsBad = src.thresholds.tpsBad;
            this.thresh.tpsGoodColor = src.thresholds.tpsGoodColor;
            this.thresh.tpsWarnColor = src.thresholds.tpsWarnColor;
            this.thresh.tpsBadColor = src.thresholds.tpsBadColor;

            this.thresh.msptWarn = src.thresholds.msptWarn;
            this.thresh.msptBad = src.thresholds.msptBad;
            this.thresh.msptGoodColor = src.thresholds.msptGoodColor;
            this.thresh.msptWarnColor = src.thresholds.msptWarnColor;
            this.thresh.msptBadColor = src.thresholds.msptBadColor;
        }

        void applyTo(TpsHudConfig dst) {
            dst.enabled = enabled;
            dst.anchor = anchor;
            dst.padding = padding;
            dst.scale = scale;
            dst.precision = precision;
            dst.format = format;
            dst.smoothingWindow = smoothingWindow;
            dst.autoHideF3 = autoHideF3;

            dst.thresholds.tpsWarn = thresh.tpsWarn;
            dst.thresholds.tpsBad = thresh.tpsBad;
            dst.thresholds.tpsGoodColor = thresh.tpsGoodColor;
            dst.thresholds.tpsWarnColor = thresh.tpsWarnColor;
            dst.thresholds.tpsBadColor = thresh.tpsBadColor;

            dst.thresholds.msptWarn = thresh.msptWarn;
            dst.thresholds.msptBad = thresh.msptBad;
            dst.thresholds.msptGoodColor = thresh.msptGoodColor;
            dst.thresholds.msptWarnColor = thresh.msptWarnColor;
            dst.thresholds.msptBadColor = thresh.msptBadColor;
        }
    }
}
