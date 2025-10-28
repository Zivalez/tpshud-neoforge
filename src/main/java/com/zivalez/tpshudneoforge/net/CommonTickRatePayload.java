// src/main/java/com/zivalez/tpshudneoforge/net/CommonTickRatePayload.java
package com.zivalez.tpshudneoforge.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

public record CommonTickRatePayload(float tps) implements CustomPacketPayload {
    public static final Type<CommonTickRatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "tick_rate"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CommonTickRatePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, CommonTickRatePayload::tps,
                    CommonTickRatePayload::new
            );

    @Override
    public Type<CommonTickRatePayload> type() {
        return TYPE;
    }
}
