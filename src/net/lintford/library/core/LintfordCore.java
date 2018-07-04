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
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import net.lintford.library.GameInfo;
import net.lintford.library.controllers.camera.CameraController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.debug.DebugManager.DebugLogLevel;
import net.lintford.library.core.debug.DebugMemory;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.options.MasterConfig;

/**
 * The LintfordCore tracks the core state of an LWJGL application including a {@link DisplayConfig}, {@link ResourceManager}, {@link GameTime}, {@link Camera}, {@link HUD}, {@link InputState} and {@link RenderState}. It also defines the behaviour for
 * creating an OpenGL window.
 */
public abstract class LintfordCore {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public final int CORE_ID = BaseEntity.getEntityNumber();

	protected GameInfo mGameInfo;
	protected MasterConfig mMasterConfig;

	protected InputState mInputState;
	protected GameTime mGameTime;

	protected ControllerManager mControllerManager;
	protected ResourceManager mResourceManager;
	protected ResourceController mResourceController;
	protected CameraController mCameraController;

	protected Camera mGameCamera;
	protected HUD mHUD;
	protected RenderState mRenderState;

	protected boolean mIsHeadlessMode;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	/**
	 * Returns the instance of {@link GameTime} which was created when the LWJGL window was created. GameTime tracks the application time. null is returned if the LWJGL window has not yet been created.
	 */
	public GameTime time() {
		return mGameTime;
	}

	/**
	 * Returns the instance of {@link InputState} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL window has not yet been created.
	 */
	public InputState input() {
		return mInputState;
	}

	/** Returns the {@link ResourceManager}. */
	public ResourceManager resources() {
		return mResourceManager;
	}

	public MasterConfig config() {
		return mMasterConfig;
	}

	public ControllerManager controllerManager() {
		return mControllerManager;
	}

	/**
	 * Returns the active HUD {@link ICamera} instance assigned to this {@link RenderState}.
	 */
	public ICamera HUD() {
		return mHUD;
	}

	/**
	 * Returns the active game {@link ICamera} instance assigned to this {@link RenderState}. This can return null if no game camera has been explicitly set!
	 */
	public ICamera gameCamera() {
		if (mGameCamera == null) {
			DebugManager.DEBUG_MANAGER.logger().w(getClass().getSimpleName(), "GameCamera not registered with LWJGLCore! Are you trying to access the game camera outside of a GameScreen?");
			return ICamera.EMPTY;

		}

		return mGameCamera;
	}

