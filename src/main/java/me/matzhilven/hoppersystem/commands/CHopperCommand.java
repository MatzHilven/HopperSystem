package me.matzhilven.hoppersystem.commands;

import me.matzhilven.hoppersystem.HopperSystem;
import me.matzhilven.hoppersystem.hopper.CustomHopper;
import me.matzhilven.hoppersystem.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CHopperCommand implements CommandExecutor, TabExecutor {

    private final HopperSystem main;

    public CHopperCommand(HopperSystem main) {
        this.main = main;
        main.getCommand("givehopper").setExecutor(this);
        main.getCommand("givehopper").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hoppersystem.givehopper")) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-permissions"));
            return true;
        }

        if (args.length != 3) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-target"));
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-amount"));
            return true;
        }

        CustomHopper.Type cropType;

        try {
            cropType = CustomHopper.Type.valueOf(args[2]);
        } catch (IllegalArgumentException e) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-type"));
            return true;
        }

        ItemStack item = main.getItem(cropType, amount);

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), item);
        } else {
            target.getInventory().addItem(item);
        }

        StringUtils.sendMessage(sender, main.getConfig().getString("messages.given-hopper")
                .replace("%amount%", StringUtils.format(amount))
                .replace("%target%", target.getName())
                .replace("%hopper%", item.getItemMeta().getDisplayName())
        );
        StringUtils.sendMessage(target, main.getConfig().getString("messages.received-hopper")
                .replace("%amount%", StringUtils.format(amount))
                .replace("%hopper%", item.getItemMeta().getDisplayName())
        );

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], new ArrayList<>(), Arrays.asList("1", "5", "10"));
        } else if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], new ArrayList<>(), Arrays.asList("CROPS", "MOBS"));
        }
        return null;
    }
}
