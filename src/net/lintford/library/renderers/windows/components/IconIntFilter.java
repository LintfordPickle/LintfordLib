package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class IconIntFilter {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UIIconFilter mUIIconFilter;
	private int mFilterValue;
	private Rectangle mUISrcRectangle;
	private Rectangle mUIDstRectangle;
	private boolean mEnabled;
	private String mFilterName;
	private boolean mHoveredOver;
	private float r, g, b;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setSelectedColor(float pR, float pG, float pB) {
		r = pR;
		g = pG;
		b = pB;
	}

	public void resetHovered() {
		mHoveredOver = false;
	}

	public String filterName() {
		return mFilterName;
	}

	public Rectangle uiSrcRectangle() {
		return mUISrcRectangle;
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
		mUIDstRectangle.x = pX;
		mUIDstRectangle.y = pY;
		mUIDstRectangle.w = pW;
		mUIDstRectangle.h = pH;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public IconIntFilter(UIIconFilter pParent, Rectangle pSrcRect, String pName, int pFilterValue) {
		mEnabled = false;

		mUIIconFilter = pParent;
		mUISrcRectangle = pSrcRect;
		mUIDstRectangle = new Rectangle();
		mFilterName = pName;
		mFilterValue = pFilterValue;

		// Set selected color to white
		r = g = b = 1f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore pCore) {
		if (mUIDstRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				mUIIconFilter.onFilterClick(this);

				pCore.input().setLeftMouseClickHandled();

				return true;

			}

		}

		return false;
	}

	public void draw(LintfordCore pCore, TextureBatch pTextureBatch, FontUnit pTextFont, float pComponentZDepth) {

		float lR = mEnabled ? r : 1f;
		float lG = mEnabled ? g : 1f;
		float lB = mEnabled ? b : 1f;

		pTextureBatch.begin(pCore.HUD());

		// Draw the 'tab' background
		if (mEnabled) {
			// Draw a 'open' tab
			pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 384, 0, 64, 64, mUIDstRectangle.x - 2, mUIDstRectangle.y - 2, mUIDstRectangle.w + 4, mUIDstRectangle.h + 6, -0.5f, lR, lG, lB, 1f);

		} else {
			// Draw a 'closed' tab
			pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 320, 0, 64, 64, mUIDstRectangle.x - 2, mUIDstRectangle.y - 2, mUIDstRectangle.w + 4, mUIDstRectangle.h + 6, -0.5f, lR, lG, lB, 1f);

		}

		if (mHoveredOver) {
			final float lTextHalfW = pTextFont.bitmap().getStringWidth(mFilterName) / 2;
			final float lTextHeight = pTextFont.bitmap().fontHeight();

			// Draw a background texture behind the texture so it is always legible.
			pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 64, 0, 32, 32, mUIDstRectangle.x + 16 - lTextHalfW, mUIDstRectangle.y - 19, lTextHalfW * 2 + 4, lTextHeight, -0.2f, 1f, 1f, 1f, 1.0f);

		}

		// Draw the background icon
		pTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, mUISrcRectangle.x, mUISrcRectangle.y, mUISrcRectangle.w, mUISrcRectangle.h, mUIDstRectangle.x, mUIDstRectangle.y, mUIDstRectangle.w, mUIDstRectangle.h, -0.5f, lR, lG, lB, 1f);

		pTextureBatch.end();

		if (mHoveredOver) {
			final float lTextHalfW = pTextFont.bitmap().getStringWidth(mFilterName) / 2;

			pTextFont.begin(pCore.HUD());
			pTextFont.draw(mFilterName, mUIDstRectangle.x + 16 - lTextHalfW, mUIDstRectangle.y - 19, -0.2f, 1f);
			pTextFont.end();
		}

	}

}
