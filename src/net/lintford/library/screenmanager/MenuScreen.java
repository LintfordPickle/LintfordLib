package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintford.library.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public abstract class MenuScreen extends Screen implements EntryInteractions {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final float ANIMATION_TIMER_LENGTH = 130; // ms

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseLayout> mLayouts;
	protected LAYOUT_ALIGNMENT mLayoutAlignment = LAYOUT_ALIGNMENT.CENTER;
	protected String mMenuTitle;
	protected String mMenuOverTitle;
	protected String mMenuSubTitle;
	protected float mTitleFontHeight;

	protected MenuEntry mActiveEntry;
	protected boolean mIsSelectedActive;
	protected int mSelectedEntryIndex;
	protected int mSelectedLayoutIndex;

	protected boolean mESCBackEnabled;
	protected ClickAction mClickAction;
	protected float mAnimationTimer;
	protected FontUnit mMenuFont;
	protected FontUnit mMenuFontBold;
	protected FontUnit mMenuHeaderFont;

	protected float mTitlePaddingHorizontal;
	protected float mTitlePaddingVertical;
	protected float mScreenPaddingLeft;
	protected float mScreenPaddingRight;
	protected float mScreenPaddingTop;
	protected float mScreenPaddingBottom;
	protected float mLayoutPaddingHorizontal;
	protected float mLayoutPaddingVertical;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEntryActive() {
		return mActiveEntry != null;
	}

	public LAYOUT_ALIGNMENT layoutAlignment() {
		return mLayoutAlignment;
	}

	public void layoutAlignment(LAYOUT_ALIGNMENT layoutAlignment) {
		mLayoutAlignment = layoutAlignment;
	}

	public float uiTextScale() {
		return mScreenManager.UiStructureController().uiTextScaleFactor();
	}

	public FontUnit font() {
		return mMenuFont;
	}

	public FontUnit fontBold() {
		return mMenuFontBold;
	}

	public FontUnit fontHeader() {
		return mMenuHeaderFont;
	}

	public boolean isAnimating() {
		return mAnimationTimer > 0;
	}

	public String menuTitle() {
		return mMenuTitle;
	}

	public void menuTitle(String title) {
		mMenuTitle = title;
	}

	public String menuSubTitle() {
		return mMenuSubTitle;
	}

	public void menuOverTitle(String overTitle) {
		mMenuOverTitle = overTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuScreen(ScreenManager screenManager, String menuTitle) {
		super(screenManager);

		mLayouts = new ArrayList<>();

		mShowBackgroundScreens = false;
		mShowContextualKeyHints = true;

		mMenuTitle = menuTitle;
		mBlockKeyboardInputInBackground = true;
		mBlockMouseInputInBackground = true;
		mBlockGamepadInputInBackground = true;

		mScreenPaddingTop = 0.f;
		mScreenPaddingBottom = 0.f;
		mScreenPaddingLeft = 0.f;
		mScreenPaddingRight = 0.f;

		mLayoutPaddingHorizontal = 5.f;
		mLayoutPaddingVertical = 2.f;
		mTitlePaddingHorizontal = 10.f;
		mTitlePaddingVertical = 20.f;

		mClickAction = new ClickAction();
		mESCBackEnabled = true;

		screenManager.contextHintManager().contextHintProvider(null);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize() {
		super.initialize();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).initialize();
		}

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, true);
	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);

		mMenuFont = resourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_ENTRY_NAME);
		mMenuFontBold = resourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_BOLD_ENTRY_NAME);
		mMenuHeaderFont = resourceManager.fontManager().getFontUnit(ScreenManager.FONT_MENU_TITLE_NAME);
		mTitleFontHeight = mRendererManager.headerFontHeight();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).loadResources(resourceManager);
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).unloadResources();
		}

		mMenuFont = null;
		mMenuHeaderFont = null;
	}

	@Override
	public void handleInput(LintfordCore core) {
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		super.handleInput(core);

		if (mESCBackEnabled) {
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_B, this)) {
				if (mScreenState == ScreenState.Active) {
					exitScreen();
					return;
				}
			}
		}

		// TODO :Check for gamepad axis for each of the cardinal directions ..
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
			onNavigationUp(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
			onNavigationDown(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this)) {
			onNavigationLeft(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this)) {
			onNavigationRight(core);
		}

		// Process ENTER key press
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this) || core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this)) {
			final var lEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);

			if (lEntry != null)
				lEntry.onClick(core.input());
		}

		final var lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(core);
		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (!mIsinitialized)
			return;

		if (!coveredByOtherScreen) {

			if (mESCBackEnabled) {
				mScreenManager.contextHintManager().screenManagerHintState().buttonB = true;
				mScreenManager.contextHintManager().screenManagerHintState().buttonBHint = "back";
				mScreenManager.contextHintManager().screenManagerHintState().keyEsc = true;
				mScreenManager.contextHintManager().screenManagerHintState().keyEscHint = "back";
			} else {
				mScreenManager.contextHintManager().screenManagerHintState().buttonB = false;
				mScreenManager.contextHintManager().screenManagerHintState().buttonBHint = null;
				mScreenManager.contextHintManager().screenManagerHintState().keyEsc = false;
			}
		}

		final var lDeltaTime = core.appTime().elapsedTimeMilli();

		updateLayoutSize(core);

		if (mAnimationTimer > 0) {
			mAnimationTimer -= lDeltaTime;

		} else if (mClickAction.entryUid() != -1 && !mClickAction.isConsumed()) { // something was clicked
			handleOnClick();

			mClickAction.reset();

			return;
		}

		final var lNumLayouts = mLayouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lLayout = mLayouts.get(i);
			if (mSelectedLayoutIndex == i) {
				lLayout.focusedEntryIndex(mSelectedEntryIndex);
			} else {
				lLayout.focusedEntryIndex(-1);
			}

			lLayout.update(core);
		}
	}

	public void updateLayoutSize(LintfordCore core) {
		if (mRendererManager == null || mLayouts.size() == 0)
			return;

		updateLayout(core, mLayouts, mLayoutAlignment);
	}

	protected void updateLayout(LintfordCore core, List<BaseLayout> layoutList, LAYOUT_ALIGNMENT alignment) {
		if (layoutList == null || layoutList.size() == 0)
			return;

		if (mRendererManager == null)
			return;

		final var lUIHUDStructureController = mRendererManager.uiStructureController();
		if (lUIHUDStructureController == null)
			return;

		final int lLeftLayoutCount = layoutList.size();

		final float lScreenContentWidth = lUIHUDStructureController.menuMainRectangle().width();

		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);

			// Layout size gets
			float lLayoutWidth = lScreenContentWidth - mLayoutPaddingHorizontal * 2f;
			if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THREEQUARTER) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f * 3f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.TWOTHIRD) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f * 2f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.HALF) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 2f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THIRD) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.QUARTER) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
			}

			var lLayoutNewX = 0.f;
			switch (alignment) {
			case LEFT:
				lLayoutNewX = lUIHUDStructureController.menuMainRectangle().left() + mLayoutPaddingHorizontal;
				break;
			case CENTER:
				lLayoutNewX = 0 - lLayoutWidth / 2f;
				break;
			case RIGHT:
				lLayoutNewX = lUIHUDStructureController.menuMainRectangle().right() - mLayoutPaddingHorizontal - lLayoutWidth;
				break;
			}

			lBaseLayout.x(lLayoutNewX);
			lBaseLayout.width(lLayoutWidth);
		}

		var lLayoutNewY = (int) (lUIHUDStructureController.menuMainRectangle().top() + mScreenPaddingTop);
		var lLayoutHeight = (int) (lUIHUDStructureController.menuMainRectangle().height() - mScreenPaddingTop);

		// See how many layouts only take what they need
		int lCountOfSharers = lLeftLayoutCount;
		int lCountOfTakers = 0;
		int heightTaken = 0;

		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);
			if (lBaseLayout.layoutFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lCountOfTakers++;
				heightTaken += lBaseLayout.getEntryHeight();
			}
		}

		lCountOfSharers -= lCountOfTakers;

		// Split the remaining height between the shares
		int lSizeOfEachShareElement = 0;
		if (lCountOfSharers > 0) {
			lSizeOfEachShareElement = (int) Math.floor(((lLayoutHeight - heightTaken) / lCountOfSharers) - mLayoutPaddingVertical * (lCountOfSharers + 1));
		}

		float lTop = lLayoutNewY;
		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);

			lBaseLayout.y(lTop);

			if (lBaseLayout.layoutFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				// Takers (cannot be larger than available)
				final var lTitleHeight = lBaseLayout.titleBarSize();
				var lNewHeight = Math.min(lBaseLayout.getEntryHeight() + mLayoutPaddingVertical + lBaseLayout.cropPaddingTop() + lBaseLayout.cropPaddingBottom() + lTitleHeight, lLayoutHeight);
				if (lBaseLayout.maxHeight() > 0 && lNewHeight > lBaseLayout.maxHeight()) {
					lNewHeight = lBaseLayout.maxHeight();
				}

				lBaseLayout.height(lNewHeight);
				lBaseLayout.updateStructure();
				lTop += lBaseLayout.getEntryHeight() + mLayoutPaddingVertical;
			} else {
				// sharers
				int lNewHeight = lSizeOfEachShareElement;
				if (lBaseLayout.maxHeight() != -1 && lNewHeight > lBaseLayout.maxHeight()) {
					lNewHeight = (int) lBaseLayout.maxHeight();
				}
				lBaseLayout.height(lNewHeight);
				lBaseLayout.updateStructure();
				lTop += lSizeOfEachShareElement + mLayoutPaddingVertical;
			}
		}
	}

	@Override
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		if (!mIsinitialized)
			return;

		super.draw(core);

		final float lMenuScreenZDepth = ZLayers.LAYER_SCREENMANAGER;

		drawMenuTitle(core);

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(core, lMenuScreenZDepth + (i * 0.001f));
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public int getLayoutCount() {
		return mLayouts.size();
	}

	public void addLayout(BaseLayout layoutToAdd) {
		mLayouts.add(layoutToAdd);
	}

	public void removeLayout(BaseLayout layoutToAdd) {
		mLayouts.remove(layoutToAdd);
	}

	protected void drawMenuTitle(LintfordCore core) {
		if (mMenuTitle == null || mMenuTitle.length() == 0 && mMenuHeaderFont == null)
			return;

		final var lUiStructureController = mScreenManager.UiStructureController();
		final float lUiTextScale = lUiStructureController.uiTextScaleFactor();

		final var lHeaderRect = lUiStructureController.menuTitleRectangle();
		final var lHeaderFontWidth = mMenuHeaderFont.getStringWidth(mMenuTitle, lUiTextScale);
		final var lHeaderFontHeight = mMenuHeaderFont.fontHeight() * lUiTextScale;

		final float lMenuTitlePositionX = lHeaderRect.centerX() - lHeaderFontWidth * .5f;
		final float lMenuTitlePositionY = lHeaderRect.top() + 5;

		mMenuHeaderFont.begin(core.HUD());
		mMenuHeaderFont.drawText(mMenuTitle, lMenuTitlePositionX, lMenuTitlePositionY, -0.01f, screenColor, lUiTextScale);
		mMenuHeaderFont.end();

		if (mMenuFont != null) {
			mMenuFont.begin(core.HUD());

			final float lOverTitleWidth = mMenuFont.getStringWidth(mMenuOverTitle, lUiTextScale);
			if (mMenuOverTitle != null && mMenuOverTitle.length() > 0) {
				mMenuFont.drawText(mMenuOverTitle, lHeaderRect.centerX() - lOverTitleWidth * .5f, lMenuTitlePositionY, -0.01f, screenColor, lUiTextScale);
			}

			final float lSubTitleWidth = mMenuFont.getStringWidth(mMenuSubTitle, lUiTextScale);
			if (mMenuSubTitle != null && mMenuSubTitle.length() > 0) {
				mMenuFont.drawText(mMenuSubTitle, lHeaderRect.centerX() - lSubTitleWidth * .5f, lMenuTitlePositionY + lHeaderFontHeight, -0.01f, screenColor, lUiTextScale);
			}

			mMenuFont.end();
		}
	}

	protected void onCancel() {
		exitScreen();
	}

	@Override
	public void menuEntryOnClick(InputManager inputState, int entryUid) {
		mClickAction.setNewClick(entryUid);
		mAnimationTimer = ANIMATION_TIMER_LENGTH;
	}

	protected abstract void handleOnClick();

	@Override
	public boolean isActionConsumed() {
		return mClickAction != null && mClickAction.isConsumed();
	}

	@Override
	public void onViewportChange(float width, float height) {
		super.onViewportChange(width, height);

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mLayouts.get(i).onViewportChange(width, height);
		}
	}

	// INPUT INTERACTION --------------------

	@Override
	public void onMenuEntryActivated(MenuEntry activeEntry) {
		mActiveEntry = activeEntry;
	}

	@Override
	public void onMenuEntryDeactivated(MenuEntry activeEntry) {
		mActiveEntry = null;
	}

	// mouse
	public void setFocusOnEntry(MenuEntry entry) {
		if (mActiveEntry != null)
			mActiveEntry.onDeselection(mScreenManager.core().input());

		mScreenManager.contextHintManager().contextHintProvider(null);
		mActiveEntry = null;

		final int lNumLayouts = mLayouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lLayout = mLayouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var IsDesiredEntry = lEntry == entry;

				if (IsDesiredEntry) {
					lEntry.mHasFocus = true;
					mScreenManager.contextHintManager().contextHintProvider(lEntry);

					mSelectedLayoutIndex = i;
					mSelectedEntryIndex = j;

				} else {
					lEntry.mHasFocus = false;
					lEntry.mIsActive = false;
				}
			}
		}
	}

	public void deactivateAllEntries() {

	}

	protected MenuEntry getSelectedEntry(List<BaseLayout> selectedLayouts, int selectedLayoutIndex, int selectedEntryIndex) {
		if (selectedLayouts == null || selectedLayouts.size() == 0)
			return null;

		final var lLayout = selectedLayouts.get(selectedLayoutIndex);
		return lLayout.getMenuEntryByIndex(selectedEntryIndex);
	}

	protected void updateAllEntriesToMatchSelected(List<BaseLayout> layouts, int selectedLayoutIndex, int selectedEntryIndex, boolean focusSelected) {
		final int lNumLayouts = layouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lIsLayoutSelected = focusSelected & i == selectedLayoutIndex;
			final var lLayout = layouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var lIsEntrySelected = lIsLayoutSelected && j == selectedEntryIndex;

				if (lIsEntrySelected) {
					lEntry.mHasFocus = true;
					mScreenManager.contextHintManager().contextHintProvider(lEntry);
				} else {
					lEntry.mHasFocus = false;
				}
			}
		}
	}

	// NAVIGATION ---------------------------

	protected void onNavigationUp(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		if (acceptGamepadInput == false && acceptKeyboardInput == false)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		getPreviousEnabledEntry();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, true);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	protected void onNavigationDown(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		if (acceptGamepadInput == false && acceptKeyboardInput == false)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		getNextEnabledEntry();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, true);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	protected void onNavigationLeft(LintfordCore core) {

	}

	protected void onNavigationRight(LintfordCore core) {

	}

	// NAVIGATION ---------------------------

	protected void getPreviousEnabledEntry() {
		if (mActiveEntry != null)
			return;

		boolean found = false;

		var checkEntryIndex = mSelectedEntryIndex;

		if (mSelectedLayoutIndex >= mLayouts.size())
			mSelectedLayoutIndex = 0;

		while (found == false) {
			checkEntryIndex--;

			if (checkEntryIndex < 0) {
				if (mLayouts.size() > 1) {
					getPreviousEnabledLayout();

					// whatever layout is now active, go with it
					final var lLayout = mLayouts.get(mSelectedLayoutIndex);

					if (lLayout.entries().size() == 0) {
						mSelectedEntryIndex = 0;
						return;
					}

					checkEntryIndex = lLayout.entries().size() - 1;

					if (checkEntryIndex == mSelectedEntryIndex)
						return;

					if (!lLayout.hasEntry(checkEntryIndex)) {
						mSelectedEntryIndex = 0;
						return;
					}

					mSelectedEntryIndex = checkEntryIndex;
					return;

				} else {
					final var lLayout = mLayouts.get(mSelectedLayoutIndex);
					checkEntryIndex = lLayout.entries().size() - 1;

					if (lLayout.entries().size() == 0) {
						mSelectedEntryIndex = 0;
						return;
					}

					if (checkEntryIndex == mSelectedEntryIndex)
						return;

					final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
					if (lFoundEntry.enabled() == false)
						continue;

					mSelectedEntryIndex = checkEntryIndex;
					return;
				}
			} else {
				final var lLayout = mLayouts.get(mSelectedLayoutIndex);

				if (checkEntryIndex == mSelectedEntryIndex) {
					mSelectedEntryIndex = checkEntryIndex;
					return;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false)
					continue;

				mSelectedEntryIndex = checkEntryIndex;
				return;
			}
		}

		return;
	}

	protected void getPreviousEnabledLayout() {
		if (mActiveEntry != null)
			return;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (mLayouts.size() == 1) {
			mSelectedLayoutIndex = 0;
			return;
		}

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex--;

			if (selectedLayoutIndex == mSelectedLayoutIndex)
				return;

			if (selectedLayoutIndex < 0) {
				selectedLayoutIndex = mLayouts.size() - 1;

				final var lLayout = mLayouts.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex--;
					continue;
				}

				mSelectedLayoutIndex = selectedLayoutIndex;
				return;
			}

			final var lLayout = mLayouts.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex--;
				continue;
			}

			mSelectedLayoutIndex = selectedLayoutIndex;
			return;

		}

		return;
	}

	protected void getNextEnabledEntry() {
		if (mActiveEntry != null)
			return;

		final var maxTries = 5;
		var currentTry = 0;

		var checkEntryIndex = mSelectedEntryIndex;

		while (currentTry < maxTries) {
			checkEntryIndex++;
			final var lLayout = mLayouts.get(mSelectedLayoutIndex);

			if (checkEntryIndex >= lLayout.entries().size()) {
				getNextEnabledLayout();
				checkEntryIndex = 0;

				final var lNextLayout = mLayouts.get(mSelectedLayoutIndex);
				if (lNextLayout.entries().get(checkEntryIndex).enabled() == false)
					continue;

				mSelectedEntryIndex = 0;
				return;

			} else {
				if (checkEntryIndex == mSelectedEntryIndex) {
					return;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false) {
					currentTry++;
					continue;
				}

				if (checkEntryIndex == mSelectedEntryIndex)
					return;

				mSelectedEntryIndex = checkEntryIndex;
				return;
			}
		}

		return;
	}

	protected void getNextEnabledLayout() {
		if (mActiveEntry != null)
			return;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (mLayouts.size() == 1) {
			mSelectedLayoutIndex = 0;
			return;
		}

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex++;

			if (selectedLayoutIndex == mSelectedLayoutIndex) {
				return;
			}

			if (selectedLayoutIndex >= mLayouts.size()) {
				selectedLayoutIndex = 0;

				final var lLayout = mLayouts.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex++;
					continue;
				}

				mSelectedLayoutIndex = selectedLayoutIndex;
				return;
			}

			final var lLayout = mLayouts.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex++;
				continue;
			}

			mSelectedLayoutIndex = selectedLayoutIndex;
			return;
		}

		return;
	}

}