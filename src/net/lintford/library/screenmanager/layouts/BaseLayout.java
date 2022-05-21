package net.lintford.library.screenmanager.layouts;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;

/**
 * The dimensions of the BaseLayout are set by the parent screen.
 */
public abstract class BaseLayout extends Rectangle implements IScrollBarArea {

	private static final long serialVersionUID = 5742176250891551930L;

	public static final float USE_HEIGHT_OF_ENTRIES = -1;

	protected static final float TITLE_BAR_HEIGHT = 32.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected LAYOUT_WIDTH mLayoutWidth = LAYOUT_WIDTH.THREEQUARTER;
	protected FILLTYPE mLayoutFillType = FILLTYPE.FILL_CONTAINER;

	public final ScreenManager screenManager;
	public final MenuScreen parentScreen;
	public final Color layoutColor = new Color(ColorConstants.WHITE);

	protected List<MenuEntry> mMenuEntries;
	protected int mSelectedEntry = 0;
	protected int mNumberEntries;

	protected boolean mDrawBackground;

	// Margin is applied to the outside of this component
	protected float mTopMargin;
	protected float mBottomMargin;
	protected float mLeftMargin;
	protected float mRightMargin;

	// Padding is applied internally on the component
	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;

	protected float mMinWidth;
	protected float mMaxWidth = -1; // inactive
	protected float mMinHeight;
	protected float mMaxHeight = -1; // inactive
	protected float mForcedHeight;
	protected float mForcedEntryHeight;

	private boolean mResourcesLoaded;

	protected final Rectangle contentDisplayRectange = new Rectangle();
	protected ScrollBarContentRectangle mContentArea;
	protected ScrollBar mScrollBar;

	protected boolean mEnabled;
	protected boolean mVisible;

	protected float mEntryOffsetFromTop;
	protected String mLayoutTitle;
	protected boolean mShowTitle;

	protected float mCropPaddingBottom = 0.f;
	protected float mCropPaddingTop = 0.f;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float titleBarSize() {
		return mShowTitle ? TITLE_BAR_HEIGHT : 0.f;
	}

	public void showTitle(boolean pNewShowTitle) {
		mShowTitle = pNewShowTitle;
	}

	public void title(String pNewTitle) {
		mLayoutTitle = pNewTitle;
	}

	public void maxWidth(float pMaxWidth) {
		mMaxWidth = pMaxWidth;
	}

	public float maxWidth() {
		return mMaxWidth;
	}

	public void maxHeight(float pMaxHeight) {
		mMaxHeight = pMaxHeight;
	}

	public float maxHeight() {
		return mMaxHeight;
	}

	public void setEntryOffsetY(float pNewOffset) {
		mEntryOffsetFromTop = pNewOffset;
	}

	public FILLTYPE layoutFillType() {
		return mLayoutFillType;

	}

	public void layoutFillType(FILLTYPE pFilltype) {
		if (pFilltype == null)
			return;

		mLayoutFillType = pFilltype;

	}

	public LAYOUT_WIDTH layoutWidth() {
		return mLayoutWidth;

	}

	public void layoutWidth(LAYOUT_WIDTH pLayoutWidth) {
		if (pLayoutWidth == null)
			return;

		mLayoutWidth = pLayoutWidth;

	}

	public void minWidth(float pNewValue) {
		mMinWidth = pNewValue;

	}

	public void minHeight(float pNewValue) {
		mMinHeight = pNewValue;

	}

	public void setDrawBackground(boolean pEnabled, Color pColor) {
		mDrawBackground = pEnabled;
		layoutColor.setFromColor(pColor);
	}

	public boolean isLoaded() {
		return mResourcesLoaded;
	}

	public float marginLeft() {
		return mLeftMargin;
	}

	public float marginRight() {
		return mRightMargin;
	}

	public float marginTop() {
		return mTopMargin;
	}

	public float marginBottom() {
		return mBottomMargin;
	}

	public void marginLeft(float pNewValue) {
		mLeftMargin = pNewValue;
	}

	public void marginRight(float pNewValue) {
		mRightMargin = pNewValue;
	}

	public void marginTop(float pNewValue) {
		mTopMargin = pNewValue;
	}

	public void marginBottom(float pNewValue) {
		mBottomMargin = pNewValue;
	}

	public float paddingLeft() {
		return mLeftPadding;
	}

