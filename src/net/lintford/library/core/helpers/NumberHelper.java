package net.lintford.library.core.helpers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberHelper {

	private static DecimalFormat decimalFormat;

	public static DecimalFormat decimalFormatter() {
		if (decimalFormat == null) {
			createDecimalFormatter();
		}
		return decimalFormat;
	}

	private static void createDecimalFormatter() {
		final var decimalFormatSymbols = new DecimalFormatSymbols();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormatSymbols.setGroupingSeparator(',');

		decimalFormat = new DecimalFormat("#,##0.00", decimalFormatSymbols);
	}
}
