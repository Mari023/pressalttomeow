package de.mari_023.pressalttomeow;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MeowPayload implements CustomPacketPayload {
    public static Type<MeowPayload> TYPE = new Type<>(ResourceLocation.tryBuild("pressalttomeow", "m"));
    public static StreamCodec<ByteBuf, MeowPayload> CODEC = StreamCodec.of((buffer, value) -> {
    }, (buffer) -> new MeowPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return MeowPayload.TYPE;
    }
}
