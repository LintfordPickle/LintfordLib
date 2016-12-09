package net.ld.library.screenmanager.entries;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.spritebatch.SpriteBatch;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.input.InputState.INPUT_TYPES;
import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.MenuScreen;
import net.ld.library.screenmanager.Screen;
import net.ld.library.screenmanager.ScreenManager;

public class MenuEntry extends Rectangle {

	// =============================================
	// Constants
	// =============================================

	protected static final float MENUENTRY_WIDTH = 310;
	protected static final float MENUENTRY_HEIGHT = 32;
	protected static final float FOCUS_TIMER = .050f; // seconds

	public enum BUTTON_SIZE {
		tiny, narrow, normal, wide;
	}

	// =============================================
	// Variables
	// =============================================

	protected IMenuEntryClickListener mClickListener;
	
	protected ScreenManager mScreenManager;
	protected DisplayConfig mDisplayConfig;
	protected MenuScreen mParentScreen;
	protected boolean mActive; // Not drawn/updated etc.
	protected boolean mEnabled; // drawn but greyed out
	protected String mText;
	protected float mScale;
	private float mScaleCounter;
	protected int mMenuEntryID;
	protected boolean mDrawBackground;
	protected boolean mScaleOnHover;

	protected float mAnimationTimer;

	protected boolean mHoveredOver;
	protected boolean mCanHoverOver;
	protected boolean mHasFocus;
	protected boolean mFocusLocked;
	protected boolean mCanHaveFocus;
	protected float mClickTimer;
	protected BUTTON_SIZE mButtonSize = BUTTON_SIZE.normal;

	// FIXME: Get rid of this (not batching if it is one SpriteBatch per menu entry!)
	protected SpriteBatch mSpriteBatch;

	private boolean mIsInitialised, mIsLoaded;

	protected float mHorizontalPadding = 5f;
	protected float mVerticalPadding = 5f;

	boolean isAnimating;
	float animationTimeRemaining;
	public float mZ;

	// =============================================
	// Properties
	// =============================================

	public MenuScreen parentScreen() {
		return mParentScreen;
	}

	public boolean canHaveFocus() {
		return mCanHaveFocus;
	}

	public void canHaveFocus(boolean pNewValue) {
		mCanHaveFocus = pNewValue;
	}

	public boolean canHoverOver() {
		return mCanHoverOver;
	}

	public void canHoverOver(boolean pNewValue) {
		mCanHoverOver = pNewValue;
	}

	public float paddingHorizontal() {
		return mHorizontalPadding;
	}

	public float paddingVertical() {
		return mVerticalPadding;
	}

	public void entryID(int pNewValue) {
		mMenuEntryID = pNewValue;
	}

	public String entryText() {
		return mText;
	}

	public void entryText(String pNewValue) {
		mText = pNewValue;
	}

	public void buttonSize(BUTTON_SIZE pNewSize) {
		mButtonSize = pNewSize;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return MENUENTRY_HEIGHT;
	}

	public boolean hoveredOver() {
		return mHoveredOver;
	}

	public void hoveredOver(boolean pNewValue) {
		mHoveredOver = pNewValue;
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean pNewValue) {
		mHasFocus = pNewValue;
	}

	public boolean hasFocusLock() {
		return mFocusLocked;
	}

	public void hasFocusLock(boolean pNewValue) {
		mFocusLocked = pNewValue;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean pEnabled) {
		mEnabled = pEnabled;
	}

	public boolean active() {
		return mActive;
	}

	public void active(boolean pEnabled) {
		mActive = pEnabled;
	}

	public int entryID() {
		return mMenuEntryID;
	}

	// =============================================
	// Constructors
	// =============================================

