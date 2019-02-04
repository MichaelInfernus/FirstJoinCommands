package com.michaelinfernus.firstjoincommands;

import org.bukkit.Bukkit;
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

@SuppressWarnings("unused")
public final class FirstJoinCommands extends JavaPlugin implements Listener, CommandExecutor {

    private List<String> commandsOnFirstJoin;

    @Override
    public void onEnable() {
        // Plugin startup logic
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
        if(args[0].equalsIgnoreCase("reload") || sender.hasPermission("firstjoincommand.reload"))
            loadConfig();
        else
            return false;
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if(!player.hasPlayedBefore()) {
            for(String command : commandsOnFirstJoin) {
                Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }
    }
}
