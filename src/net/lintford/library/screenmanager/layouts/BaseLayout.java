package net.lintford.library.screenmanager.layouts;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;

/**
 * The dimensions of the BaseLayout are set by the parent screen.
 */
public abstract class BaseLayout extends Rectangle implements IScrollBarArea {

	private static final long serialVersionUID = 5742176250891551930L;

	public static final float USE_HEIGHT_OF_ENTRIES = -1;

	public final static float LAYOUT_WIDTH = 800;

	public enum FILL_TYPE {
		DYNAMIC, STATIC
	};

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FILL_TYPE mFillType = FILL_TYPE.STATIC;
	protected ALIGNMENT mAlignment;

	protected MenuScreen mParentScreen;
	protected List<MenuEntry> mMenuEntries;
	protected int mSelectedEntry = 0;
	protected int mNumberEntries;
	protected TextureBatch mSpriteBatch;

	protected boolean mDrawBackground;
	protected float mR, mG, mB, mA = 1;

	// The margin is applied to the outside of this component
	protected float mTopMargin;
	protected float mBottomMargin;
	protected float mLeftMargin;
	protected float mRightMargin;

	protected float mMinWidth;
	protected float mMinHeight;
	protected float mForcedHeight;
	protected float mForcedEntryHeight;

	private boolean mIsLoaded;

	protected ScrollBarContentRectangle mContentArea;
	protected float mYScrollPos;
	protected ScrollBar mScrollBar;
	protected boolean mScrollBarsEnabled;
	protected boolean mEnabled;
	protected boolean mVisible; // ony affects drawing

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FILL_TYPE fillType() {
		return mFillType;
	}

	public void fillType(FILL_TYPE pValue) {
		mFillType = pValue;
	}

	public void minWidth(float pNewValue) {
		mMinWidth = pNewValue;

	}

	public void minHeight(float pNewValue) {
		mMinHeight = pNewValue;

	}

	public MenuScreen parentScreen() {
		return mParentScreen;
	}

	public void setDrawBackground(boolean pEnabled, float pR, float pG, float pB, float pA) {
		mDrawBackground = pEnabled;
		mR = pR;
		mG = pG;
		mB = pB;
		mA = pA;

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

	public ALIGNMENT alignment() {
		return mAlignment;
	}

	public void alignment(ALIGNMENT pNewAlignment) {
		mAlignment = pNewAlignment;
	}

	/** @returns A list of menu entries so derived classes can change the menu contents. */
	public List<MenuEntry> menuEntries() {
		return mMenuEntries;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseLayout(MenuScreen pParentScreen) {
		super();

		mParentScreen = pParentScreen;
		mMenuEntries = new ArrayList<>();

		mSpriteBatch = new TextureBatch();
		mEnabled = true;
		mVisible = true;

		// Set some defaults
		mAlignment = ALIGNMENT.center;

		w = LAYOUT_WIDTH;
		x = -w / 2;

		mTopMargin = 10f;
		mBottomMargin = 10f;
		mLeftMargin = 5f;
		mRightMargin = 5f;

		mMinWidth = 100f;
		mMinHeight = 10f;

		mForcedEntryHeight = USE_HEIGHT_OF_ENTRIES;
		mForcedHeight = USE_HEIGHT_OF_ENTRIES;

		mContentArea = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentArea);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).initialise();
		}

