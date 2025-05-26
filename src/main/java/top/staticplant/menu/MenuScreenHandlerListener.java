package top.staticplant.menu;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;

import java.util.Optional;

public class MenuScreenHandlerListener implements ScreenHandlerListener {
    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null)
            return;
        NbtCompound nbtCompound = nbtComponent.copyNbt();
        Optional<Boolean> op = nbtCompound.getBoolean("top.staticplant.menu");
        if (op.isEmpty())
            return;
        if (op.get())
            handler.getSlot(slotId).setStack(ItemStack.EMPTY); // 从玩家背包删除该物品
    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {}
}
