package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.renderers.windows.UiWindow;

public class UiSeparator extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mSeparatorHeight;
	private final Color mSeparatorColor = new Color();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float separatorHeight() {
		return mSeparatorHeight;
	}

	public void separatorHeight(float newHeight) {
		mSeparatorHeight = newHeight;
		desiredHeight(mSeparatorHeight + 2.f);
	}

	public float separatorColor() {
		return mSeparatorHeight;
	}

	public void separatorColor(Color newColor) {
		mSeparatorColor.setFromColor(newColor);
	}

	public void separatorColor(float r, float g, float b, float a) {
		mSeparatorColor.setRGBA(r, g, b, a);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiSeparator(UiWindow parentWindow) {
		this(parentWindow, 3.f);
	}

	public UiSeparator(UiWindow parentWindow, float separatorHeight) {
		super(parentWindow);

		separatorHeight(separatorHeight);
		mSeparatorColor.setFromColor(ColorConstants.TextEntryColor);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(mSeparatorColor);
		lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mSeparatorHeight, componentZDepth);
		if (mW > 32) {
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mSeparatorHeight, componentZDepth);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mSeparatorHeight, componentZDepth);
		}
		lSpriteBatch.end();
	}
}
