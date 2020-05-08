package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 7704116387039308007L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EntryInteractions mCallback;
	private int mClickID;
	private String mButtonLabel;
	private boolean mHoveredOver;
	private boolean mIsClicked;
	private float mClickTimer;

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (!mIsClicked && intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				mIsClicked = true;
				final var MINIMUM_CLICK_TIMER = 200;
				
				// Callback to the listener and pass our ID
				if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mClickTimer = 0;
					mCallback.menuEntryOnClick(pCore.input(), mClickID);
					
					return true;
					
				}

			}

		} else {
			mHoveredOver = false;
		}

		if (!pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			mIsClicked = false;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mClickTimer += pCore.appTime().elapsedTimeMilli();

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {

		float lR = mHoveredOver ? 0.9f : 0.9f;
		float lG = mHoveredOver ? 0.5f : 0.9f;
		float lB = mHoveredOver ? 0.6f : 0.9f;

		// Draw the button background
		pTextureBatch.begin(pCore.HUD());
		// pTextureBatch.draw(pUITexture, 0, 32, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.draw(pUITexture, 32, 32, 224, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pComponentZDepth, lR, lG, lB, 1f);
		// pTextureBatch.draw(pUITexture, 256, 32, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pCore.HUD());
		lFontRenderer.draw(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 2f, pComponentZDepth, 1f);
		lFontRenderer.end();

		Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this, lR, lG, lB);

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
