package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.BaseLayout.FILL_TYPE;

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
	public static final int MENUSCREEN_HEADER_FONT_POINT_SIZE = 48;

	public static final float ANIMATION_TIMER_LENGTH = 130; // ms

	public static final float TITLE_PADDING_X = 10f;

	public enum ORIENTATION {
		horizontal, vertical,
	}

	public enum ALIGNMENT {
		left, center, right // top, center, bottom
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ALIGNMENT mChildAlignment;

	protected List<BaseLayout> mLayouts;
	protected String mMenuTitle;
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

	public String menuTitle() {
		return mMenuTitle;
	}

	public void menuTitle(String pNewMenuTitle) {
		mMenuTitle = pNewMenuTitle;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager);

		mLayouts = new ArrayList<>();

		mShowInBackground = false;

		mMenuTitle = pMenuTitle;

		mChildAlignment = ALIGNMENT.center;

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
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).initialise();
		}

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).loadGLContent(pResourceManager);
		}

		final String lFontPathname = mScreenManager.fontPathname();
		mMenuFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_FONT_NAME, lFontPathname, MENUSCREEN_FONT_POINT_SIZE, true);
		mMenuHeaderFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_HEADER_FONT_NAME, lFontPathname, MENUSCREEN_HEADER_FONT_POINT_SIZE, false);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).unloadGLContent();
		}

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		// TODO: Animations should be handled in another object
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		if (mESCBackEnabled && pCore.input().keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (mLayouts.size() == 0)
			return; // nothing to do

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

		// Process Mouse input
		int lLayoutCount = mLayouts.size();
		for (int i = 0; i < lLayoutCount; i++) {
			BaseLayout lLayout = mLayouts.get(i);
			lLayout.handleInput(pCore);
		}

	}

	@Override
	public void updateStructureDimensions(LintfordCore pCore) {
		Rectangle lHUDRect = mScreenManager.UIHUDController().HUDRectangle();

		// Update the component dimensions, then this layout knows how big it is
		final int lCount = layouts().size();

		float lTotalHeight = lHUDRect.h;
		float lStaticSize = 0;
		int lNumDynElements = 0;
		float lDynamicSize = 0;

		// Figure out how much vertical space the static layouts need
		for (int i = 0; i < lCount; i++) {
			BaseLayout lLayout = mLayouts.get(i);

			if (lLayout.fillType() == FILL_TYPE.STATIC) {
				if (lLayout.enabled())
					lStaticSize += lLayout.marginTop() + lLayout.height() + lLayout.marginBottom();

			} else {
				lNumDynElements++;
			}

		}

		lDynamicSize = lTotalHeight - lStaticSize;
		lNumDynElements = Math.max(1, lNumDynElements);

		for (int i = 0; i < lCount; i++) {
			BaseLayout lLayout = mLayouts.get(i);
			lLayout.w = lHUDRect.w;

			// Dynamic entries use up all the remaining space
			// TODO: Add dynamic entry weights
			if (lLayout.fillType() == FILL_TYPE.DYNAMIC) {
				lLayout.h = lDynamicSize / lNumDynElements;

			} else {
				// Static entries take their desired heights.
				lLayout.h = lLayout.getDesiredHeight();

			}

			mLayouts.get(i).updateStructureDimensions();
		}

	}

	@Override
	public void updateStructurePositions(LintfordCore pCore) {
		Rectangle lHUDRect = mScreenManager.UIHUDController().HUDRectangle();

		float lYPos = lHUDRect.top() + mPaddingTop;

		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			BaseLayout lLayout = layouts().get(i);
			if (!lLayout.enabled())
				continue;

			lYPos += lLayout.marginTop();

			// lLayout.width = lLayout.getEntryWidth();
			lLayout.x = lHUDRect.left();
			lLayout.y = lYPos;

			lYPos += lLayout.h + lLayout.marginBottom();

		}

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).updateStructurePositions();

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

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

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();

		final float MENUSCREEN_Z_DEPTH = ZLayers.LAYER_SCREENMANAGER;

		Rectangle lHUDRect = pCore.HUD().boundingRectangle();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left() + TITLE_PADDING_X, lHUDRect.top(), MENUSCREEN_Z_DEPTH, mR, mG, mB, mA, luiTextScale);
		mMenuHeaderFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, MENUSCREEN_Z_DEPTH + (i * 0.001f));

		}

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
	public void menuEntryOnClick(int pEntryID) {
		mClickAction.setNewClick(pEntryID);
		mAnimationTimer = ANIMATION_TIMER_LENGTH * 2f;
	}

	protected abstract void handleOnClick();
}
