package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.input.InputState;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.BaseLayout.LAYOUT_ALIGNMENT;
import net.lintford.library.screenmanager.layouts.BaseLayout.LAYOUT_FILL_TYPE;
import net.lintford.library.screenmanager.layouts.ListLayout;

public abstract class MenuScreen extends Screen implements EntryInteractions {

	public class ClickAction {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private int mButtonID = -1;
		private boolean mConsumed = false;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public boolean isConsumed() {
			return mConsumed;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ClickAction() {
			mConsumed = false;
		}

		public ClickAction(int pButtonID) {
			mConsumed = false;
			mButtonID = pButtonID;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public int consume() {
			mConsumed = true;
			return mButtonID;
		}

		public void setNewClick(int pEntryID) {
			mConsumed = false;
			mButtonID = pEntryID;

		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String MENUSCREEN_FONT_NAME = "MenuScreenFont";
	public static final int MENUSCREEN_FONT_POINT_SIZE = 16;
	public static final String MENUSCREEN_HEADER_FONT_NAME = "MenuScreenHeaderFont";
	public static final int MENUSCREEN_HEADER_FONT_POINT_SIZE = 76;

	public static final float ANIMATION_TIMER_LENGTH = 130; // ms

	public static final float INNER_PADDING = 10f;

	public static final float TITLE_PADDING_X = 10f;

	public enum LAYOUT_WIDTH_SIZE {
		half, full,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseLayout> mLayouts;
	protected ListLayout mFooterLayout;

	protected String mMenuTitle;
	protected String mMenuOverTitle;
	protected String mMenuSubTitle;

	protected int mSelectedEntry = 0;
	protected int mSelectedLayout = 0;

	protected float mPaddingLeft;
	protected float mPaddingRight;
	protected float mPaddingTop;
	protected float mPaddingBottom;

	protected boolean mESCBackEnabled;

	protected ClickAction mClickAction;
	protected float mAnimationTimer;

	protected FontUnit mMenuFont;
	protected FontUnit mMenuHeaderFont;

	protected float mContentHeight;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

		mShowInBackground = false;

		mMenuTitle = pMenuTitle;

		mPaddingTop = 0.0f;
		mPaddingBottom = 0f;
		mPaddingLeft = 0f;
		mPaddingRight = 0f;
		mContentHeight = 0;

		mClickAction = new ClickAction();
		mESCBackEnabled = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		super.initialise();

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).initialise();
		}

		footerLayout().initialise();

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		final String lFontPathname = mScreenManager.fontPathname();
		mMenuFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_FONT_NAME, lFontPathname, MENUSCREEN_FONT_POINT_SIZE, true, entityGroupID());
		mMenuHeaderFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_HEADER_FONT_NAME, lFontPathname, MENUSCREEN_HEADER_FONT_POINT_SIZE, false, entityGroupID());

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).loadGLContent(pResourceManager);
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

		footerLayout().unloadGLContent();

		mMenuFont.unloadGLContent();
		mMenuHeaderFont.unloadGLContent();

