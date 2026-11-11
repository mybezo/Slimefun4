package com.xzavier0722.mc.plugin.slimefun4.storage.controller.attributes;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunUniversalData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;

/**
 * This attribute declares that a {@link SlimefunItem} uses {@link SlimefunUniversalData}
 * <p>
 * When this {@link SlimefunItem} is a machine, its material must support
 * a PDC storage container (used to identify the UUID).
 * Otherwise this item/machine cannot be bound to universal data.
 *
 * See here for materials that support PDC:
 * <a href="https://jd.papermc.io/paper/1.21/org/bukkit/block/TileState.html">Paper Doc</a>
 *
 * @author NoRainCity
 *
 * @see SlimefunUniversalData
 * @see SlimefunUniversalBlockData
 */
public interface UniversalBlock {}
