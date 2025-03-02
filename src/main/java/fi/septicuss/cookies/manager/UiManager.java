package fi.septicuss.cookies.manager;

import fi.septicuss.cookies.CookiePlugin;
import fi.septicuss.cookies.ui.Ui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UiManager {

    private final CookiePlugin plugin;
    private final Set<Ui> trackedUis = new HashSet<>();

    public UiManager(CookiePlugin plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        this.runTasks();
    }

    public void runTasks() {

        var uiTicker = new BukkitRunnable() {
            @Override
            public void run() {
                for (Ui ui : trackedUis)
                    if (ui.isTickable()) ui.tick();
            }
        };

        uiTicker.runTaskTimerAsynchronously(plugin, 1L, 1L);

    }

    public void trackUi(Ui ui) {
        this.trackedUis.add(ui);
    }

    public Set<Ui> getTrackedUis() {
        return Collections.unmodifiableSet(trackedUis);
    }

    public Ui getUi(Inventory inventory) {
        for (Ui ui : this.trackedUis) {
            if (ui.getInventory().equals(inventory)) {
                return ui;
            }
        }
        return null;
    }

    public void closeAndDispose(Ui ui) {
        final List<HumanEntity> viewers = ui.getInventory().getViewers();
        for (HumanEntity viewer : viewers) {
            viewer.closeInventory();
        }
        this.dispose(ui);
    }

    public void dispose(Ui ui) {
        this.trackedUis.remove(ui);
    }

    public void closeAll() {
        final Set<HumanEntity> viewers = new HashSet<>();
        this.trackedUis.forEach(ui -> {
            viewers.addAll(ui.getInventory().getViewers());
            ui.closeEvent(null);
        });
        viewers.forEach(HumanEntity::closeInventory);
    }


}
