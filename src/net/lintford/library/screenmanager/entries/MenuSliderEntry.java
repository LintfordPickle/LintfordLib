package net.lintford.library.screenmanager.entries;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
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

	private TextureBatch mTextureBatch;
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

		mTextureBatch = new TextureBatch();

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

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().isMouseTimedLeftClickAvailable()) {
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

					pCore.input().setLeftMouseClickHandled();

				}
			} else {
				// mParentScreen.setHoveringOn(this);
				hasFocus(true);

			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
			}

		} else {
			mToolTipTimer = 0;

		}

		if (mTrackingClick && pCore.input().mouseLeftClick()) {
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
	public void updateStructureDimensions() {
		// TODO: This -50 is because of the scrollbar - this is why I needed to keep the padding :(
		w = Math.min(mParentLayout.w - 50f, MENUENTRY_MAX_WIDTH);

	}

	@Override
	public void updateStructurePositions() {
		super.updateStructurePositions();

		if (mShowInfoButton) {
			mInfoButton.set(x, y, 32f, 32f);

		}

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		final float yPos = mShowGuideValuesEnabled ? y + 32 : y;

		mDownButton.x = x + w / 2 + 16;
		mDownButton.y = yPos;
		mUpButton.x = x + w - 32;
		mUpButton.y = yPos;

		mBarPosX = x + w / 2 + mDownButton.w + 16;
		mBarWidth = w / 2 - 48;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final FontUnit lFont = lParentScreen.font();

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, luiTextScale);
		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, luiTextScale) * 0.5f;
		final float lLabelHeight = lFont.bitmap().fontHeight();

		final float yPos = mShowGuideValuesEnabled ? y + h / 2f - lLabelHeight / 2f + 32 : y + h / 2f - lLabelHeight / 2f;

		if (mButtonsEnabled) {
			// Draw the left/right buttons
			mTextureBatch.begin(pCore.HUD());
			final float ARROW_BUTTON_SIZE = 32;
			final float ARROW_PADDING_X = mDownButton.w - ARROW_BUTTON_SIZE;
			final float ARROW_PADDING_Y = mDownButton.h - ARROW_BUTTON_SIZE;

			mTextureBatch.draw(mUITexture, 160, 0, 32, 32, mDownButton.x + ARROW_PADDING_X, yPos + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);
			mTextureBatch.draw(mUITexture, 224, 0, 32, 32, mUpButton.x + ARROW_PADDING_X, yPos + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, mZ, 1f, 1f, 1f, 1f);

			mTextureBatch.end();
		}

		// Draw the slider bar and caret
		mTextureBatch.begin(pCore.HUD());

		final float lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, mBarPosX, mBarWidth - 16);

		mTextureBatch.draw(mUITexture, 0, 192, 32, 32, mBarPosX, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);
		mTextureBatch.draw(mUITexture, 32, 192, 32, 32, mBarPosX + 32, yPos, mBarWidth - 64 - 32, 32, mZ, 1f, 1f, 1f, 1f);
		mTextureBatch.draw(mUITexture, 64, 192, 32, 32, mBarPosX + mBarWidth - 64, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);

		// Draw the caret
		mTextureBatch.draw(mUITexture, 192, 192, 32, 32, lCaretPos, yPos, 32, 32, mZ, 1f, 1f, 1f, 1f);

		mTextureBatch.end();

		// draw the label to the left and the value //
		lFont.begin(pCore.HUD());
		lFont.draw(mLabel, x + w / 2 - lLabelWidth - 10 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, pParentZDepth, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), luiTextScale, -1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2f - lLabelHeight / 2f, pParentZDepth, lParentScreen.r(), lParentScreen.g(), lParentScreen.b(), lParentScreen.a(), luiTextScale, -1);

		if (mShowValueEnabled) {
			float valueWith = lFont.bitmap().getStringWidth("" + mValue);
			float lowerWith = lFont.bitmap().getStringWidth("" + mLowerBound);
			float upperWith = lFont.bitmap().getStringWidth("" + mUpperBound);

			String lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}
			if (mShowGuideValuesEnabled)
				lFont.draw("" + mLowerBound, mBarPosX - lowerWith / 2 + 16, y + 2, -2f, 1f);
			lFont.draw("" + lValueString, centerX() + w / 4 - valueWith / 2, yPos + 2 - 8f, -2f, 1f);
			if (mShowGuideValuesEnabled)
				lFont.draw("" + mUpperBound, mBarPosX + mBarWidth - upperWith / 2 - 48, y + 2, -2f, 1f);
		}

		lFont.end();

		if (mShowInfoButton) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(mUITexture, 544, 0, 32, 32, mInfoButton, mZ, 1f, 1f, 1f, 1f);
			mTextureBatch.end();
		}

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
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
	public void onClick(InputState pInputState) {
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
