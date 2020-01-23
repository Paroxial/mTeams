package com.pvpraids.raid.team;

import com.pvpraids.raid.util.LocationUtil;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class TeamManager {
	private static final File DIRECTORY = new File("teams");

	static {
		DIRECTORY.mkdir();
	}

	private final Set<Team> teams = new HashSet<>();

	public TeamManager() {
		for (File file : DIRECTORY.listFiles()) {
			if (file == null) {
				continue;
			}

			loadTeam(file.getName());
		}
	}

	public Team getTeam(String teamName) {
		return teams.stream().filter(team -> team.getName().equalsIgnoreCase(teamName)).findAny().orElse(null);
	}

	public Team getTeam(UUID id) {
		return teams.stream().filter(team -> team.getMembers().containsKey(id)).findAny().orElse(null);
	}

	public Team getTeam(Player player) {
		return getTeam(player.getUniqueId());
	}

	public boolean doesTeamExist(String name) {
		return teams.stream().anyMatch(team -> team.getName().equalsIgnoreCase(name));
	}

	public void addTeam(Team team) {
		teams.add(team);
	}

	public void deleteTeam(Team team) {
		teams.remove(team);

		File file = new File(DIRECTORY, team.getName());
		file.delete();
	}

	private void loadTeam(String teamName) {
		try {
			File file = new File(DIRECTORY, teamName);
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedReader in = new BufferedReader(new FileReader(file));
			String password = in.readLine();
			if (password.isEmpty()) {
				password = null;
			}
			boolean friendlyFire = Boolean.valueOf(in.readLine());
			Location hq = LocationUtil.stringToLocation(in.readLine());
			Location rally = LocationUtil.stringToLocation(in.readLine());

			Map<UUID, Boolean> members = new HashMap<>();

			String line = in.readLine();
			while (line != null && !line.isEmpty()) {
				String[] parts = line.split(":");
				UUID uuid = UUID.fromString(parts[0]);
				boolean manager = Boolean.valueOf(parts[1]);

				members.put(uuid, manager);

				line = in.readLine();
			}

			in.close();

			Team team = new Team(teamName, password, friendlyFire, hq, rally, members);
			teams.add(team);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveTeam(Team team) {
		try {
			File file = new File(DIRECTORY, team.getName());
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(team.getPassword() != null ? team.getPassword() : "");
			out.newLine();
			out.write(String.valueOf(team.isFriendlyFire()));
			out.newLine();
			out.write(team.getHq() != null ? LocationUtil.locationToString(team.getHq()) : "null");
			out.newLine();
			out.write(team.getRally() != null ? LocationUtil.locationToString(team.getRally()) : "null");
			out.newLine();
			for (Map.Entry<UUID, Boolean> members : team.getMembers().entrySet()) {
				out.write(members.getKey().toString() + ":" + members.getValue());
				out.newLine();
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
