package me.matzhilven.hoppersystem.data;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class PlayerDataFile {

    private final HopperSystem main;

    private final String name;
    private final File file;

    private FileConfiguration config;

    public PlayerDataFile(HopperSystem main, File file) {
        this.main = main;
        this.name = "/playerdata/" + file.getName();
        this.file = new File(main.getDataFolder(), name);

        setup();
        load();
    }

    public PlayerDataFile(HopperSystem main, Player player) {
        this.main = main;
        this.name = "/playerdata/" + player.getUniqueId() + ".yml";
        this.file = new File(main.getDataFolder(), name);

        setup();
    }

    private void setup() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            main.getServer().getConsoleSender().sendMessage("Error saving " + name);
            e.printStackTrace();
        }
    }

    public void saveData(UUID uuid) {
        List<CustomHopper> hoppers = main.getHopperManager().getPlayerHoppers(uuid);

        if (hoppers.size() == 0) {
            config.set("hoppers", null);
            save();
            return;
        }

        int id = 1;
        for (CustomHopper hopper : hoppers) {

            config.set("hoppers." + id + ".location", hopper.getLocation());
            config.set("hoppers." + id + ".type", hopper.getType().toString());

            id++;
        }

        save();
    }

    public void load() {
        if (!config.isConfigurationSection("hoppers")) return;

        UUID owner = UUID.fromString(file.getName().replace(".yml", ""));

        for (String id : config.getConfigurationSection("hoppers").getKeys(false)) {
            Location location = config.getLocation("hoppers." + id + ".location");
            CustomHopper.Type type = CustomHopper.Type.valueOf(config.getString("hoppers." + id + ".type"));

            if (location != null) {
                Hopper hopper = (Hopper) location.getBlock().getState();
                CustomHopper customHopper = new CustomHopper(owner, location, hopper, type, false);
                main.getHopperManager().addHopper(customHopper);
            }
        }

    }

}
