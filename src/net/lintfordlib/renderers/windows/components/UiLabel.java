package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiLabel extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelText;
	private boolean mShowBackgroundOnHover;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean showBackgroundOnHover() {
		return mShowBackgroundOnHover;
	}

	public void showBackgroundOnHover(boolean newValue) {
		mShowBackgroundOnHover = newValue;
	}

	public void labelText(String labelText) {
		mLabelText = labelText;
	}

	public String labelText() {
		return mLabelText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabel(UiWindow parentWindow) {
		this(parentWindow, "Label not set");
	}

	public UiLabel(UiWindow parentWindow, String labelText) {
		super(parentWindow);

		mLabelText = labelText;
		mShowBackgroundOnHover = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lMouseX = core.HUD().getMouseWorldSpaceX();
		final float lMouseY = core.HUD().getMouseWorldSpaceY();

		if (mShowBackgroundOnHover && intersectsAA(lMouseX, lMouseY)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.MenuPanelPrimaryColor);
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mH, componentZDepth);
			if (mW > 32) {
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mH, componentZDepth);
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mH, componentZDepth);
			}
			spriteBatch.end();
		}

		if (mLabelText != null) {
			final float lTextHeight = textFont.fontHeight();

			textFont.begin(core.HUD());
			textFont.setTextColor(ColorConstants.TextEntryColor);
			textFont.drawText(mLabelText, mX, mY + mH / 2 - lTextHeight / 2, componentZDepth - 0.01f, 1f, -1);
			textFont.end();
		}
	}
}
