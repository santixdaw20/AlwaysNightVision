package com.mineday.alwaysnightvision;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

public class NightVisionListener implements Listener {

    private final AlwaysNightVision plugin;

    public NightVisionListener(AlwaysNightVision plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.applyNightVision(player);

        String msg = plugin.getJoinMessage();
        if (msg != null && !msg.isEmpty()) {
            player.sendMessage(msg);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> plugin.applyNightVision(player));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Bukkit.getScheduler().runTask(plugin, () -> plugin.applyNightVision(player));
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.applyNightVision(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> plugin.applyNightVision(event.getPlayer()));
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC
                || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
            Bukkit.getScheduler().runTask(plugin, () -> plugin.applyNightVision(player));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPotionEffectChange(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getModifiedType() != PotionEffectType.NIGHT_VISION) return;
        if (player.hasPermission("alwaysnightvision.bypass")) return;
        if (!plugin.isFeatureEnabled()) return;

        switch (event.getAction()) {
            case REMOVED, CLEARED -> Bukkit.getScheduler().runTask(plugin,
                    () -> plugin.applyNightVision(player));
            default -> {
            }
        }
    }
}
