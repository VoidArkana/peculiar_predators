package net.rinkablu.peculiarpredators.common.entity.custom;

import com.mojang.datafixers.DataFixUtils;
import com.peeko32213.unusualprehistory.common.entity.msc.util.dino.EntityBaseDinosaurAnimal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TroodonEntity extends EntityBaseDinosaurAnimal implements NeutralMob{

    @javax.annotation.Nullable
    private TroodonEntity leader;
    private int schoolSize = 1;

    protected static final RawAnimation TROODON_WALK = RawAnimation.begin().thenLoop("walking");
    protected static final RawAnimation TROODON_IDLE = RawAnimation.begin().thenLoop("idle");

    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(TroodonEntity.class, EntityDataSerializers.INT);
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @javax.annotation.Nullable
    private UUID persistentAngerTarget;

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(TroodonEntity.class, EntityDataSerializers.INT);

    public TroodonEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    //attributes
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(3, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));

        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));

        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, YutyEntity.class, 8.0F, 1.6D, 1.4D));        super.registerGoals();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
        this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        this.addPersistentAngerSaveData(compound);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));

        this.readPersistentAngerSaveData(this.level(), compound);
    }

    //variants
    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    public void determineVariant(int variantChange) {
        if (variantChange>50){
            this.setVariant(0);
        }else {
            this.setVariant(1);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag tag) {
        this.determineVariant(this.random.nextInt(0, 100));
        return super.finalizeSpawn(levelAccessor, difficultyInstance, spawnType, spawnGroupData, tag);
    }

    @Override
    protected SoundEvent getAttackSound() {
        return null;
    }

    @Override
    protected int getKillHealAmount() {
        return 0;
    }

    @Override
    protected boolean canGetHungry() {
        return false;
    }

    @Override
    protected boolean hasTargets() {
        return false;
    }

    @Override
    protected boolean hasAvoidEntity() {
        return false;
    }

    @Override
    protected boolean hasCustomNavigation() {
        return false;
    }

    @Override
    protected boolean hasMakeStuckInBlock() {
        return false;
    }

    @Override
    protected boolean customMakeStuckInBlockCheck(BlockState blockState) {
        return false;
    }

    @Override
    protected TagKey<EntityType<?>> getTargetTag() {
        return null;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController[]{new AnimationController(this, "Normal", 5, this::Controller)});
    }

    protected <E extends TroodonEntity> PlayState Controller(AnimationState<E> event) {
        if (this.isFromBook() || (this.isInWater() && this.getDeltaMovement().horizontalDistanceSqr() < 1.0E-6)){
            return PlayState.STOP;
        }
        else {
             if(this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && this.onGround()) {
                 event.setAndContinue(TROODON_WALK);
            } else if (this.onGround()){
                 event.setAndContinue(TROODON_IDLE);
            }
            return PlayState.CONTINUE;
        }
    }

    public boolean isFollower() {
        return this.leader != null && this.leader.isAlive();
    }

    public TroodonEntity startFollowing(TroodonEntity pLeader) {
        this.leader = pLeader;
        pLeader.addFollower();
        return pLeader;
    }

    public void stopFollowing() {
        this.leader.removeFollower();
        this.leader = null;
    }

    public int getMaxSchoolSize() {
        return 6;
    }

    private void addFollower() {
        ++this.schoolSize;
    }

    private void removeFollower() {
        --this.schoolSize;
    }

    public boolean canBeFollowed() {
        return this.hasFollowers() && this.schoolSize < this.getMaxSchoolSize();
    }

    public void tick() {
        super.tick();
        if (this.hasFollowers() && this.level().random.nextInt(200) == 1) {
            List<? extends TroodonEntity> list = this.level().getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
            if (list.size() <= 1) {
                this.schoolSize = 1;
            }
        }

    }

    public boolean hasFollowers() {
        return this.schoolSize > 1;
    }

    public boolean inRangeOfLeader() {
        return this.distanceToSqr(this.leader) <= 121.0D && this.distanceToSqr(this.leader) > 3.0D;
    }

    public void pathToLeader() {
        if (this.isFollower()) {
            this.getNavigation().moveTo(this.leader, 1.0D);
        }

    }

    public void addFollowers(Stream<? extends TroodonEntity> pFollowers) {
        pFollowers.limit((long)(this.getMaxSchoolSize() - this.schoolSize)).filter((p_27538_) -> {
            return p_27538_ != this;
        }).forEach((p_27536_) -> {
            p_27536_.startFollowing(this);
        });
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int pTime) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }


    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@javax.annotation.Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }


    public class FollowFlockLeaderGoal extends Goal {
        private static final int INTERVAL_TICKS = 200;
        private final TroodonEntity mob;
        private int timeToRecalcPath;
        private int nextStartTick;

        public FollowFlockLeaderGoal(TroodonEntity pFish) {
            this.mob = pFish;
            this.nextStartTick = this.nextStartTick(pFish);
        }

        protected int nextStartTick(TroodonEntity pTaskOwner) {
            return reducedTickDelay(200 + pTaskOwner.getRandom().nextInt(200) % 20);
        }

        public boolean canUse() {
            if (this.mob.hasFollowers()) {
                return false;
            } else if (this.mob.isFollower()) {
                return true;
            } else if (this.nextStartTick > 0) {
                --this.nextStartTick;
                return false;
            } else {
                this.nextStartTick = this.nextStartTick(this.mob);
                Predicate<TroodonEntity> predicate = (p_25258_) -> {
                    return p_25258_.canBeFollowed() || !p_25258_.isFollower();
                };
                List<? extends TroodonEntity> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), this.mob.getBoundingBox().inflate(16.0D, 8.0D, 16.0D), predicate);
                TroodonEntity abstractschoolingfish = DataFixUtils.orElse(list.stream().filter(TroodonEntity::canBeFollowed).findAny(), this.mob);
                abstractschoolingfish.addFollowers(list.stream().filter((p_25255_) -> {
                    return !p_25255_.isFollower();
                }));
                return this.mob.isFollower();
            }
        }

        public boolean canContinueToUse() {
            return this.mob.isFollower() && this.mob.inRangeOfLeader();
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.mob.stopFollowing();
        }

        public void tick() {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.mob.pathToLeader();
            }
        }
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if (pFallDistance > 1.0F) {
            this.playSound(SoundEvents.HORSE_LAND, 0F, 1.0F);
        }

        int i = this.calculateFallDamage(pFallDistance, pMultiplier);
        if (i <= 0) {
            return false;
        } else {
            this.hurt(pSource, (float)i);
            if (this.isVehicle()) {
                Iterator var5 = this.getIndirectPassengers().iterator();

                while(var5.hasNext()) {
                    Entity entity = (Entity)var5.next();
                    entity.hurt(pSource, (float)i);
                }
            }

            return true;
        }
    }

}
