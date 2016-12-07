package net.ld.library.screenmanager;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.camera.ICamera;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;
import net.ld.library.screenmanager.Screen.ScreenState;

public class ScreenManager {

	// =============================================
	// Constants
	// =============================================

	/** The default texture name for the ScreenManager core textures. */
	public static final String SCREEN_MANAGER_TEXTURE_NAME = "ScreenManager_Texture";

	/** The default texture name for the ScreenManager core 9Patch textures. */
	public static final String SCREEN_MANAGER_PATCH_TEXTURE_NAME = "ScreenManager_9Patch_Texture";

	// =============================================
	// Variables
	// =============================================

	private DisplayConfig mDisplayConfig;
	private GameTime mGameTime;
	private InputState mInputState;
	private RenderState mRenderState;
	private HUD mHUDCamera;
	private Camera mWorldCamera;

	private ArrayList<Screen> mScreens;
	private ArrayList<Screen> mScreensToUpdate;
	private ResourceManager mResourceManager;

	private boolean mIsInitialised;
	private boolean mIsLoaded;

	// =============================================
	// Properties
	// =============================================

	public RenderState renderState() {
		return mRenderState;
	}

	public ResourceManager resourceManager() {
		return mResourceManager;
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

	public HUD HUD() {
		return mHUDCamera;
	}

	public Camera gameCamera() {
		return mWorldCamera;
	}

	public void setWorldCamera(ICamera pNewCamera) {
		if (mRenderState != null) {
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

		mRenderState = new RenderState();

		mIsInitialised = false;
		mIsLoaded = false;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise(InputState pInputState, GameTime pGameTime, HUD pHUDCamera, Camera pGameCamera) {
		mInputState = pInputState;
		mGameTime = pGameTime;
		mWorldCamera = pGameCamera;
		mHUDCamera = pHUDCamera;

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).initialise();
		}

		mRenderState.initialise(mHUDCamera, mWorldCamera, mGameTime, mDisplayConfig);

		mIsInitialised = true;
	}

	public void loadGLContent(ResourceManager pResourceManager) {
		// Store the reference to the resource manager as we'll be loading more screens later
		mResourceManager = pResourceManager;

		// Load the minimum font for the screen manager (contains textures for buttons and windows etc.)
		TextureManager.textureManager().loadTextureFromResource(SCREEN_MANAGER_TEXTURE_NAME, "/res/textures/screenmanager.png", GL11.GL_NEAREST);
		TextureManager.textureManager().loadTextureFromResource(SCREEN_MANAGER_PATCH_TEXTURE_NAME, "/res/textures/menu9patch.png", GL11.GL_NEAREST);

		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {
			mScreens.get(i).loadGLContent(pResourceManager);
		}

		mIsLoaded = true;
	}

	public void unloadGLContent() {
		int lCount = mScreens.size();
		for (int i = 0; i < lCount; i++) {

			mScreens.get(i).unloadGLContent();

		}
	}

	public void update(GameTime pGameTime) {
		if (!mIsInitialised || !mIsLoaded)
			return;

		mScreensToUpdate.clear();

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

				// if (!lScreen.isPopup()) {
				//
				// lCoveredByOtherScreen = true;
				//
				// }

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
					pScreen.loadGLContent(mResourceManager);
				}
			}
		}

		mScreens.add(pScreen);

	}

	public void removeScreen(Screen pScreen) {

		if (mIsInitialised) {
			pScreen.unloadGLContent();
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
