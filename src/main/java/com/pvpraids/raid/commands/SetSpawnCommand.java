package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends PlayerCommand {
	public SetSpawnCommand() {
		super("setspawn", Rank.ADMIN);
	}

	@Override
	public void execute(Player player, String[] args) {
		Location flatLocation = player.getLocation();
		flatLocation.setX(flatLocation.getBlockX() + 0.5);
		flatLocation.setY(flatLocation.getBlockY() + 3.0);
		flatLocation.setZ(flatLocation.getBlockZ() + 0.5);
		RaidPlugin.getInstance().getRaidConfig().setSpawn(flatLocation);

		player.sendMessage(CC.GREEN + "Spawn set to your location!");
	}
}
