package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.UIWindow;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class UIRadioButton extends UIWidget implements IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8110750137089332530L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EntryInteractions mCallback;
	private int mClickID;
	private String mButtonLabel;
	private boolean mIsSelected;
	private float mValue;
	private float mMouseTimer;

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
		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				// Callback to the listener and pass our ID
				if (mCallback != null) {
					mCallback.menuEntryOnClick(pCore.input(), mClickID);

				}

				return true;

			}

		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		mMouseTimer += pCore.time().elapseGameTimeMilli();
	}

	@Override
	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth) {

		float lR = mIsSelected ? 0.4f : 0.3f;
		float lG = mIsSelected ? 0.4f : 0.34f;
		float lB = mIsSelected ? 0.4f : 0.65f;

		// Draw the button background
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, x, y, w, h, pComponentZDepth, lR, lG, lB, 1f);
		pTextureBatch.end();

		FontUnit lFontRenderer = mParentWindow.rendererManager().textFont();

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = lFontRenderer.bitmap().getStringWidth(lButtonText);

		lFontRenderer.begin(pCore.HUD());
		lFontRenderer.draw(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - lFontRenderer.bitmap().fontHeight() / 4f, pComponentZDepth + 0.1f, 1f);
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

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;

	}

}