	public RenderState renderState() {
		return mRenderState;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LintfordCore(GameInfo pGameInfo) {
		this(pGameInfo, false);

	}

	public LintfordCore(GameInfo pGameInfo, boolean pHeadless) {
		mGameInfo = pGameInfo;
		mIsHeadlessMode = pHeadless;

		DebugManager.DEBUG_MANAGER.startDebug(DebugLogLevel.info);

		DebugMemory.dumpMemoryToLog();

		// Print out the working directory
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Working directory: " + System.getProperty("user.dir"));
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), System.getProperty("java.vendor"));
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), System.getProperty("java.vendor.url"));
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), System.getProperty("java.version"));

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Creates a new OpenGL window, instantiates all auxiliary classes and starts the main game loop.
	 */
	public void createWindow() {
		// Load the configuration files saved previously by the user (else create new
		// ones)
		mMasterConfig = new MasterConfig();
		mMasterConfig.loadConfigFiles(MasterConfig.configuration.all);

		mGameTime = new GameTime();
		mInputState = new InputState();

		initialiseGLFWWindow();

		mResourceManager = new ResourceManager(mMasterConfig);

		// Load the singleton subsystems
		TextureManager.textureManager();

		mControllerManager = new ControllerManager(this);

		mResourceController = new ResourceController(mControllerManager, mResourceManager, CORE_ID);

		mHUD = new HUD(mMasterConfig.display());
		mRenderState = new RenderState();

		onRunGameLoop();

	};

	/**
	 * Implemented in the sub-classe. Sets the default OpenGL state for the game.
	 */
	protected void onInitialiseGL() {
		// glClearColor(0f, 0f, 0f, 1.0f);
		glClearColor(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);

		// Enable depth testing
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// Enable depth testing
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		// 2D Game - no face culling required (no 3d meshes)
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDisable(GL11.GL_CULL_FACE);

	}

	/**
	 * Implemented in the sub-class. Sets the default state of the application (note. OpenGL context is not available at this point).
	 */
	protected void onInitialiseApp() {

	}

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadGLContent() {
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Loading GL content");

		mResourceManager.loadGLContent();

		DebugManager.DEBUG_MANAGER.loadGLContent(mResourceManager);

	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadGLContent() {
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Unloading GL content");

		DebugManager.DEBUG_MANAGER.unloadGLContent();

		mResourceManager.unloadContent();

	}

	/** The main game loop. */
	protected void onRunGameLoop() {

		onInitialiseGL();

		onInitialiseApp();

		// Lazy initialisation of texture manager
		TextureManager.textureManager();

		onLoadGLContent();

		DisplayConfig lDisplayConfig = mMasterConfig.display();

		// Game loop
		while (!glfwWindowShouldClose(lDisplayConfig.windowID())) {
			if (lDisplayConfig.isWindowFocused()) {

			}
			mGameTime.update();

			onHandleInput();

			onUpdate();

			if (!mIsHeadlessMode) {
				onDraw();

				DebugManager.DEBUG_MANAGER.draw(this);

			}

			glfwSwapBuffers(lDisplayConfig.windowID());

			mInputState.endUpdate();
			glfwPollEvents();

		}

		onUnloadGLContent();

		System.exit(0);

	}

	/**
	 * handle input for auxiliary classes. If extended in sub-classes, ensure to handleInput on auxiliary classes!
	 */
	protected void onHandleInput() {
		DebugManager.DEBUG_MANAGER.handleInput(this);
		mHUD.handleInput(this);

		// TODO: Problems with Z?
		mControllerManager.handleInput(this, CORE_ID);

	}

	/**
	 * update auxiliary classes. If extended in sub-classes, ensure to update the auxiliary classes!
	 */
	protected void onUpdate() {
		mMasterConfig.update(this);
		mInputState.update(this);
		mResourceManager.update(this);
		mHUD.update(this);
		if (mGameCamera != null)
			mGameCamera.update(this);
		mControllerManager.update(this, CORE_ID);

		DebugManager.DEBUG_MANAGER.update(this);

	}

	/** Implemented in sub-class. Draws the game components. */
	protected void onDraw() {
		if (mIsHeadlessMode)
			return;

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

	}

	/** When called, sends the glfwWindowShouldClose message to GLFW. */
	public void closeApp() {
		// Close the GLFW window
		glfwSetWindowShouldClose(mMasterConfig.display().windowID(), true);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void toggleFullscreen() {
		DisplayConfig lDisplay = mMasterConfig.display();
		if (lDisplay == null)
			return; // has the game been properly started yet?

		lDisplay.toggleFullscreen(800, 600);

	}

	public void initialiseGLFWWindow() {
		// TODO: Load default/saved window settings (fullscreen, dimensions, position etc.).

		long lWindowID = mMasterConfig.onCreateWindow(mGameInfo, false, mGameInfo.windowWidth(), mGameInfo.windowHeight(), mGameInfo.windowResizeable());

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mMouseScrollCallback);

		mInputState.resetFlags();

	}

	public void setNewGameCamera(Camera pCamera) {
		mGameCamera = pCamera;

		if (pCamera == null)
			mGameCamera = new Camera(mMasterConfig.display());

		mCameraController = new CameraController(mControllerManager, mGameCamera, CORE_ID);

	}

	public void removeGameCamera() {
		mGameCamera = null;

		if (mCameraController != null) {
			mControllerManager.removeController(mCameraController, CORE_ID);
			mCameraController = null;

		}

	}

}
