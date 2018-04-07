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

	private String mTextureName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void textureName(String pTextureName) {
		mTextureName = pTextureName;
	}

	public String textureName() {
		return mTextureName;
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

					// mInputField.append(mEmptyString);

					pCore.input().stopCapture();

					mHasFocus = false;
					mShowCaret = false;

				}

				pCore.input().setLeftMouseClickHandled();
			}
		}

		if (pCore.input().isMouseTimedLeftClickAvailable()) {
			if (intersects(pCore.HUD().getMouseCameraSpace())) {
				pCore.input().mouseTimedLeftClick();

				onClick(pCore.input());

				pCore.input().setLeftMouseClickHandled();

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

		final int lCANCEL_RECT_SIZE = 18;
		mCancelRectangle.x = x + w - lCANCEL_RECT_SIZE - 10;
		mCancelRectangle.y = y + h / 2 - lCANCEL_RECT_SIZE / 2;
		mCancelRectangle.w = lCANCEL_RECT_SIZE;
		mCancelRectangle.h = lCANCEL_RECT_SIZE;

		if (mHasFocus) {
			// flash and update the location of the caret
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}
			
			// Limit the number of characters which can be entered
			if(mInputField.length() > 15)
				mInputField.delete(15, mInputField.length() - 1);
			
		}
		
		

	}

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, FontUnit pTextFont) {
		if(mTextureName == null) mTextureName = TextureManager.TEXTURE_CORE_UI_NAME;
		
		// Renders the background of the input text widget
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(TextureManager.textureManager().getTexture(mTextureName), 448     , 32, 32, 32, x,          y, 32,     32, -0.1f, 1f, 1f, 1f, 1);
		pTextureBatch.draw(TextureManager.textureManager().getTexture(mTextureName), 448 + 32, 32, 32, 32, x + 32,     y, w - 64, 32, -0.1f, 1f, 1f, 1f, 1);
		pTextureBatch.draw(TextureManager.textureManager().getTexture(mTextureName), 448 + 64, 32, 32, 32, x + w - 32, y, 32, 32, -0.1f, 1f, 1f, 1f, 1);
		pTextureBatch.end();

		// Draw the cancel button rectangle
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(TextureManager.textureManager().getTexture(mTextureName), 288, 64, 32, 32, mCancelRectangle.x, mCancelRectangle.y, mCancelRectangle.w, mCancelRectangle.h, -0.1f, 1f, 1f, 1f, 1);
		pTextureBatch.end();

		final float lInputTextWidth = pTextFont.bitmap().getStringWidth(mInputField.toString());

		String lText = mInputField.toString();
		final float lTextHeight = pTextFont.bitmap().fontHeight();
		float lAlpha = 1f;
		if (lText.length() == 0 && !mHasFocus) {
			if (mEmptyString.isEmpty() ) {
				lText = "<search>";

			} else {
				lText = mEmptyString;

			}

			lAlpha = 0.5f;
		}

		pTextFont.begin(pCore.HUD());
		pTextFont.draw(lText, x + 10, y + h / 2 - lTextHeight / 2, -0.1f, 1f, 1f, 1f, lAlpha, 1f, -1);

		if (mShowCaret && mHasFocus) {
			pTextFont.draw("|", x + 10 + lInputTextWidth + SPACE_BETWEEN_TEXT * 3, y + h / 2 - lTextHeight / 2, 1f);

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
