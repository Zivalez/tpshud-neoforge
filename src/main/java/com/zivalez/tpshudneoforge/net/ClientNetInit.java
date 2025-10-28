// src/main/java/com/zivalez/tpshudneoforge/net/ClientNetInit.java
package com.zivalez.tpshudneoforge.net;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

// ClientNetInit.java
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientNetInit {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent evt) {
        final PayloadRegistrar reg = evt.registrar(MODID);
        reg.playToClient(
                CommonTickRatePayload.TYPE,
                CommonTickRatePayload.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                })
        );
    }
}

