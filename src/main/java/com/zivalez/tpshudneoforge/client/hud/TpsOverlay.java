package com.zivalez.tpshudneoforge.client.hud;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.config.TpsHudConfig;
import com.zivalez.tpshudneoforge.core.TpsTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class TpsOverlay {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post evt) {
        TpsHudConfig cfg = ConfigManager.get();
        if (!cfg.enabled) return;

        GuiGraphics gfx = evt.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        float mspt = TpsTracker.getMspt();
        float tps = TpsTracker.getTps();

        String label = cfg.showMspt ? "MSPT: " : "TPS: ";
        String value;
        if (cfg.showMspt) {
            value = Float.isNaN(mspt) ? "--" : String.format("%.1f", mspt);
        } else {
            value = Float.isNaN(tps) ? "--" : String.format("%.1f", tps);
        }

        int x = cfg.x;
        int y = cfg.y;
        int labelW = font.width(label);
        int valueW = font.width(value);
        int h = font.lineHeight;

        gfx.pose().pushPose();
        gfx.pose().scale(cfg.scale, cfg.scale, 1f);

        if (cfg.background) {
            int pad = 2;
            int w = labelW + valueW + pad * 2;
            int hh = h + pad * 2;
            gfx.fill(x - pad, y - pad, x - pad + w, y - pad + hh, 0x66000000);
        }

        if (cfg.shadow) {
            gfx.drawString(font, label, x, y, cfg.textColor, true);
            gfx.drawString(font, value, x + labelW, y, cfg.valueTextColor, true);
        } else {
            gfx.drawString(font, label, x, y, cfg.textColor, false);
            gfx.drawString(font, value, x + labelW, y, cfg.valueTextColor, false);
        }
        gfx.pose().popPose();
    }
}