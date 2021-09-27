package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.Vector2f;
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

	public static MenuEntry menuSeparator() {
		final var lNewMenuSeparatorEntry = new MenuEntry(null, null, null);
		lNewMenuSeparatorEntry.enabled(false);
		lNewMenuSeparatorEntry.active(false);
		lNewMenuSeparatorEntry.drawButtonBackground(false);

		return lNewMenuSeparatorEntry;

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ALIGNMENT mHorizontalAlignment = ALIGNMENT.CENTER;
	protected ALIGNMENT mVerticalAlignment = ALIGNMENT.CENTER;
	protected FILLTYPE mHorizontalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected FILLTYPE mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;

	protected final Rectangle mInfoIconDstRectangle = new Rectangle();
	protected final Rectangle mWarnIconDstRectangle = new Rectangle();
	public final Color entryColor = new Color();
	public final Color textColor = new Color();

	protected ScreenManager mScreenManager;
	protected SpriteSheetDefinition mCoreSpritesheet;
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
	protected String mToolTipText;
	protected boolean mShowInfoIcon;
	protected boolean mShowWarnIcon;
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
		mHoveredOver = mCanHoverOver && pNewValue;
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

		entryColor.setFromColor(ColorConstants.WHITE);
		textColor.setFromColor(ColorConstants.TextEntryColor);

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
		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		mIsLoaded = false;

	}

	public boolean handleInput(LintfordCore pCore) {
		if (!mActive || !mEnabled || isAnimating)
			return false;

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			hoveredOver(parentLayout().parentScreen.acceptMouseInput);

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.appTime().elapsedTimeMilli();

			}

			if (canHoverOver()) {
				if (pCore.input().mouse().isMiddleOwnerNotAssigned()) {
					mParentLayout.parentScreen.setHoveringOn(this);

				}

				if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					hasFocus(true);
					mParentLayout.parentScreen.setFocusOn(pCore, this, false);

					onClick(pCore.input());

					return true;

				}

			}

		} else {
			hoveredOver(false);
			mToolTipTimer = 0;

		}

		return false;
	}

	public void updateStructure() {
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		if (mShowInfoIcon) {
			mInfoIconDstRectangle.set(lScreenOffset.x + x + paddingLeft(), lScreenOffset.y + y, 32f, 32f);
		}

		if (mShowWarnIcon) {
			mWarnIconDstRectangle.set(lScreenOffset.x + x + paddingLeft(), lScreenOffset.y + y, 32f, 32f);
		}
	};

	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		if (!mActive)
			return;

		final float lParentScreenAlpha = pScreen.screenColor.a;
		entryColor.a = lParentScreenAlpha;
		textColor.a = lParentScreenAlpha;

		if (!intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mHoveredOver = false;
		}

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

		if ((mToolTipEnabled && mToolTipTimer >= 1000 && mHoveredOver) || mInfoIconDstRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace())) {
			mScreenManager.toolTip().toolTipProvider(this);
		}
	}

	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		if (!mActive || !mIsinitialized || !mIsLoaded)
			return;

		mZ = pParentZDepth;

		final var lScreenOffset = pScreen.screenPositionOffset();
		final var lParentScreenAlpha = pScreen.screenColor.a;
		final var isAnimationActive = mAnimationTimer > 0.f;

		if (mEnabled) {
			if (isAnimationActive) {
				entryColor.setFromColor(ColorConstants.getColorWithAlpha(ColorConstants.SecondaryColor, (mAnimationTimer / 255.f) * lParentScreenAlpha));
			} else {
				entryColor.setFromColor(ColorConstants.getColorWithAlpha(ColorConstants.SecondaryColor, lParentScreenAlpha));
			}
			entryColor.a = lParentScreenAlpha;

		} else {
			entryColor.setFromColor(ColorConstants.getColorWithAlpha(ColorConstants.SecondaryColor, .35f));
			entryColor.a = lParentScreenAlpha * .6f;
		}

		final var lSpriteBatch = mParentLayout.parentScreen.spriteBatch();

		if (mDrawBackground) {
			boolean use5Steps = w > 32 * 8;

			final float lTileSize = 32;
			final float lHalfWidth = (int) (w * .5f);
			int lLeft = (int) (lScreenOffset.x + centerX() - lHalfWidth);
			final float lInnerWidth = w - 32 * (use5Steps ? 4 : 2);
			entryColor.a = 1.f;
			if (isInClickedState()) {
				lSpriteBatch.begin(pCore.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lInnerWidth, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.end();
			} else if (mHoveredOver && mHighlightOnHover) {
				lSpriteBatch.begin(pCore.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lInnerWidth, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.end();

			} else {
				lSpriteBatch.begin(pCore.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - h / 2, lInnerWidth, h, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - h / 2, lTileSize, h, mZ, entryColor);
				lSpriteBatch.end();
			}
		}

		else if (mHoveredOver && mEnabled) {
			final float lColorMod = 1.f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);
			lColor.a = 0.25f;

			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - w / 2, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (w / 2) + 32, lScreenOffset.y + centerY() - h / 2, w - 64, h, mZ, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (w / 2) - 32, lScreenOffset.y + centerY() - h / 2, 32, h, mZ, lColor);
			lSpriteBatch.end();
		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lMenuFont = mParentLayout.parentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(pCore.HUD());
				final float lStringWidth = lMenuFont.getStringWidth(mText, lUiTextScale);
				final var lTextColor = mHoveredOver ? ColorConstants.FLAME : ColorConstants.TextHeadingColor;
				lTextColor.a = lParentScreenAlpha;
				lMenuFont.drawText(mText, lScreenOffset.x + centerX() - lStringWidth * 0.5f, lScreenOffset.y + centerY() - lMenuFont.fontHeight() * .5f, mZ, lTextColor, lUiTextScale);
				lMenuFont.end();
			}
		}

		if (mShowInfoIcon) {
			drawInfoIcon(pCore, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);
		}

		if (mShowWarnIcon) {
			drawWarningIcon(pCore, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);
		}

		//		if (!mEnabled) {
		//			drawdisabledBlackOverbar(pCore, lSpriteBatch, entryColor.a);
		//		}

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(pCore.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + x, lScreenOffset.y + y, w, h, mZ, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void drawInfoIcon(LintfordCore pCore, SpriteBatch pSpriteBatch, Rectangle pDestRect, float pScreenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, pScreenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		pSpriteBatch.begin(pCore.HUD());
		pSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_INFO, lScreenOffset.x + pDestRect.x(), lScreenOffset.y + pDestRect.y(), pDestRect.w(), pDestRect.h(), mZ, lColor);
		pSpriteBatch.end();
	}

	public void drawWarningIcon(LintfordCore pCore, SpriteBatch pSpriteBatch, Rectangle pDestRect, float pScreenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, pScreenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		pSpriteBatch.begin(pCore.HUD());
		pSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_WARNING, lScreenOffset.x + pDestRect.x(), lScreenOffset.y + pDestRect.y(), pDestRect.w(), pDestRect.h(), mZ, lColor);
		pSpriteBatch.end();
	}

	public void drawDebugCollidableBounds(LintfordCore pCore, SpriteBatch pSpriteBatch) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;
			pSpriteBatch.begin(pCore.HUD());
			pSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + x, lScreenOffset.y + y, w, h, mZ, ColorConstants.Debug_Transparent_Magenta);
			pSpriteBatch.end();
		}
	}

	public void drawdisabledBlackOverbar(LintfordCore pCore, SpriteBatch pSpriteBatch, float pScreenAlpha) {
		final var lColor = ColorConstants.getColor(.1f, .1f, .1f, .75f * pScreenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		pSpriteBatch.begin(pCore.HUD());
		pSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (w / 2), lScreenOffset.y + centerY() - h / 2, w, h, mZ, lColor);
		pSpriteBatch.end();
	}

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

	@Override
	public boolean isParentActive() {
		return mParentLayout != null && mParentLayout.parentScreen != null && mParentLayout.parentScreen.isExiting() == false;
	}
}
