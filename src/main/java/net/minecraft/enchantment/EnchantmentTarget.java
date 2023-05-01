/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.Vanishable;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum EnchantmentTarget {
    ARMOR{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof ArmorItem;
        }
    }
    ,
    ARMOR_FEET{

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem lv;
            return item instanceof ArmorItem && (lv = (ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
        }
    }
    ,
    ARMOR_LEGS{

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem lv;
            return item instanceof ArmorItem && (lv = (ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
        }
    }
    ,
    ARMOR_CHEST{

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem lv;
            return item instanceof ArmorItem && (lv = (ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
        }
    }
    ,
    ARMOR_HEAD{

        @Override
        public boolean isAcceptableItem(Item item) {
            ArmorItem lv;
            return item instanceof ArmorItem && (lv = (ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
        }
    }
    ,
    WEAPON{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof SwordItem;
        }
    }
    ,
    DIGGER{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof MiningToolItem;
        }
    }
    ,
    FISHING_ROD{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof FishingRodItem;
        }
    }
    ,
    TRIDENT{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof TridentItem;
        }
    }
    ,
    BREAKABLE{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item.isDamageable();
        }
    }
    ,
    BOW{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof BowItem;
        }
    }
    ,
    WEARABLE{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof Equipment || Block.getBlockFromItem(item) instanceof Equipment;
        }
    }
    ,
    CROSSBOW{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof CrossbowItem;
        }
    }
    ,
    VANISHABLE{

        @Override
        public boolean isAcceptableItem(Item item) {
            return item instanceof Vanishable || Block.getBlockFromItem(item) instanceof Vanishable || BREAKABLE.isAcceptableItem(item);
        }
    };


    public abstract boolean isAcceptableItem(Item var1);
}

