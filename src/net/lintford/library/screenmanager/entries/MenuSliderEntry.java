package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuSliderEntry extends MenuEntry {

	private static final long serialVersionUID = -8125859270010821953L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Rectangle mDownButton;
	private Rectangle mUpButton;
	private String mLabel;
	private final String mSeparator = " : ";
	private String mUnit = "%";
	private int mValue;
	private int mLowerBound;
	private int mUpperBound;
	private boolean mButtonsEnabled;
	private boolean mShowValueEnabled;
	private boolean mShowGuideValuesEnabled;
	private boolean mShowUnit;
	private float mBarPosX;
	private float mBarWidth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void showValueGuides(boolean newValue) {
		mShowGuideValuesEnabled = newValue;
	}

	public boolean showValueGuides() {
		return mShowGuideValuesEnabled;
	}

	public void showValueUnit(boolean newValue) {
		mShowUnit = newValue;
	}

	public boolean showValueUnit() {
		return mShowUnit;
	}

	public void setValueUnit(String valueUnit) {
		mUnit = valueUnit;
	}

	public void showValue(boolean newValue) {
		mShowValueEnabled = newValue;
	}

	public boolean showValue() {
		return mShowValueEnabled;
	}

	public void buttonsEnabled(boolean newValue) {
		mButtonsEnabled = newValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public int getCurrentValue() {
		return mValue;
	}

	public void setBounds(int lowBound, int highBound, int stepSize) {
		mLowerBound = lowBound;
		mUpperBound = highBound;
		setValue(highBound - lowBound / 2);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuSliderEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mLabel = "Label:";

		mDownButton = new Rectangle(0, 0, 32, 32);
		mUpButton = new Rectangle(0, 0, 32, 32);
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		mDownButton.setPosition(mX + mW / 2 + 16, mY);
		mUpButton.setPosition(mX + mW - 32, mY);

		mBarPosX = mX + mW / 2 + mDownButton.width() + 16;
		mBarWidth = mW / 2 - 48;
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var lTextBoldFont = mParentScreen.fontBold();
		final var lSpriteBatch = mParentScreen.spriteBatch();
		final var lUiTextScale = mParentScreen.uiTextScale();

		final var lLabelWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);
		final var lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final var lLabelHeight = lTextBoldFont.getStringHeight(mLabel, lUiTextScale);// ;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		if (mHasFocus & mEnabled) {
			final var lHighlightColor = ColorConstants.getColorWithAlpha(ColorConstants.MenuEntryHighlightColor, lParentScreenAlpha);
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, lHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, lHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, lHighlightColor);
			lSpriteBatch.end();
		}

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			lSpriteBatch.begin(core.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.width() - lArrowButtonSize;

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mDownButton.x() + lArrowButtonPaddingX, lScreenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mUpButton.x() + lArrowButtonPaddingX, lScreenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);

			lSpriteBatch.end();
		}

		// Draw the slider bar and caret
		lSpriteBatch.begin(core.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 16);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_LEFT, lScreenOffset.x + mBarPosX, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_MID, lScreenOffset.x + mBarPosX + 32, lScreenOffset.y + mY, mBarWidth - 64 - 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_RIGHT, lScreenOffset.x + mBarPosX + mBarWidth - 64, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_NUBBLE, lScreenOffset.x + lCaretPos, lScreenOffset.y + mY, 32, 32, mZ, entryColor);
		lSpriteBatch.end();

		final var lColorWhiteWithAlpha = ColorConstants.getWhiteWithAlpha(lParentScreenAlpha);

		// draw the label to the left and the value //
		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + mX + mW / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2f - lLabelHeight / 2f, mZ, lColorWhiteWithAlpha, lUiTextScale, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2f - lLabelHeight / 2f, mZ, lColorWhiteWithAlpha, lUiTextScale, -1);

		if (mShowValueEnabled) {
			final float lValueStringWidth = lTextBoldFont.getStringWidth(Integer.toString(mValue), lUiTextScale);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final float lLabelOffset = 0;
			if (mShowGuideValuesEnabled) {
				final float lLowerBoundStringWidth = lTextBoldFont.getStringWidth(Integer.toString(mLowerBound));
				lTextBoldFont.drawText(Integer.toString(mLowerBound), lScreenOffset.x + mBarPosX - lLowerBoundStringWidth / 2 + 16, lScreenOffset.y + mY + lLabelOffset + 16, mZ, lColorWhiteWithAlpha, 1f);
			}

			final float endPositionX = lCaretPos + 128.f + lValueStringWidth;
			final float lValueStringPositionX = endPositionX > mBarPosX + mBarWidth ? lCaretPos - 32.f - 5.f : lCaretPos + 32f;
			lTextBoldFont.drawText(lValueString, lScreenOffset.x + lValueStringPositionX, lScreenOffset.y + mY + mH * .5f - lLabelHeight * .5f + 16, mZ, lColorWhiteWithAlpha, lUiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = lTextBoldFont.getStringWidth(Integer.toString(mUpperBound));
				lTextBoldFont.drawText(Integer.toString(mUpperBound), lScreenOffset.x + mBarPosX + mBarWidth - lUpperBoundStringWidth / 2 - 48, lScreenOffset.y + mY + lLabelOffset + 16, mZ, lColorWhiteWithAlpha, 1f);
			}
		}

		lTextBoldFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsActive = !mIsActive;

		if (mIsActive)
			mParentScreen.onMenuEntryActivated(this);
		else
			mParentScreen.onMenuEntryDeactivated(this);

	}

	public void setValue(int newValue) {
		if (newValue < mLowerBound)
			newValue = mLowerBound;

		if (newValue > mUpperBound)
			newValue = mUpperBound;

		mValue = newValue;
	}
}
