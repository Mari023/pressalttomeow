package de.mari_023.pressalttomeow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.Arrays;
import java.util.Random;
import java.util.WeakHashMap;

public class PressAltToMeow implements ModInitializer {
    public static final ResourceLocation NETWORK_CHANNEL = new ResourceLocation("pressalttomeow", "m");
    private static final WeakHashMap<ServerPlayer, Long> LAST_MEOW = new WeakHashMap<>();
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

    private static void playSound(ServerPlayer player, SoundEvent soundEvent) {
        player.level().playSound(null, player.blockPosition(), soundEvent, SoundSource.PLAYERS, 1f, 1f);
    }

    public static SoundEvent defaultSounds() {
        return selectSound(SoundEvents.CAT_AMBIENT, SoundEvents.CAT_STRAY_AMBIENT, SoundEvents.CAT_PURREOW, SoundEvents.OCELOT_AMBIENT);
    }

    public static SoundEvent selectSound(SoundEvent... soundEvent) {
        return (SoundEvent) Arrays.stream(soundEvent).toArray()[RANDOM.nextInt(soundEvent.length)];
    }
}
