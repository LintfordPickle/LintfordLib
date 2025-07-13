package net.lintfordlib.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.InputType;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.renderers.SimpleRendererManager;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.LAYOUT_WIDTH;
import net.lintfordlib.screenmanager.entries.EntryInteractions;
import net.lintfordlib.screenmanager.layouts.BaseLayout;

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

	/** The active entry is an entry which has 'captured' the input to prevent navigation away. Example: Textfield during buffered input or a dropdown box. */
	protected MenuEntry mActiveEntry;
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

	protected float mMenuHeaderPadding;

	protected float mUiScale;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public MenuEntry getFocusedEntry() {
		final var selectedLayout = mLayouts.get(mSelectedLayoutIndex);
		return selectedLayout.getMenuEntryByIndex(mSelectedEntryIndex);
	}

	public boolean isEntryActive() {
		return mActiveEntry != null;
	}

	/** Scale factor between [1,2] representing the difference of the window and the base resolution. */
	public float uiScale() {
		return mUiScale;
	}

	public LAYOUT_ALIGNMENT layoutAlignment() {
		return mLayoutAlignment;
	}

	public void layoutAlignment(LAYOUT_ALIGNMENT layoutAlignment) {
		mLayoutAlignment = layoutAlignment;
	}

	public float uiTextScale() {
		return screenManager.UiStructureController().uiTextScaleFactor();
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

	protected MenuScreen(ScreenManager screenManager, String menuTitle) {
		this(screenManager, menuTitle, null);
	}

	protected MenuScreen(ScreenManager screenManager, String menuTitle, SimpleRendererManager rendererManager) {
		super(screenManager, rendererManager);

		mLayouts = new ArrayList<>();

		mShowBackgroundScreens = false;
		mShowContextualKeyHints = true;
		mShowContextualFooterBar = true;

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

		mSelectedLayoutIndex = 0;
		mSelectedEntryIndex = 0;

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
		mTitleFontHeight = mRendererManager.sharedResources().headerFontHeight();

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

		if (mESCBackEnabled && (mScreenState == ScreenState.ACTIVE) && core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE, this)) {
			onEscPressed();
			return;
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP, this)) {
			screenManager.contextHintManager().setKeyboardHints();
			onNavigationUp(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, this)) {
			screenManager.contextHintManager().setGamePadHints();
			onNavigationUp(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN, this)) {
			screenManager.contextHintManager().setKeyboardHints();
			onNavigationDown(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, this)) {
			screenManager.contextHintManager().setGamePadHints();
			onNavigationDown(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_LEFT, this)) {
			screenManager.contextHintManager().setKeyboardHints();
			onNavigationLeft(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, this)) {
			screenManager.contextHintManager().setGamePadHints();
			onNavigationLeft(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_RIGHT, this)) {
			screenManager.contextHintManager().setKeyboardHints();
			onNavigationRight(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, this)) {
			screenManager.contextHintManager().setGamePadHints();
			onNavigationRight(core, InputType.Gamepad);
		}

		final var lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(core);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_B, this)) {
			onNavigationBack(core, InputType.Gamepad);
		}

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER, this)) {
			onNavigationConfirm(core, InputType.Keyboard);
		}

		if (core.input().gamepads().isGamepadButtonDownTimed(GLFW.GLFW_GAMEPAD_BUTTON_A, this)) {
			onNavigationConfirm(core, InputType.Gamepad);
		}
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (!mIsinitialized)
			return;

		final var lDisplayConfig = core.config().display();
		mUiScale = MathHelper.scaleToRange(lDisplayConfig.windowHeight(), 500, 960, 1, 2);

		if (!coveredByOtherScreen) {

			if (mESCBackEnabled) {
				screenManager.contextHintManager().screenManagerHintState().buttonB = true;
				screenManager.contextHintManager().screenManagerHintState().buttonBHint = "back";
				screenManager.contextHintManager().screenManagerHintState().keyEsc = true;
				screenManager.contextHintManager().screenManagerHintState().keyEscHint = "back";
			} else {
				screenManager.contextHintManager().screenManagerHintState().buttonB = false;
				screenManager.contextHintManager().screenManagerHintState().buttonBHint = null;
				screenManager.contextHintManager().screenManagerHintState().keyEsc = false;
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
		if (mRendererManager == null || mLayouts.isEmpty())
			return;

		updateLayout(core, mLayouts, mLayoutAlignment);
	}

	protected void updateLayout(LintfordCore core, List<BaseLayout> layoutList, LAYOUT_ALIGNMENT alignment) {
		if (layoutList == null || layoutList.isEmpty())
			return;

		if (mRendererManager == null)
			return;

		final var lUIHUDStructureController = mRendererManager.uiStructureController();
		if (lUIHUDStructureController == null)
			return;

		final var lLayoutCount = layoutList.size();
		final var lScreenContentWidth = lUIHUDStructureController.menuMainRectangle().width();

		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);

			// Layout size gets
			float lLayoutWidth = lScreenContentWidth - mLayoutPaddingHorizontal * 2f;
			if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THREEQUARTER) {
				if (lBaseLayout.maxWidthDefined())
					lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f * 3f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
				else
					lLayoutWidth = lScreenContentWidth / 4f * 3f - mLayoutPaddingHorizontal * 2f;

			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.TWOTHIRD) {
				if (lBaseLayout.maxWidthDefined())
					lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f * 2f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
				else
					lLayoutWidth = lScreenContentWidth / 3f * 2f - mLayoutPaddingHorizontal * 2f;

			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.HALF) {
				if (lBaseLayout.maxWidthDefined())
					lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 2f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
				else
					lLayoutWidth = lScreenContentWidth / 2f - mLayoutPaddingHorizontal * 2f;

			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.THIRD) {
				if (lBaseLayout.maxWidthDefined())
					lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 3f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
				else
					lLayoutWidth = lScreenContentWidth / 3f - mLayoutPaddingHorizontal * 2f;

			} else if (lBaseLayout.layoutWidth() == LAYOUT_WIDTH.QUARTER) {
				if (lBaseLayout.maxWidthDefined())
					lLayoutWidth = MathHelper.clamp(lScreenContentWidth / 4f - mLayoutPaddingHorizontal * 2f, 0.f, lBaseLayout.maxWidth());
				else
					lLayoutWidth = lScreenContentWidth / 4f - mLayoutPaddingHorizontal * 2f;

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
		int lCountOfSharers = lLayoutCount;
		int lCountOfTakers = 0;
		int heightTaken = 0;

		for (int i = 0; i < lLayoutCount; i++) {
			final var lBaseLayout = layoutList.get(i);
			if (lBaseLayout.layoutFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lCountOfTakers++;
				final var lTitleHeight = lBaseLayout.titleBarSize();
				heightTaken += lBaseLayout.getEntryHeight() + lTitleHeight;
			}
		}

		lCountOfSharers -= lCountOfTakers;

		// Split the remaining height between the shares
		int lSizeOfEachShareElement = 0;
		if (lCountOfSharers > 0) {
			lSizeOfEachShareElement = (int) Math.floor(((lLayoutHeight - heightTaken) / lCountOfSharers) - mLayoutPaddingVertical * (lCountOfSharers + 1));
		}

		float lTop = lLayoutNewY;
		for (int i = 0; i < lLayoutCount; i++) {
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

				lTop += lBaseLayout.getEntryHeight() + lBaseLayout.marginBottom() + lTitleHeight + mLayoutPaddingVertical;

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

	/** Given a MenuEntry instance, it will search the current MenuScreen for a match and then set the selectedENtry/selectedLayout variables accordingly. If the entry is not found, then nothing it changed. */
	public void resolveSelectedEntry(MenuEntry entry) {
		final var numLayouts = mLayouts.size();
		for (int i = 0; i < numLayouts; i++) {
			final var layout = mLayouts.get(i);
			final var numEntries = layout.entries().size();
			for (int j = 0; j < numEntries; j++) {
				final var checkEntry = layout.entries().get(j);
				if (checkEntry == entry || checkEntry.resolveChildEntry(entry)) {
					mSelectedLayoutIndex = i;
					mSelectedEntryIndex = j;
					return;
				}
			}
		}
		return;
	}

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

		final var lUiStructureController = screenManager.UiStructureController();
		final float lUiTextScale = lUiStructureController.uiTextScaleFactor();

		final var lHeaderRect = lUiStructureController.menuTitleRectangle();
		final var lHeaderFontWidth = mMenuHeaderFont.getStringWidth(mMenuTitle, lUiTextScale);
		final var lHeaderFontHeight = mMenuHeaderFont.fontHeight() * lUiTextScale;

		final float lMenuTitlePositionX = lHeaderRect.centerX() - lHeaderFontWidth * .5f;
		final float lMenuTitlePositionY = lHeaderRect.top() + lHeaderRect.height() * 0.5f;//

		mMenuHeaderFont.begin(core.HUD());

		mMenuHeaderFont.setTextColorA(screenColor.a);
		mMenuHeaderFont.setShadowColorA(screenColor.a);

		mMenuFont.setTextColorA(screenColor.a);
		mMenuFont.setShadowColorA(screenColor.a);

		mMenuHeaderFont.drawShadowedText(mMenuTitle, screenPositionOffset().x + lMenuTitlePositionX, screenPositionOffset().y + lMenuTitlePositionY, .01f, 2.f, 2.f, 1.f);
		mMenuHeaderFont.end();

		mMenuFont.begin(core.HUD());

		mMenuFont.setTextColor(ColorConstants.TextHeadingColor);

		final float lOverTitleWidth = mMenuFont.getStringWidth(mMenuOverTitle, lUiTextScale);
		if (mMenuOverTitle != null && mMenuOverTitle.length() > 0) {
			mMenuFont.drawShadowedText(mMenuOverTitle, screenPositionOffset().x + lHeaderRect.centerX() - lOverTitleWidth * .5f, screenPositionOffset().y + lMenuTitlePositionY - mMenuHeaderFont.fontHeight() / 2, .01f, 1.f, 1.f, 1.f);
		}

		final float lSubTitleWidth = mMenuFont.getStringWidth(mMenuSubTitle, lUiTextScale);
		if (mMenuSubTitle != null && mMenuSubTitle.length() > 0) {
			mMenuFont.drawShadowedText(mMenuSubTitle, screenPositionOffset().x + lHeaderRect.centerX() - lSubTitleWidth * .5f, screenPositionOffset().y + lMenuTitlePositionY + lHeaderFontHeight, .01f, 1.f, 1.f, 1.f);
		}

		mMenuFont.end();
	}

	protected void onCancel() {
		exitScreen();
	}

	/** This is called when an entry is clicked.This is called regardless (and including) if an entry was registered as a click listener. */
	public void menuEntryOnClick(InputManager inputState, MenuEntry entry) {

	}

	/** This is called when a previously registered entry (with an entryUid) is clicked. */
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

	protected void onEscPressed() {
		exitScreen();
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

	// MOUSE

	public void setFocusOnEntry(MenuEntry entry) {
		screenManager.contextHintManager().contextHintProvider(null);

		if (mActiveEntry != null) {
			mActiveEntry.onDeactivation(screenManager.core().input());
			mActiveEntry = null;
		}

		final int lNumLayouts = mLayouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lLayout = mLayouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var IsDesiredEntry = lEntry == entry;

				if (IsDesiredEntry) {
					lEntry.mHasFocus = true;
					screenManager.contextHintManager().contextHintProvider(lEntry);

					mSelectedLayoutIndex = i;
					mSelectedEntryIndex = j;

				} else {

					if (lEntry.setFocusOnChildEntry(entry)) {
						lEntry.mHasFocus = true;
						screenManager.contextHintManager().contextHintProvider(lEntry);

						mSelectedLayoutIndex = i;
						mSelectedEntryIndex = j;
					} else {
						lEntry.mHasFocus = false;
					}
				}
			}
		}
	}

	public void deactivateAllEntries() {

	}

	protected MenuEntry getSelectedEntry(List<BaseLayout> selectedLayouts, int selectedLayoutIndex, int selectedEntryIndex) {
		if (selectedLayouts == null || selectedLayouts.isEmpty())
			return null;

		final var lLayout = selectedLayouts.get(selectedLayoutIndex);
		return lLayout.getMenuEntryByIndex(selectedEntryIndex);
	}

	protected void updateAllEntriesToMatchSelected(List<BaseLayout> layouts, int selectedLayoutIndex, int selectedEntryIndex, boolean focusSelected) {
		final int lNumLayouts = layouts.size();
		for (int i = 0; i < lNumLayouts; i++) {
			final var lIsLayoutSelected = focusSelected && i == selectedLayoutIndex;
			final var lLayout = layouts.get(i);
			final int lNumEntries = lLayout.entries().size();
			for (int j = 0; j < lNumEntries; j++) {
				final var lEntry = lLayout.entries().get(j);
				final var lIsEntrySelected = lIsLayoutSelected && j == selectedEntryIndex;

				if (lIsEntrySelected) {
					lEntry.hasFocus(true);
					screenManager.contextHintManager().contextHintProvider(lEntry);
				} else {
					lEntry.hasFocus(false);
				}
			}
		}
	}

	// NAVIGATION ---------------------------

	protected void onNavigationBack(LintfordCore core, InputType inputType) {
		System.out.println("nav back");

		if (mActiveEntry != null)
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

	}

	protected void onNavigationConfirm(LintfordCore core, InputType inputType) {
		System.out.println("nav confirm");

		if (mActiveEntry != null)
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		final var lEntry = getSelectedEntry(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex);

		if (lEntry != null)
			lEntry.onClick(core.input());

	}

	protected void scrollItemIntoLayoutView() {
		final var selectedLayout = mLayouts.get(mSelectedLayoutIndex);
		selectedLayout.scrollContentItemIntoView(mSelectedEntryIndex);
	}

	protected void onNavigationUp(LintfordCore core, InputType inputType) {
		System.out.println("nav up");

		if (mActiveEntry != null)
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		getPreviousEnabledEntry();

		scrollItemIntoLayoutView();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, true);

		screenManager.toolTip().toolTipProvider(null);

		screenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_UP);
	}

	protected void onNavigationDown(LintfordCore core, InputType inputType) {
		System.out.println("nav down");

		if (mActiveEntry != null)
			return;

		if (inputType == InputType.Gamepad && !acceptGamepadInput)
			return;

		if (inputType == InputType.Keyboard && !acceptKeyboardInput)
			return;

		core.input().mouse().isMouseMenuSelectionEnabled(false);
		getNextEnabledEntry();

		scrollItemIntoLayoutView();

		updateAllEntriesToMatchSelected(mLayouts, mSelectedLayoutIndex, mSelectedEntryIndex, true);

		screenManager.toolTip().toolTipProvider(null);

		screenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_NAVIGATION_DOWN);
	}

	protected void onNavigationLeft(LintfordCore core, InputType inputType) {
		System.out.println("nav left");

		// different from vertical navigation, left/right navigation can be used to switch between items within container entries (like horizontal button groups).

		final var selectedEntry = mLayouts.get(mSelectedLayoutIndex).entries().get(mSelectedEntryIndex);
		selectedEntry.onNavigationLeft(core);

		scrollItemIntoLayoutView();

	}

	protected void onNavigationRight(LintfordCore core, InputType inputType) {
		System.out.println("nav right");

		// different from vertical navigation, left/right navigation can be used to switch between items within container entries (like horizontal button groups).

		final var selectedEntry = mLayouts.get(mSelectedLayoutIndex).entries().get(mSelectedEntryIndex);
		selectedEntry.onNavigationRight(core);

		scrollItemIntoLayoutView();

	}

	protected void getPreviousEnabledEntry() {
		if (mActiveEntry != null)
			return;

		var checkEntryIndex = mSelectedEntryIndex;

		if (mSelectedLayoutIndex >= mLayouts.size())
			mSelectedLayoutIndex = 0;

		if (mLayouts.isEmpty())
			return;

		while (true) {
			checkEntryIndex--;

			if (checkEntryIndex < 0) {
				if (mLayouts.size() > 1) {
					getPreviousEnabledLayout();

					// whatever layout is now active, go with it
					final var layout = mLayouts.get(mSelectedLayoutIndex);

					if (layout.entries().isEmpty()) {
						mSelectedEntryIndex = 0;
						return;
					}

					checkEntryIndex = layout.entries().size() - 1;

					if (checkEntryIndex == mSelectedEntryIndex)
						return;

					if (!layout.hasEntry(checkEntryIndex)) {
						mSelectedEntryIndex = 0;
						return;
					}

					mSelectedEntryIndex = checkEntryIndex;
					return;

				} else {
					final var layout = mLayouts.get(mSelectedLayoutIndex);
					checkEntryIndex = layout.entries().size() - 1;

					if (layout.entries().isEmpty()) {
						mSelectedEntryIndex = 0;
						return;
					}

					if (checkEntryIndex == mSelectedEntryIndex)
						return;

					final var foundEntry = layout.entries().get(checkEntryIndex);
					if (!foundEntry.enabled() || !foundEntry.canHaveFocus())
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

				final var foundEntry = lLayout.entries().get(checkEntryIndex);
				if (!foundEntry.enabled() || !foundEntry.canHaveFocus())
					continue;

				mSelectedEntryIndex = checkEntryIndex;
				return;
			}
		}
	}

	protected void getPreviousEnabledLayout() {
		if (mActiveEntry != null)
			return;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (mLayouts.size() == 1) {
			mSelectedLayoutIndex = 0;
			return;
		}

		while (true) {
			selectedLayoutIndex--;

			if (selectedLayoutIndex == mSelectedLayoutIndex)
				return;

			if (selectedLayoutIndex < 0) {
				selectedLayoutIndex = mLayouts.size() - 1;

				final var lLayout = mLayouts.get(selectedLayoutIndex);
				if (lLayout.entries().isEmpty()) {
					selectedLayoutIndex--;
					continue;
				}

				mSelectedLayoutIndex = selectedLayoutIndex;
				return;
			}

			final var lLayout = mLayouts.get(selectedLayoutIndex);
			if (lLayout.entries().isEmpty()) {
				selectedLayoutIndex--;
				continue;
			}

			mSelectedLayoutIndex = selectedLayoutIndex;
			return;

		}
	}

	protected void getNextEnabledEntry() {
		if (mActiveEntry != null)
			return;

		final var maxTries = 5;
		var currentTry = 0;

		var checkEntryIndex = mSelectedEntryIndex;

		if (mLayouts.isEmpty())
			return;

		while (currentTry < maxTries) {
			checkEntryIndex++;
			final var layout = mLayouts.get(mSelectedLayoutIndex);

			if (checkEntryIndex >= layout.entries().size()) {
				getNextEnabledLayout();
				checkEntryIndex = 0;

				final var lNextLayout = mLayouts.get(mSelectedLayoutIndex);
				final var foundEntry = lNextLayout.entries().get(checkEntryIndex);
				if (!foundEntry.enabled() || !foundEntry.canHaveFocus())
					continue;

				mSelectedEntryIndex = 0;
				return;

			} else {
				if (checkEntryIndex == mSelectedEntryIndex) {
					return;
				}

				final var foundEntry = layout.entries().get(checkEntryIndex);
				if (!foundEntry.enabled() || !foundEntry.canHaveFocus()) {
					currentTry++;
					continue;
				}

				mSelectedEntryIndex = checkEntryIndex;
				return;
			}
		}
	}

	protected void getNextEnabledLayout() {
		if (mActiveEntry != null)
			return;

		int selectedLayoutIndex = mSelectedLayoutIndex;

		if (mLayouts.size() == 1) {
			mSelectedLayoutIndex = 0;
			return;
		}

		while (true) {
			selectedLayoutIndex++;

			if (selectedLayoutIndex == mSelectedLayoutIndex) {
				return;
			}

			if (selectedLayoutIndex >= mLayouts.size()) {
				selectedLayoutIndex = 0;

				final var lLayout = mLayouts.get(selectedLayoutIndex);
				if (lLayout.entries().isEmpty()) {
					selectedLayoutIndex++;
					continue;
				}

				mSelectedLayoutIndex = selectedLayoutIndex;
				return;
			}

			final var lLayout = mLayouts.get(selectedLayoutIndex);
			if (lLayout.entries().isEmpty()) {
				selectedLayoutIndex++;
				continue;
			}

			mSelectedLayoutIndex = selectedLayoutIndex;
			return;
		}
	}
}