	public float paddingRight() {
		return mRightPadding;
	}

	public float paddingTop() {
		return mTopPadding;
	}

	public float paddingBottom() {
		return mBottomPadding;
	}

	public void paddingLeft(float pNewValue) {
		mLeftPadding = pNewValue;
	}

	public void paddingRight(float pNewValue) {
		mRightPadding = pNewValue;
	}

	public void paddingTop(float pNewValue) {
		mTopPadding = pNewValue;
	}

	public void paddingBottom(float pNewValue) {
		mBottomPadding = pNewValue;
	}

	/** Forces the layout to use the height value provided. Use BaseLayout.USE_HEIGHT_OF_ENTRIES value to use the sum of the entry heights. */
	public void forceHeight(float pNewHeight) {
		mForcedHeight = pNewHeight;
	}

	/** Forces the layout to use the height value provided for the total height of all the content. Use BaseLayout.USE_HEIGHT_OF_ENTRIES value to use the sum of the entry heights. */
	public void forceEntryHeight(float pNewHeight) {
		mForcedEntryHeight = pNewHeight;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}

	public boolean visible() {
		return mVisible;
	}

	public void visible(boolean pEnabled) {
		mVisible = pEnabled;
	}

	public float cropPaddingBottom() {
		return mCropPaddingBottom;
	}

	/**This is the amount of padding to add to the bottom of the inner-area when cropping during scrolling. The amount should be tied with the graphic CoreSpriteSheet.Panel3x3Bottom*/
	public void cropPaddingBottom(float pNewCropPaddingTop) {
		mCropPaddingBottom = pNewCropPaddingTop;
	}

	public float cropPaddingTop() {
		return mCropPaddingTop;
	}

