package com.pvpraids.raid.commands.staff;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;

public class ClearEntitiesCommand extends PlayerCommand {
	public ClearEntitiesCommand() {
		super("clearentities", Rank.ADMIN);
	}

	@Override
	public void execute(Player player, String[] args) {
		Bukkit.getWorlds().forEach(world -> world.getEntities().stream()
				.filter(entity -> !(entity instanceof Player) && !(entity instanceof Hanging))
				.forEach(Entity::remove));

		player.sendMessage(CC.GREEN + "Cleared all entities.");
	}
}
