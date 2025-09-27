package net.lintfordlib.renderers.windows.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.TextureBatch9Patch;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.interfaces.IUiListBoxListener;
import net.lintfordlib.renderers.windows.components.interfaces.IUiWidgetInteractions;

public class UiVerticalTextListBox extends UIWidget implements IScrollBarArea, IUiWidgetInteractions {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	private static final int ORDER_BUTTON_UP = 1;
	private static final int ORDER_BUTTON_DOWN = 2;

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

	private UiButtonImage mUpButton;
	private UiButtonImage mDownButton;

	private float mMinHeight = 50;
	private float mMaxHeight = 200;

	private boolean mShowReorderButtons;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean showReorderButtons() {
		return mShowReorderButtons;
	}

	public void showReorderButtons(boolean newValue) {
		mShowReorderButtons = newValue;
	}

	@Override
	public void desiredHeight(float desiredHeight) {
		if (desiredHeight <= 0) {
			mDesiredHeight = 0;
			return;
		}

		// otherwise, cap to between min and max
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

	/** Returns an unmodifiableList with the mItems as backing. */
	public List<UiListBoxItem> items() {
		return Collections.unmodifiableList(mItems);
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
		if (index < 0 || index >= mItems.size())
			return null;

		return mItems.get(index);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiVerticalTextListBox(int entityGroupUid) {
		mMinHeight = Float.MIN_VALUE;
		mMaxHeight = Float.MAX_VALUE;

		mAssetHeightInpx = 25.f;
		mVerticalAssetSeparationInPx = 2.f;

		mContentArea = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(this);

		mScrollbar = new ScrollBar(this, mContentArea);

		mUpButton = new UiButtonImage();
		mUpButton.buttonLabel("");
		mUpButton.setDimensions(20, 20);
		mUpButton.setUiWidgetListener(this, ORDER_BUTTON_UP);
		mUpButton.spriteName("TEXTURE_CONTROL_UP");

		mDownButton = new UiButtonImage();
		mDownButton.buttonLabel("");
		mDownButton.setDimensions(20, 20);
		mDownButton.setUiWidgetListener(this, ORDER_BUTTON_DOWN);
		mDownButton.spriteName("TEXTURE_CONTROL_DOWN");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		mScrollbar.handleInput(core, null);

		if (intersectsAA(core.HUD().getMouseCameraSpace())) {

			if (mShowReorderButtons) {
				if (mUpButton.handleInput(core))
					return true;

				if (mDownButton.handleInput(core))
					return true;

			}

			boolean itemSelected = false;
			final var lNumitems = mItems.size();
			for (int i = 0; i < lNumitems; i++) {
				itemSelected |= mItems.get(i).handleInput(core);
			}

			// handle item selected separate from item input handling?
			if (itemSelected || core.input().mouse().isMouseLeftButtonDownTimed(this) && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
				final var lMouseRelY = core.HUD().getMouseWorldSpaceY() - mY;
				mSelectedItemIndex = (int) ((lMouseRelY - mScrollbar.currentYPos()) / (mAssetHeightInpx + mVerticalAssetSeparationInPx));

				UiListBoxItem lSelectedItem = null;
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

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lNumAssets = mItems.size();
		final var lContentHeight = lNumAssets * (mAssetHeightInpx + mVerticalAssetSeparationInPx) + mVerticalAssetSeparationInPx;

		// TODO: The UiVerticalListBox height/desired needs more work.,
		// One issue is the rednering of the background below, we forces a minimum height of tileSize (which is 32!), can so the display overlaps the mPanelArea.

		if (mDesiredHeight != 0) {
			mH = mDesiredHeight;
		} else {
			mH = MathHelper.clamp(lContentHeight, mMinHeight, mMaxHeight);
		}

		// @formatter:off
		  mUpButton.setPosition(mX + mW - 32, mY + mH - 48);
		mDownButton.setPosition(mX + mW - 32, mY + mH - 25);
		// @formatter:on

		mContentArea.set(mX, mY, mW, Math.max(mH, lContentHeight));
		mWindowRectangle.set(mX, mY, mW, mH);
		mScrollbar.update(core);

		if (mShowReorderButtons) {
			mUpButton.update(core);
			mDownButton.update(core);

		}

		// Update the positions of the individual items

		float lAssetPositionX = mX + mVerticalAssetSeparationInPx;
		float lAssetPositionY = mY + mScrollbar.currentYPos();

		final var lNumitems = mItems.size();
		for (int i = 0; i < lNumitems; i++) {
			final var lItemToRender = mItems.get(i);
			lItemToRender.set(lAssetPositionX, lAssetPositionY, mW, mAssetHeightInpx);
			lAssetPositionY += mAssetHeightInpx + mVerticalAssetSeparationInPx;

			if (i == mSelectedItemIndex) {
				final var lSelectedBackgroundColor = ColorConstants.getColor(255.f / 255.f, 83.f / 255.f, 15.f / 255.f, 0.4f);
				lItemToRender.backgroundColor.setFromColor(lSelectedBackgroundColor);
			} else if (i % 2 == 0) {
				lItemToRender.backgroundColor.setRGBA(1.0f, 0.81f, 0.75f, 0.1f);
			} else {
				lItemToRender.backgroundColor.setRGBA(1.f, 1.f, 1.f, 0.f);
			}

			lItemToRender.update(core);
		}
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		if (mH > 32.f) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorWhite();
			TextureBatch9Patch.drawBackground(lSpriteBatch, coreSpritesheetDefinition, 32, mX, mY, mW, mH, false, componentZDepth);
			lSpriteBatch.end();
		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, componentZDepth);
			lSpriteBatch.end();
		}

		mWindowRectangle.preDraw(core, lSpriteBatch, mWindowRectangle, 2);

		lSpriteBatch.begin(core.HUD());
		textFont.begin(core.HUD());

		final var lNumitems = mItems.size();
		for (int i = 0; i < lNumitems; i++) {
			final var lItemToRender = mItems.get(i);
			lItemToRender.draw(core, lSpriteBatch, coreSpritesheetDefinition, textFont, componentZDepth + 0.01f);
		}

		lSpriteBatch.end();
		textFont.end();

		mWindowRectangle.postDraw(core);

		if (mScrollbar.scrollBarEnabled()) {
			mScrollbar.scrollBarAlpha(.8f);
			lSpriteBatch.begin(core.HUD());
			mScrollbar.draw(core, lSpriteBatch, coreSpritesheetDefinition, componentZDepth - 0.01f);
			lSpriteBatch.end();
		}

		if (mShowReorderButtons) {
			mUpButton.draw(core, sharedResources, coreSpritesheetDefinition, textFont, componentZDepth);
			mDownButton.draw(core, sharedResources, coreSpritesheetDefinition, textFont, componentZDepth);

		}

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
		if (!mItems.contains(newItem)) {
			mItems.add(newItem);

			if (mCallbackListener != null) {
				mCallbackListener.onItemAdded(newItem);
			}
		}
	}

	public void clearItems() {
		if (mItems.isEmpty())
			return;

		mItems.clear();
		mSelectedItemIndex = -1;

		mScrollbar.resetBarTop();
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

	private void moveSelectedUp() {
		if (mSelectedItemIndex <= 0) {
			return;
		}

		var toMove = mItems.get(mSelectedItemIndex);
		mItems.set(mSelectedItemIndex, mItems.get(mSelectedItemIndex - 1));
		mItems.set(mSelectedItemIndex - 1, toMove);
		mSelectedItemIndex--;

	}

	private void moveSelectedDown() {
		if (mSelectedItemIndex >= mItems.size() - 1) {
			return;
		}

		var toMove = mItems.get(mSelectedItemIndex);
		mItems.set(mSelectedItemIndex, mItems.get(mSelectedItemIndex + 1));
		mItems.set(mSelectedItemIndex + 1, toMove);
		mSelectedItemIndex++;

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

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		// ignore
	}

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case ORDER_BUTTON_UP:
			moveSelectedUp();
			break;
		case ORDER_BUTTON_DOWN:
			moveSelectedDown();
			break;
		}

	}

}
