package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

public class UiTextButton extends UIWidget {

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

	public void buttonListenerUid(int entryUid) {
		mEntryUid = entryUid;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiTextButton() {
		this(NO_LABEL_TEXT);
	}

	public UiTextButton(String label) {
		mButtonLabel = label;
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
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		final var lColorMod = mHoveredOver ? 1.f : .6f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);

		final var lFontRenderer = sharedResources.uiTextFont();
		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColor(lColor);
		lSpriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, 32, componentZDepth);
		lSpriteBatch.end();

		final var lButtonText = mButtonLabel != null ? mButtonLabel : NO_LABEL_TEXT;
		final var lTextWidth = lFontRenderer.getStringWidth(lButtonText);

		lFontRenderer.begin(core.HUD());
		lFontRenderer.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		lFontRenderer.drawText(lButtonText, mX + mW / 2f - lTextWidth / 2f, mY + mH / 2f - lFontRenderer.fontHeight() / 4f, componentZDepth - 0.1f, 1f);
		lFontRenderer.end();
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
