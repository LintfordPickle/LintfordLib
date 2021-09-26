package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IBufferedTextInputCallback;
import net.lintford.library.core.input.IUiInputKeyPressCallback;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.windows.UiWindow;

public class UIInputText extends UIWidget implements IBufferedTextInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	private static final float CARET_FLASH_TIME = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient boolean mHasFocus;
	private transient float mCaretFlashTimer;
	private transient boolean mShowCaret;
	private transient String mTempString;
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

	public boolean mouseClickBreaksInputTextFocus() {
		return mMouseClickBreaksInputTextFocus;
	}

	public void mouseClickBreaksInputTextFocus(boolean pNewValue) {
		mMouseClickBreaksInputTextFocus = pNewValue;
	}

	public String emptyString() {
		return mEmptyString;
	}

	public void emptyString(String pNewString) {
		if (pNewString == null)
			mEmptyString = "";
		else
			mEmptyString = pNewString;
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

	public void inputString(String pNewValue) {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		mInputField.append(pNewValue);
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

	public UIInputText(UiWindow pParentWindow) {
		super(pParentWindow);

		mResetOnDefaultClick = true;
		mInputField = new StringBuilder();

		mCancelRectangle = new Rectangle();
		mEmptyString = "";

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		if (mCancelRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mCancelRectHovered = true;
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mInputField.length() > 0) {

					if (mInputField.length() > 0)
						mInputField.delete(0, mInputField.length());
					mStringLength = 0;

					pCore.input().keyboard().stopBufferedTextCapture();

					mHasFocus = false;
					mShowCaret = false;

				}
			}
		} else {
			mCancelRectHovered = false;
		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				onClick(pCore.input());
				mHasFocus = true;

				return true;

			}

		}

		// Stop the keyboard capture if the player clicks somewhere else within the game
		if (mHasFocus && mMouseClickBreaksInputTextFocus && (pCore.input().mouse().isMouseLeftButtonDownTimed(this) || pCore.input().mouse().isMouseRightButtonDownTimed(this))) {
			pCore.input().keyboard().stopBufferedTextCapture();

			mHasFocus = false;
			mShowCaret = false;

		}

		return false;

	}

	public void update(LintfordCore pCore) {
		super.update(pCore);

		mCaretFlashTimer += pCore.appTime().elapsedTimeMilli();

		final int lHorizontalPadding = 5;
		final int lCancelRectSize = 16;
		mCancelRectangle.set(x + w - lCancelRectSize - lHorizontalPadding, y + h / 2 - lCancelRectSize / 2, lCancelRectSize, lCancelRectSize);

		if (mHasFocus) {
			// flash and update the location of the caret
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}

			// Limit the number of characters which can be entered
			if (mInputField.length() > 15)
				mInputField.delete(15, mInputField.length() - 1);

		}

	}

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		// Renders the background of the input text widget
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_LEFT, x, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
		if (w > 32) {
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_MID, x + 32, y, w - 64, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_INPUT_FIELD_RIGHT, x + w - 32, y, 32, h, pComponentZDepth, ColorConstants.MenuPanelPrimaryColor);
		}

		// Draw the cancel button rectangle
		final var lEraserColor = mCancelRectHovered ? ColorConstants.WHITE : ColorConstants.getWhiteWithAlpha(.5f);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_ERASER, mCancelRectangle, pComponentZDepth, lEraserColor);

		final float lInputTextWidth = pTextFont.getStringWidth(mInputField.toString());

		String lText = mInputField.toString();
		final float lTextHeight = pTextFont.fontHeight();
		if (lText.length() == 0 && !mHasFocus) {
			if (mEmptyString.isEmpty()) {
				lText = "<search>";
			} else {
				lText = mEmptyString;
			}
		}

		pTextFont.drawText(lText, x + 10, y + h * .5f - lTextHeight * .5f, pComponentZDepth, ColorConstants.TextEntryColor, 1f, -1);
		if (mShowCaret && mHasFocus) {
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x + 10 + lInputTextWidth, y + h * .5f - lTextHeight * .5f, pTextFont.fontHeight() / 2.f, pTextFont.fontHeight(), pComponentZDepth, ColorConstants.WHITE);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setKeyUpdateListener(IUiInputKeyPressCallback pKeyUpdateListener) {
		mIUiInputKeyPressCallback = pKeyUpdateListener;

	}

	public void onClick(InputManager pInputState) {
		mHasFocus = !mHasFocus;

		if (mHasFocus) {
			pInputState.keyboard().startBufferedTextCapture(this);

			// Store the current string in case the user cancels the input, in which case, we
			// can restore the previous entry.
			if (mInputField.length() > 0)
				mTempString = mInputField.toString();

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
	public void onKeyPressed(int pCodePoint) {
		mStringLength = mInputField.length();

		if (mIUiInputKeyPressCallback != null) {
			mIUiInputKeyPressCallback.keyPressUpdate(pCodePoint);

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
		if (mTempString != null && mTempString.length() == 0) {
			mInputField.append(mTempString);
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
