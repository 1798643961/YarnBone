/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public abstract class EntityTypePredicate {
    public static final EntityTypePredicate ANY = new EntityTypePredicate(){

        @Override
        public boolean matches(EntityType<?> type) {
            return true;
        }

        @Override
        public JsonElement toJson() {
            return JsonNull.INSTANCE;
        }
    };
    private static final Joiner COMMA_JOINER = Joiner.on(", ");

    public abstract boolean matches(EntityType<?> var1);

    public abstract JsonElement toJson();

    public static EntityTypePredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        String string = JsonHelper.asString(json, "type");
        if (string.startsWith("#")) {
            Identifier lv = new Identifier(string.substring(1));
            return new Tagged(TagKey.of(RegistryKeys.ENTITY_TYPE, lv));
        }
        Identifier lv = new Identifier(string);
        EntityType lv2 = (EntityType)Registries.ENTITY_TYPE.getOrEmpty(lv).orElseThrow(() -> new JsonSyntaxException("Unknown entity type '" + lv + "', valid types are: " + COMMA_JOINER.join(Registries.ENTITY_TYPE.getIds())));
        return new Single(lv2);
    }

    public static EntityTypePredicate create(EntityType<?> type) {
        return new Single(type);
    }

    public static EntityTypePredicate create(TagKey<EntityType<?>> tag) {
        return new Tagged(tag);
    }

    static class Tagged
    extends EntityTypePredicate {
        private final TagKey<EntityType<?>> tag;

        public Tagged(TagKey<EntityType<?>> tag) {
            this.tag = tag;
        }

        @Override
        public boolean matches(EntityType<?> type) {
            return type.isIn(this.tag);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive("#" + this.tag.id());
        }
    }

    static class Single
    extends EntityTypePredicate {
        private final EntityType<?> type;

        public Single(EntityType<?> type) {
            this.type = type;
        }

        @Override
        public boolean matches(EntityType<?> type) {
            return this.type == type;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(Registries.ENTITY_TYPE.getId(this.type).toString());
        }
    }
}

