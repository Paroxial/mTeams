package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.util.LocationUtil;
import org.bukkit.entity.Player;

public class SetHomeCommand extends PlayerCommand {
	public SetHomeCommand() {
		super("sethome");
	}

	@Override
	public void execute(Player player, String[] strings) {
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (LocationUtil.isCloseToSpawn(player.getLocation())) {
			player.sendMessage(CC.RED + "You cannot set your home this close to spawn.");
			return;
		}

		rp.setHome(player.getLocation());
		player.sendMessage(CC.GOLD + "Your home has been set.");
	}
}
