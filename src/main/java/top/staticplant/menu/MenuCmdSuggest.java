package top.staticplant.menu;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MenuCmdSuggest implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        Set<String> keys = GlobalDataBase.config.keySet();
        for (String key : keys) {
            boolean isQuoted = false;
            for (char c : key.toCharArray())
                if (c < 33 || c > 126) {
                    isQuoted = true;
                    break;
                }
            if (!isQuoted)
                builder.suggest(key);
            else
                builder.suggest(GlobalDataBase.quote(key));
        }
        return builder.buildFuture();
    }
}
