package fi.septicuss.cookies.ui;

import fi.septicuss.cookies.CookiePlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public abstract class Ui {

    private final Inventory inventory;
    private Component title;

    public Ui(int rows) {
        this.inventory = Bukkit.createInventory(null, rows * 9, "");
    }

    public void updateTitle(Component title) {
        this.title = title;
        this.inventory.getViewers().forEach(viewer -> {
            if (viewer instanceof Player player) {
                CookiePlugin.getInstance().getTitleHandler().setPlayerInventoryTitle(player, title);
            }
        });
    }

    public void open(Player player) {
        if (this.title != null) {
            CookiePlugin.getInstance().getTitleHandler().queueTitle(player, this.title);
        }

        this.loadItems(player);
        player.openInventory(this.inventory);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void tick() {}

    public boolean isTickable() { return false; }

    public void loadItems(Player player) {};

    public void clickEvent(InventoryClickEvent event) {};

    public void openEvent(InventoryOpenEvent event) {};

    public void closeEvent(InventoryCloseEvent event) {};




}
