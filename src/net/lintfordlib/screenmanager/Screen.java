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
import net.lintfordlib.screenmanager.transitions.TransitionSwipeIn;
import net.lintfordlib.screenmanager.transitions.TransitionSwipeIn.SwipeInDirection;
import net.lintfordlib.screenmanager.transitions.TransitionSwipeOut;
import net.lintfordlib.screenmanager.transitions.TransitionSwipeOut.SwipeOutDirection;

public abstract class Screen implements IInputProcessor {

	// --------------------------------------
	// Enums
	// --------------------------------------

	protected static final float INPUT_TIMER_WAIT = 100.0f;

	// @formatter:off
	public enum ScreenState {
		NONE,					// Screen not yet transitioned.
		ACTIVE, 				// Screen is the active screen
		HIDDEN,					// Screen is in background / sleeping
		
		TRANSITION_STARTING,	// screen has been added to the stack
		TRANSITION_SLEEPING,	// screen is moving down the stack 
		TRANSITION_RESUMING, 	// screen is being recalled from the stack
		TRANSITION_EXITING 		// screen is exiting the stack
	}
	// @formatter:on

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Color screenColor = new Color(ColorConstants.WHITE());
	public final ScreenManager screenManager;

	protected final RendererManager mRendererManager;
	protected SpriteSheetDefinition mCoreSpritesheet;

	protected BaseTransition mTransitionOn;
	protected BaseTransition mTransitionOff;
	protected BaseTransition mTransitionResume;
	protected BaseTransition mTransitionExit;

	protected ScreenState mScreenState;

	protected boolean mOtherScreenHasFocus;
	protected boolean mResourcesLoaded;
	protected boolean mGlInitialized;
	protected boolean mSingletonScreen;
	protected boolean mIsinitialized;
	protected boolean mIsPopup;
	protected boolean mAlwaysOnTop;
	protected boolean mShowMouseCursor;
	protected boolean mCanBeHidden; // cannot be hidden
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

	/** Can be set by a screen to indicate that it wishes to override the UiResolution with the flag value in {@link mStretchUiResolution}. This property is only evaluated during loading screens. */
	protected boolean mOverrideUiStretch;
	protected boolean mStretchUiResolution;

	/** Can be set by a screen to indicate that it wishes to override the GameResolution with the flag value in {@link mStretchGameResolution}. This property is only evaluated during loading screens. */
	protected boolean mOverrideGameStretch;
	protected boolean mStretchGameResolution;

	// --------------------------------------
	// Properties
	// -------------------------------------

	public boolean overrideUiStretch() {
		return mOverrideUiStretch;
	}

	public boolean stretchUiResolution() {
		return mStretchUiResolution;
	}

	public boolean overrideGameStretch() {
		return mOverrideGameStretch;
	}

	public boolean stretchGameResolution() {
		return mStretchGameResolution;
	}

	public RendererManager rendererManager() {
		return mRendererManager;
	}

	public Vector2f screenPositionOffset() {
		return mScreenOffset;
	}

	public LineBatch lineBatch() {
		return screenManager.sharerdResources().uiLineBatch();
	}

	public SpriteBatch spriteBatch() {
		return screenManager.sharerdResources().uiSpriteBatch();
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
		return !mOtherScreenHasFocus && (mScreenState == ScreenState.ACTIVE || mScreenState == ScreenState.TRANSITION_STARTING);
	}

	/** If true, underlying screens in the stack will be visible in the background of this popup screen */
	public boolean isPopup() {
		return mIsPopup;
	}

	/** If true, this screen is currently in the process (transition) of exiting */
	public boolean isExiting() {
		return mScreenState == ScreenState.TRANSITION_EXITING;
	}

	/**
	 * Indicates whether or not this screen can be transition into the {@link ScreenState.HIDDEN} state. Some screens, for example the menu background screen, should always be active, even if they are not the top most in the screen stack.
	 */
	public boolean canBeHidden() {
		return mCanBeHidden;
	}

	public void transitionOn() {
		transitionOn(false);
	}

	public void transitionOn(boolean forceInstance) {
		if (!forceInstance) {
			System.out.println("Screen transitionOn() w/ transition : " + getClass().getSimpleName());

			if (mScreenState == ScreenState.NONE) {
				mScreenState = ScreenState.TRANSITION_STARTING;
			}
		} else {
			System.out.println("Screen transitionOn() w/o transition : " + getClass().getSimpleName());

			mScreenState = ScreenState.ACTIVE;
			mTransitionOn.applyFinishedEffects(this);

			if (mTransitionOn != null)
				mTransitionOn.reset();
		}

	}

	public void transitionOff() {
		transitionOff(false);
	}

	public void transitionOff(boolean forceInstance) {
		if (!mCanBeHidden)
			return;

		if (!forceInstance) {
			System.out.println("Screen transitionOff() w/ transition: " + getClass().getSimpleName());

			if (mScreenState == ScreenState.ACTIVE) {
				mScreenState = ScreenState.TRANSITION_SLEEPING;
			}
		} else {
			System.out.println("Screen transitionOff() w/o transition: " + getClass().getSimpleName());

			mScreenState = ScreenState.HIDDEN;
			mTransitionOff.applyFinishedEffects(this);

			if (mTransitionOff != null)
				mTransitionOff.reset();
		}

	}

	public void transitionExit() {
		transitionExit(false);
	}

