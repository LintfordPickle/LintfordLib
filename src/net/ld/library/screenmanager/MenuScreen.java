package net.ld.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.fonts.FontUnit;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.entries.IMenuEntryClickListener;
import net.ld.library.screenmanager.entries.MenuEntry;

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

	public static final String MENUSCREEN_FONT_NAME = "MenuScreenFont";
	public static final String MENUSCREEN_TITLE_FONT_NAME = "MenuScreenTitleFont";

	public static final float ANIMATION_TIMER_LENGTH = 130; // ms

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
	protected ALIGNMENT mAlignment;

	private ArrayList<MenuEntry> mMenuEntries;
	private String mMenuTitle;
	protected int mSelectedEntry = 0;
	protected float mEntryOffsetFromTop;

	protected float mLeftMargin;
	protected float mRightMargin;
	protected float mTopMargin;
	protected float mBottomMargin;
	protected float mZ; // some windows (dialogs) need better control of the z
						// order

	protected boolean mESCBackEnabled;

	protected ClickAction mClickAction;
	protected float mAnimationTimer;

	protected FontUnit mMenuFont;
	protected FontUnit mMenuTitleFont;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public FontUnit font() {
		return mMenuFont;
	}

	public FontUnit fontTitle() {
		return mMenuTitleFont;
	}

	public boolean isAnimating() {
		return mAnimationTimer > 0;
	}

	protected List<MenuEntry> menuEntries() {
		return mMenuEntries;
	}

	protected float transitionOffset() {
		// Make the menu slide into place during transitions, using a
		// power curve to make things look more interesting (this makes
		// the movement slow down as it nears the end).
		return (float) Math.pow(mTransitionPosition, 2) * 256;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuScreen(ScreenManager pScreenManager, String pMenuTitle) {
		super(pScreenManager);

		mMenuEntries = new ArrayList<>();

		mMenuTitle = pMenuTitle;

		mOrientation = ORIENTATION.vertical;
		mAlignment = ALIGNMENT.center;

		mTopMargin = 90f;
		mBottomMargin = 80f;
		mLeftMargin = 80f;
		mRightMargin = 80f;

		mClickAction = new ClickAction();
		mESCBackEnabled = true;

		mEntryOffsetFromTop = 90;
		mZ = 2.1f; // default to 2.1

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise() {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).initialise();

		}

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			mMenuEntries.get(i).loadGLContent(pResourceManager);

		}

		mMenuFont = pResourceManager.fontManager().loadFontFromResource(MENUSCREEN_FONT_NAME, "/res/fonts/monofont.ttf", 40);
		mMenuTitleFont = pResourceManager.fontManager().loadFontFromResource(MENUSCREEN_TITLE_FONT_NAME, "/res/fonts/monofont.ttf", 84);

	}

	@Override
	public void unloadGLContent() {

	}

	@Override
	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		// TODO: Animations should be handled in another object
		if (mAnimationTimer > 0 || mClickAction.isConsumed())
			return; // don't handle input if 'animation' is playing

		if (mESCBackEnabled && pInputState.keyDownTimed(GLFW.GLFW_KEY_ESCAPE)) {
			if (mScreenState == ScreenState.Active) {
				exitScreen();
				return;
			}
		}

		if (menuEntries() == null || menuEntries().size() == 0)
			return; // nothing to do

		final int lCount = menuEntries().size();

		// Respond to UP key
		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_UP)) {
			boolean lFound = false;
			int lIterCount = 0;
			while (!lFound && lIterCount < lCount) {
				mSelectedEntry--;
				if (mSelectedEntry < 0)
					mSelectedEntry = menuEntries().size() - 1;

				// if this new button is not deactivated, then use it
				if (menuEntries().get(mSelectedEntry).enabled()) {
					for (int i = 0; i < lCount; i++) {
						menuEntries().get(i).hasFocus(false);
						menuEntries().get(i).hoveredOver(false);
					}
					menuEntries().get(mSelectedEntry).hasFocus(true);

					lFound = true; // exit while
				}

				lIterCount++;
			}

			// TODO: play sound for menu entry changed
		}

		// Respond to DOWN key
		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_DOWN)) {
			boolean lFound = false;
			int lIterCount = 0;
			while (!lFound && lIterCount < lCount) {
				mSelectedEntry++;
				if (mSelectedEntry > menuEntries().size() - 1)
					mSelectedEntry = 0;

				// if this new button is not deactivated, then use it
				if (menuEntries().get(mSelectedEntry).enabled()) {
					for (int i = 0; i < lCount; i++) {
						menuEntries().get(i).hasFocus(false);
						menuEntries().get(i).hoveredOver(false);
					}
					menuEntries().get(mSelectedEntry).hasFocus(true);

					lFound = true; // exit while
				}

				lIterCount++;
			}

			// TODO: play sound for menu entry changed
		}

		// Process ENTER key press
		if (pInputState.keyDownTimed(GLFW.GLFW_KEY_ENTER)) {

			// TODO: Play menu click sound

			menuEntries().get(mSelectedEntry).onClick(pInputState);

		}

		for (int i = 0; i < lCount; i++) {
			MenuEntry lMenuEntry = menuEntries().get(i);
			if (lMenuEntry.handleInput(pInputState))
				break;
		}
	}

	protected void updateMenuEntryLocations(RenderState pRenderState) {

		int lOffsetFromTop = (int) mEntryOffsetFromTop;

		// Make the menu slide into place during transitions, using a
		// power curve to make things look more interesting (this makes
		// the movement slow down as it nears the end).
		float lTransitionOffset = transitionOffset();

		Rectangle lHUDRect = pRenderState.hudCamera().boundingRectangle();

		float lPosY = lHUDRect.top();

		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {

			MenuEntry lEntry = menuEntries().get(i);

			float lX = 0;

			lX += lTransitionOffset;

			lEntry.x = lX - lEntry.width / 2;
			lEntry.y = lPosY + lOffsetFromTop;
			lEntry.mZ = mZ + 0.1f;

			lPosY += (lEntry.height + 35);
		}
	}

	@Override
	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pGameTime, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (mAnimationTimer > 0) {
			mAnimationTimer -= pGameTime.elapseGameTimeMilli();

		} else if (mClickAction.mButtonID != -1 && !mClickAction.isConsumed()) { // something
																					// was
																					// clicked
			handleOnClick();
			mClickAction.setNewClick(-1);
			return;

		}

		final int lCount = mMenuEntries.size();
		for (int i = 0; i < lCount; i++) {
			boolean lIsSelected = isActive() && (i == mSelectedEntry);
			mMenuEntries.get(i).update(pGameTime, this, lIsSelected);

		}

	}

	@Override
	public void draw(RenderState pRenderState) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn
				&& mScreenState != ScreenState.TransitionOff)
			return;

		// make sure our entries are in the right place before we draw them
		updateMenuEntryLocations(pRenderState);

		Rectangle lHUDRect = mScreenManager.HUD().boundingRectangle();

		mMenuTitleFont.begin(mScreenManager.HUD());
		mMenuTitleFont.draw(mMenuTitle, lHUDRect.left() + 5, lHUDRect.top() + 5, 3f, 1f, 1f, 1f, 1f, 1f);
		mMenuTitleFont.end();

		mMenuFont.begin(mScreenManager.HUD());

		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			menuEntries().get(i).draw(this, pRenderState, mSelectedEntry == i);
		}

		mMenuFont.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	protected void onCancel() {
		exitScreen();
	}

	public boolean hasElementFocus() {
		final int lLayoutCount = mMenuEntries.size();
		for (int i = 0; i < lLayoutCount; i++) {
			if (mMenuEntries.get(i).hasFocus()) {
				return true;
			}
		}

		return false;
	}

	public void setFocusOn(InputState pInputState, MenuEntry pMenuEntry, boolean pForce) {
		// Set focus to this entry
		pMenuEntry.onClick(pInputState);

		// and disable focus on the rest
		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (!menuEntries().get(i).equals(pMenuEntry)) {
				// reset other element focuses
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);
			} else {
				// Remember which element we have just focused on
				mSelectedEntry = i;
			}
		}
	}

	public void setFocusOffAll() {

	}

	public void setHoveringOn(MenuEntry pMenuEntry) {
		pMenuEntry.hoveredOver(true);

		int lCount = menuEntries().size();
		for (int i = 0; i < lCount; i++) {
			if (pMenuEntry != menuEntries().get(i)) {
				menuEntries().get(i).hasFocus(false);
				menuEntries().get(i).hoveredOver(false);

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