	public MenuEntry(ScreenManager pScreenManager, MenuScreen pParentScreen, String pMenuEntryLabel) {
		mScreenManager = pScreenManager;
		mDisplayConfig = pScreenManager.displayConfig();
		mParentScreen = pParentScreen;
		mText = pMenuEntryLabel;

		mSpriteBatch = new SpriteBatch();

		mActive = true;
		mEnabled = true;
		mCanHaveFocus = true;
		mCanHoverOver = true;
		mDrawBackground = true;
		mScaleOnHover = false;
		
		mZ = 2f;

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise() {
		switch (mButtonSize) {
		case tiny:
			width = MENUENTRY_WIDTH * 0.5f;
			break;
		case narrow:
			width = MENUENTRY_WIDTH * 0.75f;
			break;
		case normal:
			width = MENUENTRY_WIDTH;
			break;
		case wide:
			width = MENUENTRY_WIDTH * 1.35f;
			break;
		}

		height = MENUENTRY_HEIGHT;

		mIsInitialised = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mSpriteBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		mSpriteBatch.unloadGLContent();
		
		mClickListener = null; // kill reference

		mIsLoaded = false;

	}

	public boolean handleInput(InputState pInputState) {
		if (!mActive || !mEnabled || isAnimating)
			return false;

		// TODO(John): Why is the last input active needed and remove if not!
		if (intersects(mScreenManager.HUD().getMouseCameraSpace()) && pInputState.lastInputActive() == INPUT_TYPES.Mouse) {
			// We should make sure no other component is currently using this leftClick.

			hasFocus(true);
			if (canHoverOver() && pInputState.leftClickOwner() == -1) {
				mParentScreen.setHoveringOn(this);
			}

			if (canHaveFocus() && pInputState.mouseLeftClick()) {
				if (!pInputState.tryAquireLeftClickOwnership(hashCode()))
					return false;

				pInputState.setLeftMouseClickHandled();
				mParentScreen.setFocusOn(pInputState, this, false);

				return true;
			}

		} else {
			hasFocus(false);
			hoveredOver(false);
		}

		return false;
	}

	public void updateStructure(RenderState pRenderState) {

	}

	public void update(GameTime pGameTime, MenuScreen pScreen, boolean pIsSelected) {
		if (!mActive)
			return;

		if (mAnimationTimer > 0) {
			mAnimationTimer -= pGameTime.elapseGameTime();

		}
		mClickTimer += pGameTime.elapseGameTime();

		if (mScaleOnHover && mHasFocus && canHaveFocus()) {
			mScaleCounter += pGameTime.elapseGameTime() / 500.0f;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		}

		else if (mScaleOnHover && mHoveredOver) {
			mScaleCounter += pGameTime.elapseGameTime() / 500.0f;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);

		}

		else {
			mScale = 0.75f;
		}

	}

	public void draw(Screen pScreen, RenderState pRenderState, boolean pIsSelected) {
		if (!mActive || !mIsInitialised || !mIsLoaded)
			return;

		// The colour of the entry background is determined by its enabled and hovered states
		// Hovered Normal Disabled
		float lSY = mAnimationTimer > 0 ? 0 : mHoveredOver ? 0 : 16;
		float lSX = mAnimationTimer > 0 ? 16 : 0;

		if (mDrawBackground) {
			mSpriteBatch.begin(mScreenManager.HUD());
			mSpriteBatch.draw(lSX, lSY, 16, 16, x, y, mZ, width, 32, 1f, 1f, 1f, 1f, 1f, TextureManager.textureManager().getTexture(ScreenManager.SCREEN_MANAGER_TEXTURE_NAME));
			mSpriteBatch.end();
		}

		/* Draw the button highlight when this element has focus. */
		// ---> TODO: 

		// Render the MenuEntry label
		float lScale = 1f;
		mParentScreen.font().begin(mScreenManager.HUD());
		mParentScreen.font().draw(mText, x + width / 2 - mParentScreen.font().bitmap().getStringWidth(mText, lScale) * 0.5f, y + height / 2 - 10, mZ + 0.1f, 0.97f, .92f, .95f, 1f, lScale);
		mParentScreen.font().end();
	}

	// =============================================
	// Methods
	// =============================================

	public void registerClickListener(IMenuEntryClickListener pListener, int pID) {
		mMenuEntryID = pID;
		mClickListener = pListener;
	}

	public void onClick(InputState pInputState) {
		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickTimer < FOCUS_TIMER)
			return;

		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;

		// Play a button click animation, then call the listeners
		mClickListener.onClick(mMenuEntryID);
	}

}
