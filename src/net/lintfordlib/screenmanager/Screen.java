package net.lintfordlib.screenmanager;

import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.linebatch.LineBatch;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.time.TimeSpan;
import net.lintfordlib.renderers.RendererManager;
import net.lintfordlib.screenmanager.transitions.BaseTransition;
import net.lintfordlib.screenmanager.transitions.TransitionFadeIn;
import net.lintfordlib.screenmanager.transitions.TransitionFadeOut;

public abstract class Screen implements IInputProcessor {

	// --------------------------------------
	// Enums
	// --------------------------------------

	protected static final float INPUT_TIMER_WAIT = 100.0f;

	public enum ScreenState {
		TRANSITION_ON, ACTIVE, TRANSITION_OFF, HIDDEN,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Color screenColor = new Color(ColorConstants.WHITE);
	public final ScreenManager screenManager;

	protected final RendererManager mRendererManager;
	protected SpriteSheetDefinition mCoreSpritesheet;
	protected BaseTransition mTransitionOn;
	protected BaseTransition mTransitionOff;
	protected ScreenState mScreenState;
	protected boolean mIsExiting;
	protected boolean mOtherScreenHasFocus;
	protected boolean mResourcesLoaded;
	protected boolean mGlInitialized;
	protected boolean mSingletonScreen;
	protected boolean mIsinitialized;
	protected boolean mIsPopup;
	protected boolean mAlwaysOnTop;
	protected boolean mShowMouseCursor;
	protected boolean mShowBackgroundScreens;
	protected boolean mShowContextualFooterBar;
	protected boolean mShowContextualKeyHints;

	protected boolean acceptMouseInput;
	protected boolean acceptKeyboardInput;
	protected boolean acceptGamepadInput;

	protected boolean mBlockKeyboardInputInBackground;
	protected boolean mBlockGamepadInputInBackground;
	protected boolean mBlockMouseInputInBackground;

	protected float mInputTimer;
	protected final Vector2f mScreenOffset = new Vector2f();

	public boolean mOverrideUiStretch;
	public boolean mStretchUiResolution;

	public boolean mOverrideGameStretch;
	public boolean mStretchGameResolution;

	// --------------------------------------
	// Properties
	// -------------------------------------

	public RendererManager rendererManager() {
		return mRendererManager;
	}

	public Vector2f screenPositionOffset() {
		return mScreenOffset;
	}

	public LineBatch lineBatch() {
		return mRendererManager.uiLineBatch();
	}

	public SpriteBatch spriteBatch() {
		return mRendererManager.uiSpriteBatch();
	}

	public int entityGroupUid() {
		return mRendererManager.entityGroupUid();
	}

	/** Defines if only one instance of this type of object can exist (in the {@link ScreenManager} screen stack) at a time */
	public boolean singletonScreen() {
		return mSingletonScreen;
	}

	/**
	 * Some screens are just hotbars, floating on top of the other screens in the {@link ScreenManager} stack. Screens which are always on top do not block input to screens below them.
	 */
	public boolean alwaysOnTop() {
		return mAlwaysOnTop;
	}

	/** Should screens in the background be shown or should they be transitioned off? */
	public boolean showBackgroundScreens() {
		return mShowBackgroundScreens;
	}

	public boolean showMouseCursor() {
		return mShowMouseCursor;
	}

	public boolean isResourcesLoaded() {
		return mResourcesLoaded;
	}

	public boolean isGlInitialized() {
		return mGlInitialized;
	}

	public boolean isinitialized() {
		return mIsinitialized;
	}

	public boolean isActive() {
		return !mOtherScreenHasFocus && (mScreenState == ScreenState.ACTIVE || mScreenState == ScreenState.TRANSITION_ON);
	}

	/** If true, underlying screens in the stack will be visible in the background of this popup screen */
	public boolean isPopup() {
		return mIsPopup;
	}

	/** If true, this screen is currently in the process (transition) of exiting */
	public boolean isExiting() {
		return mIsExiting;
	}

	public void isExiting(boolean pIsExiting) {
		mIsExiting = pIsExiting;
	}

	public void setTransitionOn(BaseTransition transitionOn) {
		mTransitionOn = transitionOn;
	}

	public void setTransitionOff(BaseTransition transitionOff) {
		mTransitionOff = transitionOff;
	}

	public void transitionOff() {
		if (mScreenState == ScreenState.ACTIVE) {
			mScreenState = ScreenState.TRANSITION_OFF;
		}
	}

