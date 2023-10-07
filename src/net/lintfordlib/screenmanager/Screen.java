package net.lintfordlib.screenmanager;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.ResourceGroupProvider;
import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.LintfordCore.CoreTime;
import net.lintfordlib.core.graphics.Color;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
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

	protected final float INPUT_TIMER_WAIT = 100.0f;

	public enum ScreenState {
		TransitionOn, Active, TransitionOff, Hidden,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Color screenColor = new Color(ColorConstants.WHITE);
	protected final ScreenManager mScreenManager;
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

	public boolean acceptMouseInput;
	public boolean acceptKeyboardInput;
	public boolean acceptGamepadInput;

	protected boolean mBlockKeyboardInputInBackground;
	protected boolean mBlockGamepadInputInBackground;
	protected boolean mBlockMouseInputInBackground;

	protected float mMouseClickTimer;
	protected final Vector2f mScreenOffset = new Vector2f();

	// --------------------------------------
	// Properties
	// -------------------------------------

	public ScreenManager screenManager() {
		return mScreenManager;
	}

	public RendererManager rendererManager() {
		return mRendererManager;
	}

	public Vector2f screenPositionOffset() {
		return mScreenOffset;
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
		return !mOtherScreenHasFocus && (mScreenState == ScreenState.Active || mScreenState == ScreenState.TransitionOn);
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
		if (mScreenState == ScreenState.Active) {
			mScreenState = ScreenState.TransitionOff;
		}
	}

	public void transitionOn() {
		if (mScreenState == ScreenState.Hidden) {
			mScreenState = ScreenState.TransitionOn;
		}
	}

	public ScreenState screenState() {
		return mScreenState;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Screen(ScreenManager screenManager) {
		this(screenManager, new RendererManager(screenManager.core(), ResourceGroupProvider.getRollingEntityNumber()));
	}

	public Screen(ScreenManager screenManager, RendererManager rendererManager) {
		mScreenState = ScreenState.Hidden;
		mScreenManager = screenManager;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(200));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(200));

		if (rendererManager == null) {
			mRendererManager = new RendererManager(mScreenManager.core(), ResourceGroupProvider.getRollingEntityNumber());
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
		if (mRendererManager.decreaseGlContentCount())
			mRendererManager.unloadResources();

		mScreenManager.core().controllerManager().removeControllerGroup(entityGroupUid());

		mCoreSpritesheet = null;

		mResourcesLoaded = false;
		mGlInitialized = false;
	}

	public void handleInput(LintfordCore core) {
		mRendererManager.handleInput(core);

		mScreenManager.core().controllerManager().handleInput(mScreenManager.core(), entityGroupUid());
	}

	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		if (mMouseClickTimer > 0)
			mMouseClickTimer -= core.appTime().elapsedTimeMilli();

		mOtherScreenHasFocus = otherScreenHasFocus;

		if (mIsExiting) {
			mScreenState = ScreenState.TransitionOff;

			if (updateTransition(core.appTime(), mTransitionOff))
				mScreenManager.removeScreen(this);

			return;
		}

		if (mScreenState == ScreenState.TransitionOff) {
			if (mTransitionOff == null || updateTransition(core.appTime(), mTransitionOff)) {
				mScreenState = ScreenState.Hidden;
				if (mTransitionOff != null)
					mTransitionOff.reset();
			}
		} else if (mScreenState == ScreenState.TransitionOn) {
			if (mTransitionOn == null || updateTransition(core.appTime(), mTransitionOn)) {
				mScreenState = ScreenState.Active;
				if (mTransitionOn != null)
					mTransitionOn.reset();
			}
		}

		if (!coveredByOtherScreen)
			mScreenManager.core().controllerManager().update(mScreenManager.core(), entityGroupUid());

		mRendererManager.update(core);

	}

	public void draw(LintfordCore core) {
		mRendererManager.draw(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean updateTransition(CoreTime gameTime, BaseTransition transition) {
		if (transition == null)
			return true;

		transition.updateTransition(this, gameTime);

		if (transition.isFinished())
			return true; // finished

		return false; // not finished
	}

	public void exitScreen() {
		if (mTransitionOff == null || mTransitionOff.timeSpan().equals(TimeSpan.zero())) {
			mScreenManager.removeScreen(this);
		} else {
			mIsExiting = true;
		}
	}

	public void onScreenAdded() {
		mScreenManager.contextHintManager().drawVersionBar(mShowContextualFooterBar);
		mScreenManager.contextHintManager().drawContextBackground(mShowContextualKeyHints);
	}

	public void onScreenRemoved() {
		mRendererManager.unloadResources();
		mRendererManager.removeAllListeners();
		mRendererManager.removeAllRenderers();
	}

	public void onGainedFocus() {
		mScreenManager.core().input().keyboard().stopBufferedTextCapture();

		mScreenManager.contextHintManager().drawVersionBar(mShowContextualFooterBar);
		mScreenManager.contextHintManager().drawContextBackground(mShowContextualKeyHints);
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
		return mMouseClickTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseClickTimer = 200;
	}
}