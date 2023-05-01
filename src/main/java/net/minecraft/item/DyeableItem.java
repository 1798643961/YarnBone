/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface DyeableItem {
    public static final String COLOR_KEY = "color";
    public static final String DISPLAY_KEY = "display";
    public static final int DEFAULT_COLOR = 10511680;

    default public boolean hasColor(ItemStack stack) {
        NbtCompound lv = stack.getSubNbt(DISPLAY_KEY);
        return lv != null && lv.contains(COLOR_KEY, NbtElement.NUMBER_TYPE);
    }

    default public int getColor(ItemStack stack) {
        NbtCompound lv = stack.getSubNbt(DISPLAY_KEY);
        if (lv != null && lv.contains(COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return lv.getInt(COLOR_KEY);
        }
        return 10511680;
    }

    default public void removeColor(ItemStack stack) {
        NbtCompound lv = stack.getSubNbt(DISPLAY_KEY);
        if (lv != null && lv.contains(COLOR_KEY)) {
            lv.remove(COLOR_KEY);
        }
    }

    default public void setColor(ItemStack stack, int color) {
        stack.getOrCreateSubNbt(DISPLAY_KEY).putInt(COLOR_KEY, color);
    }

    public static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
        int n;
        float h;
        ItemStack lv = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem lv2 = null;
        Item lv3 = stack.getItem();
        if (lv3 instanceof DyeableItem) {
            lv2 = (DyeableItem)((Object)lv3);
            lv = stack.copy();
            lv.setCount(1);
            if (lv2.hasColor(stack)) {
                int k = lv2.getColor(lv);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                h = (float)(k & 0xFF) / 255.0f;
                i += (int)(Math.max(f, Math.max(g, h)) * 255.0f);
                is[0] = is[0] + (int)(f * 255.0f);
                is[1] = is[1] + (int)(g * 255.0f);
                is[2] = is[2] + (int)(h * 255.0f);
                ++j;
            }
            for (DyeItem lv4 : colors) {
                float[] fs = lv4.getColor().getColorComponents();
                int l = (int)(fs[0] * 255.0f);
                int m = (int)(fs[1] * 255.0f);
                n = (int)(fs[2] * 255.0f);
                i += Math.max(l, Math.max(m, n));
                is[0] = is[0] + l;
                is[1] = is[1] + m;
                is[2] = is[2] + n;
                ++j;
            }
        }
        if (lv2 == null) {
            return ItemStack.EMPTY;
        }
        int k = is[0] / j;
        int o = is[1] / j;
        int p = is[2] / j;
        h = (float)i / (float)j;
        float q = Math.max(k, Math.max(o, p));
        k = (int)((float)k * h / q);
        o = (int)((float)o * h / q);
        p = (int)((float)p * h / q);
        n = k;
        n = (n << 8) + o;
        n = (n << 8) + p;
        lv2.setColor(lv, n);
        return lv;
    }
}

