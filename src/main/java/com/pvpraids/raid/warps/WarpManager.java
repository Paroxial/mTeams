package com.pvpraids.raid.warps;

import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.player.rank.Rank;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class WarpManager {
	private static final int MEMBER_WARPS = 5;
	private static final int VIP_WARPS = 15;
	private static final int PRO_WARPS = 25;
	private static final int HIGHROLLER_WARPS = 30;

	private static final File DIRECTORY = new File("warps");

	static {
		DIRECTORY.mkdir();
	}

	private final Map<UUID, Set<Warp>> warps = new HashMap<>();

	public Set<Warp> getWarps(Player player) {
		return warps.get(player.getUniqueId());
	}

	public void addWarp(Player player, Warp warp) {
		Set<Warp> warpSet = getWarps(player);

		Optional<Warp> existingWarp = warpSet.stream().filter(other -> other.getName().equalsIgnoreCase(warp.getName())).findAny();
		existingWarp.ifPresent(warpSet::remove);

		warpSet.add(warp);

		warps.replace(player.getUniqueId(), warpSet);
	}

	public void removeWarp(Player player, Warp warp) {
		Set<Warp> warpSet = getWarps(player);
		warpSet.remove(warp);

		warps.replace(player.getUniqueId(), warpSet);
	}

	public int getMaxWarpsByRank(CoreProfile profile) {
		if (profile.hasRank(Rank.HIGHROLLER)) {
			return HIGHROLLER_WARPS;
		}
		if (profile.hasRank(Rank.PRO)) {
			return PRO_WARPS;
		}
		if (profile.hasRank(Rank.VIP)) {
			return VIP_WARPS;
		}
		return MEMBER_WARPS;
	}

	public void loadWarps(UUID uuid) {
		Set<Warp> warpSet = new HashSet<>();
		try {
			File file = new File(DIRECTORY, uuid.toString());
			if (!file.exists()) {
				file.createNewFile();
			}

			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			while (line != null) {
				String[] parts = line.split(":");

				String name = parts[0];
				Location loc = LocationUtil.stringToLocation(parts[1]);
				Warp warp = new Warp(name, loc);

				warpSet.add(warp);
				line = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		warps.put(uuid, warpSet);
	}

	public void saveWarps(UUID uuid) {
		Set<Warp> warpSet = warps.get(uuid);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(DIRECTORY, uuid.toString())));
			for (Warp warp : warpSet) {
				out.write(warp.getName() + ":" + LocationUtil.locationToString(warp.getLocation()));
				out.newLine();
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		warps.remove(uuid);
	}
}
