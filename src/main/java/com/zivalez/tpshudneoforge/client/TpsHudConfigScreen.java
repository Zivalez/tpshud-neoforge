package com.zivalez.tpshudneoforge.client.config;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TpsHudConfigScreen extends Screen {

    private final Screen parent;
    private final List<net.minecraft.client.gui.components.AbstractWidget> widgets = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();

    private ModelView mv;

    public TpsHudConfigScreen(Screen parent) {
        super(Component.literal("TPS HUD Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        widgets.clear();
        labels.clear();

        TpsHudConfig cfg = ConfigManager.get();
        this.mv = new ModelView(cfg);

        int left = this.width / 2 - 160;
        int y = 30;
        int w = 320;
        int gap = 6;

        // Title
        labels.add(new Label(left, y, Component.literal("TPS HUD – Settings")));
        y += 14 + gap;

        // Enabled
        labels.add(new Label(left, y + 5, Component.literal("Enabled")));
        var enabledBtn = CycleButton.booleanBuilder(Component.literal("ON"), Component.literal("OFF"))
                .withInitialValue(mv.enabled)
                .displayOnlyValue(true)
                .create(left + 140, y, w - 140, 20, Component.empty());
        enabledBtn.setValue(mv.enabled);
        enabledBtn.setCallback((b, v) -> mv.enabled = v);
        widgets.add(enabledBtn);
        y += 22 + gap;

        // Anchor
        labels.add(new Label(left, y + 5, Component.literal("Anchor")));
        var anchorBtn = CycleButton.<TpsHudConfig.Anchor>builder(a -> Component.literal(a.name().replace('_','-')))
                .withValues(TpsHudConfig.Anchor.values())
                .withInitialValue(mv.anchor)
                .create(left + 140, y, w - 140, 20, Component.empty());
        anchorBtn.setValue(mv.anchor);
        anchorBtn.setCallback((b, v) -> mv.anchor = v);
        widgets.add(anchorBtn);
        y += 22 + gap;

        // Padding
        labels.add(new Label(left, y + 5, Component.literal("Padding (px)")));
        var padBox = new EditBox(this.font, left + 140, y, w - 140, 20, Component.empty());
        padBox.setValue(String.valueOf(mv.padding));
        padBox.setResponder(s -> {
            try { mv.padding = Math.max(0, Math.min(64, Integer.parseInt(s.trim()))); padBox.setTextColor(0xE0E0E0); }
            catch (Exception ex) { padBox.setTextColor(0xFF5555); }
        });
        widgets.add(padBox);
        y += 22 + gap;

        // Scale
        labels.add(new Label(left, y + 5, Component.literal("Scale (%)")));
        var scaleDec = Button.builder(Component.literal("-"), b -> { mv.scale = Math.max(0.5f, mv.scale - 0.05f); })
                .bounds(left + 140, y, 20, 20).build();
        var scaleBox = new EditBox(this.font, left + 162, y, w - 140 - 44, 20, Component.empty());
        scaleBox.setValue(String.valueOf(Math.round(mv.scale * 100)));
        scaleBox.setResponder(s -> {
            try { int p = Integer.parseInt(s.trim()); mv.scale = Math.max(50, Math.min(200, p)) / 100f; scaleBox.setTextColor(0xE0E0E0); }
            catch (Exception ex) { scaleBox.setTextColor(0xFF5555); }
        });
        var scaleInc = Button.builder(Component.literal("+"), b -> { mv.scale = Math.min(2.0f, mv.scale + 0.05f); })
                .bounds(left + w - 22, y, 22, 20).build();
        widgets.add(scaleDec); widgets.add(scaleBox); widgets.add(scaleInc);
        y += 22 + gap;

        // Precision
        labels.add(new Label(left, y + 5, Component.literal("Precision (decimals)")));
        var precisionBtn = CycleButton.<Integer>builder(v -> Component.literal(String.valueOf(v)))
                .withValues(0,1,2,3)
                .withInitialValue(mv.precision)
                .create(left + 140, y, w - 140, 20, Component.empty());
        precisionBtn.setCallback((b, v) -> mv.precision = v);
        widgets.add(precisionBtn);
        y += 22 + gap;

        // Format
        labels.add(new Label(left, y + 5, Component.literal("Format")));
        var formatBtn = CycleButton.<TpsHudConfig.Format>builder(v -> Component.literal(v.name()))
                .withValues(TpsHudConfig.Format.values())
                .withInitialValue(mv.format)
                .create(left + 140, y, w - 140, 20, Component.empty());
        formatBtn.setCallback((b, v) -> mv.format = v);
        widgets.add(formatBtn);
        y += 22 + gap;

        // Smoothing Window
        labels.add(new Label(left, y + 5, Component.literal("Smoothing Window (samples)")));
        var smoothBox = new EditBox(this.font, left + 140, y, w - 140, 20, Component.empty());
        smoothBox.setValue(String.valueOf(mv.smoothingWindow));
        smoothBox.setResponder(s -> {
            try { mv.smoothingWindow = Math.max(5, Math.min(240, Integer.parseInt(s.trim()))); smoothBox.setTextColor(0xE0E0E0); }
            catch (Exception ex) { smoothBox.setTextColor(0xFF5555); }
        });
        widgets.add(smoothBox);
        y += 22 + gap;

        // Auto-hide F3
        labels.add(new Label(left, y + 5, Component.literal("Auto-hide when F3 open")));
        var autoHideBtn = CycleButton.booleanBuilder(Component.literal("ON"), Component.literal("OFF"))
                .withInitialValue(mv.autoHideF3)
                .displayOnlyValue(true)
                .create(left + 140, y, w - 140, 20, Component.empty());
        autoHideBtn.setCallback((b, v) -> mv.autoHideF3 = v);
        widgets.add(autoHideBtn);
        y += 22 + gap;

        // Mode
        labels.add(new Label(left, y + 5, Component.literal("Display Mode")));
        var modeBtn = CycleButton.<TpsHudConfig.Mode>builder(v -> Component.literal(v.name()))
                .withValues(TpsHudConfig.Mode.values())
                .withInitialValue(mv.displayMode)
                .create(left + 140, y, w - 140, 20, Component.empty());
        modeBtn.setCallback((b, v) -> mv.displayMode = v);
        widgets.add(modeBtn);
        y += 22 + gap;

        // Thresholds Titles
        labels.add(new Label(left, y + 5, Component.literal("TPS Thresholds"))); y += 14 + gap;

        // TPS warn/bad
        labels.add(new Label(left, y + 5, Component.literal("Warn if TPS ≤")));
        var tpsWarn = new EditBox(this.font, left + 140, y, (w - 140)/2 - 4, 20, Component.empty());
        tpsWarn.setValue(trimDouble(mv.thresh.tpsWarn));
        tpsWarn.setResponder(s -> validateDoubleBox(tpsWarn, s, v -> mv.thresh.tpsWarn = clamp(v, 0, 20)));
        widgets.add(tpsWarn);

        labels.add(new Label(left + 140 + (w - 140)/2 + 8, y + 5, Component.literal("Bad if TPS ≤")));
        var tpsBad = new EditBox(this.font, left + 140 + (w - 140)/2 + 8 + 90, y, (w - 140) - ((w - 140)/2 + 8 + 90), 20, Component.empty());
        tpsBad.setValue(trimDouble(mv.thresh.tpsBad));
        tpsBad.setResponder(s -> validateDoubleBox(tpsBad, s, v -> mv.thresh.tpsBad = clamp(v, 0, 20)));
        widgets.add(tpsBad);
        y += 22 + gap;

        // TPS colors
        labels.add(new Label(left, y + 5, Component.literal("TPS Colors (#RRGGBB Good/Warn/Bad)")));
        var tpsGood = hexBox(left + 140, y, (w - 140 - 16)/3, mv.thresh.tpsGoodColor, v -> mv.thresh.tpsGoodColor = v);
        var tpsWarnC = hexBox(left + 140 + (w - 140 - 16)/3 + 8, y, (w - 140 - 16)/3, mv.thresh.tpsWarnColor, v -> mv.thresh.tpsWarnColor = v);
        var tpsBadC = hexBox(left + 140 + 2*((w - 140 - 16)/3) + 16, y, (w - 140 - 16)/3, mv.thresh.tpsBadColor, v -> mv.thresh.tpsBadColor = v);
        widgets.add(tpsGood); widgets.add(tpsWarnC); widgets.add(tpsBadC);
        y += 22 + gap;

        // MSPT
        labels.add(new Label(left, y + 5, Component.literal("MSPT Thresholds"))); y += 14 + gap;

        labels.add(new Label(left, y + 5, Component.literal("Warn if MSPT ≥")));
        var msptWarn = new EditBox(this.font, left + 140, y, (w - 140)/2 - 4, 20, Component.empty());
        msptWarn.setValue(trimDouble(mv.thresh.msptWarn));
        msptWarn.setResponder(s -> validateDoubleBox(msptWarn, s, v -> mv.thresh.msptWarn = clamp(v, 0, 200)));
        widgets.add(msptWarn);

        labels.add(new Label(left + 140 + (w - 140)/2 + 8, y + 5, Component.literal("Bad if MSPT ≥")));
        var msptBad = new EditBox(this.font, left + 140 + (w - 140)/2 + 8 + 90, y, (w - 140) - ((w - 140)/2 + 8 + 90), 20, Component.empty());
        msptBad.setValue(trimDouble(mv.thresh.msptBad));
        msptBad.setResponder(s -> validateDoubleBox(msptBad, s, v -> mv.thresh.msptBad = clamp(v, 0, 200)));
        widgets.add(msptBad);
        y += 22 + gap;

        labels.add(new Label(left, y + 5, Component.literal("MSPT Colors (#RRGGBB Good/Warn/Bad)")));
        var msptGood = hexBox(left + 140, y, (w - 140 - 16)/3, mv.thresh.msptGoodColor, v -> mv.thresh.msptGoodColor = v);
        var msptWarnC = hexBox(left + 140 + (w - 140 - 16)/3 + 8, y, (w - 140 - 16)/3, mv.thresh.msptWarnColor, v -> mv.thresh.msptWarnColor = v);
        var msptBadC = hexBox(left + 140 + 2*((w - 140 - 16)/3) + 16, y, (w - 140 - 16)/3, mv.thresh.msptBadColor, v -> mv.thresh.msptBadColor = v);
        widgets.add(msptGood); widgets.add(msptWarnC); widgets.add(msptBadC);
        y += 22 + gap;

        // Buttons: Done / Cancel
        var done = Button.builder(Component.literal("Done"), b -> {
            mv.applyTo(ConfigManager.get());
            ConfigManager.save();
            onClose();
        }).bounds(this.width / 2 - 155, this.height - 28, 150, 20).build();

        var cancel = Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(this.width / 2 + 5, this.height - 28, 150, 20).build();

        widgets.add(done); widgets.add(cancel);
        super.init();
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        this.renderBackground(gfx, mouseX, mouseY, delta);
        for (var w : widgets) w.render(gfx, mouseX, mouseY, delta);
        for (var l : labels) gfx.drawString(this.font, l.text(), l.x(), l.y(), 0xFFFFFF, false);
        super.render(gfx, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        for (var w : widgets) w.tick();
        super.tick();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private record Label(int x, int y, Component text) {}

    private EditBox hexBox(int x, int y, int w, String init, java.util.function.Consumer<String> onValid) {
        var box = new EditBox(this.font, x, y, w, 20, Component.empty());
        box.setValue(init);
        box.setResponder(s -> {
            String t = s.trim().toUpperCase(Locale.ROOT);
            if (t.startsWith("#")) t = t.substring(1);
            boolean ok = t.matches("[0-9A-F]{6}");
            box.setTextColor(ok ? 0xE0E0E0 : 0xFF5555);
            if (ok) onValid.accept("#" + t);
        });
        return box;
    }

    private static void validateDoubleBox(EditBox box, String s, java.util.function.Consumer<Double> onValid) {
        try {
            double v = Double.parseDouble(s.trim());
            box.setTextColor(0xE0E0E0);
            onValid.accept(v);
        } catch (Exception ex) {
            box.setTextColor(0xFF5555);
        }
    }

    private static String trimDouble(double v) {
        if (Math.abs(v - Math.round(v)) < 1e-6) return String.valueOf((long)Math.round(v));
        return String.valueOf(v);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // Lightweight copy of config for editing, then apply back
    private static final class ModelView {
        public boolean enabled;
        public TpsHudConfig.Anchor anchor;
        public int padding;
        public float scale;
        public int precision;
        public TpsHudConfig.Format format;
        public int smoothingWindow;
        public boolean autoHideF3;
        public TpsHudConfig.Mode displayMode;
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
            this.displayMode = src.displayMode;

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
            dst.displayMode = displayMode;

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