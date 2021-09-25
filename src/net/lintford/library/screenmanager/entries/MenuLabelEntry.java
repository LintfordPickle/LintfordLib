package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.BaseLayout;

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

	public void trimText(boolean pNewValue) {
		mTrimText = pNewValue;
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (pNewValue) {

		}

		super.hasFocus(pNewValue);
	}

	public void label(String pNewLabel) {
		mText = pNewLabel;
	}

	public String label() {
		return mText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuLabelEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mDrawBackground = false;
		mText = "Unnamed Label";

		mCanHaveFocus = false;
		mCanHoverOver = false;
		entryColor.setFromColor(ColorConstants.getColorWithRGBMod(ColorConstants.TertiaryColor, .5f));

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!enabled())
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lTextBoldFont = lParentScreen.fontBold();
		final var lScreenOffset = pScreen.screenPositionOffset();
		final var lUiTextScale = lParentScreen.uiTextScale();
		final var lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final var lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSpriteBatch = lParentScreen.spriteBatch();

		if (mDrawBackground) {
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_LEFT,  lScreenOffset.x + x, lScreenOffset.y + y, 32, 32, pParentZDepth + .15f, entryColor);
			
			final float lMidLength = w - 64;
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_MID,   lScreenOffset.x + x + 32, lScreenOffset.y + y, lMidLength, 32, pParentZDepth + .15f, entryColor);
			
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_RIGHT, lScreenOffset.x + x + lMidLength + 32, lScreenOffset.y + y, 32, 32, pParentZDepth + .15f, entryColor);
			lSpriteBatch.end();

		}

		float lX = x + w / 2 - lLabelWidth / 2; // Center label
		switch (mHorizontalAlignment) {
		case LEFT:
			lX = x;
			break;
		case RIGHT:
			lX = x - mLeftPadding - lLabelWidth;
			break;
		default:
			lX = x + w / 2 - lLabelWidth / 2; // Center label
			break;
		}

		lTextBoldFont.begin(pCore.HUD());
		lTextBoldFont.drawText(mText, lScreenOffset.x + lX + 15.f, lScreenOffset.y + y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);
		lTextBoldFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lSpriteBatch, mInfoIconDstRectangle, lParentScreen.screenColor.a);
		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lSpriteBatch, mWarnIconDstRectangle, lParentScreen.screenColor.a);
		}

		drawDebugCollidableBounds(pCore, lSpriteBatch);
	}
}
