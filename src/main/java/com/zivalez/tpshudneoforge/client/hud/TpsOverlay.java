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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class TpsOverlay {

    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(tpshudneoforge.MODID, "tps_hud");

    private static final double TPS_CAP = 20.0;

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

        if (cfg.autoHideF3 && isDebugActive(mc)) return;

        float tpsRaw = TpsTracker.getTps();
        float msptRaw = TpsTracker.getMspt();
        if (Float.isNaN(tpsRaw)) return;

        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        drawOverlay(gfx, cfg, tpsRaw, msptRaw, sw, sh);
    }

    public static void renderPreview(GuiGraphics gfx, TpsHudConfig cfg, float tpsRaw, float msptRaw, int sw, int sh) {
        if (!cfg.enabled) return;
        drawOverlay(gfx, cfg, tpsRaw, msptRaw, sw, sh);
    }

    // --- Shared drawing logic ------------------------------------------------------------
    private static void drawOverlay(GuiGraphics gfx, TpsHudConfig cfg, float tpsRaw, float msptRaw, int sw, int sh) {
        Font font = Minecraft.getInstance().font;

        int prec = Mth.clamp(cfg.precision, 0, 3);
        String tpsStr = formatCapped(tpsRaw, TPS_CAP, prec);
        String msptStr = formatNumber(msptRaw, prec);

        int tpsColor = pickTpsColor(cfg, tpsRaw);
        int msptColor = pickMsptColor(cfg, msptRaw);

        String line1 = switch (cfg.format) {
            case TPS -> "TPS: " + tpsStr;
            case MSPT -> "MSPT: " + msptStr;
            default -> "TPS: " + tpsStr;
        };
        String line2 = (cfg.format == TpsHudConfig.Format.BOTH) ? "MSPT: " + msptStr : null;

        int w = font.width(line1);
        int h = font.lineHeight;
        if (line2 != null) {
            w = Math.max(w, font.width(line2));
            h = font.lineHeight * 2 + 2;
        }

        int pad = Math.max(0, cfg.padding);
        float scale = Mth.clamp(cfg.scale, 0.5f, 2.0f);

        int drawX;
        int drawY;
        int pivotX;
        int pivotY;

        switch (cfg.anchor) {
            case TOP_LEFT -> {
                pivotX = pad;
                pivotY = pad;
                drawX = 0;
                drawY = 0;
            }
            case TOP_RIGHT -> {
                pivotX = sw - pad;
                pivotY = pad;
                drawX = -w;
                drawY = 0;
            }
            case BOTTOM_LEFT -> {
                pivotX = pad;
                pivotY = sh - pad;
                drawX = 0;
                drawY = -h;
            }
            default -> {
                pivotX = sw - pad;
                pivotY = sh - pad;
                drawX = -w;
                drawY = -h;
            }
        }

        gfx.pose().pushPose();
        gfx.pose().translate(pivotX, pivotY, 0);
        gfx.pose().scale(scale, scale, 1.0f);

        int bgPad = 3;
        int bgLeft = drawX - bgPad;
        int bgTop = drawY - bgPad;
        int bgRight = drawX + w + bgPad;
        int bgBottom = drawY + (line2 == null ? font.lineHeight : font.lineHeight * 2 + 2) + bgPad;

        if (cfg.background) {
            gfx.fill(bgLeft, bgTop, bgRight, bgBottom, 0x66000000);
        }

        if (cfg.format == TpsHudConfig.Format.TPS) {
            String label = "TPS: ";
            int labelW = font.width(label);
            gfx.drawString(font, label, drawX, drawY, cfg.textColor, cfg.shadow);
            gfx.drawString(font, stripSign(tpsStr), drawX + labelW, drawY, tpsColor, cfg.shadow);
        } else if (cfg.format == TpsHudConfig.Format.MSPT) {
            String label = "MSPT: ";
            int labelW = font.width(label);
            gfx.drawString(font, label, drawX, drawY, cfg.textColor, cfg.shadow);
            gfx.drawString(font, stripSign(msptStr), drawX + labelW, drawY, msptColor, cfg.shadow);
        } else {
            String label1 = "TPS: ";
            int l1 = font.width(label1);
            gfx.drawString(font, label1, drawX, drawY, cfg.textColor, cfg.shadow);
            gfx.drawString(font, stripSign(tpsStr), drawX + l1, drawY, tpsColor, cfg.shadow);

            int y2 = drawY + font.lineHeight + 2;
            String label2 = "MSPT: ";
            int l2 = font.width(label2);
            gfx.drawString(font, label2, drawX, y2, cfg.textColor, cfg.shadow);
            gfx.drawString(font, stripSign(msptStr), drawX + l2, y2, msptColor, cfg.shadow);
        }

        gfx.pose().popPose();
    }

    // --- Utils --------------------------------------------------------------------------
    private static String formatCapped(float value, double cap, int precision) {
        double v = Math.min(value, cap);
        BigDecimal bd = BigDecimal.valueOf(v).setScale(precision, RoundingMode.HALF_UP);
        BigDecimal capBD = BigDecimal.valueOf(cap).setScale(precision, RoundingMode.HALF_UP);
        if (bd.compareTo(capBD) > 0) bd = capBD;
        return bd.toPlainString();
    }

    private static String formatNumber(float value, int precision) {
        return BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP).toPlainString();
    }

    private static String stripSign(String s) {
        if (s != null && s.startsWith("+")) return s.substring(1);
        return s;
    }

    private static boolean isDebugActive(Minecraft mc) {
        try {
            Object gui = mc.gui;
            if (gui != null) {
                var mGetDbg = gui.getClass().getMethod("getDebugOverlay");
                Object dbg = mGetDbg.invoke(gui);
                if (dbg != null) {
                    for (String m : new String[]{"showDebugScreen", "showDebug", "shouldShowDebug", "isDebugEnabled"}) {
                        try {
                            var mm = dbg.getClass().getMethod(m);
                            Object r = mm.invoke(dbg);
                            if (r instanceof Boolean b) return b;
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            }
        } catch (Throwable ignored) {}

        try {
            Object opt = mc.options;
            if (opt != null) {
                for (String f : new String[]{"renderDebug", "renderDebugScreen", "debugEnabled"}) {
                    try {
                        var ff = opt.getClass().getField(f);
                        ff.setAccessible(true);
                        Object r = ff.get(opt);
                        if (r instanceof Boolean b) return b;
                    } catch (NoSuchFieldException ignored) {}
                }
            }
        } catch (Throwable ignored) {}

        return false;
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
