package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

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
		this(screenManager, parentScreen, "");
	}

	public MenuLabelEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, label);

		mDrawBackground = false;

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
		final var spriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			final float lMidLength = mW - 64;

			spriteBatch.setColor(entryColor);

			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_LEFT, lScreenOffset.x + mX, lScreenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_MID, lScreenOffset.x + mX + 32, lScreenOffset.y + mY, lMidLength, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_RIGHT, lScreenOffset.x + mX + lMidLength + 32, lScreenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.end();

		} else if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, spriteBatch);

		float lX;
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
		lTextBoldFont.setTextColor(textColor);
		lTextBoldFont.drawText(mText, lScreenOffset.x + lX + 15.f, lScreenOffset.y + mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, lUiTextScale);
		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		drawDebugCollidableBounds(core, spriteBatch);
	}
}
