package fi.septicuss.cookies.listener;

import fi.septicuss.cookies.manager.UiManager;
import fi.septicuss.cookies.ui.Ui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class UiListener implements Listener {

    private final UiManager manager;

    public UiListener(final UiManager uiManager) {
        this.manager = uiManager;
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();
        final Ui ui = this.manager.getUi(inventory);
        if (ui == null) return;

        ui.clickEvent(event);
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();
        final Ui ui = this.manager.getUi(inventory);
        if (ui == null) return;

        ui.openEvent(event);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        final Ui ui = this.manager.getUi(inventory);
        if (ui == null) return;

        ui.closeEvent(event);

        if (inventory.getViewers().size() <= 1) {
            this.manager.dispose(ui);
        }
    }


}
