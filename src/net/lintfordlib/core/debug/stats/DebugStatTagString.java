package net.lintfordlib.core.debug.stats;

import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;

public class DebugStatTagString extends DebugStatTag<String> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagString(String pLabel) {
		super(pLabel);

		mAutoReset = false;

	}

	public DebugStatTagString(String pLabel, String pValue) {
		this(pLabel);

		mAutoReset = false;
		mValue = pValue;

	}

	DebugStatTagString(final int uid, String label, String pValue) {
		super(uid, label);

		mValue = pValue;
		mDefaultValue = pValue;

		mAutoReset = false;

	}

	DebugStatTagString(final int uid, String label, String pValue, boolean pAutoReset) {
		this(uid, label, pValue);

		mAutoReset = pAutoReset;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.drawText(String.format("%s : %s", mLabel, mValue), positionX, positionY, -0.01f, ColorConstants.getColor(mRed, mGreen, mBlue), 1.f, -1);
	}
}
