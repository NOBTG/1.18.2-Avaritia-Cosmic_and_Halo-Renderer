package avaritia.nobtg;

import codechicken.lib.datagen.ItemModelProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class HaloCustomLoaderBuilder
        extends ItemModelProvider.CustomLoaderBuilder {
    @Nullable
    private Pair<String, ResourceLocation> haloTexture;
    private int haloColor = -16777216;
    private int haloSize = 0;
    private IntList layerColors = new IntArrayList();
    private boolean pulse;

    public HaloCustomLoaderBuilder(ItemModelProvider.SimpleItemModelBuilder parent) {
        this(new ResourceLocation("avaritia", "halo"), parent);
    }

    private HaloCustomLoaderBuilder(ResourceLocation loaderId, ItemModelProvider.SimpleItemModelBuilder parent) {
        super(loaderId, parent);
    }

    public HaloCustomLoaderBuilder addLayerColor(int color) {
        this.layerColors.add(color);
        return this;
    }

    public HaloCustomLoaderBuilder haloTexture(String key, ResourceLocation loc) {
        this.haloTexture = Pair.of(key, loc);
        return this;
    }

    public HaloCustomLoaderBuilder haloColor(int color) {
        this.haloColor = color;
        return this;
    }

    public HaloCustomLoaderBuilder haloSize(int size) {
        this.haloSize = size;
        return this;
    }

    public HaloCustomLoaderBuilder pulse() {
        this.pulse = true;
        return this;
    }

    protected void build(ItemModelBuilder builder) {
        assert (this.haloTexture != null);
        builder.texture(this.haloTexture.getLeft(), this.haloTexture.getRight());
    }

    @Override
    protected JsonObject toJson(JsonObject json) {
        assert (this.haloTexture != null);
        super.toJson(json);
        JsonObject halo = new JsonObject();
        halo.addProperty("texture", "#" + this.haloTexture.getKey());
        halo.addProperty("color", this.haloColor);
        halo.addProperty("size", this.haloSize);
        halo.addProperty("pulse", this.pulse);
        json.add("halo", halo);
        if (!this.layerColors.isEmpty()) {
            JsonArray colorsArray = new JsonArray(this.layerColors.size());
            IntIterator itr = this.layerColors.intIterator();
            while (itr.hasNext()) {
                colorsArray.add(itr.nextInt());
            }
            json.add("layerColors", colorsArray);
        }
        return json;
    }
}