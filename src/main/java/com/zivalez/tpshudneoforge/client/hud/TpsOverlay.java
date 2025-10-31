package com.zivalez.tpshudneoforge.client.hud;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import com.zivalez.tpshudneoforge.core.TpsTracker;
import com.zivalez.tpshudneoforge.tpshudneoforge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.Locale;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TpsOverlay {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(tpshudneoforge.MODID, "tps_hud");

    private TpsOverlay() {}

    @SubscribeEvent
    public static void onRegisterLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(LAYER_ID, (gfx, delta) -> render(gfx));
    }

    public static void render(GuiGraphics gfx) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null) return;

        TpsHudConfig cfg = ConfigManager.get();
        if (!cfg.enabled) return;


        float tps = TpsTracker.getTps();
        float mspt = TpsTracker.getMspt();
        if (Float.isNaN(tps)) return;

        int prec = Mth.clamp(cfg.precision, 0, 3);
        String tpsStr = String.format(Locale.ROOT, "%." + prec + "f", tps);
        String msptStr = String.format(Locale.ROOT, "%." + prec + "f", mspt);

        String line1;
        String line2 = null;

        int tpsColor = pickTpsColor(cfg, tps);
        int msptColor = pickMsptColor(cfg, mspt);

        switch (cfg.format) {
            case TPS -> line1 = "TPS: " + tpsStr;
            case MSPT -> line1 = "MSPT: " + msptStr;
            default -> {
                if (cfg.displayMode == TpsHudConfig.Mode.COMPACT) {
                    line1 = "TPS " + tpsStr + " | MSPT " + msptStr;
                } else {
                    line1 = "TPS: " + tpsStr;
                    line2 = "MSPT: " + msptStr;
                }
            }
        }

        Font font = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        int w = font.width(line1);
        int h = font.lineHeight;
        if (!StringUtil.isNullOrEmpty(line2)) {
            w = Math.max(w, font.width(line2));
            h = h * 2 + 2;
        }

        int pad = Math.max(0, cfg.padding);
        int x = pad, y = pad;
        switch (cfg.anchor) {
            case TOP_RIGHT -> { x = sw - pad - w; y = pad; }
            case BOTTOM_LEFT -> { x = pad; y = sh - pad - h; }
            case BOTTOM_RIGHT -> { x = sw - pad - w; y = sh - pad - h; }
            case TOP_LEFT -> { /* default */ }
        }

        float scale = Mth.clamp(cfg.scale, 0.5f, 2.0f);

        gfx.pose().pushPose();
        gfx.pose().translate(x, y, 0);
        gfx.pose().scale(scale, scale, 1.0f);

        int bgPad = 3;
        int bgW = w + bgPad * 2;
        int bgH = (line2 == null ? font.lineHeight : font.lineHeight * 2 + 2) + bgPad * 2;

        if (cfg.background) {
            gfx.fill(-bgPad, -bgPad, -bgPad + bgW, -bgPad + bgH, 0x66000000);
        }

        if (cfg.format == TpsHudConfig.Format.TPS) {
            gfx.drawString(font, "TPS: ", 0, 0, cfg.textColor, cfg.shadow);
            int labelW = font.width("TPS: ");
            gfx.drawString(font, tpsStr, labelW, 0, tpsColor, cfg.shadow);
        } else if (cfg.format == TpsHudConfig.Format.MSPT) {
            gfx.drawString(font, "MSPT: ", 0, 0, cfg.textColor, cfg.shadow);
            int labelW = font.width("MSPT: ");
            gfx.drawString(font, msptStr, labelW, 0, msptColor, cfg.shadow);
        } else if (cfg.displayMode == TpsHudConfig.Mode.COMPACT) {
            gfx.drawString(font, line1, 0, 0, cfg.textColor, cfg.shadow);
        } else {
            gfx.drawString(font, "TPS: ", 0, 0, cfg.textColor, cfg.shadow);
            int labelW = font.width("TPS: ");
            gfx.drawString(font, tpsStr, labelW, 0, tpsColor, cfg.shadow);
        }

        if (line2 != null) {
            int y2 = font.lineHeight + 2;
            gfx.drawString(font, "MSPT: ", 0, y2, cfg.textColor, cfg.shadow);
            int lbl = font.width("MSPT: ");
            gfx.drawString(font, msptStr, lbl, y2, msptColor, cfg.shadow);
        }

        gfx.pose().popPose();
    }

    private static int pickTpsColor(TpsHudConfig cfg, float tps) {
        TpsHudConfig.Thresholds th = cfg.thresholds;
        if (tps <= th.tpsBad) return parseHex(th.tpsBadColor, cfg.valueTextColor);
        if (tps <= th.tpsWarn) return parseHex(th.tpsWarnColor, cfg.valueTextColor);
        return parseHex(th.tpsGoodColor, cfg.valueTextColor);
    }

    private static int pickMsptColor(TpsHudConfig cfg, float mspt) {
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
