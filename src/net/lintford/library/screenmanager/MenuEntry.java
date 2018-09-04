package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.input.InputState.INPUT_TYPES;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.screenmanager.entries.EntryInteractions;

public class MenuEntry extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -226493862481815669L;

	protected static final float MENUENTRY_DEF_BUTTON_WIDTH = 300;
	protected static final float MENUENTRY_HEIGHT = 32;
	protected static final float FOCUS_TIMER = 500f; // milli

	protected static final float Z_STATE_MODIFIER_PASSIVE = 0.005f; // Entry passive
	protected static final float Z_STATE_MODIFIER_ACTIVE = 0.006f; // Entry active

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
	protected EntryInteractions mClickListener;
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
	protected boolean mFocusLocked; // used only for buffered input
	protected boolean mCanHaveFocus;
	protected float mClickTimer;
	protected BUTTON_SIZE mButtonSize = BUTTON_SIZE.normal;

	protected TextureBatch mTextureBatch;

	private boolean mIsInitialised, mIsLoaded;
	public float mZ;

	protected float mHorizontalPadding = 5f;
	protected float mVerticalPadding = 5f;

	boolean isAnimating;
	float animationTimeRemaining;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean drawButtonBackground() {
		return mDrawBackground;
	}

	public void drawButtonBackground(boolean pNewValue) {
		mDrawBackground = pNewValue;
	}

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
		return w;
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

		mTextureBatch = new TextureBatch();

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
			w = MENUENTRY_DEF_BUTTON_WIDTH * 0.5f;
			break;
		case narrow:
			w = MENUENTRY_DEF_BUTTON_WIDTH * 0.75f;
			break;
		case normal:
			w = MENUENTRY_DEF_BUTTON_WIDTH;
			break;
		case wide:
			w = MENUENTRY_DEF_BUTTON_WIDTH * 1.35f;
			break;
		}

		h = MENUENTRY_HEIGHT;

		mIsInitialised = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mTextureBatch.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();

		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!mActive || !mEnabled || isAnimating)
			return false;

		final float deltaTime = (float) pCore.time().elapseGameTimeMilli() / 1000f;

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().lastInputActive() == INPUT_TYPES.Mouse) {
			// We should make sure no other component is currently using this leftClick.

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += deltaTime * 1000f;
			}

			if (canHoverOver()) {
				hasFocus(true);

				if (pCore.input().leftClickOwner() == -1) {
					mParentScreen.setHoveringOn(this);

				}
			}

			if (canHaveFocus() && pCore.input().mouseLeftClick()) {
				pCore.input().tryAquireLeftClickOwnership(hashCode());
				pCore.input().setLeftMouseClickHandled();
				mParentScreen.setFocusOn(pCore, this, false);

				onClick(pCore.input());

				return true;
			}

		} else {
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

		mZ = pParentZDepth;

		// Set the tint of the back based on whether the button is enabled or not
		float lR = mEnabled ? mAnimationTimer <= 0 ? 1f : 0.75f : .35f;
		float lG = mEnabled ? mAnimationTimer <= 0 ? 1f : 0.75f : .35f;
		float lB = mEnabled ? 1f : .35f;

		float tile_size = 32;

		// Scale the width depending on the button size
		Texture lTexture = TextureManager.TEXTURE_CORE_UI;

		// Draw the button highlight when this element has focus.
		if (mDrawBackground && mHoveredOver && mHighlightOnHover) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(lTexture, 0, 64, 32, 32, centerX() - w / 2, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, mParentScreen.mA);
			switch (mButtonSize) {
			default:
				mTextureBatch.draw(lTexture, 32, 64, 224, 32, centerX() - (w / 2) + tile_size, centerY() - h / 2, w - tile_size * 2, h, mZ, lR, lG, lB, mParentScreen.mA);
				mTextureBatch.draw(lTexture, 256, 64, 32, 32, centerX() + (w / 2) - tile_size, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, mParentScreen.mA);
			}
			mTextureBatch.end();

		} else if (mDrawBackground) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(lTexture, 0, 32, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lR, lG, lB, mParentScreen.mA);
			switch (mButtonSize) {
			default:
				mTextureBatch.draw(lTexture, 32, 32, 224, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lR, lG, lB, mParentScreen.mA);
				mTextureBatch.draw(lTexture, 256, 32, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lR, lG, lB, mParentScreen.mA);
			}
			mTextureBatch.end();
		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float FONT_SCALE = 1f;

			float lColMod = (mHoveredOver && mHighlightOnHover) ? 0.7f : 1f;

			mParentScreen.font().begin(pCore.HUD());
			mParentScreen.font().draw(mText, centerX() - mParentScreen.font().bitmap().getStringWidth(mText, FONT_SCALE) * 0.5f, centerY() - mParentScreen.font().bitmap().fontHeight() * FONT_SCALE / 2 - 2f, mZ,
					0.97f * lColMod, .92f * lColMod, .92f * lColMod, mParentScreen.mA, FONT_SCALE);
			mParentScreen.font().end();

		}

		if (mToolTipEnabled && mToolTipTimer >= 1000) {
			Vector2f lToolTipPosition = pCore.HUD().getMouseCameraSpace();
			mScreenManager.toolTip().draw(pCore, mToolTip, lToolTipPosition.x, lToolTipPosition.y);

		}

		if (ConstantsTable.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			mTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, ALPHA);
			mTextureBatch.end();

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

	public void registerClickListener(EntryInteractions pListener, int pID) {
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
		mClickListener.menuEntryOnClick(mMenuEntryID);

	}

}
