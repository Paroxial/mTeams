package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import org.bukkit.entity.Player;

public class SuicideCommand extends PlayerCommand {
	public SuicideCommand() {
		super("suicide");
	}

	@Override
	public void execute(Player player, String[] args) {
		player.damage(99999.0);
		if (!player.isDead()) {
			player.setHealth(0.0);
		}
		player.sendMessage(CC.RED + "You took your own life...");
	}
}
