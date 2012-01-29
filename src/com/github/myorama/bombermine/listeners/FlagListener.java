package com.github.myorama.bombermine.listeners;

import com.github.myorama.bombermine.Bombermine;
import com.github.myorama.bombermine.models.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class FlagListener implements Listener {
	
	private Bombermine plugin;

	public FlagListener(Bombermine instance) {
		this.plugin = instance;
	}
	
	/**
	 * Flag management on PlayerPickupItemEvent
	 * @param event
	 */
	@EventHandler
	public void pickUpFlag(PlayerPickupItemEvent event) {
		// rooting event to call a CTFGame synchronized method
		this.plugin.getCtfGame().pickUpFlag(event);
	}
	
	/**
	 * No flag break for owner team and spectator
	 * @param event
	 */
	@EventHandler
	public void preventFlagBreak(BlockBreakEvent event) {

		Player player = event.getPlayer();
		Block block = event.getBlock();

		Team team = plugin.getCtfGame().getPlayerTeam(player);
		
		if(!this.plugin.getCtfGame().isStarted()){
			if(player.getGameMode() != GameMode.CREATIVE){
				event.setCancelled(true);
				player.sendMessage(String.format("%sYou cannot interract while the game is stopped. Please start the game or get Creative mode", ChatColor.RED));
				return;
			}
		}
		else if(team == null){
			if(player.getGameMode() != GameMode.CREATIVE){
				event.setCancelled(true);
				player.sendMessage(String.format("%sYou cannot interract while in spectator. Please join a team or get Creative mode", ChatColor.RED));
				return;
			}
		}
		
		if(block.getType() == Material.WOOL){
			Team flagTeam = this.plugin.getCtfGame().getTeamByFlag(block);
			if(flagTeam != null){
				if(player.getGameMode() == GameMode.CREATIVE){
					event.setCancelled(true);
					player.sendMessage(String.format("%sPlease use /bm team flag <color> to change flag position", ChatColor.RED));
				}
				else if(team == flagTeam){ // trying to break own team flag
					player.sendMessage(String.format("%sYou cannot break your own flag", ChatColor.RED));
					event.setCancelled(true);
				}
			}else{ // No drop for wool blocks that are not a flag
				event.getBlock().setType(Material.AIR);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void changingGameMode(PlayerGameModeChangeEvent event){
		if(event.getNewGameMode() == GameMode.SURVIVAL && plugin.getCtfGame().isStarted()){
			Player player = event.getPlayer();
			Team team = plugin.getCtfGame().getPlayerTeam(player);
			if(team != null){
				player.teleport(team.getSpawnLoc());
			}else{
				player.teleport(plugin.getCtfGame().getDefaultSpawn());
			}
		}
	}
	
	@EventHandler
	public void leavingRunner(PlayerQuitEvent event){
		Player player = event.getPlayer();
		this.plugin.getCtfGame().removePlayer(player);
	}
	
	@EventHandler
	public void preventFlagSwitch(PlayerItemHeldEvent event) {
		// TODO check if player doesnt delong to flag's team
	}
}
