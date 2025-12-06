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
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.screenmanager.Screen.ScreenState;
import net.lintfordlib.screenmanager.ScreenManagerConstants.ALIGNMENT;
import net.lintfordlib.screenmanager.ScreenManagerConstants.FILLTYPE;
import net.lintfordlib.screenmanager.animations.AnimationController;
import net.lintfordlib.screenmanager.animations.IUiAnimationTarget;
import net.lintfordlib.screenmanager.animations.UiScaleAnimator;
import net.lintfordlib.screenmanager.entries.EntryInteractions;

public class MenuEntry extends Rectangle implements IInputProcessor, IToolTipProvider, IContextHintProvider, IUiAnimationTarget {

	public class GamepadMenuIcon {

		private boolean mIsManualEnabled;
		private boolean mIsFocusHintEnabled;

		private int mGamepadInputCodeManual;

		public void manualGamepadInputCode(int inputCode) {
			mGamepadInputCodeManual = inputCode;
			mIsManualEnabled = mGamepadInputCodeManual != -1;
		}

		public boolean focusHintEnabled() {
			return mIsFocusHintEnabled;
		}

		public void focusHintEnabled(boolean isEnabled) {
			mIsFocusHintEnabled = isEnabled;
		}

		public void manualInputCode(int gamepadInputCode) {
			mIsManualEnabled = true;
			mGamepadInputCodeManual = gamepadInputCode;
		}

		public int manualGamepadInputCode() {
			return mGamepadInputCodeManual;
		}

		public void disableManualInputCode() {
			mIsManualEnabled = false;
		}

		public void disableFocusInputCode() {
			mIsManualEnabled = false;
		}

