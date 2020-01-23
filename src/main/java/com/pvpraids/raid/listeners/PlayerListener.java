package com.pvpraids.raid.listeners;

import com.google.common.collect.ImmutableSet;
import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.core.utils.time.TimeUtil;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.commands.staff.NearModCommand;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.task.WarpTask;
import com.pvpraids.raid.team.Team;
import com.pvpraids.raid.util.ItemUtil;
import com.pvpraids.raid.util.LocationUtil;
import com.pvpraids.raid.util.PlayerUtil;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

// TODO: fix tag shit
@RequiredArgsConstructor
public class PlayerListener implements Listener {
	private static final int DEATHBAN_SECONDS = 60;
	private static final Set<Material> ALLOWED_INTERACTION_TYPES = ImmutableSet.of(Material.STONE_BUTTON, Material.WOOD_BUTTON,
			Material.GOLD_PLATE, Material.WOOD_PLATE, Material.STONE_PLATE, Material.IRON_PLATE);
	private final RaidPlugin plugin;

	private static boolean isYouTubeBlock(Block block) {
		return LocationUtil.isWithinSpawn(block.getLocation()) && block.getLocation().getBlockY() == 64
				&& block.getType() == Material.CARPET && block.getData() == 14;
	}

	private static boolean isTwitterBlock(Block block) {
		return LocationUtil.isWithinSpawn(block.getLocation()) && block.getLocation().getBlockY() == 64
				&& block.getType() == Material.CARPET && block.getData() == 3;
	}

	private static boolean isRedditBlock(Block block) {
		return LocationUtil.isWithinSpawn(block.getLocation()) && block.getLocation().getBlockY() == 64
				&& block.getType() == Material.CARPET && block.getData() == 6;
	}

//	private void createScoreboard(Player player) {
//		Scoreboard playerScoreboard = player.getScoreboard();
//
//		// create teams for each rank
//		for (Rank rank : Rank.values()) {
//			org.bukkit.scoreboard.Team team = playerScoreboard.registerNewTeam(rank.name());
//			team.setPrefix(rank.getColor());
//		}
//
//		CoreProfile playerProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
//		Rank playerRank = playerProfile.getVisibleRank();
//
//		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
//			if (onlinePlayer == player) {
//				continue;
//			}
//			// add all online players to the joiner's respective rank teams
//			CoreProfile onlineProfile = CorePlugin.getInstance().getProfileManager().getProfile(onlinePlayer.getUniqueId());
//
//			if (onlineProfile == null) {
//				continue;
//			}
//
//			Rank onlineRank = onlineProfile.getVisibleRank();
//			org.bukkit.scoreboard.Team playerTeam = playerScoreboard.getTeam(onlineRank.name());
//
//			playerTeam.addEntry(onlinePlayer.getName());
//
//			// add the joiner to all online players' respective rank teams
//			org.bukkit.scoreboard.Team onlineTeam = onlinePlayer.getScoreboard().getTeam(playerRank.name());
//
//			onlineTeam.addEntry(player.getName());
//		}
//	}

//	private void updateScoreboardRank(Player player, Rank oldRank, Rank newRank) {
//		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
//			if (onlinePlayer == player) {
//				continue;
//			}
//
//			// add the rank-changed player to the online player's new rank team
//			org.bukkit.scoreboard.Team oldTeam = onlinePlayer.getScoreboard().getTeam(oldRank.name());
//
//			oldTeam.removeEntry(player.getName());
//
//			org.bukkit.scoreboard.Team newTeam = onlinePlayer.getScoreboard().getTeam(newRank.name());
//
//			newTeam.addEntry(player.getName());
//		}
//	}
//
//	@EventHandler
//	public void onRankChange(PlayerRankChangeEvent event) {
//		Player player = event.getPlayer();
//		Rank oldRank = event.getOldRank(), newRank = event.getNewRank();
//
//		updateScoreboardRank(player, oldRank, newRank);
//	}
//
//	@EventHandler
//	public void onTagChange(PlayerTagChangeEvent event) {
//		Player player = event.getPlayer();
//		CoreProfile profile = event.getProfile();
//		Rank oldTag = event.getOldTag(), newTag = profile.getVisibleRank();
//
//		updateScoreboardRank(player, oldTag, newTag);
//	}

	private static boolean isDiscordBlock(Block block) {
		return LocationUtil.isWithinSpawn(block.getLocation()) && block.getLocation().getBlockY() == 64
				&& block.getType() == Material.CARPET && block.getData() == 11;
	}

	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			RaidPlayer rp = plugin.getPlayerManager().addPlayer(event.getUniqueId());
			rp.load();

			long lastDeathBan = rp.getLastDeathBan();
			long wait = DEATHBAN_SECONDS * 1000L;
			long diff = System.currentTimeMillis() - lastDeathBan;

