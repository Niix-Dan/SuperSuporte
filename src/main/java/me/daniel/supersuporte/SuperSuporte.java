package me.daniel.SuperSuporte;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class SuperSuporte extends JavaPlugin {
    public static SuperSuporte plugin;
    public static String version;
    
    @Override
    public void onEnable() {
        plugin = this;
        version = "1.4";
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Cmds();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        Bukkit.getConsoleSender().sendMessage("§e[SuperSuporte] §eSuperSuporte §ePlugin §ahabilitado §ecom §esucesso!");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§c[SuperSuporte] §cSuperSuporte §cPlugin §4desabilitado §ccom §csucesso!");
    }

    public void Cmds() {
        getCommand("duvida").setExecutor(new Comando());
        getCommand("duvidas").setExecutor(new Comando());
        getCommand("responder").setExecutor(new Comando());
        getCommand("supersuporte").setExecutor(new SuperComando());
    }
    
    public void ConfigReload() {
        reloadConfig();
    }

}
