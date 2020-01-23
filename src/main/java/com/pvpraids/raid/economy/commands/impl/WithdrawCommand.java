package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.Economy;
import com.pvpraids.raid.economy.EconomyUtil;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.players.RaidPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WithdrawCommand extends EconomyCommand {
	public WithdrawCommand() {
		super("withdraw");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /withdraw (amount)");
			return;
		}

		int amount;

		try {
			amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /withdraw (amount)");
			return;
		}

		if (amount < 1) {
			player.sendMessage(CC.RED + "You must specify a valid amount.");
			return;
		}

		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (!rp.subtractGold(amount)) {
			player.sendMessage(ChatColor.RED + "You don't have " + amount + " gold ingots to withdraw!");
			return;
		}

		EconomyUtil.give(player, Economy.UNIT_GOLD_MATERIAL, amount);
		player.sendMessage(ChatColor.GREEN + "Withdrew " + amount + " gold ingots.");
		player.sendMessage(ChatColor.GREEN + "New Balance: " + RaidPlugin.getInstance().getEconomy().toGoldString(rp.getBalance()));
	}
}
