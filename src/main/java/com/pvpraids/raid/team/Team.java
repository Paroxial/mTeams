package com.pvpraids.raid.team;

import com.pvpraids.raid.RaidPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Setter
@Getter
public class Team {
	public static final int MAX_PLAYERS = 20;

	private String name;
	private String password;
	private boolean friendlyFire;
	private Location hq;
	private Location rally;
	private Map<UUID, Boolean> members;

	public Team(String name, String password, boolean friendlyFire, Location hq, Location rally, Map<UUID, Boolean> members) {
		this.name = name;
		this.password = password;
		this.friendlyFire = friendlyFire;
		this.hq = hq;
		this.rally = rally;
		this.members = members;
	}

	public Team(String name, String password) {
		this(name, password, false, null, null, new HashMap<>());
	}

	public void sendAnonymousMessage(String message) {
		members.keySet().stream()
				.filter(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid) != null)
				.filter(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid).isOnline())
				.map(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid)).forEach(member ->

				member.sendMessage(message)
		);
	}

	public void sendMessage(Player sender, String message) {
		members.keySet().stream()
				.filter(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid) != null)
				.filter(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid).isOnline())
				.map(uuid -> RaidPlugin.getInstance().getServer().getPlayer(uuid)).forEach(member ->

				member.sendMessage(ChatColor.GRAY + "*" + ChatColor.WHITE + "<" + ChatColor.YELLOW
						+ sender.getName() + ChatColor.WHITE + ">" + ChatColor.GRAY + " [" + name + "] " + ChatColor.WHITE + message)
		);
	}

	public boolean isOnTeam(Player player) {
		return members.containsKey(player.getUniqueId());
	}

	public boolean isOnTeam(UUID uuid) {
		return members.containsKey(uuid);
	}

	public boolean isManager(Player player) {
		return members.get(player.getUniqueId());
	}

	public boolean isManager(UUID uuid) {
		return members.get(uuid);
	}

	public void addMember(Player player) {
		members.put(player.getUniqueId(), false);
	}

	public void setManager(UUID uuid, boolean manager) {
		members.replace(uuid, manager);
	}

	public void setManager(Player player, boolean manager) {
		setManager(player.getUniqueId(), manager);
	}

	public boolean hasManager() {
		return members.keySet().stream().anyMatch(uuid -> members.get(uuid));
	}

	public void removePlayer(Player player) {
		removePlayer(player.getUniqueId());
	}

	public void removePlayer(UUID uuid) {
		members.remove(uuid);
	}
}
