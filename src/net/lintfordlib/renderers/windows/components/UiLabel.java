package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;

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

	public UiLabel() {
		this("Label not set");
	}

	public UiLabel(String labelText) {
		mLabelText = labelText;
		mShowBackgroundOnHover = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lMouseX = core.HUD().getMouseWorldSpaceX();
		final float lMouseY = core.HUD().getMouseWorldSpaceY();

		if (mShowBackgroundOnHover && intersectsAA(lMouseX, lMouseY)) {
			final var lSpriteBatch = sharedResources.uiSpriteBatch();

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.MenuPanelPrimaryColor);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mH, componentZDepth);
			if (mW > 32) {
				lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mH, componentZDepth);
				lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mH, componentZDepth);
			}
			lSpriteBatch.end();
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
