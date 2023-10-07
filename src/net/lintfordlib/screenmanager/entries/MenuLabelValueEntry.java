package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
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
		super.draw(core, screen, parentZDepth);
		if (!enabled())
			return;

		final var lTextBoldFont = mParentScreen.fontBold();
		final var lUiTextScale = mParentScreen.uiTextScale();
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		final float lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final float lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;

		float lX = mX + mW / 2 - lLabelWidth / 2;
		switch (mHorizontalAlignment) {
		case LEFT:
			lX = mX;
			break;
		case RIGHT:
			lX = mX - mLeftPadding - lLabelWidth;
			break;
		default:
			lX = mX + mW / 2 - lLabelWidth / 2;
			break;
		}

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mValueText, lScreenOffset.x + lX + 100.f, lScreenOffset.y + mY + mH / 2f - lFontHeight / 2f, parentZDepth + .15f, textColor, lUiTextScale);
		lTextBoldFont.end();
	}
}
