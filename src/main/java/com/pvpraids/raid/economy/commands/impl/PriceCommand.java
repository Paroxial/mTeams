package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.MarketItem;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.economy.material.MarketMaterial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PriceCommand extends EconomyCommand {
	public PriceCommand() {
		super("price");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length != 2) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /price (amount) (item)");
			return;
		}

		int amount;

		try {
			amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /price (amount) (item)");
			return;
		}

		if (amount < 0 || amount > 1000) {
			player.sendMessage(CC.RED + "You must specify an amount between 0 and 1,000.");
			return;
		}

		int amountO = amount;
		MarketMaterial material = checkMaterial(player, args[1]);
		if (material == null) {
			return;
		}

		List<MarketItem> market = new ArrayList<>(RaidPlugin.getInstance().getEconomy().getMarket().market(material));

		Collections.sort(market);

		long price = 0;
		while (!market.isEmpty() && amount > 0) {
			MarketItem item = market.get(0);
			market.remove(0);

			int itemAmount = Math.min(item.getAmount(), amount);
			if (item.getValue() > 0) {
				price += itemAmount * item.getValue();
			}
			amount -= itemAmount;
		}

		if (amount > 0) {
			player.sendMessage(ChatColor.RED + "There aren't enough " + material.toString() + " available to buy " + amountO + "!");
			return;
		}

		player.sendMessage(ChatColor.GREEN.toString() + amountO + " " + material.toString() + " would cost " + RaidPlugin.getInstance().getEconomy().toGoldString(price));
	}
}
