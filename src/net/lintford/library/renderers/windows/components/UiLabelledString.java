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

	public void labelText(String pLabelText) {
		mLabelText = pLabelText;
	}

	public String labelText() {
		return mLabelText;
	}

	public void value(String pValueInt) {
		mValueString = pValueInt;
	}

	public String value() {
		return mValueString;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabelledString(UiWindow pParentWindow) {
		this(pParentWindow, "Label not set");

	}

	public UiLabelledString(UiWindow pParentWindow, String pLabelText) {
		super(pParentWindow);

		mLabelText = pLabelText;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lMouseX = pCore.HUD().getMouseWorldSpaceX();
		final float lMouseY = pCore.HUD().getMouseWorldSpaceY();

		final boolean lIsMouseHovering = intersectsAA(lMouseX, lMouseY);

		if (lIsMouseHovering) {
			// Renders the background of the input text widget
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, x, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
			if (w > 32) {
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, x + 32, y, w - 64, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, x + w - 32, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
			}
		}

		final float lTextHeight = pTextFont.fontHeight();
		pTextFont.drawText(mLabelText, x + lHorizontalPadding, y + h / 2 - lTextHeight / 2, pComponentZDepth, ColorConstants.TextEntryColor, 1f, -1);

		if (mValueString != null && mValueString.length() > 0) {
			final float lValueWidth = pTextFont.getStringWidth(mValueString);
			pTextFont.drawText(mValueString, x + w - lValueWidth - lHorizontalPadding, y + h / 2 - lTextHeight / 2, pComponentZDepth, ColorConstants.TextEntryColor, 1f, -1);
		}
	}
}
