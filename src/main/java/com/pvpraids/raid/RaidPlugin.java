package com.pvpraids.raid;

import com.pvpraids.admin.util.AdminUtil;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.raid.commands.HatCommand;
import com.pvpraids.raid.commands.HelpCommand;
import com.pvpraids.raid.commands.HomeCommand;
import com.pvpraids.raid.commands.KitCommand;
import com.pvpraids.raid.commands.LeaderboardCommand;
import com.pvpraids.raid.commands.RulesCommand;
import com.pvpraids.raid.commands.ScoreboardCommand;
import com.pvpraids.raid.commands.SetHomeCommand;
import com.pvpraids.raid.commands.SetSpawnCommand;
import com.pvpraids.raid.commands.SpawnCommand;
import com.pvpraids.raid.commands.StatsCommand;
import com.pvpraids.raid.commands.SuicideCommand;
import com.pvpraids.raid.commands.WarpsCommand;
import com.pvpraids.raid.commands.staff.ClearEntitiesCommand;
import com.pvpraids.raid.commands.staff.LastInvCommand;
import com.pvpraids.raid.commands.staff.NearModCommand;
import com.pvpraids.raid.economy.Economy;
import com.pvpraids.raid.economy.commands.impl.BalanceCommand;
import com.pvpraids.raid.economy.commands.impl.BuyCommand;
import com.pvpraids.raid.economy.commands.impl.DepositCommand;
import com.pvpraids.raid.economy.commands.impl.HoldingCommand;
import com.pvpraids.raid.economy.commands.impl.PriceCommand;
import com.pvpraids.raid.economy.commands.impl.SellCommand;
import com.pvpraids.raid.economy.commands.impl.WithdrawCommand;
import com.pvpraids.raid.leaderboard.LeaderboardManager;
import com.pvpraids.raid.listeners.CombatLogListener;
import com.pvpraids.raid.listeners.FastCookListener;
import com.pvpraids.raid.listeners.MobCaptureListener;
import com.pvpraids.raid.listeners.OldEnchantListener;
import com.pvpraids.raid.listeners.PearlCooldownListener;
import com.pvpraids.raid.listeners.PickaxeStatsListener;
import com.pvpraids.raid.listeners.PlayerListener;
import com.pvpraids.raid.listeners.SalvageListener;
import com.pvpraids.raid.listeners.ScoreboardListener;
import com.pvpraids.raid.listeners.SoupListener;
import com.pvpraids.raid.listeners.VanishListener;
import com.pvpraids.raid.listeners.WorldListener;
import com.pvpraids.raid.listeners.XPListener;
import com.pvpraids.raid.listeners.fixes.DisabledPotionListener;
import com.pvpraids.raid.listeners.fixes.NetherLimitListener;
import com.pvpraids.raid.listeners.fixes.PortalFixListener;
import com.pvpraids.raid.listeners.fixes.StrengthFixListener;
import com.pvpraids.raid.packet.ExploitBlocker;
import com.pvpraids.raid.packet.NoclipPacketManager;
import com.pvpraids.raid.players.RaidPlayer;
import com.pvpraids.raid.players.RaidPlayerManager;
import com.pvpraids.raid.salvaging.SalvageManager;
import com.pvpraids.raid.storage.RaidConfig;
import com.pvpraids.raid.task.BlockClearTask;
import com.pvpraids.raid.task.WarpTask;
import com.pvpraids.raid.team.TeamManager;
import com.pvpraids.raid.team.commands.TeamCommand;
import com.pvpraids.raid.tracking.TrackCommand;
import com.pvpraids.raid.warps.WarpCommand;
import com.pvpraids.raid.warps.WarpManager;
import com.pvpraids.scoreboardapi.api.CustomScoreboard;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class RaidPlugin extends JavaPlugin {
	@Getter
	private static RaidPlugin instance;

	private final Map<UUID, WarpTask> warping = new HashMap<>();
	private final Map<Block, Long> blocksPlacedNearSpawn = new HashMap<>();

	private RaidPlayerManager playerManager;
	private Economy economy;
	private WarpManager warpManager;
	private TeamManager teamManager;
	private SalvageManager salvageManager;
	private LeaderboardManager leaderboardManager;
	private CustomScoreboard raidScoreboard;
	private RaidConfig raidConfig;

	@Setter
	private Location spawn;

	@Override
	public void onEnable() {
		instance = this;

		playerManager = new RaidPlayerManager();
		economy = new Economy(this);
		warpManager = new WarpManager();
		teamManager = new TeamManager();
		salvageManager = new SalvageManager(this);
		leaderboardManager = new LeaderboardManager(this);
		raidConfig = new RaidConfig();
		spawn = raidConfig.getLocation("spawn");

		registerCommands(
				new ScoreboardCommand(this), new SetHomeCommand(), new HomeCommand(),
				new SpawnCommand(), new TrackCommand(), new WarpCommand(this),
				new TeamCommand(), new BalanceCommand(), new BuyCommand(),
				/*new CountCommand(),*/ new DepositCommand(), new HoldingCommand(),
				new PriceCommand(), new SellCommand(), new WithdrawCommand(),
				/*new TestKitCommand(),*/ new LastInvCommand(this), new StatsCommand(this),
				new LeaderboardCommand(this), new HelpCommand(), new NearModCommand(),
				new ClearEntitiesCommand(), new SetSpawnCommand(), new SuicideCommand(),
				new WarpsCommand(), new RulesCommand(), new HatCommand(), new KitCommand()
		);

		registerListeners(
				new MobCaptureListener(this), new PlayerListener(this), new SalvageListener(this),
				new SoupListener(), new WorldListener(this), new XPListener(), new StrengthFixListener(),
				new ScoreboardListener(this), new FastCookListener(this), new PickaxeStatsListener(),
				new CombatLogListener(this), new NetherLimitListener(), new PearlCooldownListener(this),
				new VanishListener(this), new DisabledPotionListener(), new OldEnchantListener(),
				new PortalFixListener()
		);

		// Create recipe for XP bottle
		ItemStack item = new ItemBuilder(Material.GLASS_BOTTLE).name(XPListener.XP_BOTTLE_EMPTY).lore(XPListener.XP_BOTTLE_LORE).build();
		getServer().addRecipe(new ShapelessRecipe(item).addIngredient(1, Material.GLASS_BOTTLE));

		// soup recipes
		getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP))
				.addIngredient(1, Material.BOWL)
				.addIngredient(1, Material.CACTUS));
		getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP))
				.addIngredient(1, Material.BOWL)
				.addIngredient(1, Material.COCOA));

		NoclipPacketManager.init();
		new ExploitBlocker(this);

		new BlockClearTask(this).runTaskTimer(this, 20 * 5, 20 * 5);

		raidScoreboard = new CustomScoreboard(this, 2);

		// async update tasks
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			leaderboardManager.updateAll();
			playerManager.getPlayers().values().forEach(RaidPlayer::save);

			AdminUtil.tellStaff(Rank.ADMIN, CC.RED + "Updating leaderboard, saving player data...");
		}, 20 * 60, 20 * 60 * 15);
	}

	@Override
	public void onDisable() {
		economy.getMarket().save();

		playerManager.getPlayers().values().forEach(RaidPlayer::save);

		teamManager.getTeams().forEach(team -> teamManager.saveTeam(team));

		Set<UUID> toSave = new HashSet<>(warpManager.getWarps().keySet());
		toSave.forEach(uuid -> warpManager.saveWarps(uuid));

		raidConfig.save();

		warping.clear();
		blocksPlacedNearSpawn.clear();
	}

	private void registerCommands(Command... commands) {
		try {
			Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
			final boolean accessible = commandMapField.isAccessible();

			commandMapField.setAccessible(true);

			CommandMap commandMap = (CommandMap) commandMapField.get(getServer());

			for (Command command : commands) {
				commandMap.register(command.getName(), getName(), command);
			}

			commandMapField.setAccessible(accessible);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			getLogger().severe("An error occurred while registering commands.");
			e.printStackTrace();
		}
	}

	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}
}
