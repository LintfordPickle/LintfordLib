package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class DebugStatTagInt extends DebugStatTag<Integer> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagInt(String pLabel, int pDefValue) {
		super(pLabel);

		value = pDefValue;
		defaultValue = pDefValue;

	}

	public DebugStatTagInt(String pLabel, int pDefValue, boolean pAutoReset) {
		this(pLabel, pDefValue);

		autoReset = pAutoReset;

	}

	DebugStatTagInt(final int pID, String pLabel, int pDefValue) {
		super(pID, pLabel);

		value = pDefValue;
		defaultValue = pDefValue;

	}

	DebugStatTagInt(final int pID, String pLabel, int pDefValue, boolean pAutoReset) {
		this(pID, pLabel, pDefValue);

		autoReset = pAutoReset;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit pFontUnit, float pPosX, float pPosY) {
		pFontUnit.drawText(String.format("%s : %d", label, value), pPosX, pPosY, -0.01f, ColorConstants.getColor(r, g, b), 1.f, -1);

	}

}
