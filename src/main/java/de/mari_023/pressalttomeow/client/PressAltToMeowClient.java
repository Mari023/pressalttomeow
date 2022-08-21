package de.mari_023.pressalttomeow.client;

import de.mari_023.pressalttomeow.PressAltToMeow;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.glfw.GLFW;

public class PressAltToMeowClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private static long LAST_MEOW;
    private static boolean isOnServer = false;

    @Override
    public void onInitializeClient() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.pressalttomeow.meow", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.categories.gameplay"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (System.currentTimeMillis() - LAST_MEOW > 1000) {
                    if (isOnServer) ClientPlayNetworking.send(PressAltToMeow.NETWORK_CHANNEL, PacketByteBufs.create());
                    else meow();
                    LAST_MEOW = System.currentTimeMillis();
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PressAltToMeow.NETWORK_CHANNEL, (client, handler, buf, packetSender) -> isOnServer = true);
    }

    private void meow() {
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 1f, 1f);
    }
}
