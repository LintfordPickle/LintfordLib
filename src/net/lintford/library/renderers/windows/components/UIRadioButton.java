package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class UIRadioButton extends UIWidget {

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
	private boolean mIsSelected;
	private float mValue;
	private boolean mIsClicked; // click animation/delay
	private float mClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isSelected() {
		return mIsSelected;
	}

	public void isSelected(final boolean pNewValue) {
		mIsSelected = pNewValue;
	}

	public float value() {
		return mValue;
	}

	public void value(final float pNewValue) {
		mValue = pNewValue;
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

	public void buttonListenerID(final int pNewID) {
		mClickID = pNewID;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIRadioButton(final UIWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIRadioButton(final UIWindow pParentWindow, final int pClickID) {
		this(pParentWindow, NO_LABEL_TEXT, pClickID);

	}

	public UIRadioButton(final UIWindow pParentWindow, final String pLabel, final int pClickID) {
		super(pParentWindow);

		mClickID = pClickID;

		mButtonLabel = pLabel;
		w = 200;
		h = 25;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (intersects(pCore.HUD().getMouseCameraSpace()) && !mIsClicked && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
			mIsClicked = true;
			final float MINIMUM_CLICK_TIMER = 1000;
			// Callback to the listener and pass our ID
			if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
				mClickTimer = 0;
				mCallback.onClick(mClickID);
				return true;

			}

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

		float lR = mIsSelected ? 0.4f : 0.3f;
		float lG = mIsSelected ? 0.4f : 0.34f;
		float lB = mIsSelected ? 0.4f : 0.65f;

		final TextureBatch SPRITE_BATCH = mParentWindow.rendererManager().uiTextureBatch();

		// Draw the button background
		SPRITE_BATCH.begin(pCore.HUD());
		SPRITE_BATCH.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, 0f, lR, lG, lB, 1f);
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
