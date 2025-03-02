package net.lintfordlib.core.debug.stats;

import net.lintfordlib.core.graphics.fonts.FontUnit;

public class DebugStatTagCaption extends DebugStatTag<String> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagCaption(String label) {
		super(label);

		mAutoReset = false;
	}

	DebugStatTagCaption(final int uid, String label) {
		super(uid, label);

		mAutoReset = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.setTextColorRGBA(mRed, mGreen, mBlue, 1.f);
		fontUnit.drawText(String.format("%s", mLabel), positionX, positionY, .01f, 1.f, -1);
	}
}
