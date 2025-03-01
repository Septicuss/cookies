package fi.septicuss.cookies.ui.title;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBundle;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TitleHandler implements PacketListener {

    private record WindowData(int windowId, int containerType) { }

    private record WindowItemData(int windowId, int stateID, List<ItemStack> items, ItemStack carriedItem) {
        public WrapperPlayServerWindowItems wrapper() {
            return new WrapperPlayServerWindowItems(windowId, stateID, items, carriedItem);
        }
    }

    private static final int GENERIC_6_ROW_TYPE = 5;
    private final HashMap<UUID, WindowData> windowData;
    private final HashMap<UUID, Component> titleQueue;
    private final HashMap<UUID, WindowItemData> itemsQueue;

    public TitleHandler() {
        this.windowData = new HashMap<>();
        this.titleQueue = new HashMap<>();
        this.itemsQueue = new HashMap<>();
    }

    public void initialize() {
        PacketEvents.getAPI().getEventManager().registerListener(this, PacketListenerPriority.MONITOR);
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.OPEN_WINDOW) {
            if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
                if (windowData.containsKey(event.getUser().getUUID())) {
                    if (itemsQueue.containsKey(event.getUser().getUUID())) {
                        return;
                    }
                    var items = new WrapperPlayServerWindowItems(event);
                    this.itemsQueue.put(event.getUser().getUUID(), new WindowItemData(items.getWindowId(), items.getStateId(), items.getItems(), items.getCarriedItem().orElse(null)));
                }
            }
            return;
        }

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
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLOSE_WINDOW) {
            return;
        }

        final UUID uuid = event.getUser().getUUID();

        titleQueue.remove(uuid);
        windowData.remove(uuid);
        itemsQueue.remove(uuid);
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

        if (this.itemsQueue.containsKey(player.getUniqueId())) {
            final WrapperPlayServerWindowItems items = this.itemsQueue.get(player.getUniqueId()).wrapper();
            user.sendPacket(items);
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, this.itemsQueue.get(player.getUniqueId()).wrapper());
        }

        user.sendPacket(bundle);
    }

}
