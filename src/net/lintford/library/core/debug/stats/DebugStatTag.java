package net.lintford.library.core.debug.stats;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontUnit;

public abstract class DebugStatTag<T> {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean autoReset;
	public String label;
	public String postFix;
	public final int id;
	public T value;
	public T defaultValue;
	public float r, g, b;
	// public List<DebugStatTag> children;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setLabel(String pNewLabel) {
		label = pNewLabel;

	}

	public void setValue(T pNewValue) {
		value = pNewValue;

	}

	public void setDefaultValue(T pNewValue) {
		defaultValue = pNewValue;
	}

	public void reset() {
		if (autoReset)
			value = defaultValue;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStatTag(String pLabel) {
		this(DebugStats.getNewStatTagCounter(), pLabel);

	}

	DebugStatTag(final int pID, String pLabel) {
		id = pID;
		label = pLabel;
		autoReset = true;
		r = g = b = 1f;

		// children = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		return false;

	}

	public abstract void draw(FontUnit pFontUnit, float pPosX, float pPosY);

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addChild(DebugStatTag<?> pChildTag) {

	}

}
