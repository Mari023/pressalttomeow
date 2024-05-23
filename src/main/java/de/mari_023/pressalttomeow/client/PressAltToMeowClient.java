package de.mari_023.pressalttomeow.client;

import com.mojang.blaze3d.platform.InputConstants;
import de.mari_023.pressalttomeow.MeowPayload;
import de.mari_023.pressalttomeow.PressAltToMeow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import org.lwjgl.glfw.GLFW;

public class PressAltToMeowClient implements ClientModInitializer {
    private static KeyMapping keyBinding;
    private static long LAST_MEOW;
    private static boolean isOnServer = false;

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.pressalttomeow.meow", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.gameplay"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                if (System.currentTimeMillis() - LAST_MEOW > 1000) {
                    if (isOnServer) ClientPlayNetworking.send(new MeowPayload());
                    else meow();
                    LAST_MEOW = System.currentTimeMillis();
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(MeowPayload.TYPE, (payload, context) -> isOnServer = true);
    }

    private void meow() {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        player.level().playSound(player, player.blockPosition(), PressAltToMeow.defaultSounds(), SoundSource.PLAYERS, 1f, 1f);
    }
}
