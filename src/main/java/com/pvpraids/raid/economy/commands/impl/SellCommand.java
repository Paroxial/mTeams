package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.economy.material.MarketMaterial;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SellCommand extends EconomyCommand {
	public SellCommand() {
		super("sell");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length != 3) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /sell (amount) (item) (price)");
			return;
		}
		int amount;

		try {
			amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /sell (amount) (item) (price)");
			return;
		}

		if (amount < 0 || amount > 1000) {
			player.sendMessage(CC.RED + "You must specify an amount between 0 and 1,000.");
			return;
		}

		MarketMaterial material = checkMaterial(player, args[1]);
		if (material == null) {
			return;
		}

		double price;

		try {
			price = Double.parseDouble(args[2]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /sell (amount) (item) (price)");
			return;
		}

		if (price < 0 || price > 1000) {
			player.sendMessage(CC.RED + "You must specify a price between 0 and 1,000.");
			return;
		}

		RaidPlugin.getInstance().getEconomy().getMarket().sell(player, material, amount, RaidPlugin.getInstance().getEconomy().toValue(price));
	}
}
