package net.ld.library.renderers.windows.components;

import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.graphics.texturebatch.TextureBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.IBufferedInputCallback;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.renderers.RendererManager;

public class UIInputText extends Rectangle implements IBufferedInputCallback {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float SPACE_BETWEEN_TEXT = 1;
	private static final float CARET_FLASH_TIME = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private RendererManager mRendererManager;

	private boolean mHasFocus;
	private float mCaretFlashTimer;
	private boolean mShowCaret;
	private String mTempString;
	private String mEmptyString;

	private Rectangle mCancelRectangle;

	// A little wierd, we store the string length to check if the string has changed since the last frame (since
	// working with the length (int) doesn't cause a heap allocation as toString() does )
	private int mStringLength;
	private StringBuilder mInputField;
	private boolean mResetOnDefaultClick;

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

	public UIInputText(final RendererManager pRendererManager) {
		mRendererManager = pRendererManager;

		mResetOnDefaultClick = true;
		mInputField = new StringBuilder();

		mCancelRectangle = new Rectangle();
		mEmptyString = "";

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(InputState pInputState) {
		if (pInputState.isMouseTimedLeftClickAvailable()) {
			if (mCancelRectangle.intersects(pInputState.HUD().getMouseCameraSpace())) {
				if (mInputField.length() > 0) {
					pInputState.mouseTimedLeftClick();
					if (mInputField.length() > 0)
						mInputField.delete(0, mInputField.length());
					mStringLength = 0;

					mInputField.append(mEmptyString);

					pInputState.stopCapture();

					mHasFocus = false;
					mShowCaret = false;

				}
			}
		}

		if (pInputState.isMouseTimedLeftClickAvailable()) {
			if (intersects(pInputState.HUD().getMouseCameraSpace())) {
				pInputState.mouseTimedLeftClick();

				onClick(pInputState);

				return true;

			} else if (mHasFocus) {
				System.out.println("UIInputText lost focus");
				mHasFocus = false;

				return false;
			}

		}

		return false;
	}

	public void update(GameTime pGameTime) {
		mCaretFlashTimer += pGameTime.elapseGameTimeMilli();

		final int lCANCEL_RECT_SIZE = 24;
		mCancelRectangle.x = x + width - lCANCEL_RECT_SIZE - 2;
		mCancelRectangle.y = y + 2;
		mCancelRectangle.width = lCANCEL_RECT_SIZE;
		mCancelRectangle.height = lCANCEL_RECT_SIZE;

		if (mHasFocus) {
			// flash and update the location of the caret
			if (mCaretFlashTimer > CARET_FLASH_TIME) {
				mShowCaret = !mShowCaret;
				mCaretFlashTimer = 0;
			}
		}

	}

	public void draw(RenderState pRenderState, TextureBatch pUISpriteBatch) {

		pUISpriteBatch.begin(pRenderState.hudCamera());
		pUISpriteBatch.draw(64, 96, 32, 32, x, y, 0.5f, width, height, 1f, 1f, 1f, 1f, 1, TextureManager.CORE_TEXTURE);
		pUISpriteBatch.end();

		// Draw the cancel button rectangle
		pUISpriteBatch.begin(pRenderState.hudCamera());
		pUISpriteBatch.draw(256, 16, 16, 16, mCancelRectangle.x, mCancelRectangle.y, 0.5f, mCancelRectangle.width, mCancelRectangle.height, 1f, 1f, 1f, 1f, 1, TextureManager.CORE_TEXTURE);
		pUISpriteBatch.end();

		FontUnit lFont = mRendererManager.textFont();

		final float lInputTextWidth = lFont.bitmap().getStringWidth(mInputField.toString());

		lFont.begin(pRenderState.hudCamera());
		lFont.draw(mInputField.toString(), x + 5, y + height - lFont.bitmap().fontHeight() * 0.5f - 3, 1f, 1f);

		if (mShowCaret && mHasFocus) {
			lFont.draw("|", x + lInputTextWidth + SPACE_BETWEEN_TEXT, y + height - lFont.bitmap().fontHeight() * 0.5f - 3, 1f, 1f);
		}

		lFont.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void onClick(InputState pInputState) {
		mHasFocus = !mHasFocus;

		if (mHasFocus) {

			System.out.println("UIInputText has focus");

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
	public void onEnterPressed() {
		mHasFocus = false;
		mShowCaret = false;

	}

	@Override
	public void onKeyPressed(char pCh) {
		// By changing the mStringLength, we provide a way for listeners to poll if there have been any changes
		// TODO: Use a proper listener interface
		mStringLength = mInputField.length();

	}

	@Override
	public boolean getEnterFinishesInput() {
		return true;
	}

	@Override
	public void onEscapePressed() {
		if (mInputField.length() > 0) {
			mInputField.delete(0, mInputField.length());
		}
		if (mTempString != null && mTempString.length() == 0) {
			mInputField.append(mTempString);
		}

		mStringLength = 0;

		mHasFocus = false;
		mShowCaret = false;
	}

	@Override
	public boolean getEscapeFinishesInput() {
		return true;
	}

}
