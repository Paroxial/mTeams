package com.pvpraids.raid.economy.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.economy.Economy;
import com.pvpraids.raid.economy.material.MarketMaterial;
import com.pvpraids.raid.economy.material.Materials;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class EconomyCommand extends PlayerCommand {
	public EconomyCommand(String name) {
		super(name);
	}

	@Override
	public void execute(Player player, String[] strings) {
		if (!Economy.ENABLED) {
			player.sendMessage(ChatColor.RED + "The economy is currently disabled.");
			return;
		}

		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (!rp.isSpawnProtected() && LocationUtil.isCloseToSpawn(player.getLocation())) {
			player.sendMessage(ChatColor.RED + "You can't use economy within 512 blocks of spawn!");
			return;
		}

		onCommand(player, strings);
	}

	public abstract void onCommand(Player player, String[] args);

	protected MarketMaterial checkMaterial(Player player, String alias) {
		if (alias.equalsIgnoreCase("inhand")) {
			MarketMaterial material = Materials.get(player.getItemInHand());
			if (material == null) {
				player.sendMessage(ChatColor.RED + "That item cannot enter the economy.");
				return null;
			}

			return material;
		} else {
			MarketMaterial material = Materials.get(alias);
			if (material == null) {
				player.sendMessage(ChatColor.RED + "Material \"" + alias + "\" not recognized!");
				return null;
			}

			return material;
		}
	}
}
