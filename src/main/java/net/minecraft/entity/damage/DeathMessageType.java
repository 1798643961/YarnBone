/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.entity.damage;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum DeathMessageType implements StringIdentifiable
{
    DEFAULT("default"),
    FALL_VARIANTS("fall_variants"),
    INTENTIONAL_GAME_DESIGN("intentional_game_design");

    public static final Codec<DeathMessageType> CODEC;
    private final String id;

    private DeathMessageType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    static {
        CODEC = StringIdentifiable.createCodec(DeathMessageType::values);
    }
}

