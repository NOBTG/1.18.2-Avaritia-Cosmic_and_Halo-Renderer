package avaritia.nobtg;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import codechicken.lib.util.ClientUtils;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Objects;

public class AvaritiaShaders {
    private static final CrashLock LOCK = new CrashLock("Already Initialized");
    private static final float[] COSMIC_UVS = new float[40];
    public static CCShaderInstance cosmicShader;
    public static CCUniform cosmicTime;
    public static CCUniform cosmicYaw;
    public static CCUniform cosmicPitch;
    public static CCUniform cosmicExternalScale;
    public static CCUniform cosmicOpacity;
    public static CCUniform cosmicUVs;

    public static void init() {
        LOCK.lock();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AvaritiaShaders::onRegisterShaders);
        MinecraftForge.EVENT_BUS.addListener(AvaritiaShaders::onRenderTick);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceManager(), new ResourceLocation("avaritia", "cosmic"), DefaultVertexFormat.BLOCK), e -> {
            cosmicShader = (CCShaderInstance) e;
            cosmicTime = Objects.requireNonNull(cosmicShader.getUniform("time"));
            cosmicYaw = Objects.requireNonNull(cosmicShader.getUniform("yaw"));
            cosmicPitch = Objects.requireNonNull(cosmicShader.getUniform("pitch"));
            cosmicExternalScale = Objects.requireNonNull(cosmicShader.getUniform("externalScale"));
            cosmicOpacity = Objects.requireNonNull(cosmicShader.getUniform("opacity"));
            cosmicUVs = Objects.requireNonNull(cosmicShader.getUniform("cosmicuvs"));
            cosmicShader.onApply(() -> cosmicTime.glUniform1f((float) ClientUtils.getRenderTime()));
        });
    }

    private static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        for (int i = 0; i < AvaritiaTextures.COSMIC_SPRITES.length; ++i) {
            TextureAtlasSprite sprite = AvaritiaTextures.COSMIC_SPRITES[i];
            AvaritiaShaders.COSMIC_UVS[i * 4] = sprite.getU0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 1] = sprite.getV0();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 2] = sprite.getU1();
            AvaritiaShaders.COSMIC_UVS[i * 4 + 3] = sprite.getV1();
        }
        if (cosmicUVs != null) cosmicUVs.glUniformF(false, COSMIC_UVS);
    }
}