	public void transitionExit(boolean forceInstant) {
		if (!forceInstant) {
			System.out.println("Screen transitionExit() w/ transition: " + getClass().getSimpleName());

			if (mScreenState == ScreenState.ACTIVE) {
				mScreenState = ScreenState.TRANSITION_EXITING;
			}
		} else {
			System.out.println("Screen transitionExit() w/o transition: " + getClass().getSimpleName());

			screenManager.removeScreen(this);
		}

	}

	public void transitionResume() {
		transitionResume(false);
	}

	public void transitionResume(boolean forceInstant) {

		if (!forceInstant) {
			System.out.println("Screen transitionResume() w/ transition: " + getClass().getSimpleName());

			if (mScreenState == ScreenState.HIDDEN) {
				mScreenState = ScreenState.TRANSITION_RESUMING;
			}
		} else {
			System.out.println("Screen transitionResume() w/o transition: " + getClass().getSimpleName());

			mScreenState = ScreenState.ACTIVE;
			mTransitionResume.applyFinishedEffects(this);

			if (mTransitionResume != null)
				mTransitionResume.reset();

		}
	}

	public ScreenState screenState() {
		return mScreenState;
	}

	public void forceScreenState(ScreenState newState) {
		mScreenState = newState;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	protected Screen(ScreenManager screenManager) {
		this(screenManager, new RendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber()));
	}

	protected Screen(ScreenManager screenManager, RendererManager rendererManager) {
		this.screenManager = screenManager;

		mScreenState = ScreenState.NONE;

		mTransitionOn = new TransitionSwipeIn(new TimeSpan(200), SwipeInDirection.Right);
		mTransitionOff = new TransitionFadeOut(new TimeSpan(150));
		mTransitionResume = new TransitionFadeIn(new TimeSpan(150));
		mTransitionExit = new TransitionSwipeOut(new TimeSpan(200), SwipeOutDirection.Left);

		if (rendererManager == null) {
			mRendererManager = new RendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber());
		} else {
			mRendererManager = rendererManager;
		}

		mSingletonScreen = false;

		mResourcesLoaded = false;
		mShowMouseCursor = true;

		mCanBeHidden = true;

		mBlockKeyboardInputInBackground = true;
		mBlockMouseInputInBackground = true;
		mBlockGamepadInputInBackground = true;

		screenColor.a = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
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

		mRendererManager.removeAllRenderers();

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

		switch (mScreenState) {
		case TRANSITION_EXITING:
			if (mTransitionExit == null || updateTransition(core.appTime(), mTransitionExit)) {
				screenManager.removeScreen(this);

				if (mTransitionExit != null)
					mTransitionExit.reset();

				System.out.println("transition EXIT finished : " + getClass().getSimpleName());
			}

			break;

		case TRANSITION_SLEEPING:
			if (mTransitionOff == null || updateTransition(core.appTime(), mTransitionOff)) {
				mScreenState = ScreenState.HIDDEN;
				if (mTransitionOff != null)
					mTransitionOff.reset();

				System.out.println("transition SLEEP finished : " + getClass().getSimpleName());
			}
			break;

		case TRANSITION_RESUMING:
			if (mTransitionResume == null || updateTransition(core.appTime(), mTransitionResume)) {
				mScreenState = ScreenState.ACTIVE;
				if (mTransitionResume != null)
					mTransitionResume.reset();

				System.out.println("transition RESUME finished : " + getClass().getSimpleName());
			}
			break;

		case TRANSITION_STARTING:
			if (mTransitionOn == null || updateTransition(core.appTime(), mTransitionOn)) {
				mScreenState = ScreenState.ACTIVE;
				if (mTransitionOn != null)
					mTransitionOn.reset();

				System.out.println("transition START finished : " + getClass().getSimpleName());
			}
			break;

		default: // fall-through / active
		}

		if (!coveredByOtherScreen)
			screenManager.core().controllerManager().update(screenManager.core(), entityGroupUid());

		mRendererManager.update(core);

	}

	public void draw(LintfordCore core) {
		if (!mRendererManager.isLoaded())
			throw new RuntimeException("The RendererManager has been unloaded!");

		mRendererManager.drawStageHierarchy(core);
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

	public void sleepScreen() {
		System.out.println("sleepScreen()");
		if (mTransitionOff == null || mTransitionOff.timeSpan().equals(TimeSpan.zero())) {
			mScreenState = ScreenState.HIDDEN;
		} else {
			mScreenState = ScreenState.TRANSITION_SLEEPING;
		}
	}

	public void resumeScreen() {
		System.out.println("resumeScreen()");
		if (mTransitionResume == null || mTransitionResume.timeSpan().equals(TimeSpan.zero())) {
			mScreenState = ScreenState.ACTIVE;
		} else {
			mScreenState = ScreenState.TRANSITION_RESUMING;
		}
	}

	public void exitScreen() {
		System.out.println("exitScreen()");
		if (mTransitionExit == null || mTransitionExit.timeSpan().equals(TimeSpan.zero())) {
			screenManager.removeScreen(this);
		} else {
			mScreenState = ScreenState.TRANSITION_EXITING;
			screenManager.startPreviousScreenResumeTransition(this);
		}
	}

	public void onScreenAdded() {
		screenManager.contextHintManager().drawVersionBar(mShowContextualFooterBar);
		screenManager.contextHintManager().drawContextBackground(mShowContextualKeyHints);
	}

	public void onScreenRemoved() {

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