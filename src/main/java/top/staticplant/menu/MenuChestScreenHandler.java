package top.staticplant.menu;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MenuChestScreenHandler extends ScreenHandler {

    public MenuChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId); // 使用现有的9x3箱子类型

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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            super.onSlotClick(slotIndex, button, actionType, player);
            return;
        }
        Slot slot = this.getSlot(slotIndex);
        String command = "";
        if (slot != null && (slot.hasStack() & slot instanceof MenuSlot)) {
            // 监听物品从箱子内被取出的操作
            ItemStack stack = slot.getStack();
            Text nameText = stack.get(DataComponentTypes.CUSTOM_NAME);
            String name = nameText == null ? stack.getItem().getName().getString() : nameText.getString();
            if (GlobalDataBase.config.containsKey(name)) {
                command = GlobalDataBase.config.get(name).command;
                ((ServerPlayerEntity) player).closeHandledScreen();
            }
        }
        super.onSlotClick(slotIndex, button, actionType, player);

        if (!"".equals(command)) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            CommandManager commandManager = GlobalDataBase.server.getCommandManager();
            ServerCommandSource source = serverPlayer.getCommandSource();
            commandManager.executeWithPrefix(source, command);
        }
    }
}
