package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.input.InputState.INPUT_TYPES;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuEntry extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -226493862481815669L;

	protected static final float MENUENTRY_DEF_BUTTON_WIDTH = 300;
	protected static final float MENUENTRY_DEF_BUTTON_HEIGHT = 32;

	protected static final float MENUENTRY_MAX_WIDTH = 700;

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
	protected BaseLayout mParentLayout;
	protected boolean mActive; // Not drawn/updated etc.
	protected boolean mEnabled;
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

	// This is the padding INSIDE of the component (i.e. applied to child elements).
	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;

	// The margin is applied to the outside of this component
	protected float mTopMargin;
	protected float mBottomMargin;
	protected float mLeftMargin;
	protected float mRightMargin;

	protected float mMaxWidth;
	protected float mMaxHeight;

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

	public BaseLayout parentLayout() {
		return mParentLayout;
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

	public float paddingLeft() {
		return mLeftPadding;
	}

	public float paddingRight() {
		return mRightPadding;
	}

	public float paddingTop() {
		return mTopPadding;
	}

	public float paddingBottom() {
		return mBottomPadding;
	}

	public float marginLeft() {
		return mLeftMargin;
	}

	public float marginRight() {
		return mRightMargin;
	}

	public float marginTop() {
		return mTopMargin;
	}

	public float marginBottom() {
		return mBottomMargin;
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

		switch (mButtonSize) {
		case tiny:
			mMaxWidth = MENUENTRY_DEF_BUTTON_WIDTH * 0.5f;
			mMaxHeight = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;
		case narrow:
			mMaxWidth = MENUENTRY_DEF_BUTTON_WIDTH * 0.75f;
			mMaxHeight = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;
		case normal:
			mMaxWidth = MENUENTRY_DEF_BUTTON_WIDTH;
			mMaxHeight = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;
		case wide:
			mMaxWidth = MENUENTRY_DEF_BUTTON_WIDTH * 2f;
			mMaxHeight = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;
		}

	}

	public float maxWidth() {
		return mMaxWidth;
	}

	public float maxHeight() {
		return mMaxHeight;
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

	public MenuEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pMenuEntryLabel) {
		mScreenManager = pScreenManager;
		mParentLayout = pParentLayout;
		mText = pMenuEntryLabel;

		mTextureBatch = new TextureBatch();

		mActive = true;
		mEnabled = true;
		mCanHaveFocus = true;
		mCanHoverOver = true;
		mDrawBackground = true;
		mScaleonHover = false;
		mHighlightOnHover = true;

		mTopMargin = 3f;
		mBottomMargin = 3f;

		mMaxWidth = MENUENTRY_DEF_BUTTON_WIDTH;
		mMaxHeight = MENUENTRY_DEF_BUTTON_HEIGHT;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {
		w = MENUENTRY_DEF_BUTTON_WIDTH;
		h = MENUENTRY_DEF_BUTTON_HEIGHT;

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
					mParentLayout.parentScreen().setHoveringOn(this);

				}
			}

			if (canHaveFocus() && pCore.input().mouseLeftClick()) {
				pCore.input().tryAquireLeftClickOwnership(hashCode());
				pCore.input().setLeftMouseClickHandled();
				mParentLayout.parentScreen().setFocusOn(pCore, this, false);

				onClick(pCore.input());

				return true;
			}

		} else {
			hoveredOver(false);
			mToolTipTimer = 0;

		}

		return false;
	}

	public void updateStructureDimensions() {
		switch (mButtonSize) {
		case tiny:
			w = MENUENTRY_DEF_BUTTON_WIDTH * 0.25f;
			h = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;

		case narrow:
			w = MENUENTRY_DEF_BUTTON_WIDTH * 0.45f;
			h = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;

		default: // narrow
			w = MENUENTRY_DEF_BUTTON_WIDTH;
			h = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;

		case wide:
			w = MENUENTRY_DEF_BUTTON_WIDTH * 1.5f;
			h = MENUENTRY_DEF_BUTTON_HEIGHT;
			break;
		}

	};

	public void updateStructurePositions() {
	};

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
		float lR = mEnabled ? mAnimationTimer <= 0 ? 1f : 0.55f : .35f;
		float lG = mEnabled ? mAnimationTimer <= 0 ? 1f : 0.55f : .35f;
		float lB = mEnabled ? mAnimationTimer <= 0 ? 1f : 0.55f : .35f;
		float lA = mParentLayout.parentScreen().mA;

		float tile_size = 32;

		// Scale the width depending on the button size
		Texture lTexture = TextureManager.TEXTURE_CORE_UI;

		// Draw the button highlight when this element has focus.
		if (mDrawBackground && mHoveredOver && mHighlightOnHover) {
			lR *= 0.6f;
			lG *= 0.6f;
			lB *= 0.6f;

			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(lTexture, 0, 64, 32, 32, centerX() - w / 2, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, lA);
			switch (mButtonSize) {
			default:
				mTextureBatch.draw(lTexture, 32, 64, 224, 32, centerX() - (w / 2) + tile_size, centerY() - h / 2, w - tile_size * 2, h, mZ, lR, lG, lB, lA);
				mTextureBatch.draw(lTexture, 256, 64, 32, 32, centerX() + (w / 2) - tile_size, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, lA);
			}
			mTextureBatch.end();

		} else if (mDrawBackground) {
			mTextureBatch.begin(pCore.HUD());
			mTextureBatch.draw(lTexture, 0, 32, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
			switch (mButtonSize) {
			default:
				mTextureBatch.draw(lTexture, 32, 32, 224, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lR, lG, lB, lA);
				mTextureBatch.draw(lTexture, 256, 32, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
			}
			mTextureBatch.end();
		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float FONT_SCALE = 1f;

			float lColMod = 1f; // no color mod for the text (mHoveredOver && mHighlightOnHover) ? 0.7f : 1f;

			FontUnit lMenuFont = mParentLayout.parentScreen().font();

			lMenuFont.begin(pCore.HUD());
			lMenuFont.draw(mText, centerX() - lMenuFont.bitmap().getStringWidth(mText, FONT_SCALE) * 0.5f, centerY() - lMenuFont.bitmap().fontHeight() * FONT_SCALE / 2 - 2f, mZ, 0.97f * lColMod, .92f * lColMod,
					.92f * lColMod, lA, FONT_SCALE);
			lMenuFont.end();

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
