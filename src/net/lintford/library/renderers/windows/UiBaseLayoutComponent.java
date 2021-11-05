package net.lintford.library.renderers.windows;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.renderers.windows.components.UIWidget;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;

/**
 * The dimensions of the BaseLayout are set by the parent screen.
 */
public class UiBaseLayoutComponent extends UIWidget implements IScrollBarArea {

	private static final long serialVersionUID = 5742176250891551930L;

	public static final float USE_HEIGHT_OF_ENTRIES = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected LAYOUT_WIDTH mLayoutWidth = LAYOUT_WIDTH.THREEQUARTER;
	protected FILLTYPE mLayoutFillType = FILLTYPE.FILL_CONTAINER;

	public final Color layoutColor = new Color(ColorConstants.WHITE);

	protected List<UIWidget> mUiWidgets;
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

	private boolean mResourcesLoaded;

	protected ScrollBarContentRectangle mContentArea;
	protected ScrollBar mScrollBar;
	protected float mYScrollPosition;
	protected float mZScrollAcceleration;
	protected float mZScrollVelocity;

	protected boolean mScrollBarsEnabled;
	protected boolean mScrollBarsEnabled_Internal;
	protected boolean mEnabled;
	protected boolean mVisible; // ony affects drawing

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

	/** @returns A list of menu entries so derived classes can change the menu contents. */
	public List<UIWidget> widgets() {
		return mUiWidgets;
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

	public UiBaseLayoutComponent(UiWindow pParentWindow) {
		super(pParentWindow);

		mUiWidgets = new ArrayList<>();

		mEnabled = true;
		mVisible = true;

		mTopMargin = 0f;
		mBottomMargin = 0f;
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
		int lCount = mUiWidgets.size();
		for (int i = 0; i < lCount; i++) {
			mUiWidgets.get(i).initialize();
		}

		// width = getEntryWidth();
		h = getDesiredHeight();

	}

	public void loadResources(ResourceManager pResourceManager) {
		final int lWidgetCount = mUiWidgets.size();
		for (int i = 0; i < lWidgetCount; i++) {
			mUiWidgets.get(i).loadResources(pResourceManager);
		}

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		final int lWidgetCount = mUiWidgets.size();
		for (int i = 0; i < lWidgetCount; i++) {
			mUiWidgets.get(i).unloadResources();
		}

		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore pCore) {
		if (widgets() == null || widgets().size() == 0)
			return false; // nothing to do

		final var lEntryCount = widgets().size();
		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = widgets().get(i);
			if (lMenuEntry.handleInput(pCore)) {
				// return true;
			}

		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			if (true && pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

		}

		if (mScrollBarsEnabled_Internal) {
			mScrollBar.handleInput(pCore);

		}

		return false;
	}

	public void update(LintfordCore pCore) {
		final int lCount = mUiWidgets.size();
		for (int i = 0; i < lCount; i++) {
			mUiWidgets.get(i).update(pCore);

		}

		if (!mScrollBarsEnabled_Internal)
			mYScrollPosition = 0;

		final float lPaddingHorizontal = 5.f;
		final float lWidthOffset = mScrollBarsEnabled_Internal ? 25.f : lPaddingHorizontal;

		float xPos = x + lPaddingHorizontal;
		float yPos = y + mYScrollPosition;

		// Order the child widgets (that's what the bseLayout is for)
		for (int i = 0; i < lCount; i++) {
			final var lWidget = mUiWidgets.get(i);

			if (!lWidget.isVisible())
				continue;

			lWidget.x(xPos);
			lWidget.y((int) yPos);
			lWidget.setPosition(xPos, yPos);
			lWidget.w(w - lWidthOffset);

			yPos += lWidget.h();

		}

		mContentArea.set(x, y, w, getEntryHeight());

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

	public void draw(LintfordCore pCore, SpriteBatch pSpriteBatch, SpriteSheetDefinition pCoreSpritesheet, FontUnit pTextFont, float pComponentZDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var lFontUnit = mParentWindow.rendererManager().uiTextFont();

		if (mDrawBackground) {
			if (h < 64) {
				pSpriteBatch.begin(pCore.HUD());
				final float lAlpha = 0.8f;
				final var lColor = ColorConstants.getColor(0.1f, 0.1f, 0.1f, lAlpha);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, pComponentZDepth, lColor);
				pSpriteBatch.end();

			} else {
				final float TILE_SIZE = 32;

				pSpriteBatch.begin(pCore.HUD());
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y, TILE_SIZE, TILE_SIZE, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + TILE_SIZE, y, w - TILE_SIZE * 2, TILE_SIZE, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + w - TILE_SIZE, y, TILE_SIZE, TILE_SIZE, pComponentZDepth, layoutColor);

				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y + TILE_SIZE, TILE_SIZE, h - TILE_SIZE * 2, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + TILE_SIZE, y + TILE_SIZE, w - TILE_SIZE * 2, h - 64, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + w - TILE_SIZE, y + TILE_SIZE, TILE_SIZE, h - TILE_SIZE * 2, pComponentZDepth, layoutColor);

				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y + h - TILE_SIZE, TILE_SIZE, TILE_SIZE, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + TILE_SIZE, y + h - TILE_SIZE, w - TILE_SIZE * 2, TILE_SIZE, pComponentZDepth, layoutColor);
				pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x + w - TILE_SIZE, y + h - TILE_SIZE, TILE_SIZE, TILE_SIZE, pComponentZDepth, layoutColor);
				pSpriteBatch.end();

				if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_OUTLINES", false)) {
					Debug.debugManager().drawers().drawRectImmediate(pCore.HUD(), this);
				}
			}
		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.depthPadding(6f);
			mContentArea.preDraw(pCore, pSpriteBatch, pCoreSpritesheet);
		}

		final int lCount = widgets().size();
		for (int i = lCount - 1; i >= 0; --i) {
			final var lWidget = widgets().get(i);

			if (!lWidget.isVisible())
				continue;

			lWidget.draw(pCore, pSpriteBatch, pCoreSpritesheet, lFontUnit, pComponentZDepth + .1f);

		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.postDraw(pCore);
			mScrollBar.draw(pCore, pSpriteBatch, pCoreSpritesheet, pComponentZDepth + .1f);

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			pSpriteBatch.begin(pCore.HUD());
			pSpriteBatch.draw(pCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, x, y, w, h, ZLayers.LAYER_DEBUG, ColorConstants.Debug_Transparent_Magenta);
			pSpriteBatch.end();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean hasEntry(int pIndex) {
		if (pIndex < 0 || pIndex >= widgets().size())
			return false;

		return true;
	}

	public float getEntryWidth() {
		final int lEntryCount = widgets().size();
		if (lEntryCount == 0)
			return 0;

		// return the widest entry
		float lResult = 0;
		for (int i = 0; i < lEntryCount; i++) {
			float lTemp = widgets().get(i).w();
			if (lTemp > lResult) {
				lResult = lTemp;
			}
		}

		return lResult;

	}

	public float getEntryHeight() {
		if (mForcedEntryHeight != USE_HEIGHT_OF_ENTRIES && mForcedEntryHeight >= 0)
			return mForcedEntryHeight;

		final int lEntryCount = widgets().size();
		if (lEntryCount == 0)
			return 0;

		// Return the combined height
		float lResult = marginTop();

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = widgets().get(i);
			lResult += lMenuEntry.h();

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

}
