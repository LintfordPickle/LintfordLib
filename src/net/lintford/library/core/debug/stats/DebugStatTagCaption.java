package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class DebugStatTagCaption extends DebugStatTag<String> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagCaption(String pLabel) {
		super(pLabel);

		autoReset = false;

	}

	DebugStatTagCaption(final int pID, String pLabel) {
		super(pID, pLabel);

		autoReset = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit pFontUnit, float pPosX, float pPosY) {
		pFontUnit.drawText(String.format("%s", label), pPosX, pPosY, -0.01f, ColorConstants.getColor(r, g, b), 1.f, -1);
	}
}
