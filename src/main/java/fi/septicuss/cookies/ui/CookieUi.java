package fi.septicuss.cookies.ui;

import fi.septicuss.cookies.data.cookie.CookieData;
import fi.septicuss.cookies.data.cookie.CookieUpgrade;
import fi.septicuss.cookies.manager.CookieDataManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CookieUi extends Ui {

    private static final int AUTOSAVE_TIME_SECONDS = 60;

    private final UUID uuid;
    private final CookieData cookieData;
    private final CookieDataManager dataManager;

    private int timer;
    private double accumulatedCookies = 0;


    public CookieUi(CookieDataManager dataManager, UUID uuid) {
        super(6);

        this.uuid = uuid;
        this.cookieData = dataManager.get(uuid);
        this.dataManager = dataManager;
        this.updateTitle(Component.text(cookieData.getCookies()));
    }

    @Override
    public void loadItems(Player player) {
        final Inventory inventory = this.getInventory();

        inventory.setItem(0, new ItemStack(Material.DIRT));
    }

    @Override
    public void clickEvent(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getSlot() == 0) {
            cookieData.addUpgrade(CookieUpgrade.PLANT);
        }

        if (event.getSlot() == 1) {
            cookieData.removeUpgrade(CookieUpgrade.PLANT);
        }

        cookieData.setCookies(cookieData.getCookies() + 1);
        this.updateTitle(Component.text(cookieData.getCookies()));
    }

    @Override
    public void closeEvent(InventoryCloseEvent event) {

        if (getInventory().getViewers().size() <= 1) {
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
            this.updateTitle(Component.text(cookieData.getCookies()));
        }

        if (timer >= AUTOSAVE_TIME_SECONDS * 20) {
            this.save();
            this.timer = 0;
        } else {
            this.timer++;
        }
    }

    @Override
    public boolean isTickable() {
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    private void save() {
        this.cookieData.updateLastAccessed();
        this.dataManager.save(this.uuid, this.cookieData);
    }
}
