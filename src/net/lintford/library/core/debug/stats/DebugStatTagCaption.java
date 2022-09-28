package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class DebugStatTagCaption extends DebugStatTag<String> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagCaption(String label) {
		super(label);

		mAtoReset = false;
	}

	DebugStatTagCaption(final int uid, String label) {
		super(uid, label);

		mAtoReset = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.drawText(String.format("%s", mLabel), positionX, positionY, -0.01f, ColorConstants.getColor(mRed, mGreen, mBlue), 1.f, -1);
	}
}
