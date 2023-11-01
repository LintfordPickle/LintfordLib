package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.IScrollBarArea;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

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
	private static final float ITEM_HEIGHT = 25.f;

	private static final String NO_ITEMS_FOUND_TEXT = "No items found";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mSelectedIndex;
	private int mHighlightedIndex;
	private float mItemHeight;
	private List<MenuEnumEntryItem> mItems;
	private transient boolean mOpen;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private boolean mAllowDuplicates;
	private String mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void noItemsFoundText(String newText) {
		if (newText == null)
			mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

		mNoItemsFoundText = newText;
	}

	@Override
	public float height() {
		return super.height();// mOpen ? super.height() + mWindowRectangle.height() : super.height();
	}

	public boolean allowDuplicates() {
		return mAllowDuplicates;
	}

	public void allowDuplicates(boolean newValue) {
		mAllowDuplicates = newValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	@Override
	public void isActive(boolean newValue) {
		super.isActive(newValue);

		mOpen = mIsActive;
	}

	public MenuEnumEntryItem selectedItem() {
		if (mItems == null || mItems.size() == 0)
			return null;

		return mItems.get(mSelectedIndex);

	}

	public void setSelectedEntry(int index) {
		if (index < 0)
			index = 0;

		if (index >= mItems.size())
			index = mItems.size() - 1;

		mSelectedIndex = index;
	}

	public void setSelectedEntry(String name) {
		final int lNumDropDownItems = mItems.size();
		for (int i = 0; i < lNumDropDownItems; i++) {
			final var lDropDownItem = mItems.get(i);
			if (lDropDownItem == null)
				continue;

			if (lDropDownItem.name.equals(name)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	public void setSelectEntry(T value) {
		final int lNumDropDownItems = mItems.size();
		for (int i = 0; i < lNumDropDownItems; i++) {
			if (mItems.get(i).value.equals(value)) {
				mSelectedIndex = i;
				return;
			}
		}
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuDropDownEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		super(screenManager, parentScreen, "");

		mItems = new ArrayList<>();

		mEnableUpdateDraw = true;
		mOpen = false;

		mContentRectangle = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mSelectedIndex = 0;

		contextHintState.buttonA = true;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {

		if (!core.input().mouse().isMouseMenuSelectionEnabled())
			return false;

		if (!mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace()))
			return false;

		if (!core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (mHasFocus == false)
			mParentScreen.setFocusOnEntry(this);

		if (mOpen && mScrollBar.handleInput(core, mScreenManager))
			return true;

		else if (mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			if (mOpen) {
				final float lConsoleLineHeight = mItemHeight;
				// Something inside the dropdown was highlighted / hovered over
				float lRelativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos();

				int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
				int lSelectedIndex = lRelativeIndex;

				if (lSelectedIndex < 0)
					lSelectedIndex = 0;

				if (lSelectedIndex >= mItems.size())
					lSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = lSelectedIndex;
			}

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mOpen) {
					// TODO: play the menu clicked sound
					final float lConsoleLineHeight = mItemHeight;
					float lRelativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos();

					int lRelativeIndex = (int) (lRelativeheight / lConsoleLineHeight);
					int lSelectedIndex = lRelativeIndex;

					if (lSelectedIndex < 0)
						lSelectedIndex = 0;

					if (lSelectedIndex >= mItems.size())
						lSelectedIndex = mItems.size() - 1;

					mSelectedIndex = lSelectedIndex;

					if (mClickListener != null) {
						mClickListener.onMenuEntryChanged(this);
					}

					mOpen = false;

				} else {
					// First check to see if the player clicked the info button
					if (mShowInfoIcon && mInfoIconDstRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
						mToolTipEnabled = true;
						mToolTipTimer = 1000;
					} else {
						mOpen = true;
						mParentScreen.onMenuEntryActivated(this);
					}

					if (mToolTipEnabled)
						mToolTipTimer += core.appTime().elapsedTimeMilli();

				}
			}

			return true;
		} else {
			mToolTipTimer = 0;
		}

		return mOpen;
	}

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {

		if (mIsActive) {
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
				mSelectedIndex--;

				if (mSelectedIndex < 0)
					mSelectedIndex = 0;

				mHighlightedIndex = mSelectedIndex;
				return true;
			}

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
				mSelectedIndex++;

				if (mSelectedIndex >= mItems.size())
					mSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = mSelectedIndex;
				return true;
			}
		}

		return super.onHandleKeyboardInput(core);
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {

		if (mIsActive) {
			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
				mSelectedIndex--;

				if (mSelectedIndex < 0)
					mSelectedIndex = 0;

				mHighlightedIndex = mSelectedIndex;
				return true;
			}

			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
				mSelectedIndex++;

				if (mSelectedIndex >= mItems.size())
					mSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = mSelectedIndex;
				return true;
			}
		}

		return super.onHandleGamepadInput(core);
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		if (mShowInfoIcon)
			mInfoIconDstRectangle.set(mX, mY, 32f, 32f);

		final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
		mItemHeight = ITEM_HEIGHT * lUiTextScale;

		mContentRectangle.set(mX, mY + mScrollBar.currentYPos(), mW, mItems.size() * mItemHeight);
		if (mOpen) {
			mWindowRectangle.set(mX + mW / 2, mY, mW / 2, OPEN_HEIGHT);
			set(mX, mY, mW, 32.f);
		} else {
			mWindowRectangle.set(this);
			mWindowRectangle.expand(1);
		}

		mScrollBar.update(core);

		if (mOpen) {
			contextHintState.buttonDpadU = true;
			contextHintState.buttonDpadD = true;
		} else {
			contextHintState.buttonDpadU = false;
			contextHintState.buttonDpadD = false;
		}

		if (mOpen && core.input().mouse().isMouseLeftButtonDown() && !mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mOpen = false;
		}

		mIsActive = mOpen;

		final var lMouseMenuControls = core.input().mouse().isMouseMenuSelectionEnabled();
		if (mOpen && mScrollBar.scrollBarEnabled() && lMouseMenuControls == false) {
			final var lCurrentIndex = mHighlightedIndex;
			final var lEntryTopExtent = mContentRectangle.y() + (lCurrentIndex * mItemHeight);
			final var lEntryBottomExtent = mContentRectangle.y() + ((lCurrentIndex + 1) * mItemHeight);

			final var lWindowTopExtent = mWindowRectangle.y();
			final var lWindowBottomExtent = mWindowRectangle.bottom();

			if (lEntryTopExtent < lWindowTopExtent) {
				if (Math.abs(lEntryTopExtent - lWindowTopExtent) > 5)
					mScrollBar.RelCurrentYPos(5);
				else
					mScrollBar.RelCurrentYPos(1);
			}

			if (lEntryBottomExtent > lWindowBottomExtent) {
				if (Math.abs(lEntryBottomExtent - lWindowBottomExtent) > 5)
					mScrollBar.RelCurrentYPos(-5);
				else
					mScrollBar.RelCurrentYPos(-1);
			}
		}
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float componentDepth) {
		final float lUiTextScale = mParentScreen.uiTextScale();
		final var lTextBoldFont = mParentScreen.fontBold();

		final float lParentScreenAlpha = screen.screenColor.a;
		entryColor.a = lParentScreenAlpha;
		textColor.a = lParentScreenAlpha;

		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		textColor.setFromColor(ColorConstants.TextEntryColor);

		final var lScreenOffset = screen.screenPositionOffset();

		final var lFontHeight = lTextBoldFont.fontHeight() * lUiTextScale;
		final var lSpriteBatch = mParentScreen.spriteBatch();

		lTextBoldFont.begin(core.HUD());
		if (mHasFocus && mEnabled) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, (int) (lScreenOffset.x + centerX() - mW / 2), lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, (int) (lScreenOffset.x + centerX() - (mW / 2) + 32), lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, (int) (lScreenOffset.x + centerX() + (mW / 2) - 32), lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, ColorConstants.MenuEntryHighlightColor);
			lSpriteBatch.end();
		}

		if (mItems == null || mItems.size() == 0) {
			// LOCALIZATION: No entries added to dropdown list
			final String lNoEntriesText = mNoItemsFoundText;
			final var lTextWidth = lTextBoldFont.getStringWidth(lNoEntriesText);
			lTextBoldFont.drawText(lNoEntriesText, lScreenOffset.x + mX + mW * .5f - lTextWidth * .5f, lScreenOffset.y + mY + mItemHeight / 2f - lFontHeight / 2f, mZ, textColor, lUiTextScale, -1);
			lTextBoldFont.end();
			return;
		}

		final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);

		// Render the selected item in the 'top spot'
		final String lCurItem = lSelectedMenuEnumEntryItem.name;
		final float lSelectedTextWidth = lTextBoldFont.getStringWidth(lCurItem);
		lTextBoldFont.drawText(lCurItem, lScreenOffset.x + mX + mW * .5f + -lSelectedTextWidth / 2, lScreenOffset.y + mY + mItemHeight / 2f - lFontHeight / 2f, mZ, textColor, lUiTextScale, -1);
		lTextBoldFont.end();

		// CONTENT PANE

		// Draw the down arrow
		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_DOWN, lScreenOffset.x + right() - 32, lScreenOffset.y + top(), 32, 32, mZ, entryColor);
		lSpriteBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}

		if (!mEnabled)
			drawdisabledBlackOverbar(core, lSpriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);
	}

	@Override
	public void postStencilDraw(LintfordCore core, Screen screen, float parentZDepth) {
		super.postStencilDraw(core, screen, parentZDepth);

		final float lUiTextScale = mParentScreen.uiTextScale();
		final var lTextBoldFont = mParentScreen.fontBold();

		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mOpen) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, mWindowRectangle, mZ, ColorConstants.getBlackWithAlpha(1.f));
			lSpriteBatch.end();

			lTextBoldFont.begin(core.HUD());

			// We need to use a stencil buffer to clip the list box items (which, when scrolling, could appear out-of-bounds of the listbox).
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
			GL11.glStencilMask(0xFF); // Write to stencil buffer

			// Make sure we are starting with a fresh stencil buffer
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_BLACK, mWindowRectangle, -8f, ColorConstants.getBlackWithAlpha(0.f));
			lSpriteBatch.end();

			// Start the stencil buffer test to filter out everything outside of the scroll view
			GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

			float lYPos = mWindowRectangle.y() + mScrollBar.currentYPos();

			final int lItemCount = mItems.size();
			for (int i = 0; i < lItemCount; i++) {
				final var lItem = mItems.get(i);
				final float lItemTextWidth = lTextBoldFont.getStringWidth(lItem.name);

				if (i == mHighlightedIndex)
					textColor.setFromColor(ColorConstants.PrimaryColor);
				else
					textColor.setFromColor(ColorConstants.TextEntryColor);

				lTextBoldFont.drawText(lItem.name, lScreenOffset.x + mX + (mW / 4 * 3) - lItemTextWidth / 2, lScreenOffset.y + lYPos, mZ + 0.1f, textColor, lUiTextScale, -1);
				lYPos += mItemHeight;
			}

			lTextBoldFont.end();

			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}

		if (mOpen && mScrollBar.areaNeedsScrolling()) {
			lSpriteBatch.begin(core.HUD());
			mScrollBar.draw(core, lSpriteBatch, mCoreSpritesheet, -0.1f, screen.screenColor.a);
			lSpriteBatch.end();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsActive = !mIsActive;

		if (mIsActive) {
			mParentScreen.onMenuEntryActivated(this);
			mOpen = true;
		} else {
			mParentScreen.onMenuEntryDeactivated(this);
			mOpen = false;
		}
	}

	@Override
	public void onDeselection(InputManager inputManager) {
		super.onDeselection(inputManager);

		mOpen = false;
		mIsActive = false;
	}

	public void addItem(MenuEnumEntryItem item) {
		if (mAllowDuplicates) {
			mItems.add(item);
			return;
		}

		if (mItems.contains(item))
			return;

		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).name.equals(item.name)) {
				return;
			}
		}

		mItems.add(item);
	}

	// --------------------------------------
	// Implemented Methods
	// --------------------------------------

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