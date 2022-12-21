package net.lintford.library.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.batching.SpriteBatch;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;

public class UiIconFilter extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 200319889967127089L;

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
	public IconIntFilter getFilterByValue(int iconFilterIndex) {
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			if (mIconFilters.get(i).filterValue() == iconFilterIndex)
				return mIconFilters.get(i);
		}

		return null;
	}

	/** Returns the IconIntFilter with the specified name if found. Null returned otherwise */
	public IconIntFilter getFilterByName(String iconFilterName) {
		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			if (mIconFilters.get(i).filterName().equals(iconFilterName))
				return mIconFilters.get(i);
		}

		return null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiIconFilter() {
		mIconFilters = new ArrayList<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadResources(ResourceManager resourceManager) {

	}

	public boolean handleInput(LintfordCore core) {
		final int lIconFilterCount = mIconFilters.size();
		for (int i = 0; i < lIconFilterCount; i++) {
			mIconFilters.get(i).resetHovered();
		}

		for (int i = 0; i < lIconFilterCount; i++) {
			if (mIconFilters.get(i).handleInput(core)) {
				return true;
			}
		}

		return false;
	}

	public void update(LintfordCore core) {
		float lPosX = mX;
		float lPosY = mY;

		int lCount = mIconFilters.size();
		for (int i = 0; i < lCount; i++) {
			IconIntFilter lFilter = mIconFilters.get(i);

			lFilter.setDstRectangle(lPosX, lPosY, 32, 32);

			lPosX += lFilter.uiDstRectangle().width() + HORIZONTAL_PADDING;
		}
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition hudSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final int lItemFilterCount = mIconFilters.size();
		for (int i = 0; i < lItemFilterCount; i++) {
			final var lIconIntFilter = mIconFilters.get(i);

			lIconIntFilter.draw(core, spriteBatch, hudSpritesheetDefinition, textFont, componentZDepth);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/**
	 * Adds a new FilterIcon to this FilterIconSet.
	 * 
	 * @param pSrcRect     The source retangle in the UI_TEXTURE_RESOURCE to use when rendering
	 * @param pName        The name of the filter (e.g. 'All', 'Building Materials' etc.)
	 * @param pFilterValue The item-value to use when filtering in items.
	 */
	public void addFilterIcon(IconIntFilter newFilterIcon) {
		mIconFilters.add(newFilterIcon);
	}

	public void onFilterClick(IconIntFilter iconFilterClicked) {
		boolean lWasEnabled = iconFilterClicked.filterEnabled();

		final int lIconFilterCount = mIconFilters.size();
		for (int i = 0; i < lIconFilterCount; i++) {
			mIconFilters.get(i).filterEnabled(false);
		}

		if (lWasEnabled) {
			iconFilterClicked.filterEnabled(false);
			mCurrentFilter = NO_FILTER_VALUE;
		} else {
			iconFilterClicked.filterEnabled(true);
			mCurrentFilter = iconFilterClicked.filterValue();
		}
	}
}
