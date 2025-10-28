package com.zivalez.tpshudneoforge.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;

public record CommonHandshakePayload() implements CustomPacketPayload {
    public static final ResourceLocation RL = ResourceLocation.fromNamespaceAndPath("tpshudneoforge", "handshake");
    public static final Type<CommonHandshakePayload> ID = new Type<>(RL);
    public static final StreamCodec<FriendlyByteBuf, CommonHandshakePayload> CODEC =
            CustomPacketPayload.codec((p, buf) -> {}, (buf) -> new CommonHandshakePayload());

    @Override public Type<CommonHandshakePayload> type() {
        return ID;
    }
}