package net.lintfordlib.renderers.windows;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.UIWidget;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;

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

	public final Color layoutColor = new Color(ColorConstants.WHITE());

	protected List<UIWidget> mUiWidgets;
	protected int mSelectedEntry = 0;
	protected int mNumberEntries;

	protected boolean mDrawBackground;

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

	public void scrollBarsEnabled(boolean newValue) {
		mScrollBarsEnabled = newValue;
	}

	public void maxWidth(float newValue) {
		mMaxWidth = newValue;
	}

	public float maxWidth() {
		return mMaxWidth;
	}

	public void setEntryOffsetY(float newValue) {
		mEntryOffsetFromTop = newValue;
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

	public void layoutWidth(LAYOUT_WIDTH playoutWidth) {
		if (playoutWidth == null)
			return;

		mLayoutWidth = playoutWidth;
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

	public UiBaseLayoutComponent(UiWindow parentWindow) {
		super(parentWindow);

		mUiWidgets = new ArrayList<>();

		mEnabled = true;
		mVisible = true;

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

	@Override
	public void initialize() {
		int lCount = mUiWidgets.size();
		for (int i = 0; i < lCount; i++) {
			mUiWidgets.get(i).initialize();
		}

		mH = getDesiredHeight();

	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		final int lWidgetCount = mUiWidgets.size();
		for (int i = 0; i < lWidgetCount; i++) {
			mUiWidgets.get(i).loadResources(resourceManager);
		}

		mResourcesLoaded = true;
	}

	@Override
	public void unloadResources() {
		final int lWidgetCount = mUiWidgets.size();
		for (int i = 0; i < lWidgetCount; i++) {
			mUiWidgets.get(i).unloadResources();
		}

		mResourcesLoaded = false;
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (widgets() == null || widgets().isEmpty())
			return false; // nothing to do

		final var lEntryCount = widgets().size();
		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = widgets().get(i);

			lMenuEntry.handleInput(core);

			// Chance here to return true if child entry handled the input ...
		}

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				mZScrollAcceleration += core.input().mouse().mouseWheelYOffset() * 250.0f;
			}
		}

		if (mScrollBarsEnabled_Internal) {
			mScrollBar.handleInput(core, mParentWindow.rendererManager());
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		final int lCount = mUiWidgets.size();
		for (int i = 0; i < lCount; i++) {
			mUiWidgets.get(i).update(core);
		}

		if (!mScrollBarsEnabled_Internal)
			mYScrollPosition = 0;

		final float lPaddingHorizontal = 5.f;
		final float lWidthOffset = mScrollBarsEnabled_Internal ? 25.f : lPaddingHorizontal;

		float xPos = mX + lPaddingHorizontal;
		float yPos = mY + mYScrollPosition;

		// Order the child widgets (that's what the bseLayout is for)
		for (int i = 0; i < lCount; i++) {
			final var lWidget = mUiWidgets.get(i);

			if (!lWidget.isVisible())
				continue;

			lWidget.x(xPos);
			lWidget.y((int) yPos);
			lWidget.setPosition(xPos, yPos);
			lWidget.width(mW - lWidthOffset);

			yPos += lWidget.height();
		}

		mContentArea.set(mX, mY, mW, getEntryHeight());

		mScrollBarsEnabled_Internal = mScrollBarsEnabled && mContentArea.height() - mH > 0;
		if (mScrollBarsEnabled_Internal) {
			final float lDeltaTime = (float) core.appTime().elapsedTimeMilli() / 1000f;
			float lScrollSpeedFactor = mYScrollPosition;

			mZScrollVelocity += mZScrollAcceleration;
			lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
			mZScrollVelocity *= 0.85f;
			mZScrollAcceleration = 0.0f;
			mYScrollPosition = lScrollSpeedFactor;

			// Constrain
			if (mYScrollPosition > 0)
				mYScrollPosition = 0;
			if (mYScrollPosition < -(mContentArea.height() - this.mH)) {
				mYScrollPosition = -(mContentArea.height() - this.mH);
			}

			mScrollBar.update(core);
		}
	}

	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheet, FontUnit textFont, float componentZDepth) {
		if (!mEnabled || !mVisible)
			return;

		final var lFontUnit = mParentWindow.rendererManager().uiTextFont();

		if (mDrawBackground) {
			if (mH < 64) {
				spriteBatch.begin(core.HUD());
				spriteBatch.setColorRGBA(0.1f, 0.1f, 0.1f, .8f);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, componentZDepth);
				spriteBatch.end();
			} else {
				final float TILE_SIZE = 32;

				spriteBatch.begin(core.HUD());
				spriteBatch.setColor(layoutColor);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX, mY, TILE_SIZE, TILE_SIZE, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + TILE_SIZE, mY, mW - TILE_SIZE * 2, TILE_SIZE, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + mW - TILE_SIZE, mY, TILE_SIZE, TILE_SIZE, componentZDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX, mY + TILE_SIZE, TILE_SIZE, mH - TILE_SIZE * 2, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + TILE_SIZE, mY + TILE_SIZE, mW - TILE_SIZE * 2, mH - 64, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + mW - TILE_SIZE, mY + TILE_SIZE, TILE_SIZE, mH - TILE_SIZE * 2, componentZDepth);

				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX, mY + mH - TILE_SIZE, TILE_SIZE, TILE_SIZE, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + TILE_SIZE, mY + mH - TILE_SIZE, mW - TILE_SIZE * 2, TILE_SIZE, componentZDepth);
				spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, mX + mW - TILE_SIZE, mY + mH - TILE_SIZE, TILE_SIZE, TILE_SIZE, componentZDepth);
				spriteBatch.end();
			}
		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.depthPadding(6f);
			mContentArea.preDraw(core, spriteBatch);
		}

		final int lCount = widgets().size();
		for (int i = lCount - 1; i >= 0; --i) {
			final var lWidget = widgets().get(i);

			if (!lWidget.isVisible())
				continue;

			lWidget.draw(core, spriteBatch, coreSpritesheet, lFontUnit, componentZDepth + .1f);
		}

		if (mScrollBarsEnabled_Internal) {
			mContentArea.postDraw(core);
			mScrollBar.scrollBarAlpha(.8f);
			mScrollBar.draw(core, spriteBatch, coreSpritesheet, componentZDepth + .1f);
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(coreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mX, mY, mW, mH, ZLayers.LAYER_DEBUG);
			spriteBatch.end();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean hasEntry(int index) {
		if (index < 0 || index >= widgets().size())
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
			float lTemp = widgets().get(i).width();
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

		float lResult = marginTop();

		for (int i = 0; i < lEntryCount; i++) {
			final var lMenuEntry = widgets().get(i);
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
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentArea;
	}
}
