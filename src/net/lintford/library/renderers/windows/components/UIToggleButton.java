package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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
	private boolean mIsToggledOn;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isToggledOn() {
		return mIsToggledOn;
	}

	public void isToggledOn(final boolean pIsToggledOn) {
		mIsToggledOn = pIsToggledOn;
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

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;

			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.menuEntryOnClick(pCore.input(), mClickID);
				}

				return true;

			}

		} else {
			mHoveredOver = false;

		}

		return false;
	}

	@Override
	public void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {
		float lR = mIsToggledOn ? mR : mR * .3f;
		float lG = mIsToggledOn ? mG : mG * .3f;
		float lB = mIsToggledOn ? mB : mB * .3f;

		if (mHoveredOver) {
			lR -= 0.1f;
			lG -= 0.1f;
			lB -= 0.1f;
		}

		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(pUITexture, 0, 32, 32, 32, x, y, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.draw(pUITexture, 32, 32, 32, 32, x + 32, y, w - 64, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.draw(pUITexture, 128, 32, 32, 32, x + w - 32, y, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.end();

		if (mButtonLabel != null && mButtonLabel.length() > 0) {
			final float lTextWidth = pTextFont.bitmap().getStringWidth(mButtonLabel);
			final float lTextHeight = pTextFont.bitmap().getStringHeight(mButtonLabel);

			pTextFont.begin(pCore.HUD());
			pTextFont.draw(mButtonLabel, x + w / 2.f - lTextWidth / 2.f, y + h / 2f - lTextHeight / 2.f, 1.0f);
			pTextFont.end();

		}

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

}
