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
import net.lintford.library.screenmanager.layouts.ListLayout;

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

	protected List<MenuEntry> mFloatingEntries;
	protected List<BaseLayout> mLayouts;
	protected ListLayout mFooterLayout;
	protected LAYOUT_ALIGNMENT mLayoutAlignment = LAYOUT_ALIGNMENT.CENTER;
	protected String mMenuTitle;
	protected String mMenuOverTitle;
	protected String mMenuSubTitle;
	protected float mTitleFontHeight;
	protected int mSelectedEntry = 0;
	protected int mSelectedLayout = 0;
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

	protected List<MenuEntry> floatingEntries() {
		return mFloatingEntries;
	}

	protected ListLayout footerLayout() {
		return mFooterLayout;
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
		mFooterLayout = new ListLayout(this);

		mFloatingEntries = new ArrayList<>();

		mShowBackgroundScreens = false;

		mMenuTitle = menuTitle;
		mBlockInputInBackground = true;

		mPaddingTopNormalized = 0.f;
		mPaddingBottomNormalized = 0.f;
		mPaddingLeftNormalized = 0.f;
		mPaddingRightNormalized = 0.f;

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

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.initialize();
		}

		footerLayout().initialize();
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

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.loadResources(resourceManager);
		}

		footerLayout().loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).unloadResources();
		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.unloadResources();
		}

		footerLayout().unloadResources();

		mMenuFont = null;
		mMenuHeaderFont = null;
	}

	@Override
	public void handleInput(LintfordCore core) {
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		super.handleInput(core);

		if (mESCBackEnabled && core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (mLayouts.size() > 0) {
			// Respond to UP key
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP)) {

				if (mSelectedEntry > 0) {
					mSelectedEntry--; //
				}

				final var lLayout = mLayouts.get(mSelectedLayout);

				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				// Set focus on the new entry
				if (lLayout.getMenuEntryByIndex(mSelectedEntry).enabled()) {
					lLayout.updateFocusOffAllBut(core, lLayout.getMenuEntryByIndex(mSelectedEntry));
					lLayout.getMenuEntryByIndex(mSelectedEntry).hasFocus(true);
				}

				// TODO: play sound for menu entry changed

			}

			// Respond to DOWN key
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {
				final var lLayout = mLayouts.get(mSelectedLayout);

				if (mSelectedEntry < lLayout.getMenuEntryCount() - 1) {
					mSelectedEntry++; //
				}

				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				// Set focus on the new entry
				if (lLayout.getMenuEntryByIndex(mSelectedEntry).enabled()) {
					lLayout.updateFocusOffAllBut(core, lLayout.getMenuEntryByIndex(mSelectedEntry));
					lLayout.getMenuEntryByIndex(mSelectedEntry).hasFocus(true);
				}

				// TODO: play sound for menu entry changed

			}

			// Process ENTER key press
			if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER)) {
				final var lLayout = mLayouts.get(mSelectedLayout);
				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				lLayout.getMenuEntryByIndex(mSelectedEntry).onClick(core.input());
			}
		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.handleInput(core);
		}

		// Process Mouse input
		int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(core);
		}

		footerLayout().handleInput(core);
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

		final var lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.update(core, this, false);
		}

		final var lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).update(core);
		}

		footerLayout().updateStructure();
		footerLayout().update(core);
	}

	public void updateLayoutSize(LintfordCore core) {
		if (mRendererManager == null || mLayouts.size() == 0)
			return;

		updateLayout(core, mLayouts, mLayoutAlignment);

		// *** FOOTER *** //
		final var lHUDController = mRendererManager.uiStructureController();

		final float lInnerPaddingW = OUTER_PADDING_W * lHUDController.windowAutoScaleFactorX();

		float lWidthOfPage = lHUDController.menuFooterRectangle().width() - lInnerPaddingW * 2f;
		float lHeightOfPage = lHUDController.menuFooterRectangle().height();

		float lLeftOfPage = lHUDController.menuFooterRectangle().centerX() - lWidthOfPage / 2;
		float lTopOfPage = lHUDController.menuFooterRectangle().top();

		footerLayout().set(lLeftOfPage, lTopOfPage, lWidthOfPage, lHeightOfPage);
		footerLayout().updateStructure();
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

		final float lInnerPaddingW = INNER_PADDING_W * lUIHUDStructureController.windowAutoScaleFactorX();
		final float lInnerPaddingH = INNER_PADDING_H * lUIHUDStructureController.windowAutoScaleFactorY();

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
		float lLayoutHeight = lUIHUDStructureController.menuMainRectangle().height() - mPaddingTopNormalized;

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
				// Take whats needed, but cannot be larger than available (I guess)
				float lNewHeight = Math.min(lBaseLayout.getEntryHeight() + lInnerPaddingH, lLayoutHeight) + lBaseLayout.cropPaddingTop() + lBaseLayout.cropPaddingBottom();
				if (lBaseLayout.maxHeight() > 0 && lNewHeight > lBaseLayout.maxHeight()) {
					lNewHeight = lBaseLayout.maxHeight();
				}

				lBaseLayout.height(lNewHeight);
				lBaseLayout.updateStructure();
				lTop += lBaseLayout.getEntryHeight() + lInnerPaddingH;

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

		footerLayout().draw(core, lMenuScreenZDepth + (1 * 0.001f));

		final int lNumFloatingEntries = mFloatingEntries.size();
		for (int i = 0; i < lNumFloatingEntries; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.draw(core, this, false, lMenuScreenZDepth + (i * 0.001f));
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

	public void removeLayout(BaseLayout layoutToRemove) {

	}

	public void clearLayouts() {
		mLayouts.clear();
	}

	public BaseLayout getLayoutByIndex(int layoutIndex) {
		if (layoutIndex < 0 || layoutIndex >= mLayouts.size() - 1)
			return null;
		return mLayouts.get(layoutIndex);
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
		final float lMenuTitlePositionY = lHeaderRect.bottom() - lHeaderFontHeight - TITLE_PADDING_Y * lUiStructureController.windowAutoScaleFactorY();

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

	public boolean hasElementFocus() {
		int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			if (mLayouts.get(i).doesElementHaveFocus()) {
				return true;
			}
		}

		return false;
	}

	/** Sets the focus to a specific {@link MenuEntry}, clearing the focus from all other entries. */
	public void setFocusOn(LintfordCore core, MenuEntry menuEntry, boolean forceFocus) {
		if (mLayouts.size() == 0)
			return; // nothing to do

		BaseLayout lLayout = mLayouts.get(mSelectedLayout);
		lLayout.updateFocusOffAllBut(core, menuEntry);

		menuEntry.hasFocus(true);

	}

	public void setHoveringOn(MenuEntry menuEntry) {
		if (mLayouts.size() == 0)
			return; // nothing to do

		if (hasElementFocus())
			return;

		final var lLayout = mLayouts.get(mSelectedLayout);
		menuEntry.hoveredOver(true);

		int lCount = lLayout.getMenuEntryCount();
		for (int i = 0; i < lCount; i++) {
			if (!lLayout.getMenuEntryByIndex(i).equals(menuEntry)) {
				lLayout.getMenuEntryByIndex(i).hoveredOver(false);
			} else {
				mSelectedEntry = i;
			}
		}
	}

	public void setHoveringOffAll() {

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
}