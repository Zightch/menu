package top.staticplant.menu;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MenuCmd implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity() instanceof PlayerEntity)) {
            context.getSource().sendFeedback(() -> Text.literal("该指令只能由玩家执行"), false);
            return Command.SINGLE_SUCCESS;
        }
        String option = "";
        try {
            option = StringArgumentType.getString(context, "option");
        } catch (Exception ignore) {}
        String unquoted = GlobalDataBase.unquote(option);
        if ("".equals(option) && unquoted.isEmpty()) {
            PlayerEntity player = (PlayerEntity) source.getEntity(); // 获取目标玩家
            openMenu(player); // 打开菜单
            return Command.SINGLE_SUCCESS;
        }
        if (!unquoted.isEmpty()) option = unquoted;
        if (!GlobalDataBase.config.containsKey(option)) {
            context.getSource().sendError(Text.literal("没有找到选项: %s".formatted(option)));
            return Command.SINGLE_SUCCESS;
        }
        // TODO 直接执行
        return Command.SINGLE_SUCCESS;
    }

    private void openMenu(PlayerEntity player) {
        SimpleInventory inventory = new SimpleInventory(27); // 创建一个3行9列的虚拟箱子
        // 在这里填充虚拟箱子的内容
        inventory.setStack(0, new ItemStack(Registries.ITEM.get(Identifier.tryParse("minecraft:stone")), 1));

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (syncId, inv, p) ->
                                new MenuChestScreenHandler(syncId, inv, inventory),
                        Text.literal("菜单")
                )
        );
    }
}
