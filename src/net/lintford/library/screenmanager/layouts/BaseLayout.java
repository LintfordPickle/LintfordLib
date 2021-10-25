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

	// The margin is applied to the outside of this component
	protected float mTopMargin;
	protected float mBottomMargin;
	protected float mLeftMargin;
	protected float mRightMargin;

	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;

	protected float mMinWidth;
	protected float mMaxWidth = -1; // inactive
	protected float mMinHeight;
	protected float mForcedHeight;
	protected float mForcedEntryHeight;

	private boolean mIsLoaded;

	protected ScrollBarContentRectangle mContentArea;
	protected ScrollBar mScrollBar;
	protected float mYScrollPosition;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;

	protected boolean mScrollBarsEnabled;
	protected boolean mScrollBarsEnabled_Internal;
	protected boolean mEnabled;
	protected boolean mVisible;

	protected float mEntryOffsetFromTop;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean scrollBarsEnabled() {
		return mScrollBarsEnabled;
	}

	public void scrollBarsEnabled(boolean pNewValue) {
		mScrollBarsEnabled = pNewValue;
	}

	public void maxWidth(float pMaxWidth) {
		mMaxWidth = pMaxWidth;
	}

	public float maxWidth() {
		return mMaxWidth;
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
		return mIsLoaded;
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

	/** @returns A list of menu entries so derived classes can change the menu contents. */
	//	public List<MenuEntry> menuEntries() {
	//		return mMenuEntries;
	//	}

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

		mTopMargin = 10f;
		mBottomMargin = 10f;
		mLeftMargin = 5f;
		mRightMargin = 5f;

		mTopPadding = 5.f;
		mBottomPadding = 5.f;
		mLeftPadding = 1.f;
		mRightPadding = 1.f;

		mMinWidth = 100f;
		mMinHeight = 10f;

		mForcedEntryHeight = USE_HEIGHT_OF_ENTRIES;
		mForcedHeight = USE_HEIGHT_OF_ENTRIES;

		mScrollBarsEnabled = true;

		mContentArea = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentArea);

		mEntryOffsetFromTop = 10;

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

	public void loadGLContent(ResourceManager pResourceManager) {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadGLContent(pResourceManager);
		}

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).unloadGLContent();
		}

		mIsLoaded = false;

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
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

		} else {
		}

		updateFocusOfAll(pCore);

		if (mScrollBarsEnabled_Internal) {
			mScrollBar.handleInput(pCore);

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

		mScrollBarsEnabled_Internal = mScrollBarsEnabled && mContentArea.h() - h > 0;
		if (mScrollBarsEnabled_Internal) {

			final float lDeltaTime = (float) pCore.appTime().elapsedTimeMilli() / 1000f;
			float lScrollSpeedFactor = mYScrollPosition;

			mZScrollVelocity += mZScrollAcceleration;
			lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
			mZScrollVelocity *= 0.85f;
			mZScrollAcceleration = 0.0f;
			mYScrollPosition = lScrollSpeedFactor;

			// Constrain
			if (mYScrollPosition > 0)
				mYScrollPosition = 0;
			if (mYScrollPosition < -(mContentArea.h() - this.h)) {
				mYScrollPosition = -(mContentArea.h() - this.h);
			}

			mScrollBar.update(pCore);
		}
	}

	public void draw(LintfordCore pCore, float pComponentDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var lSpriteBatch = parentScreen.spriteBatch();
		final var lSpriteSheetCore = pCore.resources().spriteSheetManager().coreSpritesheet();

		final var lScreenOffset = parentScreen.screenPositionOffset();
		final float lTileSize = 32;

		final var lParentScreenAlpha = parentScreen.screenColor.a;
		layoutColor.a = lParentScreenAlpha;

		if (mDrawBackground) {
			if (h < 64) {
				lSpriteBatch.begin(pCore.HUD());
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT, lScreenOffset.x + x, (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID, lScreenOffset.x + x + lTileSize, (int) (lScreenOffset.y + y), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT, lScreenOffset.x + x + w - lTileSize, (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT, lScreenOffset.x + x, (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID, lScreenOffset.x + x + lTileSize, (int) (lScreenOffset.y + y + h - lTileSize), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, lScreenOffset.x + x + w - lTileSize, (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.end();
			} else {
				lSpriteBatch.begin(pCore.HUD());
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, lScreenOffset.x + x, (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, lScreenOffset.x + x + lTileSize, (int) (lScreenOffset.y + y), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, lScreenOffset.x + x + w - lTileSize, (int) (lScreenOffset.y + y), lTileSize, lTileSize, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, lScreenOffset.x + x, (int) (lScreenOffset.y + y + lTileSize), lTileSize, h - lTileSize * 2, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, lScreenOffset.x + x + lTileSize, (int) (lScreenOffset.y + y + lTileSize), w - lTileSize * 2, h - 64, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, lScreenOffset.x + x + w - lTileSize, (int) (lScreenOffset.y + y + lTileSize), lTileSize, h - lTileSize * 2, pComponentDepth, layoutColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, lScreenOffset.x + x, (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, lScreenOffset.x + x + lTileSize, (int) (lScreenOffset.y + y + h - lTileSize), w - lTileSize * 2, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, lScreenOffset.x + x + w - lTileSize, (int) (lScreenOffset.y + y + h - lTileSize), lTileSize, lTileSize, pComponentDepth, layoutColor);
				lSpriteBatch.end();

				if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_OUTLINES", false)) {
					Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this);
				}
			}
		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.depthPadding(6f);
			mContentArea.preDraw(pCore, lSpriteBatch, lSpriteSheetCore);
		}

		final int lCount = mMenuEntries.size();
		for (int i = lCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).draw(pCore, parentScreen, mSelectedEntry == i, pComponentDepth + i * .001f);
		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.postDraw(pCore);
			lSpriteBatch.begin(pCore.HUD());
			mScrollBar.scrollBarAlpha(lParentScreenAlpha);
			mScrollBar.draw(pCore, lSpriteBatch, lSpriteSheetCore, pComponentDepth + .1f);
			lSpriteBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
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

		// Return the combined height
		float lResult = marginTop();

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			lResult += lMenuEntry.marginTop();
			lResult += lMenuEntry.h();
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
	public float currentYPos() {
		return mYScrollPosition;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mYScrollPosition += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mYScrollPosition = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return this;
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
