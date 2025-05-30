package top.staticplant.menu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GlobalDataBase {
    public static MinecraftServer server;
    public static final Logger LOGGER = LoggerFactory.getLogger("menu");
    public static final Type JSON_OBJECT_TYPE = new TypeToken<Map<String, Object>>() {} .getType();
    public static final MenuCmd MENU_COMMAND = new MenuCmd();
    public static final MenuCmdSuggest MENU_COMMAND_SUGGEST = new MenuCmdSuggest();
    public static final MenuCRC MENU_COMMAND_REGISTRATION_CALLBACK = new MenuCRC();
    public static Map<String, MenuEntry> config = new HashMap<>();
    public static final ScreenHandlerType<?>[] SCREEN_HANDLER_TYPES = new ScreenHandlerType<?>[]{
            ScreenHandlerType.GENERIC_9X1,
            ScreenHandlerType.GENERIC_9X2,
            ScreenHandlerType.GENERIC_9X3,
            ScreenHandlerType.GENERIC_9X4,
            ScreenHandlerType.GENERIC_9X5,
            ScreenHandlerType.GENERIC_9X6
    };
    public static final MenuSize[] MENU_SIZES = new MenuSize[]{
            new MenuSize(9, 1),
            new MenuSize(9, 2),
            new MenuSize(9, 3),
            new MenuSize(9, 4),
            new MenuSize(9, 5),
            new MenuSize(9, 6)
    };

    private GlobalDataBase() {}

    public static String quote(String input) {
        Gson gson = new Gson();
        return gson.toJson(input);
    }

    public static String unquote(String input) {
        Gson gson = new Gson();
        try {
            String result = gson.fromJson(input, String.class);
            if (result == null)
                result = "";
            return result;
        } catch (JsonSyntaxException e) {
            return "";
        }
    }

    public static List<ItemStack> newMenu() {
        List<ItemStack> itemStacks = new ArrayList<>();
        // 准备菜单
        for (Map.Entry<String, MenuEntry> entry : GlobalDataBase.config.entrySet()) {
            String key = entry.getKey();
            MenuEntry value = entry.getValue();
            ItemConvertible item = Registries.ITEM.get(Identifier.tryParse(value.item));
            ItemStack itemStack = new ItemStack(item, 1);

            // 设置自定义名称和描述
            itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(key));
            List<Text> description = new ArrayList<>();
            String[] descriptionList = value.description.split("\n");
            for (String descriptionLine : descriptionList) {
                description.add(Text.literal(descriptionLine));
            }
            LoreComponent loreComponent = new LoreComponent(description);
            itemStack.set(DataComponentTypes.LORE, loreComponent);

            // 设置自定义数据
            NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            if (nbtComponent == null)
                nbtComponent = NbtComponent.of(new NbtCompound());
            NbtCompound nbtCompound = nbtComponent.copyNbt();
            nbtCompound.putBoolean("top.staticplant.menu", true);
            nbtComponent = NbtComponent.of(nbtCompound);
            itemStack.set(DataComponentTypes.CUSTOM_DATA, nbtComponent);

            itemStacks.add(itemStack);
        }
        return itemStacks;
    }
}
