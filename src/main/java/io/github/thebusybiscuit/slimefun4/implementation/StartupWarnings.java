package io.github.thebusybiscuit.slimefun4.implementation;

import io.github.thebusybiscuit.slimefun4.utils.NumberUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class stores some startup warnings we occasionally need to print.
 * If you setup your server the recommended way, you are never going to see
 * any of these messages.
 *
 * @author TheBusyBiscuit
 *
 */
final class StartupWarnings {

    private static final String BORDER = "****************************************************";
    private static final String PREFIX = "* ";

    private StartupWarnings() {}

    @ParametersAreNonnullByDefault
    static void discourageCSCoreLib(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "It looks like CS-CoreLib is installed.");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "CS-CoreLib has not been a required dependency since 2021/01/30.");
        logger.log(Level.SEVERE, PREFIX + "You need to uninstall CS-CoreLib for Slimefun to work properly.");
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidMinecraftVersion(Logger logger, String detectedVer, String slimefunVersion) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun failed to load!");
        logger.log(Level.SEVERE, PREFIX + "You are using an unsupported Minecraft version!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "You are using Minecraft {0}", detectedVer);
        logger.log(Level.SEVERE, PREFIX + "but Slimefun {0} only supports these versions:", slimefunVersion);
        logger.log(Level.SEVERE, PREFIX + "Minecraft {0}", String.join(" / ", Slimefun.getSupportedVersions()));
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void invalidServerSoftware(Logger logger) {
        logger.log(Level.SEVERE, BORDER);
        logger.log(Level.SEVERE, PREFIX + "Slimefun failed to load!");
        logger.log(Level.SEVERE, PREFIX + "CraftBukkit servers are no longer supported!");
        logger.log(Level.SEVERE, PREFIX);
        logger.log(Level.SEVERE, PREFIX + "You need to use Paper or a Paper fork");
        logger.log(Level.SEVERE, PREFIX + "(We recommend Paper)");
        logger.log(Level.SEVERE, BORDER);
    }

    @ParametersAreNonnullByDefault
    static void oldJavaVersion(Logger logger, int recommendedJavaVersion) {
        int javaVersion = NumberUtils.getJavaVersion();

        logger.log(Level.WARNING, BORDER);
        logger.log(Level.WARNING, PREFIX + "The Java version you are using (Java {0}) is outdated.", javaVersion);
        logger.log(Level.WARNING, PREFIX);
        logger.log(Level.WARNING, PREFIX + "Because newer Minecraft versions require Java {0},", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "we recommend updating to Java {0} as soon as possible.", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "To make use of features from newer Java versions,");
        logger.log(Level.WARNING, PREFIX + "Slimefun will also depend on Java {0} in the near future.", recommendedJavaVersion);
        logger.log(Level.WARNING, PREFIX + "Please update soon to avoid future compatibility issues!");
        logger.log(Level.WARNING, BORDER);
    }
}
