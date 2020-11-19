package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

/**  */
public class MenuDropDownEntry<T> extends MenuEntry implements IScrollBarArea {

	private static final long serialVersionUID = -2874418532803740656L;

	public class MenuEnumEntryItem {
		public String name;
		public T value;

		public MenuEnumEntryItem(String pName, T pValue) {
			name = pName;
			value = pValue;

		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final float OPEN_HEIGHT = 100;
	private static final float SPACE_BETWEEN_TEXT = 15;
	private static final float ITEM_HEIGHT = 25.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private int mSelectedIndex;
	private int mHighlightedIndex;
	private float mItemHeight;
	private List<MenuEnumEntryItem> mItems;
	private transient boolean mOpen;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private transient float mScrollYPosition;
	private float mZScrollAcceleration;
	private float mZScrollVelocity;

	private boolean mAllowDuplicates;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean allowDuplicates() {
		return mAllowDuplicates;
	}

	public void allowDuplicates(boolean pNewValue) {
		mAllowDuplicates = pNewValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	@Override
	public void hasFocus(boolean pNewValue) {
		super.hasFocus(pNewValue);

		if (!mFocusLocked && !pNewValue)
			mOpen = pNewValue;

	}

	public MenuEnumEntryItem selectedItem() {
		if (mItems == null || mItems.size() == 0)
			return null;

		return mItems.get(mSelectedIndex);

	}

	public void setSelectedEntry(int pIndex) {
		if (pIndex < 0)
			pIndex = 0;
		if (pIndex >= mItems.size())
			pIndex = mItems.size() - 1;

		mSelectedIndex = pIndex;

	}

	public void setSelectedEntry(String pName) {
		final int lNumDropDownItems = mItems.size();
		for (int i = 0; i < lNumDropDownItems; i++) {
			final var lDropDownItem = mItems.get(i);
			if (lDropDownItem == null)
				continue;
			if (lDropDownItem.name.equals(pName)) {
				mSelectedIndex = i;
				return;
			}
		}

	}

	public void setSelectEntry(T pValue) {
		final int lNumDropDownItems = mItems.size();
		for (int i = 0; i < lNumDropDownItems; i++) {
			if (mItems.get(i).value.equals(pValue)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuDropDownEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel) {
		super(pScreenManager, pParentLayout, "");

		mItems = new ArrayList<>();
		mLabel = pLabel;

		mActive = true;
		mOpen = false;

		mContentRectangle = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mSelectedIndex = 0;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mItems == null || mItems.size() == 0 || !mEnabled)
			return false;

		if (mOpen && mScrollBar.handleInput(pCore)) {
			return true;

		}

		// Handle clicks within the component (including both open and closed states)
		else if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

			if (mOpen) {
				final float lConsoleLineHeight = mItemHeight;
				// Something inside the dropdown was select
				float lRelativeheight = pCore.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollYPosition;

				int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
				int lSelectedIndex = lRelativeIndex;

				if (lSelectedIndex < 0)
					lSelectedIndex = 0;
				if (lSelectedIndex >= mItems.size())
					lSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = lSelectedIndex;

			}

			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mOpen) {
					// TODO: play the menu clicked sound

					final float lConsoleLineHeight = mItemHeight;
					// Something inside the dropdown was select
					float lRelativeheight = pCore.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollYPosition;

					int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
					int lSelectedIndex = lRelativeIndex;

					if (lSelectedIndex < 0)
						lSelectedIndex = 0;
					if (lSelectedIndex >= mItems.size())
						lSelectedIndex = mItems.size() - 1;

					mSelectedIndex = lSelectedIndex;

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mOpen = false;

				} else {
					// First check to see if the player clicked the info button
					if (mShowInfoIcon && mInfoIconDstRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
						mToolTipEnabled = true;
						mToolTipTimer = 1000;

					} else {
						mOpen = true;
						mParentLayout.parentScreen.setFocusOn(pCore, this, true);

					}

					//
					// Check if tool tips are enabled.
					if (mToolTipEnabled) {
						mToolTipTimer += pCore.appTime().elapsedTimeMilli();

					}

				}

				return true;

			}

		} else {
			mToolTipTimer = 0;

		}

		return false;

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (mItems == null || mItems.size() == 0)
			return;

		super.update(pCore, pScreen, pIsSelected);

		if (mShowInfoIcon) {
			mInfoIconDstRectangle.set(x, y, 32f, 32f);

		}

		final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
		mItemHeight = ITEM_HEIGHT * lUiTextScale;

		mContentRectangle.set(x, y + mScrollYPosition, w, mItems.size() * mItemHeight);
		if (mOpen) {
			mWindowRectangle.set(x + w / 2, y + 32, w / 2, OPEN_HEIGHT);
			set(x, y, w, OPEN_HEIGHT + 32.f);
		} else {
			mWindowRectangle.set(this);
			mWindowRectangle.expand(1);

		}

		mScrollBar.update(pCore);

		final var lDeltaTime = (float) pCore.appTime().elapsedTimeSeconds();
		var lScrollSpeedFactor = mScrollYPosition;

		mZScrollVelocity += mZScrollAcceleration;
		lScrollSpeedFactor += mZScrollVelocity * lDeltaTime;
		mZScrollVelocity *= 0.85f;
		mZScrollAcceleration = 0.0f;

		// Constrain
		mScrollYPosition = lScrollSpeedFactor;
		if (mScrollYPosition > 0)
			mScrollYPosition = 0;
		if (mScrollYPosition < -(fullContentArea().h() - contentDisplayArea().h()))
			mScrollYPosition = -(fullContentArea().h() - contentDisplayArea().h());

		if (mOpen && pCore.input().mouse().isMouseLeftButtonDown() && !intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mOpen = false;

		}

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pComponentDepth) {
		final var lParentScreen = mParentLayout.parentScreen;
		final float lUiTextScale = lParentScreen.uiTextScale();
		final var lFont = lParentScreen.font();

		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		// Render the label
		entryColor.r = mEnabled ? lParentScreen.r() : 0.24f;
		entryColor.g = mEnabled ? lParentScreen.g() : 0.24f;
		entryColor.b = mEnabled ? lParentScreen.b() : 0.24f;
		entryColor.a = lParentScreen.a();

		textColor.r = .94f;
		textColor.g = .94f;
		textColor.b = .94f;
		textColor.a = lParentScreen.a();

		final String lSeparator = " : ";

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, lUiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;
		final var lTextureBatch = lParentScreen.textureBatch();

		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(lSeparator, lUiTextScale) * 0.5f;
		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mLabel, x + w / 2 - 10 - lLabelWidth - lSeparatorHalfWidth, y + mItemHeight / 2f - lFontHeight / 2f, mZ, textColor.r, textColor.g, textColor.b, textColor.a, lUiTextScale, -1);
		lFont.draw(lSeparator, x + w / 2 - lSeparatorHalfWidth, y + mItemHeight / 2f - lFontHeight / 2f, mZ, textColor.r, textColor.g, textColor.b, textColor.a, lUiTextScale, -1);

