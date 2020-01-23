package com.pvpraids.raid.team.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public abstract class TeamSubCommand {
	private final String name;
	private final boolean requiresTeam;
	private final boolean requiresManager;
	private String[] aliases;

	public TeamSubCommand(String name, boolean requiresTeam, boolean requiresManager, String... aliases) {
		this(name, requiresTeam, requiresManager);
		this.aliases = aliases;
	}

	public abstract void onCommand(Player player, String label, String[] args);
}
