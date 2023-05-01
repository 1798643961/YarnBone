/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractDonkeyEntity
extends AbstractHorseEntity {
    private static final TrackedData<Boolean> CHEST = DataTracker.registerData(AbstractDonkeyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final int field_30412 = 15;

    protected AbstractDonkeyEntity(EntityType<? extends AbstractDonkeyEntity> arg, World arg2) {
        super((EntityType<? extends AbstractHorseEntity>)arg, arg2);
        this.playExtraHorseSounds = false;
    }

    @Override
    protected void initAttributes(Random random) {
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(AbstractDonkeyEntity.getChildHealthBonus(random::nextInt));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHEST, false);
    }

    public static DefaultAttributeContainer.Builder createAbstractDonkeyAttributes() {
        return AbstractDonkeyEntity.createBaseHorseAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.175f).add(EntityAttributes.HORSE_JUMP_STRENGTH, 0.5);
    }

    public boolean hasChest() {
        return this.dataTracker.get(CHEST);
    }

    public void setHasChest(boolean hasChest) {
        this.dataTracker.set(CHEST, hasChest);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 17;
        }
        return super.getInventorySize();
    }

    @Override
    public double getMountedHeightOffset() {
        return super.getMountedHeightOffset() - 0.25;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasChest()) {
            if (!this.world.isClient) {
                this.dropItem(Blocks.CHEST);
            }
            this.setHasChest(false);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("ChestedHorse", this.hasChest());
        if (this.hasChest()) {
            NbtList lv = new NbtList();
            for (int i = 2; i < this.items.size(); ++i) {
                ItemStack lv2 = this.items.getStack(i);
                if (lv2.isEmpty()) continue;
                NbtCompound lv3 = new NbtCompound();
                lv3.putByte("Slot", (byte)i);
                lv2.writeNbt(lv3);
                lv.add(lv3);
            }
            nbt.put("Items", lv);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasChest(nbt.getBoolean("ChestedHorse"));
        this.onChestedStatusChanged();
        if (this.hasChest()) {
            NbtList lv = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < lv.size(); ++i) {
                NbtCompound lv2 = lv.getCompound(i);
                int j = lv2.getByte("Slot") & 0xFF;
                if (j < 2 || j >= this.items.size()) continue;
                this.items.setStack(j, ItemStack.fromNbt(lv2));
            }
        }
        this.updateSaddle();
    }

    @Override
    public StackReference getStackReference(int mappedIndex) {
        if (mappedIndex == 499) {
            return new StackReference(){

                @Override
                public ItemStack get() {
                    return AbstractDonkeyEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
                }

                @Override
                public boolean set(ItemStack stack) {
                    if (stack.isEmpty()) {
                        if (AbstractDonkeyEntity.this.hasChest()) {
                            AbstractDonkeyEntity.this.setHasChest(false);
                            AbstractDonkeyEntity.this.onChestedStatusChanged();
                        }
                        return true;
                    }
                    if (stack.isOf(Items.CHEST)) {
                        if (!AbstractDonkeyEntity.this.hasChest()) {
                            AbstractDonkeyEntity.this.setHasChest(true);
                            AbstractDonkeyEntity.this.onChestedStatusChanged();
                        }
                        return true;
                    }
                    return false;
                }
            };
        }
        return super.getStackReference(mappedIndex);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        boolean bl;
        boolean bl2 = bl = !this.isBaby() && this.isTame() && player.shouldCancelInteraction();
        if (this.hasPassengers() || bl) {
            return super.interactMob(player, hand);
        }
        ItemStack lv = player.getStackInHand(hand);
        if (!lv.isEmpty()) {
            if (this.isBreedingItem(lv)) {
                return this.interactHorse(player, lv);
            }
            if (!this.isTame()) {
                this.playAngrySound();
                return ActionResult.success(this.world.isClient);
            }
            if (!this.hasChest() && lv.isOf(Items.CHEST)) {
                this.addChest(player, lv);
                return ActionResult.success(this.world.isClient);
            }
        }
        return super.interactMob(player, hand);
    }

    private void addChest(PlayerEntity player, ItemStack chest) {
        this.setHasChest(true);
        this.playAddChestSound();
        if (!player.getAbilities().creativeMode) {
            chest.decrement(1);
        }
        this.onChestedStatusChanged();
    }

    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_DONKEY_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    public int getInventoryColumns() {
        return 5;
    }
}

