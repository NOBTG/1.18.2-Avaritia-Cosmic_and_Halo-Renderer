package avaritia.nobtg;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import net.covers1624.quack.gson.JsonUtils;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.jetbrains.annotations.NotNull;

public class CosmicModelLoader implements IModelLoader<CosmicModelLoader.CosmicModelGeometry> {
    public static final ResourceLocation ID = new ResourceLocation("avaritia", "cosmic");

    public @NotNull CosmicModelGeometry read(@NotNull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        JsonObject cosmicObj = modelContents.getAsJsonObject("cosmic");
        if (cosmicObj == null) throw new IllegalStateException("Missing 'cosmic' object.");
        JsonObject clean = modelContents.deepCopy();
        clean.remove("cosmic");
        clean.remove("loader");
        return new CosmicModelGeometry(deserializationContext.deserialize(clean, BlockModel.class), JsonUtils.getString(cosmicObj, "mask"));
    }

    public void onResourceManagerReload(@NotNull ResourceManager manager) {
    }

    public static class CosmicModelGeometry implements IModelGeometry<CosmicModelGeometry> {
        private final BlockModel baseModel;
        private final String maskTexture;
        private Material maskMaterial;

        public CosmicModelGeometry(BlockModel baseModel, String maskTexture) {
            this.baseModel = baseModel;
            this.maskTexture = maskTexture;
        }

        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new CosmicBakedModel(this.baseModel.bake(bakery, this.baseModel, spriteGetter, modelTransform, modelLocation, true), spriteGetter.apply(this.maskMaterial));
        }

        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            HashSet<Material> materials = new HashSet<>();
            this.maskMaterial = owner.resolveTexture(this.maskTexture);
            if (Objects.equals(this.maskMaterial.texture(), MissingTextureAtlasSprite.getLocation()))
                missingTextureErrors.add(Pair.of(this.maskTexture, owner.getModelName()));
            materials.add(this.maskMaterial);
            materials.addAll(this.baseModel.getMaterials(modelGetter, missingTextureErrors));
            return materials;
        }
    }
}