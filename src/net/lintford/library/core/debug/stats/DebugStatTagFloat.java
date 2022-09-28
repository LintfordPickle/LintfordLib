package net.lintford.library.core.debug.stats;

import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;

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

		mAtoReset = autoReset;
	}

	DebugStatTagFloat(final int uid, String label, float defaultValue) {
		super(uid, label);

		mValue = defaultValue;
		mDefaultValue = defaultValue;
	}

	DebugStatTagFloat(final int uid, String label, float defaultValue, boolean autoReset) {
		this(uid, label, defaultValue);

		mAtoReset = autoReset;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(FontUnit fontUnit, float positionX, float positionY) {
		fontUnit.drawText(String.format("%s : %.2f", mLabel, mValue), positionX, positionY, -0.01f, ColorConstants.getColor(mRed, mGreen, mBlue), 1f, -1);
	}
}
