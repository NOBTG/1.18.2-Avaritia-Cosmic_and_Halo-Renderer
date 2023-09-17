package avaritia.nobtg;

import codechicken.lib.texture.SpriteRegistryHelper;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class AvaritiaTextures {
    private static final CrashLock LOCK = new CrashLock("Already Initialized");
    private static final SpriteRegistryHelper SPRITE_HELPER = new SpriteRegistryHelper();
    public static TextureAtlasSprite[] COSMIC_SPRITES = new TextureAtlasSprite[10];
    public static TextureAtlasSprite COSMIC_0;
    public static TextureAtlasSprite COSMIC_1;
    public static TextureAtlasSprite COSMIC_2;
    public static TextureAtlasSprite COSMIC_3;
    public static TextureAtlasSprite COSMIC_4;
    public static TextureAtlasSprite COSMIC_5;
    public static TextureAtlasSprite COSMIC_6;
    public static TextureAtlasSprite COSMIC_7;
    public static TextureAtlasSprite COSMIC_8;
    public static TextureAtlasSprite COSMIC_9;

    public static void init() {
        LOCK.lock();
        SPRITE_HELPER.addIIconRegister(registrar -> {
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_0"), e -> AvaritiaTextures.COSMIC_SPRITES[0] = COSMIC_0 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_1"), e -> AvaritiaTextures.COSMIC_SPRITES[1] = COSMIC_1 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_2"), e -> AvaritiaTextures.COSMIC_SPRITES[2] = COSMIC_2 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_3"), e -> AvaritiaTextures.COSMIC_SPRITES[3] = COSMIC_3 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_4"), e -> AvaritiaTextures.COSMIC_SPRITES[4] = COSMIC_4 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_5"), e -> AvaritiaTextures.COSMIC_SPRITES[5] = COSMIC_5 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_6"), e -> AvaritiaTextures.COSMIC_SPRITES[6] = COSMIC_6 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_7"), e -> AvaritiaTextures.COSMIC_SPRITES[7] = COSMIC_7 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_8"), e -> AvaritiaTextures.COSMIC_SPRITES[8] = COSMIC_8 = e);
            registrar.registerSprite(AvaritiaTextures.shader("cosmic_9"), e -> AvaritiaTextures.COSMIC_SPRITES[9] = COSMIC_9 = e);
        });
    }

    private static ResourceLocation shader(String path) {
        return new ResourceLocation("avaritia", "shader/" + path);
    }
}