		// width = getEntryWidth();
		h = getDesiredHeight();

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadGLContent(pResourceManager);
		}

		mSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).unloadGLContent();
		}

		mSpriteBatch.unloadGLContent();

		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		if (menuEntries() == null || menuEntries().size() == 0)
			return false; // nothing to do

		// if (mContentArea.intersects(pCore.HUD().getMouseCameraSpace())) {
		final int lEntryCount = menuEntries().size();
		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lMenuEntry = menuEntries().get(i);
			if (lMenuEntry.handleInput(pCore)) {
				return true;
			}
		}

		// }

		updateFocusOfAll(pCore);

		if (mScrollBarsEnabled) {
			mScrollBar.handleInput(pCore);

		}

		return false;
	}

	public void update(LintfordCore pCore) {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			boolean lIsSelected = mParentScreen.isActive() && (i == mSelectedEntry);
			mMenuEntries.get(i).update(pCore, mParentScreen, lIsSelected);

		}

		mContentArea.x = x;
		mContentArea.y = y;
		mContentArea.w = w;
		mContentArea.h = getEntryHeight();

		mScrollBar.update(pCore);
		mScrollBarsEnabled = mContentArea.h - h > 0;

	}

	public void draw(LintfordCore pCore, float pComponentDepth) {
		if (!mEnabled || !mVisible)
			return;

		if (mDrawBackground) {
			final float TILE_SIZE = 32f;

			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 64, TILE_SIZE, TILE_SIZE, x, y, TILE_SIZE, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 64, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y, w - 64, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 64, TILE_SIZE, TILE_SIZE, x + w - 32, y, TILE_SIZE, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);

			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 96, TILE_SIZE, TILE_SIZE, x, y + 32, TILE_SIZE, h - 64, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 96, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + 32, w - 64, h - 64, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 96, TILE_SIZE, TILE_SIZE, x + w - 32, y + 32, TILE_SIZE, h - 64, pComponentDepth, 1, 1, 1, 0.85f);

			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 448, 128, TILE_SIZE, TILE_SIZE, x, y + h - 32, TILE_SIZE, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 480, 128, TILE_SIZE, TILE_SIZE, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 512, 128, TILE_SIZE, TILE_SIZE, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, pComponentDepth, 1, 1, 1, 0.85f);
			mSpriteBatch.end();
		}

		if (mScrollBarsEnabled) {
			mContentArea.depthPadding(6f);
			mContentArea.preDraw(pCore, mSpriteBatch);

		}

		int lCount = menuEntries().size();
		for (int i = lCount - 1; i >= 0; --i) {
			menuEntries().get(i).draw(pCore, mParentScreen, mSelectedEntry == i, pComponentDepth + i * .001f);

		}

		if (mScrollBarsEnabled) {
			mScrollBar.draw(pCore, mSpriteBatch, pComponentDepth + .1f);
			mContentArea.postDraw(pCore);

		}

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, ZLayers.LAYER_DEBUG, 1f, 0.2f, 1f, 0.4f);
			mSpriteBatch.end();
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void updateStructureDimensions() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).updateStructureDimensions();

		}

	}

	public void updateStructurePositions() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).updateStructurePositions();

		}

	}

	/**
	 * Checks to see if any UI element on this layout has a focus lock. (i.e. if some element is currently being used, like an input field).
	 * 
	 * @return true if some element has a focus lock, false otherwise.
	 */
	public boolean doesElementHaveFocus() {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (menuEntries().get(i).hasFocusLock())
				return true;
		}
		return false;
	}

	public void setFocusOn(InputState pInputState, MenuEntry pMenuEntry, boolean pForce) {

		// If another entry has locked the focus
		// (i.e. another input entry), then don't change focus
		if (doesElementHaveFocus() && !pForce)
			return;

		// Set focus to this entry
		pMenuEntry.onClick(pInputState);

		// and disable focus on the rest
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!menuEntries().get(i).equals(pMenuEntry)) {
				// reset other element focuses
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);
			} else {
				// Remember which element we have just focused on
				mSelectedEntry = i;
			}
		}
	}

	/** Updates the focus of all {@link MenuEntry}s in this layout, based on the current {@link InputState}. */
	public void updateFocusOfAll(LintfordCore pCore) {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			MenuEntry lMenuEntry = menuEntries().get(i);

			// Update the hovered over status (needed to disable hovering on entries)
			if (lMenuEntry.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
				lMenuEntry.hoveredOver(true);

			} else {
				lMenuEntry.hoveredOver(false);

			}

			// Update the focus of entries where the mouse is clicked in other areas (other than any
			// one specific entry).
			if (pCore.input().mouseLeftClick()) {
				if (lMenuEntry.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
					lMenuEntry.hasFocus(true);

				} else {
					lMenuEntry.hasFocus(false);

				}

			}

		}

	}

	/** Sets the focus of a specific {@link MenuEntry}, removing focus from all other entries. */
	public void updateFocusOffAllBut(LintfordCore pCore, MenuEntry pMenuEntry) {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			MenuEntry lMenuEntry = menuEntries().get(i);
			if (pMenuEntry == lMenuEntry)
				continue;

			if (pCore.input().mouseLeftClick()) {
				if (lMenuEntry.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
					lMenuEntry.hasFocus(true);

				} else {
					lMenuEntry.hasFocus(false);

				}

			}

		}

	}

	public void setHoverOffAll() {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).hoveredOver(false);
		}
	}

	public boolean hasEntry(int pIndex) {
		if (pIndex < 0 || pIndex >= menuEntries().size())
			return false;

		return true;
	}

	public float getEntryWidth() {
		final int lEntryCount = menuEntries().size();
		if (lEntryCount == 0)
			return 0;

		// return the widest entry
		float lResult = 0;
		for (int i = 0; i < lEntryCount; i++) {
			float lTemp = menuEntries().get(i).marginLeft() + menuEntries().get(i).w + menuEntries().get(i).marginRight();
			if (lTemp > lResult) {
				lResult = lTemp;
			}
		}

		return lResult;

	}

	public float getEntryHeight() {
		if (mForcedEntryHeight != USE_HEIGHT_OF_ENTRIES && mForcedEntryHeight >= 0)
			return mForcedEntryHeight;

		final int lEntryCount = menuEntries().size();
		if (lEntryCount == 0)
			return 0;

		// Return the combined height
		float lResult = 0;

		for (int i = 0; i < lEntryCount; i++) {
			MenuEntry lEntry = menuEntries().get(i);
//			if (!menuEntries().get(i).enabled())
//				continue;

			lResult += lEntry.marginTop();
			lResult += lEntry.h;
			lResult += lEntry.marginBottom();
		}

		return lResult;

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
		return mYScrollPos;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mYScrollPos += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mYScrollPos = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentArea;
	}

}
