package me.matzhilven.hoppersystem.hopper;

import me.matzhilven.hoppersystem.utils.ConfigUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;

import java.util.UUID;

public class CustomHopper {

    private final UUID owner;
    private final Location location;
    private final Hopper hopper;
    private final Type type;

    public CustomHopper(UUID owner, Location location, Hopper hopper, Type type) {
        this.owner = owner;
        this.location = location;
        this.hopper = hopper;
        this.type = type;
    }

    public static CustomHopper fromString(String configStr) {
        String[] split = configStr.split("\\|");
        Location location = ConfigUtils.toLocation(split[1]);
        if (location.getWorld() == null) return null;
        if (location.getWorld().getBlockAt(location).getType() != Material.HOPPER) return null;

        return new CustomHopper(UUID.fromString(split[0]), location,
                (Hopper) location.getWorld().getBlockAt(location).getState(), Type.valueOf(split[2]));
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public Hopper getHopper() {
        return hopper;
    }

    public Chunk getChunk() {
        return location.getChunk();
    }

    public String getConfigString() {
        return owner.toString() + "|" + ConfigUtils.toString(location) + "|" + type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CROPS, MOBS
    }
}
