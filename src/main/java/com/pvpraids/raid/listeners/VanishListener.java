package com.pvpraids.raid.listeners;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class VanishListener implements Listener {
	private final RaidPlugin plugin;
	private final Map<UUID, Block> openedChests = new HashMap<>();

	@EventHandler
	public void onSilentChestOpen(PlayerInteractEvent event) {
		if (!event.hasBlock() || event.getClickedBlock().getType() != Material.CHEST || !event.getAction().name().contains("RIGHT")) {
			return;
		}

		Player player = event.getPlayer();
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (profile.isVanished()) {
			event.setCancelled(true);
			BlockState state = event.getClickedBlock().getState();
			InventoryHolder holder = ((Chest) state).getInventory().getHolder();
			Inventory inventory;

			if (holder instanceof DoubleChest) {
				DoubleChest chest = (DoubleChest) holder;
				inventory = chest.getInventory();
			} else {
				inventory = holder.getInventory();
			}

			// you have to create a new one or it just opens the chest normally
			Inventory newInventory = plugin.getServer().createInventory(null, inventory.getSize());
			int i = 0;

			for (ItemStack content : inventory.getContents()) {
				newInventory.setItem(i++, content);
			}

			openedChests.put(player.getUniqueId(), event.getClickedBlock());
			player.openInventory(newInventory);
			player.sendMessage(CC.YELLOW + "You opened a chest silently in vanish mode. You are now modifying its contents.");
		}
	}

	@EventHandler
	public void onSilentChestClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		if (openedChests.containsKey(player.getUniqueId())) {
			Block block = openedChests.remove(player.getUniqueId());
			BlockState state = block.getState();
			InventoryHolder holder = ((Chest) state).getInventory().getHolder();
			Inventory inventory;

			if (holder instanceof DoubleChest) {
				DoubleChest chest = (DoubleChest) holder;
				inventory = chest.getInventory();
			} else {
				inventory = holder.getInventory();
			}

			inventory.setContents(event.getInventory().getContents());
			player.sendMessage(CC.GREEN + "Saved chest contents.");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		openedChests.remove(event.getPlayer().getUniqueId());
	}
}
