package fi.septicuss.cookies.manager;

import fi.septicuss.cookies.CookiePlugin;
import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.ui.CookieUi;
import fi.septicuss.cookies.ui.Ui;
import fi.septicuss.cookies.utils.CookieItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CookieBlockManager {

    private static final ItemStack COOKIE_BLOCK_ITEM;

    static {
        final ItemStack item = new ItemStack(Material.RAW_GOLD_BLOCK);
        final ItemMeta meta = item.getItemMeta();
        meta.setItemName(ChatColor.GOLD + "Cookie Block");
        item.setItemMeta(meta);
        COOKIE_BLOCK_ITEM = item;
    }

    private final CookiePlugin plugin;

    public CookieBlockManager(CookiePlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getCookieBlockItem(UUID cookieUuid) {
        final CookieData cookieData = plugin.getCookieDataManager().get(cookieUuid);
        if (cookieData == null) return getCookieBlockItem();

        final ItemStack cookieItem = COOKIE_BLOCK_ITEM.clone();
        final ItemMeta meta = cookieItem.getItemMeta();
        final List<String> lore = List.of(
                ChatColor.WHITE + "Cookies: " + ChatColor.GRAY + cookieData.getCookies(),
                ChatColor.WHITE + "Cookies per second: " + ChatColor.GRAY + cookieData.getCookiesPerSecond() + "/s"
        );
        meta.setLore(lore);
        cookieItem.setItemMeta(meta);
        return CookieItemUtils.setCookieId(cookieItem, cookieUuid);
    }

    public void openCookieUi(Player player, UUID cookieUuid) {
        final UiManager uiManager = plugin.getUiManager();

        final Set<CookieUi> existingUis = this.findCookieUis(cookieUuid);
        if (!existingUis.isEmpty()) {
            for (CookieUi ui : existingUis) {
                ui.open(player);
                return;
            }
        }

        final Ui ui = new CookieUi(plugin.getCookieDataManager(), cookieUuid);
        uiManager.trackUi(ui);
        ui.open(player);
    }

    private Set<CookieUi> findCookieUis(UUID cookieUuid) {
        final Set<CookieUi> resultSet = new HashSet<>();
        final UiManager uiManager = plugin.getUiManager();
        for (Ui ui : uiManager.getTrackedUis()) {
            if (ui instanceof CookieUi cookieUi) {
                if (cookieUi.getUuid().equals(cookieUuid)) {
                    resultSet.add(cookieUi);
                }
            }
        }
        return Collections.unmodifiableSet(resultSet);
    }

    public void closeUi(UUID cookieUuid) {
        this.findCookieUis(cookieUuid).forEach(cookieUi -> this.plugin.getUiManager().closeAndDispose(cookieUi));
    }

    public ItemStack getCookieBlockItem() {
        return CookieItemUtils.setEmptyItemSignature(COOKIE_BLOCK_ITEM.clone());
    }


}
