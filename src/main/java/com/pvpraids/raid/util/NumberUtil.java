package com.pvpraids.raid.util;

import java.util.Random;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {
	public final Random RANDOM = new Random();

	public double roundOff(double x, int places) {
		double pow = Math.pow(10, places);
		return Math.round(x * pow) / pow;
	}

	public static double roundToTenthsPlace(double d) {
		return (Math.round(d * 10)) / 10.0;
	}
}
