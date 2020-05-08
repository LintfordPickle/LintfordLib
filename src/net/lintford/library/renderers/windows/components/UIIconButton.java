package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIIconButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5431518038383191422L;

	private static final int CLICK_TIMER = 250;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient EntryInteractions mCallback;
	private transient int mClickID;
	private transient String mButtonLabel;
	private transient boolean mHoveredOver;
	private transient boolean mDrawButtonText;
	private transient float mClickTimer;
	private transient boolean mButtonSolidColorBackground;
	private float mRed, mGreen, mBlue;

	private transient Texture mButtonTexture;
	private transient Rectangle mSourceRectangle;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String pNewLabel) {
		mButtonLabel = pNewLabel;
	}

	public int buttonListenerID() {
		return mClickID;
	}

	public void buttonListenerID(final int pNewLabel) {
		mClickID = pNewLabel;
	}

	public void setButtonSolidBackgroundColor(boolean pNewValue) {
		mButtonSolidColorBackground = pNewValue;
	}

	public void setButtonSolidBackgroundColor(float pRed, float pGreen, float pBlue) {
		mRed = pRed;
		mGreen = pGreen;
		mBlue = pBlue;
	}

	public boolean setButtonSolidBackgroundColor() {
		return mButtonSolidColorBackground;
	}

	public void drawButtonText(boolean pNewValue) {
		mDrawButtonText = pNewValue;
	}

	public boolean drawButtonText() {
		return mDrawButtonText;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIIconButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIIconButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		w = 200;
		h = 25;

		mRed = mGreen = mBlue = 1f;
		mSourceRectangle = new Rectangle();

		mButtonSolidColorBackground = true;
		mDrawButtonText = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mIsVisible)
			return false;

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (mClickTimer > CLICK_TIMER) {
				if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
					// Callback to the listener and pass our ID
					if (mCallback != null) {
						mCallback.menuEntryOnClick(pCore.input(), mClickID);

					}

					mClickTimer = 0;

					return true;

				}
			}

		} else {
			mHoveredOver = false;

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		if (!mIsVisible)
			return;

		super.update(pCore);

		mClickTimer += pCore.appTime().elapsedTimeMilli();

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		if (!mIsVisible)
			return;

		if (mButtonSolidColorBackground) {
			drawSolidColorBackground(pCore, pUITexture);

		} else if (mButtonTexture != null) {
			drawTextureBackground(pCore, mButtonTexture);

		}

		// text
		if (mDrawButtonText && mButtonLabel != null && mButtonLabel.length() > 0) {
			FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

			final float lTextWidth = lFontRenderer.bitmap().getStringWidth(mButtonLabel);

			lFontRenderer.begin(pCore.HUD());
			lFontRenderer.draw(mButtonLabel, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 2f, pComponentZDepth, 1f);
			lFontRenderer.end();

		}

	}

	private void drawSolidColorBackground(LintfordCore pCore, Texture pTexture) {
		float lR = mHoveredOver ? 0.3f : mRed;
		float lG = mHoveredOver ? 0.34f : mGreen;
		float lB = mHoveredOver ? 0.65f : mBlue;

		final TextureBatch lTextureBatch = mParentWindow.rendererManager().uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(pTexture, 0, 0, 32, 32, x, y, w, h, 0f, lR, lG, lB, 1f);
		lTextureBatch.end();

	}

	private void drawTextureBackground(LintfordCore pCore, Texture pTexture) {
		float lR = mHoveredOver ? 0.3f : mRed;
		float lG = mHoveredOver ? 0.34f : mGreen;
		float lB = mHoveredOver ? 0.65f : mBlue;

		final TextureBatch lTextureBatch = mParentWindow.rendererManager().uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(pTexture, mSourceRectangle, x, y, w, h, 0f, lR, lG, lB, 1f);
		lTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions pCallbackObject, int pClickID) {
		mCallback = pCallbackObject;
		mClickID = pClickID;

	}

	public void removeClickListener(final EntryInteractions pCallbackObject) {
		mCallback = null;
	}

	public void unsetTextureSource() {
		mButtonTexture = null;

	}

	public void setTextureSource(final Texture pTexture, final float pSrcX, final float pSrcY, final float pSrcW, final float pSrcH) {
		mButtonSolidColorBackground = false;

		mButtonTexture = pTexture;
		mSourceRectangle.set(pSrcX, pSrcY, pSrcW, pSrcH);

	}

}
