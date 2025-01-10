package top.staticplant.menu;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.List;

public class MenuCmd implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof PlayerEntity player)) {
            context.getSource().sendFeedback(() -> Text.literal("该指令只能由玩家执行"), false);
            return Command.SINGLE_SUCCESS;
        }
        String option = "";
        try {
            option = StringArgumentType.getString(context, "option");
        } catch (Exception ignore) {}
        String unquoted = GlobalDataBase.unquote(option);
        if ("".equals(option) && unquoted.isEmpty()) {
            openMenu(player); // 打开菜单
            return Command.SINGLE_SUCCESS;
        }
        if (!unquoted.isEmpty()) option = unquoted;
        if (!GlobalDataBase.config.containsKey(option)) {
            context.getSource().sendError(Text.literal("没有找到选项: %s".formatted(option)));
            return Command.SINGLE_SUCCESS;
        }
        String command = GlobalDataBase.config.get(option).command;
        // 以玩家身份执行指令
        MinecraftServer server = player.getServer();
        if (server == null) {
            context.getSource().sendError(Text.literal("没有找到你的服务器"));
            return Command.SINGLE_SUCCESS;
        }
        ServerWorld world = (ServerWorld)player.getWorld();
        CommandManager commandManager = server.getCommandManager();
        commandManager.executeWithPrefix(player.getCommandSource(world), command);
        return Command.SINGLE_SUCCESS;
    }

    private void openMenu(PlayerEntity player) {
        List<ItemStack> itemStacks = GlobalDataBase.newMenu();
        SimpleInventory inventory = new SimpleInventory(itemStacks.size()); // 创建一个3行9列的虚拟箱子
        for (int i = 0; i < itemStacks.size(); i++)
            inventory.setStack(i, itemStacks.get(i));
        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (syncId, inv, p) ->
                                new MenuChestScreenHandler(syncId, inv, inventory),
                        Text.literal("菜单")
                )
        );
    }
}
