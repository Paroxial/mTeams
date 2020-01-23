package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.Economy;
import com.pvpraids.raid.economy.EconomyUtil;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.players.RaidPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DepositCommand extends EconomyCommand {
	public DepositCommand() {
		super("deposit");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		if (args.length != 1) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /deposit (amount)");
			return;
		}

		int amount;

		try {
			amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Incorrect syntax. Use /deposit (amount)");
			return;
		}

		if (amount < 1 || amount > player.getInventory().getSize() * player.getInventory().getMaxStackSize()) {
			player.sendMessage(CC.RED + "You must enter a valid amount.");
			return;
		}

		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		int count = EconomyUtil.count(player.getInventory(), Economy.UNIT_GOLD_MATERIAL);

		if (count < amount) {
			player.sendMessage(ChatColor.RED + "You don't have " + amount + " gold ingots to deposit, you only have " + count + ".");
			return;
		}

		EconomyUtil.remove(player.getInventory(), Economy.UNIT_GOLD_MATERIAL, amount);
		player.updateInventory();
		rp.addBalance(RaidPlugin.getInstance().getEconomy().toValue(amount));
		player.sendMessage(ChatColor.GREEN + "Deposited " + amount + " gold ingots.");
		player.sendMessage(ChatColor.GREEN + "New Balance: " + RaidPlugin.getInstance().getEconomy().toGoldString(rp.getBalance()));
	}
}
