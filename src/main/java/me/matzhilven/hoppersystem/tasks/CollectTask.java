package me.matzhilven.hoppersystem.tasks;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.utils.Constants;
import me.matzhilven.hoppersystem.utils.EntitiesGatherer;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class CollectTask extends BukkitRunnable {

    private final HopperSystem main;

    public CollectTask(HopperSystem main) {
        this.main = main;
    }

    @Override
    public void run() {

        for (Chunk chunk : main.getHopperManager().getChunks()) {

            final Set<Entity> entities = EntitiesGatherer.from(chunk).accepts(Item.class).gather();
            if (entities.size() == 0) continue;

            Optional<CustomHopper> optionalCustomHopper = main.getHopperManager().getFreeHopper(chunk);
            if (!optionalCustomHopper.isPresent()) continue;

            CustomHopper customHopper = optionalCustomHopper.get();
            CustomHopper.Type type = customHopper.getType();

            for (Entity entity : entities) {
                Item item = (Item) entity;
                ItemStack itemStack = item.getItemStack();

                if ((type == CustomHopper.Type.CROPS && Constants.CROP_DROPS.contains(itemStack.getType()))
                        || (type == CustomHopper.Type.MOBS && Constants.MOB_DROPS.contains(itemStack.getType()))) {

                    HashMap<Integer, ItemStack> returnedItems = customHopper.getHopper().getInventory().addItem(itemStack);
                    if (returnedItems.size() == 0) {
                        itemStack.setAmount(0);
                    } else {
                        itemStack.setAmount(returnedItems.get(0).getAmount());
                    }
                }
            }
        }
    }
}