	/**This is the amount of padding to add to the top of the inner-area when cropping during scrolling. 
	 * The amount should be tied with the graphic CoreSpriteSheet.Panel3x3Top.
	 * n.b. If the ShowTitleBar is enabled, then the cropping amount should likely be reduced to take this (additional offset) into account*/
	public void cropPaddingTop(float pNewCropPaddingTop) {
		mCropPaddingTop = pNewCropPaddingTop;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseLayout(MenuScreen pParentScreen) {
		super();

		screenManager = pParentScreen.screenManager;
		parentScreen = pParentScreen;
		mMenuEntries = new ArrayList<>();

		mEnabled = true;
		mVisible = true;

		mTopMargin = 0.f;
		mBottomMargin = 0.f;
		mLeftMargin = 0.f;
		mRightMargin = 0.f;

		mTopPadding = 0.f;
		mBottomPadding = 0.f;
		mLeftPadding = 0.f;
		mRightPadding = 0.f;

		mMinWidth = 100f;
		mMinHeight = 10f;

		mForcedEntryHeight = USE_HEIGHT_OF_ENTRIES;
		mForcedHeight = USE_HEIGHT_OF_ENTRIES;

		mContentArea = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentArea);

		mEntryOffsetFromTop = 0;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).initialize();
		}

		// width = getEntryWidth();
		h = getDesiredHeight();

	}

	public void loadResources(ResourceManager pResourceManager) {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadResources(pResourceManager);
		}

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).unloadResources();
		}

		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore pCore) {
		if (mMenuEntries == null || mMenuEntries.size() == 0)
			return false; // nothing to do

		final var lEntryCount = mMenuEntries.size();
		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (lMenuEntry.handleInput(pCore)) {
				// return true;
			}
		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			if (true && pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				final float scrollAccelerationAmt = pCore.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		updateFocusOfAll(pCore);

		if (mScrollBar.scrollBarEnabled()) {
			mScrollBar.handleInput(pCore, screenManager);
		}

		return false;
	}

	public void update(LintfordCore pCore) {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			boolean lIsSelected = parentScreen.isActive() && (i == mSelectedEntry);
			mMenuEntries.get(i).update(pCore, parentScreen, lIsSelected);
		}

		final var lScreenOffset = parentScreen.screenPositionOffset();
		mContentArea.set(lScreenOffset.x + x, lScreenOffset.y + y, w, getEntryHeight());

		final float lTitleHeight = TITLE_BAR_HEIGHT;
		final float lCropFooterHeight = mCropPaddingBottom;
		final float lCropHeaderHeight = mShowTitle ? mCropPaddingTop + lTitleHeight : mCropPaddingTop;
		contentDisplayRectange.set(x, y + lCropHeaderHeight, w, h - lCropHeaderHeight - lCropFooterHeight);

		mScrollBar.update(pCore);
	}

	public void draw(LintfordCore pCore, float pComponentDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var lSpriteBatch = parentScreen.spriteBatch();
		final var lSpriteSheetCore = pCore.resources().spriteSheetManager().coreSpritesheet();
		final var lParentScreenAlpha = parentScreen.screenColor.a;

		layoutColor.a = lParentScreenAlpha;

		if (mDrawBackground) {
			drawBackground(pCore, mShowTitle, pComponentDepth);
		}

		if (mShowTitle) {
			final var lTitleFont = parentScreen.rendererManager.uiHeaderFont();
			final var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(parentScreen.screenColor.a);

			lTitleFont.begin(pCore.HUD());
			lTitleFont.drawText(mLayoutTitle, x + 20.f, y + 20.f - (lTitleFont.fontHeight() / 2.f), pComponentDepth, lWhiteColorWithAlpha, 1.0f);
			lTitleFont.end();
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.preDraw(pCore, lSpriteBatch, lSpriteSheetCore);
		}

		final int lMenuEntryCount = mMenuEntries.size();
		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).draw(pCore, parentScreen, mSelectedEntry == i, pComponentDepth + i * .001f);
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.postDraw(pCore);
			lSpriteBatch.begin(pCore.HUD());
			mScrollBar.scrollBarAlpha(lParentScreenAlpha);
			mScrollBar.draw(pCore, lSpriteBatch, lSpriteSheetCore, pComponentDepth + .1f);
			lSpriteBatch.end();
		}

		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).postStencilDraw(pCore, parentScreen, mSelectedEntry == i, pComponentDepth + i * .001f);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	// TODO: Move this into a 'CorePanelRenderer'
	private void drawBackground(LintfordCore pCore, boolean pWithTitlebar, float pComponentDepth) {
		final var lSpriteBatch = parentScreen.spriteBatch();
		final var lSpriteSheetCore = pCore.resources().spriteSheetManager().coreSpritesheet();

		final var lScreenOffset = parentScreen.screenPositionOffset();
		final float lTileSize = 32;

		lSpriteBatch.begin(pCore.HUD());
		if (pWithTitlebar) {
			if (h < 64) {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
			} else {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y), w - lTileSize * 2 + 1, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + lTileSize), lTileSize, h - lTileSize * 2 + 1, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + lTileSize), (int) (w - lTileSize * 2) + 1, h - 64 + 1, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + lTileSize), (int) lTileSize, (int) (h - lTileSize * 2) + 1, pComponentDepth,
						layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), (int) (w - lTileSize * 2) + 1, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
			}
		} else {
			if (h < 64) {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
			} else {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y), w - lTileSize * 2 + 1, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + lTileSize), lTileSize, h - lTileSize * 2 + 1, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + lTileSize), (int) (w - lTileSize * 2) + 1, h - 64 + 1, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + lTileSize), (int) lTileSize, (int) (h - lTileSize * 2) + 1, pComponentDepth,
						layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, (int) (lScreenOffset.x + x), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, (int) (lScreenOffset.x + x + lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), (int) (w - lTileSize * 2) + 1, (int) lTileSize, pComponentDepth,
						layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - lTileSize), (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
			}
		}
		lSpriteBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_OUTLINES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clearMenuEntries() {
		mMenuEntries.clear();
	}

	public int getMenuEntryCount() {
		return mMenuEntries.size();
	}

	public MenuEntry getMenuEntryByIndex(int pMenuEntryIndex) {
		if (pMenuEntryIndex < 0 || pMenuEntryIndex > mMenuEntries.size() - 1)
			return null;
		return mMenuEntries.get(pMenuEntryIndex);
	}

	public void addMenuEntry(MenuEntry pNewEntry) {
		mMenuEntries.add(pNewEntry);
	}

	public void removeMenuEntry(MenuEntry pMenuEntry) {
		mMenuEntries.remove(pMenuEntry);
	}

	public void updateStructure() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).updateStructure();
		}
	}

	/**
	 * Checks to see if any UI element on this layout has a focus lock. (i.e. if some element is currently being used, like an input field).
	 * 
	 * @return true if some element has a focus lock, false otherwise.
	 */
	public boolean doesElementHaveFocus() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			if (mMenuEntries.get(i).hasFocusLock())
				return true;
		}
		return false;
	}

	public void setFocusOn(InputManager pInputState, MenuEntry pMenuEntry, boolean pForce) {

		// If another entry has locked the focus
		// (i.e. another input entry), then don't change focus
		if (doesElementHaveFocus() && !pForce)
			return;

		// Set focus to this entry
		pMenuEntry.onClick(pInputState);

		// and disable focus on the rest
		final var lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			if (!mMenuEntries.get(i).equals(pMenuEntry)) {
				mMenuEntries.get(i).hasFocus(false);
				mMenuEntries.get(i).hoveredOver(false);
			} else {
				mSelectedEntry = i;
			}
		}
	}

	/** Updates the focus of all {@link MenuEntry}s in this layout, based on the current {@link InputManager}. */
	public void updateFocusOfAll(LintfordCore pCore) {
		final var lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			final boolean lIsTheMouseOverThisComponent = lMenuEntry.intersectsAA(pCore.HUD().getMouseCameraSpace());
			final boolean lDoesThisComponentOwnTheMouse = pCore.input().mouse().isMouseOverThisComponent(lMenuEntry.hashCode());

			// Update the hovered over status (needed to disable hovering on entries)
			if (lDoesThisComponentOwnTheMouse && lIsTheMouseOverThisComponent) {
				lMenuEntry.hoveredOver(true);
			} else {
				lMenuEntry.hoveredOver(false);
			}

			// Update the focus of entries where the mouse is clicked in other areas (other than any one specific entry).

			if (pCore.input().mouse().isMouseLeftButtonDown()) {
				if (lDoesThisComponentOwnTheMouse && lIsTheMouseOverThisComponent) {
					lMenuEntry.hasFocus(true);
				} else {
					lMenuEntry.hasFocus(false);
				}
			}
		}
	}

	/** Sets the focus of a specific {@link MenuEntry}, removing focus from all other entries. */
	public void updateFocusOffAllBut(LintfordCore pCore, MenuEntry pMenuEntry) {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (pMenuEntry == lMenuEntry)
				continue;

			if (pCore.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				if (lMenuEntry.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
					lMenuEntry.hasFocus(true);

				} else {
					lMenuEntry.hasFocus(false);

				}
			}
		}
	}

	public void setHoverOffAll() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).hoveredOver(false);
		}
	}

	public boolean hasEntry(int pIndex) {
		if (pIndex < 0 || pIndex >= mMenuEntries.size())
			return false;

		return true;
	}

	public float getEntryWidth() {
		final int lEntryCount = mMenuEntries.size();
		if (lEntryCount == 0)
			return 0;

		// return the widest entry
		float lResult = 0;
		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			float lTemp = lMenuEntry.marginLeft() + lMenuEntry.w() + lMenuEntry.marginRight();
			if (lTemp > lResult) {
				lResult = lTemp;
			}
		}

		return lResult;
	}

	public float getEntryHeight() {
		if (mForcedEntryHeight != USE_HEIGHT_OF_ENTRIES && mForcedEntryHeight >= 0)
			return mForcedEntryHeight;

		final int lEntryCount = mMenuEntries.size();
		if (lEntryCount == 0)
			return 0;

		float lResult = paddingTop() + paddingBottom();

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (lMenuEntry.isDormant())
				continue;
			lResult += lMenuEntry.marginTop();
			lResult += lMenuEntry.height();
			lResult += lMenuEntry.marginBottom();
		}

		return lResult + marginBottom();
	}

	public float getDesiredHeight() {
		if (mForcedHeight != USE_HEIGHT_OF_ENTRIES && mForcedHeight >= 0)
			return mForcedHeight;

		return getEntryHeight();
	}

	// --------------------------------------
	// IScrollBarArea Methods
	// --------------------------------------

	@Override
	public Rectangle contentDisplayArea() {
		return contentDisplayRectange;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentArea;
	}

	// --------------------------------------
	// Events
	// --------------------------------------

	public void onViewportChange(float pWidth, float pHeight) {
		final int lLayoutCount = mMenuEntries.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mMenuEntries.get(i).onViewportChange(pWidth, pHeight);

		}

	}
}