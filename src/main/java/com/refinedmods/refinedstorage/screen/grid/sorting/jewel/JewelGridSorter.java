package com.refinedmods.refinedstorage.screen.grid.sorting.jewel;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.sorting.IGridSorter;
import com.refinedmods.refinedstorage.screen.grid.sorting.SortingDirection;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;
import com.refinedmods.refinedstorage.the_vault.Jewel;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;

abstract class JewelGridSorter implements IGridSorter {

    protected final int type;

    protected JewelGridSorter(int type) {
        this.type = type;
    }

    protected abstract int compareJewels(Jewel left, Jewel right, SortingDirection direction);

    @Override
    public boolean isApplicable(IGrid grid) {
        return grid.getSortingType() == this.type;
    }

    @Override
    public int compare(IGridStack left, IGridStack right, SortingDirection direction) {
        if (left.getIngredient() instanceof ItemStack leftStack
                && leftStack.is(ModItems.JEWEL)
                && right.getIngredient() instanceof ItemStack rightStack
                && rightStack.is(ModItems.JEWEL)) {
            var leftJewel = new Jewel(leftStack);
            var rightJewel = new Jewel(rightStack);
            return compareJewels(leftJewel, rightJewel, direction);
        }

        return 0;
    }

    protected int compareNumber(float left, float right, SortingDirection direction) {
        if (left == right) {
            return 0;
        }

        if (direction == SortingDirection.ASCENDING) {
            return (left > right) ? 1 : -1;
        } else {
            return (right > left) ? 1 : -1;
        }
    }
}
