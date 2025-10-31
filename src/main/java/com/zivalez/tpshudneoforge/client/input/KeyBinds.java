package com.zivalez.tpshudneoforge.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.zivalez.tpshudneoforge.tpshudneoforge;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class KeyBinds {

    public static final String CATEGORY = "key.categories.tpshud";

    public static KeyMapping TOGGLE_HUD;
    public static KeyMapping RELOAD_CONFIG;

    private KeyBinds() {}

    @SubscribeEvent
    public static void onRegisterKeys(RegisterKeyMappingsEvent e) {
        TOGGLE_HUD = new KeyMapping(
                "key.tpshud.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                CATEGORY
        );
        RELOAD_CONFIG = new KeyMapping(
                "key.tpshud.reload",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                CATEGORY
        );
        e.register(TOGGLE_HUD);
        e.register(RELOAD_CONFIG);
    }
}
