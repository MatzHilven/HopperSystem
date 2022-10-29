package me.matzhilven.hoppersystem.hopper;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Hopper;

import java.util.UUID;

public class CustomHopper {

    private final UUID owner;
    private final Location location;
    private final Hopper hopper;
    private final Type type;
    private boolean loaded;

    public CustomHopper(UUID owner, Location location, Hopper hopper, Type type, boolean loaded) {
        this.owner = owner;
        this.location = location;
        this.hopper = hopper;
        this.type = type;
        this.loaded = loaded;
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

    public Type getType() {
        return type;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        System.out.println(this);
        this.loaded = loaded;
    }

    public enum Type {
        CROPS, MOBS
    }
}
