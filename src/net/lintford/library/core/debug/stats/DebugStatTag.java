package net.lintford.library.core.debug.stats;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontUnit;

public abstract class DebugStatTag<T> {

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mAtoReset;
	protected String mLabel;
	protected String mPostFix;
	protected final int mUid;
	protected T mValue;
	protected T mDefaultValue;
	protected float mRed;
	protected float mGreen;
	protected float mBlue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setColor(float red, float green, float blue) {
		mRed = red;
		mGreen = green;
		mBlue = blue;
	}

	public void setLabel(String newLabel) {
		mLabel = newLabel;
	}

	public void setValue(T newValue) {
		mValue = newValue;
	}

	public void setDefaultValue(T newValue) {
		mDefaultValue = newValue;
	}

	public void reset() {
		mValue = mDefaultValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTag(String label) {
		this(DebugStats.getNewStatTagCounter(), label);

	}

	DebugStatTag(final int uid, String label) {
		mUid = uid;
		mLabel = label;
		mAtoReset = true;
		mRed = mGreen = mBlue = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		return false;
	}

	public abstract void draw(FontUnit fontUnit, float positionX, float positionY);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(DebugStatTag<?> childTag) {

	}
}
