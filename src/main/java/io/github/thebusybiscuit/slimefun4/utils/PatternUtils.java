package io.github.thebusybiscuit.slimefun4.utils;

import io.github.bakedlibs.dough.common.ChatColors;
import java.util.regex.Pattern;

/**
 * This class is created for common-use patterns used in things such as {@link String#split(String)}. <br>
 * Every time something like {@link String#split(String)} is called it will compile a {@link Pattern},
 * for code that is called often this can be a massive performance loss.
 * This class solves that, one compile but many uses!
 *
 * @author Walshy
 * @author TheBusyBiscuit
 *
 */
public final class PatternUtils {

    private PatternUtils() {}

    public static final Pattern SLASH_SEPARATOR = Pattern.compile(" / ");

    public static final Pattern YAML_ENTRY = Pattern.compile("[a-z0-9_-]+:.*");

    public static final Pattern MINECRAFT_NAMESPACEDKEY = Pattern.compile("minecraft:[a-z0-9/._-]+");

    public static final Pattern MINECRAFT_TAG = Pattern.compile("#minecraft:[a-z_]+");
    public static final Pattern SLIMEFUN_TAG = Pattern.compile("#slimefun:[a-z_]+");

    public static final Pattern USES_LEFT_LORE = Pattern.compile(ChatColors.color("&e[0-9]+ Uses? &7left")
            // Legacy (Chinese) lore, kept for backwards compatibility with existing items
            + "|" + ChatColors.color("&7\u8fd8\u53ef\u4ee5\u4f7f\u7528 &e[0-9]+ \u6b21"));
}
