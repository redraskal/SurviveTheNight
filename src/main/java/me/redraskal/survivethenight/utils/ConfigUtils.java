package me.redraskal.survivethenight.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class ConfigUtils {

    public static Location decodeLocation(String object) {
        String[] parts = object.split("@");
        return new Location(Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
    }

    public static String encodeLocation(Location object) {
        return object.getWorld().getName() + "@" + object.getX() + "@" + object.getY() + "@" + object.getZ()
                + "@" + object.getYaw() + "@" + object.getPitch();
    }

    public static Cuboid decodeCuboid(String object) {
        String pos1 = object.split(";")[0];
        String pos2 = object.split(";")[1];
        return new Cuboid(decodeLocation(pos1), decodeLocation(pos2));
    }

    public static String encodeCuboid(Cuboid object) {
        return encodeLocation(object.getPos1()) + ";" + encodeLocation(object.getPos2());
    }

    public static List<String> encodeLocationList(List<Location> object) {
        List<String> result = new ArrayList<>();

        object.forEach(location -> result.add(encodeLocation(location)));

        return result;
    }

    public static List<Location> decodeLocationList(List<String> object) {
        List<Location> result = new ArrayList<>();

        object.forEach(location -> result.add(decodeLocation(location)));

        return result;
    }

    public static List<String> encodeBlockList(List<Block> object) {
        List<String> result = new ArrayList<>();

        object.forEach(block -> result.add(encodeLocation(block.getLocation())));

        return result;
    }

    public static List<Block> decodeBlockList(List<String> object) {
        List<Block> result = new ArrayList<>();

        object.forEach(block -> result.add(decodeLocation(block).getBlock()));

        return result;
    }

    public static List<String> encodeCuboidList(List<Cuboid> object) {
        List<String> result = new ArrayList<>();

        object.forEach(cuboid -> result.add(encodeCuboid(cuboid)));

        return result;
    }

    public static List<Cuboid> decodeCuboidList(List<String> object) {
        List<Cuboid> result = new ArrayList<>();

        object.forEach(cuboid -> result.add(decodeCuboid(cuboid)));

        return result;
    }

    public static List<String> encodeAreaMap(Map<String, Cuboid> object) {
        List<String> result = new ArrayList<>();

        for(Map.Entry<String, Cuboid> entry : object.entrySet()) {
            result.add(entry.getKey() + "&" + encodeCuboid(entry.getValue()));
        }

        return result;
    }

    public static Map<String, Cuboid> decodeAreaMap(List<String> object) {
        Map<String, Cuboid> result = new HashMap<>();

        object.forEach(cuboid -> result.put(cuboid.split("&")[0], decodeCuboid(cuboid.split("&")[1])));

        return result;
    }

    public static List<String> encodeCuboidMap(Map<Cuboid, Location> object) {
        List<String> result = new ArrayList<>();

        for(Map.Entry<Cuboid, Location> entry : object.entrySet()) {
            result.add(encodeCuboid(entry.getKey()) + "&" + encodeLocation(entry.getValue()));
        }

        return result;
    }

    public static Map<Cuboid, Location> decodeCuboidMap(List<String> object) {
        Map<Cuboid, Location> result = new HashMap<>();

        object.forEach(cuboid -> result.put(decodeCuboid(cuboid.split("&")[0]), decodeLocation(cuboid.split("&")[1])));

        return result;
    }
}