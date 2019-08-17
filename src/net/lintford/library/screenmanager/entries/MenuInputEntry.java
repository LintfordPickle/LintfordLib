package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.input.IBufferedInputCallback;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuInputEntry extends MenuEntry implements IBufferedInputCallback {

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

		mCanHoverOver = false;

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
			pCore.input().startCapture(this);

		} else {
			// pCore.input().stopCapture();

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

		final double lDeltaTime = pCore.time().elapseGameTimeMilli();

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

		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final MenuScreen lParentScreen = mParentLayout.parentScreen();
		final FontUnit lFont = lParentScreen.font();

		if (lFont == null)
			return;

		final float lLabelTextWidth = lFont.bitmap().getStringWidth(mLabel, luiTextScale);
		float lAdjustedLabelScaleW = luiTextScale;
		if (mEnableScaleTextToWidth && w * 0.4f < lLabelTextWidth && lLabelTextWidth > 0)
			lAdjustedLabelScaleW = (w * 0.4f) / lLabelTextWidth;

		final float lLabelTextHeight = lFont.bitmap().fontHeight() * lAdjustedLabelScaleW;

		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(mSeparator, luiTextScale) * 0.5f;
		final float lInputTextWidth = lFont.bitmap().getStringWidth(mInputField.toString(), luiTextScale);

		float lAdjustedLInputScaleW = luiTextScale;
		if (mEnableScaleTextToWidth && w * 0.4f < lInputTextWidth && lInputTextWidth > 0)
			lAdjustedLInputScaleW = (w * 0.4f) / lInputTextWidth;

		final float lInputTextHeight = lFont.bitmap().fontHeight() * lAdjustedLInputScaleW;

		float r = mEnabled ? 1f : 0.6f;
		float g = mEnabled ? 1f : 0.6f;
		float b = mEnabled ? 1f : 0.6f;

		lFont.begin(pCore.HUD());
		lFont.draw(mLabel, x + w / 2 - 10 - (lLabelTextWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, y + h / 2 - lLabelTextHeight * 0.5f, pParentZDepth + .1f, r, g, b, 1f, lAdjustedLabelScaleW, -1);
		lFont.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lLabelTextHeight * 0.5f, pParentZDepth + .1f, r, g, b, 1f, luiTextScale, -1);
		lFont.draw(mInputField.toString(), x + w / 2 + lSeparatorHalfWidth * lAdjustedLInputScaleW + SPACE_BETWEEN_TEXT, y + h / 2 - lInputTextHeight * 0.5f, pParentZDepth + .1f, r, g, b, 1f, lAdjustedLInputScaleW, -1);

		if (mShowCaret && mHasFocus) {
			lFont.draw("|", x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT + lInputTextWidth * lAdjustedLInputScaleW, y + h / 2 - lInputTextHeight * 0.5f, pParentZDepth + .1f, lAdjustedLInputScaleW);

		}

		lFont.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputState pInputState) {
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
	public void onKeyPressed(char pCh) {

	}

}
