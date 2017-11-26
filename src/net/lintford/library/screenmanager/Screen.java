package net.lintford.library.screenmanager;

import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.screenmanager.transitions.BaseTransition;
import net.lintford.library.screenmanager.transitions.TransitionFadeIn;
import net.lintford.library.screenmanager.transitions.TransitionFadeOut;

public abstract class Screen {

	// The RENDER_RESOLUTION is mapped in the options to the resolution selected by the player.
	static enum RENDER_RESOLUTION {
		UI, // Resolution to render all UI in, e.g. 800x600
		GAME, // Resolution to render all GAME in, e.g. 1024x768
	}

	// --------------------------------------
	// Enums
	// --------------------------------------

	public enum TRANSITION {
		On, Off,
	}

	public enum ScreenState {
		TransitionOn, Active, TransitionOff, Hidden,
	}

	protected final float INPUT_TIMER_WAIT = 100.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ScreenManager mScreenManager;
	protected DisplayConfig mDisplayConfig;
	protected BaseTransition mTransitionOn;
	protected BaseTransition mTransitionOff;
	protected ScreenState mScreenState;
	protected boolean mIsExiting;
	protected boolean mOtherScreenHasFocus;
	protected boolean mIsLoaded;
	protected boolean mIsInitialised;
	protected boolean mIsPopup;
	protected boolean mShowMouseCursor;
	protected float mR, mG, mB, mA;

	protected RenderTarget mScreenRenderTarget;

	protected boolean mShowInBackground;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public boolean isInitialised() {
		return mIsInitialised;
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

	public ScreenManager screenManager() {
		return mScreenManager;
	}

	public void screenManager(ScreenManager pScreenManager) {
		mScreenManager = pScreenManager;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public Screen(ScreenManager pScreenManager) {

		mScreenManager = pScreenManager;
		mDisplayConfig = pScreenManager.masterConfig().displayConfig();

		mTransitionOn = new TransitionFadeIn(new TimeSpan(250));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(250));

		mIsLoaded = false;
		mShowMouseCursor = true; // default on

		mR = mG = mB = 1f;
		mA = 0f; // because we are fading in and out
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {
		mIsExiting = false;
		mIsLoaded = false;

		mIsInitialised = true;

	}

	public abstract void loadGLContent(ResourceManager pResourceManager);

	public abstract void unloadGLContent();

	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {

	}

	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		mOtherScreenHasFocus = pOtherScreenHasFocus;

		if (mIsExiting) {
			mScreenState = ScreenState.TransitionOff;

			if (updateTransition(pGameTime, mTransitionOff)) {
				mScreenManager.removeScreen(this);

			}

		}

		else if (pCoveredByOtherScreen) {

			// if covered, then transitiion the screen off before hiding it.
			if (mScreenState != ScreenState.Hidden && !updateTransition(pGameTime, mTransitionOff)) {
				mScreenState = ScreenState.TransitionOff;

			}

			else {
				mScreenState = ScreenState.Hidden;
				if (mTransitionOff != null)
					mTransitionOff.reset();

			}

		}

		else {

			// if not covered, then transitiion the screen on before activing it.
			if (mScreenState != ScreenState.Active && !updateTransition(pGameTime, mTransitionOn)) {
				mScreenState = ScreenState.TransitionOn;

			}

			else {
				mScreenState = ScreenState.Active;
				if (mTransitionOn != null)
					mTransitionOn.reset();

			}

		}

	}

	public void updateStructure() {

	}

	public void draw(RenderState pRenderState) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private boolean updateTransition(GameTime pGameTime, BaseTransition pTransition) {

		if (pTransition == null)
			return true; // finished, nothing to do

		pTransition.updateTransition(this, pGameTime);

		if (pTransition.isFinished())
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

	public void onGainedFocus() {

		// Don't allow keyboard capture across screens
		mScreenManager.inputState().stopCapture();

	}

	public void onLostFocus() {

	}

}