		public int getSpriteFrame(int gamepadInputCode) {
			switch (gamepadInputCode) {

			case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_NORTH:
				return CoreTextureNames.TEXTURE_GAMEPAD_YELLOW;

			case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_SOUTH:
				return CoreTextureNames.TEXTURE_GAMEPAD_GREEN;

			case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_EAST:
				return CoreTextureNames.TEXTURE_GAMEPAD_RED;

			case GamepadInputCodes.LINTFORD_GAMEPAD_BUTTON_WEST:
				return CoreTextureNames.TEXTURE_GAMEPAD_BLUE;

			}

			return -1;
		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -226493862481815669L;

	protected static final float FOCUS_TIMER = 500f; // milli

	protected static final float Z_STATE_MODIFIER_PASSIVE = 0.005f; // Entry passive
	protected static final float Z_STATE_MODIFIER_ACTIVE = 0.006f; // Entry active

	public static final String SOUND_ON_CLICK_NAME = "SOUND_MENU_CLICK";

	public static final int ENTRY_DEFAULT_HEIGHT = 32;

	private static MenuEntry MENU_SEPARATOR;

	public static MenuEntry menuSeparator() {
		if (MENU_SEPARATOR == null) {
			MENU_SEPARATOR = new MenuEntry(null, null, null);

			MENU_SEPARATOR.enabled(false);
			MENU_SEPARATOR.isActive(false);
			MENU_SEPARATOR.enableUpdateDraw(false);
			MENU_SEPARATOR.drawButtonBackground(false);
			MENU_SEPARATOR.desiredHeight(5.f);
		}

		return MENU_SEPARATOR;
	}

	public static MenuEntry newMenuSeparator() {
		final var newSeparator = new MenuEntry(null, null, null);

		newSeparator.enabled(false);
		newSeparator.isActive(false);
		newSeparator.enableUpdateDraw(false);
		newSeparator.drawButtonBackground(false);

		return newSeparator;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected MenuScreen mParentScreen;

	protected final Rectangle aabb = new Rectangle();

	protected ALIGNMENT mHorizontalAlignment = ALIGNMENT.CENTER;
	protected ALIGNMENT mVerticalAlignment = ALIGNMENT.CENTER;
	protected FILLTYPE mHorizontalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected FILLTYPE mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
	protected final Rectangle mInfoIconDstRectangle = new Rectangle();
	protected final Rectangle mWarnIconDstRectangle = new Rectangle();
	public final Color entryColor = new Color();
	public final Color textColor = new Color();
	protected SpriteSheetDefinition mCoreSpritesheet;
	public final GamepadMenuIcon gamepadMenuIcon = new GamepadMenuIcon();
	public final ContextHintState contextHintState = new ContextHintState();
	protected float mSeparatorOffsetX;

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

	protected EntryInteractions mClickListener;
	protected int mMenuEntryID;

	protected boolean mDrawBackground;
	protected boolean mHighlightOnHover;
	protected float mAnimationTimer;
	protected boolean mToolTipEnabled;
	protected boolean mIsMouseOver;
	protected float mToolTipTimer;
	protected String mToolTipText;
	protected boolean mShowInfoIcon;
	protected boolean mShowWarnIcon;

	protected float mInputTimer;
	private boolean mIsinitialized;
	private boolean mResourcesLoaded;
	protected float mZ;

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

	protected final AnimationController mAnimation = new AnimationController(this, new UiScaleAnimator());

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void startAnimation() {
		if (mAnimation == null || !mResourcesLoaded || !mIsinitialized)
			return;

		mAnimation.start();
	}

	public float separatorOffsetX() {
		return mSeparatorOffsetX;
	}

	public void separatorOffsetX(float newOffsetX) {
		mSeparatorOffsetX = newOffsetX;
	}

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
		if (mHasFocus == newValue)
			return;

		mHasFocus = newValue;
		if (mHasFocus)
			onGainFocus();
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

	/**
	 * Gets whether the update/draw methods are called automatically by the screenmanager.
	 */
	public boolean enableUpdateDraw() {
		return mEnableUpdateDraw;
	}

	/**
	 * Sets whether the update/draw methods should be called automatically from the screenmanager. Default is true.
	 */
	public void enableUpdateDraw(boolean enableUpdateDraw) {
		mEnableUpdateDraw = enableUpdateDraw;
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

	/** Sets a desired heigt for this entry. Note: Setting this value will automatically change the vertical fill type to 'FILLTYPE.TAKE_DESIRED_SIZE'. */
	public void desiredHeight(float newValue) {
		mDesiredHeight = newValue;

		mVerticalFillType = FILLTYPE.TAKE_DESIRED_SIZE;
	}

	@Override
	public float scale() {
		return mScale;
	}

	@Override
	public void scale(float newValue) {
		mScale = newValue;
	}

	/** Returns false if this is a container entry and there is another child in the *nav left* position. Otherwise false. */
	public boolean leftMostChildSelected() {
		return true;
	}

	/** Returns false if this is a container entry and there is another child in the *nav right* position. Otherwise false. */
	public boolean rightMostChildSelected() {
		return true;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		this(screenManager, parentScreen, null);
	}

	public MenuEntry(ScreenManager screenManager, MenuScreen parentScreen, String menuEntryLabel) {
		mScreenManager = screenManager;
		mParentScreen = parentScreen;

		mText = menuEntryLabel;
		mMenuEntryID = -1;

		mEnableUpdateDraw = true;
		mEnabled = true;
		mIsActive = true;
		mAffectParentStructure = true;
		mCanHaveFocus = true;
		mDrawBackground = true;
		mHighlightOnHover = true;

		mTopMargin = 1f;
		mBottomMargin = 1f;
		mLeftMargin = 10f;
		mRightMargin = 10f;

		mMinWidth = 32.f;
		mMaxWidth = 2048.f;

		mScale = 1.f;
		mMinHeight = 4.f;
		mMaxHeight = 512.f;

		mDesiredWidth = 400.f;
		mDesiredHeight = ENTRY_DEFAULT_HEIGHT;

		mW = mDesiredWidth;
		mH = mDesiredHeight;

		entryColor.setFromColor(ColorConstants.WHITE());
		textColor.setFromColor(ColorConstants.TextEntryColor);

		mVerticalFillType = FILLTYPE.TAKE_WHATS_NEEDED;
		mHorizontalFillType = FILLTYPE.HALF_PARENT;

		// maybe set this from some global constants or options
		gamepadMenuIcon.focusHintEnabled(true);
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
		mCoreSpritesheet = null;
		mResourcesLoaded = false;
	}

	public void updateStructure() {
		final var screenOffset = mParentScreen != null ? mParentScreen.screenPositionOffset() : Vector2f.Zero;
		final var tileSize = Math.min(32, mH);

		if (mShowInfoIcon) // TODO: This isn't correct - the draw code is already offsetting by the screen transition
			mInfoIconDstRectangle.set(screenOffset.x + mX + paddingLeft(), screenOffset.y + mY, tileSize, tileSize);

		if (mShowWarnIcon) // TODO: This isn't correct - the draw code is already offsetting by the screen transition
			mWarnIconDstRectangle.set(screenOffset.x + mX + paddingLeft(), screenOffset.y + mY, tileSize, tileSize);

	};

	public boolean onHandleInputActions(LintfordCore core) {
		return false;
	}

	public boolean onHandleKeyboardInput(LintfordCore core) {
		return false;
	}

	public boolean onHandleGamepadInput(LintfordCore core) {
		return false;
	}

	public boolean onHandleMouseInput(LintfordCore core) {
		if (!mIsActive || !enabled() || readOnly())
			return false;

		if (mParentScreen == null || !mEnabled || mReadOnly)
			return false;

		if (!core.input().mouse().isMouseMenuSelectionEnabled()) {
			mIsMouseOver = false;
			return false;
		}

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode())) {
			mIsMouseOver = false;
			mAnimation.stop();
			return false;
		}

		// isHovering

		if (!mIsMouseOver) {
			mAnimation.start();
			mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_OVER);
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
		if (!mIsActive || !mAffectParentStructure && !mEnableUpdateDraw)
			return;

		final var intersectsUs = intersectsAA(core.HUD().getMouseCameraSpace());
		if (!intersectsUs)
			mIsMouseOver = false;

		mAnimation.update(core);

		final float parentScreenAlpha = screen.screenColor.a;
		entryColor.a = parentScreenAlpha;
		textColor.a = parentScreenAlpha;

		final var deltaTime = (float) core.appTime().elapsedTimeMilli();

		if (mInputTimer >= 0)
			mInputTimer -= deltaTime;

		if (mAnimationTimer > 0)
			mAnimationTimer -= deltaTime;

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
		if (!mIsActive || !mAffectParentStructure || !mEnableUpdateDraw || !mIsinitialized || !mResourcesLoaded)
			return;

		mZ = parentZDepth;

		final var screenOffset = screen.screenPositionOffset();
		final var parentScreenAlpha = screen.screenColor.a;
		final var isAnimationActive = mAnimationTimer > 0.f;

		if (mEnabled) {
			if (isInClickedState()) {
				entryColor.r = 1.f;
				entryColor.g = 1.f;
				entryColor.b = 1.f;
			} else if (mHasFocus) {
				entryColor.r = .8f;
				entryColor.g = .8f;
				entryColor.b = .8f;
			} else if (isAnimationActive) {
				entryColor.setFromColor(ColorConstants.getColorWithAlpha(ColorConstants.SecondaryColor, (mAnimationTimer / 255.f) * parentScreenAlpha));
			} else {
				entryColor.r = .6f;
				entryColor.g = .6f;
				entryColor.b = .6f;
			}
			entryColor.a = parentScreenAlpha;

		} else {
			entryColor.setFromColor(ColorConstants.getColorWithAlpha(ColorConstants.SecondaryColor, .35f));
			entryColor.a = parentScreenAlpha * .6f;
		}

		final var spriteBatch = mParentScreen.spriteBatch();

		if (mDrawBackground) {
			boolean use5Steps = mW > 32 * 8;
			final var tileSize = 32.f;
			final var halfWidth = mW * .5f;
			var left = screenOffset.x + centerX() - halfWidth;
			final var innerWidth = (mW - 32 * (use5Steps ? 4 : 2));
			entryColor.a = parentScreenAlpha;

			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(entryColor);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_LEFT, left, screenOffset.y + centerY() - mH / 2, tileSize, mH, mZ);
			if (use5Steps)
				spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDLEFT, left += 32.f, screenOffset.y + centerY() - mH / 2, tileSize, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MID, left += 32, screenOffset.y + centerY() - mH / 2, innerWidth, mH, mZ);
			if (use5Steps)
				spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_MIDRIGHT, (left -= 32) + halfWidth * 2.f - 96.f, screenOffset.y + centerY() - mH / 2, tileSize, mH, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_MENU_BUTTON_HORIZONTAL_RIGHT, (left -= 32) + halfWidth * 2 - 32, screenOffset.y + centerY() - mH / 2, tileSize, mH, mZ);
			spriteBatch.end();
		}

