package avaritia.nobtg;

import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.LambdaUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Transformation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.SimpleModelState;
import org.jetbrains.annotations.NotNull;

public class CosmicBakedModel extends WrappedItemModel implements IItemRenderer {
    private final List<BakedQuad> maskQuads;
    private final ItemOverrides overrideList = new ItemOverrides() {
        public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
            CosmicBakedModel.this.entity = entity;
            CosmicBakedModel.this.world = world == null ? (entity == null ? null : (ClientLevel) entity.level) : null;
            return CosmicBakedModel.this.wrapped.getOverrides().resolve(originalModel, stack, world, entity, seed);
        }
    };
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();

    public CosmicBakedModel(BakedModel wrapped, TextureAtlasSprite maskSprite) {
        super(wrapped);
        this.maskQuads = CosmicBakedModel.bakeItem(maskSprite);
    }

    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        this.renderWrapped(stack, pStack, source, packedLight, packedOverlay, true);
        if (source instanceof MultiBufferSource.BufferSource bs) bs.endBatch();
        RenderType cosmicRenderType = RenderType.create("avaritia:cosmic", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 0x200000, true, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> AvaritiaShaders.cosmicShader)).setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED).createCompositeState(true));
        float yaw = 0.0f;
        float pitch = 0.0f;
        float scale = 1.0f;
        if (transformType != ItemTransforms.TransformType.GUI) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                yaw = (float) ((double) (mc.player.getYRot() * 2.0f) * Math.PI / 360.0);
                pitch = -((float) ((double) (mc.player.getXRot() * 2.0f) * Math.PI / 360.0));
            }
        } else scale = 25.0f;
        if (AvaritiaShaders.cosmicOpacity != null) AvaritiaShaders.cosmicOpacity.glUniform1f(1.0f);
        AvaritiaShaders.cosmicYaw.glUniform1f(yaw);
        AvaritiaShaders.cosmicPitch.glUniform1f(pitch);
        AvaritiaShaders.cosmicExternalScale.glUniform1f(scale);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        VertexConsumer cons = source.getBuffer(cosmicRenderType);
        itemRenderer.renderQuadList(pStack, cons, this.maskQuads, stack, packedLight, packedOverlay);
    }

    public ModelState getModelTransform() {
        return this.parentState;
    }

    public boolean useAmbientOcclusion() {
        return this.wrapped.useAmbientOcclusion();
    }

    public boolean isGui3d() {
        return this.wrapped.isGui3d();
    }

    public boolean usesBlockLight() {
        return this.wrapped.usesBlockLight();
    }

    public ItemOverrides getOverrides() {
        return this.overrideList;
    }

    private static List<BakedQuad> bakeItem(TextureAtlasSprite... sprites) {
        return CosmicBakedModel.bakeItem(Transformation.identity(), sprites);
    }

    private static List<BakedQuad> bakeItem(Transformation state, TextureAtlasSprite... sprites) {
        LambdaUtils.checkArgument(sprites, "Sprites must not be Null or empty!", ArrayUtils::isNullOrContainsNull);
        LinkedList<BakedQuad> quads = new LinkedList<>();
        for (int i = 0; i < sprites.length; ++i) {
            TextureAtlasSprite sprite = sprites[i];
            for (BlockElement element : ITEM_MODEL_GENERATOR.processFrames(i, "layer" + i, sprite))
                for (Map.Entry entry : element.faces.entrySet())
                    quads.add(FACE_BAKERY.bakeQuad(element.from, element.to, (BlockElementFace) entry.getValue(), sprite, (Direction) entry.getKey(), SimpleModelState.IDENTITY, element.rotation, element.shade, new ResourceLocation("avaritia", "dynamic")));
        }
        return quads;
    }
}