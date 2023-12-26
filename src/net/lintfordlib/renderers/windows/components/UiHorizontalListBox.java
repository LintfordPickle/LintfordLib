package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
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

		mAssetSize = 64.f;
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
				final var lMouseRelX = core.HUD().getMouseWorldSpaceX() - mX - mScrollbar.currentXPos();

				final var lHalfSep = mAssetSeparation * .5f;
				final var assetWidth = mAssetSize + lHalfSep * 2;

				mSelectedItemIndex = (int) (lMouseRelX / assetWidth);

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

		final var lNumAssets = mItems.size();
		for (int i = 0; i < lNumAssets; i++) {
			final var lAssetItemToRender = mItems.get(i);
			final var lIsSelectedItem = mSelectedItemIndex == i;

			final var lHalfSep = mAssetSeparation * .5f;
			// work out the area of this asset
			final var assetX = mScrollbar.currentXPos() + mX + (mAssetSeparation + mAssetSize) * i;
			final var assetY = mY + 2;
			final var assetW = mAssetSize + lHalfSep * 2;
			final var assetH = mH - mScrollbar.height() - 10.f;

			if (lAssetItemToRender instanceof UiListBoxImageItem) {
				var lImageItem = (UiListBoxImageItem) lAssetItemToRender;

				if (lImageItem.iconContainer.isLoaded()) {
					final var spriteDef = lImageItem.iconContainer.spriteSheetDefinition;
					final var spriteInst = lImageItem.iconContainer.spriteInstance;
					final var spriteDestRect = lImageItem.iconContainer;

					final var lImageSize = 32.f;
					spriteDestRect.set(assetX + assetW * .5f - lImageSize * .5f, mY + 5.f, lImageSize, lImageSize);

					spriteBatch.draw(spriteDef, spriteInst, spriteDestRect, componentZDepth, entityColor);
				}

				if (lIsSelectedItem)
					Debug.debugManager().drawers().drawRectImmediate(core.HUD(), assetX, assetY, assetW, assetH, 1.f, 0.f, 0.f);

			}

			if (lAssetItemToRender.displayName != null) {
				final var lTextToRender = lAssetItemToRender.displayName.substring(0, Math.min(6, lAssetItemToRender.displayName.length()));
				final var lTextWidth = textFont.getStringWidth(lTextToRender);

				textFont.drawText(lTextToRender, assetX + assetW * .5f - lTextWidth * .5f, mY + 32 + 20, componentZDepth, 1.f);
			}
		}

		spriteBatch.end();
		textFont.end();

		mContentArea.postDraw(core);

		// DEBUG
		Debug.debugManager().drawers().drawRectImmediate(core.HUD(), this, 1.f, 0.f, 0.f);

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
