package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import org.bukkit.entity.Player;

public class WarpsCommand extends PlayerCommand {
	public WarpsCommand() {
		super("warps");
		setAliases("gos");
	}

	@Override
	public void execute(Player player, String[] strings) {
		player.performCommand("warp list");
	}
}
