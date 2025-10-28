// src/main/java/com/zivalez/tpshudneoforge/net/NetInit.java
package com.zivalez.tpshudneoforge.net;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static com.zivalez.tpshudneoforge.tpshudneoforge.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetInit {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent evt) {
    }
}
