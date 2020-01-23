package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FriendlyFireCommand extends TeamSubCommand {
	public FriendlyFireCommand() {
		super("friendlyfire", true, true, "ff");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);
		team.setFriendlyFire(!team.isFriendlyFire());

		team.sendAnonymousMessage(ChatColor.YELLOW + "Friendly fire is now " + (team.isFriendlyFire() ? "on" : "off"));
	}
}
