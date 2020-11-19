package net.lintford.library.screenmanager.entries;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
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

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, lUiTextScale);
		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lLabelHeight = lFont.bitmap().getStringHeight(mLabel, lUiTextScale);//;

		final float yPos = y;

		entryColor.r = 1.f;
		entryColor.g = 1.f;
		entryColor.b = 1.f;
		entryColor.a = lParentScreen.a();

		if (mHoveredOver) {
			final float lHoveredColorHighlightR = 204.f / 255.f;
			final float lHoveredColorHighlightG = 115.f / 255.f;
			final float lHoveredColorHighlightB = 102.f / 255.f;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.end();

		}

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			lTextureBatch.begin(pCore.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.w() - lArrowButtonSize;

			lTextureBatch.draw(mUITexture, 0, 224, 32, 32, mDownButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);
			lTextureBatch.draw(mUITexture, 32, 224, 32, 32, mUpButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);

			lTextureBatch.end();
		}

		// Draw the slider bar and caret
		lTextureBatch.begin(pCore.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 32);

		lTextureBatch.draw(mUITexture, 0, 192, 32, 32, mBarPosX, yPos, 32, 32, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);
		lTextureBatch.draw(mUITexture, 32, 192, 32, 32, mBarPosX + 32, yPos, mBarWidth - 64 - 32, 32, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);
		lTextureBatch.draw(mUITexture, 64, 192, 32, 32, mBarPosX + mBarWidth - 64, yPos, 32, 32, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);

		// Draw the caret
		lTextureBatch.draw(mUITexture, 192, 192, 32, 32, lCaretPos, yPos, 32, 32, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);

		lTextureBatch.end();

		// draw the label to the left and the value //
		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mLabel, x + w / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, mZ, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);

		if (mShowValueEnabled) {
			final float lValueStringWidth = lFont.bitmap().getStringWidth(Integer.toString(mValue), lUiTextScale);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final float lLabelOffset = 0;
			if (mShowGuideValuesEnabled) {
				final float lLowerBoundStringWidth = lFont.bitmap().getStringWidth(Integer.toString(mLowerBound));
				lFont.draw(Integer.toString(mLowerBound), mBarPosX - lLowerBoundStringWidth / 2 + 16, y + lLabelOffset, mZ, 1f);
			}

			final float endPositionX = lCaretPos + 128.f + lValueStringWidth;
			final float lValueStringPositionX = endPositionX > mBarPosX + mBarWidth ? lCaretPos - 32.f - 5.f : lCaretPos + 32f;
			lFont.draw(lValueString, lValueStringPositionX, y + h * .5f - lLabelHeight * .5f, mZ, lUiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = lFont.bitmap().getStringWidth(Integer.toString(mUpperBound));
				lFont.draw(Integer.toString(mUpperBound), mBarPosX + mBarWidth - lUpperBoundStringWidth / 2 - 48, y + lLabelOffset, mZ, 1f);
			}
		}

		lFont.end();

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lTextureBatch, mInfoIconDstRectangle, entryColor.a);

		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lTextureBatch, mWarnIconDstRectangle, entryColor.a);

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			final float lStringAlpha = 0.3f;
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, lStringAlpha);
			lTextureBatch.end();

		}

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
