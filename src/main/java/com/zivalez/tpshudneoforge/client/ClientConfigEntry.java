package com.zivalez.tpshudneoforge.client;

import com.zivalez.tpshudneoforge.client.TpsHudConfigScreen;
import com.zivalez.tpshudneoforge.tpshudneoforge;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = tpshudneoforge.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ClientConfigEntry {
    private ClientConfigEntry() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (mc, parent) -> new TpsHudConfigScreen(parent)
        );
    }
}
