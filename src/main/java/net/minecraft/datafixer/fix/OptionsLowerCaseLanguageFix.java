/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class OptionsLowerCaseLanguageFix
extends DataFix {
    public OptionsLowerCaseLanguageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(TypeReferences.OPTIONS), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("lang").asString().result();
            if (optional.isPresent()) {
                return dynamic.set("lang", dynamic.createString(optional.get().toLowerCase(Locale.ROOT)));
            }
            return dynamic;
        }));
    }
}
