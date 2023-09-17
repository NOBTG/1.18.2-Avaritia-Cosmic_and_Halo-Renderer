package avaritia.nobtg;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class InfinitySwordItem extends SwordItem {
    public InfinitySwordItem(Item.Properties properties) {
        super(Tiers.NETHERITE, 3, -2.0f, properties);
    }
}