/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package net.minecraft.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;

public class JigsawBlockEntity
extends BlockEntity {
    public static final String TARGET_KEY = "target";
    public static final String POOL_KEY = "pool";
    public static final String JOINT_KEY = "joint";
    public static final String NAME_KEY = "name";
    public static final String FINAL_STATE_KEY = "final_state";
    private Identifier name = new Identifier("empty");
    private Identifier target = new Identifier("empty");
    private RegistryKey<StructurePool> pool = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier("empty"));
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.JIGSAW, pos, state);
    }

    public Identifier getName() {
        return this.name;
    }

    public Identifier getTarget() {
        return this.target;
    }

    public RegistryKey<StructurePool> getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public Joint getJoint() {
        return this.joint;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setTarget(Identifier target) {
        this.target = target;
    }

    public void setPool(RegistryKey<StructurePool> pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString(NAME_KEY, this.name.toString());
        nbt.putString(TARGET_KEY, this.target.toString());
        nbt.putString(POOL_KEY, this.pool.getValue().toString());
        nbt.putString(FINAL_STATE_KEY, this.finalState);
        nbt.putString(JOINT_KEY, this.joint.asString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.name = new Identifier(nbt.getString(NAME_KEY));
        this.target = new Identifier(nbt.getString(TARGET_KEY));
        this.pool = RegistryKey.of(RegistryKeys.TEMPLATE_POOL, new Identifier(nbt.getString(POOL_KEY)));
        this.finalState = nbt.getString(FINAL_STATE_KEY);
        this.joint = Joint.byName(nbt.getString(JOINT_KEY)).orElseGet(() -> JigsawBlock.getFacing(this.getCachedState()).getAxis().isHorizontal() ? Joint.ALIGNED : Joint.ROLLABLE);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        BlockPos lv = this.getPos().offset(this.getCachedState().get(JigsawBlock.ORIENTATION).getFacing());
        Registry<StructurePool> lv2 = world.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        RegistryEntry.Reference<StructurePool> lv3 = lv2.entryOf(this.pool);
        StructurePoolBasedGenerator.generate(world, lv3, this.target, maxDepth, lv, keepJigsaws);
    }

    public /* synthetic */ Packet toUpdatePacket() {
        return this.toUpdatePacket();
    }

    public static enum Joint implements StringIdentifiable
    {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private Joint(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<Joint> byName(String name) {
            return Arrays.stream(Joint.values()).filter(joint -> joint.asString().equals(name)).findFirst();
        }

        public Text asText() {
            return Text.translatable("jigsaw_block.joint." + this.name);
        }
    }
}

