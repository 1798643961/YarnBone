/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class NbtPredicate {
    public static final NbtPredicate ANY = new NbtPredicate(null);
    @Nullable
    private final NbtCompound nbt;

    public NbtPredicate(@Nullable NbtCompound nbt) {
        this.nbt = nbt;
    }

    public boolean test(ItemStack stack) {
        if (this == ANY) {
            return true;
        }
        return this.test(stack.getNbt());
    }

    public boolean test(Entity entity) {
        if (this == ANY) {
            return true;
        }
        return this.test(NbtPredicate.entityToNbt(entity));
    }

    public boolean test(@Nullable NbtElement element) {
        if (element == null) {
            return this == ANY;
        }
        return this.nbt == null || NbtHelper.matches(this.nbt, element, true);
    }

    public JsonElement toJson() {
        if (this == ANY || this.nbt == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(this.nbt.toString());
    }

    public static NbtPredicate fromJson(@Nullable JsonElement json) {
        NbtCompound lv;
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        try {
            lv = StringNbtReader.parse(JsonHelper.asString(json, "nbt"));
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
        }
        return new NbtPredicate(lv);
    }

    public static NbtCompound entityToNbt(Entity entity) {
        ItemStack lv2;
        NbtCompound lv = entity.writeNbt(new NbtCompound());
        if (entity instanceof PlayerEntity && !(lv2 = ((PlayerEntity)entity).getInventory().getMainHandStack()).isEmpty()) {
            lv.put("SelectedItem", lv2.writeNbt(new NbtCompound()));
        }
        return lv;
    }
}

