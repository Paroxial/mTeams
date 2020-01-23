package com.pvpraids.raid.commands;

import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class HatCommand extends PlayerCommand {
	public HatCommand() {
		super("hat", Rank.HIGHROLLER);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length >= 1 && (args[0].equalsIgnoreCase("remove")
				|| args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset"))) {
			PlayerInventory inventory = player.getInventory();
			ItemStack hat = inventory.getHelmet();

			player.sendMessage(CC.GREEN + "Your hat has been removed!");

			// inventory is full
			if (inventory.firstEmpty() == -1) {
				player.getWorld().dropItemNaturally(player.getLocation(), hat);
				player.sendMessage(CC.YELLOW + "It was dropped on the ground because your inventory is full.");
			} else {
				inventory.addItem(hat);
			}

			inventory.setHelmet(null);
			player.updateInventory();
			return;
		}

		ItemStack hat = player.getItemInHand();

		if (hat == null || hat.getType() == Material.AIR || !hat.getType().isBlock()) {
			player.sendMessage(CC.RED + "You're not holding any hat.");
			return;
		}

		PlayerInventory inventory = player.getInventory();
		ItemStack currentHat = inventory.getHelmet();

		player.sendMessage(CC.GREEN + "You're now wearing your held item!");

		// already wearing a helmet
		if (currentHat != null && currentHat.getType() != Material.AIR) {
			// inventory is full
			if (inventory.firstEmpty() == -1) {
				player.getWorld().dropItemNaturally(player.getLocation(), currentHat);
				player.sendMessage(CC.YELLOW + "Your helmet was dropped on the ground to put on your hat.");
			} else {
				inventory.addItem(currentHat);
			}
		}

		player.setItemInHand(null);
		inventory.setHelmet(hat);
		player.updateInventory();
	}
}
