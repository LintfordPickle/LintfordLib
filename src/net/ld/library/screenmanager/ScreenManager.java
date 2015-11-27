package net.ld.library.screenmanager;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.ICamera;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.Screen.ScreenState;

public class ScreenManager {

	// =============================================
	// Variables
	// =============================================

	private DisplayConfig mDisplayConfig;
	private GameTime mGameTime;
	private InputState mInputState;
	private RenderState mRenderState;
	private ICamera mHUDCamera;
	private ICamera mWorldCamera;

	private ArrayList<Screen> mScreens;
	private ArrayList<Screen> mScreensToUpdate;

	private boolean mIsInitialised;
	private boolean mIsLoaded;

	protected ResourceManager mResources;

	// =============================================
	// Properties
	// =============================================

	public RenderState renderState() {
		return mRenderState;
	}

	public ResourceManager resources() {
		return mResources;
	}

	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	public ArrayList<Screen> screens() {
		return mScreens;
	}

	public InputState inputState() {
		return mInputState;
	}

	public GameTime gameTime() {
		return mGameTime;
	}

	public ICamera HUD() {
		return mHUDCamera;
	}

	public ICamera gameCamera() {
		return mWorldCamera;
	}

	public void setWorldCamera(ICamera pNewCamera){
		if(mRenderState != null){
			mRenderState.gameCamera(pNewCamera);
		}
	}
	
	// =============================================
	// Constructors
	// =============================================

	public ScreenManager(DisplayConfig pDisplayConfig) {
		mDisplayConfig = pDisplayConfig;

		mScreens = new ArrayList<Screen>();
		mScreensToUpdate = new ArrayList<Screen>();

		mResources = new ResourceManager(mDisplayConfig);
		mWorldCamera = new Camera(pDisplayConfig);
		mRenderState = new RenderState();

		mIsInitialised = false;
		mIsLoaded = false;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise(InputState pInputState, GameTime pGameTime, ICamera pHUDCamera) {
		mInputState = pInputState;
		mGameTime = pGameTime;
		mHUDCamera = pHUDCamera;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).initialise();
		}

		mRenderState.initialise(mHUDCamera, mWorldCamera, mGameTime, mDisplayConfig);

		mIsInitialised = true;
	}

	public void loadContent() {
		mResources.loadContent();

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).loadContent(mResources);
		}

		mIsLoaded = true;
	}

	public void unloadContent() {
		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreens.get(i).unloadContent();

		}
	}

	public void update(GameTime pGameTime) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		mScreensToUpdate.clear();

		mRenderState.update(pGameTime);

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreensToUpdate.add(mScreens.get(i));

		}

		boolean lOtherScreenHasFocus = false;
		boolean lCoveredByOtherScreen = false;

		while (mScreensToUpdate.size() > 0) {
			Screen lScreen = mScreensToUpdate.get(mScreensToUpdate.size() - 1);

			mScreensToUpdate.remove(mScreensToUpdate.size() - 1);

			lScreen.update(pGameTime, lOtherScreenHasFocus, lCoveredByOtherScreen);

			if (lScreen.screenState() == ScreenState.TransitionOn || lScreen.screenState() == ScreenState.Active) {

				// only handle input if this screen isn't covered by another
				// screen on the top of the stack
				// and if the debug console is not open (this takes priority of
				// keyboard input).
				if (!lOtherScreenHasFocus) {

					lScreen.handleInput(pGameTime, mInputState, true, false);

					lOtherScreenHasFocus = true;

				}

//				if (!lScreen.isPopup()) {
//
//					lCoveredByOtherScreen = true;
//
//				}

			}

		}

	}

	public void draw() {
		if (!mIsInitialised || !mIsLoaded)
			return;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			if (mScreens.get(i).screenState() == ScreenState.Hidden)
				continue;

			// FIXME: Start rendering from here using a RenderState object
			mScreens.get(i).draw(mRenderState);

		}
	}

	// =============================================
	// Methods
	// =============================================

	public void addScreen(Screen pScreen) {

		if (!pScreen.isLoaded()) {
			pScreen.screenManager(this);
			pScreen.isExiting(false);

			if (mIsInitialised) {// screen manager already initialized? then
									// load this screen manually
				// TODO: Add some kind of check for already initialise (as we do
				// this in multiple places)
				pScreen.initialise();
			}

			if (mIsLoaded) { // screen manager already loaded? then load this
								// screen manually
				if (!pScreen.mIsLoaded) {
					pScreen.loadContent(mResources);
				}
			}
		}

		mScreens.add(pScreen);

	}

	public void removeScreen(Screen pScreen) {

		if (mIsInitialised) {
			pScreen.unloadContent();
		}

		if (mScreens.contains(pScreen))
			mScreens.remove(pScreen);

		if (mScreensToUpdate.contains(pScreen))
			mScreensToUpdate.remove(pScreen);

	}

	public void fadeBackBufferToBlack(float pAlpha) {

		// TODO: Render a full screen black quad ...

	}

	public void exitGame() {
		// Quit the app
		GLFW.glfwSetWindowShouldClose(mDisplayConfig.windowID(), GL11.GL_TRUE);
	}

}
