package com.pvpraids.raid.storage;

import com.pvpraids.core.storage.flatfile.Config;
import com.pvpraids.raid.RaidPlugin;
import org.bukkit.Location;

public class RaidConfig extends Config {
	public RaidConfig() {
		super(RaidPlugin.getInstance(), "raid");
		addDefault("spawn", RaidPlugin.getInstance().getServer().getWorlds().get(0).getSpawnLocation());
		copyDefaults();
	}

	public void setSpawn(Location location) {
		RaidPlugin.getInstance().setSpawn(location);
		set("spawn", location);
	}
}
