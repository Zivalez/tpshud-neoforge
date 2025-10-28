package com.zivalez.tpshudneoforge.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CommonTickRatePayload(double tickRate, boolean convertedFromTps) implements CustomPacketPayload {
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath("tpshudneoforge", "tps");
    public static final Type<CommonTickRatePayload> ID = new Type<>(RL);

    public static final StreamCodec<RegistryFriendlyByteBuf, CommonTickRatePayload> CODEC =
            CustomPacketPayload.codec((p, buf) -> {
                buf.writeDouble(p.tickRate());
                buf.writeBoolean(p.convertedFromTps());
            }, buf -> new CommonTickRatePayload(buf.readDouble(), buf.readBoolean()));

    @Override
    public Type<CommonTickRatePayload> type() {
        return ID;
    }
}
