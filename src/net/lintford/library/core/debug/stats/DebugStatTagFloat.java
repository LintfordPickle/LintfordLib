package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class DebugStatTagFloat extends DebugStatTag<Float> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagFloat(String pLabel, float pDefValue) {
		super(pLabel);

		value = pDefValue;
		defaultValue = pDefValue;

	}

	public DebugStatTagFloat(String pLabel, float pDefValue, boolean pAutoReset) {
		this(pLabel, pDefValue);

		autoReset = pAutoReset;

	}

	DebugStatTagFloat(final int pID, String pLabel, float pDefValue) {
		super(pID, pLabel);

		value = pDefValue;
		defaultValue = pDefValue;

	}

	DebugStatTagFloat(final int pID, String pLabel, float pDefValue, boolean pAutoReset) {
		this(pID, pLabel, pDefValue);

		autoReset = pAutoReset;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit pFontUnit, float pPosX, float pPosY) {
		pFontUnit.drawText(String.format("%s : %.2f", label, value), pPosX, pPosY, -0.01f, ColorConstants.getColor(r, g, b), 1f, -1);

	}

}
