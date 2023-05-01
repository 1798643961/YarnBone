/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.util;

import net.minecraft.util.TranslatableOption;

public enum Arm implements TranslatableOption
{
    LEFT(0, "options.mainHand.left"),
    RIGHT(1, "options.mainHand.right");

    private final int id;
    private final String translationKey;

    private Arm(int id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public Arm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getTranslationKey() {
        return this.translationKey;
    }
}

