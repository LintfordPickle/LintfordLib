package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.UiWindow;
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

	public UIRadioButton(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIRadioButton(final UiWindow pParentWindow, final int pClickID) {
		this(pParentWindow, NO_LABEL_TEXT, pClickID);

	}

	public UIRadioButton(final UiWindow pParentWindow, final String pLabel, final int pClickID) {
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

		mMouseTimer -= pCore.appTime().elapsedTimeMilli();
	}

	@Override
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lColorMod = mIsSelected ? 0.4f : 0.3f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

		// Draw the button background
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, pComponentZDepth, lColor);

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = pTextFont.getStringWidth(lButtonText);

		pTextFont.drawText(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - pTextFont.fontHeight() / 4f, pComponentZDepth + 0.01f, ColorConstants.WHITE, 1f);
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
