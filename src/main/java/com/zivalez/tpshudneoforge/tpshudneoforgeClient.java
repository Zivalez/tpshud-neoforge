package com.zivalez.tpshudneoforge;

import com.zivalez.tpshudneoforge.client.TpsHudConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public final class tpshudneoforgeClient {
    public tpshudneoforgeClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> new TpsHudConfigScreen(parent));
    }
}
