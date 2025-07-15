package net.lintfordlib.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.renderers.windows.components.ScrollBar;
import net.lintfordlib.renderers.windows.components.ScrollBarContentRectangle;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;
import net.lintfordlib.screenmanager.ConstantsScreenManagerAudio;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

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

	private static final float OPEN_HEIGHT = 170;
	private static final float ITEM_HEIGHT = 25.f;

	private static final String NO_ITEMS_FOUND_TEXT = "No items found";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mSelectedIndex;
	private int mHighlightedIndex;
	private float mItemHeight;
	private String mSeparator;
	private List<MenuEnumEntryItem> mItems;
	protected boolean mIsInputActive;
	private transient boolean mOpen;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBarContentRectangle mWindowRectangle;
	private transient ScrollBar mScrollBar;
	private boolean mAllowDuplicateNames;
	private String mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void noItemsFoundText(String newText) {
		if (newText == null)
			mNoItemsFoundText = NO_ITEMS_FOUND_TEXT;

		mNoItemsFoundText = newText;
	}

	public boolean allowDuplicateNames() {
		return mAllowDuplicateNames;
	}

	public void allowDuplicateNames(boolean newValue) {
		mAllowDuplicateNames = newValue;
	}

	public List<MenuEnumEntryItem> items() {
		return mItems;
	}

	public MenuEnumEntryItem selectedItem() {
		if (mItems == null || mItems.isEmpty())
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

	public String label() {
		return mText;
	}

	public void label(String newValue) {
		mText = newValue;
	}

	public String separator() {
		return mSeparator;
	}

	public void separator(String newSeparatorValue) {
		mSeparator = newSeparatorValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuDropDownEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		this(screenManager, parentScreen, "");
	}

	public MenuDropDownEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, label);

		mItems = new ArrayList<>();

		mEnableUpdateDraw = true;
		mOpen = false;

		mContentRectangle = new ScrollBarContentRectangle(this);
		mWindowRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		mSelectedIndex = 0;
		mSeparator = ":";

		contextHintState.buttonA = true;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mEnabled)
			return false;

		if (!core.input().mouse().isMouseMenuSelectionEnabled())
			return false;

		if (!mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace()))
			return false;

		if (!core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (!mHasFocus)
			mParentScreen.setFocusOnEntry(this);

		if (mOpen && mScrollBar.handleInput(core, mScreenManager))
			return true;

		else if (mWindowRectangle.intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().tryAcquireMouseOverThisComponent(hashCode())) {
			if (mOpen) {
				// Something inside the dropdown was highlighted / hovered over
				final var consoleLineHeight = mItemHeight;
				final var relativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos();
				final var relativeIndex = (int) (relativeheight / consoleLineHeight);
				var selectedIndex = relativeIndex;

				if (selectedIndex < 0)
					selectedIndex = 0;

				if (selectedIndex >= mItems.size())
					selectedIndex = mItems.size() - 1;

				mHighlightedIndex = selectedIndex;
			}

			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mOpen) {
					mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_OPEN);

					final var lConsoleLineHeight = mItemHeight;
					final var relativeheight = core.HUD().getMouseCameraSpace().y - mWindowRectangle.y() - mScrollBar.currentYPos();
					final var relativeIndex = (int) (relativeheight / lConsoleLineHeight);
					var selectedIndex = relativeIndex;

					if (selectedIndex < 0)
						selectedIndex = 0;

					if (selectedIndex >= mItems.size())
						selectedIndex = mItems.size() - 1;

					mSelectedIndex = selectedIndex;

					if (mClickListener != null)
						mClickListener.onMenuEntryChanged(this);

					mOpen = false;
					mParentScreen.onMenuEntryDeactivated(this);

				} else {
					// First check to see if the player clicked the info button
					if (mShowInfoIcon && mInfoIconDstRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
						mToolTipEnabled = true;
						mToolTipTimer = 1000;
					} else {
						mOpen = true;
						mParentScreen.onMenuEntryActivated(this);
						scrollContentItemIntoView(mSelectedIndex);
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
		if (!mIsActive)
			return false;

		if (mIsInputActive) {
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
				mSelectedIndex--;

				if (mSelectedIndex < 0)
					mSelectedIndex = 0;

				mHighlightedIndex = mSelectedIndex;
				scrollContentItemIntoView(mHighlightedIndex);
				return true;
			}

			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
				mSelectedIndex++;

				if (mSelectedIndex >= mItems.size())
					mSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = mSelectedIndex;
				scrollContentItemIntoView(mHighlightedIndex);

				return true;
			}

			if (mOpen && core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this)) {
				mSelectedIndex = mHighlightedIndex;
				mOpen = false;
				// mIsInputActive = false; // handled below in onClick
				mParentScreen.onMenuEntryDeactivated(this);
			}

		}

		return super.onHandleKeyboardInput(core);
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		if (!mIsActive)
			return false;

		if (mIsInputActive) {
			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
				mSelectedIndex--;

				if (mSelectedIndex < 0)
					mSelectedIndex = 0;

				mHighlightedIndex = mSelectedIndex;
				scrollContentItemIntoView(mHighlightedIndex);
				return true;
			}

			if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
				mSelectedIndex++;

				if (mSelectedIndex >= mItems.size())
					mSelectedIndex = mItems.size() - 1;

				mHighlightedIndex = mSelectedIndex;
				scrollContentItemIntoView(mHighlightedIndex);
				return true;
			}

			if (mOpen && core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_CROSS, this)) {
				mSelectedIndex = mHighlightedIndex;
				mOpen = false;
				mParentScreen.onMenuEntryDeactivated(this);
			}

			// TODO: add tool tip triangle to all entries
			if (mShowInfoIcon && core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_TRIANGLE, this)) {
				mToolTipEnabled = true;
				mToolTipTimer = 1000;
			}
		}

		return super.onHandleGamepadInput(core);
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		if (!mIsActive)
			return;

		if (mShowInfoIcon)
			mInfoIconDstRectangle.set(mX, mY, 32f, 32f);

		final var uiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
		mItemHeight = ITEM_HEIGHT * uiTextScale;

		mContentRectangle.set(mX, mY + mScrollBar.currentYPos(), mW, mItems.size() * mItemHeight);
		if (mOpen) {
			mWindowRectangle.set(mX, mY, mW, OPEN_HEIGHT);
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
			mParentScreen.onMenuEntryDeactivated(this);
		}

		mIsInputActive = mOpen;
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float componentDepth) {
		final var uiTextScale = mParentScreen.uiTextScale();
		final var textBoldFont = mParentScreen.fontBold();

		final var lParentScreenAlpha = screen.screenColor.a;
		entryColor.a = lParentScreenAlpha;
		textColor.a = lParentScreenAlpha;

		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		textColor.setFromColor(ColorConstants.TextEntryColor);

		final var screenOffset = screen.screenPositionOffset();
		final var fontHeight = textBoldFont.fontHeight() * uiTextScale;
		final var spriteBatch = mParentScreen.spriteBatch();

		mW = (int) mW;

		// Applys to the box
		if (mHasFocus && mEnabled) {
			renderHighlight(core, screen, spriteBatch);

		}

		if (mIsInputActive) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.MenuEntrySelectedColor);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() - mW / 2, screenOffset.y + centerY() - mH / 2, 32, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() - mW / 2 + 32, screenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + centerX() + mW / 2 - 32, screenOffset.y + centerY() - mH / 2, 32, mH, mZ);
			spriteBatch.end();
		}

		final float lSeparatorHalfWidth = textBoldFont.getStringWidth(mSeparator, uiTextScale) * 0.5f;
		if (mText != null && mText.length() > 0) {
			final float lStringWidth = textBoldFont.getStringWidth(mText, 1.f);

			textBoldFont.begin(core.HUD());
			textBoldFont.setTextColor(textColor);
			textBoldFont.drawText(mText, screenOffset.x + (mX + mW / 2 - 10) - lStringWidth - lSeparatorHalfWidth, screenOffset.y + mY + mH / 2.f - textBoldFont.fontHeight() * 0.5f, mZ, 1.f, -1);
			textBoldFont.drawText(mSeparator, screenOffset.x + mX + mW / 2 - lSeparatorHalfWidth, screenOffset.y + mY + mH / 2 - textBoldFont.fontHeight() * 0.5f, mZ, uiTextScale, -1);
			textBoldFont.end();
		}

		if (mItems == null || mItems.isEmpty()) {
			// LOCALIZATION: No entries added to dropdown list
			final var lNoEntriesText = mNoItemsFoundText;
			final var lTextWidth = textBoldFont.getStringWidth(lNoEntriesText);

			textBoldFont.begin(core.HUD());
			textBoldFont.setTextColor(textColor);
			textBoldFont.drawText(lNoEntriesText, screenOffset.x + mX + mW * .5f - lTextWidth * .5f, screenOffset.y + mY + mItemHeight / 2f - fontHeight / 2f, mZ, uiTextScale, -1);
			textBoldFont.end();
			return;
		}

		final var lSelectedMenuEnumEntryItem = mItems.get(mSelectedIndex);

		// Render the selected item in the 'top spot'
		final var lCurItem = lSelectedMenuEnumEntryItem.name;

		textBoldFont.begin(core.HUD());
		textBoldFont.setTextColor(textColor);
		textBoldFont.drawText(lCurItem, screenOffset.x + mX + mW / 2.f + 16.f + lSeparatorHalfWidth, screenOffset.y + mY + mH / 2.f - textBoldFont.fontHeight() * 0.5f, mZ, uiTextScale, -1);
		textBoldFont.end();

		// CONTENT PANE
		// Content is drawn in the postStencilDraw method

		// Draw the down arrow
		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(entryColor);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_DOWN, screenOffset.x + right() - 32, screenOffset.y + top(), 32, 32, mZ);
		spriteBatch.end();

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + mX, screenOffset.y + mY, mW, mH, mZ);
			spriteBatch.end();
		}

		if (!mEnabled)
			drawdisabledBlackOverbar(core, spriteBatch, entryColor.a);

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, entryColor.a);
	}

	@Override
	public void postStencilDraw(LintfordCore core, Screen screen, float parentZDepth) {
		super.postStencilDraw(core, screen, parentZDepth);

		final var uiTextScale = mParentScreen.uiTextScale();
		final var textBoldFont = mParentScreen.fontBold();

		mZ = mOpen ? ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_ACTIVE : ZLayers.LAYER_SCREENMANAGER + Z_STATE_MODIFIER_PASSIVE;

		final var screenOffset = screen.screenPositionOffset();
		final var spriteBatch = mParentScreen.spriteBatch();

		if (mOpen) {
			final float lSeparatorHalfWidth = textBoldFont.getStringWidth(mSeparator, uiTextScale) * 0.5f;

			// Draw the background rectangle
			final var xx = mX + mW / 2.f;
			final var yy = mWindowRectangle.y();
			final var ww = mWindowRectangle.width() / 2.f;
			final var hh = mWindowRectangle.height();

			spriteBatch.begin(core.HUD());
			spriteBatch.setColorRGBA(0.f, 0.f, 0.f, 1.f);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, xx, yy, ww, hh, mZ);
			spriteBatch.end();

			textBoldFont.begin(core.HUD());

			mWindowRectangle.stencilClear();
			mWindowRectangle.preDraw(core, spriteBatch);

			float itemYPos = mY + mH / 2.f - textBoldFont.fontHeight() * .5f + mScrollBar.currentYPos();
			final int lItemCount = mItems.size();
			for (int i = 0; i < lItemCount; i++) {
				final var lItem = mItems.get(i);

				if (i == mHighlightedIndex)
					textColor.setFromColor(ColorConstants.PrimaryColor);
				else
					textColor.setFromColor(ColorConstants.TextEntryColor);

				textBoldFont.setTextColor(textColor);
				textBoldFont.drawText(lItem.name, screenOffset.x + mX + mW / 2.f + 16.f + lSeparatorHalfWidth, screenOffset.y + itemYPos, mZ + 0.1f, uiTextScale, -1);
				itemYPos += mItemHeight;
			}

			textBoldFont.end();

			mWindowRectangle.postDraw(core);
		}

		if (mOpen && mScrollBar.areaNeedsScrolling()) {
			mScrollBar.positionOffset.x = screenOffset.x;
			mScrollBar.positionOffset.y = screenOffset.y;

			spriteBatch.begin(core.HUD());
			mScrollBar.scrollBarAlpha(screen.screenColor.a);
			mScrollBar.draw(core, spriteBatch, mCoreSpritesheet, .1f);
			spriteBatch.end();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager inputManager) {
		super.onClick(inputManager);

		mIsInputActive = !mIsInputActive;

		if (mIsInputActive) {
			mParentScreen.onMenuEntryActivated(this);
			mOpen = true;
			resetCoolDownTimer(); // don't close straight away onEnter in handleInput

			scrollContentItemIntoView(mSelectedIndex);
			mHighlightedIndex = mSelectedIndex;

		} else {
			mParentScreen.onMenuEntryDeactivated(this);
			mOpen = false;
		}
	}

	@Override
	public void onDeactivation(InputManager inputManager) {
		super.onDeactivation(inputManager);

		mOpen = false;
		mIsInputActive = false;
	}

	public void addItem(MenuEnumEntryItem item) {
		if (mItems.contains(item))
			return;

		if (mAllowDuplicateNames) {
			mItems.add(item);
			return;
		}

		final int lNumItems = mItems.size();
		for (int i = 0; i < lNumItems; i++) {
			if (mItems.get(i).name.equals(item.name)) {
				return;
			}
		}

		mItems.add(item);
	}

	private void scrollContentItemIntoView(int itemIndex) {
		final var lItemCount = mItems.size();
		final var idealTopPosition = -(itemIndex - 1) * mItemHeight;
		final var topPosition = MathHelper.clamp(idealTopPosition, lItemCount * -mItemHeight, 0);
		mScrollBar.AbsCurrentYPos(topPosition);
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