package net.lintfordlib.screenmanager.entries.listboxitems;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;

public class LabelValueListBoxItem extends MenuListBoxItem {

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

	public LabelValueListBoxItem(ScreenManager screenManager, MenuListBox parentListBox, int index, String label, String value, int entityGroupUid) {
		super(screenManager, parentListBox, index, entityGroupUid);

		mLabelValue = label;
		mTextValue = value;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth) {
		if (mLabelValue != null && mLabelValue.length() > 0) {

			if (mTextValue == null)
				mTextValue = "";

			final float lScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lFont = mParentListBox.parentScreen().font();

			mH = 10;

			lFont.begin(core.HUD());
			lFont.drawText(mLabelValue, mX, mY, zDepth, textColor, lScale, -1);
			lFont.drawText(mTextValue, mX + mW / 2, mY, zDepth, textColor, lScale, -1);
			lFont.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
		}
	}
}