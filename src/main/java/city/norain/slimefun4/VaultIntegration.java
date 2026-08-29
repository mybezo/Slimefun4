package city.norain.slimefun4;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.Objects;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

/**
 * Vault integration class, used to handle currency-related operations.
 *
 * @author StarWishsama
 */
public class VaultIntegration {
    private static Economy econ = null;

    protected static void register(@Nonnull Slimefun plugin) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            var rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
                plugin.getLogger().log(Level.INFO, "Successfully hooked into Vault");
            } else {
                plugin.getLogger().log(Level.WARNING, "Could not hook into Vault. If you use CMI, enable the economy in its config.");
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "Vault is not installed, money-based research unlocking will be unavailable");
        }
    }

    protected static void cleanup() {
        econ = null;
    }

    public static double getPlayerBalance(OfflinePlayer p) {
        Objects.requireNonNull(p, "Player cannot be null!");
        Objects.requireNonNull(econ, "Vault instance cannot be null!");

        return econ.getBalance(p);
    }

    public static void withdrawPlayer(OfflinePlayer p, double withdraw) {
        Objects.requireNonNull(p, "Player cannot be null!");
        Objects.requireNonNull(econ, "Vault instance cannot be null!");

        econ.withdrawPlayer(p, withdraw);
    }

    public static boolean isEnabled() {
        return econ != null && Slimefun.getConfigManager().isUseMoneyUnlock();
    }
}
