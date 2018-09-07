package com.raoulvdberge.refinedstorage.api.storage.externalstorage;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Provides an external storage handler to the external storage block.
 *
 * @param <T>
 */
public interface IExternalStorageProvider<T> {
    /**
     * @param tile      the tile
     * @param direction the direction of the external storage
     * @return true if the provider can provide, false otherwise
     */
    boolean canProvide(TileEntity tile, EnumFacing direction);

    /**
     * @param context   the context of the external storage
     * @param tile      the tile supplier
     * @param direction the direction of the external storage
     * @return the external storage handler
     */
    @Nonnull
    IStorageExternal<T> provide(IExternalStorageContext context, Supplier<TileEntity> tile, EnumFacing direction);

    /**
     * Returns the priority of this external storage provider.
     * The one with the highest priority is chosen.
     * Refined Storage's default handlers for {@link net.minecraftforge.items.IItemHandler} and {@link net.minecraftforge.fluids.capability.IFluidHandler} return 0.
     *
     * @return the priority
     */
    int getPriority();
}
