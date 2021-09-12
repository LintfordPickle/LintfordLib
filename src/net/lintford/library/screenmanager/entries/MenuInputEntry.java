package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.input.IBufferedTextInputCallback;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuInputEntry extends MenuEntry implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3017844090126571950L;

	private static final float SPACE_BETWEEN_TEXT = 15;
	private static final float CARET_FLASH_TIME = 250; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private String mDefaultText;
	private final String mSeparator = " : ";
	private float mCaretFlashTimer;
	private boolean mShowCaret;
	private String mTempString;
	private boolean mEnableScaleTextToWidth;

	private StringBuilder mInputField;
	private boolean mResetOnDefaultClick;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean pNewValue) {
		mEnableScaleTextToWidth = pNewValue;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		if (!mFocusLocked)
			super.hasFocus(pNewValue);

	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public String entryText() {
		return inputString();
	}

	public void inputString(String pNewValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		if (pNewValue != null)
			mInputField.append(pNewValue);

	}

	public String inputString() {
		return mInputField.toString();
	}

	public void setDefaultText(String pText, boolean pResetOnClick) {
		mDefaultText = pText;
		mResetOnDefaultClick = pResetOnClick;
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}

		mInputField.append(mDefaultText);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuInputEntry(ScreenManager pScreenManager, BaseLayout pParentlayout) {
		super(pScreenManager, pParentlayout, "");

		mLabel = "Label:";
		mResetOnDefaultClick = true;

		mDrawBackground = false;
		mHighlightOnHover = false;
		mCanHoverOver = true;
		mEnableScaleTextToWidth = true;

		mInputField = new StringBuilder();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mEnabled)
			return false;

		boolean lResult = super.handleInput(pCore);

		if (mHasFocus) {
			pCore.input().keyboard().startBufferedTextCapture(this);

		}

		return lResult;
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		if (!mEnabled) {
			mHasFocus = false;
			return;
		}

		final double lDeltaTime = pCore.appTime().elapsedTimeMilli();

		mCaretFlashTimer += lDeltaTime;

		if (mHasFocus) {
			// flash and update the location of the carot
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}

		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!mActive)
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lFont = lParentScreen.font();
		final var lTextureBatch = lParentScreen.textureBatch();

		mZ = pParentZDepth;

		if (lFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();
		final float lLabelTextWidth = lFont.getStringWidth(mLabel, lUiTextScale);

		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && w * 0.4f < lLabelTextWidth && lLabelTextWidth > 0)
			lAdjustedLabelScaleW = (w * 0.4f) / lLabelTextWidth;

		entryColor.r = mHoveredOver ? (204.f / 255.f) : .1f;
		entryColor.g = mHoveredOver ? (115.f / 255.f) : .1f;
		entryColor.b = mHoveredOver ? (102.f / 255.f) : .1f;
		entryColor.a = mHoveredOver ? lParentScreen.screenColor.a : 0.26f;

		if (mHoveredOver) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, ColorConstants.MenuEntryHighlightColor);
			lTextureBatch.end();

		}

		final float lLabelTextHeight = lFont.fontHeight() * lAdjustedLabelScaleW;

		final float lSeparatorHalfWidth = lFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lInputTextWidth = lFont.getStringWidth(mInputField.toString(), lUiTextScale);

		float lAdjustedLInputScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && w * 0.4f < lInputTextWidth && lInputTextWidth > 0)
			lAdjustedLInputScaleW = (w * 0.4f) / lInputTextWidth;

		final float lInputTextHeight = lFont.fontHeight() * lAdjustedLInputScaleW;

		entryColor.r = mEnabled ? 1f : 0.6f;
		entryColor.g = mEnabled ? 1f : 0.6f;
		entryColor.b = mEnabled ? 1f : 0.6f;

		lFont.begin(pCore.HUD());
		lFont.drawText(mLabel, x + w / 2 - 10 - (lLabelTextWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, y + h / 2 - lLabelTextHeight * 0.5f, pParentZDepth + .1f, textColor, lAdjustedLabelScaleW, -1);
		lFont.drawText(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lLabelTextHeight * 0.5f, pParentZDepth + .1f, textColor, lUiTextScale, -1);
		lFont.drawText(mInputField.toString(), x + w / 2 + lSeparatorHalfWidth * lAdjustedLInputScaleW + SPACE_BETWEEN_TEXT, y + h / 2 - lInputTextHeight * 0.5f, pParentZDepth + .1f, textColor, lAdjustedLInputScaleW,
				-1);

		final float lTextHeight = lFont.fontHeight();

		if (mShowCaret && mHasFocus) {
			lTextureBatch.begin(pCore.HUD());
			final float lCaretPositionX = x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT + lInputTextWidth * lAdjustedLInputScaleW;
			final float lCaretPositionY = y + h / 2 - lTextHeight / 2.f;
			lTextureBatch.draw(mUITexture, 0, 0, lTextHeight / 2.f, lTextHeight, lCaretPositionX, lCaretPositionY, lTextHeight / 2.f, lTextHeight, mZ, ColorConstants.WHITE);
			lTextureBatch.end();

		}

		lFont.end();

		if (!mEnabled) {
			drawdisabledBlackOverbar(pCore, lTextureBatch, entryColor.a);

		}

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lTextureBatch, mInfoIconDstRectangle, 1.f);

		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lTextureBatch, mWarnIconDstRectangle, 1.f);

		}

		drawDebugCollidableBounds(pCore, lTextureBatch);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

		// Store the current string in case the user cancels the input, in which case, we
		// can restore the previous entry.
		if (mInputField.length() > 0)
			mTempString = mInputField.toString();

		if (mResetOnDefaultClick && mInputField.toString().equals(mDefaultText)) {
			if (mInputField.length() > 0) {
				mInputField.delete(0, mInputField.length());

			}

		}

	}

	@Override
	public StringBuilder getStringBuilder() {
		return mInputField;
	}

	@Override
	public boolean onEnterPressed() {
		mHasFocus = false;
		mShowCaret = false;

		if (mResetOnDefaultClick && mInputField.length() == 0) {
			setDefaultText(mDefaultText, true);
		}

		return getEnterFinishesInput();

	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
	}

	@Override
	public boolean onEscapePressed() {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		if (mTempString != null && mTempString.length() == 0) {
			mInputField.append(mTempString);
		}

		mHasFocus = false;
		mShowCaret = false;

		return getEscapeFinishesInput();

	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public void onKeyPressed(int pCodePoint) {

	}

}
