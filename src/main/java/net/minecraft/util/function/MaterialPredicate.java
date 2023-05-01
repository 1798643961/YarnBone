/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.util.function;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import org.jetbrains.annotations.Nullable;

public class MaterialPredicate
implements Predicate<BlockState> {
    private static final MaterialPredicate IS_AIR = new MaterialPredicate(Material.AIR){

        @Override
        public boolean test(@Nullable BlockState arg) {
            return arg != null && arg.isAir();
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((BlockState)object);
        }
    };
    private final Material material;

    MaterialPredicate(Material material) {
        this.material = material;
    }

    public static MaterialPredicate create(Material material) {
        return material == Material.AIR ? IS_AIR : new MaterialPredicate(material);
    }

    @Override
    public boolean test(@Nullable BlockState arg) {
        return arg != null && arg.getMaterial() == this.material;
    }

    @Override
    public /* synthetic */ boolean test(@Nullable Object state) {
        return this.test((BlockState)state);
    }
}

