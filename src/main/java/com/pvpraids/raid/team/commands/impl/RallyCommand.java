package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.task.WarpTask;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RallyCommand extends TeamSubCommand {
	public RallyCommand() {
		super("rally", true, false);
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		Team team = RaidPlugin.getInstance().getTeamManager().getTeam(player);
		if (team.getRally() == null) {
			player.sendMessage(ChatColor.RED + "Your team doesn't have a rally set!");
			return;
		}

		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);
		if (rp.isSpawnProtected()) {
			player.sendMessage(CC.RED + "You can't teleport to your team's rally from spawn!");
			return;
		}

		new WarpTask(player, team.getRally());
	}
}
