package com.bawnorton.plaques.mixin.client;

import com.bawnorton.plaques.client.PlaquesClient;
import com.bawnorton.plaques.client.render.PlaqueTextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.FontManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Final private FontManager fontManager;

    @Inject(method = "<init>", at = @At(value = "RETURN", target = "Lnet/minecraft/client/font/FontManager;createTextRenderer()Lnet/minecraft/client/font/TextRenderer;"))
    private void onInit(RunArgs args, CallbackInfo ci) {
        FontManagerAccessor fontManagerAccessor = (FontManagerAccessor) fontManager;
        PlaquesClient.plaqueTextRenderer = new PlaqueTextRenderer((id) -> fontManagerAccessor.getFontStorages().getOrDefault(fontManagerAccessor.getIdOverrides().getOrDefault(id, id), fontManagerAccessor.getMissingStorage()), false);
    }
}
