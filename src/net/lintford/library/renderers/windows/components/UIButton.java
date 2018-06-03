package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
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
		if (!mIsClicked && intersects(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().tryAquireLeftClickOwnership(hashCode())) {
				mIsClicked = true;
				final float MINIMUM_CLICK_TIMER = 200;
				// Callback to the listener and pass our ID
				if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mClickTimer = 0;
					mCallback.menuEntryOnClick(mClickID);
					return true;
				}

			}

		} else {
			mHoveredOver = false;
		}

		if (!pCore.input().mouseLeftClick()) {
			mIsClicked = false;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mClickTimer += pCore.time().elapseGameTimeMilli();

	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, FontUnit pTextFont, float pComponentZDepth) {

		float lR = mHoveredOver ? 1f : 0.9f;
		float lG = mHoveredOver ? 1f : 0.9f;
		float lB = mHoveredOver ? 1f : 0.9f;

		// Draw the button background
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 32, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 32, 32, 224, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 256, 32, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pCore.HUD());
		lFontRenderer.draw(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 2f, pComponentZDepth, 1f);
		lFontRenderer.end();

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
