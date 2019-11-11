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
	private transient float mR, mG, mB;
	private transient boolean mHoveredOver;
	private transient boolean mDrawButtonBackground;
	private transient boolean mDrawButtonText;
	private transient float mClickTimer;

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

	public void drawButtonBackground(boolean pNewValue) {
		mDrawButtonBackground = pNewValue;
	}

	public boolean drawButtonBackground() {
		return mDrawButtonBackground;
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

		mR = mG = mB = 1f;
		mSourceRectangle = new Rectangle();

		mDrawButtonBackground = true;
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

		mClickTimer += pCore.time().elapseGameTimeMilli();

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		if (!mIsVisible)
			return;

		float lR = mHoveredOver ? 0.3f : mR;
		float lG = mHoveredOver ? 0.34f : mG;
		float lB = mHoveredOver ? 0.65f : mB;

		final TextureBatch lTextureBatch = mParentWindow.rendererManager().uiTextureBatch();

		// Draw the button background
		lTextureBatch.begin(pCore.HUD());

		if (mDrawButtonBackground)
			lTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, 0f, lR, lG, lB, 1f);

		if (mButtonTexture != null)
			lTextureBatch.draw(mButtonTexture, mSourceRectangle.x, mSourceRectangle.y, mSourceRectangle.w, mSourceRectangle.h, x, y, w, h, 0f, lR, lG, lB, 1f);

		lTextureBatch.end();

		// text
		if (mDrawButtonText && mButtonLabel != null && mButtonLabel.length() > 0) {
			FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

			final float lTextWidth = lFontRenderer.bitmap().getStringWidth(mButtonLabel);

			lFontRenderer.begin(pCore.HUD());
			lFontRenderer.draw(mButtonLabel, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 2f, pComponentZDepth, 1f);
			lFontRenderer.end();

		}

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

	public void setTextureSource(final Texture pTexture, final float pSrcX, final float pSrcY, final float pSrcW, final float pSrcH) {
		mButtonTexture = pTexture;

		mSourceRectangle.x = pSrcX;
		mSourceRectangle.y = pSrcY;
		mSourceRectangle.w = pSrcW;
		mSourceRectangle.h = pSrcH;

	}

}
