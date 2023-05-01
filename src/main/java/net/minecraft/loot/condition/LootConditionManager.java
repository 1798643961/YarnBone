/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LootConditionManager
extends JsonDataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = LootGsons.getConditionGsonBuilder().create();
    private Map<Identifier, LootCondition> conditions = ImmutableMap.of();

    public LootConditionManager() {
        super(GSON, "predicates");
    }

    @Nullable
    public LootCondition get(Identifier id) {
        return this.conditions.get(id);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager arg, Profiler arg2) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        map.forEach((id, json) -> {
            try {
                if (json.isJsonArray()) {
                    LootCondition[] lvs = GSON.fromJson((JsonElement)json, LootCondition[].class);
                    builder.put(id, new AndCondition(lvs));
                } else {
                    LootCondition lv = GSON.fromJson((JsonElement)json, LootCondition.class);
                    builder.put(id, lv);
                }
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't parse loot table {}", id, (Object)exception);
            }
        });
        ImmutableMap<Identifier, LootCondition> map2 = builder.build();
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, map2::get, id -> null);
        map2.forEach((id, condition) -> condition.validate(lv.withCondition("{" + id + "}", (Identifier)id)));
        lv.getMessages().forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
        this.conditions = map2;
    }

    public Set<Identifier> getIds() {
        return Collections.unmodifiableSet(this.conditions.keySet());
    }

    static class AndCondition
    implements LootCondition {
        private final LootCondition[] terms;
        private final Predicate<LootContext> predicate;

        AndCondition(LootCondition[] terms) {
            this.terms = terms;
            this.predicate = LootConditionTypes.joinAnd(terms);
        }

        @Override
        public final boolean test(LootContext arg) {
            return this.predicate.test(arg);
        }

        @Override
        public void validate(LootTableReporter reporter) {
            LootCondition.super.validate(reporter);
            for (int i = 0; i < this.terms.length; ++i) {
                this.terms[i].validate(reporter.makeChild(".term[" + i + "]"));
            }
        }

        @Override
        public LootConditionType getType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public /* synthetic */ boolean test(Object context) {
            return this.test((LootContext)context);
        }
    }
}

