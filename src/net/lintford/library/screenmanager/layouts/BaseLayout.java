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

	public void maxHeight(float maxHeight) {
		mMaxHeight = maxHeight;
	}

	public float maxHeight() {
		return mMaxHeight;
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

	public BaseLayout(MenuScreen pParentScreen) {
		super();

		screenManager = pParentScreen.screenManager();
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
		if (mMenuEntries == null || mMenuEntries.size() == 0)
			return false; // nothing to do

		// limit mouse interaction within the baseLayout to within the contentDisplayArea
		// due to the constraints imposed by the title bar, via the crop top and crop bottom, the contentDisplayArea is a subset of the layout
		if (core.input().mouse().isMouseMenuSelectionEnabled() && contentDisplayArea().intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (true && core.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				final float scrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}

			final int lCount = mMenuEntries.size();
			for (int i = 0; i < lCount; i++) {
				var lInputHandled = false;
				lInputHandled = mMenuEntries.get(i).onHandleMouseInput(core);

				if (lInputHandled)
					return lInputHandled;
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
		if (lFocusedEntry != null && mScrollBar.scrollBarEnabled() && lMouseMenuControls == false) {
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

		if (mDrawBackground)
			drawBackground(core, mShowTitle, componentDepth);

		if (mShowTitle) {
			final var lTitleFont = parentScreen.rendererManager().uiHeaderFont();
			final var lWhiteColorWithAlpha = ColorConstants.getWhiteWithAlpha(parentScreen.screenColor.a);

			lTitleFont.begin(core.HUD());
			lTitleFont.drawText(mLayoutTitle, mX + 20.f, mY + 20.f - (lTitleFont.fontHeight() / 2.f), componentDepth, lWhiteColorWithAlpha, 1.0f);
			lTitleFont.end();
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.preDraw(core, lSpriteBatch, lSpriteSheetCore);
		}

		final int lMenuEntryCount = mMenuEntries.size();
		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).draw(core, parentScreen, componentDepth + i * .001f);
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.postDraw(core);
			lSpriteBatch.begin(core.HUD());
			mScrollBar.scrollBarAlpha(parentScreen.screenColor.a);
			mScrollBar.draw(core, lSpriteBatch, lSpriteSheetCore, componentDepth + .1f, parentScreen.screenColor.a);
			lSpriteBatch.end();
		}

		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).postStencilDraw(core, parentScreen, componentDepth + i * .001f);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	// TODO: Move this into a 'CorePanelRenderer'
	private void drawBackground(LintfordCore core, boolean withTitlebar, float componentDepth) {
		final var lSpriteBatch = parentScreen.spriteBatch();
		final var lSpriteSheetCore = core.resources().spriteSheetManager().coreSpritesheet();

		final var lScreenOffset = parentScreen.screenPositionOffset();
		final int ts = 32;

		final var lColor = ColorConstants.getColorWithAlpha(layoutColor, layoutColor.a * parentScreen.screenColor.a);

		final int x = (int) Math.floor(mX);
		final int y = (int) Math.floor(mY);
		final int w = (int) Math.floor(mW);
		final int h = (int) Math.floor(mH);

		// @formatter:off
		lSpriteBatch.begin(core.HUD());
		if (withTitlebar) {
			if (mH < 64) {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     (int) (lScreenOffset.x + x),            (int) (lScreenOffset.y + y), ts+1, ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      (int) (lScreenOffset.x + x + ts),       (int) (lScreenOffset.y + y), w - (ts-1) * 2, ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    (int) (lScreenOffset.x + x + w - ts),   (int) (lScreenOffset.y + y), ts+1, ts+1, componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  (int) (lScreenOffset.x + x),            (int) (lScreenOffset.y + y + h - ts), ts, ts, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   (int) (lScreenOffset.x + x + ts),       (int) (lScreenOffset.y + y + h - ts), w - (ts-1) * 2, ts, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - ts),   (int) (lScreenOffset.y + y + h - ts), ts, ts, componentDepth, lColor);
			} else {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT,     (int) (lScreenOffset.x + x),            (int) (lScreenOffset.y + y), ts + 1, ts + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID,      (int) (lScreenOffset.x + x + ts),       (int) (lScreenOffset.y + y), w - (ts - 1) * 2, ts + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT,    (int) (lScreenOffset.x + x + w - ts),   (int) (lScreenOffset.y + y), ts + 1, ts + 1, componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT,     (int) (lScreenOffset.x + x),            (int) (lScreenOffset.y + y + ts), ts + 1, h - ts * 2, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER,   (int) (lScreenOffset.x + x + ts),       (int) (lScreenOffset.y + y + ts), (int) (w - (ts-1) * 2), h - 64, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT,    (int) (lScreenOffset.x + x + w - ts),   (int) (lScreenOffset.y + y + ts), (int) ts, (int) (h - ts * 2), componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT,  (int) (lScreenOffset.x + x),            (int) (lScreenOffset.y + y + h - ts), ts + 1, ts + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID,   (int) (lScreenOffset.x + x + ts),       (int) (lScreenOffset.y + y + h - ts), (int) (w - (ts-1) * 2), ts, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, (int) (lScreenOffset.x + x + w - ts),   (int) (lScreenOffset.y + y + h - ts), ts + 1, ts + 1, componentDepth, lColor);
			}
		} else {
			if (mH < 64) {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     (int) (lScreenOffset.x + mX),           (int) (lScreenOffset.y + mY), ts+1, ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      (int) (lScreenOffset.x + mX + ts),      (int) (lScreenOffset.y + mY), mW - (ts-1) * 2, ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    (int) (lScreenOffset.x + mX + mW - ts), (int) (lScreenOffset.y + mY), ts+1, ts+1, componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  (int) (lScreenOffset.x + mX),           (int) (lScreenOffset.y + mY + mH - ts), ts+1, ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   (int) (lScreenOffset.x + mX + ts),      (int) (lScreenOffset.y + mY + mH - ts), mW - (ts-1) * 2, ts, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, (int) (lScreenOffset.x + mX + mW - ts), (int) (lScreenOffset.y + mY + mH - ts), ts+1, ts+1, componentDepth, lColor);
			} else {
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_LEFT,     (int) (lScreenOffset.x + mX),           (int) (lScreenOffset.y + mY), ts + 1, ts + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_MID,      (int) (lScreenOffset.x + mX + ts),      (int) (lScreenOffset.y + mY), mW - (ts - 1) * 2, ts + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_TOP_RIGHT,    (int) (lScreenOffset.x + mX + mW - ts), (int) (lScreenOffset.y + mY), ts + 1, ts + 1, componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_LEFT,     (int) (lScreenOffset.x + mX),           (int) (lScreenOffset.y + mY + ts), ts + 1, mH - ts * 2 + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_CENTER,   (int) (lScreenOffset.x + mX + ts),      (int) (lScreenOffset.y + mY + ts), (mW - (ts-1) * 2), mH - 64 + 1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_MID_RIGHT,    (int) (lScreenOffset.x + mX + mW - ts), (int) (lScreenOffset.y + mY + ts), ts + 1,(mH - ts * 2) + 1, componentDepth, lColor);

				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_LEFT,  (int) (lScreenOffset.x + mX),           (int) (lScreenOffset.y + mY + mH - ts - 1), ts + 1, ts+1, componentDepth, layoutColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_MID,   (int) (lScreenOffset.x + mX + ts),      (int) (lScreenOffset.y + mY + mH - ts - 1), (mW - (ts - 1) * 2), ts+1, componentDepth, lColor);
				lSpriteBatch.draw(lSpriteSheetCore, CoreTextureNames.TEXTURE_PANEL_3X3_01_BOTTOM_RIGHT, (int) (lScreenOffset.x + mX + mW - ts), (int) (lScreenOffset.y + mY + mH - ts - 1), ts + 1, ts+1, componentDepth, lColor);
			}
		}
		lSpriteBatch.end();
		// @formatter:on

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_OUTLINES", false)) {
			Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this);
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
		if (menuIndex < 0 || menuIndex >= mMenuEntries.size())
			return false;

		return true;
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