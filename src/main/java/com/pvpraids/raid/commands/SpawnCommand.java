package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.task.WarpTask;
import org.bukkit.entity.Player;

public class SpawnCommand extends PlayerCommand {
	public SpawnCommand() {
		super("spawn");
	}

	@Override
	public void execute(Player player, String[] strings) {
		new WarpTask(player, RaidPlugin.getInstance().getSpawn());
	}
}
