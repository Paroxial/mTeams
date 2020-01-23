package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HoldingCommand extends EconomyCommand {
	public HoldingCommand() {
		super("holding");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		MarketMaterial material = Materials.get(player.getItemInHand());
		if (material == null) {
			player.sendMessage(ChatColor.GREEN + "You're not holding anything.");
		} else {
			player.sendMessage(ChatColor.GREEN + "You're holding " + material.toString() + ".");
		}
	}
}
