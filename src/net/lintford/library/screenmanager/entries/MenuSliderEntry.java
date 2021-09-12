package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
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

		final var lTextureBatch = lParentScreen.textureBatch();
		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.getStringWidth(mLabel, lUiTextScale);
		final float lSeparatorHalfWidth = lFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lLabelHeight = lFont.getStringHeight(mLabel, lUiTextScale);// ;

		final float yPos = y;

		entryColor.setFromColor(lParentScreen.screenColor);

		if (mHoveredOver & mEnabled) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.end();

		}

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			lTextureBatch.begin(pCore.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.w() - lArrowButtonSize;

			lTextureBatch.draw(mUITexture, 0, 224, 32, 32, mDownButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);
			lTextureBatch.draw(mUITexture, 32, 224, 32, 32, mUpButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, entryColor);

			lTextureBatch.end();
		}

		// Draw the slider bar and caret
		lTextureBatch.begin(pCore.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 32);

		lTextureBatch.draw(mUITexture, 0, 192, 32, 32, mBarPosX, yPos, 32, 32, mZ, entryColor);
		lTextureBatch.draw(mUITexture, 32, 192, 32, 32, mBarPosX + 32, yPos, mBarWidth - 64 - 32, 32, mZ, entryColor);
		lTextureBatch.draw(mUITexture, 64, 192, 32, 32, mBarPosX + mBarWidth - 64, yPos, 32, 32, mZ, entryColor);

		// Draw the caret
		lTextureBatch.draw(mUITexture, 192, 192, 32, 32, lCaretPos, yPos, 32, 32, mZ, entryColor);

		lTextureBatch.end();

		// draw the label to the left and the value //
		lFont.begin(pCore.HUD());
		lFont.drawText(mLabel, x + w / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, mZ, ColorConstants.TextEntryColor, lUiTextScale, -1);
		lFont.drawText(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, mZ, ColorConstants.TextEntryColor, lUiTextScale, -1);

		if (mShowValueEnabled) {
			final float lValueStringWidth = lFont.getStringWidth(Integer.toString(mValue), lUiTextScale);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final float lLabelOffset = 0;
			if (mShowGuideValuesEnabled) {
				final float lLowerBoundStringWidth = lFont.getStringWidth(Integer.toString(mLowerBound));
				lFont.drawText(Integer.toString(mLowerBound), mBarPosX - lLowerBoundStringWidth / 2 + 16, y + lLabelOffset, mZ, ColorConstants.WHITE, 1f);
			}

			final float endPositionX = lCaretPos + 128.f + lValueStringWidth;
			final float lValueStringPositionX = endPositionX > mBarPosX + mBarWidth ? lCaretPos - 32.f - 5.f : lCaretPos + 32f;
			lFont.drawText(lValueString, lValueStringPositionX, y + h * .5f - lLabelHeight * .5f, mZ, ColorConstants.WHITE, lUiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = lFont.getStringWidth(Integer.toString(mUpperBound));
				lFont.drawText(Integer.toString(mUpperBound), mBarPosX + mBarWidth - lUpperBoundStringWidth / 2 - 48, y + lLabelOffset, mZ, ColorConstants.WHITE, 1f);
			}
		}

		lFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lTextureBatch, mInfoIconDstRectangle, entryColor.a);

		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lTextureBatch, mWarnIconDstRectangle, entryColor.a);

		}

		if (!mEnabled) {
			drawdisabledBlackOverbar(pCore, lTextureBatch, entryColor.a);
		}

		drawDebugCollidableBounds(pCore, lTextureBatch);

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
