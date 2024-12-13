package net.rinkablu.peculiarpredators;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.rinkablu.peculiarpredators.client.renderer.BabyShastaRenderer;
import net.rinkablu.peculiarpredators.client.renderer.ShastaRenderer;
import net.rinkablu.peculiarpredators.client.renderer.TroodonRenderer;
import net.rinkablu.peculiarpredators.client.renderer.YutyRenderer;
import net.rinkablu.peculiarpredators.common.block.PPBlocks;
import net.rinkablu.peculiarpredators.common.entity.PPEntities;
import net.rinkablu.peculiarpredators.common.entity.custom.BabyShastaEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.ShastaEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.TroodonEntity;
import net.rinkablu.peculiarpredators.common.entity.custom.YutyEntity;
import net.rinkablu.peculiarpredators.common.item.PPItems;
import net.rinkablu.peculiarpredators.server.MessageHurtMultipart;
import net.rinkablu.peculiarpredators.server.MessageInteractMultipart;
import net.rinkablu.peculiarpredators.util.ClientProxy;
import net.rinkablu.peculiarpredators.util.CommonProxy;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Mod(PeculiarPredators.MODID)
public class PeculiarPredators
{

    public static final SimpleChannel NETWORK_WRAPPER;
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static int packetsRegistered;
    public static final List<Runnable> CALLBACKS = new ArrayList<>();

    public static final String MODID = "peculiarpredators";

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    static {
        NetworkRegistry.ChannelBuilder channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("yetanotherfishmod", "main_channel"));
        String version = PROTOCOL_VERSION;
        version.getClass();
        channel = channel.clientAcceptedVersions(version::equals);
        version = PROTOCOL_VERSION;
        version.getClass();
        NETWORK_WRAPPER = channel.serverAcceptedVersions(version::equals).networkProtocolVersion(() -> {
            return PROTOCOL_VERSION;
        }).simpleChannel();
    }

    public PeculiarPredators()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        PPEntities.register(modEventBus);
        PPItems.register(modEventBus);
        PPBlocks.register(modEventBus);
        PPCreativeTab.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        PROXY.init();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageHurtMultipart.class, MessageHurtMultipart::write, MessageHurtMultipart::read, MessageHurtMultipart.Handler::handle);
        NETWORK_WRAPPER.registerMessage(packetsRegistered++, MessageInteractMultipart.class, MessageInteractMultipart::write, MessageInteractMultipart::read, MessageInteractMultipart.Handler::handle);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(PROXY::clientInit);

            CALLBACKS.forEach(Runnable::run);
            CALLBACKS.clear();

            EntityRenderers.register(PPEntities.TROODON.get(), TroodonRenderer::new);
            EntityRenderers.register(PPEntities.YUTY.get(), YutyRenderer::new);
            EntityRenderers.register(PPEntities.SHASTA.get(), ShastaRenderer::new);
            EntityRenderers.register(PPEntities.BABY_SHASTA.get(), BabyShastaRenderer::new);

        }

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {

        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event){
            event.put(PPEntities.TROODON.get(), TroodonEntity.createAttributes().build());
            event.put(PPEntities.YUTY.get(), YutyEntity.createAttributes().build());
            event.put(PPEntities.SHASTA.get(), ShastaEntity.createAttributes().build());
            event.put(PPEntities.BABY_SHASTA.get(), BabyShastaEntity.createAttributes().build());
        }

    }


    public static <MSG> void sendMSGToServer(MSG message) {
        NETWORK_WRAPPER.sendToServer(message);
    }
}
