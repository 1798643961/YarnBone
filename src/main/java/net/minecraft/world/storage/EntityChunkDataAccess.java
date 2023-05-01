/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.world.storage;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import net.minecraft.world.storage.StorageIoWorker;
import org.slf4j.Logger;

public class EntityChunkDataAccess
implements ChunkDataAccess<Entity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String ENTITIES_KEY = "Entities";
    private static final String POSITION_KEY = "Position";
    private final ServerWorld world;
    private final StorageIoWorker dataLoadWorker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final TaskExecutor<Runnable> taskExecutor;
    protected final DataFixer dataFixer;

    public EntityChunkDataAccess(ServerWorld world, Path path, DataFixer dataFixer, boolean dsync, Executor executor) {
        this.world = world;
        this.dataFixer = dataFixer;
        this.taskExecutor = TaskExecutor.create(executor, "entity-deserializer");
        this.dataLoadWorker = new StorageIoWorker(path, dsync, "entities");
    }

    @Override
    public CompletableFuture<ChunkDataList<Entity>> readChunkData(ChunkPos pos) {
        if (this.emptyChunks.contains(pos.toLong())) {
            return CompletableFuture.completedFuture(EntityChunkDataAccess.emptyDataList(pos));
        }
        return this.dataLoadWorker.readChunkData(pos).thenApplyAsync(nbt -> {
            if (nbt.isEmpty()) {
                this.emptyChunks.add(pos.toLong());
                return EntityChunkDataAccess.emptyDataList(pos);
            }
            try {
                ChunkPos lv = EntityChunkDataAccess.getChunkPos((NbtCompound)nbt.get());
                if (!Objects.equals(pos, lv)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", pos, pos, lv);
                }
            }
            catch (Exception exception) {
                LOGGER.warn("Failed to parse chunk {} position info", (Object)pos, (Object)exception);
            }
            NbtCompound lv2 = this.fixChunkData((NbtCompound)nbt.get());
            NbtList lv3 = lv2.getList(ENTITIES_KEY, NbtElement.COMPOUND_TYPE);
            List list = EntityType.streamFromNbt(lv3, this.world).collect(ImmutableList.toImmutableList());
            return new ChunkDataList(pos, list);
        }, this.taskExecutor::send);
    }

    private static ChunkPos getChunkPos(NbtCompound chunkNbt) {
        int[] is = chunkNbt.getIntArray(POSITION_KEY);
        return new ChunkPos(is[0], is[1]);
    }

    private static void putChunkPos(NbtCompound chunkNbt, ChunkPos pos) {
        chunkNbt.put(POSITION_KEY, new NbtIntArray(new int[]{pos.x, pos.z}));
    }

    private static ChunkDataList<Entity> emptyDataList(ChunkPos pos) {
        return new ChunkDataList<Entity>(pos, ImmutableList.of());
    }

    @Override
    public void writeChunkData(ChunkDataList<Entity> dataList) {
        ChunkPos lv = dataList.getChunkPos();
        if (dataList.isEmpty()) {
            if (this.emptyChunks.add(lv.toLong())) {
                this.dataLoadWorker.setResult(lv, null);
            }
            return;
        }
        NbtList lv2 = new NbtList();
        dataList.stream().forEach(entity -> {
            NbtCompound lv = new NbtCompound();
            if (entity.saveNbt(lv)) {
                lv2.add(lv);
            }
        });
        NbtCompound lv3 = NbtHelper.putDataVersion(new NbtCompound());
        lv3.put(ENTITIES_KEY, lv2);
        EntityChunkDataAccess.putChunkPos(lv3, lv);
        this.dataLoadWorker.setResult(lv, lv3).exceptionally(ex -> {
            LOGGER.error("Failed to store chunk {}", (Object)lv, ex);
            return null;
        });
        this.emptyChunks.remove(lv.toLong());
    }

    @Override
    public void awaitAll(boolean sync) {
        this.dataLoadWorker.completeAll(sync).join();
        this.taskExecutor.awaitAll();
    }

    private NbtCompound fixChunkData(NbtCompound chunkNbt) {
        int i = NbtHelper.getDataVersion(chunkNbt, -1);
        return DataFixTypes.ENTITY_CHUNK.update(this.dataFixer, chunkNbt, i);
    }

    @Override
    public void close() throws IOException {
        this.dataLoadWorker.close();
    }
}

