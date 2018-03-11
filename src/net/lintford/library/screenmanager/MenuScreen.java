package net.lintford.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public abstract class MenuScreen extends Screen implements IMenuEntryClickListener {

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

	private static final float Z_DEPTH = -1f;

	public static final String MENUSCREEN_FONT_NAME = "MenuScreenFont";
	public static final String MENUSCREEN_HEADER_FONT_NAME = "MenuScreenHeaderFont";

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

	protected ORIENTATION mOrientation;
	protected ALIGNMENT mChildAlignment;

	protected List<BaseLayout> mLayouts;
	protected String mMenuTitle;
	protected int mSelectedEntry = 0;
	protected int mSelectedLayout = 0;

	protected float mLeftMargin;
	protected float mRightMargin;
	protected float mTopMargin;
	protected float mBottomMargin;

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager);

		mLayouts = new ArrayList<>();
		mShowInBackground = false;

		mMenuTitle = pMenuTitle;

		mOrientation = ORIENTATION.vertical;
		mChildAlignment = ALIGNMENT.center;

		mTopMargin = 170.0f;
		mBottomMargin = 80f;
		mLeftMargin = 80f;
		mRightMargin = 80f;
		mContentHeight = 0;

		mClickAction = new ClickAction();
		mESCBackEnabled = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------<

	@Override
	public void initialise() {
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).initialise();
		}

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).loadGLContent(pResourceManager);
		}

		final String lFontPathname = mScreenManager.fontPathname();
		mMenuFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_FONT_NAME, lFontPathname, 24, true);
		mMenuHeaderFont = pResourceManager.fontManager().loadNewFont(MENUSCREEN_HEADER_FONT_NAME, lFontPathname, 35, false);

	}

	@Override
	public void unloadGLContent() {
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
				lLayout.setFocusOffAll(pCore.input().mouseLeftClick());
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
				lLayout.setFocusOffAll(pCore.input().mouseLeftClick());
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
	public void updateStructure(LintfordCore pCore) {
		AARectangle lHUDRect = pCore.HUD().boundingRectangle();

		if (mOrientation == ORIENTATION.vertical) {
			float lYPos = lHUDRect.top() + mTopMargin;

			final int lLayoutCount = layouts().size();
			for (int i = 0; i < lLayoutCount; i++) {
				// TODO: Ignore floating layouts
				BaseLayout lLayout = layouts().get(i);

				// lLayout.width = lLayout.getEntryWidth();
				lLayout.h = lLayout.getHeight();

				lYPos += lLayout.paddingTop();

				switch (mChildAlignment) {
				case left:
					lLayout.x = lHUDRect.left() + lLayout.paddingLeft();
					break;
				case center:
					lLayout.x = lHUDRect.left() + lHUDRect.w / 2 - lLayout.w / 2;
					break;
				case right:
					lLayout.x = lHUDRect.right() - lLayout.w - lLayout.paddingRight();
					break;
				}

				lLayout.y = lYPos;

				lYPos += lLayout.h + lLayout.paddingBottom();

			}

			mContentHeight = lYPos - lHUDRect.top() - mTopMargin;
		}

		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).updateStructure();

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

		AARectangle lHUDRect = pCore.HUD().boundingRectangle();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left() + TITLE_PADDING_X, lHUDRect.top(), -0f, mR, mG, mB, mA, 1f);
		mMenuHeaderFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, Z_DEPTH);

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

	public void setFocusOn(InputState pInputState, MenuEntry pMenuEntry, boolean pForce) {

		if (mLayouts.size() == 0)
			return; // nothing to do

		BaseLayout lLayout = mLayouts.get(mSelectedLayout);

		lLayout.setFocusOffAll(pInputState.mouseLeftClick());
		// Set focus to this entry
		pMenuEntry.onClick(pInputState);

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
				lLayout.menuEntries().get(i).hasFocus(false);
				lLayout.menuEntries().get(i).hoveredOver(false);
			} else {
				mSelectedEntry = i;
			}
		}

	}

	public void setHoveringOffAll() {

	}

	public void onClick(int pEntryID) {
		mClickAction.setNewClick(pEntryID);
		mAnimationTimer = ANIMATION_TIMER_LENGTH;
	}

	protected abstract void handleOnClick();
}
