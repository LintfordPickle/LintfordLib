package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;

public class DebugStatTagString extends DebugStatTag<String> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagString(String pLabel) {
		super(pLabel);

		autoReset = false;

	}

	public DebugStatTagString(String pLabel, String pValue) {
		this(pLabel);

		autoReset = false;
		value = pValue;

	}

	DebugStatTagString(final int pID, String pLabel, String pValue) {
		super(pID, pLabel);

		value = pValue;
		defaultValue = pValue;

		autoReset = false;

	}

	DebugStatTagString(final int pID, String pLabel, String pValue, boolean pAutoReset) {
		this(pID, pLabel, pValue);

		autoReset = pAutoReset;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit pFontUnit, float pPosX, float pPosY) {
		pFontUnit.draw(String.format("%s : %s", label, value), pPosX, pPosY, -0.01f, ColorConstants.getColor(r, g, b), 0.75f, -1);

	}

}
