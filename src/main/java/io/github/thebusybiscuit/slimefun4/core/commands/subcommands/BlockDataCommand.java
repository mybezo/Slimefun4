package io.github.thebusybiscuit.slimefun4.core.commands.subcommands;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import javax.annotation.ParametersAreNonnullByDefault;
import net.guizhanss.slimefun4.utils.ChatUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This command can directly set Slimefun Block data.
 *
 * @author ybw0014
 */
class BlockDataCommand extends SubCommand {
    @ParametersAreNonnullByDefault
    BlockDataCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "blockdata", false);
    }

    @Override
    protected String getDescription() {
        return "commands.blockdata.description";
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
            return;
        }

        if (!sender.hasPermission("slimefun.command.blockdata")) {
            Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            return;
        }

        if (args.length < 3) {
            Slimefun.getLocalization()
                    .sendMessage(
                            sender,
                            "messages.usage",
                            true,
                            msg -> msg.replace("%usage%", "/sf blockdata get/set/remove <key> [value]"));
            return;
        }

        Block target = player.getTargetBlockExact(8, FluidCollisionMode.NEVER);
        var blockData = StorageCacheUtils.getDataContainer(target.getLocation());

        if (target == null || target.getType().isAir() || blockData == null) {
            ChatUtils.sendMessage(player, "&cYou need to look at a Slimefun block to run this command!");
            return;
        }

        String key = args[2];

        switch (args[1]) {
            case "get" -> {
                String value = blockData.getData(key);
                ChatUtils.sendMessage(
                        player,
                        "&aThis block has &b%key% &aset to: &e%value%",
                        msg -> msg.replace("%key%", key).replace("%value%", value == null ? "null" : value));
            }
            case "set" -> {
                if (args.length < 4) {
                    Slimefun.getLocalization()
                            .sendMessage(
                                    sender,
                                    "messages.usage",
                                    true,
                                    msg -> msg.replace("%usage%", "/sf blockdata set <key> <value>"));
                    return;
                }

                if (key.equalsIgnoreCase("id")) {
                    ChatUtils.sendMessage(player, "&cYou cannot modify the block ID!");
                    return;
                }

                String value = args[3];

                blockData.setData(key, value);
                ChatUtils.sendMessage(
                        player,
                        "&aSet &b%key% &afor this block to: &e%value%",
                        msg -> msg.replace("%key%", key).replace("%value%", value));
            }
            case "remove" -> {
                if (key.equalsIgnoreCase("id")) {
                    ChatUtils.sendMessage(player, "&cYou cannot modify the block ID!");
                    return;
                }

                blockData.removeData(key);
                ChatUtils.sendMessage(player, "&aRemoved &b%key% &afrom this block", msg -> msg.replace("%key%", key));
            }
            default -> {
                Slimefun.getLocalization()
                        .sendMessage(
                                sender,
                                "messages.usage",
                                true,
                                msg -> msg.replace("%usage%", "/sf blockdata get/set/remove <key> [value]"));
            }
        }
    }
}
