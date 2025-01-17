package com.refinedmods.refinedstorage.blockentity.grid;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.the_vault.JewelAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GridBlockEntity extends NetworkNodeBlockEntity<GridNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Boolean, GridBlockEntity> EXACT_PATTERN = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.BOOLEAN, true, t -> t.getNode().isExactPattern(), (t, v) -> {
        t.getNode().setExactPattern(v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.updateExactPattern(p)));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> PROCESSING_TYPE = IType.createParameter((initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> VIEW_TYPE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getViewType(), (t, v) -> {
        if (IGrid.isValidViewType(v)) {
            t.getNode().setViewType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final BlockEntitySynchronizationParameter<List<Set<ResourceLocation>>, GridBlockEntity> ALLOWED_ITEM_TAGS = new BlockEntitySynchronizationParameter<>(RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedItemTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedItemTags(v));
    public static final BlockEntitySynchronizationParameter<List<Set<ResourceLocation>>, GridBlockEntity> ALLOWED_FLUID_TAGS = new BlockEntitySynchronizationParameter<>(RSSerializers.LIST_OF_SET_SERIALIZER, new ArrayList<>(), t -> t.getNode().getAllowedTagList().getAllowedFluidTags(), (t, v) -> t.getNode().getAllowedTagList().setAllowedFluidTags(v));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SORTING_DIRECTION = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSortingDirection(), (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.getNode().setSortingDirection(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    private final GridType type;
    private final LazyOptional<IItemHandler> diskCapability = LazyOptional.of(() -> getNode().getPatterns());
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SORTING_TYPE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSortingType(), (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.getNode().setSortingType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));

    public static final BlockEntitySynchronizationParameter<String, GridBlockEntity> JEWEL_ATTRIBUTE =
            new BlockEntitySynchronizationParameter<>(
                    EntityDataSerializers.STRING,
                    JewelAttribute.ATTRIBUTES.get(0).id,
                    t -> t.getNode().getJewelAttributeSorting(),
                    (t, v) -> {
                        if (JewelAttribute.ATTRIBUTES.stream().anyMatch(attr -> Objects.equals(attr.id, v))) {
                            t.getNode().setJewelAttributeSorting(v);
                            t.getNode().markDirty();
                        }
                    },
                    (initial, p) -> trySortGrid(initial)
            );

    public GridBlockEntity(GridType type, BlockPos pos, BlockState state) {
        super(getType(type), pos, state);

        this.type = type;

        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(JEWEL_ATTRIBUTE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(TAB_SELECTED);
        dataManager.addWatchedParameter(TAB_PAGE);
        dataManager.addWatchedParameter(EXACT_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_PATTERN);
        dataManager.addWatchedParameter(PROCESSING_TYPE);
        dataManager.addParameter(ALLOWED_ITEM_TAGS);
        dataManager.addParameter(ALLOWED_FLUID_TAGS);
    }

    public static void trySortGrid(boolean initial) {
        if (!initial) {
            BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort());
        }
    }

    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SEARCH_BOX_MODE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getSearchField().setMode(p)));

    public static BlockEntityType<GridBlockEntity> getType(GridType type) {
        switch (type) {
            case NORMAL:
                return RSBlockEntities.GRID;
            case JEWEL:
                return RSBlockEntities.JEWEL_GRID;
            case CRAFTING:
                return RSBlockEntities.CRAFTING_GRID;
            case PATTERN:
                return RSBlockEntities.PATTERN_GRID;
            case FLUID:
                return RSBlockEntities.FLUID_GRID;
            default:
                throw new IllegalArgumentException("Unknown grid type " + type);
        }
    }

    @Override
    @Nonnull
    public GridNetworkNode createNode(Level level, BlockPos pos) {
        return new GridNetworkNode(level, pos, type);
    }

    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> SIZE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.resize(grid.getMinecraft(), grid.width, grid.height)));

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && type == GridType.PATTERN) {
            return diskCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> TAB_SELECTED = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabSelected(), (t, v) -> {
        t.getNode().setTabSelected(v == t.getNode().getTabSelected() ? -1 : v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, grid -> grid.getView().sort()));
    public static final BlockEntitySynchronizationParameter<Integer, GridBlockEntity> TAB_PAGE = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0 && v <= t.getNode().getTotalTabPages()) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });
    public static final BlockEntitySynchronizationParameter<Boolean, GridBlockEntity> PROCESSING_PATTERN = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.BOOLEAN, false, t -> t.getNode().isProcessingPattern(), (t, v) -> {
        t.getNode().setProcessingPattern(v);
        t.getNode().clearMatrix();
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(GridScreen.class, BaseScreen::init));


}
