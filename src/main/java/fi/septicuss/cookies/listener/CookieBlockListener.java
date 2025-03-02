package fi.septicuss.cookies.listener;

import com.jeff_media.customblockdata.events.CustomBlockDataRemoveEvent;
import fi.septicuss.cookies.manager.CookieBlockManager;
import fi.septicuss.cookies.utils.CookieBlockUtils;
import fi.septicuss.cookies.utils.CookieItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CookieBlockListener implements Listener {

    private final CookieBlockManager manager;

    public CookieBlockListener(CookieBlockManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();
        if (block == null || !CookieBlockUtils.isCookieBlock(block)) return;

        final UUID uuid = CookieBlockUtils.getBlockId(block);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            CookieBlockUtils.wipeCookieBlockData(block);
            block.setType(Material.AIR);

            this.handleBreakCookieBlock(block, uuid);
            return;
        }

        this.manager.openCookieUi(event.getPlayer(), uuid);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        final ItemStack item = event.getItemInHand();

        if (CookieItemUtils.hasEmptyItemSignature(item)) {
            this.handlePlaceCookieBlock(block, null);
            return;
        }

        if (CookieItemUtils.hasCookieId(item)) {
            final UUID uuid = CookieItemUtils.getCookieId(item);
            this.handlePlaceCookieBlock(block, uuid);
        }

    }

    @EventHandler
    public void on(CustomBlockDataRemoveEvent event) {
        final Block block = event.getBlock();
        if (!CookieBlockUtils.isCookieBlock(block)) return;

        final UUID uuid = CookieBlockUtils.getBlockId(block);
        this.handleBreakCookieBlock(block, uuid);
    }

    private void handlePlaceCookieBlock(Block block, UUID uuid) {
        CookieBlockUtils.setBlockId(block, uuid == null ? UUID.randomUUID() : uuid);
    }

    private void handleBreakCookieBlock(Block block, UUID uuid) {
        this.manager.closeUi(uuid);
        block.getDrops().clear();
        block.getWorld().dropItemNaturally(block.getLocation().clone().add(0.5, 0.5, 0.5), this.manager.getCookieBlockItem(uuid));

    }


}
