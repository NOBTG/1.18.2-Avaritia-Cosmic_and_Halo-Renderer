package avaritia.nobtg;


import codechicken.lib.CodeChickenLib;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientInit {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init() {
        LOCK.lock();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ClientInit::clientSetupEvent);
        modEventBus.addListener(ClientInit::onModelRegistryEvent);
        AvaritiaTextures.init();
        AvaritiaShaders.init();
    }

    private static void onModelRegistryEvent(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation("avaritia", "halo"), new HaloItemModelLoader());
        ModelLoaderRegistry.registerLoader(CosmicModelLoader.ID, new CosmicModelLoader());
    }

    private static void clientSetupEvent(FMLClientSetupEvent event) {
        ItemProperties.register(CodeChickenLib.INFINITY_BOW.get(), new ResourceLocation("avaritia", "pull"), (stack, level, entity, i) -> {
            if (entity == null) return 0.0f;
            return entity.getUseItem() != stack ? 0.0f : (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / (float)InfinityBowItem.DRAW_TIME;
        });
        ItemProperties.register(CodeChickenLib.INFINITY_BOW.get(), new ResourceLocation("avaritia", "pulling"), (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f);
    }
}