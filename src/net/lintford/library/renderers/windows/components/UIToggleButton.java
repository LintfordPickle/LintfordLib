package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIToggleButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8326753044931375276L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	EntryInteractions mCallback;
	private int mClickID;
	private String mButtonLabel;
	private float mR, mG, mB;
	private boolean mHoveredOver;

	private Texture mButtonTexture;
	private Rectangle mSourceRectangle;

	private boolean mIsEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public void isEnabled(final boolean pNewValue) {
		mIsEnabled = pNewValue;
	}

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIToggleButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIToggleButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

		mR = 0.3f;
		mG = 0.34f;
		mB = 0.65f;

		mSourceRectangle = new Rectangle();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().tryAquireLeftClickOwnership(hashCode())) {

				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.menuEntryOnClick(mClickID);
				}

				return true;

			}

		} else {
			mHoveredOver = false;

		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		float lR = mIsEnabled ? 0.3f : mHoveredOver ? 0.3f : mR;
		float lG = mIsEnabled ? 0.13f : mHoveredOver ? 0.34f : mG;
		float lB = mIsEnabled ? 0.19f : mHoveredOver ? 0.65f : mB;

		// Draw the button background
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, pComponentZDepth, lR, lG, lB, 1f);

		if (mButtonTexture != null) {
			pTextureBatch.draw(mButtonTexture, mSourceRectangle.x, mSourceRectangle.y, mSourceRectangle.w, mSourceRectangle.h, x, y, w, h, pComponentZDepth, lR, lG, lB, 1f);
		}

		pTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions pCallbackObject) {
		mCallback = pCallbackObject;
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
