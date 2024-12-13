package net.rinkablu.peculiarpredators.common.entity.custom;

import com.peeko32213.unusualprehistory.common.entity.IAttackEntity;
import com.peeko32213.unusualprehistory.common.entity.msc.util.*;
import com.peeko32213.unusualprehistory.common.entity.msc.util.dino.EntityTameableBaseDinosaurAnimal;
import com.peeko32213.unusualprehistory.core.registry.UPSounds;
import com.peeko32213.unusualprehistory.core.registry.UPTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.rinkablu.peculiarpredators.common.item.PPItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class YutyEntity extends EntityTameableBaseDinosaurAnimal implements CustomFollower, IAttackEntity {

    private static final EntityDataAccessor<Integer> COMMAND = SynchedEntityData.defineId(YutyEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(YutyEntity.class, EntityDataSerializers.BOOLEAN);
    public static final Logger LOGGER = LogManager.getLogger();
    private int attackCooldown;
    public static final int ATTACK_COOLDOWN = 30;

    protected static final RawAnimation YUTY_WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation YUTY_IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation YUTY_ATTACK = RawAnimation.begin().thenLoop("attack");
    protected static final RawAnimation YUTY_SIT = RawAnimation.begin().thenLoop("idle_sit");

    public YutyEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.2F);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0, false));
        this.goalSelector.addGoal(2, new IMeleeAttackGoal());
        this.goalSelector.addGoal(3, new BabyPanicGoal(this, 2.0));
        this.goalSelector.addGoal(1, new CustomRideGoal(this, 2.0));this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F){
            @Override
            public boolean canUse() {
                return super.canUse() && !YutyEntity.this.isVehicle();
            }
        });
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this){
            @Override
            public boolean canUse() {
                return super.canUse() && (!YutyEntity.this.isVehicle() || !YutyEntity.this.isPassenger());
            }
        });
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1f));
        this.goalSelector.addGoal(0, new SitWhenOrderedToGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2D, 20.0F, 6.0F, false));
    }

    protected void playStepSound(BlockPos p_28301_, BlockState p_28302_) {
        this.playSound((SoundEvent)UPSounds.MAJUNGA_STEP.get(), 0.1F, 1.0F);
    }

    public boolean doHurtTarget(Entity target) {
        float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float knockback = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        boolean shouldHurt;
        if (shouldHurt = target.hurt(this.damageSources().mobAttack(this), damage)) {
            if (knockback > 0.0F && target instanceof LivingEntity) {
                ((LivingEntity)target).knockback((double)(knockback * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            this.doEnchantDamageEffects(this, target);
            this.setLastHurtMob(target);
        }

        this.level().broadcastEntityEvent(this, (byte)4);
        return shouldHurt;
    }

    public void performAttack() {
        if (!this.level().isClientSide) {
            this.setSwinging(true);
            Iterator var1 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0)).iterator();

            while(true) {
                Entity entity;
                YutyEntity ulughbegsaurus;
                do {
                    do {
                        do {
                            do {
                                do {
                                    if (!var1.hasNext()) {
                                        return;
                                    }

                                    entity = (Entity)var1.next();
                                } while(this.hasSwung());
                            } while(!this.isSaddled());
                        } while(!this.isTame());
                    } while(!this.hasControllingPassenger());

                    if (!(entity instanceof YutyEntity)) {
                        break;
                    }

                    ulughbegsaurus = (YutyEntity)entity;
                } while(ulughbegsaurus.isTame());

                if (!entity.is(this.getControllingPassenger())) {
                    entity.hurt(this.damageSources().mobAttack(this), 8.0F);
                }
            }
        }
    }

    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.FAIL;
        } else {
            if (this.isFood(itemstack) && !this.isTame()) {
                if (!this.level().isClientSide) {
                    this.tame(player);
                    itemstack.shrink(1);
                    this.level().broadcastEntityEvent(this, (byte)7);
                }

                this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if (this.isTame() && this.isOwnedBy(player)) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    if (!this.level().isClientSide) {
                        this.heal((float)itemstack.getFoodProperties(this).getNutrition());
                    }

                    this.gameEvent(GameEvent.EAT, this);
                    return InteractionResult.SUCCESS;
                } else if (itemstack.getItem() == Items.SADDLE && !this.isSaddled()) {
                    this.usePlayerItem(player, hand, itemstack);
                    this.setSaddled(true);
                    return InteractionResult.SUCCESS;
                } else if (itemstack.getItem() == Items.SHEARS && this.isSaddled()) {
                    this.setSaddled(false);
                    this.spawnAtLocation(Items.SADDLE);
                    return InteractionResult.SUCCESS;
                } else if (!player.isShiftKeyDown() && !this.isBaby() && this.isSaddled()) {
                    if (!this.level().isClientSide) {
                        player.startRiding(this);
                    }

                    return InteractionResult.SUCCESS;
                } else {
                    this.setCommand((this.getCommand() + 1) % 3);
                    if (this.getCommand() == 3) {
                        this.setCommand(0);
                    }

                    int var10001 = this.getCommand();
                    player.displayClientMessage(Component.translatable("entity.unusualprehistory.all.command_" + var10001, new Object[]{this.getName()}), true);
                    boolean sit = this.getCommand() == 2;
                    if (sit) {
                        this.setOrderedToSit(true);
                        return InteractionResult.SUCCESS;
                    } else {
                        this.setOrderedToSit(false);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public boolean isFood(ItemStack pStack) {
        return pStack.is(PPItems.RAW_TROODON_DRUMSTICK.get()) || pStack.is(PPItems.COOKED_TROODON_DRUMSTICK.get()) ;
    }

    public SoundEvent getEatingSound(ItemStack p_28540_) {
        return SoundEvents.GENERIC_EAT;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Saddle", this.isSaddled());
        compound.putInt("Command", this.getCommand());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSaddled(compound.getBoolean("Saddle"));
        this.setCommand(compound.getInt("Command"));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
        this.entityData.define(COMMAND, 0);
    }

    public boolean isSaddled() {
        return (Boolean)this.entityData.get(SADDLED);
    }

    public void setSaddled(boolean saddled) {
        this.entityData.set(SADDLED, saddled);
    }

    @javax.annotation.Nullable
    public LivingEntity getControllingPassenger() {
        Iterator var1 = this.getPassengers().iterator();

        Entity passenger;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            passenger = (Entity)var1.next();
        } while(!(passenger instanceof Player));

        return (Player)passenger;
    }

    protected void positionRider(Entity pPassenger, Entity.MoveFunction pCallback) {
        float ySin = Mth.sin(this.yBodyRot * 0.017453292F);
        float yCos = Mth.cos(this.yBodyRot * 0.017453292F);
        pPassenger.setPos(this.getX() + (double)(1.1F * -ySin),
                this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset() + 0.4000000059604645,
                this.getZ() - (double)(1.1F * -yCos));
    }

    public double getPassengersRidingOffset() {
        return 2.1;
    }

    public void tick() {
        super.tick();
        if (this.attackCooldown > 0) {
            --this.attackCooldown;
        }

        if (this.getCommand() == 2 && !this.isVehicle()) {
            this.setOrderedToSit(true);
        } else {
            this.setOrderedToSit(false);
        }

    }

    public void travel(Vec3 destination) {
        LivingEntity passenger = this.getControllingPassenger();
        if (this.isVehicle() && passenger != null) {
            double delta = 0.5;
            this.setYRot(passenger.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(passenger.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            float f = (float)((double)passenger.xxa * 0.5);
            float f1 = passenger.zza;
            if (f1 <= 0.0F) {
                f1 = (float)((double)f1 * 0.25);
            }

            this.setSpeed((float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 1.1));
            super.travel(new Vec3((double)f, destination.y, (double)f1));
        } else {
            super.travel(destination);
        }

    }

    public float getStepHeight() {
        return 1.2F;
    }

    public boolean isAlliedTo(Entity entityIn) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entityIn == livingentity) {
                return true;
            }

            if (entityIn instanceof TamableAnimal) {
                return ((TamableAnimal)entityIn).isOwnedBy(livingentity);
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(entityIn);
            }
        }

        return entityIn.is(this);
    }

    public int getCommand() {
        return this.entityData.get(COMMAND);
    }

    public void setCommand(int command) {
        this.entityData.set(COMMAND, command);
    }

    public void afterAttack() {
        this.level().broadcastEntityEvent(this, (byte)5);
        this.setSwinging(false);
    }

    public int getMaxAttackCooldown() {
        return 30;
    }

    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    public void setAttackCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    public boolean shouldFollow() {
        return this.getCommand() == 1;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController[]{new AnimationController(this, "Normal", 5, this::Controller)});
        controllers.add(new AnimationController[]{new AnimationController(this, "Attack", 0, this::attackController)});
    }

    protected <E extends YutyEntity> PlayState Controller(AnimationState<E> event) {
        if (this.isFromBook()){
            return PlayState.STOP;
        }
        else {
            if (this.isInSittingPose()){
                event.setAndContinue(YUTY_SIT);
            }else if(this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
                event.setAndContinue(YUTY_WALK);
            } else if (this.onGround() && !this.isInSittingPose()){
                event.setAndContinue(YUTY_IDLE);
            }
            return PlayState.CONTINUE;
        }
    }
    protected <E extends YutyEntity> PlayState attackController(AnimationState<E> event) {
        if (this.swinging && event.getController().getAnimationState().equals(AnimationController.State.STOPPED)){
            event.getController().forceAnimationReset();
            event.getController().setAnimation(YUTY_ATTACK);
            this.swinging=false;
        }
        return PlayState.CONTINUE;
    }

    protected SoundEvent getAttackSound() {
        return (SoundEvent) UPSounds.ULUGH_BITE.get();
    }

    protected int getKillHealAmount() {
        return 5;
    }

    protected boolean canGetHungry() {
        return false;
    }

    protected boolean hasTargets() {
        return false;
    }

    protected boolean hasAvoidEntity() {
        return false;
    }

    protected boolean hasCustomNavigation() {
        return false;
    }

    protected boolean hasMakeStuckInBlock() {
        return false;
    }

    protected boolean customMakeStuckInBlockCheck(BlockState blockState) {
        return false;
    }

    @Override
    protected TagKey<EntityType<?>> getTargetTag() {
        return null;
    }

    private void attack(LivingEntity entity) {
        entity.hurt(this.damageSources().mobAttack(this), 5.0F);
    }

    class IMeleeAttackGoal extends MeleeAttackGoal {
        public IMeleeAttackGoal() {
            super(YutyEntity.this, 1.6, true);
        }

        protected double getAttackReachSqr(LivingEntity p_25556_) {
            return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 0.66F + p_25556_.getBbWidth());
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0) {
                this.resetAttackCooldown();
                ((YutyEntity)this.mob).attack(enemy);
            }

        }
    }
}
