package net.lintfordlib.core.debug.stats;

import net.lintfordlib.core.graphics.fonts.FontUnit;

public class DebugStatTagFloat extends DebugStatTag<Float> {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTagFloat(String label, float defaultValue) {
		super(label);

		mValue = defaultValue;
		mDefaultValue = defaultValue;
	}

	public DebugStatTagFloat(String label, float defaultValue, boolean autoReset) {
		this(label, defaultValue);

		mAutoReset = autoReset;
	}

	DebugStatTagFloat(final int uid, String label, float defaultValue) {
		super(uid, label);

		mValue = defaultValue;
		mDefaultValue = defaultValue;
	}

	DebugStatTagFloat(final int uid, String label, float defaultValue, boolean autoReset) {
		this(uid, label, defaultValue);

		mAutoReset = autoReset;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.setTextColorRGBA(mRed, mGreen, mBlue, 1.f);
		fontUnit.drawText(String.format("%s : %.2f", mLabel, mValue), positionX, positionY, .01f, 1f, -1);
	}
}
