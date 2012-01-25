/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.myorama.bombermine.commandexecutors;

import com.github.myorama.bombermine.Bombermine;
import com.github.myorama.bombermine.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Nittero
 */
public class BombermineCommandExecutor implements CommandExecutor {

	private Bombermine plugin;
	private final String ERROR_PLAYER_ONLY = ChatColor.RED + "You must be a player to do this.";
	private final String ERROR_UNAUTHORIZED = ChatColor.RED + "You have not the right to do this";

	public BombermineCommandExecutor(Bombermine instance) {
		this.plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO show help for command and manage sub commands
		// TODO register permissions for each command
		// TODO test if sender is console or player
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		ChatColor msgColor = ChatColor.GREEN;
		ChatColor errColor = ChatColor.RED;
		ChatColor bcColor = ChatColor.GOLD;

		// TODO a simplifier (permissions user/moderator/admin)
		
		if (args.length == 0) {
			if (player != null) {
				if(hasModRights(player)){
					player.sendMessage(msgColor + "/bm join | join [player] <team> | leave [player]");
					player.sendMessage(msgColor + "/bm leave [player]");
					player.sendMessage(msgColor + "/bm start|stop|restart");
				}else if(hasPlayerRights(player)){
					player.sendMessage(msgColor + "/bm join [team]");
					player.sendMessage(msgColor + "/bm leave");
				}
				if (hasAdminRights(player)) {
					player.sendMessage(msgColor + "/bm setspawn <team>");
				}
			} else {
				sender.sendMessage(msgColor + "/bm join <player> <team>");
				sender.sendMessage(msgColor + "/bm leave <player>");
				sender.sendMessage(msgColor + "/bm start|stop|restart");
			}
		} else if (args.length > 0) {
			if (args[0].equals("join")) {
				if(args.length == 1){
					if(player != null){
						if(hasPlayerRights(player)){
							Team team = this.plugin.getCtfGame().addPlayer(player);
							if(team != null){
								broadcast(bcColor + player.getName() + " joined the team: " + team.getName());
							}else{
								sender.sendMessage("Teams are full");
							}
						}
						else{
							sender.sendMessage(ERROR_UNAUTHORIZED);
						}
					}
					else{
						sender.sendMessage(ERROR_PLAYER_ONLY);
					}
				}
				else if (args.length == 2) {
					if(player != null){
						if(hasPlayerRights(player)){
							Team team = plugin.getCtfGame().getTeamById(args[1]);
							if(team == null){
								sender.sendMessage(errColor + "Team does not exist");
							}else{
								if(team.addPlayer(player)){
									broadcast(bcColor + player.getName() + " joined the team: " + team.getName());
								}else{
									sender.sendMessage(errColor + args[1] + "Team is full");
								}
							}
						}
						else{
							sender.sendMessage(ERROR_UNAUTHORIZED);
						}
					}
					else{
						sender.sendMessage(ERROR_PLAYER_ONLY);
					}
				} else if (args.length == 3) {
					if(hasModRights(player)){
						Team team = plugin.getCtfGame().getTeamById(args[2]);
						if(team == null){
							sender.sendMessage(errColor + "Team does not exist");
						} else {
							Player tPlayer = Bukkit.getServer().getPlayer(args[1]);
							if(tPlayer == null){
								sender.sendMessage(errColor + "Player " + args[1] + " is not online");
							}
							else{
								if(team.addPlayer(tPlayer)){
									broadcast(msgColor + args[1] + " joined the team: " + team.getName());
								}else{
									sender.sendMessage(errColor + "Team is full");
								}
							}
						}
					}
					else{
						sender.sendMessage(ERROR_UNAUTHORIZED);
					}
				} else {
					if(player == null){
						if(hasModRights(player)){
							player.sendMessage(msgColor + "/bm join | join [player] <team>");
						}else if(hasPlayerRights(player)){
							player.sendMessage(msgColor + "/bm join [team]");
						}
					} else {
						sender.sendMessage(msgColor + "/bm join <player> <team>");
					}
				}
			}
		}
		return true;
	}
	
	private boolean hasPlayerRights(Player player){
		if(player == null){
			return true;
		}else{
			return player.hasPermission("bombermine.player") || hasModRights(player);
		}
	}
	
	private boolean hasModRights(Player player){
		if(player == null){
			return true;
		}else{
			return player.hasPermission("bombermine.moderator") || hasAdminRights(player);
		}
	}
	
	private boolean hasAdminRights(Player player){
		if(player == null){
			return true;
		}else{
			return player.hasPermission("bombermine.admin");
		}
	}
	
	private void broadcast(String msg){
		Bukkit.getServer().getConsoleSender().sendMessage(msg);
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			player.sendMessage(msg);
		}
	}
}
