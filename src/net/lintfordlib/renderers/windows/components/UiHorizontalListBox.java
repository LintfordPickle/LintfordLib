package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;

public class UiHorizontalListBox extends UIWidget implements IScrollBarArea {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<UiListBoxItem> mItems = new ArrayList<>();
	private int mSelectedItemIndex;
	private transient boolean mHasFocus;
	private ScrollBarContentRectangle mContentArea;
	private ScrollBarHorizontal mScrollbar;
	private float mAssetSeparation;
	private float mAssetSize;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float assetSize() {
		return mAssetSize;
	}

	public void assetSize(float newAssetSize) {
		mAssetSize = newAssetSize;
	}

	public float assetSeparation() {
		return mAssetSeparation;
	}

	public void assetSeparation(float newAsseteparation) {
		mAssetSeparation = newAsseteparation;
	}

	public List<UiListBoxItem> assets() {
		return mItems;
	}

	public int selectedAssetIndex() {
		return mSelectedItemIndex;
	}

	public UiListBoxItem getSelectedItem() {
		if (mSelectedItemIndex < 0 || mSelectedItemIndex >= mItems.size())
			return null;

		return mItems.get(mSelectedItemIndex);
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean v) {
		mHasFocus = v;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiHorizontalListBox(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow);

		mAssetSize = 32.f;
		mAssetSeparation = 16.f;

		mContentArea = new ScrollBarContentRectangle(this);
		mScrollbar = new ScrollBarHorizontal(this, mContentArea);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		mScrollbar.handleInput(core, null);

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().isMouseLeftButtonDownTimed(this) && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final var lMouseRelX = core.HUD().getMouseWorldSpaceX() - mX;
				mSelectedItemIndex = (int) (((lMouseRelX - mScrollbar.currentXPos()) - mAssetSeparation) / (mAssetSize + mAssetSeparation * 2.f));

				return true;
			}
		}

		return false;
	}

	public void update(LintfordCore core) {
		super.update(core);

		mScrollbar.update(core);

		final var lNumAssets = mItems.size();
		final var lContentWidth = lNumAssets * (mAssetSize + mAssetSeparation * 2.f) + mAssetSeparation * 2.f;

		mContentArea.set(mX, mY, lContentWidth, mH);
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		spriteBatch.begin(core.HUD());
		TextureBatch9Patch.drawBackground(core, spriteBatch, coreSpritesheetDefinition, 32, (int) mX, (int) mY, (int) mW, (int) mH, ColorConstants.WHITE, false, componentZDepth);
		spriteBatch.end();

		mContentArea.preDraw(core, spriteBatch);

		spriteBatch.begin(core.HUD());
		textFont.begin(core.HUD());
		float lAssetPositionX = mX + mScrollbar.currentXPos() + mAssetSeparation;

		final var lNumAssets = mItems.size();
		for (int i = 0; i < lNumAssets; i++) {
			final var lAssetItemToRender = mItems.get(i);
			final var lIsSelectedItem = mSelectedItemIndex == i;

			lAssetPositionX += mAssetSeparation;
			textFont.drawText(lAssetItemToRender.displayName, lAssetPositionX, mY + 32 + 20, componentZDepth, 0.5f);

			if (lIsSelectedItem) {
				// TODO: The rendering and clicking position needs a lot of work
				spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mX + mAssetSize + (i * (mAssetSize + mAssetSeparation)), mY, mAssetSize, mAssetSize, componentZDepth, entityColor);
			}

			lAssetPositionX += mAssetSize + mAssetSeparation;
		}

		spriteBatch.end();
		textFont.end();

		mContentArea.postDraw(core);

		mScrollbar.draw(core, spriteBatch, coreSpritesheetDefinition, componentZDepth);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addItem(UiListBoxItem newItem) {
		if (mItems.contains(newItem) == false) {
			mItems.add(newItem);
		}
	}

	// --------------------------------------
	// Inherited-Methods
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
