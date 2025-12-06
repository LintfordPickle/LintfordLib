package net.lintfordlib.screenmanager.entries.listboxitems;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.Screen;
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

	protected String mLabelValue;
	protected String mTextValue;

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

	public LabelValueListBoxItem(ScreenManager screenManager, MenuListBox parentListBox, int entityGroupUid) {
		super(screenManager, parentListBox, entityGroupUid);
	}

	public LabelValueListBoxItem(ScreenManager screenManager, MenuListBox parentListBox, String label, String value, int entityGroupUid) {
		this(screenManager, parentListBox, entityGroupUid);

		mLabelValue = label;
		mTextValue = value;

		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, SpriteBatch spriteBatch, SpriteSheetDefinition coreDef, FontUnit fontUnit, float zDepth, boolean isActiveSelection, boolean isHighlighted) {

		if (isHighlighted) {
			spriteBatch.setColorWhite();
			renderHighlight(core, screen, spriteBatch, coreDef, zDepth - .01f);
		}

		if (isActiveSelection) {
			spriteBatch.setColorWhite();
			renderSelectionBar(core, screen, spriteBatch, coreDef, zDepth - .01f);
		}

		final var transitionOffset = screen.screenPositionOffset();

		spriteBatch.setColor(entryColor);
		spriteBatch.draw(coreDef, CoreTextureNames.TEXTURE_WHITE, transitionOffset.x + mX, transitionOffset.y + mY, mW, mH, zDepth);

		if (mLabelValue != null && mLabelValue.length() > 0) {

			final var textScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var font = mParentListBox.parentScreen().font();

			font.begin(core.HUD());
			font.setTextColor(textColor);
			font.drawText(mLabelValue, transitionOffset.x + mX, transitionOffset.y + mY, zDepth, textScale, -1);
			font.drawText(mTextValue, transitionOffset.x + mX + mW / 2, transitionOffset.y + mY, zDepth, textScale, -1);
			font.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
		}
	}
}