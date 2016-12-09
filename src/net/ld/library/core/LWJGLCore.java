package net.ld.library.core;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import net.ld.library.GameInfo;
import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

public abstract class LWJGLCore {

	// =============================================
	// Variables
	// =============================================

	protected GameInfo mGameInfo;
	protected DisplayConfig mDisplayConfig;
	protected ResourceManager mResourceManager;
	protected GameTime mGameTime;
	protected Camera mGameCamera;
	protected HUD mHUDCamera;
	protected InputState mInputState;
	protected RenderState mRenderState;
	
	// =============================================
	// Properties
	// =============================================

	public GameTime gameTime() {
		return mGameTime;
	}

	public InputState inputState() {
		return mInputState;
	}

	public HUD hud() {
		return mHUDCamera;
	}

	public Camera gameCamera() {
		return mGameCamera;
	}

	// =============================================
	// Constructor(s)
	// =============================================

	public LWJGLCore(GameInfo pGameInfo) {
		// Load the configuration files saved previously by the user (else
		// create new ones)
		// FIXME: Load the configuration files (or just create new ones)
		mGameInfo = pGameInfo;
		mDisplayConfig = new DisplayConfig(mGameInfo);
		mResourceManager = new ResourceManager(mDisplayConfig);
		mRenderState = new RenderState();

		// Print out the working directory
		System.out.println("working directory: " + System.getProperty("user.dir"));
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void createWindow() {

		mGameCamera = new Camera(mDisplayConfig, 0, 0, DisplayConfig.WINDOW_WIDTH, DisplayConfig.WINDOW_HEIGHT);
		mHUDCamera = new HUD(-DisplayConfig.WINDOW_WIDTH / 2, -DisplayConfig.WINDOW_HEIGHT / 2, DisplayConfig.WINDOW_WIDTH / 2, DisplayConfig.WINDOW_HEIGHT / 2);
		
		mGameTime = new GameTime();
		mInputState = new InputState(mDisplayConfig, mGameCamera, mHUDCamera, mGameTime);
		
		mRenderState.initialise(mHUDCamera, mGameCamera, mGameTime, mDisplayConfig);

		long lWindowID = mDisplayConfig.onCreateWindow();

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mMouseScrollCallback);

		onInitialiseGL();

		// Lazy initialisation of texture manager
		TextureManager.textureManager();

		onInitialiseApp();

		onLoadGLContent();

		// Starts the game loop.
		onRunGameLoop();
	}

	public abstract void onInitialiseGL();

	public abstract void onInitialiseApp();

	protected void onLoadGLContent() {
		mResourceManager.loadGLContent();

	}
	
	protected void onUnloadGLContent() {
		mResourceManager.unloadGLContent();
		

	}

	protected void onRunGameLoop() {

		// Game loop
		while (!glfwWindowShouldClose(mDisplayConfig.windowID())) {

			// Only update time if window has focus
			if(mDisplayConfig.hasFocus()){
				mGameTime.update();
				
			}

			onHandleInput();

			onUpdate(mGameTime);

			onDraw();

			glfwSwapBuffers(mDisplayConfig.windowID());

			mDisplayConfig.resetFlags();
			mInputState.resetFlags();

			glfwPollEvents();
		}
		
		onUnloadGLContent();

		System.exit(0);
	}

	protected void onHandleInput(){
		mDisplayConfig.handleInput(mInputState);
		mHUDCamera.handleInput(mInputState);
		mGameCamera.handleInput(mInputState);
		
	}

	protected void onUpdate(GameTime pGameTime) {
		mDisplayConfig.update(pGameTime);
		mInputState.update(pGameTime);
		mHUDCamera.update(pGameTime);
		mGameCamera.update(pGameTime);
		mResourceManager.update(pGameTime);
	}

	protected abstract void onDraw();

	public void closeApp() {
		// Close the GLFW window
		glfwSetWindowShouldClose(mDisplayConfig.windowID(), true);
	}

	// =============================================
	// Methods
	// =============================================

}
