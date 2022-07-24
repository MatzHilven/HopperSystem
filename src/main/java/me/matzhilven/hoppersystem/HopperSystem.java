package me.matzhilven.hoppersystem;

import me.matzhilven.hoppersystem.commands.CHopperCommand;
import me.matzhilven.hoppersystem.data.DataConfig;
import me.matzhilven.hoppersystem.hopper.CHopperManager;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.listeners.WorldListeners;
import me.matzhilven.hoppersystem.listeners.PlayerListeners;
import me.matzhilven.hoppersystem.tasks.CollectTask;
import me.matzhilven.hoppersystem.utils.ItemBuilder;
import me.matzhilven.hoppersystem.utils.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class HopperSystem extends JavaPlugin {

    private CHopperManager CHopperManager;
    private DataConfig dataConfig;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        CHopperManager = new CHopperManager(this);
        dataConfig = new DataConfig(this);

        new CHopperCommand(this);

        new PlayerListeners(this);
        new WorldListeners(this);

        new CollectTask(this).runTaskTimer(this, getConfig().getLong("pickup-delay"),
                getConfig().getLong("pickup-delay"));


        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Logger.log("saving data...");
            dataConfig.saveCroppers();
        }, 20L * 60L * 5L, 20L * 60L * 5L);
    }

    @Override
    public void onDisable() {
        dataConfig.saveCroppers();
    }

    public CHopperManager getCropperManager() {
        return CHopperManager;
    }

    public ItemStack getItem(CustomHopper.Type type, int amount) {

        String section = type == CustomHopper.Type.CROPS ? "cropper." : "mob-hopper.";
        return new ItemBuilder(Material.HOPPER)
                .setName(getConfig().getString(section + "name"))
                .setLore(getConfig().getStringList(section + "lore"))
                .addNBT("cropper", type.toString())
                .setAmount(amount)
                .toItemStack();
    }
}
