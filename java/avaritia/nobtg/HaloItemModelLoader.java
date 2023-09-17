package avaritia.nobtg;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import net.covers1624.quack.gson.JsonUtils;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.jetbrains.annotations.NotNull;

public class HaloItemModelLoader implements IModelLoader<HaloItemModelLoader.HaloItemModelGeometry> {

    public @NotNull HaloItemModelGeometry read(@NotNull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        JsonObject haloObj = modelContents.getAsJsonObject("halo");
        if (haloObj == null) {
            throw new IllegalStateException("Missing 'halo' object.");
        }
        IntArrayList layerColors = new IntArrayList();
        JsonArray layerColorsArr = modelContents.getAsJsonArray("layerColors");
        if (layerColorsArr != null) {
            for (JsonElement jsonElement : layerColorsArr) {
                layerColors.add(jsonElement.getAsInt());
            }
        }
        String texture = JsonUtils.getString(haloObj, "texture");
        int color = JsonUtils.getInt(haloObj, "color");
        int size = JsonUtils.getInt(haloObj, "size");
        boolean pulse = JsonUtils.getAsPrimitive(haloObj, "pulse").getAsBoolean();
        JsonObject clean = modelContents.deepCopy();
        clean.remove("halo");
        clean.remove("loader");
        BlockModel baseModel = deserializationContext.deserialize(clean, BlockModel.class);
        return new HaloItemModelGeometry(baseModel, layerColors, texture, color, size, pulse);
    }

    public void onResourceManagerReload(@NotNull ResourceManager manager) {
    }

    public static class HaloItemModelGeometry implements IModelGeometry<HaloItemModelGeometry> {
        private final BlockModel baseModel;
        private final IntList layerColors;
        private final String texture;
        private final int color;
        private final int size;
        private final boolean pulse;
        private Material haloMaterial;

        public HaloItemModelGeometry(BlockModel baseModel, IntList layerColors, String texture, int color, int size, boolean pulse) {
            this.baseModel = baseModel;
            this.layerColors = layerColors;
            this.texture = texture;
            this.color = color;
            this.size = size;
            this.pulse = pulse;
        }

        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            BakedModel bakedBaseModel = this.baseModel.bake(bakery, this.baseModel, spriteGetter, modelTransform, modelLocation, false);
            return new HaloBakedModel(HaloItemModelGeometry.tintLayers(bakedBaseModel, this.layerColors), spriteGetter.apply(this.haloMaterial), this.color, this.size, this.pulse);
        }

        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            HashSet<Material> materials = new HashSet<Material>();
            this.haloMaterial = owner.resolveTexture(this.texture);
            if (Objects.equals(this.haloMaterial.texture(), MissingTextureAtlasSprite.getLocation())) {
                missingTextureErrors.add(Pair.of(this.texture, owner.getModelName()));
            }
            materials.add(this.haloMaterial);
            materials.addAll(this.baseModel.getMaterials(modelGetter, missingTextureErrors));
            return materials;
        }

        private static BakedModel tintLayers(BakedModel model, IntList layerColors) {
            if (layerColors.isEmpty()) {
                return model;
            }
            HashMap<Direction, List<BakedQuad>> faceQuads = new HashMap<Direction, List<BakedQuad>>();
            for (Direction face : Direction.values()) {
                faceQuads.put(face, HaloItemModelGeometry.transformQuads(model.getQuads(null, face, new Random(), EmptyModelData.INSTANCE), layerColors));
            }
            List<BakedQuad> unculled = HaloItemModelGeometry.transformQuads(model.getQuads(null, null, new Random(), EmptyModelData.INSTANCE), layerColors);
            return new SimpleBakedModel(unculled, faceQuads, model.useAmbientOcclusion(), model.usesBlockLight(), model.isGui3d(), model.getParticleIcon(), model.getTransforms(), ItemOverrides.EMPTY);
        }

        private static List<BakedQuad> transformQuads(List<BakedQuad> quads, IntList layerColors) {
            ArrayList<BakedQuad> newQuads = new ArrayList<>(quads.size());
            for (BakedQuad quad : quads) {
                newQuads.add(HaloItemModelGeometry.transformQuad(quad, layerColors));
            }
            return newQuads;
        }

        private static BakedQuad transformQuad(BakedQuad quad, IntList layerColors) {
            int tintIndex = quad.getTintIndex();
            if (tintIndex == -1 || tintIndex >= layerColors.size()) {
                return quad;
            }
            int tint = layerColors.getInt(tintIndex);
            if (tint == -1) {
                return quad;
            }
            Quad newQuad = new Quad();
            newQuad.reset(CachedFormat.BLOCK);
            quad.pipe(newQuad);
            float r = (float) (tint >> 16 & 0xFF) / 255.0f;
            float g = (float) (tint >> 8 & 0xFF) / 255.0f;
            float b = (float) (tint & 0xFF) / 255.0f;
            for (Quad.Vertex v : newQuad.vertices) {
                v.color[0] = v.color[0] * r;
                v.color[1] = v.color[1] * g;
                v.color[2] = v.color[2] * b;
            }
            newQuad.tintIndex = -1;
            return newQuad.bake();
        }
    }
}