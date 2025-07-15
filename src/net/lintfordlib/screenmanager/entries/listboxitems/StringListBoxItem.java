package net.lintfordlib.screenmanager.entries.listboxitems;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.entries.MenuListBox;
import net.lintfordlib.screenmanager.entries.MenuListBoxItem;

public class StringListBoxItem extends MenuListBoxItem {

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

	public StringListBoxItem(ScreenManager pScreenManager, MenuListBox parentListBox, String value, int entityGroupUid) {
		super(pScreenManager, parentListBox, entityGroupUid);

		mH = 25;
		mTextValue = value;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth, boolean isSelected, boolean isHighlighted) {
		final var lTransitionOffset = screen.screenPositionOffset();

		if (entryColor.a > 0.f) {
			spriteBatch.setColor(entryColor);
			spriteBatch.draw(coreDef, CoreTextureNames.TEXTURE_WHITE, lTransitionOffset.x + mX, lTransitionOffset.y + mY, mW, mH, zDepth);
		}

		if (mTextValue != null && mTextValue.length() > 0) {
			final float lScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lFont = mParentListBox.parentScreen().font();
			final float lFontHeight = lFont.getStringHeight(mTextValue, lScale);

			lFont.setTextColor(ColorConstants.TextEntryColor);
			lFont.drawText(mTextValue, lTransitionOffset.x + mX, lTransitionOffset.y + mY + mH / 2.f - lFontHeight / 2, zDepth, lScale, -1);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
		}
	}
}