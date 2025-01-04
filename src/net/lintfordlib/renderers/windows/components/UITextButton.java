package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

public class UITextButton extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -532019333230394347L;

	private static final String NO_LABEL_TEXT = "unlabled";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EntryInteractions mCallback;
	private int mEntryUid;
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

	public void buttonLabel(final String newLabel) {
		mButtonLabel = newLabel;
	}

	public int buttonListenerUid() {
		return mEntryUid;
	}

	public void buttonListenerUid(final int entryUid) {
		mEntryUid = entryUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UITextButton(final UiWindow parentWindow) {
		this(parentWindow, 0);
	}

	public UITextButton(final UiWindow parentWindow, final int entryUid) {
		super(parentWindow);

		mEntryUid = entryUid;

		mButtonLabel = NO_LABEL_TEXT;
		mW = 200;
		mH = 25;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (!mIsClicked && intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				mIsClicked = true;
				final float MINIMUM_CLICK_TIMER = 200;
				// Callback to the listener and pass our ID
				if (mCallback != null && mClickTimer > MINIMUM_CLICK_TIMER) {
					mClickTimer = 0;
					mCallback.menuEntryOnClick(core.input(), mEntryUid);
					return true;
				}
			}

		} else {
			mHoveredOver = false;
		}

		if (!core.input().mouse().isMouseLeftButtonDown()) {
			mIsClicked = false;
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		mClickTimer += core.appTime().elapsedTimeMilli();
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lColorMod = mHoveredOver ? .3f : 1.f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lColor);
		spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, 32, 32, componentZDepth);
		spriteBatch.end();

		final var lFontRenderer = mParentWindow.rendererManager().uiTextFont();
		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = lFontRenderer.getStringWidth(lButtonText);

		lFontRenderer.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		lFontRenderer.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - lFontRenderer.fontHeight() / 4f, componentZDepth, 1f);
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

}
