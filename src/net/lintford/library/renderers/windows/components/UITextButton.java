package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class UITextButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	IMenuEntryClickListener mCallback;
	private int mClickID;
	private String mButtonLabel;
	private float mR, mG, mB;
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

	public UITextButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UITextButton(final UIWindow pParentWindow, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = NO_LABEL_TEXT;
		w = 200;
		h = 25;

		mR = mG = mB = 1f;

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
					mCallback.onClick(mClickID);
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
	public void draw(LintfordCore pCore) {

		mR = 0.19f;
		mG = 0.13f;
		mB = 0.3f;

		float lR = mHoveredOver ? 0.3f : mR;
		float lG = mHoveredOver ? 0.34f : mG;
		float lB = mHoveredOver ? 0.65f : mB;
		
		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiTextureBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pCore.HUD());
		SPRITE_BATCH.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, 32, 32, -0.1f, lR, lG, lB, 1f);
		SPRITE_BATCH.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pCore.HUD());
		lFontRenderer.draw(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 4f, 1f);
		lFontRenderer.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = pCallbackObject;
	}

	public void removeClickListener(final IMenuEntryClickListener pCallbackObject) {
		mCallback = null;
	}

}
