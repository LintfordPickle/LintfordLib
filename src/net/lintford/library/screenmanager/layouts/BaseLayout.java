package net.lintford.library.screenmanager.layouts;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.renderers.windows.UIRectangle;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.MenuScreen.ALIGNMENT;

public abstract class BaseLayout extends UIRectangle implements IScrollBarArea {

	public static final float USE_HEIGHT_OF_ENTRIES = -1;

	public enum ORIENTATION {
		horizontal, vertical;
	}

	public enum ANCHOR {
		top, bottom;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ORIENTATION mOrientation;
	protected ALIGNMENT mAlignment;
	protected ANCHOR mAnchor;

	protected MenuScreen mParentScreen;
	protected List<MenuEntry> mMenuEntries;
	protected int mSelectedEntry = 0;
	protected int mNumberEntries;
	protected TextureBatch mSpriteBatch;

	protected boolean mDrawBackground;
	protected float mR, mG, mB, mA = 1;

	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;

	protected float mMaxWidth;
	protected float mMaxHeight;
	protected float mForcedHeight;
	protected float mForcedEntryHeight;

	private boolean mIsLoaded;

	protected ScrollBarContentRectangle mContentArea;
	protected float mYScrollPos;
	protected ScrollBar mScrollBar;
	protected boolean mScrollBarsEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public void setPadding(float pTop, float pLeft, float pRight, float pBottom) {
		mTopPadding = pTop;
		mLeftPadding = pLeft;
		mRightPadding = pRight;
		mBottomPadding = pBottom;

	}

	public ANCHOR anchor() {
		return mAnchor;
	}

	public void anchor(ANCHOR pNewAnchor) {
		mAnchor = pNewAnchor;
	}

	public ORIENTATION orientation() {
		return mOrientation;
	}

	public void orientation(ORIENTATION pNewAlignment) {
		mOrientation = pNewAlignment;
	}

	public ALIGNMENT alignment() {
		return mAlignment;
	}

	public void alignment(ALIGNMENT pNewAlignment) {
		mAlignment = pNewAlignment;
	}

	public void setCenterPosition(float pCenterX, float pCenterY, float pWidth, float pHeight) {
		x = pCenterX + -pWidth / 2;
		y = pCenterY + -pHeight / 2;

		width = pWidth;
		height = pHeight;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BaseLayout(MenuScreen pParentScreen) {
		super();

		mParentScreen = pParentScreen;
		mMenuEntries = new ArrayList<>();

		mSpriteBatch = new TextureBatch();

		// Set some defaults
		mAlignment = ALIGNMENT.center;
		mOrientation = ORIENTATION.vertical;
		mLeftPadding = 10f;
		mRightPadding = 10f;
		mTopPadding = 5f;
		mBottomPadding = 5f;

		width = 800;
		x = -width / 2;

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
		height = getHeight();

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

		setFocusOffAll(pCore.input().mouseLeftClick());

		if (mContentArea.intersects(pCore.HUD().getMouseCameraSpace())) {
			final int lEntryCount = menuEntries().size();
			for (int i = 0; i < lEntryCount; i++) {
				MenuEntry lMenuEntry = menuEntries().get(i);
				if (lMenuEntry.handleInput(pCore)) {
					return true;
				}
			}
		}

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

		height = getHeight();

		mContentArea.x = x;
		mContentArea.y = y;
		mContentArea.width = width;
		mContentArea.height = getEntryHeight();

		mScrollBar.update(pCore);
		mScrollBarsEnabled = mContentArea.height - getHeight() > 0;

	}

	public void draw(LintfordCore pCore, float pParentZDepth) {

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mSpriteBatch.begin(pCore.HUD());
			final float SCALE = 1f;
			final float ALPHA = 0.6f;
			mSpriteBatch.draw(0, 0, 32, 32, x, y, pParentZDepth + .1f, width, height, SCALE, 1f, 0.2f, 1f, ALPHA, TextureManager.TEXTURE_CORE_UI);
			mSpriteBatch.end();
		}

		if (mDrawBackground) {
			mSpriteBatch.begin(pCore.HUD());
			final float SCALE = 1f;
			mSpriteBatch.draw(0, 0, 32, 32, x, y, pParentZDepth + .1f, width, height, SCALE, mR, mG, mB, mA, TextureManager.TEXTURE_CORE_UI);
			mSpriteBatch.end();
		}

		if (mScrollBarsEnabled) {
			// TODO: SPRITE
			mContentArea.preDraw(pCore, mSpriteBatch);

		}

		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).draw(pCore, mParentScreen, mSelectedEntry == i, pParentZDepth + i * .1f);

		}

		if (mScrollBarsEnabled) {
			mContentArea.postDraw(pCore);
			mScrollBar.draw(pCore, mSpriteBatch, pParentZDepth + .1f);

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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

	/** Turns off the focus for all elements in this layout. */
	public void setFocusOffAll(boolean pLeftMouseClicked) {
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!menuEntries().get(i).hasFocusLock() || pLeftMouseClicked) {
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);
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

		if (mOrientation == ORIENTATION.horizontal) {
			float lResult = 0;
			for (int i = 0; i < lEntryCount; i++) {
				lResult += menuEntries().get(i).paddingHorizontal();
				lResult += menuEntries().get(i).getWidth();
				lResult += menuEntries().get(i).paddingHorizontal();
			}
			return lResult;
		} else {
			// return the widest entry
			float lResult = 0;
			for (int i = 0; i < lEntryCount; i++) {
				float lTemp = menuEntries().get(i).paddingHorizontal() + menuEntries().get(i).getWidth() + menuEntries().get(i).paddingHorizontal();
				if (lTemp > lResult) {
					lResult = lTemp;
				}
			}
			return lResult;
		}

	}

	public float getEntryHeight() {
		if (mForcedEntryHeight != USE_HEIGHT_OF_ENTRIES && mForcedEntryHeight >= 0)
			return mForcedEntryHeight;

		final int lEntryCount = menuEntries().size();
		if (lEntryCount == 0)
			return 0;

		if (mOrientation == ORIENTATION.vertical) {
			// Return the combined height
			float lResult = paddingTop();
			for (int i = 0; i < lEntryCount; i++) {
				lResult += menuEntries().get(i).paddingVertical();
				lResult += menuEntries().get(i).getHeight();
				lResult += menuEntries().get(i).paddingVertical();
			}

			lResult += paddingBottom();

			return lResult;
		} else {
			// return the tallest entry
			float lResult = 0;
			for (int i = 0; i < lEntryCount; i++) {
				float lTemp = menuEntries().get(i).paddingVertical() + menuEntries().get(i).height + menuEntries().get(i).paddingVertical();
				if (lTemp > lResult) {
					lResult = lTemp;
				}
			}
			return 0;
		}
	}

	public float getHeight() {
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
	public UIRectangle windowArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle contentArea() {
		return mContentArea;
	}

}
