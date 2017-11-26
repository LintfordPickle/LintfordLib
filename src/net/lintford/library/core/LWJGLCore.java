package net.lintford.library.core;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.controllers.BaseControllerGroups;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.RendererController;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.debug.DebugManager.DebugLogLevel;
import net.lintford.library.core.debug.DebugMemory;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.options.MasterConfig;
import net.lintford.library.renderers.RendererManager;

/**
 * The LWJGLCore tracks the core state of an LWJGL application including a {@link DisplayConfig}, {@link ResourceManager}, {@link GameTime}, {@link Camera}, {@link HUD}, {@link InputState} and {@link RenderState}. It also defines the behaviour for
 * creating an OpenGL window.
 */
public abstract class LWJGLCore {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected GameInfo mGameInfo;
	protected MasterConfig mMasterConfig;

	protected InputState mInputState;
	protected GameTime mGameTime;

	protected ControllerManager mControllerManager;
	protected RendererManager mRendererManager;
	protected ResourceManager mResourceManager;

	protected HUD mHUD;
	protected RenderState mRenderState;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	/**
	 * Returns the instance of {@link GameTime} which was created when the LWJGL window was created. GameTime tracks the application time. null is returned if the LWJGL window has not yet been created.
	 */
	public GameTime gameTime() {
		return mGameTime;
	}

	/**
	 * Returns the instance of {@link InputState} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL window has not yet been created.
	 */
	public InputState inputState() {
		return mInputState;
	}

	public MasterConfig configuration() {
		return mMasterConfig;
	}

	public RendererManager rendererManager() {
		return mRendererManager;
	}

	public ControllerManager controllerManager() {
		return mControllerManager;
	}

	public HUD HUD() {
		return mHUD;
	}

	public RenderState renderState() {
		return mRenderState;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LWJGLCore(GameInfo pGameInfo) {
		mGameInfo = pGameInfo;

		ConstantsTable.setAppConstants(pGameInfo.applicationName());

		DebugManager.DEBUG_MANAGER.startDebug(DebugLogLevel.info);

		DebugMemory.dumpMemoryToLog();

		// Print out the working directory
		System.out.println("working directory: " + System.getProperty("user.dir"));

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Creates a new OpenGL window, instantiates all auxiliary classes and starts the main game loop.
	 */
	public void createWindow() {
		// Load the configuration files saved previously by the user (else create new ones)
		mMasterConfig = new MasterConfig();
		mMasterConfig.loadConfigFiles(MasterConfig.configuration.all);

		mGameTime = new GameTime();
		mInputState = new InputState(mGameTime);

		initialiseGLFWWindow();

		mResourceManager = new ResourceManager(mMasterConfig);

		// Load the singleton subsystems
		TextureManager.textureManager();

		mControllerManager = new ControllerManager();
		mRendererManager = new RendererManager();

		mHUD = new HUD(mMasterConfig.displayConfig());
		mRenderState = new RenderState(mMasterConfig.displayConfig(), mHUD, mGameTime);

		mControllerManager.addController(new RendererController(mControllerManager, mRendererManager, BaseControllerGroups.CONTROLLER_CORE_GROUP_ID));

		onRunGameLoop();

	}
;
	/**
	 * Implemented in the sub-classe. Sets the default OpenGL state for the game.
	 */
	public void onInitialiseGL() {

	}

	/**
	 * Implemented in the sub-class. Sets the default state of the application (note. OpenGL context is not available at this point).
	 */
	public void onInitialiseApp() {

	}

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadGLContent() {
		mResourceManager.loadGLContent();
		mRendererManager.loadGLContent(mResourceManager);

	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadGLContent() {
		mResourceManager.unloadContent();
		mRendererManager.unloadGLContent();

	}

	/** The main game loop. */
	protected void onRunGameLoop() {

		onInitialiseGL();

		onInitialiseApp();

		// Lazy initialization of texture manager
		TextureManager.textureManager();

		onLoadGLContent();

		DisplayConfig lDisplayConfig = mMasterConfig.displayConfig();

		// Game loop
		while (!glfwWindowShouldClose(lDisplayConfig.windowID())) {
			if (lDisplayConfig.isWindowFocused()) {

			}
			mGameTime.update();
			onHandleInput();
			onUpdate(mGameTime);

			onDraw(mRenderState);

			glfwSwapBuffers(lDisplayConfig.windowID());

			glfwPollEvents();

		}

		onUnloadGLContent();

		System.exit(0);
	}

	/**
	 * handle input for auxiliary classes. If extended in sub-classes, ensure to handleInput on auxiliary classes!
	 */
	protected void onHandleInput() {
		if (mInputState.keyDownTimed(GLFW.GLFW_KEY_F11)) {
			toggleFullscreen();

		}

		mHUD.handleInput(mInputState);

		mControllerManager.handleInput(mInputState);

	}

	/**
	 * update auxiliary classes. If extended in sub-classes, ensure to update the auxiliary classes!
	 */
	protected void onUpdate(GameTime pGameTime) {
		mMasterConfig.update(pGameTime);
		mInputState.update(pGameTime);
		mResourceManager.update(pGameTime);

		mHUD.update(pGameTime);

		mControllerManager.update(pGameTime);
	}

	/** Implemented in sub-class. Draws the game components. */
	protected void onDraw(RenderState pRenderState) {
		mRendererManager.draw(pRenderState);

	}

	/** When called, sends the glfwWindowShouldClose message to GLFW. */
	public void closeApp() {
		// Close the GLFW window
		glfwSetWindowShouldClose(mMasterConfig.displayConfig().windowID(), true);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void toggleFullscreen() {
		DisplayConfig lDisplay = mMasterConfig.displayConfig();
		if (lDisplay == null)
			return; // has the game been properly started yet?

		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Toggling fullscreen (to " + (lDisplay.fullscreen() ? "Fullscreen)" : "Windowed)"));

		onUnloadGLContent();

		lDisplay.toggleFullScreenFlag();
		mMasterConfig.onCreateWindow();

		initialiseGLFWWindow();

		// Recreate the OpenGL State
		onInitialiseGL();

		onLoadGLContent();

	}

	public void initialiseGLFWWindow() {
		long lWindowID = mMasterConfig.onCreateWindow();

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mMouseScrollCallback);

		mInputState.resetFlags();
		
	}

}
