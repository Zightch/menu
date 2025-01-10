package top.staticplant.menu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Menu implements ModInitializer {

    @Override
    public void onInitialize() {
        if (!config()) {
            return;
        }

        CommandRegistrationCallback.EVENT.register(GlobalDataBase.MENU_COMMAND_REGISTRATION_CALLBACK);
        GlobalDataBase.LOGGER.info("menu模组已启用");
    }

    boolean config() {
        FabricLoader loader = FabricLoader.getInstance();
        Path configDir = loader.getConfigDir();
        configDir = configDir.resolve("menu");
        if (Files.notExists(configDir)) { // 如果不存在配置文件目录
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                GlobalDataBase.LOGGER.error("创建配置文件目录失败: ", e);
                GlobalDataBase.LOGGER.info("发生无法修正的错误, menu模组将被禁用");
                return false;
            }
        }
        Path configFile = configDir.resolve("config.json");
        if (Files.notExists(configFile)) { // 如果不存在配置文件
            try {
                Files.createFile(configFile);
            } catch (IOException e) {
                GlobalDataBase.LOGGER.error("创建配置文件失败: ", e);
                GlobalDataBase.LOGGER.info("发生无法修正的错误, menu模组将被禁用");
                return false;
            }
            try {
                Files.writeString(configFile, "{}"); // 尝试初始化配置文件
            } catch (IOException e) {
                GlobalDataBase.LOGGER.error("初始化配置文件失败: ", e);
                GlobalDataBase.LOGGER.info("发生无法修正的错误, menu模组将被禁用");
                return false;
            }
        }
        String configData = "";
        try {
            configData = Files.readString(configFile); // 尝试读取配置文件
        } catch (IOException e) {
            GlobalDataBase.LOGGER.error("读取配置文件失败: ", e);
            GlobalDataBase.LOGGER.info("发生无法修正的错误, menu模组将被禁用");
            return false;
        }
        if (configData.isEmpty()) { // 如果配置文件为空
            try {
                Files.writeString(configFile, "{}"); // 尝试初始化配置文件
                configData = "{}";
            } catch (IOException e) {
                GlobalDataBase.LOGGER.error("初始化配置文件失败: ", e);
                GlobalDataBase.LOGGER.info("发生无法修正的错误, menu模组将被禁用");
                return false;
            }
        }

        // 解析
        Gson gson = new Gson();
        try {
            GlobalDataBase.config = gson.fromJson(configData, GlobalDataBase.JSON_OBJECT_TYPE);
        } catch (JsonSyntaxException e) {
            GlobalDataBase.LOGGER.error("配置文件解析失败: ", e);
            GlobalDataBase.config = new HashMap<>();
        }

        return true;
    }
}
