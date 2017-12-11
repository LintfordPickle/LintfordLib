package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.input.InputState.INPUT_TYPES;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.renderers.windows.UIRectangle;
import net.lintford.library.screenmanager.entries.IMenuEntryClickListener;

public class MenuEntry extends UIRectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final float MENUENTRY_WIDTH = 288;
	protected static final float MENUENTRY_HEIGHT = 32;
	protected static final float FOCUS_TIMER = 500f; // milli

	public enum BUTTON_SIZE {
		tiny, narrow, normal, wide;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected MenuScreen mParentScreen;
	protected boolean mActive; // Not drawn/updated etc.
	protected boolean mEnabled; // drawn but greyed out
	protected String mText;
	protected float mScale;
	private float mScaleCounter;
	protected IMenuEntryClickListener mClickListener;
	protected int mMenuEntryID;
	protected boolean mDrawBackground;
	protected boolean mHighlightOnHover;
	protected boolean mScaleonHover;

	protected float mAnimationTimer;

	protected boolean mHoveredOver;
	protected boolean mCanHoverOver;
	protected boolean mToolTipEnabled;
	protected float mToolTipTimer;
	protected String mToolTip;
	protected boolean mHasFocus;
	protected boolean mFocusLocked;
	protected boolean mCanHaveFocus;
	protected float mClickTimer;
	protected BUTTON_SIZE mButtonSize = BUTTON_SIZE.normal;

	protected TextureBatch mSpriteBatch;

	private boolean mIsInitialised, mIsLoaded;
	public float mZ;

	protected float mHorizontalPadding = 5f;
	protected float mVerticalPadding = 5f;

	boolean isAnimating;
	float animationTimeRemaining;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEntry(ScreenManager pScreenManager, MenuScreen pParentScreen, String pMenuEntryLabel) {
		mScreenManager = pScreenManager;
		mParentScreen = pParentScreen;
		mText = pMenuEntryLabel;

		mSpriteBatch = new TextureBatch();

		mActive = true;
		mEnabled = true;
		mCanHaveFocus = true;
		mCanHoverOver = true;
		mDrawBackground = true;
		mScaleonHover = false;
		mHighlightOnHover = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

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

		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!mActive || !mEnabled || isAnimating)
			return false;

		final float deltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;

		// TODO(John): Why is the last input active needed and remove if not!
		if (intersects(pCore.HUD().getMouseCameraSpace()) && pCore.input().lastInputActive() == INPUT_TYPES.Mouse) {
			// We should make sure no other component is currently using this leftClick.

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += deltaTime;
			}

			if (canHoverOver()) {
				hasFocus(true);

				if (pCore.input().leftClickOwner() == -1) {
					mParentScreen.setHoveringOn(this);
					
				}
			}

			if (canHaveFocus() && pCore.input().tryAquireLeftClickOwnership(hashCode())) {
				pCore.input().setLeftMouseClickHandled();
				mParentScreen.setFocusOn(pCore.input(), this, false);

				return true;
			}

		} else {
			hasFocus(false);
			hoveredOver(false);
			mToolTipTimer = 0;
		}

		return false;
	}

	public void updateStructure() {

	}

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (!mActive)
			return;

		final float deltaTime = (float) pCore.time().elapseGameTimeMilli();

		if (mAnimationTimer > 0) {
			mAnimationTimer -= deltaTime;

		}
		mClickTimer += deltaTime;

		if (mScaleonHover && mHasFocus && canHaveFocus()) {
			mScaleCounter += deltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		}

		else if (mScaleonHover && mHoveredOver) {
			mScaleCounter += deltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);

		}

		else {
			mScale = 0.75f;
		}

	}

	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!mActive || !mIsInitialised || !mIsLoaded)
			return;

		// Set the tint of the back based on whether the button is enabled or not
		float lR = mEnabled ? 1f : .35f;
		float lG = mEnabled ? 1f : .35f;
		float lB = mEnabled ? 1f : .35f;
		
		float tile_size = 32;
		
		// Scale the width depending on the button size
		Texture lTexture = TextureManager.textureManager().getTexture(ScreenManager.SCREENMANAGER_TEXTURE_NAME);

		// Draw the button highlight when this element has focus.
		if (mHasFocus && mHighlightOnHover) {
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(0, 64, 32, 32, centerX() - width / 2, centerY() - height / 2, -2f, tile_size, height, 1f, mParentScreen.mA, lTexture);
			switch(mButtonSize) {
			default:
				mSpriteBatch.draw(32, 64, 224, 32, centerX() - (width / 2) + tile_size, centerY() - height / 2, -2f, width - tile_size*2, height, 1f, mParentScreen.mA, lTexture);
				mSpriteBatch.draw(256, 64, 32, 32, centerX() + (width / 2) - tile_size, centerY() - height / 2, -2f, tile_size, height, 1f, mParentScreen.mA, lTexture);		
			}
			mSpriteBatch.end();

		} else if (mDrawBackground) {
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(0, 32, 32, 32, centerX() - width / 2, centerY() - height / 2, -2f, 32, height, 1f, lR, lG, lB, mParentScreen.mA, lTexture);
			switch(mButtonSize) {
			default:
				mSpriteBatch.draw(32, 32, 224, 32, centerX() - (width / 2) + 32, centerY() - height / 2, -2f, width - 64, height, 1f, mParentScreen.mA, lTexture);
				mSpriteBatch.draw(256, 32, 32, 32, centerX() + (width / 2) - 32, centerY() - height / 2, -2f, 32, height, 1f, mParentScreen.mA, lTexture);	
			}
			mSpriteBatch.end();
		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float FONT_SCALE = 1f;
			mParentScreen.font().begin(pCore.HUD());
			mParentScreen.font().draw(mText, centerX() - mParentScreen.font().bitmap().getStringWidth(mText, FONT_SCALE) * 0.5f, centerY() - mParentScreen.font().bitmap().fontHeight() * FONT_SCALE / 2, -1f, 0.97f, .92f, mParentScreen.mA,
					mParentScreen.mA, FONT_SCALE);
			mParentScreen.font().end();

		}

		if (mToolTipEnabled && mToolTipTimer >= 1000) {
			Vector2f lToolTipPosition = pCore.input().mouseWindowCoords();
			mScreenManager.toolTip().draw(mToolTip, lToolTipPosition.x, lToolTipPosition.y);

		}

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mSpriteBatch.begin(pCore.HUD());
			final float SCALE = 1f;
			final float ALPHA = 0.3f;
			mSpriteBatch.draw(0, 0, 32, 32, x, y, mZ, width, height, SCALE, 1f, 0.2f, 0.2f, ALPHA, TextureManager.TEXTURE_CORE_UI);
			mSpriteBatch.end();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setToolTip(String pToolTipText) {
		if (pToolTipText == null || pToolTipText.length() == 0) {
			mToolTipEnabled = false;
			return;
		}

		mToolTipEnabled = true;
		mToolTip = pToolTipText;
	}

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
