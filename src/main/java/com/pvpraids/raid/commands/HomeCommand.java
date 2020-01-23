package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.task.WarpTask;
import org.bukkit.entity.Player;

public class HomeCommand extends PlayerCommand {
	public HomeCommand() {
		super("home");
	}

	@Override
	public void execute(Player player, String[] strings) {
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);
		if (rp.getHome() == null) {
			player.sendMessage(CC.RED + "You have not yet set your home!");
			return;
		}

		if (rp.isSpawnProtected()) {
			player.sendMessage(CC.RED + "You can't go home from spawn!");
			return;
		}

		new WarpTask(player, rp.getHome());
	}
}
