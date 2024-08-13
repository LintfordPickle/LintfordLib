package net.lintfordlib.screenmanager;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.screenmanager.Screen.ScreenState;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

public class MenuEntry extends Rectangle implements IInputProcessor, IToolTipProvider, IContextHintProvider {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -226493862481815669L;

	protected static final float FOCUS_TIMER = 500f; // milli

	protected static final float Z_STATE_MODIFIER_PASSIVE = 0.005f; // Entry passive
	protected static final float Z_STATE_MODIFIER_ACTIVE = 0.006f; // Entry active

	public static final String SOUND_ON_CLICK_NAME = "SOUND_MENU_CLICK";

	public static final int ENTRY_DEFAULT_HEIGHT = 32;

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

	protected ScreenManager mScreenManager;
	protected MenuScreen mParentScreen;

	protected ALIGNMENT mHorizontalAlignment = ALIGNMENT.CENTER;
	protected ALIGNMENT mVerticalAlignment = ALIGNMENT.CENTER;
	protected FILLTYPE mHorizontalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected FILLTYPE mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected final Rectangle mInfoIconDstRectangle = new Rectangle();
	protected final Rectangle mWarnIconDstRectangle = new Rectangle();
	public final Color entryColor = new Color();
	public final Color textColor = new Color();
	protected SpriteSheetDefinition mCoreSpritesheet;

	public final ContextHintState contextHintState = new ContextHintState();

	protected boolean mEnabled;
	protected boolean mReadOnly; // Same as enabled, but the text is not greyed (legibility)
	protected boolean mEnableUpdateDraw;

	protected boolean mHasFocus;
	protected boolean mCanHaveFocus;

	protected boolean mIsActive;
	protected boolean mCanBeActivated;

	protected boolean mAffectParentStructure;
	protected String mText;
	protected float mScale;
	private float mScaleCounter;

	protected EntryInteractions mClickListener;
	protected int mMenuEntryID;

	protected boolean mDrawBackground;
	protected boolean mHighlightOnHover;
	protected boolean mScaleOnFocus;
	protected float mAnimationTimer;
	protected boolean mToolTipEnabled;
	protected boolean mIsMouseOver;
	protected float mToolTipTimer;
	protected String mToolTipText;
	protected boolean mShowInfoIcon;
	protected boolean mShowWarnIcon;

	protected float mInputTimer;
	private boolean mIsinitialized, mResourcesLoaded;
	public float mZ;

	// padding is the spacing within the component
	protected float mTopPadding;
	protected float mBottomPadding;
	protected float mLeftPadding;
	protected float mRightPadding;

	// margins are the spacings external to the component
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

	public boolean affectsParentStructure() {
		return mAffectParentStructure;
	}

