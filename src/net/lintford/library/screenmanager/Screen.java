package net.lintford.library.screenmanager;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.LintfordCore.CoreTime;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.core.maths.Vector2f;
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

	public final ScreenManager screenManager;
	public final RendererManager rendererManager;
	public final Color screenColor = new Color(ColorConstants.WHITE);
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
	protected boolean mBlockInputInBackground;
	public boolean acceptMouseInput;
	public boolean acceptKeyboardInput;
	protected float mMouseClickTimer;
	private final Vector2f mScreenOffset = new Vector2f();

	// --------------------------------------
	// Properties
	// -------------------------------------

	public Vector2f screenPositionOffset() {
		return mScreenOffset;
	}

	public SpriteBatch spriteBatch() {
		return rendererManager.uiSpriteBatch();
	}

	public TextureBatchPCT textureBatch() {
		return rendererManager.uiTextureBatch();
	}

	public int entityGroupID() {
		return rendererManager.entityGroupID;
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

	public boolean isPopup() {
		return mIsPopup;
	}

	public boolean isExiting() {
		return mIsExiting;
	}

	public void isExiting(boolean pIsExiting) {
		mIsExiting = pIsExiting;
	}

	public void setTransitionOn(BaseTransition pNewTransition) {
		mTransitionOn = pNewTransition;
	}

	public void setTransitionOff(BaseTransition pNewTransition) {
		mTransitionOff = pNewTransition;
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

	public Screen(ScreenManager pScreenManager) {
		this(pScreenManager, new RendererManager(pScreenManager.core(), BaseEntity.getEntityNumber()));
	}

	public Screen(ScreenManager pScreenManager, RendererManager pRendererManager) {
		mScreenState = ScreenState.Hidden;
		screenManager = pScreenManager;

		mTransitionOn = new TransitionFadeIn(new TimeSpan(200));
		mTransitionOff = new TransitionFadeOut(new TimeSpan(200));

		if (pRendererManager == null) {
			rendererManager = new RendererManager(screenManager.core(), BaseEntity.getEntityNumber());
		} else {
			rendererManager = pRendererManager;
		}

		mSingletonScreen = false;

		mResourcesLoaded = false;
		mShowMouseCursor = true; // default on

		screenColor.a = 1.f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		mIsExiting = false;
		mResourcesLoaded = false;

		rendererManager.initialize();

		mIsinitialized = true;

	}

	/** Maybe run on a shared context / background thread */
	public void loadResources(ResourceManager pResourceManager) {
		rendererManager.loadResources(pResourceManager);
		rendererManager.increaseGlContentCount();

		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();
		mResourcesLoaded = true;
	}

	public void initializeGlContent() {
		mGlInitialized = true;
	}

	public void unloadResources() {
		if (rendererManager.decreaseGlContentCount()) {
			rendererManager.unloadResources();
		}

		screenManager.core().controllerManager().removeControllerGroup(entityGroupID());

		mCoreSpritesheet = null;

		mResourcesLoaded = false;
		mGlInitialized = false;
	}

	public void handleInput(LintfordCore pCore) {
		rendererManager.handleInput(pCore);
		screenManager.core().controllerManager().handleInput(screenManager.core(), entityGroupID());

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
			return;
		}

		if (mScreenState == ScreenState.TransitionOff) {
			if (mTransitionOff == null || updateTransition(pCore.appTime(), mTransitionOff)) {
				mScreenState = ScreenState.Hidden;
				if (mTransitionOff != null)
					mTransitionOff.reset();
			}
		} else if (mScreenState == ScreenState.TransitionOn) {
			if (mTransitionOn == null || updateTransition(pCore.appTime(), mTransitionOn)) {
				mScreenState = ScreenState.Active;
				if (mTransitionOn != null)
					mTransitionOn.reset();
			}
		}

		if (!pCoveredByOtherScreen) {
			screenManager.core().controllerManager().update(screenManager.core(), entityGroupID());
		}
	}

	public void draw(LintfordCore pCore) {
		if (isGlInitialized() == false) {
			initializeGlContent();
		}

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

		rendererManager.unloadResources();
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