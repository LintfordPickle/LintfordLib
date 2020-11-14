package net.lintford.library.screenmanager.entries;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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

	private TextureBatchPCT mTextureBatch;
	private Texture mUITexture;

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

		mTextureBatch = new TextureBatchPCT();

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);
		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

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

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);
					mParentLayout.parentScreen().setHoveringOn(this);

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
	public void updateStructure() {
		super.updateStructure();

		// TODO: This -50 is because of the scrollbar - this is why I needed to keep the padding :(
		w = Math.min(mParentLayout.w() - 50f, mMaxWidth);

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

		final var lParentScreen = mParentLayout.parentScreen();
		final var lFont = lParentScreen.font();
		if (lFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, lUiTextScale);
		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lLabelHeight = lFont.bitmap().fontHeight();

		final float yPos = y + h / 2f - lLabelHeight / 2f;

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			mTextureBatch.begin(pCore.HUD());
			final float lArrowButtonSize = 32;
			final float lArrowButtonPaddingX = mDownButton.w() - lArrowButtonSize;

			mTextureBatch.draw(mUITexture, 0, 224, 32, 32, mDownButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, 1f, 1f, 1f, 1f);
			mTextureBatch.draw(mUITexture, 32, 224, 32, 32, mUpButton.x() + lArrowButtonPaddingX, yPos, lArrowButtonSize, lArrowButtonSize, mZ, 1f, 1f, 1f, 1f);

			mTextureBatch.end();
		}

		// Draw the slider bar and caret
		mTextureBatch.begin(pCore.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 32);

		mTextureBatch.draw(mUITexture, 0, 192, 32, 32, mBarPosX, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);
		mTextureBatch.draw(mUITexture, 32, 192, 32, 32, mBarPosX + 32, yPos, mBarWidth - 64 - 32, 32, mZ, 1f, 1f, 1f, 1f);
		mTextureBatch.draw(mUITexture, 64, 192, 32, 32, mBarPosX + mBarWidth - 64, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);

		// Draw the caret
		mTextureBatch.draw(mUITexture, 192, 192, 32, 32, lCaretPos, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);

		mTextureBatch.end();

		// draw the label to the left and the value //
		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mLabel, x + w / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, pParentZDepth, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, pParentZDepth, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), lUiTextScale, -1);

		if (mShowValueEnabled) {
			float valueWith = lFont.bitmap().getStringWidth("" + mValue);
			float lowerWith = lFont.bitmap().getStringWidth("" + mLowerBound);
			float upperWith = lFont.bitmap().getStringWidth("" + mUpperBound);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final float lLabelOffset = 0;
			if (mShowGuideValuesEnabled)
				lFont.draw("" + mLowerBound, mBarPosX - lowerWith / 2 + 16, y + lLabelOffset, mZ, 1f);
			lFont.draw("" + lValueString, mBarPosX + mBarWidth / 2.f - valueWith / 2, y + lLabelOffset, mZ, 1f);
			if (mShowGuideValuesEnabled)
				lFont.draw("" + mUpperBound, mBarPosX + mBarWidth - upperWith / 2 - 48, y + lLabelOffset, mZ, 1f);
		}

		lFont.end();

		if (mShowInfoIcon) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(mUITexture, 192, 160, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			mTextureBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			mTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, ALPHA);
			mTextureBatch.end();

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
