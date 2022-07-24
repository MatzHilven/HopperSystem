package me.matzhilven.hoppersystem.listeners;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.utils.Constants;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class WorldListeners implements Listener {

    private final HopperSystem main;

    public WorldListeners(HopperSystem main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onItemSpawn(ItemSpawnEvent e) {
        Material material = e.getEntity().getItemStack().getType();

        if (Constants.CROP_DROPS.contains(material)) {

            if (!main.getCropperManager().isHopperInChunk(CustomHopper.Type.CROPS, e.getLocation().getChunk())) return;

            Optional<CustomHopper> optionalCropper = main.getCropperManager().getFreeHopper(CustomHopper.Type.CROPS, e.getLocation().getChunk());

            if (!optionalCropper.isPresent()) return;

            CustomHopper hopper = optionalCropper.get();

            HashMap<Integer, ItemStack> returnedItems = hopper.getHopper().getInventory().addItem(e.getEntity().getItemStack());

            if (returnedItems.size() == 0) e.setCancelled(true);
            return;
        }

        if (!Constants.MOB_DROPS.contains(material)) return;

        if (!main.getCropperManager().isHopperInChunk(CustomHopper.Type.MOBS, e.getLocation().getChunk())) return;

        Optional<CustomHopper> optionalCropper = main.getCropperManager().getFreeHopper(CustomHopper.Type.MOBS, e.getLocation().getChunk());

        if (!optionalCropper.isPresent()) return;

        CustomHopper hopper = optionalCropper.get();

        HashMap<Integer, ItemStack> returnedItems = hopper.getHopper().getInventory().addItem(e.getEntity().getItemStack());

        if (returnedItems.size() == 0) e.setCancelled(true);
    }

    @EventHandler
    private void onPistonExtend(BlockPistonExtendEvent e) {
        if (e.getBlock().getType() != Material.HOPPER) return;
        if (main.getCropperManager().getHopperAtLoc(e.getBlock().getLocation()) == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    private void onEntityExplode(EntityExplodeEvent e) {
        if (e.blockList().isEmpty()) return;

        List<Block> exploded = new ArrayList<>();

        for (Block block : e.blockList()) {
            if (block == null) continue;
            if (block.getType() != Material.HOPPER && main.getCropperManager().getHopperAtLoc(block.getLocation()) == null) {
                exploded.add(block);
            }
        }
        e.blockList().clear();
        e.blockList().addAll(exploded);
    }

}
