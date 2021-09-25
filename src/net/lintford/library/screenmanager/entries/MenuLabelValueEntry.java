package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.layouts.BaseLayout;

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

	public void showLabel(boolean pShowLabel) {
		mShowLabel = pShowLabel;
	}

	public void valueText(String pValueText) {
		mValueText = pValueText;
	}

	public String valueText() {
		return mValueText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuLabelValueEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout);

		mDrawBackground = false;
		mText = "Add your message";
		mShowLabel = true;

		mCanHaveFocus = false;
		mCanHoverOver = false;

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);
		if (!enabled())
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lTextBoldFont = lParentScreen.fontBold();
		final var lUiTextScale = lParentScreen.uiTextScale();
		final var lScreenOffset = lParentScreen.screenPositionOffset();

		final float lLabelWidth = lTextBoldFont.getStringWidth(mText, lUiTextScale);
		final float lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;

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
		lTextBoldFont.drawText(mValueText, lScreenOffset.x + lX + 100.f, lScreenOffset.y + y + h / 2f - lFontHeight / 2f, pParentZDepth + .15f, textColor, lUiTextScale);
		lTextBoldFont.end();
	}
}
