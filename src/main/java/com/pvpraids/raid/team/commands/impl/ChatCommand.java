package com.pvpraids.raid.team.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.team.commands.TeamSubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatCommand extends TeamSubCommand {
	public ChatCommand() {
		super("chat", true, false, "c");
	}

	@Override
	public void onCommand(Player player, String label, String[] args) {
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		rp.setTeamChat(!rp.isTeamChat());
		player.sendMessage(ChatColor.YELLOW + "You have " + (rp.isTeamChat() ? "begun" : "left") + " team chat only mode.");
	}
}
