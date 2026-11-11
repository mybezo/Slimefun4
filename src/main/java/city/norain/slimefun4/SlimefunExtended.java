package city.norain.slimefun4;

import city.norain.slimefun4.compatibillty.VersionedEvent;
import city.norain.slimefun4.listener.SlimefunMigrateListener;
import city.norain.slimefun4.utils.EnvUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import io.github.bakedlibs.dough.versions.MinecraftVersion;
import io.github.bakedlibs.dough.versions.UnknownServerVersionException;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.guizhanss.guizhanlib.minecraft.utils.MinecraftVersionUtil;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.Server;

public final class SlimefunExtended {
    private static SlimefunMigrateListener migrateListener = new SlimefunMigrateListener();

    @Getter
    private static boolean databaseDebugMode = false;

    @Deprecated(since = "2026.1.1", forRemoval = true)
    private static MinecraftVersion minecraftVersion;

    @Deprecated(since = "2026.1.1", forRemoval = true)
    public static MinecraftVersion getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Returns the Minecraft version of the current server, including the major, minor and patch version.
     * For example: 26.1.2 returns (26, 1, 2), and 26.1 returns (26, 1, 0).
     *
     * Returns null if the server version cannot be recognized.
     *
     * @since 2026.1
     * @param server
     * @return
     */
    public static ServerVersion getServerVerDetail(Server server) {
        String mcVersion = server.getMinecraftVersion();

        if (mcVersion.isBlank()) {
            return null;
        }

        // Extract the numeric part of the version
        String[] versionPart = mcVersion.split("\\.");

        // Possibly a snapshot or pre-release?
        if (versionPart.length < 2) {
            return null;
        }

        try {
            int majorVersion = Integer.parseInt(versionPart[0]);

            // Since 26.1, Minecraft uses the year as the major version number
            if (majorVersion != 1 && majorVersion < 26) {
                return null;
            }

            int minorVersion = Integer.parseInt(versionPart[1]);
            int patchVersion = versionPart.length > 2 ? Integer.parseInt(versionPart[2]) : 0;
            return new ServerVersion(majorVersion, minorVersion, patchVersion);
        } catch (NumberFormatException e) {
            server.getLogger().log(Level.WARNING, "Unable to parse the current server version: " + mcVersion, e);
            return null;
        }
    }

    /**
     * @since 2026.1
     * @param major the major version number (e.g., 26 for Minecraft 26.1)
     * @param minor
     * @return
     */
    public static boolean isAtLeast(int major, int minor) {
        return MinecraftVersionUtil.isAtLeast(major, minor);
    }

    /**
     * @since 2026.1
     * @param major the major version number (e.g., 26 for Minecraft 26.1)
     * @param minor
     * @return
     */
    public static boolean isAtLeast(int major, int minor, int patch) {
        return MinecraftVersionUtil.isAtLeast(major, minor, patch);
    }

    private static void checkDebug() {
        if ("true".equals(System.getProperty("slimefun.database.debug"))) {
            databaseDebugMode = true;

            Slimefun.getSQLProfiler().start();
            Slimefun.logger().log(Level.INFO, "Database debug mode enabled");
        } else {
            Configurator.setLevel(HikariConfig.class.getName(), org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel(HikariDataSource.class.getName(), org.apache.logging.log4j.Level.OFF);
            Configurator.setLevel(HikariPool.class.getName(), org.apache.logging.log4j.Level.OFF);
        }
    }

    public static boolean checkEnvironment(@Nonnull Slimefun sf) {
        try {
            minecraftVersion = MinecraftVersion.of(sf.getServer());
        } catch (UnknownServerVersionException ignored) {
            // sf.getLogger().log(Level.WARNING, "Unable to recognize the server version you are using :(");
            // return false;
        }

        if (EnvironmentChecker.checkHybridServer()) {
            sf.getLogger().log(Level.WARNING, "#######################################################");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "Hybrid server detected, Slimefun will be disabled!");
            sf.getLogger().log(Level.WARNING, "Hybrid servers have been reported to cause issues by multiple users,");
            sf.getLogger().log(Level.WARNING, "bypassing this check means no support will be provided.");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "#######################################################");
            return false;
        }

        if (Slimefun.getConfigManager().isBypassEnvironmentCheck()) {
            sf.getLogger().log(Level.WARNING, "#######################################################");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "You have disabled the environment compatibility check!");
            sf.getLogger().log(Level.WARNING, "No support will be provided if the compatibility check fails.");
            sf.getLogger().log(Level.WARNING, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            sf.getLogger().log(Level.WARNING, "");
            sf.getLogger().log(Level.WARNING, "#######################################################");
            return true;
        } else {
            return !EnvironmentChecker.checkIncompatiblePlugins(sf.getLogger());
        }
    }

    public static void init(@Nonnull Slimefun sf) {
        EnvironmentChecker.scheduleSlimeGlueCheck(sf);

        EnvUtil.init();

        checkDebug();

        VaultIntegration.register(sf);

        migrateListener.register(sf);

        VersionedEvent.init();
    }

    public static void shutdown() {
        migrateListener = null;

        VaultIntegration.cleanup();

        databaseDebugMode = false;
    }
}
