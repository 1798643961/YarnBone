/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.data.server.loottable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class LootTableProvider
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final DataOutput.PathResolver pathResolver;
    private final Set<Identifier> lootTableIds;
    private final List<LootTypeGenerator> lootTypeGenerators;

    public LootTableProvider(DataOutput output, Set<Identifier> lootTableIds, List<LootTypeGenerator> lootTypeGenerators) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "loot_tables");
        this.lootTypeGenerators = lootTypeGenerators;
        this.lootTableIds = lootTableIds;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashMap<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(lootTypeGenerator -> lootTypeGenerator.provider().get().accept((id, builder) -> {
            if (map.put((Identifier)id, builder.type(arg.paramSet).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + id);
            }
        }));
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, id -> null, map::get);
        Sets.SetView<Identifier> set = Sets.difference(this.lootTableIds, map.keySet());
        for (Identifier lv2 : set) {
            lv.report("Missing built-in table: " + lv2);
        }
        map.forEach((id, table) -> LootManager.validate(lv, id, table));
        Multimap<String, String> multimap = lv.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((name, message) -> LOGGER.warn("Found validation problem in {}: {}", name, message));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        return CompletableFuture.allOf((CompletableFuture[])map.entrySet().stream().map(entry -> {
            Identifier lv = (Identifier)entry.getKey();
            LootTable lv2 = (LootTable)entry.getValue();
            Path path = this.pathResolver.resolveJson(lv);
            return DataProvider.writeToPath(writer, LootManager.toJson(lv2), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public final String getName() {
        return "Loot Tables";
    }

    public record LootTypeGenerator(Supplier<LootTableGenerator> provider, LootContextType paramSet) {
    }
}

