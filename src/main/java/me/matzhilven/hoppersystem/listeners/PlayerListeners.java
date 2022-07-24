package me.matzhilven.hoppersystem.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.utils.Constants;
import me.matzhilven.hoppersystem.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerListeners implements Listener {

    private final HopperSystem main;

    public PlayerListeners(HopperSystem main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.HOPPER) return;

        NBTItem nbtItem = new NBTItem(e.getItemInHand());

        if (!nbtItem.hasKey("cropper")) return;

        CustomHopper.Type type = CustomHopper.Type.valueOf(nbtItem.getString("cropper"));

        if (!main.getCropperManager().canPlaceHopper(type, e.getBlock().getChunk())) {
            if (type == CustomHopper.Type.CROPS) {
                StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.max-croppers-per-chunk"));
            } else {
                StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.max-mob-hoppers-per-chunk"));
            }
            e.setCancelled(true);
            return;
        }

        CustomHopper customHopper = new CustomHopper(e.getPlayer().getUniqueId(), e.getBlock().getLocation(), (Hopper) e.getBlock().getState(), type);
        main.getCropperManager().addHopper(customHopper);

        for (Entity entity : e.getBlock().getChunk().getEntities()) {
            if (entity instanceof Item) {
                ItemStack item = ((Item) entity).getItemStack();
                if (type == CustomHopper.Type.CROPS && Constants.CROP_DROPS.contains(item.getType())) {
                    customHopper.getHopper().getInventory().addItem(item);
                    entity.remove();
                } else if (type == CustomHopper.Type.MOBS && Constants.MOB_DROPS.contains(item.getType())) {
                    customHopper.getHopper().getInventory().addItem(item);
                    entity.remove();
                }
            }
        }

        StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.placed-hopper")
                .replace("%hopper%", nbtItem.getItem().getItemMeta().getDisplayName())
        );
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.HOPPER) return;
        if (main.getCropperManager().getHopperAtLoc(e.getBlock().getLocation()) == null) return;

        CustomHopper removedHopper = main.getCropperManager().removeHopper(e.getBlock().getLocation());
        e.setDropItems(false);

        ItemStack item = main.getItem(removedHopper.getType(), 1);

        if (e.getPlayer().getInventory().firstEmpty() == -1) {
            e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), item);
        } else {
            e.getPlayer().getInventory().addItem(item);
        }

        StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.broken-hopper")
                .replace("%hopper%", item.getItemMeta().getDisplayName())
        );
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        List<CustomHopper> playerCustomHoppers = main.getCropperManager().getPlayerHoppers(player);
        if (playerCustomHoppers.isEmpty()) return;

        for (CustomHopper playerCustomHopper : playerCustomHoppers) {
            if (playerCustomHopper.getChunk().getWorld() == player.getWorld()) {
                if (!playerCustomHopper.getChunk().isLoaded()) playerCustomHopper.getChunk().load();
            }
        }
    }
}
