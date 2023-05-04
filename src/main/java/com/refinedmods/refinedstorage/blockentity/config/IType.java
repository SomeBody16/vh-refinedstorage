package com.refinedmods.refinedstorage.blockentity.config;

import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationClientListener;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;

public interface IType {
    int ITEMS = 0;
    int FLUIDS = 1;
    int VAULT_ALTAR = 2;

    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<Integer, T> createParameter(@Nullable BlockEntitySynchronizationClientListener<Integer> clientListener) {
        return new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, ITEMS, t -> ((IType) t.getNode()).getType(), (t, v) -> {
            if (v == IType.ITEMS || v == IType.FLUIDS || v == IType.VAULT_ALTAR) {
                ((IType) t.getNode()).setType(v);
            }
        }, clientListener);
    }

    static <T extends BlockEntity & INetworkNodeProxy<?>> BlockEntitySynchronizationParameter<Integer, T> createParameter() {
        return createParameter(null);
    }

    int getType();

    void setType(int type);

    IItemHandlerModifiable getItemFilters();

    FluidInventory getFluidFilters();
}
