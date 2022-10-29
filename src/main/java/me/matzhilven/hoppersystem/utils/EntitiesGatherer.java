package me.matzhilven.hoppersystem.utils;

import com.google.common.collect.Sets;
import net.minecraft.server.level.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.function.Predicate;

public class EntitiesGatherer {

    private final Set<OPair<Integer, Integer>> chunks = Sets.newHashSet();
    private final Set<Class<?>> accepts = Sets.newHashSet();
    private World world;
    private Predicate<Entity> entityFilter;

    private EntitiesGatherer() {
    }

    public static EntitiesGatherer from(World world, int x, int z) {
        EntitiesGatherer entitiesGather = new EntitiesGatherer();
        entitiesGather.world = world;
        entitiesGather.chunks.add(new OPair<>(x, z));

        entitiesGather.entityFilter = (entity) -> entitiesGather.accepts.isEmpty() || entitiesGather.accepts.stream().anyMatch(clazz -> clazz.isAssignableFrom(entity.getClass()));

        return entitiesGather;
    }

    public static EntitiesGatherer from(Chunk chunk) {
        return from(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    public static EntitiesGatherer from(Location location) {
        return from(location.getWorld(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    public static EntitiesGatherer from(Location pos1, Location pos2) {
        int xMin = Math.min(pos1.getChunk().getX(), pos2.getChunk().getX());
        int zMin = Math.min(pos1.getChunk().getZ(), pos2.getChunk().getZ());
        int xMax = Math.max(pos1.getChunk().getX(), pos2.getChunk().getX());
        int zMax = Math.max(pos1.getChunk().getZ(), pos2.getChunk().getZ());

        EntitiesGatherer entitiesGather = new EntitiesGatherer();
        entitiesGather.world = pos1.getWorld();

        for (int x = xMin; x <= xMax; x++)
            for (int z = zMin; z <= zMax; z++)
                entitiesGather.chunks.add(new OPair<>(x, z));

        entitiesGather.entityFilter = (entity) -> {
            if (!entitiesGather.accepts.isEmpty() && entitiesGather.accepts.stream().noneMatch(clazz -> clazz.isAssignableFrom(entity.getClass())))
                return false;

            Location location = entity.getLocation();
            return location.getBlockX() >= pos1.getBlockX() && location.getBlockX() <= pos2.getBlockX() && location.getBlockZ() >= pos1.getBlockZ() && location.getBlockZ() <= pos2.getBlockZ();
        };
        return entitiesGather;
    }

    public EntitiesGatherer accepts(Class<? extends Entity> clazz) {
        accepts.add(clazz);
        return this;
    }

    public Set<Entity> gather() {
        Objects.requireNonNull(world);
        Set<Entity> returnsEntities = Sets.newHashSet();

        try {
            WorldServer worldServer = ((CraftWorld) world).getHandle();
            for (OPair<Integer, Integer> chunk : chunks) {

                net.minecraft.world.level.chunk.Chunk nmsChunk = worldServer.getChunkIfLoaded(chunk.getKey(), chunk.getValue());
                if (nmsChunk == null) continue;

                try {

                    returnsEntities.addAll(Arrays.asList(nmsChunk.bukkitChunk.getEntities()));

//                    for (int i = 0; i < 16; i++) {
//                        final List<Entity> entities = new ArrayList<>(Collections.synchronizedList(entitiesSlices[i]));
//
//                        entities.forEach(entity -> {
//                            try {
//                                returnsEntities.add((Entitçy) ENTITY_GET_BUKKIT_ENTITY_METHOD.invoke(entity));
//                            } catch (IllegalAccessException | InvocationTargetException e) {
//                                e.printStackTrace();
//                            }
//                        });
//                    }
                } catch (NoSuchElementException | ConcurrentModificationException ignored) {
                }
            }

            returnsEntities.removeIf(entity -> !entityFilter.test(entity));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return returnsEntities;
    }

    private static class OPair<O, T> {
        private final T key;
        private final O value;

        public OPair(T key, O value) {
            this.key = key;
            this.value = value;
        }

        public O getValue() {
            return value;
        }

        public T getKey() {
            return key;
        }
    }

}
