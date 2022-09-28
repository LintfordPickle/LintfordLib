package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;

public class UiLabelledString extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelText;
	private String mValueString;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelText(String labelText) {
		mLabelText = labelText;
	}

	public String labelText() {
		return mLabelText;
	}

	public void value(String newValue) {
		mValueString = newValue;
	}

	public String value() {
		return mValueString;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabelledString(UiWindow parentWindow) {
		this(parentWindow, "Label not set");
	}

	public UiLabelledString(UiWindow parentWindow, String labelText) {
		super(parentWindow);

		mLabelText = labelText;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lMouseX = core.HUD().getMouseWorldSpaceX();
		final float lMouseY = core.HUD().getMouseWorldSpaceY();

		final boolean lIsMouseHovering = intersectsAA(lMouseX, lMouseY);

		if (lIsMouseHovering) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
			if (mW > 32) {
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mH, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
			}
		}

		final float lTextHeight = textFont.fontHeight();
		textFont.drawText(mLabelText, mX + lHorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, ColorConstants.TextEntryColor, 1f, -1);

		if (mValueString != null && mValueString.length() > 0) {
			final float lValueWidth = textFont.getStringWidth(mValueString);
			textFont.drawText(mValueString, mX + mW - lValueWidth - lHorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, ColorConstants.TextEntryColor, 1f, -1);
		}
	}
}
