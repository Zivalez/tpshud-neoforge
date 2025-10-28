package com.zivalez.tpshudneoforge.net;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NetInit {
    public static final String NETWORK_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent evt) {
        PayloadRegistrar reg = evt.registrar(NETWORK_VERSION);
        // server-to-client tickrate channel
        reg.playToClient(CommonTickRatePayload.ID, CommonTickRatePayload.CODEC, (payload, ctx) -> {
            // server-side default handler (if needed for validation/logging)
        });
        // handshake optional
        reg.playBidirectional(CommonHandshakePayload.ID, CommonHandshakePayload.CODEC,
                (payload, ctx) -> {/* client handler on server? probably none */},
                (payload, ctx) -> {/* server handler */});
    }
}