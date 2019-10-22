package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCrafterManager;
import com.raoulvdberge.refinedstorage.container.slot.CrafterManagerSlot;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.raoulvdberge.refinedstorage.screen.IScreenInfoProvider;
import com.raoulvdberge.refinedstorage.screen.grid.filtering.GridFilterParser;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import com.raoulvdberge.refinedstorage.screen.grid.stack.ItemGridStack;
import com.raoulvdberge.refinedstorage.tile.TileCrafterManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class CrafterManagerContainer extends BaseContainer {
    public class CrafterManagerListener implements IContainerListener {
        private ServerPlayerEntity base;
        private boolean receivedContainerData;

        public CrafterManagerListener(ServerPlayerEntity base) {
            this.base = base;
        }

        public ServerPlayerEntity getPlayer() {
            return base;
        }

        @Override
        public void sendAllContents(Container container, NonNullList<ItemStack> items) {
            if (receivedContainerData) {
                base.sendAllContents(container, items);
            }
        }

        @Override
        public void sendSlotContents(Container container, int slotInd, ItemStack stack) {
            if (receivedContainerData) {
                base.sendSlotContents(container, slotInd, stack);
            }
        }

        public void setReceivedContainerData() {
            receivedContainerData = true;
        }

        @Override
        public void sendWindowProperty(Container container, int varToUpdate, int newValue) {
            base.sendWindowProperty(container, varToUpdate, newValue);
        }
    }

    private IScreenInfoProvider display;
    private NetworkNodeCrafterManager crafterManager;
    private Map<String, Integer> containerData;
    private Map<String, IItemHandlerModifiable> dummyInventories = new HashMap<>();
    private Map<String, Integer> headings = new HashMap<>();
    private int rows;

    @Override
    public void addListener(IContainerListener listener) {
        if (listener instanceof ServerPlayerEntity) {
            listener = new CrafterManagerListener((ServerPlayerEntity) listener);
        }

        super.addListener(listener);
    }

    // TODO add AT for listeners
    public List<IContainerListener> getListeners() {
        return new ArrayList<>();
    }

    public CrafterManagerContainer(TileCrafterManager crafterManager, PlayerEntity player, IScreenInfoProvider display, int windowId) {
        super(RSContainers.CRAFTER_MANAGER, crafterManager, player, windowId);

        this.display = display;
        this.crafterManager = crafterManager.getNode();

        if (!player.world.isRemote) {
            addPlayerInventory(8, display.getYPlayerInventory());

            if (crafterManager.getNode().getNetwork() != null) {
                for (Map.Entry<String, List<IItemHandlerModifiable>> entry : crafterManager.getNode().getNetwork().getCraftingManager().getNamedContainers().entrySet()) {
                    for (IItemHandlerModifiable handler : entry.getValue()) {
                        for (int i = 0; i < handler.getSlots(); ++i) {
                            addSlot(new CrafterManagerSlot(handler, i, 0, 0, true, display, this.crafterManager));
                        }
                    }
                }
            }
        }
    }

    public void initSlots(@Nullable Map<String, Integer> newContainerData) {
        if (newContainerData == null) { // We resized
            if (containerData == null) { // No container data received yet, do nothing..
                return;
            }
        } else {
            containerData = newContainerData; // Received container data

            dummyInventories.clear();
        }

        this.inventorySlots.clear();
        // TODO at for inventoryItemStacks this.inventoryItemStacks.clear();
        this.headings.clear();

        rows = 0;

        addPlayerInventory(8, display.getYPlayerInventory());

        int y = 19 + 18 - display.getCurrentOffset() * 18;
        int x = 8;

        List<Predicate<IGridStack>> filters = GridFilterParser.getFilters(null, display.getSearchFieldText(), Collections.emptyList());

        for (Map.Entry<String, Integer> category : containerData.entrySet()) {
            IItemHandlerModifiable dummy;

            if (newContainerData == null) { // We're only resizing, get the previous inventory...
                dummy = dummyInventories.get(category.getKey());
            } else {
                dummyInventories.put(category.getKey(), dummy = new BaseItemHandler(category.getValue()) {
                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Nonnull
                    @Override
                    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                        if (new PatternItemValidator(getPlayer().getEntityWorld()).test(stack)) {
                            return super.insertItem(slot, stack, simulate);
                        }

                        return stack;
                    }
                });
            }

            boolean foundItemsInCategory = false;

            int yHeading = y - 19;

            int slotFound = 0;
            for (int slot = 0; slot < category.getValue(); ++slot) {
                boolean visible = true;

                if (!display.getSearchFieldText().trim().isEmpty()) {
                    ItemStack stack = dummy.getStackInSlot(slot);

                    if (stack.isEmpty()) {
                        visible = false;
                    } else {
                        // TODO CraftingPattern pattern = ItemPattern.getPatternFromCache(crafterManager.getWorld(), stack);
                        CraftingPattern pattern = null;

                        visible = false;

                        for (ItemStack output : pattern.getOutputs()) {
                            ItemGridStack outputConverted = new ItemGridStack(output);

                            for (Predicate<IGridStack> filter : filters) {
                                if (filter.test(outputConverted)) {
                                    visible = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                addSlot(new CrafterManagerSlot(dummy, slot, x, y, visible, display, crafterManager));

                if (visible) {
                    foundItemsInCategory = true;

                    x += 18;

                    // Don't increase y level if we are on our last slot row (otherwise we do y += 18 * 3)
                    if ((slotFound + 1) % 9 == 0 && slot + 1 < category.getValue()) {
                        x = 8;
                        y += 18;
                        rows++;
                    }

                    slotFound++;
                }
            }

            if (foundItemsInCategory) {
                headings.put(category.getKey(), yHeading);

                x = 8;
                y += 18 * 2;
                rows += 2; // Heading and first row
            }
        }
    }

    public Map<String, Integer> getHeadings() {
        return headings;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 9 * 4) {
                if (!mergeItemStack(stack, 9 * 4, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, 0, 9 * 4, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}