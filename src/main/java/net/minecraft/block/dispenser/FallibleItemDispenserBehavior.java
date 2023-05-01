/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.block.dispenser;

import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.WorldEvents;

public abstract class FallibleItemDispenserBehavior
extends ItemDispenserBehavior {
    private boolean success = true;

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    protected void playSound(BlockPointer pointer) {
        pointer.getWorld().syncWorldEvent(this.isSuccess() ? WorldEvents.DISPENSER_DISPENSES : WorldEvents.DISPENSER_FAILS, pointer.getPos(), 0);
    }
}