		if (mHoveredOver) {
			final float lHoveredColorHighlightR = 204.f / 255.f;
			final float lHoveredColorHighlightG = 115.f / 255.f;
			final float lHoveredColorHighlightB = 102.f / 255.f;

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.end();

		}

		if (mItems == null || mItems.size() == 0) {
			// LOCALIZATION: No entries added to dropdown list
			final String lNoEntriesText = "No items found";
			lFont.draw(lNoEntriesText, x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT, y + h / 2f - lFontHeight / 2f, mZ, textColor.r, textColor.g, textColor.b, textColor.a, lUiTextScale, -1);
			lFont.end();
			return;
		}

		final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);

		// Render the selected item in the 'top spot'
		final String lCurItem = lSelectedMenuEnumEntryItem.name;
		final float lSelectedTextWidth = lFont.bitmap().getStringWidth(lCurItem);
		lFont.draw(lCurItem, x + (w / 4 * 3) + -lSelectedTextWidth / 2, y + mItemHeight / 2f - lFontHeight / 2f, mZ, textColor.r, textColor.g, textColor.b, textColor.a, lUiTextScale, -1);
		lFont.end();

		// CONTENT PANE

		if (mOpen) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, mWindowRectangle, mZ, 0.f, 0.f, 0.f, 1);
			lTextureBatch.end();

			lFont.begin(pCore.HUD());

			// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
			GL11.glStencilMask(0xFF); // Write to stencil buffer

			// Make sure we are starting with a fresh stencil buffer
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 32, 0, 32, 32, mWindowRectangle, -8f, 1, 1, 1, 0);
			lTextureBatch.end();

			// Start the stencil buffer test to filter out everything outside of the scroll view
			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

			float lYPos = mWindowRectangle.y() + mScrollYPosition;

			final int lItemCount = mItems.size();
			for (int i = 0; i < lItemCount; i++) {
				final var lItem = mItems.get(i);
				final float lItemTextWidth = lFont.bitmap().getStringWidth(lItem.name);

				// Highlight the item were the mouse is over
				// FIXME: Take the LintfordCore primary color
				if (i == mHighlightedIndex) {
					textColor.r = 204.f / 255.f;
					textColor.g = 115.f / 255.f;
					textColor.b = 102.f / 255.f;

				} else {
					textColor.r = .94f;
					textColor.g = .94f;
					textColor.b = .94f;
				}

				lFont.draw(lItem.name, x + (w / 4 * 3) - lItemTextWidth / 2, lYPos, mZ + 0.1f, textColor.r, textColor.g, textColor.b, textColor.a, lUiTextScale, -1);
				lYPos += mItemHeight;

			}

			lFont.end();

			GL11.glDisable(GL11.GL_STENCIL_TEST);

		}

		if (mOpen && mScrollBar.areaNeedsScrolling())
			mScrollBar.draw(pCore, lTextureBatch, mUITexture, -0.1f);

		// Draw the down arrow
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mUITexture, 96, 224, 32, 32, right() - 32, top(), 32, 32, mZ, entryColor.r, entryColor.g, entryColor.b, entryColor.a);
		lTextureBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, ALPHA);
			lTextureBatch.end();

		}

		if(!mEnabled) {
			drawdisabledBlackOverbar(pCore, lTextureBatch, entryColor.a);
			
		}

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lTextureBatch, mInfoIconDstRectangle, entryColor.a);

		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lTextureBatch, mWarnIconDstRectangle, entryColor.a);

		}

	}

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

	}

	public void addItem(MenuEnumEntryItem pItem) {
		if (mAllowDuplicates) {
			mItems.add(pItem);

			return;
		}

		// Proceed in the case that duplicates are not allowed
		final int COUNT = mItems.size();
		if (mItems.contains(pItem)) {
			return;

		}

		for (int i = 0; i < COUNT; i++) {
			if (mItems.get(i).name.equals(pItem.name)) {
				return;
			}

		}

		mItems.add(pItem);

	}

	// --------------------------------------
	// Implemented Methods
	// --------------------------------------

	@Override
	public float currentYPos() {
		return mScrollYPosition;
	}

	@Override
	public void RelCurrentYPos(float pAmt) {
		mScrollYPosition += pAmt;

	}

	@Override
	public void AbsCurrentYPos(float pValue) {
		mScrollYPosition = pValue;

	}

	@Override
	public Rectangle contentDisplayArea() {
		return mWindowRectangle;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;

	}

	public void clearItems() {
		mSelectedIndex = 0;
		mItems.clear();

	}

}