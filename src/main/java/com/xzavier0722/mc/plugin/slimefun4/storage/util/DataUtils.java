package com.xzavier0722.mc.plugin.slimefun4.storage.util;

import city.norain.slimefun4.utils.StringUtil;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.StorageType;
import io.github.thebusybiscuit.slimefun4.core.debug.Debug;
import io.github.thebusybiscuit.slimefun4.core.debug.TestCase;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;

public class DataUtils {
    /**
     * Serializes an {@link ItemStack} into a Base64 string compatible with the legacy API.
     *
     * @param itemStack The {@link ItemStack} to serialize
     * @return The Base64-encoded item data
     * @deprecated Use {@link #serializeItemStackBytes(ItemStack)} instead, to avoid the extra space overhead of Base64
     */
    @Deprecated
    public static String serializeItemStack(ItemStack itemStack) {
        var itemData = serializeItemStackBytes(itemStack);
        return itemData.length == 0 ? "" : Base64.getEncoder().encodeToString(itemData);
    }

    public static byte[] serializeItemStackBytes(ItemStack itemStack) {
        Debug.log(TestCase.BACKPACK, "Serializing itemstack: " + itemStack);

        if (itemStack == null) {
            return new byte[0];
        }

        try {
            var itemData = ItemStackDataCodec.serialize(itemStack);

            if (!Slimefun.getConfigManager().isBypassItemLengthCheck()
                    && Slimefun.getDatabaseManager().getBlockDataStorageType() == StorageType.MYSQL
                    && itemData.length > 16_777_215) {

                throw new IllegalArgumentException("Item too large detected, please contact the developer of the corresponding item plugin: " + StringUtil.itemStackToString(itemStack)
                        + ", size = " + itemData.length);
            }

            return itemData;
        } catch (Throwable e) {
            Slimefun.logger().log(Level.SEVERE, "An error occurred while serializing the item, a null value will be stored", e);
            return new byte[0];
        }
    }

    /**
     * Deserializes an {@link ItemStack} from the database. Supports both the current native binary format and the legacy Bukkit object stream format.
     *
     * @param itemData The binary data to deserialize
     * @return The deserialized {@link ItemStack} object
     */
    @Nullable public static ItemStack deserializeItemStack(byte[] itemData) {
        if (itemData == null || itemData.length == 0) {
            return null;
        }

        Debug.log(TestCase.BACKPACK, "Deserializing itemstack: " + itemData.length + " bytes");

        try {
            var result = ItemStackDataCodec.deserialize(itemData);

            Debug.log(TestCase.BACKPACK, "Deserialized itemstack: " + result);

            if (result.getType().isAir()) {
                Slimefun.logger().log(Level.SEVERE, "Failed to deserialize an item from the database! The corresponding item cannot be displayed.");
            }

            return result;
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred while deserializing the item, the corresponding item cannot be displayed", ex);
        }
    }

    /**
     * Deserializes an item from a Base64 string compatible with the legacy API.
     *
     * @param base64Str The Base64-encoded item data
     * @return The deserialized item
     * @deprecated Use {@link #deserializeItemStack(byte[])} instead
     */
    @Deprecated
    @Nullable public static ItemStack deserializeItemStack(String base64Str) {
        if (base64Str == null || base64Str.isBlank()) {
            return null;
        }

        var encodedData = base64Str.getBytes(StandardCharsets.US_ASCII);
        var decodedData = Base64.getMimeDecoder().decode(base64Str);
        return deserializeItemStack(ItemStackDataCodec.isCurrent(decodedData) ? decodedData : encodedData);
    }

    public static boolean isLegacyItemStack(byte[] serializedItemStack) {
        return serializedItemStack != null
                && serializedItemStack.length > 0
                && ItemStackDataCodec.isLegacy(serializedItemStack);
    }

    public static String blockDataBase64(String text) {
        return Slimefun.getDatabaseManager().isBlockDataBase64Enabled() ? base64Encode(text) : text;
    }

    public static String blockDataDebase64(String base64Str) {
        return Slimefun.getDatabaseManager().isBlockDataBase64Enabled() ? base64Decode(base64Str) : base64Str;
    }

    public static String profileDataBase64(String text) {
        return Slimefun.getDatabaseManager().isProfileDataBase64Enabled() ? base64Encode(text) : text;
    }

    public static String profileDataDebase64(String base64Str) {
        return Slimefun.getDatabaseManager().isProfileDataBase64Enabled() ? base64Decode(base64Str) : base64Str;
    }

    public static String base64Encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String base64Decode(String base64Str) {
        return new String(Base64.getDecoder().decode(base64Str), StandardCharsets.UTF_8);
    }
}
