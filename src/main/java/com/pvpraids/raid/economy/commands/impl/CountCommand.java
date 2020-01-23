package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.raid.economy.EconomyUtil;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CountCommand extends EconomyCommand {
	public CountCommand() {
		super("count");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length == 0) {
			for (MarketMaterial material : Materials.getAllNamed()) {
				int count = EconomyUtil.count(player.getInventory(), material);
				if (count != 0) {
					player.sendMessage(ChatColor.GREEN + "You have " + count + " " + material.toString() + " in your inventory.");
				}
			}
			return;
		}

		for (String alias : args) {
			MarketMaterial material = checkMaterial(player, alias);
			if (material == null) {
				continue;
			}

			player.sendMessage(ChatColor.GREEN + "You have " + EconomyUtil.count(player.getInventory(), material) + " " + material.toString() + " in your inventory.");
		}
	}
}
