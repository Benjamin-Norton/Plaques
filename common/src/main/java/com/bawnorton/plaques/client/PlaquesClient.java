package com.bawnorton.plaques.client;

import com.bawnorton.plaques.Plaques;
import com.bawnorton.plaques.client.networking.ClientNetworking;
import com.bawnorton.plaques.client.render.PlaqueTextRenderer;
import com.bawnorton.plaques.client.render.PlaqueBlockEntityRenderer;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

public class PlaquesClient {
    public static PlaqueTextRenderer plaqueTextRenderer;

    public static void init() {
        ClientNetworking.init();
        BlockEntityRendererRegistry.register(Plaques.PLAQUE.get(), PlaqueBlockEntityRenderer::new);
    }
}
