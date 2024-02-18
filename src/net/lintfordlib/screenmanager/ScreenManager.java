package net.lintfordlib.screenmanager;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.audio.AudioFireAndForgetManager;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.graphics.fonts.FontMetaData;
import net.lintfordlib.core.input.IInputClickedFocusTracker;
import net.lintfordlib.options.IResizeListener;
import net.lintfordlib.screenmanager.Screen.ScreenState;
import net.lintfordlib.screenmanager.toast.ToastManager;

public class ScreenManager implements IInputClickedFocusManager {

	public static final FontMetaData ScreenManagerFonts = new FontMetaData();

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String FONT_MENU_TITLE_NAME = "FONT_TITLE";
	public static final String FONT_MENU_BOLD_ENTRY_NAME = "FONT_BOLD_ENTRY";
	public static final String FONT_MENU_ENTRY_NAME = "FONT_ENTRY";
	public static final String FONT_MENU_TOOLTIP_NAME = "FONT_MENU_TOOLTIP_NAME";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LintfordCore mLWJGLCore;

	private final List<Screen> mScreens = new ArrayList<>();
	private final List<Screen> mScreensToUpdate = new ArrayList<>();
	private final List<Screen> mScreensToAdd = new ArrayList<>();

	private ToolTip mToolTip;
	private ContextHintManager mContextHintManager;
	private ResourceManager mResourceManager;
	private AudioFireAndForgetManager mUISoundManager;
	private ToastManager mToastManager;
	private boolean mIsinitialized;
	private boolean mResourcesLoaded;
	private int mScreenUIDCounter;
	private HudLayoutController mUiStructureController;
	private IResizeListener mResizeListener;
	protected float mColumnMaxWidth;
	protected IInputClickedFocusTracker mTrackedInputControl;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ContextHintManager contextHintManager() {
		return mContextHintManager;
	}

	public IInputClickedFocusTracker getTrackedClickedFocusControl() {
		return mTrackedInputControl;
	}

	public void setTrackedClickedFocusControl(IInputClickedFocusTracker controlToTrack) {
		mTrackedInputControl = controlToTrack;
	}

	public float columnMaxWidth() {
		return mColumnMaxWidth;
	}

	public void columnMaxWidth(float newColumnMaxWidth) {
		mColumnMaxWidth = newColumnMaxWidth;
	}

	public ToastManager toastManager() {
		return mToastManager;
	}

	public HudLayoutController UiStructureController() {
		return mUiStructureController;
	}

	public AudioFireAndForgetManager uiSounds() {
		return mUISoundManager;
	}

	public ResourceManager resources() {
		return mResourceManager;
	}

	public ToolTip toolTip() {
		return mToolTip;
	}

	public List<Screen> screens() {
		return mScreens;
	}

	public LintfordCore core() {
		return mLWJGLCore;
	}

