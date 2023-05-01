/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class StructureSettingsFlattenFix
extends DataFix {
    public StructureSettingsFlattenFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.WORLD_GEN_SETTINGS);
        OpticFinder<?> opticFinder = type.findField("dimensions");
        return this.fixTypeEverywhereTyped("StructureSettingsFlatten", type, typed2 -> typed2.updateTyped(opticFinder, typed -> {
            Dynamic<?> dynamic = typed.write().result().orElseThrow();
            Dynamic<?> dynamic2 = dynamic.updateMapValues(StructureSettingsFlattenFix::method_40116);
            return opticFinder.type().readTyped(dynamic2).result().orElseThrow().getFirst();
        }));
    }

    private static Pair<Dynamic<?>, Dynamic<?>> method_40116(Pair<Dynamic<?>, Dynamic<?>> pair) {
        Dynamic<?> dynamic = pair.getSecond();
        return Pair.of(pair.getFirst(), dynamic.update("generator", dynamic2 -> dynamic2.update("settings", dynamic -> dynamic.update("structures", StructureSettingsFlattenFix::method_40117))));
    }

    private static Dynamic<?> method_40117(Dynamic<?> dynamic) {
        Dynamic<?> dynamic2 = dynamic.get("structures").orElseEmptyMap().updateMapValues(pair -> pair.mapSecond(dynamic2 -> dynamic2.set("type", dynamic.createString("minecraft:random_spread"))));
        return DataFixUtils.orElse(dynamic.get("stronghold").result().map(dynamic3 -> dynamic2.set("minecraft:stronghold", dynamic3.set("type", dynamic.createString("minecraft:concentric_rings")))), dynamic2);
    }
}

