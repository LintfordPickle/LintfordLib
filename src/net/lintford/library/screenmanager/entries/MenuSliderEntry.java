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
import net.lintford.library.screenmanager.layouts.BaseLayout;

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
	private int mStep;
	private boolean mButtonsEnabled;
	private boolean mShowValueEnabled;
	private boolean mShowGuideValuesEnabled;
	private boolean mShowUnit;

	private boolean mTrackingClick;
	private float mBarPosX;
	private float mBarWidth;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void showValueGuides(boolean pNewValue) {
		mShowGuideValuesEnabled = pNewValue;
	}

	public boolean showValueGuides() {
		return mShowGuideValuesEnabled;
	}

	public void showValueUnit(boolean pNewValue) {
		mShowUnit = pNewValue;
	}

	public boolean showValueUnit() {
		return mShowUnit;
	}

	public void setValueUnit(String pUnit) {
		mUnit = pUnit;
	}

	public void showValue(boolean pNewValue) {
		mShowValueEnabled = pNewValue;
	}

	public boolean showValue() {
		return mShowValueEnabled;
	}

	public void buttonsEnabled(boolean pNewValue) {
		mButtonsEnabled = pNewValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public int getCurrentValue() {
		return mValue;
	}

	public void setBounds(int pLow, int pHigh, int pStep) {
		mLowerBound = pLow;
		mUpperBound = pHigh;
		mStep = pStep;
		setValue(pHigh - pLow / 2);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuSliderEntry(ScreenManager pScreenManager, BaseLayout pParentLayout) {
		super(pScreenManager, pParentLayout, "");

		mLabel = "Label:";

		mDownButton = new Rectangle(0, 0, 32, 32);
		mUpButton = new Rectangle(0, 0, 32, 32);

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mEnabled) {

					// TODO: Play menu click sound

					if (mDownButton.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
						setValue(mValue - mStep);
					} else if (mUpButton.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
						setValue(mValue + mStep);
					} else {
						mTrackingClick = true;

					}

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mParentLayout.parentScreen.setFocusOn(pCore, this, true);
					mParentLayout.parentScreen.setHoveringOn(this);

				}

			} else {
				// mParentScreen.setHoveringOn(this);
				hasFocus(true);

			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.appTime().elapsedTimeMilli();
			}

		} else {
			mToolTipTimer = 0;

		}

		if (mTrackingClick && pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			mValue = (int) MathHelper.scaleToRange(pCore.HUD().getMouseCameraSpace().x - mBarPosX, 0, mBarWidth - 32 - 16, mLowerBound, mUpperBound);
			mValue = MathHelper.clampi(mValue, mLowerBound, mUpperBound);

			if (mClickListener != null) {
				mClickListener.menuEntryChanged(this);
			}

		} else {
			mTrackingClick = false;

		}

		return mTrackingClick;
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		mDownButton.setPosition(x + w / 2 + 16, y);
		mUpButton.setPosition(x + w - 32, y);

		mBarPosX = x + w / 2 + mDownButton.w() + 16;
		mBarWidth = w / 2 - 48;
	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final var lSpriteBatch = lParentScreen.spriteBatch();
		final var lUiTextScale = lParentScreen.uiTextScale();

		final var lLabelWidth = lFont.getStringWidth(mLabel, lUiTextScale);
		final var lSeparatorHalfWidth = lFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final var lLabelHeight = lFont.getStringHeight(mLabel, lUiTextScale);// ;

		final var lScreenOffset = pScreen.screenPositionOffset();
		final var lParentScreenAlpha = pScreen.screenColor.a;

		if (mHoveredOver & mEnabled) {
			final var lHighlightColor = ColorConstants.getColorWithAlpha(ColorConstants.MenuEntryHighlightColor, lParentScreenAlpha);
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - w / 2, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, lHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (w / 2) + 32, lScreenOffset.y + centerY() - h / 2, w - 64, h, mZ, lHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (w / 2) - 32, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, lHighlightColor);
			lSpriteBatch.end();
		}

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			lSpriteBatch.begin(pCore.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.w() - lArrowButtonSize;

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, lScreenOffset.x + mDownButton.x() + lArrowButtonPaddingX, lScreenOffset.y + y, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, lScreenOffset.x + mUpButton.x() + lArrowButtonPaddingX, lScreenOffset.y + y, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);

			lSpriteBatch.end();
		}

		// Draw the slider bar and caret
		lSpriteBatch.begin(pCore.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 32);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_LEFT, lScreenOffset.x + mBarPosX, lScreenOffset.y + y, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_MID, lScreenOffset.x + mBarPosX + 32, lScreenOffset.y + y, mBarWidth - 64 - 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_RIGHT, lScreenOffset.x + mBarPosX + mBarWidth - 64, lScreenOffset.y + y, 32, 32, mZ, entryColor);
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_NUBBLE, lScreenOffset.x + lCaretPos, lScreenOffset.y + y, 32, 32, mZ, entryColor);
		lSpriteBatch.end();

		final var lColorWhiteWithAlpha = ColorConstants.getWhiteWithAlpha(lParentScreenAlpha);

		// draw the label to the left and the value //
		lFont.begin(pCore.HUD());
		lFont.drawText(mLabel, lScreenOffset.x + x + w / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2f - lLabelHeight / 2f, mZ, lColorWhiteWithAlpha, lUiTextScale, -1);
		lFont.drawText(mSeparator, lScreenOffset.x + x + w / 2 - lSeparatorHalfWidth, lScreenOffset.y + y + h / 2f - lLabelHeight / 2f, mZ, lColorWhiteWithAlpha, lUiTextScale, -1);

		if (mShowValueEnabled) {
			final float lValueStringWidth = lFont.getStringWidth(Integer.toString(mValue), lUiTextScale);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final float lLabelOffset = 0;
			if (mShowGuideValuesEnabled) {
				final float lLowerBoundStringWidth = lFont.getStringWidth(Integer.toString(mLowerBound));
				lFont.drawText(Integer.toString(mLowerBound), lScreenOffset.x + mBarPosX - lLowerBoundStringWidth / 2 + 16, lScreenOffset.y + y + lLabelOffset, mZ, lColorWhiteWithAlpha, 1f);
			}

			final float endPositionX = lCaretPos + 128.f + lValueStringWidth;
			final float lValueStringPositionX = endPositionX > mBarPosX + mBarWidth ? lCaretPos - 32.f - 5.f : lCaretPos + 32f;
			lFont.drawText(lValueString, lScreenOffset.x + lValueStringPositionX, lScreenOffset.y + y + h * .5f - lLabelHeight * .5f, mZ, lColorWhiteWithAlpha, lUiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = lFont.getStringWidth(Integer.toString(mUpperBound));
				lFont.drawText(Integer.toString(mUpperBound), lScreenOffset.x + mBarPosX + mBarWidth - lUpperBoundStringWidth / 2 - 48, lScreenOffset.y + y + lLabelOffset, mZ, lColorWhiteWithAlpha, 1f);
			}
		}

		lFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);
		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);
		}

		if (!mEnabled) {
			drawdisabledBlackOverbar(pCore, lSpriteBatch, entryColor.a);
		}

		drawDebugCollidableBounds(pCore, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false;

		}
	}

	public void setValue(int pNewValue) {
		if (pNewValue < mLowerBound)
			pNewValue = mLowerBound;

		if (pNewValue > mUpperBound)
			pNewValue = mUpperBound;

		mValue = pNewValue;

	}
}
