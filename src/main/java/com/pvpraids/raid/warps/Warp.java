package com.pvpraids.raid.warps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class Warp {
	private final String name;
	private final Location location;
}
