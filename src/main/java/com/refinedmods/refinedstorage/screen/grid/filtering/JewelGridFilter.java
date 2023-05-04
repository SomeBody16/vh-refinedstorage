package com.refinedmods.refinedstorage.screen.grid.filtering;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.the_vault.Jewel;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class JewelGridFilter implements Predicate<IGridStack> {

    protected final IGrid grid;

    public JewelGridFilter(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public boolean test(IGridStack stack) {
        var isJewel = stack.getTags().contains("jewel");

        if (isJewel
                && grid.getSortingType() == IGrid.SORTING_TYPE_JEWEL_ATTRIBUTE
                && stack.getIngredient() instanceof ItemStack itemStack
                && itemStack.is(ModItems.JEWEL)
        ) {
            var jewel = new Jewel(itemStack);
            return jewel.attributes.containsKey(grid.getJewelAttributeSorting());
        }
        return isJewel;
    }
}
