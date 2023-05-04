package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.the_vault.JewelAttribute;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;

import java.util.Objects;

public class GridJewelAttributeSideButton extends SideButton {

    private final IGrid grid;

    public GridJewelAttributeSideButton(BaseScreen<GridContainerMenu> screen, IGrid grid) {
        super(screen);

        this.grid = grid;
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.grid.sorting_jewel")
                + "\n" + ChatFormatting.GRAY + I18n.get(grid.getJewelAttributeSorting());
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        // jewel color
        var color = JewelAttribute.of(grid.getJewelAttributeSorting()).intColor();
        GuiComponent.fill(poseStack, x + 4, y + 4, x + 4 + 8, y + 4 + 9, color);

        // jewel icon
        screen.blit(poseStack, x, y, 64, 48, 16, 16);
    }

    @Override
    public void onPress() {
        var currAttr = grid.getJewelAttributeSorting();

        var found = false;
        for (var attr : JewelAttribute.ATTRIBUTES) {
            if (found) {
                currAttr = attr.id;
                found = false;
                break;
            }
            if (Objects.equals(attr.id, currAttr)) {
                found = true;
            }
        }

        if (found) {
            currAttr = JewelAttribute.ATTRIBUTES.get(0).id;
        }

        grid.onSortingTypeChanged(IGrid.SORTING_TYPE_JEWEL_ATTRIBUTE);
        grid.onJewelAttributeChanged(currAttr);
    }
}
