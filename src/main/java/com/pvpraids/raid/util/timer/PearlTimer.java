package com.pvpraids.raid.util.timer;

import com.pvpraids.core.utils.timer.impl.IntegerTimer;
import com.pvpraids.raid.util.NumberUtil;
import java.util.concurrent.TimeUnit;

public class PearlTimer extends IntegerTimer {
	public PearlTimer() {
		super(TimeUnit.SECONDS, 10);
	}

	public String scoreboardFormattedExpiration() {
		return NumberUtil.roundToTenthsPlace((expiry - System.currentTimeMillis()) / 1000.0) + "s";
	}
}
