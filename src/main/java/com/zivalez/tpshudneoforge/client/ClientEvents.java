package com.zivalez.tpshudneoforge.client;

import com.zivalez.tpshudneoforge.core.TpsTracker;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        TpsTracker.onClientTickFallback();
    }
}
