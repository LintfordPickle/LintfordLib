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
import net.lintfordlib.core.maths.MathHelper;
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
	protected boolean mCanHaveFocus;

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

	public boolean canHaveFocus() {

		boolean atLeastOneChildCanHaveFocus = false;
		final var numEntries = mMenuEntries.size();
		for (int i = 0; i < numEntries; i++) {
			final var entry = mMenuEntries.get(i);
			if (entry.enabled() && entry.canHaveFocus()) {
				atLeastOneChildCanHaveFocus = true;
				break;
			}
		}

		return atLeastOneChildCanHaveFocus && mCanHaveFocus;
	}

	public void canHaveFocus(boolean newValue) {
		mCanHaveFocus = newValue;
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
		mCanHaveFocus = true;

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

			final var menuEntry = mMenuEntries.get(i);

			// note: different input methods have precedence.

			var lInputHandled = false;
			lInputHandled = menuEntry.onHandleInputActions(core);
			lInputHandled = lInputHandled || menuEntry.onHandleKeyboardInput(core);
			lInputHandled = lInputHandled || menuEntry.onHandleGamepadInput(core);

			if (lInputHandled)
				return lInputHandled;
		}

		if (mScrollBar.scrollBarEnabled())
			mScrollBar.handleInput(core, screenManager);

		return false;
	}

	public void update(LintfordCore core) {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			final var menuEntry = mMenuEntries.get(i);
			menuEntry.update(core, parentScreen);
		}

		final var lScreenOffset = parentScreen.screenPositionOffset();
		mContentArea.set(lScreenOffset.x + mX, lScreenOffset.y + mY, mW, getEntryHeight());

		final float titleHeight = TITLE_BAR_HEIGHT;
		final float cropFooterHeight = mCropPaddingBottom;
		final float cropHeaderHeight = mShowTitle ? mCropPaddingTop + titleHeight : mCropPaddingTop;
		contentDisplayRectange.set(mX, mY + cropHeaderHeight, mW, mH - cropHeaderHeight - cropFooterHeight);

		mScrollBar.update(core);
	}

	public void draw(LintfordCore core, float componentDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var spriteBatch = parentScreen.spriteBatch();
		final var spriteSheetCore = core.resources().spriteSheetManager().coreSpritesheet();

		final var screenOffset = parentScreen.screenPositionOffset();

		if (mDrawBackground) {
			final int ts = 32;

			final var color = ColorConstants.getColorWithAlpha(layoutColor, layoutColor.a * parentScreen.screenColor.a);

			final int x = (int) (screenOffset.x + mX);
			final int y = (int) (screenOffset.y + mY);
			final int w = (int) mW;
			final int h = (int) mH;

			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(color);
			TextureBatch9Patch.drawBackground(spriteBatch, spriteSheetCore, ts, x, y, w, h, mShowTitle, componentDepth);
			spriteBatch.end();
		}

		if (mShowTitle) {
			final var titleFont = parentScreen.rendererManager().sharedResources().uiHeaderFont();

			titleFont.begin(core.HUD());
			titleFont.setTextColorRGBA(1.f, 1.f, 1.f, parentScreen.screenColor.a);
			titleFont.drawText(mLayoutTitle, screenOffset.x + mX + 20.f, mY + 20.f - (titleFont.fontHeight() / 2.f), componentDepth, 1.0f);
			titleFont.end();
		}

		final var layoutStencilRef = 0x02;

		// So I don't forget:
		// each baselayout has its own stencil buffer (in that, we clear the stencil buffer per layout).
		// we fill the stencil buffer with a high value, then render the bounds of the layout with a low value (0x01)
		// then, for each of the nested entries that need their own stencil, we clear the region using GEQUAL, and render contents using EQUAL

		mContentArea.stencilClear(0x0);
		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.preDraw(core, spriteBatch, contentDisplayRectange, layoutStencilRef, false);
		}

		final int lMenuEntryCount = mMenuEntries.size();
		for (int i = lMenuEntryCount - 1; i >= 0; --i) {

			if (mScrollBar.scrollBarEnabled())
				mContentArea.restoreRef(layoutStencilRef);

			final var entryZDepth = componentDepth - i * .01f;
			mMenuEntries.get(i).draw(core, parentScreen, entryZDepth);
		}

		if (mScrollBar.scrollBarEnabled()) {
			mContentArea.postDraw(core);
			spriteBatch.begin(core.HUD());
			mScrollBar.positionOffset.x = screenOffset.x;
			mScrollBar.positionOffset.y = screenOffset.y;

			mScrollBar.scrollBarAlpha(parentScreen.screenColor.a);

			mScrollBar.draw(core, spriteBatch, spriteSheetCore, componentDepth + .1f);
			spriteBatch.end();
		}

		// Because some entries 'overlap' outside of the layout (like a dropdown list near the bottom of a layout), we have a post stencil draw section.

		for (int i = lMenuEntryCount - 1; i >= 0; --i) {
			mMenuEntries.get(i).postStencilDraw(core, parentScreen, componentDepth);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(spriteSheetCore, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + mX, mY, mW, mH, ZLayers.LAYER_DEBUG);
			spriteBatch.end();
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
		// if (!mMenuEntries.contains(entryToAdd))
		mMenuEntries.add(entryToAdd);
	}

	public void addMenuEntry(MenuEntry entryToAdd, int pos) {
		if (!mMenuEntries.contains(entryToAdd))
			mMenuEntries.add(pos, entryToAdd);
	}

	public void removeMenuEntry(MenuEntry entryToRemove) {
		if (mMenuEntries.contains(entryToRemove))
			mMenuEntries.remove(entryToRemove);
	}

	public void updateStructure() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).updateStructure();
		}
	}

	public boolean hasEntry(int menuIndex) {
		var hasEntry = menuIndex < 0 || menuIndex >= mMenuEntries.size();
		return !hasEntry;
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

	public void scrollContentItemIntoView(int itemIndex) {

		if (contentDisplayRectange.height() > mContentArea.height()) {
			mScrollBar.AbsCurrentYPos(0);
			return; // no need to scroll, the content fits within the display area
		}

		if (itemIndex == 0) {
			mScrollBar.AbsCurrentYPos(0);
			return;
		}

		float itopPos = 0.f;
		for (int i = 1; i <= itemIndex - 1; i++) {
			final var menuEntry = mMenuEntries.get(i);
			itopPos += menuEntry.marginTop() + menuEntry.desiredHeight() + menuEntry.marginBottom();
		}

		final var topPosition = MathHelper.clamp(-itopPos, -(mContentArea.height() - contentDisplayRectange.height()), 0);
		mScrollBar.AbsCurrentYPos(topPosition);
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