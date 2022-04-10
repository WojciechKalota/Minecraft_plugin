package me.regos.deadbodies;

import me.regos.deadbodies.listeners.DeathListener;
import me.regos.deadbodies.tasks.BodyRemoverTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeadBodies extends JavaPlugin {

    private static DeadBodies plugin;
    private BodyManager bodyManager;

    //stopped working at the very end
    //probably an easy fix

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.bodyManager = new BodyManager();
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        new BodyRemoverTask(plugin, bodyManager).runTaskTimerAsynchronously(this, 0L, 20L);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static DeadBodies getPlugin(){
        return plugin;
    }

    public BodyManager getBodyManager() {
        return bodyManager;
    }
}
