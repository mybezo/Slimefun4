package com.xzavier0722.mc.plugin.slimefun4.storage.common;

/**
 * {@link DataType} is the type of a Slimefun database controller,
 * used to distinguish between different kinds of data storage.
 */
public enum DataType {
    /**
     * Player profile, usually contains research progress, backpacks and other player related data.
     */
    PLAYER_PROFILE,

    /**
     * Slimefun block data
     */
    BLOCK_STORAGE
}
