package com.refinedmods.refinedstorage.screen.grid.sorting.jewel;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.sorting.SortingDirection;
import com.refinedmods.refinedstorage.the_vault.Jewel;
import com.refinedmods.refinedstorage.the_vault.JewelAttribute;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class JewelAttributeGridSorter extends JewelGridSorter {
    protected final IGrid grid;

    public JewelAttributeGridSorter(@Nullable IGrid grid) {
        super(IGrid.SORTING_TYPE_JEWEL_ATTRIBUTE);
        this.grid = grid;
    }

    @Override
    protected int compareJewels(Jewel left, Jewel right, SortingDirection direction) {
        if (grid == null) {
            return 0;
        }
        var attrId = grid.getJewelAttributeSorting();

        if (Objects.equals(attrId, "the_vault:jewel_size")) {
            return compareNumber(
                    left.getSize(),
                    right.getSize(),
                    direction
            );
        }

        if (JewelAttribute.of(attrId).valueType == Boolean.class) {
            direction = SortingDirection.opposite(direction);
        }

        return compareNumber(
                getValue(right, attrId),
                getValue(left, attrId),
                direction
        );
    }

    protected float getValue(Jewel jewel, String attrId) {
        var value = jewel.getAttr(attrId);
        float size = jewel.getSize();

        if (value instanceof Boolean) {
            return size;
        }
        if (value instanceof Integer intValue) {
            return intValue / size;
        }
        if (value instanceof Float floatValue) {
            return floatValue / size;
        }
        if (value instanceof Double doubleValue) {
            return doubleValue.floatValue() / size;
        }

        return 0;
    }
}
