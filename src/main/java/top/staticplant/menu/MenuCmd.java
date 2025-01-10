package top.staticplant.menu;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
            // TODO 打开客户端箱子
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
}
