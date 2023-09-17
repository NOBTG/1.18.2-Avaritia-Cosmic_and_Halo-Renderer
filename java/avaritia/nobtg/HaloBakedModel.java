package avaritia.nobtg;

import codechicken.lib.colour.ColourARGB;
import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.buffer.AlphaOverrideVertexConsumer;
import codechicken.lib.render.item.IItemRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemStack;

public class HaloBakedModel
        extends WrappedItemModel
        implements IItemRenderer {
    private static final Random RANDOM = new Random();
    private final BakedQuad haloQuad;
    private final boolean pulse;

    public HaloBakedModel(BakedModel wrapped, TextureAtlasSprite sprite, int color, int size, boolean pulse) {
        super(wrapped);
        this.haloQuad = HaloBakedModel.generateHaloQuad(sprite, size, color);
        this.pulse = pulse;
    }

    public void renderItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack pStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        if (transformType == ItemTransforms.TransformType.GUI) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderQuadList(pStack, source.getBuffer(ItemBlockRenderTypes.getRenderType((ItemStack)stack, (boolean)true)), List.of(this.haloQuad), ItemStack.EMPTY, packedLight, packedOverlay);
            if (this.pulse) {
                pStack.pushPose();
                double scale = RANDOM.nextDouble() * 0.15 + 0.95;
                double trans = (1.0 - scale) / 2.0;
                pStack.translate(trans, trans, 0.0);
                pStack.scale((float)scale, (float)scale, 1.0001f);
                this.renderWrapped(stack, pStack, source, packedLight, packedOverlay, true, e -> new AlphaOverrideVertexConsumer((VertexConsumer)e, 0.6f));
                pStack.popPose();
            }
        }
        this.renderWrapped(stack, pStack, source, packedLight, packedOverlay, true);
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

    private static BakedQuad generateHaloQuad(TextureAtlasSprite sprite, int size, int color) {
        float[] colors = new ColourARGB(color).getRGBA();
        double spread = (double)size / 16.0;
        double min = 0.0 - spread;
        double max = 1.0 + spread;
        float minU = sprite.getU0();
        float maxU = sprite.getU1();
        float minV = sprite.getV0();
        float maxV = sprite.getV1();
        Quad quad = new Quad();
        quad.reset(CachedFormat.BLOCK);
        quad.setTexture(sprite);
        HaloBakedModel.putVertex(quad.vertices[0], max, max, 0.0, maxU, minV);
        HaloBakedModel.putVertex(quad.vertices[1], min, max, 0.0, minU, minV);
        HaloBakedModel.putVertex(quad.vertices[2], min, min, 0.0, minU, maxV);
        HaloBakedModel.putVertex(quad.vertices[3], max, min, 0.0, maxU, maxV);
        for (int i = 0; i < 4; ++i) {
            System.arraycopy(colors, 0, quad.vertices[i].color, 0, 4);
        }
        quad.calculateOrientation(true);
        return quad.bake();
    }

    private static void putVertex(Quad.Vertex vertex, double x, double y, double z, double u, double v) {
        vertex.vec[0] = (float)x;
        vertex.vec[1] = (float)y;
        vertex.vec[2] = (float)z;
        vertex.uv[0] = (float)u;
        vertex.uv[1] = (float)v;
    }
}