package net.lintfordlib.screenmanager.layouts;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;

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
	public final Color layoutColor = new Color(ColorConstants.WHITE());

	protected List<MenuEntry> mMenuEntries;
	protected int mFocusedEntryIndex;

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

	public void focusedEntryIndex(int focusedEntryIndex) {
		mFocusedEntryIndex = focusedEntryIndex;
	}

	public int focusedEntryIndex() {
		return mFocusedEntryIndex;
	}

	public List<MenuEntry> entries() {
		return mMenuEntries;
	}

	public float titleBarSize() {
		return mShowTitle ? TITLE_BAR_HEIGHT : 0.f;
	}

	public void showTitle(boolean newShowTitle) {
		mShowTitle = newShowTitle;
	}

	public void title(String newTitle) {
		mLayoutTitle = newTitle;
	}

	public void maxWidth(float maxWidth) {
		mMaxWidth = maxWidth;
	}

	public float maxWidth() {
		return mMaxWidth;
	}

	public boolean maxWidthDefined() {
		return mMaxWidth != -1;
	}

	public void maxHeight(float maxHeight) {
		mMaxHeight = maxHeight;
	}

	public float maxHeight() {
		return mMaxHeight;
	}

	public boolean maxHeightDefined() {
		return mMaxHeight != -1;
	}

	public void setEntryOffsetY(float newOffset) {
		mEntryOffsetFromTop = newOffset;
	}

	public FILLTYPE layoutFillType() {
		return mLayoutFillType;

	}

	public void layoutFillType(FILLTYPE filltype) {
		if (filltype == null)
			return;

		mLayoutFillType = filltype;
	}

	public LAYOUT_WIDTH layoutWidth() {
		return mLayoutWidth;

	}

	public void layoutWidth(LAYOUT_WIDTH layoutWidth) {
		if (layoutWidth == null)
			return;

		mLayoutWidth = layoutWidth;
	}

	public void minWidth(float newValue) {
		mMinWidth = newValue;
	}

	public void minHeight(float newValue) {
		mMinHeight = newValue;
	}

	public void setDrawBackground(boolean enabled, Color color) {
		mDrawBackground = enabled;
		layoutColor.setFromColor(color);
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

	public void marginLeft(float newValue) {
		mLeftMargin = newValue;
	}

	public void marginRight(float newValue) {
		mRightMargin = newValue;
	}

	public void marginTop(float newValue) {
		mTopMargin = newValue;
	}

	public void marginBottom(float newValue) {
		mBottomMargin = newValue;
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

	public void paddingLeft(float newValue) {
		mLeftPadding = newValue;
	}

	public void paddingRight(float newValue) {
		mRightPadding = newValue;
	}

	public void paddingTop(float newValue) {
		mTopPadding = newValue;
	}

	public void paddingBottom(float newValue) {
		mBottomPadding = newValue;
	}

	/** Forces the layout to use the height value provided. Use BaseLayout.USE_HEIGHT_OF_ENTRIES value to use the sum of the entry heights. */
	public void forceHeight(float newValue) {
		mForcedHeight = newValue;
	}

	/** Forces the layout to use the height value provided for the total height of all the content. Use BaseLayout.USE_HEIGHT_OF_ENTRIES value to use the sum of the entry heights. */
	public void forceEntryHeight(float newValue) {
		mForcedEntryHeight = newValue;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean enabled) {
		mEnabled = enabled;
	}

	public boolean visible() {
		return mVisible;
	}

	public void visible(boolean enabled) {
		mVisible = enabled;
	}

	public float cropPaddingBottom() {
		return mCropPaddingBottom;
	}

	/** This is the amount of padding to add to the bottom of the inner-area when cropping during scrolling. The amount should be tied with the graphic CoreSpriteSheet.Panel3x3Bottom */
	public void cropPaddingBottom(float newCropPaddingTop) {
		mCropPaddingBottom = newCropPaddingTop;
	}

	public float cropPaddingTop() {
		return mCropPaddingTop;
	}

	/**
	 * This is the amount of padding to add to the top of the inner-area when cropping during scrolling. The amount should be tied with the graphic CoreSpriteSheet.Panel3x3Top. n.b. If the ShowTitleBar is enabled, then the cropping amount should likely be reduced to take this (additional offset) into account
	 */
	public void cropPaddingTop(float newCropPaddingTop) {
		mCropPaddingTop = newCropPaddingTop;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	protected BaseLayout(MenuScreen pParentScreen) {
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
		final var lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).initialize();
		}

		// width = getEntryWidth();
		mH = getDesiredHeight();

	}

	public void loadResources(ResourceManager resourceManager) {
		final var lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadResources(resourceManager);
		}

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		final var lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).unloadResources();
		}

		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore core) {
		if (mMenuEntries == null || mMenuEntries.isEmpty())
			return false; // nothing to do

		// limit mouse interaction within the baseLayout to within the contentDisplayArea
		// due to the constraints imposed by the title bar, via the crop top and crop bottom, the contentDisplayArea is a subset of the layout
		if (core.input().mouse().isMouseMenuSelectionEnabled() && contentDisplayArea().intersectsAA(core.HUD().getMouseCameraSpace())) {
			final int lCount = mMenuEntries.size();
			for (int i = 0; i < lCount; i++) {
				var lInputHandled = false;
				lInputHandled = mMenuEntries.get(i).onHandleMouseInput(core);

				if (lInputHandled)
					return lInputHandled;
			}

			if (core.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				final float scrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			var lInputHandled = false;
			lInputHandled = mMenuEntries.get(i).onHandleKeyboardInput(core);
			lInputHandled = lInputHandled || mMenuEntries.get(i).onHandleGamepadInput(core);

			if (lInputHandled)
				return lInputHandled;
		}

		if (mScrollBar.scrollBarEnabled()) {
			mScrollBar.handleInput(core, screenManager);
		}

		return false;
	}

	public void update(LintfordCore core) {
		var lFocusedEntry = (MenuEntry) null;

		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).update(core, parentScreen);

			if (mMenuEntries.get(i).hasFocus() || mMenuEntries.get(i).isActive()) {
				lFocusedEntry = mMenuEntries.get(i);
			}
		}

		final var lScreenOffset = parentScreen.screenPositionOffset();
		mContentArea.set(lScreenOffset.x + mX, lScreenOffset.y + mY, mW, getEntryHeight());

		final float lTitleHeight = TITLE_BAR_HEIGHT;
		final float lCropFooterHeight = mCropPaddingBottom;
		final float lCropHeaderHeight = mShowTitle ? mCropPaddingTop + lTitleHeight : mCropPaddingTop;
		contentDisplayRectange.set(mX, mY + lCropHeaderHeight, mW, mH - lCropHeaderHeight - lCropFooterHeight);

		mScrollBar.update(core);

		final var lMouseMenuControls = core.input().mouse().isMouseMenuSelectionEnabled();
		if (lFocusedEntry != null && mScrollBar.scrollBarEnabled() && !lMouseMenuControls) {
			final var lWindowTopExtent = contentDisplayRectange.y() + mCropPaddingTop;
			final var lWindowBottomExtent = contentDisplayRectange.bottom() - mCropPaddingBottom;

			final var lEntryTopExtent = lFocusedEntry.y();
			final var lEntryBottomExtent = lFocusedEntry.bottom();

			if (lEntryTopExtent < lWindowTopExtent) {
				if (Math.abs(lEntryTopExtent - lWindowTopExtent) > 20)
					mScrollBar.RelCurrentYPos(20);
				else if (Math.abs(lEntryTopExtent - lWindowTopExtent) > 5)
					mScrollBar.RelCurrentYPos(5);
				else
					mScrollBar.RelCurrentYPos(1);
			}

			if (lEntryBottomExtent > lWindowBottomExtent) {
				if (Math.abs(lEntryBottomExtent - lWindowBottomExtent) > 20)
					mScrollBar.RelCurrentYPos(-20);
				else if (Math.abs(lEntryBottomExtent - lWindowBottomExtent) > 5)
					mScrollBar.RelCurrentYPos(-5);
				else
					mScrollBar.RelCurrentYPos(-1);
			}
		}
	}

	public void draw(LintfordCore core, float componentDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var lSpriteBatch = parentScreen.spriteBatch();
		final var lSpriteSheetCore = core.resources().spriteSheetManager().coreSpritesheet();

		final var lScreenOffset = parentScreen.screenPositionOffset();

		if (mDrawBackground) {
			final int ts = 32;

			final var lColor = ColorConstants.getColorWithAlpha(layoutColor, layoutColor.a * parentScreen.screenColor.a);

			final int x = (int) (lScreenOffset.x + mX);
			final int y = (int) (lScreenOffset.y + mY);
			final int w = (int) mW;
			final int h = (int) mH;

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(lColor);
			TextureBatch9Patch.drawBackground(lSpriteBatch, lSpriteSheetCore, ts, x, y, w, h, mShowTitle, componentDepth);
			lSpriteBatch.end();
		}

		if (mShowTitle) {
			final var lTitleFont = parentScreen.rendererManager().sharedResources().uiHeaderFont();

			lTitleFont.begin(core.HUD());
			lTitleFont.setTextColorRGBA(1.f, 1.f, 1.f, parentScreen.screenColor.a);
			lTitleFont.drawText(mLayoutTitle, lScreenOffset.x + mX + 20.f, mY + 20.f - (lTitleFont.fontHeight() / 2.f), componentDepth, 1.0f);
			lTitleFont.end();
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.preDraw(core, lSpriteBatch);
		}

		final int lMenuEntryCount = mMenuEntries.size();
		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).draw(core, parentScreen, componentDepth + i * .001f);
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.postDraw(core);
			lSpriteBatch.begin(core.HUD());
			mScrollBar.positionOffset.x = lScreenOffset.x;
			mScrollBar.positionOffset.y = lScreenOffset.y;

			mScrollBar.scrollBarAlpha(parentScreen.screenColor.a);
			mScrollBar.draw(core, lSpriteBatch, lSpriteSheetCore, componentDepth + .1f);
			lSpriteBatch.end();
		}

		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).postStencilDraw(core, parentScreen, componentDepth + i * .001f);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, mY, mW, mH, ZLayers.LAYER_DEBUG);
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

	public MenuEntry getMenuEntryByIndex(int menuEntryIndex) {
		if (menuEntryIndex < 0 || menuEntryIndex > mMenuEntries.size() - 1)
			return null;
		return mMenuEntries.get(menuEntryIndex);
	}

	public void addMenuEntry(MenuEntry entryToAdd) {
		mMenuEntries.add(entryToAdd);
	}

	public void removeMenuEntry(MenuEntry entryToRemove) {
		mMenuEntries.remove(entryToRemove);
	}

	public void updateStructure() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).updateStructure();
		}
	}

	public boolean hasEntry(int menuIndex) {
		return menuIndex < 0 || menuIndex >= mMenuEntries.size();
	}

	public float getEntryWidth() {
		final int lEntryCount = mMenuEntries.size();
		if (lEntryCount == 0)
			return 0;

		float lResult = 0;
		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			float lTemp = lMenuEntry.marginLeft() + lMenuEntry.width() + lMenuEntry.marginRight();
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

		float lResult = paddingTop() + paddingBottom() + mEntryOffsetFromTop;

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = mMenuEntries.get(i);
			if (!lMenuEntry.affectsParentStructure())
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

	public void onViewportChange(float width, float height) {
		final int lLayoutCount = mMenuEntries.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mMenuEntries.get(i).onViewportChange(width, height);
		}
	}
}