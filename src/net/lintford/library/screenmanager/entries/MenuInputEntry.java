package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.input.keyboard.IBufferedTextInputCallback;
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

	public void scaleTextToWidth(boolean newValue) {
		mEnableScaleTextToWidth = newValue;
	}

	@Override
	public void hasFocus(boolean newValue) {
		if (!mFocusLocked)
			super.hasFocus(newValue);
	}

	@Override
	public boolean hasFocus() {
		return super.hasFocus();
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public String entryText() {
		return inputString();
	}

	public void inputString(String newValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		if (newValue != null)
			mInputField.append(newValue);

	}

	public String inputString() {
		return mInputField.toString();
	}

	public void setDefaultText(String text, boolean resetOnClick) {
		mDefaultText = text;
		mResetOnDefaultClick = resetOnClick;
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}

		mInputField.append(mDefaultText);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuInputEntry(ScreenManager screenManager, BaseLayout parentlayout) {
		super(screenManager, parentlayout, "");

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
	public boolean handleInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		boolean lResult = super.handleInput(core);
		if (mHasFocus)
			core.input().keyboard().startBufferedTextCapture(this);

		return lResult;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen, boolean isSelected) {
		super.update(core, screen, isSelected);

		if (!mEnabled) {
			mHasFocus = false;
			return;
		}

		final double lDeltaTime = core.appTime().elapsedTimeMilli();

		mCaretFlashTimer += lDeltaTime;

		if (mHasFocus) {
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}
		}
	}

	@Override
	public void draw(LintfordCore core, Screen screen, boolean isSelected, float parentZDepth) {
		if (!mActiveUpdateDraw)
			return;

		final var lParentScreen = mParentLayout.parentScreen;
		final var lTextBoldFont = lParentScreen.fontBold();
		final var lScreenOffset = screen.screenPositionOffset();
		final var lSpriteBatch = lParentScreen.spriteBatch();

		mZ = parentZDepth;

		if (lTextBoldFont == null)
			return;

		final float lUiTextScale = lParentScreen.uiTextScale();
		final float lLabelTextWidth = lTextBoldFont.getStringWidth(mLabel, lUiTextScale);

		float lAdjustedLabelScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lLabelTextWidth && lLabelTextWidth > 0)
			lAdjustedLabelScaleW = (mW * 0.4f) / lLabelTextWidth;

		entryColor.r = mHoveredOver ? (204.f / 255.f) : .1f;
		entryColor.g = mHoveredOver ? (115.f / 255.f) : .1f;
		entryColor.b = mHoveredOver ? (102.f / 255.f) : .1f;

		if (mHoveredOver) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		final float lLabelTextHeight = lTextBoldFont.fontHeight() * lAdjustedLabelScaleW;
		final float lSeparatorHalfWidth = lTextBoldFont.getStringWidth(mSeparator, lUiTextScale) * 0.5f;
		final float lInputTextWidth = lTextBoldFont.getStringWidth(mInputField.toString(), lUiTextScale);

		float lAdjustedLInputScaleW = lUiTextScale;
		if (mEnableScaleTextToWidth && mW * 0.4f < lInputTextWidth && lInputTextWidth > 0)
			lAdjustedLInputScaleW = (mW * 0.4f) / lInputTextWidth;

		final float lInputTextHeight = lTextBoldFont.fontHeight() * lAdjustedLInputScaleW;

		entryColor.r = mEnabled ? 1f : 0.6f;
		entryColor.g = mEnabled ? 1f : 0.6f;
		entryColor.b = mEnabled ? 1f : 0.6f;
		textColor.a = lParentScreen.screenColor.a;

		lTextBoldFont.begin(core.HUD());
		lTextBoldFont.drawText(mLabel, lScreenOffset.x + mX + mW / 2 - 10 - (lLabelTextWidth * lAdjustedLabelScaleW) - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lLabelTextHeight * 0.5f, parentZDepth + .1f, textColor, lAdjustedLabelScaleW, -1);
		lTextBoldFont.drawText(mSeparator, lScreenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, lScreenOffset.y + mY + mH / 2 - lLabelTextHeight * 0.5f, parentZDepth + .1f, textColor, lUiTextScale, -1);
		lTextBoldFont.drawText(mInputField.toString(), lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth * lAdjustedLInputScaleW + SPACE_BETWEEN_TEXT, lScreenOffset.y + mY + mH / 2 - lInputTextHeight * 0.5f, parentZDepth + .1f, textColor,
				lAdjustedLInputScaleW, -1);

		final float lTextHeight = lTextBoldFont.fontHeight();

		if (mShowCaret && mHasFocus) {
			lSpriteBatch.begin(core.HUD());
			final float lCaretPositionX = lScreenOffset.x + mX + mW / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT + lInputTextWidth * lAdjustedLInputScaleW;
			final float lCaretPositionY = lScreenOffset.y + mY + mH / 2 - lTextHeight / 2.f;
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lCaretPositionX, lCaretPositionY, lTextHeight / 2.f, lTextHeight, mZ, ColorConstants.WHITE);
			lSpriteBatch.end();
		}

		lTextBoldFont.end();

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, 1.f);

		drawDebugCollidableBounds(core, lSpriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputState) {
		super.onClick(inputState);

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

		if (mResetOnDefaultClick && mInputField.length() == 0)
			setDefaultText(mDefaultText, true);

		return getEnterFinishesInput();
	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
	}

	@Override
	public boolean onEscapePressed() {
		if (mInputField.length() > 0)
			mInputField.delete(0, mInputField.length());

		if (mTempString != null && mTempString.length() == 0)
			mInputField.append(mTempString);

		mHasFocus = false;
		mShowCaret = false;

		return getEscapeFinishesInput();
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

	@Override
	public void onKeyPressed(int codePoint) {

	}
}
