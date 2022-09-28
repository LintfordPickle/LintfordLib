package net.lintford.library.screenmanager.entries.listboxitems;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.entries.ListBox;
import net.lintford.library.screenmanager.entries.ListBoxItem;

public class StringListBoxItem extends ListBoxItem {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3360202382609680982L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mTextValue;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void textValue(String text) {
		mTextValue = text;
	}

	public String textValue() {
		return mTextValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public StringListBoxItem(ScreenManager pScreenManager, ListBox parentListBox, int index, String value, int entityGroupUid) {
		super(pScreenManager, parentListBox, index, entityGroupUid);

		mTextValue = value;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, boolean isSelected, float parentZDepth) {
		if (mTextValue != null && mTextValue.length() > 0) {
			final float lScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lFont = mParentListBox.parentLayout().parentScreen.font();
			final float lFontHeight = lFont.getStringHeight(mTextValue, lScale);

			mH = 10;

			lFont.begin(core.HUD());
			lFont.drawText(mTextValue, mX, mY - lFontHeight / 2, parentZDepth + .1f, ColorConstants.TextEntryColor, lScale, -1);
			lFont.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
		}
	}
}