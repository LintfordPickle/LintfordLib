package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.maths.Rectangle;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;

public class UIIconFilter extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------
	
	private static final long serialVersionUID = -5287653520977797364L;

	private static final float HORIZONTAL_PADDING = 10;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public static final int NO_FILTER_VALUE = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient List<IconIntFilter> mIconFilters;
	private transient int mCurrentFilter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int currentFilterValue() {
		return mCurrentFilter;
	}

	/** Returns the IconIntFilter with the specified filter value if found. Null returned otherwise */
	public IconIntFilter getFilterByValue(int pIndex) {
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			if (mIconFilters.get(i).filterValue() == pIndex)
				return mIconFilters.get(i);
		}

		return null;
	}

	/** Returns the IconIntFilter with the specified name if found. Null returned otherwise */
	public IconIntFilter getFilterByName(String pName) {
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			if (mIconFilters.get(i).filterName().equals(pName))
				return mIconFilters.get(i);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIIconFilter() {
		mIconFilters = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void loadGLContent(ResourceManager pResourceManager) {

	}

	public boolean handleInput(InputState pInputState, ICamera pHUDCamera) {
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			mIconFilters.get(i).resetHovered();
		}

		for (int i = 0; i < lCount; i++) {
			if (mIconFilters.get(i).handleInput(pInputState, pHUDCamera)) {
				return true;
			}
		}

		return false;
	}

	public void update(GameTime pGameTime) {

		float lPosX = x;
		float lPosY = y;

		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			IconIntFilter lFilter = mIconFilters.get(i);

			lFilter.setDstRectangle(lPosX, lPosY, 32, 32);

			lPosX += lFilter.uiDstRectangle().width + HORIZONTAL_PADDING;

		}
	}

	public void draw(RenderState pRenderState, TextureBatch pSpriteBatch, FontUnit lFont) {
		pSpriteBatch.begin(pRenderState.HUDCamera());

		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			IconIntFilter lFilter = mIconFilters.get(i);

			lFilter.draw(pRenderState, pSpriteBatch, lFont);
		}

		pSpriteBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Adds a new FilterIcon to this FilterIconSet.
	 * 
	 * @param pSrcRect
	 *            The source retangle in the UI_TEXTURE_RESOURCE to use when rendering
	 * @param pName
	 *            The name of the filter (e.g. 'All', 'Building Materials' etc.)
	 * @param pFilterValue
	 *            The item-value to use when filtering in items. */
	public void addFilterIcon(IconIntFilter pNewFilterIcon) {
		mIconFilters.add(pNewFilterIcon);

	}

	public void onFilterClick(IconIntFilter pObj) {
		boolean lWasEnabled = pObj.filterEnabled();

		// disable all other filter icons
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			mIconFilters.get(i).filterEnabled(false);
		}

		// We we just have one that is enabled
		if (lWasEnabled) {
			pObj.filterEnabled(false);
			mCurrentFilter = NO_FILTER_VALUE;
		} else {
			pObj.filterEnabled(true);
			mCurrentFilter = pObj.filterValue();
		}

	}

}
