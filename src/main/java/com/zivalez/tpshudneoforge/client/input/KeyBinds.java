package com.zivalez.tpshudneoforge.client.input;

import com.zivalez.tpshudneoforge.config.ConfigManager;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class KeyBinds {
    public static final String CATEGORY = "key.categories.tpshud";
    public static KeyMapping TOGGLE;
    public static KeyMapping RELOAD;

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent e) {
        TOGGLE = new KeyMapping("key.tpshud.toggle", GLFW.GLFW_KEY_F7, CATEGORY);
        RELOAD = new KeyMapping("key.tpshud.reload", GLFW.GLFW_KEY_F7, CATEGORY) {
            @Override
            public boolean isDown() {
                // Ctrl + F7 (simple check)
                long win = net.minecraft.client.Minecraft.getInstance().getWindow().getWindow();
                boolean ctrl = org.lwjgl.glfw.GLFW.glfwGetKey(win, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                        || org.lwjgl.glfw.GLFW.glfwGetKey(win, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
                return ctrl && super.isDown();
            }
        };
        e.register(TOGGLE);
        e.register(RELOAD);
    }

    private KeyBinds() {}
}