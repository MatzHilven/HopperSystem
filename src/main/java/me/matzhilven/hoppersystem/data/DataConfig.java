package me.matzhilven.hoppersystem.data;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataConfig {

    private final File data;

    private final HopperSystem main;

    private FileConfiguration config;

    public DataConfig(HopperSystem main) {
        this.main = main;
        this.data = new File(main.getDataFolder(), "data.yml");

        if (!this.data.exists()) {
            this.data.getParentFile().mkdir();
            main.saveResource("data.yml", false);;
        }

        this.config = new YamlConfiguration();

        try {
            this.config.load(data);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        loadData();
    }

    public void loadData() {
        if (config.get("croppers") == null) return;

        HashMap<Location, CustomHopper> croppers = new HashMap<>();

        for (String cropperStr : config.getStringList("croppers")) {
            CustomHopper customHopper = CustomHopper.fromString(cropperStr);
            if (customHopper == null) continue;
            croppers.put(customHopper.getLocation(), customHopper);
        }

        main.getCropperManager().setPlacedHoppers(croppers);
    }

    public synchronized void saveCroppers() {
        List<String> cropperStrings = main.getCropperManager().getPlacedHoppers().stream().map(CustomHopper::getConfigString).collect(Collectors.toList());
        config.set("croppers", cropperStrings);
        save();
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.data);
    }

    public void save() {
        try {
            this.config.save(this.data);
        } catch (IOException e) {
            this.main.getServer().getConsoleSender().sendMessage("Error saving data.yml");
            e.printStackTrace();
        }
    }
}
