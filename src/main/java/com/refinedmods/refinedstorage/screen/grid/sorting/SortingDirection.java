package com.refinedmods.refinedstorage.screen.grid.sorting;

public enum SortingDirection {
    ASCENDING,
    DESCENDING;

    public static SortingDirection opposite(SortingDirection direction) {
        return direction == ASCENDING
                ? DESCENDING
                : ASCENDING;
    }
}