		mScreenManager.core().resources().fontManager().unloadFontGroup(entityGroupID());

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		// TODO: Animations should be handled in another object
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		if (mESCBackEnabled && pCore.input().keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (mLayouts.size() > 0) {
			// Respond to UP key
			if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_UP)) {

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
			if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_DOWN)) {

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
			if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_ENTER)) {
				BaseLayout lLayout = mLayouts.get(mSelectedLayout);
				if (!lLayout.hasEntry(mSelectedEntry))
					return;

				lLayout.menuEntries().get(mSelectedEntry).onClick(pCore.input());
			}

		}

		footerLayout().handleInput(pCore);

		// Process Mouse input
		int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			BaseLayout lLayout = mLayouts.get(i);
			lLayout.handleInput(pCore);
		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		updateLayouts(pCore);

		updateFooterLayout(pCore);

		final double lDeltaTime = pCore.time().elapseGameTimeMilli();

		if (mAnimationTimer > 0) {
			mAnimationTimer -= lDeltaTime;

		} else if (mClickAction.mButtonID != -1 && !mClickAction.isConsumed()) { // something was clicked
			handleOnClick();
			mClickAction.setNewClick(-1);
			return;

		}

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).update(pCore);

		}

		footerLayout().update(pCore);

	}

	public void updateLayouts(LintfordCore pCore) {
		if (mRendererManager == null || layouts().size() == 0)
			return;

		updateLayout(pCore, layouts(), LAYOUT_ALIGNMENT.left, LAYOUT_WIDTH_SIZE.full);

	}

	private void updateFooterLayout(LintfordCore pCore) {
		if (mRendererManager == null)
			return;

		UIHUDStructureController lHUDController = mRendererManager.uiHUDController();

		float lLeftOfPage = lHUDController.menuFooterRectangle().left();
		float lTopOfPage = lHUDController.menuFooterRectangle().top();

		float lWidthOfPage = lHUDController.menuFooterRectangle().width();
		float lHeightOfPage = lHUDController.menuFooterRectangle().height();

		footerLayout().x = lLeftOfPage;
		footerLayout().y = lTopOfPage;

		footerLayout().w = lWidthOfPage;
		footerLayout().h = lHeightOfPage;

		footerLayout().updateStructure();

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		super.draw(pCore);

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final float MENUSCREEN_Z_DEPTH = ZLayers.LAYER_SCREENMANAGER;

		UIHUDStructureController lUIHUDController = mRendererManager.uiHUDController();
		Rectangle lHUDRect = lUIHUDController.menuTitleRectangle();

		final float lTitleFontHeight = mMenuHeaderFont.bitmap().fontHeight();
		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left(), lHUDRect.top(), MENUSCREEN_Z_DEPTH, mR, mG, mB, mA, luiTextScale);
		mMenuHeaderFont.end();

		mMenuFont.begin(pCore.HUD());
		if (mMenuOverTitle != null && mMenuOverTitle.length() > 0)
			mMenuFont.draw(mMenuOverTitle, lHUDRect.left(), lHUDRect.top(), MENUSCREEN_Z_DEPTH, mR, mG, mB, mA, luiTextScale);
		if (mMenuSubTitle != null && mMenuSubTitle.length() > 0)
			mMenuFont.draw(mMenuSubTitle, lHUDRect.left(), lHUDRect.top() + lTitleFontHeight, MENUSCREEN_Z_DEPTH, mR, mG, mB, mA, luiTextScale);
		mMenuFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, MENUSCREEN_Z_DEPTH + (i * 0.001f));

		}

		footerLayout().draw(pCore, MENUSCREEN_Z_DEPTH + (1 * 0.001f));

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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
	public void menuEntryOnClick(InputState pInputState, int pEntryID) {
		mClickAction.setNewClick(pEntryID);
		mAnimationTimer = ANIMATION_TIMER_LENGTH * 2f;

	}

	protected abstract void handleOnClick();

	protected void updateLayout(LintfordCore pCore, List<BaseLayout> pLayoutList, LAYOUT_ALIGNMENT pAlignment, LAYOUT_WIDTH_SIZE pWidthType) {
		if (pLayoutList == null || pLayoutList.size() == 0)
			return;

		UIHUDStructureController lHUDController = mRendererManager.uiHUDController();

		final int lLeftLayoutCount = pLayoutList.size();

		final float lPageLeft = lHUDController.menuMainRectangle().left();
		final float lPageWidth = lHUDController.menuMainRectangle().width();

		float lWidthOfPage = lPageWidth - INNER_PADDING * 2f;
		if (pWidthType == LAYOUT_WIDTH_SIZE.half) {
			lWidthOfPage = lPageWidth / 2f - INNER_PADDING * 2f;
		}

		float lHeightOfPage = lHUDController.menuMainRectangle().height();

		float lLayoutNewX = 0;
		switch (pAlignment) {
		case left:
			lLayoutNewX = lPageLeft;
			break;
		case center:
			lLayoutNewX = 0 - lWidthOfPage / 2f;
			break;
		case right:
			lLayoutNewX = 0;
			break;
		}

		float lLayoutNewY = lHUDController.menuMainRectangle().top();

		// See how many layouts only take what they need
		int lCountOfSharers = lLeftLayoutCount;
		int lCountOfTakers = 0;

		int heightTaken = 0;

		for (int i = 0; i < lLeftLayoutCount; i++) {
			BaseLayout lLayout = pLayoutList.get(i);
			if (lLayout.layoutFillType() == LAYOUT_FILL_TYPE.ONLY_WHATS_NEEDED) {
				lCountOfTakers++;
				heightTaken += lLayout.getEntryHeight();

			}

		}

		lCountOfSharers -= lCountOfTakers;

		float lSizeOfEachFillElement = ((lHeightOfPage - heightTaken) / lCountOfSharers) - INNER_PADDING;

		if (lSizeOfEachFillElement < 0)
			lSizeOfEachFillElement = 10;

		float lTop = lLayoutNewY;
		for (int i = 0; i < lLeftLayoutCount; i++) {
			BaseLayout lLayout = pLayoutList.get(i);

			lLayout.x = lLayoutNewX;
			lLayout.y = lTop;

			lLayout.w = lWidthOfPage;

			if (lLayout.layoutFillType() == LAYOUT_FILL_TYPE.FAIR_SHARE) {
				lLayout.h = lSizeOfEachFillElement;

			} else {
				lLayout.h = lLayout.getEntryHeight();
			}

			lLayout.updateStructure();

			lTop += lSizeOfEachFillElement + INNER_PADDING;

		}
	}

}