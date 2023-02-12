package net.lintford.library.screenmanager.entries.listboxitems;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.ListBox;
import net.lintford.library.screenmanager.entries.ListBoxItem;

public class LabelValueListBoxItem extends ListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3360202382609680982L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelValue;
	private String mTextValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelValue(String label) {
		mLabelValue = label;
	}

	public String labelValue() {
		return mLabelValue;
	}

	public void textValue(String text) {
		mTextValue = text;
	}

	public String textValue() {
		return mTextValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public LabelValueListBoxItem(ScreenManager screenManager, ListBox parentListBox, int index, String label, String value, int entityGroupUid) {
		super(screenManager, parentListBox, index, entityGroupUid);

		mLabelValue = label;
		mTextValue = value;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, float parentZDepth) {
		if (mLabelValue != null && mLabelValue.length() > 0) {

			if (mTextValue == null)
				mTextValue = "";

			final float lScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lFont = mParentListBox.parentScreen().font();

			mH = 10;

			lFont.begin(core.HUD());
			lFont.drawText(mLabelValue, mX, mY, parentZDepth + .1f, textColor, lScale, -1);
			lFont.drawText(mTextValue, mX + mW / 2, mY, parentZDepth + .1f, textColor, lScale, -1);
			lFont.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
		}
	}
}