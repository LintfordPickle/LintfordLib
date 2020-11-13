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

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private int mSelectedIndex;
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

		if (mScrollBar.handleInput(pCore)) {
			// Check if tool tips are enabled.
			return true;

		}

		// Handle clicks within the component (including both open and closed states)
		else if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseMiddle((hashCode()))) {
				mZScrollAcceleration += pCore.input().mouse().mouseWheelYOffset() * 250.0f;

			}

			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mOpen) {
					final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();

					// TODO: play the menu clicked sound
					final float lConsoleLineHeight = 25f * lUiTextScale;
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
						mParentLayout.parentScreen().setFocusOn(pCore, this, true);

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
	public void updateStructure() {
		super.updateStructure();

		w = Math.min(mParentLayout.w() - 50f, mMaxWidth);

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (mItems == null || mItems.size() == 0)
			return;

		super.update(pCore, pScreen, pIsSelected);

		if (mShowInfoIcon) {
			mInfoIconDstRectangle.set(x, y, 32f, 32f);

		}

		mContentRectangle.set(x, y + mScrollYPosition, w, mItems.size() * 25);
		if (mOpen) {
			mWindowRectangle.set(x + w / 2, y + 32, w / 2, OPEN_HEIGHT);
			set(x, y, w, OPEN_HEIGHT + 32.f);
		} else {
			mWindowRectangle.set(this);
			mWindowRectangle.expand(1);
			//set(x,y,w,h);
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

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pComponentDepth) {
		final var lParentScreen = mParentLayout.parentScreen();
		final float lUiTextScale = lParentScreen.uiTextScale();
		final var lFont = lParentScreen.font();

		// TITLE BAR
		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		// Render the label
		float lTextR = mEnabled ? mParentLayout.parentScreen().r() : 0.24f;
		float lTextG = mEnabled ? mParentLayout.parentScreen().g() : 0.24f;
		float lTextB = mEnabled ? mParentLayout.parentScreen().b() : 0.24f;
		float lTextA = mEnabled ? mParentLayout.parentScreen().a() : 1f;

		final String lSeparator = " : ";

		final float lLabelWidth = lFont.bitmap().getStringWidth(mLabel, lUiTextScale);
		final float lFontHeight = lFont.bitmap().fontHeight() * lUiTextScale;
		final var lTextureBatch = mParentLayout.parentScreen().rendererManager().uiTextureBatch();

		final float lSingleTextHeight = 32f;

		final float lSeparatorHalfWidth = lFont.bitmap().getStringWidth(lSeparator, lUiTextScale) * 0.5f;
		lFont.begin(pCore.HUD());
		lFont.drawShadow(mDrawTextShadow);
		lFont.draw(mLabel, x + w / 2 - 10 - lLabelWidth - lSeparatorHalfWidth, y + lSingleTextHeight / 2f - lFontHeight / 2f, mZ, lTextR, lTextG, lTextB, lTextA, lUiTextScale, -1);
		lFont.draw(lSeparator, x + w / 2 - lSeparatorHalfWidth, y + lSingleTextHeight / 2f - lFontHeight / 2f, mZ, lTextR, lTextG, lTextB, lTextA, lUiTextScale, -1);

		if (mItems == null || mItems.size() == 0) {
			// LOCALIZATION: No entries added to dropdown list
			final String lNoEntriesText = "No items found";
			mParentLayout.parentScreen().font().draw(lNoEntriesText, x + w / 2 + lSeparatorHalfWidth + SPACE_BETWEEN_TEXT, y + h / 2f - lFontHeight / 2f, mZ, lTextR, lTextG, lTextB, lTextA, lUiTextScale, -1);
			mParentLayout.parentScreen().font().end();
			return;
		}

		final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);

		// Render the selected item in the 'top spot'
		final String lCurItem = lSelectedMenuEnumEntryItem.name;
		final float lSelectedTextWidth = lFont.bitmap().getStringWidth(lCurItem);
		lFont.draw(lCurItem, x + (w / 4 * 3) + -lSelectedTextWidth / 2, y + lSingleTextHeight / 2f - lFontHeight / 2f, mZ, lTextR, lTextG, lTextB, lTextA, lUiTextScale, -1);
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

			for (int i = 0; i < mItems.size(); i++) {
				final var lItem = mItems.get(i);
				final float lItemTextWidth = lFont.bitmap().getStringWidth(lItem.name);
				lFont.draw(lItem.name, x + (w / 4 * 3) - lItemTextWidth / 2, lYPos, mZ + 0.1f, lTextR, lTextG, lTextB, lTextA, lUiTextScale, -1);
				lYPos += 25;

			}

			lFont.end();

			GL11.glDisable(GL11.GL_STENCIL_TEST);

		}

		if (mOpen && mScrollBar.areaNeedsScrolling())
			mScrollBar.draw(pCore, lTextureBatch, mUITexture, -0.1f);

		// Draw the down arrow
		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mUITexture, 96, 224, 32, 32, right() - 32 - 8f, top(), 32, 32, mZ, 1f, 1f, 1f, 1f);
		lTextureBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, ALPHA);
			lTextureBatch.end();

		}

		if (mShowInfoIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 544, 0, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();
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