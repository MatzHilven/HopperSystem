package me.matzhilven.hoppersystem.hopper;

import me.matzhilven.hoppersystem.HopperSystem;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CHopperManager {

    private final Set<Chunk> chunks;
    private final int maxCroppers;
    private final int maxMobHoppers;
    private final HashMap<Location, CustomHopper> placedHoppers;

    public CHopperManager(HopperSystem main) {
        this.placedHoppers = new HashMap<>();
        this.chunks = new HashSet<>();

        this.maxCroppers = main.getConfig().getInt("max-croppers-per-chunk");
        this.maxMobHoppers = main.getConfig().getInt("max-mob-hoppers-per-chunk");

    }

    public Optional<CustomHopper> getFreeHopper(CustomHopper.Type type, Chunk chunk) {
        return placedHoppers.values().stream()
                .filter(CustomHopper::isLoaded)
                .filter(cropper -> cropper.getType() == type)
                .filter(cropper -> cropper.getChunk().isLoaded())
                .filter(cropper -> cropper.getChunk() == chunk)
                .min(new CHopperComparator());
    }

    public Optional<CustomHopper> getFreeHopper(Chunk chunk) {
        return placedHoppers.values().stream()
                .filter(CustomHopper::isLoaded)
                .filter(cropper -> cropper.getChunk().isLoaded())
                .filter(cropper -> cropper.getChunk() == chunk)
                .min(new CHopperComparator());
    }

    public boolean canPlaceHopper(CustomHopper.Type type, Chunk chunk) {
        return placedHoppers.values().stream()
                .filter(cropper -> cropper.getType() == type)
                .filter(cropper -> cropper.getChunk().isLoaded())
                .filter(cropper -> cropper.getChunk() == chunk)
                .count() < (type == CustomHopper.Type.CROPS ? maxCroppers : maxMobHoppers);
    }

    public CustomHopper getHopperAtLoc(Location location) {
        return placedHoppers.get(location);
    }

    public boolean isHopperInChunk(CustomHopper.Type type, Chunk chunk) {
        return placedHoppers.values().stream()
                .filter(customHopper -> customHopper.getType() == type)
                .anyMatch(cropper -> cropper.getChunk() == chunk);
    }

    public void addHopper(CustomHopper customHopper) {
        System.out.println(customHopper.getOwner());
        placedHoppers.put(customHopper.getLocation(), customHopper);

        chunks.add(customHopper.getChunk());
    }

    public CustomHopper removeHopper(Location location) {

        CustomHopper customHopper = placedHoppers.remove(location);
        Chunk chunk = customHopper.getChunk();
        chunks.remove(chunk);

        placedHoppers.forEach((loc, hopper) -> {
            if (loc.getChunk() == chunk) chunks.add(chunk);
        });

        return customHopper;
    }

    public List<CustomHopper> getPlayerHoppers(UUID uuid) {
        return placedHoppers.values().stream().filter(cropper -> cropper.getOwner().equals(uuid)).collect(Collectors.toList());
    }

    public Set<Chunk> getChunks() {
        return chunks;
    }

    public void loadHoppers(Player player) {
        getPlayerHoppers(player.getUniqueId()).forEach(customHopper -> customHopper.setLoaded(true));
    }
}
