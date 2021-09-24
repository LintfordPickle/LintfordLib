package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.windows.UiWindow;
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

	public UIButton(final UiWindow pParentWindow) {
		this(pParentWindow, 0);
	}

	public UIButton(final UiWindow pParentWindow, final int pClickID) {
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
	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lColorMod = mHoveredOver ? .9f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(entityColor, lColorMod);

		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_RED, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pComponentZDepth, lColor);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_RED, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pComponentZDepth, lColor);
		pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_RED, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, pComponentZDepth, lColor);

		final String lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final float lTextWidth = pTextFont.getStringWidth(lButtonText);

		pTextFont.drawText(lButtonText, x + w / 2f - lTextWidth / 2f, y + h / 2f - pTextFont.fontHeight() / 2f, pComponentZDepth, ColorConstants.WHITE, 1f);
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
