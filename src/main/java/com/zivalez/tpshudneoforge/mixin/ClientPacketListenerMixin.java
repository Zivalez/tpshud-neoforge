package com.zivalez.tpshudneoforge.mixin;

import com.zivalez.tpshudneoforge.core.TpsTracker;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleSetTime", at = @At("HEAD"))
    private void tpshud$onSetTime(ClientboundSetTimePacket pkt, CallbackInfo ci) {
        TpsTracker.onWorldTimePacket();
    }
}
