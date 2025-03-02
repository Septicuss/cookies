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

import java.util.List;
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

    public ItemStack getCookieBlockItem() {
        return CookieItemUtils.setEmptyItemSignature(COOKIE_BLOCK_ITEM.clone());
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

        for (Ui ui : uiManager.getTrackedUis()) {
            if (ui instanceof CookieUi cookieUi) {
                if (cookieUi.getUuid().equals(cookieUuid)) {
                    cookieUi.open(player);
                    return;
                }
            }

        }

        final Ui ui = new CookieUi(plugin.getCookieDataManager(), cookieUuid);
        uiManager.trackUi(ui);
        ui.open(player);
    }

    public void closeUi(Player player, UUID cookieUuid) {
        final UiManager uiManager = plugin.getUiManager();

        for (Ui ui : uiManager.getTrackedUis()) {
            if (ui instanceof CookieUi cookieUi) {
                if (cookieUi.getUuid().equals(cookieUuid)) {

                }
            }
        }
    }




}
