package com.zivalez.tpshudneoforge.client.input;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import com.zivalez.tpshudneoforge.tpshudneoforge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class KeyBindsHandler {

    private KeyBindsHandler() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        if (KeyBinds.TOGGLE_HUD != null && KeyBinds.TOGGLE_HUD.consumeClick()) {
            var cfg = ConfigManager.get();
            cfg.enabled = !cfg.enabled;
            ConfigManager.save();
        }
        if (KeyBinds.RELOAD_CONFIG != null && KeyBinds.RELOAD_CONFIG.consumeClick()) {
            try {
                ConfigManager.reload();
            } catch (Throwable ignored) {
            }
        }
    }
}