	public int getNewUUID() {
		return mScreenUIDCounter++;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScreenManager(LintfordCore core) {
		mLWJGLCore = core;
		mScreenUIDCounter = 100;

		mToastManager = new ToastManager();
		mContextHintManager = new ContextHintManager();
		mToolTip = new ToolTip();

		mIsinitialized = false;
		mResourcesLoaded = false;

		ScreenManagerFonts.AddIfNotExists(FONT_MENU_TITLE_NAME, "/res/fonts/fontCoreText.json");
		ScreenManagerFonts.AddIfNotExists(FONT_MENU_ENTRY_NAME, "/res/fonts/fontCoreText.json");
		ScreenManagerFonts.AddIfNotExists(FONT_MENU_BOLD_ENTRY_NAME, "/res/fonts/fontCoreText.json");
		ScreenManagerFonts.AddIfNotExists(FONT_MENU_TOOLTIP_NAME, "/res/fonts/fontCoreText.json");
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {
		final int lScreensToAddCount = mScreensToAdd.size();
		for (int i = 0; i < lScreensToAddCount; i++) {
			mScreensToAdd.get(i).initialize();
		}

		mUiStructureController = (HudLayoutController) mLWJGLCore.controllerManager().getControllerByNameRequired(HudLayoutController.CONTROLLER_NAME, LintfordCore.CORE_ENTITY_GROUP_ID);

		mIsinitialized = true;
	}

	public void loadResources(final ResourceManager resourceManager) {
		mResourceManager = resourceManager;

		final int lScreenToAddCount = mScreensToAdd.size();
		for (int i = 0; i < lScreenToAddCount; i++) {
			mScreensToAdd.get(i).loadResources(resourceManager);
		}

		mUISoundManager = new AudioFireAndForgetManager(resourceManager.audioManager());
		mUISoundManager.acquireAudioSources(2);

		mToolTip.loadResources(resourceManager);
		mToastManager.loadResources(resourceManager);
		mContextHintManager.loadGlContent(mResourceManager);

		// Add a viewport listener so the screenmanager screens can react to changes in window size
		mResizeListener = new IResizeListener() {
			@Override
			public void onResize(final int pWidth, final int pHeight) {
				onViewportChanged(pWidth, pHeight);
			}
		};

		core().config().display().addResizeListener(mResizeListener);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Finished loadingGLContent");

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		final int lScreenCount = mScreens.size();
		for (int i = 0; i < lScreenCount; i++) {
			mScreens.get(i).unloadResources();
		}

		mToolTip.unloadResources();
		mToastManager.unloadResources();
		mContextHintManager.unloadResources();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Finished ScreenManager.UnloadResources");

		mResourcesLoaded = false;
	}

	public void handleInput(LintfordCore core) {
		if (mScreens == null || mScreens.size() == 0)
			return;

		var acceptKeyboardInput = true;
		var acceptGamepadInput = true;
		var acceptMouseInput = true;

		final int lScreenCount = mScreens.size() - 1;
		for (int i = lScreenCount; i >= 0; i--) {
			final var lScreen = mScreens.get(i);

			lScreen.acceptKeyboardInput = acceptKeyboardInput;
			lScreen.acceptGamepadInput = acceptGamepadInput;
			lScreen.acceptMouseInput = acceptMouseInput;

			if (lScreen.screenState() == ScreenState.Active)
				lScreen.handleInput(core);

			acceptKeyboardInput = acceptKeyboardInput && !lScreen.mBlockKeyboardInputInBackground;
			acceptGamepadInput = acceptGamepadInput && !lScreen.mBlockGamepadInputInBackground;
			acceptMouseInput = acceptMouseInput && !lScreen.mBlockMouseInputInBackground;
		}

		if (mTrackedInputControl != null) {
			if (mTrackedInputControl.inputHandledInCoreFrame() == false) {
				mTrackedInputControl.handleInput(core, this);
			}

			mTrackedInputControl.resetInputHandledInCoreFrameFlag();
			if (core.input().mouse().isMouseLeftButtonDown() == false) {
				mTrackedInputControl = null;
			}
		}
	}

	private void updateScreensToAdd(LintfordCore core) {
		// First update transitions
		final int lToAddCount = mScreensToAdd.size();
		if (lToAddCount > 0) {
			boolean lReadyToAddScreen = false;
			final var lNextScreenToAdd = mScreensToAdd.get(0);
			final var lTopScreen = getTopScreen();
			if (lTopScreen != null) {
				if (lTopScreen.screenState() == ScreenState.Active) {
					lTopScreen.onLostFocus();
					if (lNextScreenToAdd.showBackgroundScreens() == false && lNextScreenToAdd.alwaysOnTop() == false)
						lTopScreen.transitionOff();
					else {
						lReadyToAddScreen = true;
					}
				} else if (lTopScreen.screenState() == ScreenState.Hidden || lNextScreenToAdd.showBackgroundScreens()) {
					lReadyToAddScreen = true;
				}
			} else {
				lReadyToAddScreen = true;
			}

			if (lReadyToAddScreen) {
				mScreensToAdd.remove(0);
				lNextScreenToAdd.transitionOn();
				mScreens.add(lNextScreenToAdd);
			}
		}
	}

	public void update(LintfordCore core) {
		if (!mIsinitialized || !mResourcesLoaded)
			return;

		boolean lOtherScreenHasFocus = false;
		boolean lCoveredByOtherScreen = false;

		updateScreensToAdd(core);

		mScreensToUpdate.clear();

		final int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreensToUpdate.add(mScreens.get(i));
		}

		final var lTopMostScreen = getTopScreen();
		if (lTopMostScreen != null) {
			if (lTopMostScreen.screenState() == ScreenState.Hidden && lTopMostScreen.isExiting() == false)
				lTopMostScreen.transitionOn();
		}

		while (mScreensToUpdate.size() > 0) {
			final var lScreen = mScreensToUpdate.get(mScreensToUpdate.size() - 1);

			mScreensToUpdate.remove(mScreensToUpdate.size() - 1);

			if (mScreensToAdd.size() > 0)
				lCoveredByOtherScreen = true;

			lScreen.update(core, lOtherScreenHasFocus, lCoveredByOtherScreen);

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {
				lOtherScreenHasFocus = true;
			}
			if (!lScreen.isPopup()) {
				lCoveredByOtherScreen = true;
			}
		}

		mToastManager.update(core);
		mToolTip.update(core);
		mContextHintManager.update(core);
	}

	public void draw(LintfordCore core) {
		if (!mIsinitialized || !mResourcesLoaded)
			return;

		mScreensToUpdate.clear();

		final int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreensToUpdate.add(mScreens.get(i));
		}

		final var lNumScreens = mScreensToUpdate.size();
		for (int i = 0; i < lNumScreens; i++) {
			final var lScreen = mScreensToUpdate.get(i);
			if (lScreen.screenState() == ScreenState.Hidden && !lScreen.showBackgroundScreens())
				continue;

			lScreen.draw(core);
		}

		mContextHintManager.draw(core);
		mToastManager.draw(core);
		mToolTip.draw(core);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addScreen(Screen screenToAdd) {
		if (screenToAdd.singletonScreen()) {
			final int lScreenCount = mScreens.size();
			final var pNewScreenSImpleName = screenToAdd.getClass().getSimpleName();
			for (int i = 0; i < lScreenCount; i++) {
				final var lScreen = mScreens.get(i);
				if (lScreen.getClass().getSimpleName().equals(pNewScreenSImpleName)) {
					Debug.debugManager().logger().e(this.getClass().getSimpleName(), "Cannot add second SingletonScreen instance: " + pNewScreenSImpleName);
					return;
				}
			}
		}

		if (!screenToAdd.isResourcesLoaded()) {
			screenToAdd.isExiting(false);

			if (mIsinitialized && !screenToAdd.isinitialized())
				screenToAdd.initialize();

			if (mResourcesLoaded)
				screenToAdd.loadResources(mResourceManager);
		}

		screenToAdd.onScreenAdded();

		mScreensToAdd.add(screenToAdd);

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Added screen '%s'", screenToAdd.getClass().getSimpleName()));
	}

	public Screen getTopScreen() {
		if (mScreens == null || mScreens.size() == 0)
			return null;

		final int lScreenCount = mScreens.size();
		for (int i = lScreenCount - 1; i >= 0; i--) {
			if (mScreens.get(i).alwaysOnTop() == false)
				return mScreens.get(i);
		}

		return mScreens.get(mScreens.size() - 1);
	}

	public void removeScreen(Screen screenToRemove) {
		if (mIsinitialized) {
			screenToRemove.unloadResources();
		}

		screenToRemove.transitionOff();
		screenToRemove.onScreenRemoved();

		if (mScreens.contains(screenToRemove)) {
			// if this screen was the top screen, then the screen below gains focus
			if (mScreens.size() > 1 && mScreens.get(mScreens.size() - 1) == screenToRemove) {
				mScreens.get(mScreens.size() - 2).onGainedFocus();
			}

			mScreens.remove(screenToRemove);
		}

		if (mScreensToUpdate.contains(screenToRemove))
			mScreensToUpdate.remove(screenToRemove);

		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Removed screen '%s'", screenToRemove.getClass().getSimpleName()));
	}

	public void exitGame() {
		mLWJGLCore.closeApp();
	}

	public void onViewportChanged(float width, float height) {
		final int lScreenCount = mScreens.size();
		for (int i = 0; i < lScreenCount; i++) {
			mScreens.get(i).onViewportChange(width, height);
		}
	}

	public void createLoadingScreen(Screen loadingScreen) {
		exitAllScreens();

		Debug.debugManager().logger().v(getClass().getSimpleName(), "=== Loading Screen ===");

		System.gc();

		if (loadingScreen.isinitialized() == false) {
			loadingScreen.initialize();
		}

		if (loadingScreen.isResourcesLoaded() == false) {
			loadingScreen.loadResources(mResourceManager);
		}

		addScreen(loadingScreen);
	}

	private void exitAllScreens() {
		final var lScreenList = new ArrayList<Screen>();
		lScreenList.addAll(screens());

		final var lScreenCount = lScreenList.size();
		for (int i = 0; i < lScreenCount; i++) {
			if (!lScreenList.get(i).isExiting())
				lScreenList.get(i).exitScreen();
		}

		lScreenList.clear();
	}
}