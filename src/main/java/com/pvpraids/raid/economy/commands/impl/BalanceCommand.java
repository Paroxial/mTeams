package com.pvpraids.raid.economy.commands.impl;

import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.commands.EconomyCommand;
import com.pvpraids.raid.players.RaidPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BalanceCommand extends EconomyCommand {
	public BalanceCommand() {
		super("balance");
		setAliases("bal", "money");
	}

	@Override
	public void onCommand(Player player, String[] args) {
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);
		player.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.ITALIC + RaidPlugin.getInstance().getEconomy().toGoldString(rp.getBalance()));
	}
}
