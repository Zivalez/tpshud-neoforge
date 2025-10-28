package com.zivalez.tpshudneoforge.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CommonHandshakePayload() implements CustomPacketPayload {
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath("tpshudneoforge", "handshake");
    public static final Type<CommonHandshakePayload> ID = new Type<>(RL);

    public static final StreamCodec<RegistryFriendlyByteBuf, CommonHandshakePayload> CODEC =
            CustomPacketPayload.codec((p, buf) -> { /* no fields */ }, buf -> new CommonHandshakePayload());

    @Override
    public Type<CommonHandshakePayload> type() {
        return ID;
    }
}
