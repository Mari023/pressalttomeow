package de.mari_023.pressalttomeow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.WeakHashMap;

public class PressAltToMeow implements ModInitializer {
    public static final Identifier NETWORK_CHANNEL = new Identifier("pressalttomeow", "m");
    private static final WeakHashMap<ServerPlayerEntity, Long> LAST_MEOW = new WeakHashMap<>();

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(NETWORK_CHANNEL, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!LAST_MEOW.containsKey(player) || System.currentTimeMillis() - LAST_MEOW.get(player) > 1000) {
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_CAT_AMBIENT,
                        SoundCategory.PLAYERS, 1f, 1f
                );
                LAST_MEOW.put(player, System.currentTimeMillis());
            }
        }));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayNetworking.send(handler.player, PressAltToMeow.NETWORK_CHANNEL, PacketByteBufs.create()));
    }
}
