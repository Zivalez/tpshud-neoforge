package com.zivalez.tpshudneoforge.net;

import com.zivalez.tpshudneoforge.core.TpsTracker;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientNetInit {
    public static final String NETWORK_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterClientPayloadHandlersEvent evt) {
        PayloadRegistrar reg = evt.registrar(NETWORK_VERSION);
        reg.playToClient(CommonTickRatePayload.ID, CommonTickRatePayload.CODEC, (payload, ctx) -> {
            // compute mspt from tickRate if it's TPS or MSPT already
            float mspt = (float)(payload.convertedFromTps() ? (1000.0 / payload.tickRate()) : payload.tickRate());
            TpsTracker.setServerProvidedMspt(mspt);
        });
        reg.playToClient(CommonHandshakePayload.ID, CommonHandshakePayload.CODEC, (payload, ctx) -> {
            // No-op for now (could open dialog to prefer server data)
        });
    }
}