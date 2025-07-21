package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;

public class MenuLabelValueEntry extends MenuLabelEntry {

	private static final long serialVersionUID = -6246272207476797676L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mValueText;
	private boolean mShowLabel;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean showLabel() {
		return mShowLabel;
	}

	public void showLabel(boolean showLabel) {
		mShowLabel = showLabel;
	}

	public void valueText(String valueText) {
		mValueText = valueText;
	}

	public String valueText() {
		return mValueText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuLabelValueEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen);

		mDrawBackground = false;
		mText = "Add your message";
		mShowLabel = true;

		mCanHaveFocus = false;

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!enabled())
			return;

		final var textBoldFont = mParentScreen.fontBold();
		final var screenOffset = screen.screenPositionOffset();
		final var uiTextScale = mParentScreen.uiTextScale();
		final var labelWidth = textBoldFont.getStringWidth(mText, uiTextScale);
		final var valueWidth = textBoldFont.getStringWidth(mValueText, uiTextScale);
		final var separatorWidth = textBoldFont.getStringWidth(":", uiTextScale);
		final var fontHeight = textBoldFont.fontHeight() * uiTextScale;
		final var spriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			final float lMidLength = mW - 64;

			spriteBatch.setColor(entryColor);
			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_LEFT, screenOffset.x + mX, screenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_MID, screenOffset.x + mX + 32, screenOffset.y + mY, lMidLength, 32, parentZDepth + .15f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_LABEL_RIGHT, screenOffset.x + mX + lMidLength + 32, screenOffset.y + mY, 32, 32, parentZDepth + .15f);
			spriteBatch.end();

		} else if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, spriteBatch);

		final var paddingH = 15.f;
		float labelX;
		float valueX;

		mHorizontalAlignment = ALIGNMENT.CENTER;
		switch (mHorizontalAlignment) {
		case LEFT:
			labelX = mX + paddingH;
			valueX = mX + mW / 2.f + paddingH;
			break;
		case RIGHT:
			labelX = mX + mW / 2.f - paddingH - labelWidth;
			valueX = mX + mW / 2.f + paddingH;
			break;
		default:
			labelX = mX + mW / 2.f - paddingH - labelWidth;
			valueX = mX + mW / 2.f + paddingH;
			break;
		}

		textBoldFont.begin(core.HUD());
		textBoldFont.setTextColor(textColor);
		textBoldFont.drawText(mText, screenOffset.x + labelX, screenOffset.y + mY + mH / 2f - fontHeight / 2f, parentZDepth + .15f, uiTextScale);
		textBoldFont.drawText(":", screenOffset.x + mX + mW / 2 - separatorWidth * .5f, screenOffset.y + mY + mH / 2f - fontHeight / 2f, parentZDepth + .15f, uiTextScale);
		textBoldFont.drawText(mValueText, screenOffset.x + valueX, screenOffset.y + mY + mH / 2f - fontHeight / 2f, parentZDepth + .15f, uiTextScale);
		textBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, mParentScreen.screenColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, mParentScreen.screenColor.a);

		drawDebugCollidableBounds(core, spriteBatch);
	}
}
