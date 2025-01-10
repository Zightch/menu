package top.staticplant.menu;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class MenuCRC implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager
                        .literal("menu")
                        .executes(GlobalDataBase.MENU_COMMAND) // 无参数的时候执行
                        .then(
                                CommandManager
                                        .argument("option", StringArgumentType.string())
                                        .suggests(GlobalDataBase.MENU_COMMAND_SUGGEST)
                                        .executes(GlobalDataBase.MENU_COMMAND)
                        )
        ); // 注册命令
    }
}
