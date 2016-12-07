package net.ld.library.screenmanager;

import java.util.ArrayList;
import java.util.List;

import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.maths.MathHelper;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.core.time.TimeSpan;

public abstract class Screen implements LoadableScreen {

	// =============================================
	// Enums
	// =============================================

	public enum ScreenState {
		TransitionOn, Active, TransitionOff, Hidden,
	}

	protected final float INPUT_TIMER_WAIT = 100.0f;

	// =============================================
	// Variables
	// =============================================

	protected ScreenManager mScreenManager;
	protected DisplayConfig mDisplayConfig;
	protected boolean mIsPopup;
	protected boolean mShowMouseCursor;
	protected TimeSpan mTransitionOnTime;
	protected TimeSpan mTransitionOffTime;
	protected float mTransitionPosition;
	protected ScreenState mScreenState;
	protected boolean mIsExiting;
	protected boolean mOtherScreenHasFocus;
	protected boolean mIsLoaded;
	protected List<GameLoaderPart> mGameLoadableParts;

	// =============================================
	// Properties
	// =============================================

	public boolean showMouseCursor() {
		return mShowMouseCursor;
	}

	public boolean isLoaded() {
		return mIsLoaded;
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

	// =============================================
	// Constructors
	// =============================================

	public Screen(ScreenManager pScreenManager) {

		mScreenManager = pScreenManager;
		mDisplayConfig = pScreenManager.displayConfig();

		mTransitionOnTime = new TimeSpan(0);
		mTransitionOnTime.fromSeconds(0);

		mTransitionOffTime = new TimeSpan(0);
		mTransitionOffTime.fromSeconds(0);

		mIsLoaded = false;
		mShowMouseCursor = true; // defualt on

		mGameLoadableParts = new ArrayList<>();
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise() {
		mIsExiting = false;
	}

	public abstract void loadContent(ResourceManager pResourceManager);

	public abstract void unloadContent();

	public void handleInput(GameTime pGameTime, InputState pInputState, boolean pAcceptMouse, boolean pAcceptKeyboard) {

	}

	public void update(GameTime pGameTime, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {

		mOtherScreenHasFocus = pOtherScreenHasFocus;

		if (mIsExiting) {

			mScreenState = ScreenState.TransitionOff;

			if (!updateTransition(pGameTime, mTransitionOffTime, 1)) {

				mScreenManager.removeScreen(this);

			}

		}

		else if (pCoveredByOtherScreen) {

			// if covered, then transitiion the screen off before hiding it.
			if (updateTransition(pGameTime, mTransitionOffTime, 1)) {

				mScreenState = ScreenState.TransitionOff;
			}

			else {

				mScreenState = ScreenState.Hidden;

			}

		}

		else {

			// if not covered, then transitiion the screen on before activing it.
			if (updateTransition(pGameTime, mTransitionOnTime, -1)) {

				mScreenState = ScreenState.TransitionOn;
			}

			else {

				mScreenState = ScreenState.Active;

			}

		}

	}

	public abstract void draw(RenderState pRenderState);

	// =============================================
	// Methods
	// =============================================

	private boolean updateTransition(GameTime pGameTime, TimeSpan pTime, int pDirection) {

		// How much should we move by?
		float transitionDelta;

		if (pTime.equals(0))
			transitionDelta = 1;
		else
			transitionDelta = (float) (pGameTime.elapseGameTime() / pTime.milliseconds());

		// Update the transition position.
		mTransitionPosition += transitionDelta * pDirection;

		// Did we reach the end of the transition?
		if (((pDirection < 0) && (mTransitionPosition <= 0)) || ((pDirection > 0) && (mTransitionPosition >= 1))) {
			mTransitionPosition = MathHelper.clamp(mTransitionPosition, 0, 1);
			return false;
		}

		// Otherwise we are still busy with the transition.
		return true;

	}

	public void exitScreen() {
		if (mTransitionOffTime.equals(TimeSpan.zero())) {
			// mScreenManager.removeScreen(this);
			mIsExiting = true;
		} else {
			mIsExiting = true;
		}
	}

	@Override
	public List<GameLoaderPart> partsToLoad() {
		return mGameLoadableParts;
	}
}
