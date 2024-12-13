package net.rinkablu.peculiarpredators.common.entity.custom;

import com.peeko32213.unusualprehistory.common.entity.IBookEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShastaEntity extends WaterAnimal implements IBookEntity, GeoEntity {

    public final ShastaPartEntity head;
    public final ShastaPartEntity tail;
    public final ShastaPartEntity[] allParts;
    public int ringBufferIndex = -1;
    public final float[][] ringBuffer = new float[64][3];

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> FROM_BOOK = SynchedEntityData.defineId(ShastaEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final RawAnimation SHASTA_SWIM = RawAnimation.begin().thenLoop("swim");
    protected static final RawAnimation SHASTA_BEACHED = RawAnimation.begin().thenLoop("beached");

    public ShastaEntity(EntityType<? extends WaterAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

        this.head = new ShastaPartEntity(this, 5F,5F );
        this.tail = new ShastaPartEntity(this, 5F, 5F);
        this.allParts = new ShastaPartEntity[]{this.head, this.tail};

        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.01F, true);
        this.lookControl = new SmoothSwimmingLookControl(this,10);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    protected void registerGoals() {
        //this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }



    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.MOVEMENT_SPEED, (double)0.8F);
    }


    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BOOK, false);
    }

    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel);
    }


    public boolean canBeLeashed(Player pPlayer) {
        return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public boolean isFromBook() {
        return (Boolean)this.entityData.get(FROM_BOOK);
    }

    public void setFromBook(boolean fromBook) {
        this.entityData.set(FROM_BOOK, fromBook);
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.DOLPHIN_SWIM;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.isNoAi()) {
            if (this.ringBufferIndex < 0) {
                for (int i = 0; i < this.ringBuffer.length; ++i) {
                    this.ringBuffer[i][0] = this.getYRot();
                    this.ringBuffer[i][1] = (float) this.getY();
                }
            }
            this.ringBufferIndex++;
            if (this.ringBufferIndex == this.ringBuffer.length) {
                this.ringBufferIndex = 0;
            }
            this.ringBuffer[this.ringBufferIndex][0] = this.getYRot();
            this.ringBuffer[ringBufferIndex][1] = (float) this.getY();
            Vec3[] avector3d = new Vec3[this.allParts.length];

            for (int j = 0; j < this.allParts.length; ++j) {
                this.allParts[j].collideWithNearbyEntities();
                avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
            }
            final float f17 = this.getYRot() * Mth.DEG_TO_RAD;
            final float pitch = this.getXRot() * Mth.DEG_TO_RAD;
            final float xRotDiv90 = Math.abs(this.getXRot() / 90F);
            final float f3 = Mth.sin(f17) * (1 - xRotDiv90);
            final float f18 = Mth.cos(f17) * (1 - xRotDiv90);

            this.setPartPosition(this.head, f3 * -5F, -pitch * 0.8F, -f18 * -5F);
            this.setPartPosition(this.tail, f3 * 5F, pitch * 0.8F, f18 * -5F);

            for (int l = 0; l < this.allParts.length; ++l) {
                this.allParts[l].xo = avector3d[l].x;
                this.allParts[l].yo = avector3d[l].y;
                this.allParts[l].zo = avector3d[l].z;
                this.allParts[l].xOld = avector3d[l].x;
                this.allParts[l].yOld = avector3d[l].y;
                this.allParts[l].zOld = avector3d[l].z;
            }
        }
    }


    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public net.minecraftforge.entity.PartEntity<?>[] getParts() {
        return this.allParts;
    }

    private void setPartPosition(ShastaPartEntity part, double offsetX, double offsetY, double offsetZ) {
        part.setPos(this.getX() + offsetX * part.scale, this.getY() + offsetY * part.scale, this.getZ() + offsetZ * part.scale);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.006, 0.0));
            }
        } else {
            super.travel(travelVector);
        }

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController[]{new AnimationController(this, "Normal", 20, this::Controller)});
    }

    protected <E extends GeoAnimatable> PlayState Controller(AnimationState<E> event) {
        if (this.isFromBook()){
            return PlayState.STOP;

        }else if(this.isInWater()) {

            event.setAndContinue(SHASTA_SWIM);

        } else if (this.onGround() && !this.isInWater()){

            event.setAndContinue(SHASTA_BEACHED);

        }
        return PlayState.CONTINUE;
    }


    public boolean attackEntityPartFrom(ShastaPartEntity shastaPartEntity, DamageSource source, float amount) {
        return this.hurt(source, amount);
    }

    public InteractionResult interactEntityPartFrom(ShastaPartEntity shastaPartEntity, Player player, InteractionHand hand) {
        return this.mobInteract(player, hand);
    }
}
