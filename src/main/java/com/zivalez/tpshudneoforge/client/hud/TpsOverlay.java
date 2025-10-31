package com.zivalez.tpshudneoforge.client.hud;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import com.zivalez.tpshudneoforge.core.TpsTracker;
import com.zivalez.tpshudneoforge.tpshudneoforge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IIngameOverlay;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TpsOverlay {

    private TpsOverlay() {}

    /** Register a dedicated GUI layer so ordering is stable and compatible with Sodium/RSO. */
    @EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @net.neoforged.bus.api.SubscribeEvent
        public static void register(RegisterGuiLayersEvent e) {
            e.registerAboveAll("tpshudneoforge:tps_hud", new IIngameOverlay() {
                @Override
                public void render(GuiGraphics gfx, float partialTick) {
                    TpsOverlay.render(gfx);
                }
            });
        }
    }

    public static void render(GuiGraphics gfx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) return;

        TpsHudConfig cfg = ConfigManager.get();
        if (!cfg.enabled) return;
        if (cfg.autoHideF3 && mc.options.renderDebug) return;

        Font font = mc.font;

        float tps = TpsTracker.getTps();
        float mspt = TpsTracker.getMspt();
        if (Float.isNaN(tps)) return;

        // Compose strings according to format & precision
        int prec = Math.max(0, Math.min(3, cfg.precision));
        String tpsStr = String.format(java.util.Locale.ROOT, "%." + prec + "f", tps);
        String msptStr = String.format(java.util.Locale.ROOT, "%." + prec + "f", mspt);

        String label1 = "TPS";
        String label2 = "MSPT";

        // Colors by thresholds
        int tpsColor = pickTpsColor(cfg, tps);
        int msptColor = pickMsptColor(cfg, mspt);

        String line1, line2 = null;
        int valueColor1, valueColor2 = cfg.valueTextColor;

        switch (cfg.format) {
            case TPS -> {
                line1 = label1 + ": " + tpsStr;
                valueColor1 = tpsColor;
            }
            case MSPT -> {
                line1 = label2 + ": " + msptStr;
                valueColor1 = msptColor;
            }
            default -> { // BOTH
                if (cfg.displayMode == TpsHudConfig.Mode.COMPACT) {
                    line1 = label1 + " " + tpsStr + " | " + label2 + " " + msptStr;
                    valueColor1 = cfg.valueTextColor; // mix; draw single line with default value color
                } else {
                    line1 = label1 + ": " + tpsStr;
                    line2 = label2 + ": " + msptStr;
                    valueColor1 = tpsColor;
                    valueColor2 = msptColor;
                }
            }
        }

        // Layout
        int pad = Math.max(0, cfg.padding);
        int x = pad, y = pad;

        // Measure
        int w = font.width(line1);
        int h = font.lineHeight;
        if (line2 != null) {
            w = Math.max(w, font.width(line2));
            h = h * 2 + 2;
        }

        // Anchor position
        int screenW = gfx.guiWidth();
        int screenH = gfx.guiHeight();
        switch (cfg.anchor) {
            case TOP_RIGHT -> { x = screenW - pad - w; y = pad; }
            case BOTTOM_LEFT -> { x = pad; y = screenH - pad - h; }
            case BOTTOM_RIGHT -> { x = screenW - pad - w; y = screenH - pad - h; }
            default -> { x = pad; y = pad; }
        }

        // Draw
        gfx.pose().pushPose();
        float s = Math.max(0.5f, Math.min(2.0f, cfg.scale));
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(s, s, 1);

        int bgPad = 3;
        int bgW = w + bgPad * 2;
        int bgH = (line2 == null ? font.lineHeight : font.lineHeight * 2 + 2) + bgPad * 2;

        if (cfg.background) {
            gfx.fill(-bgPad, -bgPad, -bgPad + bgW, -bgPad + bgH, 0x66000000);
        }

        if (cfg.shadow) {
            gfx.drawString(font, line1, 0, 0, cfg.textColor, true);
            if (cfg.format == TpsHudConfig.Format.TPS) {
                int labelW = font.width("TPS: ");
                gfx.drawString(font, tpsStr, labelW, 0, tpsColor, true);
            } else if (cfg.format == TpsHudConfig.Format.MSPT) {
                int labelW = font.width("MSPT: ");
                gfx.drawString(font, msptStr, labelW, 0, msptColor, true);
            }
            if (line2 != null) {
                gfx.drawString(font, line2, 0, font.lineHeight + 2, cfg.textColor, true);
                int labelW2 = font.width("MSPT: ");
                gfx.drawString(font, msptStr, labelW2, font.lineHeight + 2, msptColor, true);
            }
        } else {
            gfx.drawString(font, line1, 0, 0, cfg.textColor, false);
            if (cfg.format == TpsHudConfig.Format.TPS) {
                int labelW = font.width("TPS: ");
                gfx.drawString(font, tpsStr, labelW, 0, tpsColor, false);
            } else if (cfg.format == TpsHudConfig.Format.MSPT) {
                int labelW = font.width("MSPT: ");
                gfx.drawString(font, msptStr, labelW, 0, msptColor, false);
            }
            if (line2 != null) {
                gfx.drawString(font, line2, 0, font.lineHeight + 2, cfg.textColor, false);
                int labelW2 = font.width("MSPT: ");
                gfx.drawString(font, msptStr, labelW2, font.lineHeight + 2, msptColor, false);
            }
        }

        gfx.pose().popPose();
    }

    private static int pickTpsColor(TpsHudConfig cfg, float tps) {
        if (Float.isNaN(tps)) return cfg.valueTextColor;
        TpsHudConfig.Thresholds th = cfg.thresholds;
        if (tps <= th.tpsBad) return parseHex(th.tpsBadColor, cfg.valueTextColor);
        if (tps <= th.tpsWarn) return parseHex(th.tpsWarnColor, cfg.valueTextColor);
        return parseHex(th.tpsGoodColor, cfg.valueTextColor);
    }

    private static int pickMsptColor(TpsHudConfig cfg, float mspt) {
        if (Float.isNaN(mspt)) return cfg.valueTextColor;
        TpsHudConfig.Thresholds th = cfg.thresholds;
        if (mspt >= th.msptBad) return parseHex(th.msptBadColor, cfg.valueTextColor);
        if (mspt >= th.msptWarn) return parseHex(th.msptWarnColor, cfg.valueTextColor);
        return parseHex(th.msptGoodColor, cfg.valueTextColor);
    }

    private static int parseHex(String s, int fallback) {
        try {
            String t = s == null ? "" : s.trim();
            if (t.startsWith("#")) t = t.substring(1);
            int rgb = Integer.parseInt(t, 16) & 0xFFFFFF;
            return 0xFF000000 | rgb;
        } catch (Exception e) {
            return fallback;
        }
    }
}