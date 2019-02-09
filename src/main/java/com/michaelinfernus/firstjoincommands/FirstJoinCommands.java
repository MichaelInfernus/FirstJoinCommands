package com.michaelinfernus.firstjoincommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"unused","StringConcatenationInLoop"})
public final class FirstJoinCommands extends JavaPlugin implements Listener, CommandExecutor {

    private List<String> commandsOnFirstJoin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("FirstJoinCommand").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean loadConfig() {
        try {
            if(!this.getDataFolder().exists())
                this.getDataFolder().mkdirs();
            this.saveDefaultConfig();
            this.reloadConfig();
            commandsOnFirstJoin = this.getConfig().getStringList("commandsOnFirstJoin");
            if(commandsOnFirstJoin == null)
                return false; //something didn't go right. It shouldn't be null
        }
        catch(Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to load config! Does it exist?");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0)
            return false;
        if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("firstjoincommands.admin")) {
            boolean success = loadConfig();
            if(success)
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
            else
                sender.sendMessage(ChatColor.RED + "Failed to reload config.");
            return true;
        }
        else if(args[0].equalsIgnoreCase("add") && sender.hasPermission("firstjoincommands.admin")) {
            if(args.length >= 2) {
                String commandToAdd = args[1];
                if(args.length >= 3) {
                    for(int i = 2; i < args.length; i++)
                        commandToAdd += " " + args[i];

                }
                if(commandsOnFirstJoin.contains(commandToAdd)) {
                    sender.sendMessage(ChatColor.YELLOW + "That command is already added.");
                    return true;
                }
                commandsOnFirstJoin.add(commandToAdd);
                this.getConfig().set("commandsOnFirstJoin", commandsOnFirstJoin);
                this.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Added first join command '" + commandToAdd + "' successfully.");
                return true;
            }
            else {
                sender.sendMessage(ChatColor.RED + "No command given to add.");
                return true;
            }
        }
        else if(args[0].equalsIgnoreCase("remove") && sender.hasPermission("firstjoincommands.admin")) {
            if(args.length >= 2) {
                String commandToAdd = args[1];
                if(args.length >= 3) {
                    for(int i = 2; i < args.length; i++)
                        commandToAdd += " " + args[i];

                }
                if(!commandsOnFirstJoin.contains(commandToAdd)) {
                    sender.sendMessage(ChatColor.RED + "That first join command does not exist.");
                    return true;
                }
                for(int i = commandsOnFirstJoin.size() - 1; i >= 0; i--){
                    if(commandsOnFirstJoin.get(i).equalsIgnoreCase(commandToAdd))
                        commandsOnFirstJoin.remove(i);
                }
                this.getConfig().set("commandsOnFirstJoin", commandsOnFirstJoin);
                this.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "Removed first join command '" + commandToAdd + "' successfully.");
                return true;
            }
            else {
                sender.sendMessage(ChatColor.RED + "No command given to add");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPlayedBefore()) {
            for(String command : commandsOnFirstJoin) {
                command = command.replaceAll("\\{name}", player.getName());
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }
    }
}