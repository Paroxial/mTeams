package com.pvpraids.raid.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.pvpraids.raid.RaidPlugin;
import com.pvpraids.raid.commands.staff.NearModCommand;

public class NoclipPacketManager {
	public static void init() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(RaidPlugin.getInstance(),
				PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if (NearModCommand.chunks.containsKey(event.getPlayer().getName())) {
					event.setCancelled(true);
				}
			}
		});
	}
}
