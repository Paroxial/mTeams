package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import com.pvpraids.raid.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetRallyCommand extends TeamSubCommand {
	public SetRallyCommand() {
		super("setrally", true, true);
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);

		if (LocationUtil.isCloseToSpawn(player.getLocation())) {
			player.sendMessage(ChatColor.RED + "You cannot set your rally this close to spawn.");
			return;
		}

		team.setRally(player.getLocation());
		player.sendMessage(ChatColor.YELLOW + "Rally point set.");
		team.sendAnonymousMessage(ChatColor.GRAY + "Team rally point has been updated by " + player.getName() + ".");
	}
}
