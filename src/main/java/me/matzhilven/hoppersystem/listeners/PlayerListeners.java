package me.matzhilven.hoppersystem.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.data.PlayerDataFile;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener {

    private final HopperSystem main;

    public PlayerListeners(HopperSystem main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        main.getHopperManager().loadHoppers(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerDataFile dataFile = main.getDataCache().getOrDefault(event.getPlayer().getUniqueId(), new PlayerDataFile(main, event.getPlayer()));
        dataFile.saveData(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.HOPPER) return;

        NBTItem nbtItem = new NBTItem(e.getItemInHand());

        if (!nbtItem.hasKey("cropper")) return;

        CustomHopper.Type type = CustomHopper.Type.valueOf(nbtItem.getString("cropper"));

        if (!main.getHopperManager().canPlaceHopper(type, e.getBlock().getChunk())) {
            if (type == CustomHopper.Type.CROPS) {
                StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.max-croppers-per-chunk"));
            } else {
                StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.max-mob-hoppers-per-chunk"));
            }
            e.setCancelled(true);
            return;
        }

        CustomHopper customHopper = new CustomHopper(e.getPlayer().getUniqueId(), e.getBlock().getLocation(), (Hopper) e.getBlock().getState(), type, true);
        main.getHopperManager().addHopper(customHopper);

        StringUtils.sendMessage(e.getPlayer(), main.getConfig().getString("messages.placed-hopper")
                .replace("%hopper%", nbtItem.getItem().getItemMeta().getDisplayName())
        );
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.HOPPER) return;
        if (main.getHopperManager().getHopperAtLoc(e.getBlock().getLocation()) == null) return;

        CustomHopper removedHopper = main.getHopperManager().removeHopper(e.getBlock().getLocation());
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
}
