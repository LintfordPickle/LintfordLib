package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.transitions.BaseTransition;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public abstract class Screen implements IProcessMouseInput {

	// --------------------------------------
	// Enums
	// --------------------------------------

	public enum ScreenState {
		TransitionOn, Active, TransitionOff, Hidden,
	}

	protected final float INPUT_TIMER_WAIT = 100.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	/** Used both as a ControllerGroupID and RendererGroupID */
	private int mEntityGroupID;

	public final ScreenManager screenManager;
	public final RendererManager rendererManager;

	protected BaseTransition mTransitionOn;
	protected BaseTransition mTransitionOff;
	protected ScreenState mScreenState;
	protected boolean mIsExiting;
	protected boolean mOtherScreenHasFocus;
	protected boolean mIsLoaded;
	protected boolean mSingletonScreen;
	protected boolean mIsinitialized;
	protected boolean mIsPopup;
	protected boolean mAlwaysOnTop;
	protected boolean mShowMouseCursor;
	protected float mR, mG, mB, mA;
	protected boolean mShowInBackground;
	protected boolean mBlockInputInBackground;
	protected float mMouseClickTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public TextureBatchPCT textureBatch() {
		return rendererManager.uiTextureBatch();
	}

	public int entityGroupID() {
		return mEntityGroupID;
	}

	/** Defines if only one instance of this type of object can exist (in the {@link ScreenManager} screen stack) at a time */
	public boolean singletonScreen() {
		return mSingletonScreen;
	}

	/** Some screens are just hotbars, floating on top of the other screens in the {@link ScreenManager} stack. 
	 * Screens which are always on top do not block input to screens below them.*/
	public boolean alwaysOnTop() {
		return mAlwaysOnTop;
	}

	public boolean showInBackground() {
		return mShowInBackground;
	}

	public float r() {
		return mR;
	}

	public float g() {
		return mG;
	}

	public float b() {
		return mB;
	}

	public float a() {
		return mA;
	}

	public void color(float r, float g, float b, float a) {
		mR = r;
		mG = g;
		mB = b;
		mA = a;
	}

	public boolean showMouseCursor() {
		return mShowMouseCursor;
	}

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public boolean isinitialized() {
		return mIsinitialized;
	}

	public boolean isActive() {
		return !mOtherScreenHasFocus && (mScreenState == ScreenState.Active || mScreenState == ScreenState.TransitionOn);
	}

	public boolean isPopup() {
		return mIsPopup;
	}

	public boolean isExiting() {
		return mIsExiting;
	}

	public void isExiting(boolean pIsExiting) {
		mIsExiting = pIsExiting;
	}

	public ScreenState screenState() {
		return mScreenState;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Screen(ScreenManager pScreenManager) {
		mEntityGroupID = BaseEntity.getEntityNumber();

		screenManager = pScreenManager;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(250));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(250));

		rendererManager = new RendererManager(screenManager.core(), getClass().getSimpleName(), mEntityGroupID);

		// By default, screens are not singleton
		mSingletonScreen = false;

		mIsLoaded = false;
		mShowMouseCursor = true; // default on

		mR = mG = mB = 1f;
		mA = 0f; // because we are fading in and out

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mIsExiting = false;
		mIsLoaded = false;

		rendererManager.initialize();

		mIsinitialized = true;

	}

	public void loadGLContent(ResourceManager pResourceManager) {
		rendererManager.loadGLContent(pResourceManager);

		mIsLoaded = true;

	}

	public void unloadGLContent() {
		rendererManager.unloadGLContent();

		screenManager.core().controllerManager().removeControllerGroup(mEntityGroupID);

		mIsLoaded = false;

	}

	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		rendererManager.handleInput(pCore);
		screenManager.core().controllerManager().handleInput(screenManager.core(), mEntityGroupID);

	}

	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		if (!rendererManager.isLoaded())
			throw new RuntimeException("RendererManager not loaded");

		if (mMouseClickTimer > 0) {
			mMouseClickTimer -= pCore.appTime().elapsedTimeMilli();

		}

		mOtherScreenHasFocus = pOtherScreenHasFocus;

		if (mIsExiting) {
			mScreenState = ScreenState.TransitionOff;

			if (updateTransition(pCore.appTime(), mTransitionOff)) {
				screenManager.removeScreen(this);

			}

		}

		else if (pCoveredByOtherScreen) {

			// if covered, then transition the screen off before hiding it.
			if (mScreenState != ScreenState.Hidden && !updateTransition(pCore.appTime(), mTransitionOff)) {
				mScreenState = ScreenState.TransitionOff;

			}

			else {
				mScreenState = ScreenState.Hidden;
				if (mTransitionOff != null)
					mTransitionOff.reset();

			}

		}

		else {

			// If not covered, then transition the screen on before activating it.
			if (mScreenState != ScreenState.Active && !updateTransition(pCore.appTime(), mTransitionOn)) {
				mScreenState = ScreenState.TransitionOn;

			}

			else {
				mScreenState = ScreenState.Active;
				if (mTransitionOn != null)
					mTransitionOn.reset();

			}

			screenManager.core().controllerManager().update(screenManager.core(), mEntityGroupID);

		}

	}

	public void draw(LintfordCore pCore) {
		rendererManager.draw(pCore);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean updateTransition(CoreTime pGameTime, BaseTransition pTransition) {

		if (pTransition == null)
			return true; // finished, nothing to do

		pTransition.updateTransition(this, pGameTime);

		if (pTransition.isFinished())
			return true; // finished

		return false; // not finished

	}

	public void exitScreen() {
		if (mTransitionOff == null || mTransitionOff.timeSpan().equals(TimeSpan.zero())) {
			screenManager.removeScreen(this);

		} else {
			mIsExiting = true;

		}
	}

	/** Called when a {@link Screen} is removed from the {@link ScreenManager}. */
	public void onScreenRemovedFromScreenManager() {

		GLDebug.checkGLErrorsException(getClass().getSimpleName());

		// ResourceManager clear EntityGroupID resources

		rendererManager.unloadGLContent();
		rendererManager.removeAllListeners();
		rendererManager.removeAllRenderers();
		// mRendererManager = null;

	}

	public void onGainedFocus() {
		// Don't allow keyboard capture across screens
		screenManager.core().input().keyboard().stopBufferedTextCapture();

	}

	public void onLostFocus() {

	}

	/** Called when the size of the viewport is changed. */
	public void onViewportChange(float pWidth, float pHeight) {

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