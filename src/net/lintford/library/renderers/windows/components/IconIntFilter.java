package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IProcessMouseInput;

public class IconIntFilter implements IProcessMouseInput {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UIIconFilter mUIIconFilter;
	private int mFilterValue;
	private transient SpriteSheetDefinition mIconSpritesheetDefinition;
	private transient int mIconSpriteFrameIndex;
	private Rectangle mUIDstRectangle;
	private boolean mEnabled;
	private String mFilterName;
	private boolean mHoveredOver;
	private float mMouseTimer;
	public final Color color = new Color();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void resetHovered() {
		mHoveredOver = false;
	}

	public String filterName() {
		return mFilterName;
	}

	public Rectangle uiDstRectangle() {
		return mUIDstRectangle;
	}

	public int filterValue() {
		return mFilterValue;
	}

	public void filterEnabled(boolean pNewValue) {
		mEnabled = pNewValue;
	}

	public boolean filterEnabled() {
		return mEnabled;
	}

	public void isFilterEnabled(boolean pNewValue) {
		mEnabled = pNewValue;
	}

	public void setDstRectangle(float pX, float pY, float pW, float pH) {
		mUIDstRectangle.set(pX, pY, pW, pH);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IconIntFilter(UIIconFilter pParent, SpriteSheetDefinition pSpritesheet, int pSpriteFrameIndex, String pName, int pFilterValue) {
		mEnabled = false;

		mUIIconFilter = pParent;
		mIconSpritesheetDefinition = pSpritesheet;
		mIconSpriteFrameIndex = pSpriteFrameIndex;
		mUIDstRectangle = new Rectangle();
		mFilterName = pName;
		mFilterValue = pFilterValue;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		if (mUIDstRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			mHoveredOver = true;
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				mUIIconFilter.onFilterClick(this);
				return true;
			}
		}

		return false;
	}

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		final float lColorMod = mEnabled ? 1.f : .8f;
		final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.WHITE, lColorMod);

		// Draw the 'tab' background (open/closed)
		if (mEnabled) {
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_BLUE, mUIDstRectangle.x() - 2, mUIDstRectangle.y() - 2, mUIDstRectangle.w() + 4, mUIDstRectangle.h() + 6, -0.5f, lColor);
		} else {
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_GREEN, mUIDstRectangle.x() - 2, mUIDstRectangle.y() - 2, mUIDstRectangle.w() + 4, mUIDstRectangle.h() + 6, -0.5f, lColor);
		}

		if (mHoveredOver) {
			final float lTextHalfW = pTextFont.getStringWidth(mFilterName) / 2;
			final float lTextHeight = pTextFont.fontHeight();

			// Draw a background texture behind the texture so it is always legible.
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_RED, mUIDstRectangle.x() + 16 - lTextHalfW, mUIDstRectangle.y() - 19, lTextHalfW * 2 + 4, lTextHeight, pComponentZDepth, ColorConstants.WHITE);
		}

		// Draw the background icon
		pSpriteBatch.draw(mIconSpritesheetDefinition, mIconSpriteFrameIndex, mUIDstRectangle, pComponentZDepth, lColor);

		if (mHoveredOver) {
			final float lTextHalfW = pTextFont.getStringWidth(mFilterName) / 2;
			pTextFont.drawText(mFilterName, mUIDstRectangle.x() + 16 - lTextHalfW, mUIDstRectangle.y() - 19, pComponentZDepth, ColorConstants.WHITE, 1f);
		}
	}

	// --------------------------------------
	// Inherited methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;

	}

}
