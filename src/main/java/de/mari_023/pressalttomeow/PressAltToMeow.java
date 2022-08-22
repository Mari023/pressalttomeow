package de.mari_023.pressalttomeow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Random;
import java.util.WeakHashMap;

public class PressAltToMeow implements ModInitializer {
    public static final Identifier NETWORK_CHANNEL = new Identifier("pressalttomeow", "m");
    private static final WeakHashMap<ServerPlayerEntity, Long> LAST_MEOW = new WeakHashMap<>();
    private static final Random RANDOM = new Random();

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(NETWORK_CHANNEL, (server, player, handler, buf, packetSender) -> server.execute(() -> {
            if (!player.isSpectator() && (!LAST_MEOW.containsKey(player) || System.currentTimeMillis() - LAST_MEOW.get(player) > 1000)) {
                playSound(player, defaultSounds());
                LAST_MEOW.put(player, System.currentTimeMillis());
            }
        }));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayNetworking.send(handler.player, PressAltToMeow.NETWORK_CHANNEL, PacketByteBufs.create()));
    }

    private static void playSound(ServerPlayerEntity player, SoundEvent soundEvent) {
        player.getWorld().playSound(null, player.getBlockPos(), soundEvent,
                SoundCategory.PLAYERS, 1f, 1f
        );
    }

    public static SoundEvent defaultSounds() {
        return selectSound(SoundEvents.ENTITY_CAT_AMBIENT, SoundEvents.ENTITY_CAT_STRAY_AMBIENT, SoundEvents.ENTITY_CAT_PURREOW, SoundEvents.ENTITY_OCELOT_AMBIENT);
    }

    public static SoundEvent selectSound(SoundEvent ... soundEvent) {
        return (SoundEvent) Arrays.stream(soundEvent).toArray()[RANDOM.nextInt(soundEvent.length)];
    }
}
