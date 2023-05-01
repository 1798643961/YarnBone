/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.text.Text;

public class ItemLoreToTextFix
extends DataFix {
    public ItemLoreToTextFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<?> opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("Item Lore componentize", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), dynamic -> dynamic.update("display", dynamic2 -> dynamic2.update("Lore", dynamic -> DataFixUtils.orElse(dynamic.asStreamOpt().map(ItemLoreToTextFix::fixLoreNbt).map(dynamic::createList).result(), dynamic))))));
    }

    private static <T> Stream<Dynamic<T>> fixLoreNbt(Stream<Dynamic<T>> nbt) {
        return nbt.map(dynamic -> DataFixUtils.orElse(dynamic.asString().map(ItemLoreToTextFix::componentize).map(dynamic::createString).result(), dynamic));
    }

    private static String componentize(String string) {
        return Text.Serializer.toJson(Text.literal(string));
    }
}