	public void transitionOn() {
		if (mScreenState == ScreenState.HIDDEN) {
			mScreenState = ScreenState.TRANSITION_ON;
		}
	}

	public ScreenState screenState() {
		return mScreenState;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	protected Screen(ScreenManager screenManager) {
		this(screenManager, new RendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber()));
	}

	protected Screen(ScreenManager screenManager, RendererManager rendererManager) {
		mScreenState = ScreenState.HIDDEN;
		this.screenManager = screenManager;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(200));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(200));

		if (rendererManager == null) {
			mRendererManager = new RendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber());
		} else {
			mRendererManager = rendererManager;
		}

		mSingletonScreen = false;

		mResourcesLoaded = false;
		mShowMouseCursor = true;

		mBlockKeyboardInputInBackground = true;
		mBlockMouseInputInBackground = true;
		mBlockGamepadInputInBackground = true;

		screenColor.a = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mIsExiting = false;
		mResourcesLoaded = false;

		mRendererManager.initialize();

		mIsinitialized = true;
	}

	public void loadResources(ResourceManager resourceManager) {
		mRendererManager.loadResources(resourceManager);
		mRendererManager.increaseGlContentCount();

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();

		initializeGlContent();

		mResourcesLoaded = true;
	}

	public void initializeGlContent() {
		mGlInitialized = true;
	}

	public void unloadResources() {
		if (mRendererManager.decreaseGlContentCount()) {
			mRendererManager.unloadResources();

			screenManager.core().controllerManager().removeControllerGroup(entityGroupUid());

		}

		mCoreSpritesheet = null;

		mResourcesLoaded = false;
		mGlInitialized = false;
	}

	public void handleInput(LintfordCore core) {
		mRendererManager.handleInput(core);

		screenManager.core().controllerManager().handleInput(screenManager.core(), entityGroupUid());
	}

	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		if (mInputTimer > 0)
			mInputTimer -= core.appTime().elapsedTimeMilli();

		mOtherScreenHasFocus = otherScreenHasFocus;

		if (mIsExiting) {
			mScreenState = ScreenState.TRANSITION_OFF;

			if (updateTransition(core.appTime(), mTransitionOff))
				screenManager.removeScreen(this);

			return;
		}

		if (mScreenState == ScreenState.TRANSITION_OFF) {
			if (mTransitionOff == null || updateTransition(core.appTime(), mTransitionOff)) {
				mScreenState = ScreenState.HIDDEN;
				if (mTransitionOff != null)
					mTransitionOff.reset();
			}
		} else if (mScreenState == ScreenState.TRANSITION_ON) {
			if (mTransitionOn == null || updateTransition(core.appTime(), mTransitionOn)) {
				mScreenState = ScreenState.ACTIVE;
				if (mTransitionOn != null)
					mTransitionOn.reset();
			}
		}

		if (!coveredByOtherScreen)
			screenManager.core().controllerManager().update(screenManager.core(), entityGroupUid());

		mRendererManager.update(core);

	}

	public void draw(LintfordCore core) {
		// This will default to rendering all passes for both BaseRenderers and WindowRenderers.
		mRendererManager.draw(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean updateTransition(CoreTime gameTime, BaseTransition transition) {
		if (transition == null)
			return true;

		transition.updateTransition(this, gameTime);
		return transition.isFinished();
	}

	public void exitScreen() {
		if (mTransitionOff == null || mTransitionOff.timeSpan().equals(TimeSpan.zero())) {
			screenManager.removeScreen(this);
		} else {
			mIsExiting = true;
		}
	}

	public void onScreenAdded() {
		screenManager.contextHintManager().drawVersionBar(mShowContextualFooterBar);
		screenManager.contextHintManager().drawContextBackground(mShowContextualKeyHints);
	}

	public void onScreenRemoved() {
		mRendererManager.unloadResources();
		mRendererManager.removeAllListeners();
		mRendererManager.removeAllRenderers();
	}

	public void onGainedFocus() {
		screenManager.core().input().keyboard().stopBufferedTextCapture();

		screenManager.contextHintManager().drawVersionBar(mShowContextualFooterBar);
		screenManager.contextHintManager().drawContextBackground(mShowContextualKeyHints);
	}

	public void onLostFocus() {

	}

	public void onViewportChange(float width, float height) {

	}

	@Override
	public boolean allowGamepadInput() {
		return acceptGamepadInput;
	}

	@Override
	public boolean allowKeyboardInput() {
		return acceptKeyboardInput;
	}

	@Override
	public boolean allowMouseInput() {
		return acceptMouseInput;
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

}