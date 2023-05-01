/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

public interface PositionSourceType<T extends PositionSource> {
    public static final PositionSourceType<BlockPositionSource> BLOCK = PositionSourceType.register("block", new BlockPositionSource.Type());
    public static final PositionSourceType<EntityPositionSource> ENTITY = PositionSourceType.register("entity", new EntityPositionSource.Type());

    public T readFromBuf(PacketByteBuf var1);

    public void writeToBuf(PacketByteBuf var1, T var2);

    public Codec<T> getCodec();

    public static <S extends PositionSourceType<T>, T extends PositionSource> S register(String id, S positionSourceType) {
        return (S)Registry.register(Registries.POSITION_SOURCE_TYPE, id, positionSourceType);
    }

    public static PositionSource read(PacketByteBuf buf) {
        Identifier lv = buf.readIdentifier();
        return Registries.POSITION_SOURCE_TYPE.getOrEmpty(lv).orElseThrow(() -> new IllegalArgumentException("Unknown position source type " + lv)).readFromBuf(buf);
    }

    public static <T extends PositionSource> void write(T positionSource, PacketByteBuf buf) {
        buf.writeIdentifier(Registries.POSITION_SOURCE_TYPE.getId(positionSource.getType()));
        positionSource.getType().writeToBuf(buf, positionSource);
    }
}

