/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.registry.tag;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.WorldPreset;

public class WorldPresetTags {
    public static final TagKey<WorldPreset> NORMAL = WorldPresetTags.of("normal");
    public static final TagKey<WorldPreset> EXTENDED = WorldPresetTags.of("extended");

    private WorldPresetTags() {
    }

    private static TagKey<WorldPreset> of(String id) {
        return TagKey.of(RegistryKeys.WORLD_PRESET, new Identifier(id));
    }
}

