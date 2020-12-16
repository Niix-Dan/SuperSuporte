package me.daniel.SuperSuporte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class Comando implements CommandExecutor {

    public String Mensagem(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        return sb.toString();
    }

    public String Mensagem2(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1 ; i < args.length ; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        return sb.toString();
    }
    public static T_Config configs = new T_Config(SuperSuporte.getPlugin(SuperSuporte.class), "config.yml");
    public static T_Config reports = new T_Config(SuperSuporte.getPlugin(SuperSuporte.class), "duvidas.yml");

    String prefix = configs.getConfig().getString("prefix").replaceAll("&", "§")+" ";
    
    String headname = configs.getConfig().getString("messages.duvidas.head.name");
    List<String> headdesc = configs.getConfig().getStringList("messages.duvidas.head.description");
    

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
        
        if(!configs.getConfig().getBoolean("prefix-allow")) {
            prefix = "";
        }
        
        if(!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage("Apenas jogadores podem executar comandos!");
            return true;
        }
        
        Player p = (Player) sender;
        
        if(cmd.getName().equalsIgnoreCase("duvida")) {
            if(args.length == 0) {
                List<String> no_args = configs.getConfig().getStringList("messages.duvida.no_args");
                p.sendMessage(prefix+Separator(no_args));
                Play(p, 5);
                return true;
            }
            
            if(reports.getConfig().getString("Player."+p.getName().toLowerCase()) != null ) {
                List<String> duv_exist = configs.getConfig().getStringList("messages.duvida.exist");
                p.sendMessage(prefix+Separator(duv_exist));
                Play(p, 5);
                return true;
            }
            
            String reporter = p.getName().toLowerCase();
            String mensagem = Mensagem(args);
            
            for(Player players : Bukkit.getOnlinePlayers()) {
                if (players.hasPermission("supersuporte.staff")) {
                    players.sendMessage(duvida(0, p, mensagem));
                    Play(players, 3);
                }
            }
            
            Date todaydate = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy '|' hh:mm:ss a");
            reports.set("Player."+reporter+".duvida", mensagem);
            reports.set("Player."+reporter+".expira", ft.format(todaydate));
            p.sendMessage(duvida(1, p, mensagem));
            reports.saveConfig();
        }
        
        
        if(cmd.getName().equalsIgnoreCase("duvidas")) {
            List<String> reportsmenu = new ArrayList();
            List<String> base = new ArrayList();
            List<String> base2 = new ArrayList();
            
            if(!p.hasPermission("supersuporte.staff")) {
                return true;
            }
            if(reports.getConfig().getConfigurationSection("Player") == null) {
                String empty = configs.getConfig().getString("messages.duvidas.empty").replaceAll("&", "§");
                p.sendMessage(empty);
                return true;
            }
            reports.reloadConfig();
            String nome = configs.getConfig().getString("messages.duvidas.menu-name").replaceAll("&", "§");
            Inventory inv = Bukkit.createInventory(null, 54, nome);
            inv.clear();
            
            ConfigurationSection cs = reports.getConfig().getConfigurationSection("Player");
            for(String report : cs.getKeys(false)){
                reportsmenu.add(report);
            }
            
            for(int n = 0 ; n < reportsmenu.size() ; ++n) {
                //String duvida = cs.getString(reportsmenu.get(n)+".duvida").replaceAll("((?:\\w+\\s){4}\\w+)(\\s)", "$1\n");
                String duvida = cs.getString(reportsmenu.get(n)+".duvida").replaceAll("((?:\\w+\\s){4}\\w+)(\\s)", "$1\n");
                
                base.clear();
                base2.clear();
                
                base = configs.getConfig().getStringList("messages.duvidas.head.description");
                for(String line : base) {
                    line = line.replaceAll("<player>", reportsmenu.get(n));
                    line = line.replaceAll("<duvida>", duvida);
                    line = line.replaceAll("<criado>", cs.getString(reportsmenu.get(n)+".expira"));
                    line = line.replaceAll("&", "§");
                    
                    base2.add(line);
                }
                
                ItemStack si = nameItemi(new ItemStack(Material.SKULL_ITEM),"§6§l"+reportsmenu.get(n), base2, reportsmenu.get(n));
                inv.setItem(n, si);
            }
            p.openInventory(inv);
            
        }
        
        if(cmd.getName().equalsIgnoreCase("responder")) {
            if(!p.hasPermission("supersuporte.staff")) {
                return true;
            }
            
            if(args.length == 0) {
                List<String> no_args = configs.getConfig().getStringList("messages.responder.no_args");
                p.sendMessage(prefix+Separator(no_args));
                Play(p, 5);
                return true;
            }
            
            String reporter = args[0];
            String mensagem = Mensagem2(args);
            Player players = Bukkit.getPlayer(reporter);
            if(reports.getConfig().getString("Player."+reporter.toLowerCase()) != null) {
                if(!players.isOnline()) {
                    p.sendMessage(prefix+configs.getConfig().getString("messages.responder.offline").replaceAll("&", "§"));
                    Play(p, 5);
                    return true;
                }
                String duvida = reports.getConfig().getString("Player."+reporter.toLowerCase()+".duvida");
                String resp = configs.getConfig().getString("messages.responder.player.message").replaceAll("&", "§");
                resp = resp.replaceAll("<duvida>", reports.getConfig().getString("Player."+reporter.toLowerCase()+".duvida"));
                resp = resp.replaceAll("<staff>", p.getDisplayName());
                resp = resp.replaceAll("<resposta>", mensagem);
                players.sendMessage(prefix+resp);
                
                Play(players, 4);
                PlayResp(p);
                
                reports.set("Player."+reporter.toLowerCase(), null);
                reports.saveConfig();
                
                for(Player plsa : Bukkit.getOnlinePlayers()) {
                    if(plsa.hasPermission("supersuporte.staff")) { // Staffs
                        if(!configs.getConfig().getBoolean("messages.responder.staffs.resp-message.show")) return true;
                        
                        String content = Separator(configs.getConfig().getStringList("messages.responder.staffs.resp-message.message"));
                        
                        content = content.replaceAll("<duvida>", duvida);
                        content = content.replaceAll("<staff>", p.getDisplayName()); 
                        content = content.replaceAll("<resposta>", mensagem);
                        content = content.replaceAll("<player>", players.getDisplayName());
                        
                        TextComponent msg = new TextComponent(prefix+content);
                        
                        if(configs.getConfig().getBoolean("messages.responder.staffs.mousehover.allow")) {
                            String mousehover = configs.getConfig().getString("messages.responder.staffs.mousehover.text").replaceAll("&", "§");
                            
                            mousehover = mousehover.replaceAll("<duvida>", duvida);
                            mousehover = mousehover.replaceAll("<staff>", p.getDisplayName()); 
                            mousehover = mousehover.replaceAll("<resposta>", mensagem);
                            mousehover = mousehover.replaceAll("<player>", players.getDisplayName());
                            
                            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(mousehover).create()));
                        }
                        plsa.spigot().sendMessage(msg);
                    }else { // Players
                        if(!configs.getConfig().getBoolean("messages.responder.players.resp-message.show")) return true;
                        
                        String content = Separator(configs.getConfig().getStringList("messages.responder.players.resp-message.message"));
                        
                        content = content.replaceAll("<duvida>", duvida);
                        content = content.replaceAll("<staff>", p.getPlayerListName());
                        content = content.replaceAll("<resposta>", mensagem);
                        content = content.replaceAll("<player>", players.getPlayerListName());
                        
                        TextComponent msg = new TextComponent(prefix+content);
                        
                        if(configs.getConfig().getBoolean("messages.responder.players.mousehover.allow")) {
                            String mousehover = configs.getConfig().getString("messages.responder.players.mousehover.text").replaceAll("&", "§");
                            
                            mousehover = mousehover.replaceAll("<duvida>", duvida);
                            mousehover = mousehover.replaceAll("<staff>", p.getPlayerListName());
                            mousehover = mousehover.replaceAll("<resposta>", mensagem);
                            mousehover = mousehover.replaceAll("<player>", players.getPlayerListName());
                            
                            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(mousehover).create()));
                        }
                        plsa.spigot().sendMessage(msg);
                    }
                }
            }
            
        }


        return false;
    }
    
    private ItemStack nameItemi(ItemStack item, String name, List lore, String owner) {
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        sm.setOwner(owner);
        sm.setLore(lore);
        sm.setDisplayName(name);
        
        item.setDurability((short)3);
        item.setItemMeta(sm);
        
        return item;
    }
    
    
    public String Separator(List lista) {
        String result = String.join("\n", lista);
        result = result.replaceAll("&", "§");
        return result;
    }
    
    public String duvida(int i, Player p, String msg) {
        List<String> base;
        List<String> fim = new ArrayList();
        
        if(i == 0) {
            base = configs.getConfig().getStringList("messages.duvida.staff.message");
        } else {
            base = configs.getConfig().getStringList("messages.duvida.player.message");
        }
        
        for(String line : base) {
            String linha = line.toLowerCase();
            if(linha.length() < 2) {
                fim.add(line);
            } else {
                line = prefix+line;   
                fim.add(line);
            }
        }
        
        String result = Separator(fim);
        result = result.replaceAll("<player>", p.getDisplayName()).replaceAll("<duvida>", msg);
        return result;
    }
    
    private void PlayResp(final Player p) {
        Play(p, 0);
        new BukkitRunnable() {
            public void run() {
                Play(p, 1);
                new BukkitRunnable() {
                    public void run() {
                        Play(p, 2);
                    }
                }.runTaskLater(SuperSuporte.plugin, 2);
            }
        }.runTaskLater(SuperSuporte.plugin, 2);
    }
    
    private void Play(Player p, int n) {
        
        // Quando staff envia uma resposta 0 a 2
        // Quando staff recebe uma duvida 3
        // Quando player recebe uma resposta 4
        // Quando player executa errado o comando 5
        
        
        if(n == 0) // 0
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 1, (float) 1.5); //
        else if (n == 1) // 1
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 1, (float) 1.7); //
        else if (n == 2) // 2
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 1, (float) 1.3); //
        else if (n == 3) // 3
            p.playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1, 1); //
        else if (n == 4) // 4
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1); //
        else // 5 ou mais
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 0); //
    }

}
