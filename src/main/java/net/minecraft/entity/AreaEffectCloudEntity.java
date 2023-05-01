/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AreaEffectCloudEntity
extends Entity
implements Ownable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_29972 = 5;
    private static final TrackedData<Float> RADIUS = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAITING = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ParticleEffect> PARTICLE_ID = DataTracker.registerData(AreaEffectCloudEntity.class, TrackedDataHandlerRegistry.PARTICLE);
    private static final float MAX_RADIUS = 32.0f;
    private static final float field_40730 = 0.5f;
    private static final float field_40731 = 3.0f;
    public static final float field_40732 = 6.0f;
    public static final float field_40733 = 0.5f;
    private Potion potion = Potions.EMPTY;
    private final List<StatusEffectInstance> effects = Lists.newArrayList();
    private final Map<Entity, Integer> affectedEntities = Maps.newHashMap();
    private int duration = 600;
    private int waitTime = 20;
    private int reapplicationDelay = 20;
    private boolean customColor;
    private int durationOnUse;
    private float radiusOnUse;
    private float radiusGrowth;
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;

    public AreaEffectCloudEntity(EntityType<? extends AreaEffectCloudEntity> arg, World arg2) {
        super(arg, arg2);
        this.noClip = true;
    }

    public AreaEffectCloudEntity(World world, double x, double y, double z) {
        this((EntityType<? extends AreaEffectCloudEntity>)EntityType.AREA_EFFECT_CLOUD, world);
        this.setPosition(x, y, z);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(COLOR, 0);
        this.getDataTracker().startTracking(RADIUS, Float.valueOf(3.0f));
        this.getDataTracker().startTracking(WAITING, false);
        this.getDataTracker().startTracking(PARTICLE_ID, ParticleTypes.ENTITY_EFFECT);
    }

    public void setRadius(float radius) {
        if (!this.world.isClient) {
            this.getDataTracker().set(RADIUS, Float.valueOf(MathHelper.clamp(radius, 0.0f, 32.0f)));
        }
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.setPosition(d, e, f);
    }

    public float getRadius() {
        return this.getDataTracker().get(RADIUS).floatValue();
    }

    public void setPotion(Potion potion) {
        this.potion = potion;
        if (!this.customColor) {
            this.updateColor();
        }
    }

    private void updateColor() {
        if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataTracker().set(COLOR, 0);
        } else {
            this.getDataTracker().set(COLOR, PotionUtil.getColor(PotionUtil.getPotionEffects(this.potion, this.effects)));
        }
    }

    public void addEffect(StatusEffectInstance effect) {
        this.effects.add(effect);
        if (!this.customColor) {
            this.updateColor();
        }
    }

    public int getColor() {
        return this.getDataTracker().get(COLOR);
    }

    public void setColor(int rgb) {
        this.customColor = true;
        this.getDataTracker().set(COLOR, rgb);
    }

    public ParticleEffect getParticleType() {
        return this.getDataTracker().get(PARTICLE_ID);
    }

    public void setParticleType(ParticleEffect particle) {
        this.getDataTracker().set(PARTICLE_ID, particle);
    }

    protected void setWaiting(boolean waiting) {
        this.getDataTracker().set(WAITING, waiting);
    }

    public boolean isWaiting() {
        return this.getDataTracker().get(WAITING);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void tick() {
        block20: {
            ArrayList<StatusEffectInstance> list;
            float f;
            block21: {
                boolean bl2;
                boolean bl;
                block19: {
                    float g;
                    int i2;
                    super.tick();
                    bl = this.isWaiting();
                    f = this.getRadius();
                    if (!this.world.isClient) break block19;
                    if (bl && this.random.nextBoolean()) {
                        return;
                    }
                    ParticleEffect lv = this.getParticleType();
                    if (bl) {
                        i2 = 2;
                        g = 0.2f;
                    } else {
                        i2 = MathHelper.ceil((float)Math.PI * f * f);
                        g = f;
                    }
                    for (int j = 0; j < i2; ++j) {
                        double p;
                        double o;
                        double n;
                        float h = this.random.nextFloat() * ((float)Math.PI * 2);
                        float k = MathHelper.sqrt(this.random.nextFloat()) * g;
                        double d = this.getX() + (double)(MathHelper.cos(h) * k);
                        double e = this.getY();
                        double l = this.getZ() + (double)(MathHelper.sin(h) * k);
                        if (lv.getType() == ParticleTypes.ENTITY_EFFECT) {
                            int m = bl && this.random.nextBoolean() ? 0xFFFFFF : this.getColor();
                            n = (float)(m >> 16 & 0xFF) / 255.0f;
                            o = (float)(m >> 8 & 0xFF) / 255.0f;
                            p = (float)(m & 0xFF) / 255.0f;
                        } else if (bl) {
                            n = 0.0;
                            o = 0.0;
                            p = 0.0;
                        } else {
                            n = (0.5 - this.random.nextDouble()) * 0.15;
                            o = 0.01f;
                            p = (0.5 - this.random.nextDouble()) * 0.15;
                        }
                        this.world.addImportantParticle(lv, d, e, l, n, o, p);
                    }
                    break block20;
                }
                if (this.age >= this.waitTime + this.duration) {
                    this.discard();
                    return;
                }
                boolean bl3 = bl2 = this.age < this.waitTime;
                if (bl != bl2) {
                    this.setWaiting(bl2);
                }
                if (bl2) {
                    return;
                }
                if (this.radiusGrowth != 0.0f) {
                    if ((f += this.radiusGrowth) < 0.5f) {
                        this.discard();
                        return;
                    }
                    this.setRadius(f);
                }
                if (this.age % 5 != 0) break block20;
                this.affectedEntities.entrySet().removeIf(entry -> this.age >= (Integer)entry.getValue());
                list = Lists.newArrayList();
                for (StatusEffectInstance lv2 : this.potion.getEffects()) {
                    list.add(new StatusEffectInstance(lv2.getEffectType(), lv2.mapDuration(i -> i / 4), lv2.getAmplifier(), lv2.isAmbient(), lv2.shouldShowParticles()));
                }
                list.addAll(this.effects);
                if (!list.isEmpty()) break block21;
                this.affectedEntities.clear();
                break block20;
            }
            List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
            if (list2.isEmpty()) break block20;
            for (LivingEntity lv3 : list2) {
                double r;
                double q;
                double s;
                if (this.affectedEntities.containsKey(lv3) || !lv3.isAffectedBySplashPotions() || !((s = (q = lv3.getX() - this.getX()) * q + (r = lv3.getZ() - this.getZ()) * r) <= (double)(f * f))) continue;
                this.affectedEntities.put(lv3, this.age + this.reapplicationDelay);
                for (StatusEffectInstance lv4 : list) {
                    if (lv4.getEffectType().isInstant()) {
                        lv4.getEffectType().applyInstantEffect(this, this.getOwner(), lv3, lv4.getAmplifier(), 0.5);
                        continue;
                    }
                    lv3.addStatusEffect(new StatusEffectInstance(lv4), this);
                }
                if (this.radiusOnUse != 0.0f) {
                    if ((f += this.radiusOnUse) < 0.5f) {
                        this.discard();
                        return;
                    }
                    this.setRadius(f);
                }
                if (this.durationOnUse == 0) continue;
                this.duration += this.durationOnUse;
                if (this.duration > 0) continue;
                this.discard();
                return;
            }
        }
    }

    public float getRadiusOnUse() {
        return this.radiusOnUse;
    }

    public void setRadiusOnUse(float radiusOnUse) {
        this.radiusOnUse = radiusOnUse;
    }

    public float getRadiusGrowth() {
        return this.radiusGrowth;
    }

    public void setRadiusGrowth(float radiusGrowth) {
        this.radiusGrowth = radiusGrowth;
    }

    public int getDurationOnUse() {
        return this.durationOnUse;
    }

    public void setDurationOnUse(int durationOnUse) {
        this.durationOnUse = durationOnUse;
    }

    public int getWaitTime() {
        return this.waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        Entity lv;
        if (this.owner == null && this.ownerUuid != null && this.world instanceof ServerWorld && (lv = ((ServerWorld)this.world).getEntity(this.ownerUuid)) instanceof LivingEntity) {
            this.owner = (LivingEntity)lv;
        }
        return this.owner;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
        this.duration = nbt.getInt("Duration");
        this.waitTime = nbt.getInt("WaitTime");
        this.reapplicationDelay = nbt.getInt("ReapplicationDelay");
        this.durationOnUse = nbt.getInt("DurationOnUse");
        this.radiusOnUse = nbt.getFloat("RadiusOnUse");
        this.radiusGrowth = nbt.getFloat("RadiusPerTick");
        this.setRadius(nbt.getFloat("Radius"));
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        if (nbt.contains("Particle", NbtElement.STRING_TYPE)) {
            try {
                this.setParticleType(ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("Particle")), Registries.PARTICLE_TYPE.getReadOnlyWrapper()));
            }
            catch (CommandSyntaxException commandSyntaxException) {
                LOGGER.warn("Couldn't load custom particle {}", (Object)nbt.getString("Particle"), (Object)commandSyntaxException);
            }
        }
        if (nbt.contains("Color", NbtElement.NUMBER_TYPE)) {
            this.setColor(nbt.getInt("Color"));
        }
        if (nbt.contains("Potion", NbtElement.STRING_TYPE)) {
            this.setPotion(PotionUtil.getPotion(nbt));
        }
        if (nbt.contains("Effects", NbtElement.LIST_TYPE)) {
            NbtList lv = nbt.getList("Effects", NbtElement.COMPOUND_TYPE);
            this.effects.clear();
            for (int i = 0; i < lv.size(); ++i) {
                StatusEffectInstance lv2 = StatusEffectInstance.fromNbt(lv.getCompound(i));
                if (lv2 == null) continue;
                this.addEffect(lv2);
            }
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
        nbt.putInt("Duration", this.duration);
        nbt.putInt("WaitTime", this.waitTime);
        nbt.putInt("ReapplicationDelay", this.reapplicationDelay);
        nbt.putInt("DurationOnUse", this.durationOnUse);
        nbt.putFloat("RadiusOnUse", this.radiusOnUse);
        nbt.putFloat("RadiusPerTick", this.radiusGrowth);
        nbt.putFloat("Radius", this.getRadius());
        nbt.putString("Particle", this.getParticleType().asString());
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }
        if (this.customColor) {
            nbt.putInt("Color", this.getColor());
        }
        if (this.potion != Potions.EMPTY) {
            nbt.putString("Potion", Registries.POTION.getId(this.potion).toString());
        }
        if (!this.effects.isEmpty()) {
            NbtList lv = new NbtList();
            for (StatusEffectInstance lv2 : this.effects) {
                lv.add(lv2.writeNbt(new NbtCompound()));
            }
            nbt.put("Effects", lv);
        }
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (RADIUS.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    public Potion getPotion() {
        return this.potion;
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(this.getRadius() * 2.0f, 0.5f);
    }

    @Override
    @Nullable
    public /* synthetic */ Entity getOwner() {
        return this.getOwner();
    }
}

