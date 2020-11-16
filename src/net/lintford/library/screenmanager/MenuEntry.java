package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintford.library.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuEntry extends Rectangle implements IProcessMouseInput, IToolTipProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -226493862481815669L;

	protected static final float FOCUS_TIMER = 500f; // milli

	protected static final float Z_STATE_MODIFIER_PASSIVE = 0.005f; // Entry passive
	protected static final float Z_STATE_MODIFIER_ACTIVE = 0.006f; // Entry active

	public static final String SOUND_ON_CLICK_NAME = "SOUND_MENU_CLICK";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ALIGNMENT mHorizontalAlignment = ALIGNMENT.CENTER;
	protected ALIGNMENT mVerticalAlignment = ALIGNMENT.CENTER;
	protected FILLTYPE mHorizontalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected FILLTYPE mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;

	protected ScreenManager mScreenManager;
	protected Texture mUITexture;
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
	protected boolean mDrawTextShadow;
	protected boolean mScaleonHover;
	protected float mAnimationTimer;
	protected boolean mHoveredOver;
	protected boolean mCanHoverOver;
	protected boolean mToolTipEnabled;
	protected float mToolTipTimer;
	protected String mToolTipText;
	protected boolean mShowInfoIcon;
	protected Rectangle mInfoIconDstRectangle = new Rectangle();
	protected boolean mShowWarnIcon;
	protected Rectangle mWarnIconDstRectangle = new Rectangle();
	protected boolean mHasFocus;
	protected boolean mFocusLocked; // used only for buffered input
	protected boolean mCanHaveFocus; // some menuEntry sub-types aren't focusable (like the labels)
	protected float mClickTimer;

	private boolean mIsinitialized, mIsLoaded;
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

	protected float mDesiredWidth;
	protected float mDesiredHeight;

	protected float mMinWidth;
	protected float mMinHeight;
	protected float mMaxWidth;
	protected float mMaxHeight;

	protected boolean isAnimating;
	protected float animationTimeRemaining;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean enableTextShadow() {
		return mDrawTextShadow;
	}

	public void enableTextShadow(boolean pNewValue) {
		mDrawTextShadow = pNewValue;
	}

	public ALIGNMENT horizontalAlignment() {
		return mHorizontalAlignment;
	}

	public void horizontalAlignment(ALIGNMENT pNewValue) {
		mHorizontalAlignment = pNewValue;
	}

	public ALIGNMENT verticalAlignment() {
		return mVerticalAlignment;
	}

	public void verticalAlignment(ALIGNMENT pNewValue) {
		mVerticalAlignment = pNewValue;
	}

	public FILLTYPE horizontalFillType() {
		return mHorizontalFillType;
	}

	public void horizontalFillType(FILLTYPE pNewValue) {
		mHorizontalFillType = pNewValue;
	}

	public FILLTYPE verticalFillType() {
		return mVerticalFillType;
	}

	public void verticalFillType(FILLTYPE pNewValue) {
		mVerticalFillType = pNewValue;
	}

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

	public void marginLeft(float pNewValue) {
		mLeftMargin = pNewValue;
	}

	public float marginRight() {
		return mRightMargin;
	}

	public void marginRight(float pNewValue) {
		mRightMargin = pNewValue;
	}

	public float marginTop() {
		return mTopMargin;
	}

	public void marginTop(float pNewValue) {
		mTopMargin = pNewValue;
	}

	public float marginBottom() {
		return mBottomMargin;
	}

	public void marginBottom(float pNewValue) {
		mBottomMargin = pNewValue;
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

	public float minWidth() {
		return mMinWidth;
	}

	public float minHeight() {
		return mMinHeight;
	}

	public float maxWidth() {
		return mMaxWidth;
	}

	public float maxHeight() {
		return mMaxHeight;
	}

	public void minWidth(float pNewValue) {
		mMinWidth = pNewValue;
	}

	public void minHeight(float pNewValue) {
		mMinHeight = pNewValue;
	}

	public void maxWidth(float pNewValue) {
		mMaxWidth = pNewValue;
	}

	public void maxHeight(float pNewValue) {
		mMaxHeight = pNewValue;
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

	public boolean showInfoButton() {
		return mShowInfoIcon;
	}

	public void showInfoButton(boolean pNewValue) {
		mShowInfoIcon = pNewValue;
	}

	public boolean showWarnButton() {
		return mShowWarnIcon;
	}

	public void showWarnButton(boolean pNewValue) {
		mShowWarnIcon = pNewValue;
	}

	public boolean isInClickedState() {
		return mAnimationTimer > 0.f;
	}

	public float clickStateNormalizedTime() {
		return mAnimationTimer / MenuScreen.ANIMATION_TIMER_LENGTH;
	}

	public float desiredWidth() {
		return mDesiredWidth;
	}

	public void desiredWidth(float pNewValue) {
		mDesiredWidth = pNewValue;
	}

	public float desiredHeight() {
		return mDesiredHeight;
	}

	public void desiredHeight(float pNewValue) {
		mDesiredHeight = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pMenuEntryLabel) {
		mScreenManager = pScreenManager;
		mParentLayout = pParentLayout;
		mText = pMenuEntryLabel;

		mActive = true;
		mEnabled = true;
		mCanHaveFocus = true;
		mCanHoverOver = true;
		mDrawBackground = true;
		mScaleonHover = false;
		mHighlightOnHover = true;
		mDrawTextShadow = true;

		mTopMargin = 3f;
		mBottomMargin = 6f;
		mLeftMargin = 10f;
		mRightMargin = 10f;

		mMinWidth = 32.f;
		mMaxWidth = 800.f;
		mDesiredWidth = 400.f;

		mMinHeight = 32.f;
		mMaxHeight = 32.f;
		mDesiredHeight = 32.f;

		w = mDesiredWidth;
		h = mDesiredHeight;

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
		mHorizontalFillType = FILLTYPE.HALF_PARENT;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mIsinitialized = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		mUITexture = pResourceManager.textureManager().textureCore();

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!mActive || !mEnabled || isAnimating)
			return false;

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.appTime().elapsedTimeMilli();

			}

			if (canHoverOver()) {
				hasFocus(true);

				if (pCore.input().mouse().isMiddleOwnerNotAssigned()) {
					mParentLayout.parentScreen.setHoveringOn(this);

				}

			}

			if (canHaveFocus() && pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				mParentLayout.parentScreen.setFocusOn(pCore, this, false);

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
		if (mShowInfoIcon) {
			mInfoIconDstRectangle.set(x + paddingLeft(), y, 32f, 32f);

		}

		if (mShowWarnIcon) {
			mWarnIconDstRectangle.set(x + paddingLeft(), y, 32f, 32f);

		}

	};

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (!mActive)
			return;

		final var lDeltaTime = (float) pCore.appTime().elapsedTimeMilli();

		if (mClickTimer >= 0) {
			mClickTimer -= lDeltaTime;

		}

		if (mAnimationTimer > 0) {
			mAnimationTimer -= lDeltaTime;

		}

		if (mScaleonHover && mHasFocus && canHaveFocus()) {
			mScaleCounter += lDeltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);

		} else if (mScaleonHover && mHoveredOver) {
			mScaleCounter += lDeltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);

		} else {
			mScale = 0.75f;

		}

		mHoveredOver = this.intersectsAA(pCore.HUD().getMouseCameraSpace());

		if ((mToolTipEnabled && mToolTipTimer >= 1000 && mHoveredOver) || mInfoIconDstRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mScreenManager.toolTip().toolTipProvider(this);

		}

	}

	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!mActive || !mIsinitialized || !mIsLoaded)
			return;

		mZ = pParentZDepth;

		// Set the tint of the back based on whether the button is enabled or not
		float lR = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.x : 0.55f : .35f;
		float lG = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.y : 0.55f : .35f;
		float lB = mEnabled ? mAnimationTimer <= 0 ? ColorConstants.GREY_DARK.z : 0.55f : .35f;
		float lA = mEnabled ? mParentLayout.parentScreen.mA : 0.60f;

		float tile_size = 32;

		final var lTextureBatch = mParentLayout.parentScreen.textureBatch();

		// Draw the button highlight when this element has focus.
		if (mDrawBackground) {
			if (isInClickedState()) {
				lTextureBatch.begin(pCore.HUD());
				lTextureBatch.draw(mUITexture, 0, 96, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 32, 96, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 128, 96, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
				lTextureBatch.end();

			} else if (mHoveredOver && mHighlightOnHover) {
				lR *= 0.6f;
				lG *= 0.6f;
				lB *= 0.6f;

				lTextureBatch.begin(pCore.HUD());
				lTextureBatch.draw(mUITexture, 0, 64, 32, 32, centerX() - w / 2, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 32, 64, 32, 32, centerX() - (w / 2) + tile_size, centerY() - h / 2, w - tile_size * 2, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 128, 64, 32, 32, centerX() + (w / 2) - tile_size, centerY() - h / 2, tile_size, h, mZ, lR, lG, lB, lA);
				lTextureBatch.end();

			} else {
				lTextureBatch.begin(pCore.HUD());
				lTextureBatch.draw(mUITexture, 0, 32, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 32, 32, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lR, lG, lB, lA);
				lTextureBatch.draw(mUITexture, 128, 32, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lR, lG, lB, lA);
				lTextureBatch.end();

			}
		} 
		
		else if (mHoveredOver) {
			final float lHoveredColorHighlightR = 204.f / 255.f;
			final float lHoveredColorHighlightG = 115.f / 255.f;
			final float lHoveredColorHighlightB = 102.f / 255.f;
			
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - w / 2, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() - (w / 2) + 32, centerY() - h / 2, w - 64, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, centerX() + (w / 2) - 32, centerY() - h / 2, 32, h, mZ, lHoveredColorHighlightR, lHoveredColorHighlightG, lHoveredColorHighlightB, 0.26f);
			lTextureBatch.end();

		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();

			final float lColMod = 1f; // no color mod for the text (mHoveredOver && mHighlightOnHover) ? 0.7f : 1f;

			final var lMenuFont = mParentLayout.parentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(pCore.HUD());
				lMenuFont.draw(mText, centerX() - lMenuFont.bitmap().getStringWidth(mText, lUiTextScale) * 0.5f, centerY() - lMenuFont.bitmap().fontHeight() * lUiTextScale / 2 - 2f, mZ, 0.97f * lColMod, .92f * lColMod,
						.92f * lColMod, lA, lUiTextScale);
				lMenuFont.end();

			}

		}

		if (mShowInfoIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 192, 160, 32, 32, mInfoIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

		if (mShowWarnIcon) {
			lTextureBatch.begin(pCore.HUD());
			lTextureBatch.draw(mUITexture, 224, 160, 32, 32, mWarnIconDstRectangle, mZ, 1f, 1f, 1f, 1f);
			lTextureBatch.end();

		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lTextureBatch.begin(pCore.HUD());
			final float ALPHA = 0.3f;
			lTextureBatch.draw(mUITexture, 0, 0, 32, 32, x, y, w, h, mZ, 1f, 0.2f, 0.2f, ALPHA);
			lTextureBatch.end();

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
		mToolTipText = pToolTipText;
	}

	public void registerClickListener(EntryInteractions pListener, int pID) {
		mMenuEntryID = pID;
		mClickListener = pListener;
	}

	public void onClick(InputManager pInputState) {
		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickListener.isActionConsumed()) {
			return;

		}

		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;
		mScreenManager.uiSounds().play("SOUND_MENU_CLICK");
		mClickListener.menuEntryOnClick(pInputState, mMenuEntryID);

	}

	public void onViewportChange(float pWidth, float pHeight) {

	}

	@Override
	public boolean isCoolDownElapsed() {
		return mClickTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mClickTimer = 200;

	}

	@Override
	public String toolTipText() {
		return mToolTipText;

	}

	@Override
	public boolean isMouseOver() {
		return mHoveredOver;

	}

}
