package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;
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
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lMouseX = core.HUD().getMouseWorldSpaceX();
		final float lMouseY = core.HUD().getMouseWorldSpaceY();

		final boolean lIsMouseHovering = intersectsAA(lMouseX, lMouseY);

		if (lIsMouseHovering) {
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

		final var lTextHeight = textFont.fontHeight();
		final var lValueWidth = textFont.getStringWidth(Float.toString(mValueFloat));

		textFont.begin(core.HUD());
		textFont.setTextColor(ColorConstants.TextEntryColor);
		textFont.drawText(mLabelText, mX + HorizontalPadding, mY + mH / 2.f - lTextHeight / 2.f, componentZDepth, 1f, -1);
		textFont.drawText(Float.toString(mValueFloat), mX + mW - lValueWidth - HorizontalPadding, mY + mH / 2 - lTextHeight / 2, componentZDepth, 1f, -1);
		textFont.end();
	}
}
