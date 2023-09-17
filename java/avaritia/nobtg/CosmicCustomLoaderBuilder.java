package avaritia.nobtg;

import codechicken.lib.datagen.ItemModelProvider;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

import javax.annotation.Nullable;

public class CosmicCustomLoaderBuilder extends ItemModelProvider.CustomLoaderBuilder {
    @Nullable
    private ResourceLocation maskTexture;

    public CosmicCustomLoaderBuilder(ItemModelProvider.SimpleItemModelBuilder parent) {
        super(CosmicModelLoader.ID, parent);
    }

    public CosmicCustomLoaderBuilder maskTexture(ResourceLocation loc) {
        this.maskTexture = loc;
        return this;
    }

    protected void build(ItemModelBuilder builder) {
        if (this.maskTexture == null) {
            throw new IllegalStateException("Mask texture not set?");
        }
        builder.texture("mask", this.maskTexture);
    }

    protected JsonObject toJson(JsonObject json) {
        super.toJson(json);
        JsonObject cosmic = new JsonObject();
        cosmic.addProperty("mask", "#mask");
        json.add("cosmic", cosmic);
        return json;
    }
}
