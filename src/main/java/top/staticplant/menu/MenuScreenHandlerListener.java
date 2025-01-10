package top.staticplant.menu;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

public class MenuScreenHandlerListener implements ScreenHandlerListener {
    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null)
            return;
        NbtCompound nbtCompound = nbtComponent.copyNbt();
        if (nbtCompound.getBoolean("top.staticplant.menu"))
            handler.getSlot(slotId).setStack(ItemStack.EMPTY); // 从玩家背包删除该物品
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

    }
}
