package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IBufferedInputCallback;
import net.lintford.library.core.input.InputState;

public class UIInputText extends AARectangle implements IBufferedInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float SPACE_BETWEEN_TEXT = 1;
	private static final float CARET_FLASH_TIME = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient boolean mHasFocus;
	private transient float mCaretFlashTimer;
	private transient boolean mShowCaret;
	private transient String mTempString;
	private transient String mEmptyString;

	private transient AARectangle mCancelRectangle;

	// A little wierd, we store the string length to check if the string has changed since the last frame (since
	// working with the length (int) doesn't cause a heap allocation as toString() does )
	private transient int mStringLength;
	private transient StringBuilder mInputField;
	private transient boolean mResetOnDefaultClick;

	// --------------------------------------
	// Properties
	// --------------------------------------

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
		return mInputField.toString().equals(mEmptyString);
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIInputText() {
		mResetOnDefaultClick = true;
		mInputField = new StringBuilder();

		mCancelRectangle = new AARectangle();
		mEmptyString = "";

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().isMouseTimedLeftClickAvailable()) {
			if (mCancelRectangle.intersects(pCore.HUD().getMouseCameraSpace())) {
				if (mInputField.length() > 0) {
					pCore.input().mouseTimedLeftClick();
					if (mInputField.length() > 0)
						mInputField.delete(0, mInputField.length());
					mStringLength = 0;

					mInputField.append(mEmptyString);

					pCore.input().stopCapture();

					mHasFocus = false;
					mShowCaret = false;

				}
			}
		}

		if (pCore.input().isMouseTimedLeftClickAvailable()) {
			if (intersects(pCore.HUD().getMouseCameraSpace())) {
				pCore.input().mouseTimedLeftClick();

				onClick(pCore.input());

				return true;

			} else if (mHasFocus) {
				mHasFocus = false;

				return false;
			}

		}

		return false;
	}

	public void update(LintfordCore pCore) {
		mCaretFlashTimer += pCore.time().elapseGameTimeMilli();

		final int lCANCEL_RECT_SIZE = 24;
		mCancelRectangle.x = x + w - lCANCEL_RECT_SIZE - 2;
		mCancelRectangle.y = y + 2;
		mCancelRectangle.w = lCANCEL_RECT_SIZE;
		mCancelRectangle.h = lCANCEL_RECT_SIZE;

		if (mHasFocus) {
			// flash and update the location of the caret
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}
		}

	}

	public void draw(LintfordCore pCore, TextureBatch pUISpriteBatch, FontUnit pTextFont) {
		pUISpriteBatch.begin(pCore.HUD());
		pUISpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 64, 0, 32, 32, x, y + 3, w, h - 6, -0.1f, 1f, 1f, 1f, 1);
		pUISpriteBatch.end();

		// Draw the cancel button rectangle
		pUISpriteBatch.begin(pCore.HUD());
		pUISpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 288, 64, 32, 32, mCancelRectangle.x, mCancelRectangle.y, mCancelRectangle.w, mCancelRectangle.h, -0.1f, 1f, 1f, 1f, 1);
		pUISpriteBatch.end();

		final float lInputTextWidth = pTextFont.bitmap().getStringWidth(mInputField.toString());

		String lText = mInputField.toString();
		float lAlpha = 1f;
		if (lText.length() == 0) {
			lText = "<search>";
			lAlpha = 0.5f;
		}

		pTextFont.begin(pCore.HUD());
		pTextFont.draw(lText, x + 5, y, -0.1f, 1f, 1f, 1f, lAlpha, 1f, -1);

		if (mShowCaret && mHasFocus) {
			pTextFont.draw("|", x + lInputTextWidth + SPACE_BETWEEN_TEXT * 3, y, 1f);

		}

		pTextFont.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void onClick(InputState pInputState) {
		mHasFocus = !mHasFocus;

		if (mHasFocus) {
			pInputState.startCapture(this);

			// Store the current string in case the user cancels the input, in which case, we
			// can restore the previous entry.
			if (mInputField.length() > 0)
				mTempString = mInputField.toString();

			if (mResetOnDefaultClick && mInputField.toString().equals(mEmptyString)) {
				if (mInputField.length() > 0) {
					mInputField.delete(0, mInputField.length());
				}
			}

		} else {

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
	public void onKeyPressed(char pCh) {
		mStringLength = mInputField.length();

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
