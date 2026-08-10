package com.mineday.alwaysnightvision;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AlwaysNightVision extends JavaPlugin {

    private static AlwaysNightVision instance;

    private boolean enabled = true;
    private int refreshIntervalTicks;
    private int amplifier;
    private boolean ambient;
    private boolean hideParticles;
    private boolean hideIcon;
    private int durationTicks;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        loadSettings();

        getServer().getPluginManager().registerEvents(new NightVisionListener(this), this);
        getCommand("anv").setExecutor(new AnvCommand(this));

        // Task periódico: refuerza el efecto a todos los jugadores online.
        // Esto es lo que hace que sobreviva a totems, leche, comandos /effect clear
        // externos, plugins de regiones que remueven efectos al salir, etc.
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (!enabled) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyNightVision(player);
            }
        }, 20L, refreshIntervalTicks);

        getLogger().info("AlwaysNightVision habilitado. Refrescando cada " + refreshIntervalTicks + " ticks.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AlwaysNightVision deshabilitado.");
    }

    public void loadSettings() {
        reloadConfig();
        refreshIntervalTicks = Math.max(1, getConfig().getInt("refresh-interval-ticks", 20));
        amplifier = Math.max(0, getConfig().getInt("amplifier", 0));
        ambient = getConfig().getBoolean("ambient", true);
        hideParticles = getConfig().getBoolean("hide-particles", true);
        hideIcon = getConfig().getBoolean("hide-icon", true);
        durationTicks = Math.max(200, getConfig().getInt("duration-ticks", 999999));
    }

    public void applyNightVision(Player player) {
        if (!enabled) return;
        if (player.hasPermission("alwaysnightvision.bypass")) return;

        org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.NIGHT_VISION,
                durationTicks,
                amplifier,
                ambient,
                !hideParticles,
                !hideIcon
        );
        player.addPotionEffect(effect);
    }

    public boolean isFeatureEnabled() {
        return enabled;
    }

    public void setFeatureEnabled(boolean value) {
        this.enabled = value;
    }

    public String getJoinMessage() {
        return getConfig().getString("join-message", "");
    }

    public static AlwaysNightVision getInstance() {
        return instance;
    }
}
