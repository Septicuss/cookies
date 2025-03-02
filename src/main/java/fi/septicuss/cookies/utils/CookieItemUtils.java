package fi.septicuss.cookies.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CookieItemUtils {

    public static final NamespacedKey EMPTY_COOKIE_ITEM = new NamespacedKey("cookies", "empty");
    private static final NamespacedKey COOKIE_ID_KEY = new NamespacedKey("cookies", "id");
    private static final NamespacedKey COOKIE_EMPTY_KEY = new NamespacedKey("cookies", "e");

    public static ItemStack setCookieId(ItemStack item, UUID uuid) {
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(COOKIE_ID_KEY, PersistentDataType.STRING, uuid.toString());
        item.setItemMeta(meta);
        return item;
    }

    public static UUID getCookieId(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return null;
        }

        final ItemMeta meta = item.getItemMeta();
        final String id = meta.getPersistentDataContainer().get(COOKIE_ID_KEY, PersistentDataType.STRING);

        if (id == null || id.isEmpty()) {
            return null;
        }

        return UUID.fromString(id);

    }

    public static boolean hasCookieId(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }

        final ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(COOKIE_ID_KEY, PersistentDataType.STRING);
    }

    public static ItemStack setEmptyItemSignature(ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(COOKIE_EMPTY_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean hasEmptyItemSignature(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }

        final ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(COOKIE_EMPTY_KEY, PersistentDataType.BOOLEAN);
    }

}
