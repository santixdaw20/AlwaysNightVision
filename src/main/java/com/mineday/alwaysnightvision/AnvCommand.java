package com.mineday.alwaysnightvision;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class AnvCommand implements CommandExecutor {

    private final AlwaysNightVision plugin;

    public AnvCommand(AlwaysNightVision plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7Uso: §f/anv <reload|toggle|status>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.loadSettings();
                sender.sendMessage("§aAlwaysNightVision: configuración recargada.");
            }
            case "toggle" -> {
                plugin.setFeatureEnabled(!plugin.isFeatureEnabled());
                sender.sendMessage("§aAlwaysNightVision ahora está: "
                        + (plugin.isFeatureEnabled() ? "§aACTIVADO" : "§cDESACTIVADO"));
                if (!plugin.isFeatureEnabled()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                } else {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.applyNightVision(p);
                    }
                }
            }
            case "status" -> sender.sendMessage("§7AlwaysNightVision está: "
                    + (plugin.isFeatureEnabled() ? "§aACTIVADO" : "§cDESACTIVADO"));
            default -> sender.sendMessage("§7Uso: §f/anv <reload|toggle|status>");
        }
        return true;
    }
}
