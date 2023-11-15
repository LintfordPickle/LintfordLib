package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiLabelledFloat extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelText;
	private float mValueFloat;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelText(String labelText) {
		mLabelText = labelText;
	}

	public String labelText() {
		return mLabelText;
	}

	public void value(float newValue) {
		mValueFloat = newValue;
	}

	public float value() {
		return mValueFloat;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabelledFloat(UiWindow parentWindow) {
		this(parentWindow, "Label not set");
	}

	public UiLabelledFloat(UiWindow parentWindow, String labelText) {
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

		final var lTextHeight = textFont.fontHeight();
		final var lValueWidth = textFont.getStringWidth(Float.toString(mValueFloat));

		textFont.begin(core.HUD());
		textFont.drawText(mLabelText, mX + lHorizontalPadding, mY + mH / 2.f - lTextHeight / 2.f, componentZDepth, ColorConstants.TextEntryColor, 1f, -1);
		textFont.drawText(Float.toString(mValueFloat), mX + mW - lValueWidth - lHorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, ColorConstants.TextEntryColor, 1f, -1);
		textFont.end();
	}
}
