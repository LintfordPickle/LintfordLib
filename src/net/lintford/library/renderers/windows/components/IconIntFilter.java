package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Rectangle;

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
		mUIDstRectangle.width = pW;
		mUIDstRectangle.height = pH;
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
		if (mUIDstRectangle.intersects(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = true;

			if (pCore.input().isMouseTimedLeftClickAvailable()) {
				mUIIconFilter.onFilterClick(this);

				pCore.input().mouseTimedLeftClick();
				return true;
			}
		}

		return false;
	}

	public void draw(LintfordCore pCore, TextureBatch pUISpriteBatch, FontUnit lFont) {

		float lAR = mEnabled ? r : 1f;
		float lAG = mEnabled ? g : 1f;
		float lAB = mEnabled ? b : 1f;

		pUISpriteBatch.begin(pCore.HUD());
		// Draw the background icon
		// pUISpriteBatch.draw(mUISrcRectangle.x, mUISrcRectangle.y, mUISrcRectangle.width, mUISrcRectangle.height, mUIDstRectangle.x, mUIDstRectangle.y, 0.5f, mUIDstRectangle.width, mUIDstRectangle.height, 1f, lAR, lAG, lAB, 1f,
		// RendererManager.GAME_UI);

		// Draw the 'tab' background
		if (mEnabled) {
			// Draw a 'open' tab
			pUISpriteBatch.draw(384, 0, 64, 64, mUIDstRectangle.x - 2, mUIDstRectangle.y - 2, 0.5f, mUIDstRectangle.width + 4, mUIDstRectangle.height + 6, 1f, 1f, 1f, 1f, 1f, TextureManager.TEXTURE_CORE_UI);

		} else {
			// Draw a 'closed' tab
			pUISpriteBatch.draw(320, 0, 64, 64, mUIDstRectangle.x - 2, mUIDstRectangle.y - 2, 0.5f, mUIDstRectangle.width + 4, mUIDstRectangle.height + 6, 1f, 1f, 1f, 1f, 1f, TextureManager.TEXTURE_CORE_UI);

		}

		if (mHoveredOver) {
			float lTextHalfW = lFont.bitmap().getStringWidth(mFilterName) / 2;

			// Draw a background texture behind the texture so it is always legible.
			pUISpriteBatch.draw(128, 0, 32, 32, mUIDstRectangle.x + 16 - lTextHalfW - 2, mUIDstRectangle.y - 25, 1.4f, lTextHalfW * 2 + 4, lFont.bitmap().fontHeight(), 1f, 0.5f, 0.5f, 0.5f, 1.0f, TextureManager.TEXTURE_CORE_UI);

		}

		pUISpriteBatch.end();

		if (mHoveredOver) {
			float lTextHalfW = lFont.bitmap().getStringWidth(mFilterName) / 2;

			lFont.begin(pCore.HUD());
			lFont.draw(mFilterName, mUIDstRectangle.x + 16 - lTextHalfW, mUIDstRectangle.y - 19, 1.5f, 1f);
			lFont.end();
		}

	}

}
