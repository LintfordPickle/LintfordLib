package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
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
		super(parentWindow);

		separatorHeight(3.f);
		mSeparatorColor.setFromColor(ColorConstants.TextEntryColor);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		spriteBatch.begin(core.HUD());
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, mX, mY, 32, mSeparatorHeight, componentZDepth, mSeparatorColor);
		if (mW > 32) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, mX + 32, mY, mW - 64, mSeparatorHeight, componentZDepth, mSeparatorColor);
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, mX + mW - 32, mY, 32, mSeparatorHeight, componentZDepth, mSeparatorColor);
		}
		spriteBatch.end();
	}
}
