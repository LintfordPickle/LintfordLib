package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiLabelledInt extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabelText;
	private int mValueInt;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void labelText(String labelText) {
		mLabelText = labelText;
	}

	public String labelText() {
		return mLabelText;
	}

	public void value(int newValue) {
		mValueInt = newValue;
	}

	public int value() {
		return mValueInt;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiLabelledInt(UiWindow parentWindow) {
		this(parentWindow, "Label not set");
	}

	public UiLabelledInt(UiWindow parentWindow, String labelText) {
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

		boolean lIsMouseHovering = intersectsAA(lMouseX, lMouseY);

		if (lIsMouseHovering) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.MenuPanelPrimaryColor);
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mH, componentZDepth);
			if (mW > 32) {
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mH, componentZDepth);
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mH, componentZDepth);
			}
			spriteBatch.end();
		}

		final var lTextHeight = textFont.fontHeight();
		final var lValueWidth = textFont.getStringWidth(Integer.toString(mValueInt));

		textFont.begin(core.HUD());
		textFont.setTextColor(ColorConstants.TextEntryColor);
		textFont.drawText(mLabelText, mX + HorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, 1f, -1);
		textFont.drawText(Integer.toString(mValueInt), mX + mW - lValueWidth - HorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, 1f, -1);
		textFont.end();
	}
}
