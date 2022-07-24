package me.matzhilven.hoppersystem.tasks;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.utils.Constants;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CollectTask extends BukkitRunnable {

    private final HopperSystem main;

    public CollectTask(HopperSystem main) {
        this.main = main;
    }

    @Override
    public void run() {

        for (Chunk chunk : main.getCropperManager().getChunks()) {
            if (chunk.getEntities().length == 0) continue;
            Optional<CustomHopper> optionalCustomHopper = main.getCropperManager().getFreeHopper(chunk);

            if (!optionalCustomHopper.isPresent()) continue;

            CustomHopper customHopper = optionalCustomHopper.get();
            CustomHopper.Type type = customHopper.getType();

            for (Entity entity : chunk.getEntities()) {
                if (!(entity instanceof Item)) continue;

                Item item = (Item) entity;
                ItemStack itemStack = item.getItemStack();

                if ((type == CustomHopper.Type.CROPS && Constants.CROP_DROPS.contains(itemStack.getType()))
                        || (type == CustomHopper.Type.MOBS && Constants.MOB_DROPS.contains(itemStack.getType()))) {
                    HashMap<Integer, ItemStack> returnedItems = customHopper.getHopper().getInventory().addItem(itemStack);
                    if (returnedItems.size() == 0) item.remove();
                }
            }
        }
    }
}
