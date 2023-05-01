/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.EntityTransformFix;

public class EntityHorseSplitFix
extends EntityTransformFix {
    public EntityHorseSplitFix(Schema schema, boolean bl) {
        super("EntityHorseSplitFix", schema, bl);
    }

    @Override
    protected Pair<String, Typed<?>> transform(String choice, Typed<?> typed) {
        Dynamic<?> dynamic = typed.get(DSL.remainderFinder());
        if (Objects.equals("EntityHorse", choice)) {
            int i = dynamic.get("Type").asInt(0);
            String string2 = switch (i) {
                default -> "Horse";
                case 1 -> "Donkey";
                case 2 -> "Mule";
                case 3 -> "ZombieHorse";
                case 4 -> "SkeletonHorse";
            };
            dynamic.remove("Type");
            Type<?> type = this.getOutputSchema().findChoiceType(TypeReferences.ENTITY).types().get(string2);
            return Pair.of(string2, (Typed)((Pair)typed.write().flatMap(type::readTyped).result().orElseThrow(() -> new IllegalStateException("Could not parse the new horse"))).getFirst());
        }
        return Pair.of(choice, typed);
    }
}

