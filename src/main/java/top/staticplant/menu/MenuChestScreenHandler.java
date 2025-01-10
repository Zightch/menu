package top.staticplant.menu;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
            if (GlobalDataBase.config.containsKey(name))
                command = GlobalDataBase.config.get(name).command;
        }
        super.onSlotClick(slotIndex, button, actionType, player);

        // 立刻关闭界面
        ((ServerPlayerEntity) player).closeHandledScreen();
        // 遍历玩家背包, 删除所有menu物品
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.isEmpty())
                continue;
            NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComponent == null)
                continue;
            NbtCompound nbtCompound = nbtComponent.copyNbt();
            if (nbtCompound.getBoolean("top.staticplant.menu"))
                player.getInventory().setStack(i, ItemStack.EMPTY);
        }
        if (!"".equals(command)) {
            MinecraftServer server = player.getServer();
            if (server == null) {
                return;
            }
            ServerWorld world = (ServerWorld)player.getWorld();
            CommandManager commandManager = server.getCommandManager();
            commandManager.executeWithPrefix(player.getCommandSource(world), command);
        }
    }
}
