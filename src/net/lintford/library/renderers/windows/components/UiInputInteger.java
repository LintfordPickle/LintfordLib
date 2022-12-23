package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IBufferedTextInputCallback;
import net.lintford.library.core.input.IUiInputKeyPressCallback;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.windows.ConstantsUi;
import net.lintford.library.renderers.windows.UiWindow;

public class UiInputInteger extends UIWidget implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mMaxInputCharacters;
	private transient boolean mHasFocus;
	private transient float mCaretFlashTimer;
	private transient boolean mShowCaret;
	private transient String mTempValue;
	private transient String mEmptyString;

	private transient Rectangle mCancelRectangle;
	private IUiInputKeyPressCallback mIUiInputKeyPressCallback;

	// A little wierd, we store the string length to check if the string has changed since the last frame (since
	// working with the length (int) doesn't cause a heap allocation as toString() does )
	private transient int mStringLength;
	private transient StringBuilder mInputField;
	private transient boolean mResetOnDefaultClick;
	private boolean mMouseClickBreaksInputTextFocus;
	private boolean mCancelRectHovered;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int maxnumInputCharacters() {
		return mMaxInputCharacters;
	}

	public void maxnumInputCharacters(int maxNumInputCharacters) {
		mMaxInputCharacters = maxNumInputCharacters;
	}

	public boolean mouseClickBreaksInputTextFocus() {
		return mMouseClickBreaksInputTextFocus;
	}

	public void mouseClickBreaksInputTextFocus(boolean newValue) {
		mMouseClickBreaksInputTextFocus = newValue;
	}

	public String emptyString() {
		return mEmptyString;
	}

	public void emptyString(String newValue) {
		if (newValue == null)
			mEmptyString = "";
		else
			mEmptyString = newValue;
	}

	public int stringLength() {
		return mStringLength;
	}

	public boolean isEmpty() {
		return isEmptyString();
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean v) {
		mHasFocus = v;
	}

	public void inputString(String newValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		mInputField.append(newValue);
	}

	public StringBuilder inputString() {
		return mInputField;
	}

	public boolean isEmptyString() {
		return mInputField.toString().equals(mEmptyString);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiInputInteger(UiWindow parentWindow) {
		super(parentWindow);

		mResetOnDefaultClick = true;
		mInputField = new StringBuilder();

		mCancelRectangle = new Rectangle();
		mEmptyString = "";
		mMaxInputCharacters = 15;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		if (mCancelRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mCancelRectHovered = true;
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mInputField.length() > 0) {

					if (mInputField.length() > 0)
						mInputField.delete(0, mInputField.length());
					mStringLength = 0;

					core.input().keyboard().stopBufferedTextCapture();

					mHasFocus = false;
					mShowCaret = false;
				}
			}
		} else {
			mCancelRectHovered = false;
		}

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				onClick(core.input());
				mHasFocus = true;

				return true;
			}
		}

		// Stop the keyboard capture if the player clicks somewhere else within the game
		if (mHasFocus && mMouseClickBreaksInputTextFocus && (core.input().mouse().isMouseLeftButtonDownTimed(this) || core.input().mouse().isMouseRightButtonDownTimed(this))) {
			core.input().keyboard().stopBufferedTextCapture();

			mHasFocus = false;
			mShowCaret = false;
		}

		return false;
	}

	public void update(LintfordCore core) {
		super.update(core);

		final float lCanvasScale = mParentWindow != null ? mParentWindow.uiStructureController().gameCanvasWScaleFactor() : 1.0f;
		mCaretFlashTimer += core.appTime().elapsedTimeMilli();

		// TODO: Make the icon sizes a UiConstant
		final int lCancelRectSize = 16;
		mCancelRectangle.set(mX + mW - lCancelRectSize * lCanvasScale, mY + mH / 2 * lCanvasScale - lCancelRectSize / 2 * lCanvasScale, lCancelRectSize * lCanvasScale, lCancelRectSize * lCanvasScale);

		if (mHasFocus) {
			// flash and update the location of the caret
			if (mCaretFlashTimer > ConstantsUi.CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}

			// Limit the number of characters which can be entered
			if (mInputField.length() > mMaxInputCharacters)
				mInputField.delete(mMaxInputCharacters, mInputField.length() - 1);
		}
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lCanvasScale = mParentWindow != null ? mParentWindow.uiStructureController().gameCanvasWScaleFactor() : 1.0f;

		// Renders the background of the input text widget
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, (int) mX, mY, 32 * lCanvasScale, mH * lCanvasScale, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		if (mW > 32) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, (int) mX + 32 * lCanvasScale, mY, mW - 64 * lCanvasScale, mH * lCanvasScale, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, (int) mX + mW - 32 * lCanvasScale, mY, 32 * lCanvasScale, mH * lCanvasScale, componentZDepth, ColorConstants.MenuPanelPrimaryColor);
		}

		// Draw the cancel button rectangle
		final var lEraserColor = mCancelRectHovered ? ColorConstants.WHITE : ColorConstants.getWhiteWithAlpha(.5f);
		spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_ERASER, mCancelRectangle, componentZDepth, lEraserColor);

		final float lInputTextWidth = textFont.getStringWidth(mInputField.toString());

		String lText = mInputField.toString();
		final float lTextHeight = textFont.fontHeight();
		if (lText.length() == 0 && !mHasFocus) {
			if (mEmptyString.isEmpty()) {
				lText = "<search>";
			} else {
				lText = mEmptyString;
			}
		}

		textFont.drawText(lText, mX + 10, mY + mH * .5f - lTextHeight * .5f, componentZDepth, ColorConstants.TextEntryColor, lCanvasScale, -1);
		if (mShowCaret && mHasFocus) {
			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + lInputTextWidth * lCanvasScale + 10, mY + mH * .5f * lCanvasScale - lTextHeight * .5f * lCanvasScale, textFont.fontHeight() / 2.f * lCanvasScale,
					textFont.fontHeight() * lCanvasScale, componentZDepth, ColorConstants.WHITE);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setKeyUpdateListener(IUiInputKeyPressCallback keyUpdateListener) {
		mIUiInputKeyPressCallback = keyUpdateListener;
	}

	public void onClick(InputManager inputState) {
		mHasFocus = !mHasFocus;

		if (mHasFocus) {
			inputState.keyboard().startBufferedTextCapture(this);

			if (mInputField.length() > 0)
				mTempValue = mInputField.toString();

			if (mResetOnDefaultClick && mInputField.toString().equals(mEmptyString)) {
				if (mInputField.length() > 0) {
					mInputField.delete(0, mInputField.length());
				}
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

		return getEnterFinishesInput();
	}

	@Override
	public void onKeyPressed(int codePoint) {
		mStringLength = mInputField.length();

		if (mIUiInputKeyPressCallback != null) {
			mIUiInputKeyPressCallback.keyPressUpdate(codePoint);
		}
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
		if (mTempValue != null && mTempValue.length() == 0) {
			mInputField.append(mTempValue);
		}

		mStringLength = 0;

		mHasFocus = false;
		mShowCaret = false;

		return getEscapeFinishesInput();
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}
}