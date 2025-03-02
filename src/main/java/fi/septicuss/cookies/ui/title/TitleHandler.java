package fi.septicuss.cookies.ui.title;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBundle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TitleHandler implements PacketListener {

    private static final int GENERIC_6_ROW_TYPE = 5;
    private final ConcurrentHashMap<UUID, WindowData> windowData;
    private final ConcurrentHashMap<UUID, Component> titleQueue;
    private final ConcurrentHashMap<Integer, WindowItemData> itemsData;
    private PacketListenerCommon packetListenerCommon;

    public TitleHandler() {
        this.windowData = new ConcurrentHashMap<>();
        this.titleQueue = new ConcurrentHashMap<>();
        this.itemsData = new ConcurrentHashMap<>();
    }

    public void initialize() {
        packetListenerCommon = PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
    }

    public void uninitialize() {
        if (packetListenerCommon != null) {
            PacketEvents.getAPI().getEventManager().unregisterListener(packetListenerCommon);
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Intercept open window
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            final WrapperPlayServerOpenWindow openWindow = new WrapperPlayServerOpenWindow(event);

            if (openWindow.getType() != GENERIC_6_ROW_TYPE) {
                return;
            }

            final UUID uuid = event.getUser().getUUID();

            if (titleQueue.containsKey(uuid)) {
                openWindow.setTitle(titleQueue.get(uuid));
                titleQueue.remove(uuid);
            }

            windowData.put(uuid, new WindowData(openWindow.getContainerId(), openWindow.getType()));
            return;
        }

        // Intercept item update
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            final UUID uuid = event.getUser().getUUID();

            if (!windowData.containsKey(uuid)) {
                return;
            }

            final WrapperPlayServerWindowItems items = new WrapperPlayServerWindowItems(event);
            this.itemsData.put(items.getWindowId(), new WindowItemData(items.getWindowId(), items.getStateId(), items.getItems(), items.getCarriedItem().orElse(null)));
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLOSE_WINDOW) {
            return;
        }

        final UUID uuid = event.getUser().getUUID();

        WindowData removedData = windowData.remove(uuid);
        titleQueue.remove(uuid);

        if (removedData != null) {
            boolean foundItemUse = false;

            for (WindowData data : this.windowData.values()) {
                if (data.windowId == removedData.windowId()) {
                    foundItemUse = true;
                }
            }

            if (!foundItemUse) {
                itemsData.remove(removedData.windowId());
            }

        }

    }

    public void setPlayerInventoryTitle(Player player, Component title) {
        final InventoryType type = player.getOpenInventory().getType();
        if (type == InventoryType.CRAFTING || type == InventoryType.CREATIVE)
            return;

        final WindowData data = windowData.get(player.getUniqueId());

        if (data == null) {
            return;
        }

        final int windowId = data.windowId();
        final int containerType = data.containerType();

        this.sendOpenScreenPacket(player, windowId, containerType, title);
    }

    public void queueTitle(Player player, Component title) {
        this.titleQueue.put(player.getUniqueId(), title);
    }

    private void sendOpenScreenPacket(Player player, int windowId, int containerType, Component title) {
        final User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        final WrapperPlayServerOpenWindow openWindow = new WrapperPlayServerOpenWindow(windowId, containerType, title);
        final WrapperPlayServerBundle bundle = new WrapperPlayServerBundle();

        user.sendPacket(bundle);
        user.sendPacket(openWindow);

        if (this.itemsData.containsKey(windowId)) {
            final WrapperPlayServerWindowItems items = this.itemsData.get(windowId).wrapper();
            user.sendPacket(items);
        }

        user.sendPacket(bundle);
    }

    private String debug(List<ItemStack> items) {

        StringBuilder builder = new StringBuilder();

        for (var item : items) {
            Optional<Component> componentOpt = item.getComponent(ComponentTypes.ITEM_NAME);
            componentOpt.ifPresent(component -> {
                if (component instanceof TextComponent text) {
                    builder.append(text.content());
                    for (var ch : component.children()) {
                        if (ch instanceof TextComponent t1)
                            builder.append(t1.content());
                    }
                    builder.append(";");
                }
            });
        }
        return builder.toString();
    }

    private record WindowData(int windowId, int containerType) {
    }

    private record WindowItemData(int windowId, int stateID, List<ItemStack> items, ItemStack carriedItem) {
        public WrapperPlayServerWindowItems wrapper() {
            return new WrapperPlayServerWindowItems(windowId, stateID, items, carriedItem);
        }
    }

}
