package me.matzhilven.hoppersystem.utils;

import me.matzhilven.hoppersystem.HopperSystem;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, String.format("[%s] " + s, HopperSystem.getPlugin(HopperSystem.class).getDescription().getName()));
    }

    public static void severe(String s) {
        Bukkit.getLogger().log(Level.SEVERE, String.format("[%s] " + s, HopperSystem.getPlugin(HopperSystem.class).getDescription().getName()));
    }
}
