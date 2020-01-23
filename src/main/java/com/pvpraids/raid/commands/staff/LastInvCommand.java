package com.pvpraids.raid.commands.staff;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.commands.PlayerCommand;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.storage.database.MongoRequest;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.util.PlayerUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class LastInvCommand extends PlayerCommand implements Listener {
	private final RaidPlugin plugin;
	private final Map<UUID, UUID> managingInventories = new HashMap<>();

	public LastInvCommand(RaidPlugin plugin) {
		super("lastinv", Rank.MOD);
		this.plugin = plugin;
		setUsage(CC.RED + "/lastinv <player>");
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		player.sendMessage(CC.PRIMARY + "Attempting to fetch last inventory of " + args[0] + "...");

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);

			if (target == null || !target.hasPlayedBefore()) {
				player.sendMessage(CC.RED + "That player doesn't exist.");
				return;
			}

			Document storedPlayerData = CorePlugin.getInstance().getMongoStorage().getDocument("team_players", target.getUniqueId());

			if (storedPlayerData == null) {
				player.sendMessage(CC.RED + "That player doesn't have any data.");
				return;
			}

			String serializedInventory = storedPlayerData.getString("last_saved_inventory");

			if (serializedInventory == null) {
				player.sendMessage(CC.RED + "That player never had an inventory stored.");
				return;
			}

			try {
				Inventory inventory = PlayerUtil.fromBase64(serializedInventory);

				player.sendMessage(CC.GREEN + "Opening last stored inventory of " + target.getName() + "...");

				plugin.getServer().getScheduler().runTask(plugin, () -> {
					managingInventories.put(player.getUniqueId(), target.getUniqueId());
					player.openInventory(inventory);
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@EventHandler
	public void onStopManagingInv(InventoryCloseEvent event) {
		Player manager = (Player) event.getPlayer();
		UUID targetId;

		if ((targetId = managingInventories.remove(manager.getUniqueId())) == null) {
			return;
		}

		MongoRequest request = MongoRequest.newRequest("team_players", targetId)
				.put("last_saved_inventory", PlayerUtil.toBase64(event.getInventory()));

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			request.run();
			manager.sendMessage(CC.GREEN + "Successfully saved inventory.");
		});
	}
}
