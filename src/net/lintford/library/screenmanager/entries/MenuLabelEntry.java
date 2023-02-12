package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;

public class MenuLabelEntry extends MenuEntry {

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected boolean mTrimText;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean trimText() {
		return mTrimText;
	}

	public void trimText(boolean newValue) {
		mTrimText = newValue;
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	public void label(String newLabel) {
		mText = newLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuLabelEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mDrawBackground = false;
		mText = "Unnamed Label";

		mCanHaveFocus = false;
		entryColor.setFromColor(ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, .5f));

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!enabled())
			return;

		final var lTextBoldFont = mParentScreen.fontBold();
		final var lScreenOffset = screen.screenPositionOffset();
		final var lUiTextScale = mParentScreen.uiTextScale();
		final var lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final var lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY, 32, 32, parentZDepth + .15f, entryColor);

			final float lMidLength = mW - 64;
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_MID, lScreenOffset.x + mX + 32, lScreenOffset.y + mY, lMidLength, 32, parentZDepth + .15f, entryColor);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_RIGHT, lScreenOffset.x + mX + lMidLength + 32, lScreenOffset.y + mY, 32, 32, parentZDepth + .15f, entryColor);
			lSpriteBatch.end();
		}

		float lX = mX + mW / 2 - lLabelWidth / 2; // Center label
		switch (mHorizontalAlignment) {
		case LEFT:
			lX = mX;
			break;
		case RIGHT:
			lX = mX - mLeftPadding - lLabelWidth;
			break;
		default:
			lX = mX + mW / 2 - lLabelWidth / 2; // Center label
			break;
		}

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mText, lScreenOffset.x + lX + 15.f, lScreenOffset.y + mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);
		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}
}
