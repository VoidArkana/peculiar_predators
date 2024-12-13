package net.rinkablu.peculiarpredators.common.entity.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import net.rinkablu.peculiarpredators.PeculiarPredators;
import net.rinkablu.peculiarpredators.server.MessageHurtMultipart;
import net.rinkablu.peculiarpredators.server.MessageInteractMultipart;

import javax.annotation.Nullable;
import java.util.List;

public class ShastaPartEntity extends PartEntity<ShastaEntity> {


    private final EntityDimensions size;
    public float scale = 1;

    public ShastaPartEntity(ShastaEntity parent, float pWidth, float pHeight) {
        super(parent);
        this.size = EntityDimensions.scalable(pWidth, pHeight);
        this.refreshDimensions();
    }

    public ShastaPartEntity(ShastaEntity shasta, float sizeX, float sizeY, EntityDimensions size) {
        super(shasta);
        this.size = size;
    }

    protected void collideWithNearbyEntities() {
        final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        Entity parent = this.getParent();
        if (parent != null) {
            entities.stream().filter(entity -> entity != parent && !(entity instanceof ShastaPartEntity && ((ShastaPartEntity) entity).getParent() == parent) && entity.isPushable()).forEach(entity -> entity.push(parent));
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if(this.level().isClientSide && this.getParent() != null){
            PeculiarPredators.sendMSGToServer(new MessageInteractMultipart(this.getParent().getId(), hand == InteractionHand.OFF_HAND));
        }
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().interactEntityPartFrom(this, player, hand);
    }

    protected void collideWithEntity(Entity entityIn) {
        entityIn.push(this);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}


    @Nullable
    public ItemStack getPickResult() {
        Entity parent = this.getParent();
        return parent != null ? parent.getPickResult() : ItemStack.EMPTY;
    }

    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        if(this.level().isClientSide && this.getParent() != null && !this.getParent().isInvulnerableTo(source)){
            ResourceLocation key = this.level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getKey(source.type());
            if(key != null){
                PeculiarPredators.sendMSGToServer(new MessageHurtMultipart(this.getId(), this.getParent().getId(), amount, key.toString()));
            }
        }
        return !this.isInvulnerableTo(source) && this.getParent().attackEntityPartFrom(this, source, amount);
    }

    public boolean fireImmune() {
        return true;
    }

    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    public void tick(){
        super.tick();
    }

    public EntityDimensions getDimensions(Pose poseIn) {
        return this.size == null ? EntityDimensions.scalable(0, 0) : this.size.scale(scale);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        parent.hurt(damageSource, damage);
    }

}
