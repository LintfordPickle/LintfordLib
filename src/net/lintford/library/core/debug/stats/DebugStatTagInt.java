package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

public class DebugStatTagInt extends DebugStatTag<Integer> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagInt(String label, int defaultValue) {
		super(label);

		mValue = defaultValue;
		mDefaultValue = defaultValue;
	}

	public DebugStatTagInt(String label, int defaultValue, boolean autoReset) {
		this(label, defaultValue);

		mAtoReset = autoReset;
	}

	DebugStatTagInt(final int uid, String label, int defaultValue) {
		super(uid, label);

		mValue = defaultValue;
		mDefaultValue = defaultValue;
	}

	DebugStatTagInt(final int pID, String label, int defaultValue, boolean autoReset) {
		this(pID, label, defaultValue);

		mAtoReset = autoReset;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.drawText(String.format("%s : %d", mLabel, mValue), positionX, positionY, -0.01f, ColorConstants.getColor(mRed, mGreen, mBlue), 1.f, -1);
	}
}
