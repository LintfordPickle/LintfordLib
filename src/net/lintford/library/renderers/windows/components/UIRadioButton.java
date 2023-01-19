package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.mouse.IProcessMouseInput;
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

	public void isSelected(final boolean isSelected) {
		mIsSelected = isSelected;
	}

	public float value() {
		return mValue;
	}

	public void value(final float newValue) {
		mValue = newValue;
	}

	public String buttonLabel() {
		return mButtonLabel;
	}

	public void buttonLabel(final String newButtonLabel) {
		mButtonLabel = newButtonLabel;
	}

	public int buttonListenerID() {
		return mClickID;
	}

	public void buttonListenerID(final int entryUid) {
		mClickID = entryUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIRadioButton(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UIRadioButton(final UiWindow parentWindow, final int entryUid) {
		this(parentWindow, NO_LABEL_TEXT, entryUid);
	}

	public UIRadioButton(final UiWindow parentWindow, final String label, final int entryUid) {
		super(parentWindow);

		mClickID = entryUid;

		mButtonLabel = label;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mCallback != null) {
					mCallback.menuEntryOnClick(core.input(), mClickID);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		mMouseTimer -= core.appTime().elapsedTimeMilli();
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lColorMod = mIsSelected ? 0.4f : 0.3f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, componentZDepth, lColor);

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = textFont.getStringWidth(lButtonText);

		textFont.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - textFont.fontHeight() / 4f, componentZDepth + 0.01f, ColorConstants.WHITE, 1f);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setClickListener(final EntryInteractions callbackObject) {
		mCallback = callbackObject;
	}

	public void removeClickListener(final EntryInteractions callbackObject) {
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
