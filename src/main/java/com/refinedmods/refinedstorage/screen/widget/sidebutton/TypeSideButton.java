package com.refinedmods.refinedstorage.screen.widget.sidebutton;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.blockentity.ExporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class TypeSideButton extends SideButton {
    private final BlockEntitySynchronizationParameter<Integer, ?> type;

    public TypeSideButton(BaseScreen<?> screen, BlockEntitySynchronizationParameter<Integer, ?> type) {
        super(screen);

        this.type = type;
    }

    @Override
    protected String getTooltip() {
        return I18n.get("sidebutton.refinedstorage.type") + "\n" + ChatFormatting.GRAY + I18n.get("sidebutton.refinedstorage.type." + type.getValue());
    }

    @Override
    protected void renderButtonIcon(PoseStack poseStack, int x, int y) {
        screen.blit(poseStack, x, y, 16 * type.getValue(), 128, 16, 16);
    }

    @Override
    public void onPress() {
        var currType = type.getValue();
        if (currType == IType.ITEMS) {
            currType = IType.FLUIDS;
        } else if (currType == IType.FLUIDS && type == ExporterBlockEntity.TYPE) {
            currType = IType.VAULT_ALTAR;
        } else {
            currType = IType.ITEMS;
        }

        BlockEntitySynchronizationManager.setParameter(type, currType);
    }
}
