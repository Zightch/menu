package top.staticplant.menu;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class GlobalDataBase {
    public static final Logger LOGGER = LoggerFactory.getLogger("menu");
    public static final Type JSON_OBJECT_TYPE = new TypeToken<Map<String, Object>>() {} .getType();
    public static final MenuCmd MENU_COMMAND = new MenuCmd();
    public static final MenuCmdSuggest MENU_COMMAND_SUGGEST = new MenuCmdSuggest();
    public static final MenuCRC MENU_COMMAND_REGISTRATION_CALLBACK = new MenuCRC();
    public static Map<String, String> config = new HashMap<>();
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
}
