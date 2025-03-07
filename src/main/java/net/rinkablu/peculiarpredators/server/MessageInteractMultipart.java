package net.rinkablu.peculiarpredators.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.rinkablu.peculiarpredators.PeculiarPredators;

import java.util.function.Supplier;

public class MessageInteractMultipart {

    public boolean offhand;
    public int parent;

    public MessageInteractMultipart(int parent, boolean offhand) {
        this.parent = parent;
        this.offhand = offhand;
    }


    public MessageInteractMultipart() {
    }

    public static MessageInteractMultipart read(FriendlyByteBuf buf) {
        return new MessageInteractMultipart(buf.readInt(), buf.readBoolean());
    }

    public static void write(MessageInteractMultipart message, FriendlyByteBuf buf) {
        buf.writeInt(message.parent);
        buf.writeBoolean(message.offhand);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageInteractMultipart message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                    player = PeculiarPredators.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level() != null) {
                        Entity parent = player.level().getEntity(message.parent);
                        if (player.distanceTo(parent) < 20 && parent instanceof Mob) {
                            player.interactOn(parent, message.offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                        }
                    }
                }
            });
        }
    }
}
