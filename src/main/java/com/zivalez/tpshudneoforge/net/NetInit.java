package com.zivalez.tpshudneoforge.net;

import com.zivalez.tpshudneoforge.core.TpsTracker;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class NetInit {
    public static final String NETWORK_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent evt) {
        final PayloadRegistrar reg = evt.registrar(NETWORK_VERSION);

        reg.playToClient(CommonTickRatePayload.ID, CommonTickRatePayload.CODEC, (payload, ctx) -> {
            final float mspt = (float) (payload.convertedFromTps()
                    ? (1000.0 / payload.tickRate())
                    : payload.tickRate());
            ctx.enqueueWork(() -> TpsTracker.setServerProvidedMspt(mspt));
        });

        reg.playToClient(CommonHandshakePayload.ID, CommonHandshakePayload.CODEC, (payload, ctx) -> { /* no-op */ });
        reg.playToServer(CommonHandshakePayload.ID, CommonHandshakePayload.CODEC, (payload, ctx) -> { /* no-op */ });
    }
}
