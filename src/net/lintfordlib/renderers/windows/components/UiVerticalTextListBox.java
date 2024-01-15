package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.interfaces.IUiListBoxListener;

public class UiVerticalTextListBox extends UIWidget implements IScrollBarArea {

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
	private transient ScrollBarContentRectangle mWindowRectangle;
	private ScrollBar mScrollbar;
	private float mVerticalAssetSeparationInPx;
	private float mAssetHeightInpx;
	private IUiListBoxListener mCallbackListener;

	private float mMinHeight = 50;
	private float mMaxHeight = 200;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public void desiredHeight(float desiredHeight) {
		mDesiredHeight = MathHelper.clamp(desiredHeight, mMinHeight, mMaxHeight);
	}

	public void setHeightMinMax(float min, float max) {
		if (min <= 25) // actual minimum
			min = 25;

		if (max < min) {
			max = min;
		}

		mMinHeight = min;
		mMaxHeight = max;
	}

	public float assetSize() {
		return mAssetHeightInpx;
	}

	public void assetSize(float newAssetSize) {
		mAssetHeightInpx = newAssetSize;
	}

	public float assetSeparation() {
		return mVerticalAssetSeparationInPx;
	}

	public void assetSeparation(float newAsseteparation) {
		mVerticalAssetSeparationInPx = newAsseteparation;
	}

	public List<UiListBoxItem> items() {
		return mItems;
	}

	public void selectedItemIndex(int newIndex) {
		if (newIndex < 0 || newIndex >= mItems.size())
			return;

		mSelectedItemIndex = newIndex;
	}

	public int selectedItemIndex() {
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

	public UiListBoxItem getItemByUid(int itemUid) {
		final var lNumitems = mItems.size();
		for (int i = 0; i < lNumitems; i++) {
			if (mItems.get(i).itemUid == itemUid)
				return mItems.get(i);
		}

		return null;
	}

	public UiListBoxItem getItemByIndex(int index) {
		if (index < 0 || index != mItems.size())
			return null;

		return mItems.get(index);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiVerticalTextListBox(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow);

		mAssetHeightInpx = 25.f;
		mVerticalAssetSeparationInPx = 2.f;

		mContentArea = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(parentWindow);

		mScrollbar = new ScrollBar(this, mContentArea);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public boolean handleInput(LintfordCore core) {
		mScrollbar.handleInput(core, null);

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			boolean itemSelected = false;
			final var lNumitems = mItems.size();
			for (int i = 0; i < lNumitems; i++) {
				itemSelected |= mItems.get(i).handleInput(core);
			}

			// handle item selected separate from item input handling?
			if (itemSelected || core.input().mouse().isMouseLeftButtonDownTimed(this) && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final var lMouseRelY = core.HUD().getMouseWorldSpaceY() - mY;
				mSelectedItemIndex = (int) (((lMouseRelY - mScrollbar.currentYPos())) / (mAssetHeightInpx + mVerticalAssetSeparationInPx));

				var lSelectedItem = (UiListBoxItem) null;
				if (mSelectedItemIndex >= 0 && mSelectedItemIndex < mItems.size())
					lSelectedItem = mItems.get(mSelectedItemIndex);

				if (mUiWidgetListenerCallback != null)
					mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

				if (lSelectedItem != null && mCallbackListener != null)
					mCallbackListener.onItemSelected(lSelectedItem);

				return true;
			}
		}

		return false;
	}

	public void update(LintfordCore core) {
		super.update(core);

		mScrollbar.update(core);

		final var lNumAssets = mItems.size();
		final var lContentHeight = lNumAssets * (mAssetHeightInpx + mVerticalAssetSeparationInPx) + mVerticalAssetSeparationInPx;

		if (mDesiredHeight != 0) {
			mH = mDesiredHeight;
		} else {
			mH = MathHelper.clamp(lContentHeight, mMinHeight, mMaxHeight);
		}

		mDesiredHeight = mH;
		mContentArea.set(mX, mY, mW, Math.max(mH, lContentHeight));
		mWindowRectangle.set(mX, mY, mW, mH);
		mScrollbar.update(core);

		// Update the positions of the individual items

		float lAssetPositionX = mX + mVerticalAssetSeparationInPx;
		float lAssetPositionY = mY + mScrollbar.currentYPos();

		final var lNumitems = mItems.size();
		for (int i = 0; i < lNumitems; i++) {
			final var lItemToRender = mItems.get(i);
			lItemToRender.set(lAssetPositionX, lAssetPositionY, mW, mAssetHeightInpx);
			lAssetPositionY += mAssetHeightInpx + mVerticalAssetSeparationInPx;

			if (i == mSelectedItemIndex) {
				final var lSelectedBackgroundColor = ColorConstants.getWhiteWithAlpha(0.4f);
				lItemToRender.backgroundColor.setFromColor(lSelectedBackgroundColor);
			} else if (i % 2 == 0) {
				lItemToRender.backgroundColor.setRGBA(1.0f, 0.81f, 0.75f, 0.3f);
			} else {
				lItemToRender.backgroundColor.setRGBA(1.f, 1.f, 1.f, 0.f);
			}

			lItemToRender.update(core);
		}
	}

	@Override
	public void draw(LintfordCore core, SpriteBatch spriteBatch, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		spriteBatch.begin(core.HUD());
		TextureBatch9Patch.drawBackground(core, spriteBatch, coreSpritesheetDefinition, 32, (int) mX, (int) mY, (int) mW, (int) mH, ColorConstants.WHITE, false, componentZDepth);
		spriteBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());

			spriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth, ColorConstants.Debug_Transparent_Magenta);

			spriteBatch.end();
		}

		mWindowRectangle.preDraw(core, spriteBatch, mWindowRectangle, 1);

		spriteBatch.begin(core.HUD());
		textFont.begin(core.HUD());

		final var lNumitems = mItems.size();
		for (int i = 0; i < lNumitems; i++) {
			final var lItemToRender = mItems.get(i);
			lItemToRender.draw(core, spriteBatch, coreSpritesheetDefinition, textFont, componentZDepth + 0.01f);
		}

		spriteBatch.end();
		textFont.end();

		mWindowRectangle.postDraw(core);

		mScrollbar.draw(core, spriteBatch, coreSpritesheetDefinition, componentZDepth, 0.8f);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addCallbackListener(IUiListBoxListener listener) {
		mCallbackListener = listener;
	}

	public void removeCallbackListener() {
		mCallbackListener = null;
	}

	public void addItem(UiListBoxItem newItem) {
		if (mItems.contains(newItem) == false) {
			mItems.add(newItem);

			if (mCallbackListener != null) {
				mCallbackListener.onItemAdded(newItem);
			}
		}
	}

	public void removeItem(UiListBoxItem oldItem) {
		if (mItems.contains(oldItem)) {
			mItems.remove(oldItem);

			if (mCallbackListener != null) {
				mCallbackListener.onItemRemoved(oldItem);
			}
		}
	}

	public void removeItemByUid(int itemUid) {
		final var lItem = getItemByUid(itemUid);
		if (lItem == null)
			return;

		if (mItems.contains(lItem)) {
			mItems.remove(lItem);

			if (mCallbackListener != null) {
				mCallbackListener.onItemRemoved(lItem);
			}
		}

		if (mSelectedItemIndex >= mItems.size())
			mSelectedItemIndex = -1;
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