	public void affectsParentStructure(boolean affectsParentStructure) {
		mAffectParentStructure = affectsParentStructure;
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

	public MenuScreen parentScreen() {
		return mParentScreen;
	}

	public boolean canHaveFocus() {
		return mCanHaveFocus;
	}

	public void canHaveFocus(boolean newValue) {
		mCanHaveFocus = newValue;
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

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean newValue) {
		mHasFocus = newValue;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void isActive(boolean newValue) {
		mIsActive = newValue;
	}

	public boolean enabled() {
		return mEnabled;
	}

	public void enabled(boolean enabled) {
		mEnabled = enabled;
	}

	public boolean readOnly() {
		return mReadOnly;
	}

	public void readOnly(boolean readOnly) {
		mReadOnly = readOnly;
	}

	public boolean activeUpdateDraw() {
		return mEnableUpdateDraw;
	}

	public void active(boolean enabled) {
		mEnableUpdateDraw = enabled;
	}

	@Override
	public float height() {
		return !mEnableUpdateDraw ? 0 : super.height();
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

	public MenuEntry(ScreenManager screenManager, MenuScreen parentScreen, String menuEntryLabel) {
		mScreenManager = screenManager;
		mParentScreen = parentScreen;

		mText = menuEntryLabel;

		mEnableUpdateDraw = true;
		mEnabled = true;
		mAffectParentStructure = true;
		mCanHaveFocus = true;
		mDrawBackground = true;
		mScaleOnFocus = false;
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
		mDesiredHeight = ENTRY_DEFAULT_HEIGHT;

		mW = mDesiredWidth;
		mH = mDesiredHeight;

		entryColor.setFromColor(ColorConstants.WHITE);
		textColor.setFromColor(ColorConstants.TextEntryColor);

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
		mHorizontalFillType = FILLTYPE.HALF_PARENT;

		contextHintState.buttonA = true;
		contextHintState.buttonAHint = "select";
		contextHintState.keyReturn = true;
		contextHintState.keyReturnHint = "select";
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

	public void updateStructure() {
		final var lScreenOffset = mParentScreen != null ? mParentScreen.screenPositionOffset() : Vector2f.Zero;

		if (mShowInfoIcon)
			mInfoIconDstRectangle.set(lScreenOffset.x + mX + paddingLeft(), lScreenOffset.y + mY, 32f, 32f);

		if (mShowWarnIcon)
			mWarnIconDstRectangle.set(lScreenOffset.x + mX + paddingLeft(), lScreenOffset.y + mY, 32f, 32f);

	};

	public boolean onHandleKeyboardInput(LintfordCore core) {
		return false;
	}

	public boolean onHandleGamepadInput(LintfordCore core) {
		return false;
	}

	public boolean onHandleMouseInput(LintfordCore core) {
		if (mParentScreen == null || !mEnabled)
			return false;

		if (!core.input().mouse().isMouseMenuSelectionEnabled()) {
			mIsMouseOver = false;
			return false;
		}

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			return false;
		}

		mIsMouseOver = true;

		if (!mHasFocus && mCanHaveFocus)
			mParentScreen.setFocusOnEntry(this);

		if (mToolTipEnabled)
			mToolTipTimer += core.appTime().elapsedTimeMilli();

		if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
			onClick(core.input());

			return true;
		}

		return false;
	}

	public void update(LintfordCore core, MenuScreen screen) {
		if (!mAffectParentStructure && !mEnableUpdateDraw)
			return;

		final float lParentScreenAlpha = screen.screenColor.a;
		entryColor.a = lParentScreenAlpha;
		textColor.a = lParentScreenAlpha;

		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		if (mInputTimer >= 0)
			mInputTimer -= lDeltaTime;

		if (mAnimationTimer > 0)
			mAnimationTimer -= lDeltaTime;

		if (mHasFocus && mScaleOnFocus) {
			mScaleCounter += lDeltaTime;
			mScale = 0.75f + (float) (Math.cos(mScaleCounter) * 0.05f);
		} else {
			mScale = 1.f;
		}

		if ((mToolTipEnabled && mHasFocus) || mInfoIconDstRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			mToolTipTimer += core.gameTime().elapsedTimeMilli();
		} else {
			mToolTipTimer = 0;
		}

		final var lParentScreenIsActive = mParentScreen != null && !mParentScreen.mOtherScreenHasFocus;
		if (lParentScreenIsActive && mToolTipEnabled && mToolTipTimer >= 1000) {
			mScreenManager.toolTip().toolTipProvider(this);
		}
	}

	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		if (!mAffectParentStructure || !mEnableUpdateDraw || !mIsinitialized || !mResourcesLoaded)
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

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			boolean use5Steps = mW > 32 * 8;

			final float lTileSize = 32;
			final float lHalfWidth = (int) (mW * .5f);
			int lLeft = (int) (lScreenOffset.x + centerX() - lHalfWidth);
			final float lInnerWidth = mW - 32 * (use5Steps ? 4 : 2);
			entryColor.a = lParentScreenAlpha;

			if (mEnabled) {
				if (isInClickedState()) {
					entryColor.r = 1.f;
					entryColor.g = 1.f;
					entryColor.b = 1.f;
				} else if (mHasFocus) {
					entryColor.r = .8f;
					entryColor.g = .8f;
					entryColor.b = .8f;
				} else {
					entryColor.r = .6f;
					entryColor.g = .6f;
					entryColor.b = .6f;
				}
			}

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

		else if (mHasFocus && mEnabled) {
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
			final var lMenuFont = mParentScreen.font();

			if (lMenuFont != null) {
				lMenuFont.begin(core.HUD());
				final float lStringWidth = lMenuFont.getStringWidth(mText, lUiTextScale);
				final var lTextColor = ColorConstants.getColor(mEnabled == false ? ColorConstants.GREY_DARK : mHasFocus ? ColorConstants.FLAME : ColorConstants.TextHeadingColor);
				lTextColor.a = lParentScreenAlpha;
				lMenuFont.drawText(mText, (int) (lScreenOffset.x + centerX() - lStringWidth * 0.5f), (int) (lScreenOffset.y + centerY() - lMenuFont.fontHeight() * .5f), mZ, lTextColor, lUiTextScale);

				lMenuFont.end();
			}
		}

		if (mShowInfoIcon)
			drawInfoIcon(core, lSpriteBatch, mInfoIconDstRectangle, 1.f);

		if (mShowWarnIcon)
			drawWarningIcon(core, lSpriteBatch, mWarnIconDstRectangle, 1.f);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			lSpriteBatch.end();
		}
	}

	public void postStencilDraw(LintfordCore core, Screen screen, float parentZDepth) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void drawInfoIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_INFO, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), destRect.width(), destRect.height(), mZ, lColor);
		spriteBatch.end();
	}

	public void drawWarningIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_WARNING, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), destRect.width(), destRect.height(), mZ, lColor);
		spriteBatch.end();
	}

	public void drawDebugCollidableBounds(LintfordCore core, SpriteBatch spriteBatch) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lScreenOffset = mParentScreen.screenPositionOffset();
			spriteBatch.begin(core.HUD());
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ, ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.end();
		}
	}

	public void drawdisabledBlackOverbar(LintfordCore core, SpriteBatch spriteBatch, float screenAlpha) {
		final var lColor = ColorConstants.getColor(.1f, .1f, .1f, .75f * screenAlpha);
		final var lScreenOffset = mParentScreen.screenPositionOffset();

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

	public void onViewportChange(float width, float height) {
		// ignored
	}

	@Override
	public boolean allowGamepadInput() {
		return mParentScreen.allowGamepadInput();
	}

	@Override
	public boolean allowKeyboardInput() {
		return mParentScreen.allowKeyboardInput();
	}

	@Override
	public boolean allowMouseInput() {
		return mParentScreen.allowMouseInput();
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer < 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

	@Override
	public String toolTipText() {
		return mToolTipText;
	}

	@Override
	public boolean isMouseOver() {
		return mIsMouseOver;
	}

	@Override
	public boolean isParentActive() {
		return mParentScreen.isExiting() == false && mParentScreen.screenState() == ScreenState.Active;
	}

	public void onDeselection(InputManager inputManager) {

	}

	public void onClick(InputManager inputManager) {
		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickListener.isActionConsumed())
			return;

		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;
		mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_SELECTED);
		mClickListener.menuEntryOnClick(inputManager, mMenuEntryID);
	}

	@Override
	public boolean isTopHalfOfScreen() {
		// this assumes hud is centered at 0,0
		return mY < 0;
	}

	@Override
	public ContextHintState contextHints() {
		return contextHintState;
	}
}