		if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, spriteBatch);

		// Render the MenuEntry label
		if (mText != null && mText.length() > 0) {
			final float uiTextScale = mScreenManager.UiStructureController().uiTextScaleFactor() * mScale;

			final var menuFont = mParentScreen.font();

			if (menuFont != null) {
				menuFont.begin(core.HUD());
				final var stringWidth = menuFont.getStringWidth(mText, uiTextScale);
				final var textColor = ColorConstants.getTempColorCopy(!mEnabled ? ColorConstants.GREY_DARK() : mHasFocus ? ColorConstants.MenuEntryHighlightColor : ColorConstants.TextHeadingColor);
				textColor.a = parentScreenAlpha;

				if (mHasFocus && mEnabled)
					menuFont.setTextColor(ColorConstants.MenuEntrySelectedColor);
				else
					menuFont.setTextColor(textColor);

				menuFont.setShadowColorRGBA(0.f, 0.f, 0.f, parentScreenAlpha);
				menuFont.drawShadowedText(mText, screenOffset.x + centerX() - stringWidth * 0.5f, screenOffset.y + centerY() - menuFont.fontHeight() * .5f, mZ, 1.f, 1.f, uiTextScale);

				menuFont.end();
			}
		}

		drawGamepadIcon(core, spriteBatch, screenOffset.x + mX + mW - 16, screenOffset.y + mY + mH - 16, parentScreenAlpha);

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, parentScreenAlpha);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, parentScreenAlpha);

		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, screenOffset.x + mX, screenOffset.y + mY, mW, mH, mZ);
			spriteBatch.end();
		}
	}

	protected void renderHighlight(LintfordCore core, Screen screen, boolean renderFilled, SpriteBatch spriteBatch) {
		renderHighlight(core, screen, renderFilled, spriteBatch, this);
	}

	public void renderHighlight(LintfordCore core, Screen screen, boolean renderFilled, SpriteBatch spriteBatch, Rectangle rect) {
		final var lScreenOffset = screen.screenPositionOffset();
		final var lParentScreenAlpha = screen.screenColor.a;

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorWhite();
		spriteBatch.setColorA(lParentScreenAlpha);

		final var spriteFrameLT = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_TOP);
		final var spriteFrameLC = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_CENTER);
		final var spriteFrameLB = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_LEFT_BOTTOM);

		final var spriteFrameRT = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_TOP);
		final var spriteFrameRC = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_CENTER);
		final var spriteFrameRB = mCoreSpritesheet.getSpriteFrame(CoreTextureNames.TEXTURE_ENTRY_HIGHLIGHT_FULL_RIGHT_BOTTOM);

		final var centerHeight = rect.height() - 8 - 8;
		spriteBatch.draw(mCoreSpritesheet, spriteFrameLT, lScreenOffset.x + rect.left(), lScreenOffset.y + rect.top(), 4, 8, mZ);
		spriteBatch.draw(mCoreSpritesheet, spriteFrameLC, lScreenOffset.x + rect.left(), lScreenOffset.y + rect.top() + 8, 4, centerHeight, mZ);
		spriteBatch.draw(mCoreSpritesheet, spriteFrameLB, lScreenOffset.x + rect.left(), lScreenOffset.y + rect.top() + centerHeight + 8, 4, 8, mZ);

		if (renderFilled) {
			spriteBatch.draw(mCoreSpritesheet, spriteFrameRT, lScreenOffset.x + rect.left() + 4, lScreenOffset.y + rect.top(), rect.width() - 4, 8, mZ);
			spriteBatch.draw(mCoreSpritesheet, spriteFrameRC, lScreenOffset.x + rect.left() + 4, lScreenOffset.y + rect.top() + 8, rect.width() - 4, centerHeight, mZ);
			spriteBatch.draw(mCoreSpritesheet, spriteFrameRB, lScreenOffset.x + rect.left() + 4, lScreenOffset.y + rect.top() + centerHeight + 8, rect.width() - 4, 8, mZ);
		}
		spriteBatch.end();
	}

	public void postStencilDraw(LintfordCore core, Screen screen, float parentZDepth) {
		// ignored in base
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean resolveChildEntry(MenuEntry entry) {

		// to be overriden in container entries. Given a MenuEntry, if it exists within the container, the selectedEntry index should be updated to match it.

		return false;
	}

	public boolean setFocusOnChildEntry(MenuEntry entry) {

		// to be overriden in container entries. Given a MenuEntry, if it exists within the container, the selectedEntry index should be updated to match it, and the focus is set to that entry.

		return false;
	}

	public void drawGamepadIcon(LintfordCore core, SpriteBatch spriteBatch, float posX, float posY, float screenAlpha) {
		if (parentScreen().otherScreenHasFocus())
			return; // don't render gamepad icons on background screens

		if (!core.input().gamepads().isGamepadAvailable())
			return;

		int spriteFrameUid = -1;
		if (gamepadMenuIcon.mIsManualEnabled) {
			spriteFrameUid = gamepadMenuIcon.getSpriteFrame(gamepadMenuIcon.manualGamepadInputCode());
		} else if (mHasFocus) {
			if (!gamepadMenuIcon.focusHintEnabled())
				return;

			spriteFrameUid = CoreTextureNames.TEXTURE_GAMEPAD_GREEN;
		}

		if (spriteFrameUid == -1)
			return;

		spriteBatch.begin(core.HUD());

		final var w = 16 * mScale;
		final var h = 16 * mScale;

		spriteBatch.setColorRGBA(.1f, .1f, .1f, screenAlpha * 0.7f);
		spriteBatch.drawAroundCenter(mCoreSpritesheet, spriteFrameUid, posX, posY, w, h, 0, -w / 2, -h / 2, 1.f);

		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, screenAlpha);
		spriteBatch.drawAroundCenter(mCoreSpritesheet, spriteFrameUid, posX, posY - 2, w, h, 0, -w / 2, -h / 2, 1.f);

		spriteBatch.end();
	}

	public void drawInfoIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lColor);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_INFO, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), 25, 25, mZ);
		spriteBatch.end();
	}

	public void drawWarningIcon(LintfordCore core, SpriteBatch spriteBatch, Rectangle destRect, float screenAlpha) {
		final var lColor = ColorConstants.getColor(1.f, 1.f, 1.f, screenAlpha);
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(lColor);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_WARNING, lScreenOffset.x + destRect.x(), lScreenOffset.y + destRect.y(), 25, 25, mZ);
		spriteBatch.end();
	}

	public void drawDebugCollidableBounds(LintfordCore core, SpriteBatch spriteBatch) {
		if (ConstantsApp.getBooleanValueDef("DEBUG_SHOW_UI_COLLIDABLES", false)) {
			final var lScreenOffset = mParentScreen.screenPositionOffset();
			spriteBatch.begin(core.HUD());
			spriteBatch.setColor(ColorConstants.Debug_Transparent_Magenta);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + mX, lScreenOffset.y + mY, mW, mH, mZ);
			spriteBatch.end();
		}
	}

	public void drawdisabledBlackOverbar(LintfordCore core, SpriteBatch spriteBatch, float screenAlpha) {
		final var lScreenOffset = mParentScreen.screenPositionOffset();

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(.1f, .1f, .1f, .75f * screenAlpha);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, lScreenOffset.x + centerX() - (mW / 2), lScreenOffset.y + centerY() - mH / 2, mW, mH, mZ);
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
		return !mParentScreen.isExiting() && mParentScreen.screenState() == ScreenState.ACTIVE;
	}

	public void onActivate(InputManager inputManager) {
		// ignored in base
	}

	public void onDeactivation(InputManager inputManager) {
		// ignored in base
	}

	public void onClick(InputManager inputManager) {

		if (mParentScreen != null)
			mParentScreen.menuEntryOnClick(inputManager, this);

		if (mClickListener == null || mMenuEntryID == -1)
			return;

		if (mClickListener.isActionConsumed())
			return;

		mAnimationTimer = MenuScreen.ANIMATION_TIMER_LENGTH;
		mScreenManager.uiSounds().play(ConstantsScreenManagerAudio.SCREENMANAGER_AUDIO_ENTRY_SELECTED);

		mClickListener.menuEntryOnClick(inputManager, mMenuEntryID);

		// This should be called from the MenuEntry super classes, to notify of state changes.
		// mClickListener.onMenuEntryChanged(this);
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

	/**
	 * Sets the gamepad hint code to be shown when a controller is available. The button shown is not dependant on the state of the button (i.e. hasFocus etc.). Setting the hintCode has not effect on the bound logic. Pass -1 to disable.
	 */
	public void setGamepadHintCode(int gamepadInputCode) {
		if (gamepadInputCode == -1) {
			gamepadMenuIcon.disableManualInputCode();
			return;
		}

		gamepadMenuIcon.manualGamepadInputCode(gamepadInputCode);
	}

	// ---

	/** Returns navigation handled */
	public boolean onNavigationUp(LintfordCore core) {
		return false;
	}

	/** Returns navigation handled */
	public boolean onNavigationDown(LintfordCore core) {
		return false;
	}

	/** Returns navigation handled */
	public boolean onNavigationLeft(LintfordCore core) {
		return false;
	}

	/** Returns navigation handled */
	public boolean onNavigationRight(LintfordCore core) {
		return false;
	}

	// Gives entries an opportunity onGainFocus to select a child entry by default (e.g. in horizontal groups).
	public boolean onNavigationGainFocus(LintfordCore core) {
		return false;
	}

	public void onGainFocus() {

	}

}
