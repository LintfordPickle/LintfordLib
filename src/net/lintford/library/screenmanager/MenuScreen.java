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

	public static final float OUTER_PADDING_W = 50f;
	public static final float OUTER_PADDING_H = 5f;
	public static final float INNER_PADDING_W = 5f;
	public static final float INNER_PADDING_H = 2f;
	public static final float TITLE_PADDING_X = 10f;
	public static final float TITLE_PADDING_Y = 20f;

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

	protected float mPaddingLeftNormalized;
	protected float mPaddingRightNormalized;
	protected float mPaddingTopNormalized;
	protected float mPaddingBottomNormalized;
	protected boolean mESCBackEnabled;
	protected ClickAction mClickAction;
	protected float mAnimationTimer;
	protected FontUnit mMenuFont;
	protected FontUnit mMenuFontBold;
	protected FontUnit mMenuHeaderFont;
	protected float mMenuScreenWidthScaleFactor;
	protected float mMenuScreenHeightScaleFactor;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

		mMenuTitle = menuTitle;
		mBlockInputInBackground = true;

		mPaddingTopNormalized = 0.f;
		mPaddingBottomNormalized = 0.f;
		mPaddingLeftNormalized = 0.f;
		mPaddingRightNormalized = 0.f;

		mMenuScreenWidthScaleFactor = 1.f;
		mMenuScreenHeightScaleFactor = 1.f;

		mClickAction = new ClickAction();
		mESCBackEnabled = true;
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

		updateAllEntriesToMatchSelected(mLayouts, true);
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
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_B)) {
				if (mScreenState == ScreenState.Active) {
					exitScreen();
					return;
				}
			}
		}

		// TODO :Check for gamepad axis for each of the cardinal directions ..
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP)) {
			onNavigationUp(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN)) {
			onNavigationDown(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT)) {
			onNavigationLeft(core);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) {
			onNavigationRight(core);
		}

		// Process ENTER key press
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER) || core.input().gamepads().isGamepadButtonDown(GLFW.GLFW_GAMEPAD_BUTTON_A)) {
			final var lEntry = getSelectedEntry(mLayouts);
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

		final float lScreenContentWidth = lUIHUDStructureController.menuMainRectangle().width() * mMenuScreenWidthScaleFactor;

		final float lInnerPaddingW = INNER_PADDING_W * lUIHUDStructureController.gameCanvasWScaleFactor();
		final float lInnerPaddingH = INNER_PADDING_H * lUIHUDStructureController.gameCanvasHScaleFactor();

		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);

			float lLayoutWidth = lScreenContentWidth - lInnerPaddingW * 2f;
			if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THREEQUARTER) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f * 3f - lInnerPaddingW * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.TWOTHIRD) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f * 2f - lInnerPaddingW * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.HALF) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 2f - lInnerPaddingW * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THIRD) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f - lInnerPaddingW * 2f, 0.f, lBaseLayout.maxWidth());
			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.QUARTER) {
				lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f - lInnerPaddingW * 2f, 0.f, lBaseLayout.maxWidth());
			}

			final float lScreenContentLeft = lUIHUDStructureController.menuMainRectangle().centerX() - lInnerPaddingW - lLayoutWidth;

			float lLayoutNewX = 0;
			switch (alignment) {
			case LEFT:
				lLayoutNewX = lScreenContentLeft + lInnerPaddingW;
				break;
			case CENTER:
				lLayoutNewX = 0 - lLayoutWidth / 2f;
				break;
			case RIGHT:
				lLayoutNewX = 0 + lInnerPaddingW;
				break;
			}

			lBaseLayout.x(lLayoutNewX);
			lBaseLayout.width(lLayoutWidth);
		}

		// Set the layout Y and H
		float lLayoutNewY = lUIHUDStructureController.menuMainRectangle().top() + mPaddingTopNormalized;
		float lLayoutHeight = (lUIHUDStructureController.menuMainRectangle().height() - mPaddingTopNormalized) * mMenuScreenHeightScaleFactor;

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

		// Split the remainging height between the shares
		float lSizeOfEachShareElement = 0.f;
		if (lCountOfSharers > 0) {
			lSizeOfEachShareElement = ((lLayoutHeight - heightTaken) / lCountOfSharers) - lInnerPaddingH * (lCountOfSharers + 1);
		}

		float lTop = lLayoutNewY;
		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);

			lBaseLayout.y(lTop);

			if (lBaseLayout.layoutFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				// Take whats needed, but cannot be larger than available
				float lNewHeight = Math.min(lBaseLayout.getEntryHeight() + lInnerPaddingH, lLayoutHeight) + lBaseLayout.cropPaddingTop() + lBaseLayout.cropPaddingBottom();
				if (lBaseLayout.maxHeight() > 0 && lNewHeight > lBaseLayout.maxHeight()) {
					lNewHeight = lBaseLayout.maxHeight();
				}

				lBaseLayout.height(lNewHeight);
				lBaseLayout.updateStructure();
				lTop += lBaseLayout.getEntryHeight() + lInnerPaddingH;

//			} else if (lBaseLayout.layoutFillType() == FILLTYPE.FOOTER) {
//				final var lHUDController = mRendererManager.uiStructureController();
//				final var lWidthOfPage = lHUDController.menuFooterRectangle().width() - lInnerPaddingW * 2f;
//				final var lHeightOfPage = lHUDController.menuFooterRectangle().height();
//
//				final var lLeftOfPage = lHUDController.menuFooterRectangle().centerX() - lWidthOfPage / 2;
//				final var lTopOfPage = lHUDController.menuFooterRectangle().top();
//
//				lBaseLayout.set(lLeftOfPage, lTopOfPage, lWidthOfPage, lHeightOfPage);
//				lBaseLayout.updateStructure();

			} else { // sharers
				float lNewHeight = lSizeOfEachShareElement;
				if (lBaseLayout.maxHeight() != -1 && lNewHeight > lBaseLayout.maxHeight()) {
					lNewHeight = lBaseLayout.maxHeight();
				}
				lBaseLayout.height(lNewHeight);
				lBaseLayout.updateStructure();
				lTop += lSizeOfEachShareElement + lInnerPaddingH;
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
		final float lMenuTitlePositionY = lHeaderRect.top() + 5 * lUiStructureController.gameCanvasHScaleFactor();

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
		mAnimationTimer = ANIMATION_TIMER_LENGTH * 2f;
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
		System.out.println("onMenuEntryActivated");
		mActiveEntry = activeEntry;
	}

	@Override
	public void onMenuEntryDeactivated(MenuEntry activeEntry) {
		System.out.println("onMenuEntryDeactivated");
		mActiveEntry = null;
	}

	public void setFocusOnEntry(MenuEntry entry) {
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

	protected MenuEntry getSelectedEntry(List<BaseLayout> selectedLayouts) {
		final var lLayout = selectedLayouts.get(mSelectedLayoutIndex);
		return lLayout.getMenuEntryByIndex(mSelectedEntryIndex);
	}

	protected void updateAllEntriesToMatchSelected(List<BaseLayout> layouts, boolean focusSelected) {
		final int lNumLayouts = layouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lIsLayoutSelected = focusSelected & i == mSelectedLayoutIndex;
			final var lLayout = layouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var lIsEntrySelected = lIsLayoutSelected && j == mSelectedEntryIndex;

				if (lIsEntrySelected) {
					lEntry.mHasFocus = true;
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

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		mSelectedEntryIndex = getPreviousEnabledEntry(mLayouts, mSelectedEntryIndex);

		updateAllEntriesToMatchSelected(mLayouts, true);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	protected void onNavigationDown(LintfordCore core) {
		if (mActiveEntry != null)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		mSelectedEntryIndex = getNextEnabledEntry(mLayouts, mSelectedEntryIndex);

		updateAllEntriesToMatchSelected(mLayouts, true);

		mScreenManager.toolTip().toolTipProvider(null);

		// TODO: play sound for menu entry changed
	}

	protected void onNavigationLeft(LintfordCore core) {

	}

	protected void onNavigationRight(LintfordCore core) {

	}

	protected int getPreviousEnabledEntry(List<BaseLayout> layouts, int currentEntryIndex) {
		if (mActiveEntry != null)
			return mSelectedEntryIndex;

		boolean found = false;

		var checkEntryIndex = currentEntryIndex;

		if (mSelectedLayoutIndex >= layouts.size())
			mSelectedLayoutIndex = 0;

		while (found == false) {
			checkEntryIndex--;

			if (checkEntryIndex < 0) {
				if (layouts.size() > 1) {
					mSelectedLayoutIndex = getPreviousEnabledLayout(layouts, mSelectedLayoutIndex);

					// whatever layout is now active, go with it
					final var lLayout = mLayouts.get(mSelectedLayoutIndex);

					if (lLayout.entries().size() == 0)
						return 0;

					checkEntryIndex = lLayout.entries().size() - 1;

					if (checkEntryIndex == currentEntryIndex)
						return currentEntryIndex;

					if (!lLayout.hasEntry(checkEntryIndex))
						return 0;

					return checkEntryIndex;

				} else {
					final var lLayout = layouts.get(mSelectedLayoutIndex);
					checkEntryIndex = lLayout.entries().size() - 1;

					if (lLayout.entries().size() == 0)
						return 0;

					if (checkEntryIndex == currentEntryIndex)
						return currentEntryIndex;

					final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
					if (lFoundEntry.enabled() == false)
						continue;

					return checkEntryIndex;
				}
			} else {
				final var lLayout = layouts.get(mSelectedLayoutIndex);

				if (checkEntryIndex == currentEntryIndex) {
					return currentEntryIndex;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false)
					continue;

				return checkEntryIndex;
			}
		}

		return currentEntryIndex;
	}

	protected int getPreviousEnabledLayout(List<BaseLayout> layouts, int layoutIndex) {
		if (mActiveEntry != null)
			return mSelectedLayoutIndex;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (layouts.size() == 1)
			return 0;

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex--;

			if (selectedLayoutIndex == mSelectedLayoutIndex)
				return mSelectedLayoutIndex;

			if (selectedLayoutIndex < 0) {
				selectedLayoutIndex = layouts.size() - 1;

				final var lLayout = layouts.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex--;
					continue;
				}

				return selectedLayoutIndex;
			}

			final var lLayout = layouts.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex--;
				continue;
			}

			return selectedLayoutIndex;

		}

		return mSelectedEntryIndex;
	}

	protected int getNextEnabledEntry(List<BaseLayout> layouts, int currentEntryIndex) {
		if (mActiveEntry != null)
			return mSelectedEntryIndex;

		final var maxTries = 10;
		var currentTry = 0;

		var checkEntryIndex = currentEntryIndex;

		while (currentTry < maxTries) {
			checkEntryIndex++;
			final var lLayout = layouts.get(mSelectedLayoutIndex);

			if (checkEntryIndex >= lLayout.entries().size()) {
				mSelectedLayoutIndex = getNextEnabledLayout(layouts, mSelectedLayoutIndex);
				checkEntryIndex = 0;

				final var lNextLayout = layouts.get(mSelectedLayoutIndex);
				if (lNextLayout.entries().get(checkEntryIndex).enabled() == false)
					continue;

				return 0;

			} else {
				if (checkEntryIndex == currentEntryIndex) {
					return currentEntryIndex;
				}

				final var lFoundEntry = lLayout.entries().get(checkEntryIndex);
				if (lFoundEntry.enabled() == false) {
					currentTry++;
					continue;
				}

				if (checkEntryIndex == currentEntryIndex)
					return currentEntryIndex;

				return checkEntryIndex;
			}
		}

		return mSelectedEntryIndex;
	}

	protected int getNextEnabledLayout(List<BaseLayout> layouts, int layoutIndex) {
		if (mActiveEntry != null)
			return mSelectedLayoutIndex;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (layouts.size() == 1)
			return 0;

		boolean found = false;
		while (found == false) {
			selectedLayoutIndex++;

			if (selectedLayoutIndex == mSelectedLayoutIndex)
				return mSelectedLayoutIndex;

			if (selectedLayoutIndex >= layouts.size()) {
				selectedLayoutIndex = 0;

				final var lLayout = layouts.get(selectedLayoutIndex);
				if (lLayout.entries().size() == 0) {
					selectedLayoutIndex++;
					continue;
				}

				return selectedLayoutIndex;
			}

			final var lLayout = layouts.get(selectedLayoutIndex);
			if (lLayout.entries().size() == 0) {
				selectedLayoutIndex++;
				continue;
			}

			return selectedLayoutIndex;
		}

		return mSelectedEntryIndex;
	}

}