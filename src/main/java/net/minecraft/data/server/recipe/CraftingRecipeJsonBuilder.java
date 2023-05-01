/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.data.server.recipe;

import java.util.function.Consumer;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface CraftingRecipeJsonBuilder {
    public static final Identifier ROOT = new Identifier("recipes/root");

    public CraftingRecipeJsonBuilder criterion(String var1, CriterionConditions var2);

    public CraftingRecipeJsonBuilder group(@Nullable String var1);

    public Item getOutputItem();

    public void offerTo(Consumer<RecipeJsonProvider> var1, Identifier var2);

    default public void offerTo(Consumer<RecipeJsonProvider> exporter) {
        this.offerTo(exporter, CraftingRecipeJsonBuilder.getItemId(this.getOutputItem()));
    }

    default public void offerTo(Consumer<RecipeJsonProvider> exporter, String recipePath) {
        Identifier lv2 = new Identifier(recipePath);
        Identifier lv = CraftingRecipeJsonBuilder.getItemId(this.getOutputItem());
        if (lv2.equals(lv)) {
            throw new IllegalStateException("Recipe " + recipePath + " should remove its 'save' argument as it is equal to default one");
        }
        this.offerTo(exporter, lv2);
    }

    public static Identifier getItemId(ItemConvertible item) {
        return Registries.ITEM.getId(item.asItem());
    }
}