			if (diff <= wait) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
						CC.RED + "You are deathbanned for another " + TimeUtil.formatTimeMillis(wait - diff) + "." +
								"\n" + CC.GOLD + "Bypass this by buying a rank at " + CC.YELLOW + "store.pvpraids.com");
				plugin.getPlayerManager().removePlayer(event.getUniqueId());
			}
		}
	}

//	private void destroyScoreboard(Player player) {
//		CoreProfile playerProfile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
//		Rank playerRank = playerProfile.getVisibleRank();
//
//		for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
//			if (onlinePlayer == player) {
//				continue;
//			}
//
//			// remove the leaver from all online players' respective rank teams (we don't care about the leaver's scoreboard)
//			org.bukkit.scoreboard.Team onlineTeam = onlinePlayer.getScoreboard().getTeam(playerRank.name());
//
//			onlineTeam.removeEntry(player.getName());
//		}
//	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		RaidPlayer rp = RaidPlugin.getInstance().getPlayerManager().getPlayer(player);

		if (rp == null) {
			player.kickPlayer(ChatColor.RED + "There was an error loading your player data.");
			return;
		}

		if (!LocationUtil.isWithinSpawn(player.getLocation())) {
			rp.loseSpawnProtection();
		}

		plugin.getWarpManager().loadWarps(player.getUniqueId());

		// load last saved inventory
		if (rp.getLastSavedInventory() != null) {
			try {
				player.getInventory().setContents(PlayerUtil.fromBase64(rp.getLastSavedInventory()).getContents());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		if (rp.isFirstLogin()) {
			player.teleport(RaidPlugin.getInstance().getSpawn());
			// todo give them a book and shit
			rp.setFirstLogin(false);
		}

//		createScoreboard(player);

		player.sendMessage(CC.SEPARATOR);
		player.sendMessage(CC.GREEN + "Welcome to " + CC.ACCENT + CC.B + "PvPRaids Teams"
				+ CC.PRIMARY + " (Map 1)" + CC.GREEN + "!");
		player.sendMessage("");
		player.sendMessage(CC.PRIMARY + "For more info, type " + CC.SECONDARY + "/help" + CC.PRIMARY + ".");
		player.sendMessage(CC.SEPARATOR);

		player.sendMessage("");
		player.sendMessage(CC.YELLOW + "WE HAVE TEMPORARILY ADDED /KIT " + CC.RED + CC.B + "UNTIL NEXT MAP! " + CC.GOLD + "(coming soon!)");
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(RaidPlugin.getInstance().getSpawn());

		Player player = event.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		rp.gainSpawnProtection();
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		rp.setLastCombatTag(0);

		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
//
//		if (profile.getRank() == Rank.DEFAULT) {
//			rp.setLastDeathBan(System.currentTimeMillis());
//
//			// we have to kick a tick after or items won't drop
//			plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
//				if (player.isOnline()) {
//					player.kickPlayer(CC.RED + "You have been deathbanned! Come back again in 60 seconds.\n" +
//							CC.GOLD + "Bypass this by buying a rank at " + CC.YELLOW + "store.pvpraids.com");
//				}
//			}, 1L);
//			return;
//		}

		// respawn them in-case they are stuck on the respawn screen
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			if (player.isOnline()) {
				player.spigot().respawn();
			}
		}, 10 * 20L);
	}

	private void onDisconnect(Player player) {
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		if (rp == null) {
			return;
		}

		NearModCommand.chunks.remove(player.getName());
		player.removeMetadata("nearmod", RaidPlugin.getInstance());

//		destroyScoreboard(player);
		rp.setLastSavedInventory(PlayerUtil.toBase64(player.getInventory()));

		plugin.getPlayerManager().removePlayer(player);
		plugin.getWarpManager().saveWarps(player.getUniqueId());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		onDisconnect(player);
	}

	@EventHandler
	public void onKick(PlayerKickEvent e) {
		Player player = e.getPlayer();
		onDisconnect(player);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
		Location to = e.getTo();
		Location from = e.getFrom();

		if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}

		if (!isYouTubeBlock(from.getBlock()) && isYouTubeBlock(to.getBlock())) {
			player.sendMessage(CC.PRIMARY + "Subscribe to our community YouTube channel: " + CC.SECONDARY + "youtube.com/channel/UC6Jf785i4HK3JyM8vV4Nllw");
		} else if (!isTwitterBlock(from.getBlock()) && isTwitterBlock(to.getBlock())) {
			player.sendMessage(CC.PRIMARY + "Follow us on Twitter: " + CC.SECONDARY + "twitter.com/PvPRaidsNetwork");
		} else if (!isRedditBlock(from.getBlock()) && isRedditBlock(to.getBlock())) {
			player.sendMessage(CC.PRIMARY + "Join our Reddit community: " + CC.SECONDARY + "reddit.com/r/pvpraids");
		} else if (!isDiscordBlock(from.getBlock()) && isDiscordBlock(to.getBlock())) {
			player.sendMessage(CC.PRIMARY + "Join our Discord community: " + CC.SECONDARY + "discord.gg/UhRmHkv");
		}

		if (plugin.getWarping().containsKey(player.getUniqueId())) {
			WarpTask task = plugin.getWarping().remove(player.getUniqueId());
			task.cancel();
			player.sendMessage(ChatColor.GRAY + "Warp canceled!");
		}

		if (!LocationUtil.isWithinSpawn(to)) {
			if (rp.isSpawnProtected()) {
				rp.loseSpawnProtection();
			}
		}

		if (to.getBlockX() >= LocationUtil.BORDER || to.getBlockX() <= -LocationUtil.BORDER || to.getBlockZ() >= LocationUtil.BORDER || to.getBlockZ() <= -LocationUtil.BORDER) {
			e.setCancelled(true);
			player.teleport(from);
			player.sendMessage(ChatColor.RED + "You hit the border!");
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

		if (!LocationUtil.isWithinSpawn(player.getLocation()) && rp.isSpawnProtected()) {
			rp.loseSpawnProtection();
		}
	}

	@EventHandler
	public void onInteractWithBlock(PlayerInteractEvent event) {
		if (!event.hasBlock() || ALLOWED_INTERACTION_TYPES.contains(event.getClickedBlock().getType())) {
			return;
		}

		Player player = event.getPlayer();
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (profile.hasStaff()) {
			return;
		}

		if (LocationUtil.isWithinSpawn(event.getClickedBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
		Team team = plugin.getTeamManager().getTeam(player);

		if (team != null && rp.isTeamChat()) {
			team.sendMessage(player, e.getMessage());
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) e.getEntity();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);

//		System.out.println(rp.getName() + " " + rp.isSpawnProtected());

		if (rp.isSpawnProtected()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onHungerDeplete(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		RaidPlayer rp = plugin.getPlayerManager().getPlayer(player);
		int previousLevel = player.getFoodLevel();

		if (rp.isSpawnProtected() && event.getFoodLevel() <= previousLevel) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageEntity(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}

		if (e.getEntity() instanceof Player) {
			Player damager = (Player) e.getDamager();
			Player victim = (Player) e.getEntity();
			Team team = plugin.getTeamManager().getTeam(damager);

			if (team != null && team.isOnTeam(victim)) {
				if (!team.isFriendlyFire()) {
					e.setCancelled(true);
				}
			}

			RaidPlayer dp = plugin.getPlayerManager().getPlayer(damager);
			RaidPlayer vp = plugin.getPlayerManager().getPlayer(victim);

			dp.setLastCombatTag(System.currentTimeMillis());
			vp.setLastCombatTag(System.currentTimeMillis());

			if (dp.isSpawnProtected() && !vp.isSpawnProtected()) {
				dp.loseSpawnProtection();
			}
		} else if (e.getEntity() instanceof ItemFrame) {
			ItemFrame itemFrame = (ItemFrame) e.getEntity();
			if (LocationUtil.isCloseToSpawn(itemFrame.getLocation(), LocationUtil.SPAWN_POS_BOUND)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			return;
		}

		Player killer = event.getEntity().getKiller();

		if (killer == null) {
			return;
		}

		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(killer.getUniqueId());

		if (!profile.hasDonor()) {
			return;
		}

		ItemStack heldItem = killer.getItemInHand();

		if (heldItem == null || heldItem.getType() == Material.AIR || !heldItem.containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
			return;
		}

		int level = heldItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);

		event.setDroppedExp(event.getDroppedExp() * (level * 5));
	}

	@EventHandler
	public void onPotionInteract(PlayerInteractEvent e) {
		if (!e.hasItem()) {
			return;
		}

		Player player = e.getPlayer();

		if (e.hasBlock() && e.getItem().getType() == Material.LAVA_BUCKET || e.getItem().getType() == Material.WATER_BUCKET) {
			if (LocationUtil.isWithinSpawn(e.getClickedBlock().getLocation())) {
				e.setCancelled(true);
				player.updateInventory();
			}
		}

		ItemStack item = e.getItem();

		if (item.getType() == Material.POTION) {
			Potion potion = Potion.fromItemStack(item);
			if (potion.getType() == PotionType.INVISIBILITY) {
				ItemUtil.removeItemFromInventory(item, player.getInventory());
				player.sendMessage(CC.RED + "Invisibility potions are disabled.");
			}
		}
	}
}
