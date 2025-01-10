package top.staticplant.menu;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

public class MenuChestScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public MenuChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId); // 使用现有的9x3箱子类型
        this.inventory = inventory;

        // 添加箱子的槽位
        int i, j;
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new MenuSlot(inventory, j + i * 9, 8 + j * 18, 18 + i * 18));
            }
        }

        // 添加玩家的背包槽位
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 添加玩家的快捷栏槽位
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasStack()) {
            ItemStack currentStack = slot.getStack();
            itemStack = currentStack.copy();

            if (index < this.inventory.size()) {
                if (!this.insertItem(currentStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(currentStack, 0, this.inventory.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (currentStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        Slot slot = this.getSlot(slotIndex);
        if (slot != null && (slot.hasStack() & slot instanceof MenuSlot)) {
            // 监听物品从箱子内被取出的操作
            ItemStack stack = slot.getStack();
            // 这里可以添加自定义逻辑，例如打印日志或触发事件
            GlobalDataBase.LOGGER.info("物品被取出: " + stack.getItem().getName().getString());
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
