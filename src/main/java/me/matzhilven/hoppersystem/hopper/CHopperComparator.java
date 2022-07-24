package me.matzhilven.hoppersystem.hopper;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;

public class CHopperComparator implements Comparator<CustomHopper> {

    @Override
    public int compare(CustomHopper customHopper1, CustomHopper customHopper2) {
        return Integer.compare(getItems(customHopper1.getHopper().getInventory()), getItems(customHopper2.getHopper().getInventory()));

    }

    private int getItems(Inventory inventory) {
        int amount = 0;

        for (ItemStack i : inventory.getContents()) {
            if (i == null) continue;
            amount += i.getAmount();
        }

        return amount;
    }
}
