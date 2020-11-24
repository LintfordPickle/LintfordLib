package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
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
	public static final float INNER_PADDING_W = 10f;
	public static final float INNER_PADDING_H = 5f;
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
	protected FontUnit mMenuHeaderFont;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public LAYOUT_ALIGNMENT layoutAlignment() {
		return mLayoutAlignment;
	}

	public void layoutAlignment(LAYOUT_ALIGNMENT pNewValue) {
		mLayoutAlignment = pNewValue;
	}

	public float uiTextScale() {
		return screenManager.UiStructureController().uiTextScaleFactor();
	}

	/** Returns a normal sized {@link FontUnit} which can be used to render general text to the screen. */
	public FontUnit font() {
		return mMenuFont;
	}

	/** Returns a medium sized {@link FontUnit} which can be used to render menu sub heading text to the screen. */
	public FontUnit fontHeader() {
		return mMenuHeaderFont;
	}

	public boolean isAnimating() {
		return mAnimationTimer > 0;
	}

	protected List<BaseLayout> layouts() {
		return mLayouts;
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

	public void menuTitle(String pNewMenuTitle) {
		mMenuTitle = pNewMenuTitle;
	}

	public String menuSubTitle() {
		return mMenuSubTitle;
	}

	public void menuOverTitle(String pNewMenuTitle) {
		mMenuOverTitle = pNewMenuTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager);

		mLayouts = new ArrayList<>();
		mFooterLayout = new ListLayout(this);

		mFloatingEntries = new ArrayList<>();

		mShowInBackground = false;

		mMenuTitle = pMenuTitle;
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

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).initialize();
		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			MenuEntry lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.initialize();

		}

		footerLayout().initialize();

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mMenuFont = pResourceManager.fontManager().getFont(FontManager.FONT_FONTNAME_TEXT);
		mMenuHeaderFont = pResourceManager.fontManager().getFont(FontManager.FONT_FONTNAME_HEADER);

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).loadGLContent(pResourceManager);
		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			MenuEntry lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.loadGLContent(pResourceManager);

		}

		footerLayout().loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).unloadGLContent();

		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			MenuEntry lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.unloadGLContent();

		}

		footerLayout().unloadGLContent();

		mMenuFont = null;
		mMenuHeaderFont = null;

	}

	@Override
	public void handleInput(LintfordCore pCore) {
		// TODO: Animations should be handled in another object
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		super.handleInput(pCore);

		if (mESCBackEnabled && pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (mLayouts.size() > 0) {
			// Respond to UP key
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_UP)) {

				if (mSelectedEntry > 0) {
					mSelectedEntry--; //
				}

				BaseLayout lLayout = mLayouts.get(mSelectedLayout);

				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				// Set focus on the new entry
				if (lLayout.menuEntries().get(mSelectedEntry).enabled()) {
					lLayout.updateFocusOffAllBut(pCore, lLayout.menuEntries().get(mSelectedEntry));
					lLayout.menuEntries().get(mSelectedEntry).hasFocus(true);
				}

				// TODO: play sound for menu entry changed

			}

			// Respond to DOWN key
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_DOWN)) {

				BaseLayout lLayout = mLayouts.get(mSelectedLayout);

				if (mSelectedEntry < lLayout.menuEntries().size() - 1) {
					mSelectedEntry++; //
				}

				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				// Set focus on the new entry
				if (lLayout.menuEntries().get(mSelectedEntry).enabled()) {
					lLayout.updateFocusOffAllBut(pCore, lLayout.menuEntries().get(mSelectedEntry));
					lLayout.menuEntries().get(mSelectedEntry).hasFocus(true);
				}

				// TODO: play sound for menu entry changed

			}

			// Process ENTER key press
			if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_ENTER)) {
				BaseLayout lLayout = mLayouts.get(mSelectedLayout);
				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				lLayout.menuEntries().get(mSelectedEntry).onClick(pCore.input());
			}

		}

		int lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			MenuEntry lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.handleInput(pCore);

		}

		// Process Mouse input
		int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			final var lLayout = mLayouts.get(i);
			lLayout.handleInput(pCore);

		}

		footerLayout().handleInput(pCore);

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (!mIsinitialized)
			return;

		final var lDeltaTime = pCore.appTime().elapsedTimeMilli();

		updateLayoutSize(pCore);

		if (mAnimationTimer > 0) {
			mAnimationTimer -= lDeltaTime;

		} else if (mClickAction.buttonUid() != -1 && !mClickAction.isConsumed()) { // something was clicked
			handleOnClick();

			mClickAction.reset();

			return;

		}

		final var lFloatingCount = mFloatingEntries.size();
		for (int i = 0; i < lFloatingCount; i++) {
			MenuEntry lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.update(pCore, this, false);

		}

		final var lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).update(pCore);

		}

		footerLayout().updateStructure();
		footerLayout().update(pCore);

	}

	public void updateLayoutSize(LintfordCore pCore) {
		if (rendererManager == null || layouts().size() == 0)
			return;

		updateLayout(pCore, layouts(), mLayoutAlignment);

		// *** FOOTER *** //
		final var lHUDController = rendererManager.uiStructureController();

		final float lInnerPaddingW = OUTER_PADDING_W * lHUDController.windowAutoScaleFactorX();

		float lWidthOfPage = lHUDController.menuFooterRectangle().width() - lInnerPaddingW * 2f;
		float lHeightOfPage = lHUDController.menuFooterRectangle().height();

		float lLeftOfPage = lHUDController.menuFooterRectangle().centerX() - lWidthOfPage / 2;
		float lTopOfPage = lHUDController.menuFooterRectangle().top();

		footerLayout().set(lLeftOfPage, lTopOfPage, lWidthOfPage, lHeightOfPage);
		footerLayout().updateStructure();

	}

	protected void updateLayout(LintfordCore pCore, List<BaseLayout> pLayoutList, LAYOUT_ALIGNMENT pAlignment) {
		if (pLayoutList == null || pLayoutList.size() == 0)
			return;

		if (rendererManager == null)
			return;

		final var lUIHUDStructureController = rendererManager.uiStructureController();
		if (lUIHUDStructureController == null)
			return;

		final int lLeftLayoutCount = pLayoutList.size();

		final float lScreenContentLeft = lUIHUDStructureController.menuMainRectangle().left();
		final float lScreenContentWidth = lUIHUDStructureController.menuMainRectangle().width();

		final float lInnerPaddingW = INNER_PADDING_W * lUIHUDStructureController.windowAutoScaleFactorX();
		final float lInnerPaddingH = INNER_PADDING_H * lUIHUDStructureController.windowAutoScaleFactorY();

		// Set the layout X and W
		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = pLayoutList.get(i);

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

			float lLayoutNewX = 0;
			switch (pAlignment) {
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
			lBaseLayout.w(lLayoutWidth);

		}

		float lLayoutHeight = lUIHUDStructureController.menuMainRectangle().height();

		// Set the layout Y and H
		float lLayoutNewY = lUIHUDStructureController.menuMainRectangle().top() + mPaddingTopNormalized;

		// See how many layouts only take what they need
		int lCountOfSharers = lLeftLayoutCount;
		int lCountOfTakers = 0;
		int heightTaken = 0;

		// FIXME: This system needs more work. It results in problems when the size of a 'taker' (FILLTYPE.TAKE_WHATS_NEEDED)
		// exceeds the size of the area within which it should appear.
		// The solution is currently to change the type of the layouts

		for (int i = 0; i < lLeftLayoutCount; i++) {
			final var lBaseLayout = pLayoutList.get(i);
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
			final var lBaseLayout = pLayoutList.get(i);

			lBaseLayout.y(lTop);

			if (lBaseLayout.layoutFillType() == FILLTYPE.TAKE_WHATS_NEEDED) {
				lBaseLayout.h(lBaseLayout.getEntryHeight() + lInnerPaddingH);
				lBaseLayout.updateStructure();
				lTop += lBaseLayout.getEntryHeight() + lInnerPaddingH;

			} else { // sharers
				lBaseLayout.h(lSizeOfEachShareElement);
				lBaseLayout.updateStructure();
				lTop += lSizeOfEachShareElement + lInnerPaddingH;

			}

		}
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		if (!mIsinitialized)
			return;

		super.draw(pCore);

		final float lMenuScreenZDepth = ZLayers.LAYER_SCREENMANAGER;

		drawMenuTitle(pCore);

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, lMenuScreenZDepth + (i * 0.001f));

		}

		footerLayout().draw(pCore, lMenuScreenZDepth + (1 * 0.001f));

		final int lNumFloatingEntries = mFloatingEntries.size();
		for (int i = 0; i < lNumFloatingEntries; i++) {
			final var lFloatingEntry = mFloatingEntries.get(i);
			lFloatingEntry.draw(pCore, this, false, lMenuScreenZDepth + (i * 0.001f));

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void drawMenuTitle(LintfordCore pCore) {
		if (mMenuTitle == null || mMenuTitle.length() == 0 && mMenuHeaderFont == null)
			return;

		final var lUiStructureController = screenManager.UiStructureController();
		final float lUiTextScale = lUiStructureController.uiTextScaleFactor();

		final var lHeaderRect = lUiStructureController.menuTitleRectangle();
		final var lHeaderFontWidth = mMenuHeaderFont.bitmap().getStringWidth(mMenuTitle, lUiTextScale);
		final var lHeaderFontHeight = mMenuHeaderFont.bitmap().fontHeight() * lUiTextScale;

		final float lMenuTitlePositionX = lHeaderRect.centerX() - lHeaderFontWidth * .5f;
		final float lMenuTitlePositionY = lHeaderRect.bottom() - lHeaderFontHeight - TITLE_PADDING_Y * lUiStructureController.windowAutoScaleFactorY();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lMenuTitlePositionX, lMenuTitlePositionY, -0.01f, screenColor, lUiTextScale);
		mMenuHeaderFont.end();

		if (mMenuFont != null) {
			mMenuFont.begin(pCore.HUD());

			{
				final float lOverTitleWidth = mMenuFont.bitmap().getStringWidth(mMenuOverTitle, lUiTextScale);

				if (mMenuOverTitle != null && mMenuOverTitle.length() > 0) {
					mMenuFont.draw(mMenuOverTitle, lHeaderRect.centerX() - lOverTitleWidth * .5f, lMenuTitlePositionY, -0.01f, screenColor, lUiTextScale);

				}
			}

			{
				final float lSubTitleWidth = mMenuFont.bitmap().getStringWidth(mMenuSubTitle, lUiTextScale);

				if (mMenuSubTitle != null && mMenuSubTitle.length() > 0) {
					mMenuFont.draw(mMenuSubTitle, lHeaderRect.centerX() - lSubTitleWidth * .5f, lMenuTitlePositionY + lHeaderFontHeight, -0.01f, screenColor, lUiTextScale);

				}
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
	public void setFocusOn(LintfordCore pCore, MenuEntry pMenuEntry, boolean pForce) {
		if (mLayouts.size() == 0)
			return; // nothing to do

		BaseLayout lLayout = mLayouts.get(mSelectedLayout);
		lLayout.updateFocusOffAllBut(pCore, pMenuEntry);

		pMenuEntry.hasFocus(true);

	}

	public void setHoveringOn(MenuEntry pMenuEntry) {
		if (mLayouts.size() == 0)
			return; // nothing to do

		// Make sure nothing else has focus?
		if (hasElementFocus())
			return;

		BaseLayout lLayout = mLayouts.get(mSelectedLayout);
		pMenuEntry.hoveredOver(true);

		int lCount = lLayout.menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!lLayout.menuEntries().get(i).equals(pMenuEntry)) {
				lLayout.menuEntries().get(i).hoveredOver(false);

			} else {
				mSelectedEntry = i;

			}
		}

	}

	public void setHoveringOffAll() {

	}

	@Override
	public void menuEntryOnClick(InputManager pInputState, int pEntryID) {
		mClickAction.setNewClick(pEntryID);
		mAnimationTimer = ANIMATION_TIMER_LENGTH * 2f;

	}

	protected abstract void handleOnClick();

	@Override
	public boolean isActionConsumed() {
		return mClickAction != null && mClickAction.isConsumed();

	}

	@Override
	public void onViewportChange(float pWidth, float pHeight) {
		super.onViewportChange(pWidth, pHeight);

		final int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			mLayouts.get(i).onViewportChange(pWidth, pHeight);

		}

	}
}