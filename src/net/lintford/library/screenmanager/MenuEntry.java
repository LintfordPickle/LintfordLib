package net.lintford.library.screenmanager;

import net.lintford.library.ConstantsApp;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.SpriteBatch;
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
	protected boolean mDormant;
	protected boolean mActiveUpdateDraw;
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
	protected boolean mFocusLocked;
	protected boolean mCanHaveFocus;
	protected float mClickTimer;
	private boolean mIsinitialized, mResourcesLoaded;
	public float mZ;
	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;
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

	public boolean isDormant() {
		return mDormant;
	}

	public void isDormant(boolean pIsDormant) {
		mDormant = pIsDormant;
	}

	public ALIGNMENT horizontalAlignment() {
		return mHorizontalAlignment;
	}

	public void horizontalAlignment(ALIGNMENT newValue) {
		mHorizontalAlignment = newValue;
	}

	public ALIGNMENT verticalAlignment() {
		return mVerticalAlignment;
	}

	public void verticalAlignment(ALIGNMENT newValue) {
		mVerticalAlignment = newValue;
	}

	public FILLTYPE horizontalFillType() {
		return mHorizontalFillType;
	}

	public void horizontalFillType(FILLTYPE newValue) {
		mHorizontalFillType = newValue;
	}

	public FILLTYPE verticalFillType() {
		return mVerticalFillType;
	}

	public void verticalFillType(FILLTYPE newValue) {
		mVerticalFillType = newValue;
	}

	public boolean drawButtonBackground() {
		return mDrawBackground;
	}

	public void drawButtonBackground(boolean newValue) {
		mDrawBackground = newValue;
	}

	public BaseLayout parentLayout() {
		return mParentLayout;
	}

	public boolean canHaveFocus() {
		return mCanHaveFocus;
	}

	public void canHaveFocus(boolean newValue) {
		mCanHaveFocus = newValue;
	}

	public boolean canHoverOver() {
		return mCanHoverOver;
	}

	public void canHoverOver(boolean newValue) {
		mCanHoverOver = newValue;
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

	public void marginLeft(float newValue) {
		mLeftMargin = newValue;
	}

	public float marginRight() {
		return mRightMargin;
	}

	public void marginRight(float newValue) {
		mRightMargin = newValue;
	}

	public float marginTop() {
		return mTopMargin;
	}

	public void marginTop(float newValue) {
		mTopMargin = newValue;
	}

	public float marginBottom() {
		return mBottomMargin;
	}

	public void marginBottom(float newValue) {
		mBottomMargin = newValue;
	}

	public void entryID(int newValue) {
		mMenuEntryID = newValue;
	}

	public String entryText() {
		return mText;
	}

	public void entryText(String newValue) {
		mText = newValue;
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

	public void minWidth(float newValue) {
		mMinWidth = newValue;
	}

	public void minHeight(float newValue) {
		mMinHeight = newValue;
	}

	public void maxWidth(float newValue) {
		mMaxWidth = newValue;
	}

	public void maxHeight(float newValue) {
		mMaxHeight = newValue;
	}

	public boolean hoveredOver() {
		return mHoveredOver;
	}

	public void hoveredOver(boolean newValue) {
		mHoveredOver = mCanHoverOver && newValue;
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean newValue) {
		mHasFocus = newValue;
	}

	public boolean hasFocusLock() {
		return mFocusLocked;
	}

	public void hasFocusLock(boolean newValue) {
		mFocusLocked = newValue;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean enabled) {
		mEnabled = enabled;
	}

	public boolean activeUpdateDraw() {
		return mActiveUpdateDraw;
	}

	public void active(boolean enabled) {
		mActiveUpdateDraw = enabled;
	}

	@Override
	public float height() {
		return !mActiveUpdateDraw ? 0 : super.height();
	}

	public int entryID() {
		return mMenuEntryID;
	}

	public boolean showInfoButton() {
		return mShowInfoIcon;
	}

	public void showInfoButton(boolean newValue) {
		mShowInfoIcon = newValue;
	}

	public boolean showWarnButton() {
		return mShowWarnIcon;
	}

	public void showWarnButton(boolean newValue) {
		mShowWarnIcon = newValue;
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

	public void desiredWidth(float newValue) {
		mDesiredWidth = newValue;
	}

	public float desiredHeight() {
		return mDesiredHeight;
	}

	public void desiredHeight(float newValue) {
		mDesiredHeight = newValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEntry(ScreenManager screenManager, BaseLayout parentLayout, String menuEntryLabel) {
		mScreenManager = screenManager;
		mParentLayout = parentLayout;
		mText = menuEntryLabel;

		mActiveUpdateDraw = true;
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
		mMaxWidth = 2048.f;
		mDesiredWidth = 400.f;

		mMinHeight = 4.f;
		mMaxHeight = 512.f;
		mDesiredHeight = 32.f;

		mW = mDesiredWidth;
		mH = mDesiredHeight;

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

	public void loadResources(ResourceManager resourceManager) {
		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mResourcesLoaded = true;
	}

	public void unloadResources() {
		mResourcesLoaded = false;
	}

	public boolean handleInput(LintfordCore core) {
		if (mDormant || !mActiveUpdateDraw || !mEnabled || isAnimating)
			return false;

		if (intersectsAA(core.HUD().getMouseCameraSpace()) && core.input().mouse().isMouseOverThisComponent(hashCode())) {
			hoveredOver(parentLayout().parentScreen.mAcceptMouseInput);

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += core.appTime().elapsedTimeMilli();
			}

			if (canHoverOver()) {
				if (core.input().mouse().isMiddleOwnerNotAssigned())
					mParentLayout.parentScreen.setHoveringOn(this);

				if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					hasFocus(true);
					mParentLayout.parentScreen.setFocusOn(core, this, false);

					onClick(core.input());

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

		if (mShowInfoIcon)
			mInfoIconDstRectangle.set(lScreenOffset.x + mX + paddingLeft(), lScreenOffset.y + mY, 32f, 32f);

		if (mShowWarnIcon)
			mWarnIconDstRectangle.set(lScreenOffset.x + mX + paddingLeft(), lScreenOffset.y + mY, 32f, 32f);

	};

	public void update(LintfordCore core, MenuScreen screen, boolean isSelected) {
		if (mDormant && !mActiveUpdateDraw)
			return;

		final float lParentScreenAlpha = screen.screenColor.a;
		entryColor.a = lParentScreenAlpha;
		textColor.a = lParentScreenAlpha;

		if (!intersectsAA(core.HUD().getMouseCameraSpace())) {
			mHoveredOver = false;
		}

		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		if (mClickTimer >= 0)
			mClickTimer -= lDeltaTime;

		if (mAnimationTimer > 0)
			mAnimationTimer -= lDeltaTime;

		if (mScaleonHover && mHasFocus && canHaveFocus()) {
			mScaleCounter += lDeltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		} else if (mScaleonHover && mHoveredOver) {
			mScaleCounter += lDeltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		} else {
			mScale = 0.75f;
		}

		if ((mToolTipEnabled && mToolTipTimer >= 1000 && mHoveredOver) || mInfoIconDstRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mScreenManager.toolTip().toolTipProvider(this);
		}
	}

	public void draw(LintfordCore core, Screen screen, boolean isSelected, float parentZDepth) {
		if (mDormant || !mActiveUpdateDraw || !mIsinitialized || !mResourcesLoaded)
			return;

		mZ = parentZDepth;

		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;
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
			boolean use5Steps = mW > 32 * 8;

			final float lTileSize = 32;
			final float lHalfWidth = (int) (mW * .5f);
			int lLeft = (int) (lScreenOffset.x + centerX() - lHalfWidth);
			final float lInnerWidth = mW - 32 * (use5Steps ? 4 : 2);
			entryColor.a = 1.f;
			if (isInClickedState()) {
				lSpriteBatch.begin(core.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lInnerWidth, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ,
							entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_SELECTED_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ,
						entryColor);
				lSpriteBatch.end();
			} else if (mHoveredOver && mHighlightOnHover) {
				lSpriteBatch.begin(core.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lInnerWidth, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ,
							entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HOVER_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.end();

			} else {
				lSpriteBatch.begin(core.HUD());
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_LEFT, lLeft, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDLEFT, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MID, lLeft += 32, lScreenOffset.y + centerY() - mH / 2, lInnerWidth, mH, mZ, entryColor);
				if (use5Steps)
					lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDRIGHT, (lLeft -= 32) + lHalfWidth * 2 - 96, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_RIGHT, (lLeft -= 32) + lHalfWidth * 2 - 32, lScreenOffset.y + centerY() - mH / 2, lTileSize, mH, mZ, entryColor);
				lSpriteBatch.end();
			}
		}

		else if (mHoveredOver && mEnabled) {
			final float lColorMod = 1.f;
			final var lColor = ColorConstants.getColorWithRGBMod(ColorConstants.PrimaryColor, lColorMod);
			lColor.a = 0.25f;

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - mW / 2, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2) + 32, lScreenOffset.y + centerY() - mH / 2, mW - 64, mH, mZ, lColor);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() + (mW / 2) - 32, lScreenOffset.y + centerY() - mH / 2, 32, mH, mZ, lColor);
			lSpriteBatch.end();
		}

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float lUiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor();
			final var lMenuFont = mParentLayout.parentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(core.HUD());
				final float lStringWidth = lMenuFont.getStringWidth(mText, lUiTextScale);
				final var lTextColor = mHoveredOver ? ColorConstants.FLAME : ColorConstants.TextHeadingColor;
				lTextColor.a = lParentScreenAlpha;

				lMenuFont.drawText(mText, lScreenOffset.x + centerX() - lStringWidth * 0.5f, lScreenOffset.y + centerY() - lMenuFont.fontHeight() * .5f, mZ, lTextColor, lUiTextScale);

				lMenuFont.end();
			}
		}

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, entryColor.a);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	public void postStencilDraw(LintfordCore core, Screen screen, boolean isSelected, float parentZDepth) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void drawInfoIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_INFO, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), destRect.width(), destRect.height(), mZ, lColor);
		spriteBatch.end();
	}

	public void drawWarningIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_WARNING, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), destRect.width(), destRect.height(), mZ, lColor);
		spriteBatch.end();
	}

	public void drawDebugCollidableBounds(LintfordCore core, SpriteBatch spriteBatch) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;
			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.end();
		}
	}

	public void drawdisabledBlackOverbar(LintfordCore core, SpriteBatch spriteBatch, float screenAlpha) {
		final var lColor = ColorConstants.getColor(.1f, .1f, .1f, .75f * screenAlpha);
		final var lScreenOffset = parentLayout() != null ? parentLayout().parentScreen.screenPositionOffset() : Vector2f.Zero;

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2), lScreenOffset.y + centerY() - mH / 2, mW, mH, mZ, lColor);
		spriteBatch.end();
	}

	public void setToolTip(String toolTipText) {
		if (toolTipText == null || toolTipText.length() == 0) {
			mToolTipEnabled = false;
			return;
		}

		mToolTipEnabled = true;
		mToolTipText = toolTipText;
	}

	public void registerClickListener(EntryInteractions listener, int entryUid) {
		mMenuEntryID = entryUid;
		mClickListener = listener;
	}

	public void onClick(InputManager inputState) {
		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickListener.isActionConsumed())
			return;

		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;
		mScreenManager.uiSounds().play("SOUND_MENU_CLICK");
		mClickListener.menuEntryOnClick(inputState, mMenuEntryID);
	}

	public void onViewportChange(float width, float height) {

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
