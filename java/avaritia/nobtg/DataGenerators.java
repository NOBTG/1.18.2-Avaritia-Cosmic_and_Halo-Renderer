package avaritia.nobtg;

import codechicken.lib.CodeChickenLib;
import codechicken.lib.datagen.ItemModelProvider;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

public class DataGenerators {
    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init() {
        LOCK.lock();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DataGenerators::registerDataGens);
    }

    private static void registerDataGens(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper files = event.getExistingFileHelper();
        if (event.includeClient()) gen.addProvider(new ItemModels(gen, files));
    }

    private static class ItemModels extends ItemModelProvider {
        protected ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, "avaritia", existingFileHelper);
        }

        protected void registerModels() {
            ModelFile.UncheckedModelFile bowModel = new ModelFile.UncheckedModelFile("item/bow");
            this.getSimple(CodeChickenLib.INFINITY_BOW).parent(bowModel).texture(this.modLoc("item/tools/bow/idle")).customLoader(CosmicCustomLoaderBuilder::new).maskTexture(this.modLoc("item/tools/bow/idle_mask")).end().override(o -> o.predicate(this.modLoc("pulling"), 1.0f).model(this.name(CodeChickenLib.INFINITY_BOW.get()) + "_pull_0", m -> m.parent(bowModel).texture(this.modLoc("item/tools/bow/pull_0")).customLoader(CosmicCustomLoaderBuilder::new).maskTexture(this.modLoc("item/tools/bow/pull_0_mask")).end())).override(o -> o.predicate(this.modLoc("pulling"), 1.0f).predicate(this.modLoc("pull"), 0.65f).model(this.name(CodeChickenLib.INFINITY_BOW.get()) + "_pull_1", m -> m.parent(bowModel).texture(this.modLoc("item/tools/bow/pull_1")).customLoader(CosmicCustomLoaderBuilder::new).maskTexture(this.modLoc("item/tools/bow/pull_1_mask")).end())).override(o -> o.predicate(this.modLoc("pulling"), 1.0f).predicate(this.modLoc("pull"), 0.9f).model(this.name(CodeChickenLib.INFINITY_BOW.get()) + "_pull_2", m -> m.parent(bowModel).texture(this.modLoc("item/tools/bow/pull_2")).customLoader(CosmicCustomLoaderBuilder::new).maskTexture(this.modLoc("item/tools/bow/pull_2_mask")).end()));
            this.handheld(CodeChickenLib.INFINITY_SWORD).texture("layer0", this.modLoc("item/tools/infinity_sword_layer_0")).texture("layer1", this.modLoc("item/tools/infinity_sword_layer_1")).customLoader(CosmicCustomLoaderBuilder::new).maskTexture(this.modLoc("item/tools/infinity_sword_mask"));
            this.generated(CodeChickenLib.INFINITY_INGOT).customLoader(HaloCustomLoaderBuilder::new).haloTexture("halo", new ResourceLocation("avaritia", "item/halo")).haloColor(-16777216).haloSize(10).pulse();
        }

        public @NotNull String getName() {
            return "Avaritia Item Models";
        }
    }
}