package fi.septicuss.cookies.ui;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.data.cookie.CookieUpgrade;
import fi.septicuss.cookies.manager.CookieDataManager;
import fi.septicuss.cookies.utils.CookieFontUtils;
import fi.septicuss.cookies.utils.CookieItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CookieUi extends Ui {

    private static final Set<Integer> BUTTON_SLOTS = Set.of(21, 22, 23, 30, 31, 32, 39, 40, 41);
    private static final int AUTOSAVE_TIME_SECONDS = 60;

    private final UUID uuid;
    private final CookieData cookieData;
    private final CookieDataManager dataManager;

    private int timer;
    private boolean dirty = false;
    private double accumulatedCookies = 0;

    public CookieUi(CookieDataManager dataManager, UUID uuid) {
        super(6);

        this.uuid = uuid;
        this.cookieData = dataManager.get(uuid);
        this.dataManager = dataManager;
        this.updateTitle(CookieFontUtils.render(cookieData));
    }

    @Override
    public void loadItems() {
        final Inventory inventory = this.getInventory();

        final ItemStack first = upgradeItem(CookieUpgrade.BISCUIT);
        final ItemStack second = upgradeItem(CookieUpgrade.OVEN);
        final ItemStack third = upgradeItem(CookieUpgrade.FACTORY);
        final ItemStack stats = statsItems();

        inventory.setItem(18, first);
        inventory.setItem(19, first);
        inventory.setItem(27, second);
        inventory.setItem(28, second);
        inventory.setItem(36, third);
        inventory.setItem(37, third);

        inventory.setItem(8, stats);
        inventory.setItem(7, stats);
        inventory.setItem(6, stats);
        inventory.setItem(5, stats);
        inventory.setItem(4, stats);
    }

    @Override
    public void clickEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClick() == ClickType.DOUBLE_CLICK) {
            return;
        }

        final int slot = event.getSlot();

        // -- Upgrades --

        if (slot == 18 || slot == 19) {
            this.attemptPurchase(CookieUpgrade.BISCUIT);
        }

        if (slot == 27 || slot == 28) {
            this.attemptPurchase(CookieUpgrade.OVEN);
        }

        if (slot == 36 || slot == 37) {
            this.attemptPurchase(CookieUpgrade.FACTORY);
        }

        // -- Cookie Button --

        if (BUTTON_SLOTS.contains(slot)) {
            cookieData.setCookies(cookieData.getCookies() + 1);
            this.updateTitle(CookieFontUtils.render(cookieData));
            this.markDirty();
        }
    }

    @Override
    public void closeEvent(InventoryCloseEvent event) {
        final boolean lastViewer = getInventory().getViewers().size() <= 1;

        if (lastViewer) {
            this.save();
        }
    }

    @Override
    public void tick() {

        double cookiesPerTick = cookieData.getCookiesPerSecond() / 20D;
        accumulatedCookies += cookiesPerTick;
        int wholeCookies = (int) accumulatedCookies;
        if (wholeCookies > 0) {
            cookieData.setCookies(cookieData.getCookies() + wholeCookies);
            accumulatedCookies -= wholeCookies; // Keep the fraction for next tick
            this.updateTitle(CookieFontUtils.render(cookieData));
        }

        // Reload & re-send items
        if (dirty) {
            this.loadItems();

            // Triggers a container content update packet, which we cache later
            for (HumanEntity entity : this.getInventory().getViewers()) {
                if (entity instanceof Player player) {
                    player.updateInventory();
                    break;
                }
            }

            this.dirty = false;
        }

        // Update once in a while, so that items may get updated.
        if (timer % 20 == 0) {
            markDirty();
        }

        // Autosave
        final int autosaveTimeTicks = AUTOSAVE_TIME_SECONDS * 20;
        if (timer >= autosaveTimeTicks) {
            this.save();
            this.timer = 0;
            return;
        }

        this.timer++;
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    private void attemptPurchase(CookieUpgrade upgrade) {
        if (!canAfford(upgrade)) {
            return;
        }
        cookieData.setCookies(cookieData.getCookies() - this.getPrice(upgrade));
        cookieData.addUpgrade(upgrade);
        markDirty();
    }

    private ItemStack statsItems() {
        final long speed = cookieData.getCookiesPerSecond();
        final ItemStack item = new ItemStack(Material.GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();
        meta.setItemName(ChatColor.GOLD + "Cookies");
        meta.setLore(List.of(
                ChatColor.WHITE + "Speed: " + ChatColor.GRAY + speed + " /s"
        ));
        meta.setItemModel(CookieItemUtils.EMPTY_COOKIE_ITEM);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack upgradeItem(CookieUpgrade upgrade) {
        final int levels = cookieData.getUpgrades().getOrDefault(upgrade, 0);
        final int nextPrice = upgrade.getPrice(levels + 1);
        final String upgradeName = upgrade.name().toLowerCase().substring(0, 1).toUpperCase() + upgrade.name().toLowerCase().substring(1);;
        final ChatColor purchaseColor = (cookieData.getCookies() >= nextPrice ? ChatColor.GREEN : ChatColor.RED);

        final ItemStack item = new ItemStack(Material.GLASS_PANE);
        final ItemMeta meta = item.getItemMeta();

        final List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Price: " + purchaseColor + nextPrice + " cookies");
        if (levels > 0) lore.add(ChatColor.WHITE + "Owned: " + ChatColor.GRAY + levels);
        lore.add(ChatColor.WHITE + "Speed: " + ChatColor.GRAY + upgrade.getCookiesPerSecond() * levels + "/s");

        meta.setItemName(purchaseColor + upgradeName + " Upgrade");
        meta.setItemModel(CookieItemUtils.EMPTY_COOKIE_ITEM);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private int getPrice(CookieUpgrade upgrade) {
        final int nextLevel = cookieData.getUpgrades().getOrDefault(upgrade, 0) + 1;
        return upgrade.getPrice(nextLevel);
    }

    private boolean canAfford(CookieUpgrade upgrade) {
        return cookieData.getCookies() >= this.getPrice(upgrade);
    }

    public void markDirty() {
        this.dirty = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    private void save() {
        this.dataManager.save(this.uuid, this.cookieData);
    }
}
