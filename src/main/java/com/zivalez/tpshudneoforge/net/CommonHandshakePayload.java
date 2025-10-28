// src/main/java/com/zivalez/tpshudneoforge/net/CommonHandshakePayload.java
package com.zivalez.tpshudneoforge.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

public record CommonHandshakePayload() implements CustomPacketPayload {
    public static final Type<CommonHandshakePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "handshake"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CommonHandshakePayload> CODEC =
            StreamCodec.unit(new CommonHandshakePayload());

    @Override
    public Type<CommonHandshakePayload> type() {
        return TYPE;
    }
}
