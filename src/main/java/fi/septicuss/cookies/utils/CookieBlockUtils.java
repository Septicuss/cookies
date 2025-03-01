package fi.septicuss.cookies.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import fi.septicuss.cookies.CookiePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class CookieBlockUtils {

    private static final NamespacedKey COOKIE_ID_KEY = new NamespacedKey(CookiePlugin.getInstance(), "id");

    public static void setBlockId(Block block, UUID uuid) {
        final CustomBlockData data = new CustomBlockData(block, CookiePlugin.getInstance());
        data.set(COOKIE_ID_KEY, PersistentDataType.STRING, uuid.toString());
    }

    public static UUID getBlockId(Block block) {
        final CustomBlockData data = new CustomBlockData(block, CookiePlugin.getInstance());
        final String id = data.get(COOKIE_ID_KEY, PersistentDataType.STRING);
        if (id == null) return null;
        return UUID.fromString(id);
    }

    public static boolean isCookieBlock(Block block) {
        final CustomBlockData data = new CustomBlockData(block, CookiePlugin.getInstance());
        return data.has(COOKIE_ID_KEY, PersistentDataType.STRING);
    }

    public static void wipeCookieBlockData(Block block) {
        final CustomBlockData data = new CustomBlockData(block, CookiePlugin.getInstance());

    }

}
