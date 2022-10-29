package me.matzhilven.hoppersystem;

import me.matzhilven.hoppersystem.commands.CHopperCommand;
import me.matzhilven.hoppersystem.data.PlayerDataFile;
import me.matzhilven.hoppersystem.hopper.CHopperManager;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.listeners.PlayerListeners;
import me.matzhilven.hoppersystem.listeners.WorldListeners;
import me.matzhilven.hoppersystem.tasks.CollectTask;
import me.matzhilven.hoppersystem.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class HopperSystem extends JavaPlugin {

    private CHopperManager hopperManager;
    private HashMap<UUID, PlayerDataFile> dataCache;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        hopperManager = new CHopperManager(this);

        new CHopperCommand(this);

        new PlayerListeners(this);
        new WorldListeners(this);

        new CollectTask(this).runTaskTimerAsynchronously(this, 0, getConfig().getLong("pickup-delay"));


        dataCache = new HashMap<>();
        loadCache();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerDataFile dataFile = dataCache.getOrDefault(player.getUniqueId(), new PlayerDataFile(this, player));
            dataFile.saveData(player.getUniqueId());
        });
    }

    private void loadCache() {
        File dataFolder = new File(getDataFolder(), "playerdata");

        if (!dataFolder.exists()) dataFolder.mkdirs();

        for (File file : dataFolder.listFiles()) {
            YamlConfiguration configuration = new YamlConfiguration();

            try {
                configuration.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            PlayerDataFile dataFile = new PlayerDataFile(this, file);
            dataFile.load();
            dataCache.put(UUID.fromString(file.getName().replace(".yml", "")), dataFile);
        }
    }

    public CHopperManager getHopperManager() {
        return hopperManager;
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

    public HashMap<UUID, PlayerDataFile> getDataCache() {
        return dataCache;
    }
}
