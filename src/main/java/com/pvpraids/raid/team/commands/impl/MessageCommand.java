package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageCommand extends TeamSubCommand {
	public MessageCommand() {
		super("message", true, false, "msg", "m");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		if (args.length == 0) {
			player.sendMessage(ChatColor.RED + "You must specify a message!");
			return;
		}

		StringBuilder message = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			message.append(args[i]);
			message.append(" ");
		}

		RaidPlugin.getInstance().getTeamManager().getTeam(player).sendMessage(player, message.toString());
	}
}
