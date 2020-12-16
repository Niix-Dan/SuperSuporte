package me.daniel.SuperSuporte;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static me.daniel.SuperSuporte.SuperSuporte.plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SuperComando implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        Player p = (Player) sender;
        
        if(args.length < 1) {
            Inventory inv = Bukkit.createInventory(null, 27, "§e§lSuper§4§lSuporte §c§lv"+SuperSuporte.version);
            inv.clear();
            
            List<String> apoiadores = new ArrayList();
            List<String> comandos = new ArrayList();
            
            
            apoiadores.add(" ");
            apoiadores.add("§a- §bDKplayerKS");
            apoiadores.add(" ");
            
            comandos.add(" ");
            comandos.add("§c/supersuporte §8[<reload>/<version>]");
            comandos.add("§c/duvida §8<duvida>");
            
            if(p.hasPermission("supersuporte.staff")) {
                comandos.add("§c/responder §8<player> <resposta>");
                comandos.add("§c/duvidas");
                comandos.add(" ");
            }
            
            ItemStack apoiadores_Item = newItem(new ItemStack(Material.SKULL_ITEM), "§b§lApoiadores", apoiadores, "MHF_Question");
            ItemStack comandos_Item = newItem(new ItemStack(Material.SKULL_ITEM), "§c§lComandos", comandos, "CONSOLE");
            
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1);
            item.setDurability((short) 3);
            for(int i = 0 ; i < 9 ; i++) {
                inv.setItem(i, item);
                inv.setItem(i+18, item);
            }
            inv.setItem(9, item);
            inv.setItem(17, item);
            
            inv.setItem(12, apoiadores_Item);
            inv.setItem(14, comandos_Item);
                    
            p.openInventory(inv);
            
        } else {
            T_Config config = new T_Config(SuperSuporte.getPlugin(SuperSuporte.class), "config.yml");
            
            if(args[0].equalsIgnoreCase("reload")) {
                String prefix = config.getConfig().getString("prefix").replaceAll("&", "§")+ " ";
                if(!config.getConfig().getBoolean("allow-prefix")) {
                    prefix = "";
                }
                if(!p.hasPermission("supersuporte.staff")) {
                    p.sendMessage(prefix+"§cVocê não contém permissões suficientes para executar este comando.");
                    return true;
                }
                
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                Bukkit.getServer().getPluginManager().getPlugin("SuperSuporte").reloadConfig();
                Bukkit.getPluginManager().getPlugin("SuperSuporte").reloadConfig();
                
                Comando.configs.reloadConfig();
                Events.configs.reloadConfig();
                SuperSuporte.plugin.reloadConfig();
                
                Bukkit.getPluginManager().getPlugin(SuperSuporte.plugin.getName()).reloadConfig();
                Bukkit.getServer().getPluginManager().enablePlugin(plugin);
                
                p.sendMessage(prefix+"§cConfigurações recarregadas com sucesso!");
            }
            if(args[0].equalsIgnoreCase("version")) {
                p.sendMessage("§e§lSuperSuporte §cv"+SuperSuporte.version);
            }
        }
        
        return false;
    }
    
    private ItemStack newItem(ItemStack item, String name, List lore, String owner) {
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        
        sm.setOwner(owner);
        sm.setLore(lore);
        sm.setDisplayName(name);
        
        item.setDurability((short) 3);
        item.setItemMeta(sm);
        
        return item;
    }
    
}
