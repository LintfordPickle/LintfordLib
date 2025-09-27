package net.lintfordlib.core;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharModsCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetJoystickCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.lintfordlib.ConstantsApp;
import net.lintfordlib.GameInfo;
import net.lintfordlib.assets.ResourceGroupProvider;
import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.controllers.camera.CameraController;
import net.lintfordlib.controllers.camera.CameraHUDController;
import net.lintfordlib.controllers.core.CoreTimeController;
import net.lintfordlib.controllers.core.ResourceController;
import net.lintfordlib.controllers.hud.HudLayoutController;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.camera.HUD;
import net.lintfordlib.core.camera.ICamera;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.Debug.DebugLogLevel;
import net.lintfordlib.core.debug.DebugMemory;
import net.lintfordlib.core.debug.GLDebug;
import net.lintfordlib.core.graphics.fonts.BitmapFontManager;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.KeyEventActionManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.rendering.SharedResources;
import net.lintfordlib.core.time.TimeConstants;
import net.lintfordlib.data.DataManager;
import net.lintfordlib.options.DisplayManager;
import net.lintfordlib.options.MasterConfig;
import net.lintfordlib.screenmanager.ScreenManager;

/**
 * The LintfordCore tracks the core state of an LWJGL application including a {@link DisplayManager}, {@link ResourceManager}, {@link CoreTime}, {@link Camera}, {@link HUD}, {@link InputManager} and {@link RenderState}. It also defines the behaviour for creating an OpenGL window.
 */
public abstract class LintfordCore {

	public class GameTime extends CoreTime {

		// --------------------------------------
		// Variables
		// --------------------------------------

		protected float timeModifier;
		protected boolean isTimePaused;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public boolean isTimePaused() {
			return isTimePaused;
		}

		public float timeModifier() {
			return timeModifier;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public GameTime() {
			super();
			timeModifier = 1.f;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public void setPaused(boolean newPausedState) {
			isTimePaused = newPausedState;
		}

		public void setGameTimeModifier(float newModifier) {
			timeModifier = MathHelper.clamp(newModifier, 0.0f, 10.0f);
		}
	}

	public class CoreTime {

		// --------------------------------------
		// Variables
		// --------------------------------------

		protected long lastFrameTime;
		double totalTimeMilli;
		double elapsedTimeMilli;
		double accumulatedElapsedTimeMilli;
		double targetElapsedTimeMilli;
		double maxElapsedTimeMilli;
		boolean isRenderingRunningSlowly;

		// --------------------------------------
		// Properties
		// --------------------------------------

		/**
		 * This flags returns true we've been missing update calls due to the amount of time taken to perform each draw call.
		 */
		public boolean isRunningSlowly() {
			return isRenderingRunningSlowly;
		}

		/** @return The total time in seconds. */
		public double totalTimeSeconds() {
			return totalTimeMilli / 1000.0f;
		}

		/** @return The total time in milliseconds. */
		public double totalTimeMilli() {
			return totalTimeMilli;
		}

		/** @return The elapsed time since the last frame in seconds. */
		public double elapsedTimeSeconds() {
			return elapsedTimeMilli / 1000f;
		}

		/** @return The elapsed time since the last frame in milliseconds. */
		public double elapsedTimeMilli() {
			return elapsedTimeMilli;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public CoreTime() {
			getDelta();

			final var targetFps = 60.f;
			targetElapsedTimeMilli = (1.f / targetFps) * 1000.f;
			maxElapsedTimeMilli = 64;
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		private double getDelta() {
			final var lSystemTime = System.nanoTime();
			final var lDelta = ((lSystemTime - lastFrameTime) / TimeConstants.NanoToMilli);
			lastFrameTime = lSystemTime;

			return lDelta;
		}

		public void resetElapsedTime() {
			lastFrameTime = 0;
			totalTimeMilli = 0.0f;
			elapsedTimeMilli = 0.0f;
			targetElapsedTimeMilli = 0.0f;
			maxElapsedTimeMilli = 0.0f;
		}
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int CORE_ENTITY_GROUP_ID = ResourceGroupProvider.getRollingEntityNumber();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected GameInfo mGameInfo;
	protected MasterConfig mMasterConfig;
	protected InputManager mInputState;
	protected final CoreTime mCoreTime = new CoreTime();
	protected final GameTime mGameTime = new GameTime();
	protected CoreTimeController mCoreTimeController;
	protected ResourceController mResourceController;
	protected CameraController mCameraController;
	protected CameraHUDController mCameraHUDController;
	protected ICamera mGameCamera;
	protected HUD mHUD;

	protected DataManager mDataManager;
	protected ControllerManager mControllerManager;
	protected ResourceManager mResourceManager;
	protected SharedResources mSharedResources;

	protected final float mShowLogoTimeInMilli = 3000;
	protected long mShowLogoTimer;
	protected boolean mIsHeadlessMode;
	protected boolean mIsFixedTimeStep;

	private final DebugState debugState = new DebugState();

	private class DebugState {

		private float mDebugKeyPressTimer;

		private boolean mDebugPaused;
		private boolean mDebugStepNextFrame;

		public boolean isPaused() {
			return mDebugPaused;
		}

		private void handleInput() {
			if (!Debug.debugManager().debugModeEnabled())
				return;

			// TODO: make this more usable

			if (mInputState.keyboard().isKeyDown(GLFW.GLFW_KEY_F11)) {
				if (mDebugPaused && isKeyCooldownElapsed()) {

					mDebugStepNextFrame = true;
					mDebugPaused = false;

					System.out.printf("Debug step");

					resetKeyCooldown();
				}
			}

			if (mInputState.keyboard().isKeyDown(GLFW.GLFW_KEY_F12)) {
				if (isKeyCooldownElapsed()) {

					mDebugPaused = !mDebugPaused;
					System.out.printf("Debug paused: %s\n", mDebugPaused);

					resetKeyCooldown();
				}
			}
		}

		public void update(float dt) {
			if (mDebugKeyPressTimer > 0)
				mDebugKeyPressTimer -= dt;

			if (mDebugStepNextFrame) {
				mDebugPaused = true;
				mDebugStepNextFrame = false;
			}

			handleInput();

		}

		private boolean isKeyCooldownElapsed() {
			return mDebugKeyPressTimer <= 0.f;
		}

		private void resetKeyCooldown() {
			mDebugKeyPressTimer = 200.f;
		}
	}

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isFixedTimeStep() {
		return mIsFixedTimeStep;
	}

	/**
	 * Returns the instance of {@link CoreTime} which tracks the time realted to the application.
	 */
	public CoreTime appTime() {
		return mCoreTime;
	}

	/**
	 * Returns the instance of {@link CoreTime} which tracks the time realted to the game. GameTime can be slowed or sped up, which will result n changes in the game simulation.
	 */
	public GameTime gameTime() {
		return mGameTime;
	}

	/**
	 * Returns the instance of {@link InputManager} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL window has not yet been created.
	 */
	public InputManager input() {
		return mInputState;
	}

	/** Returns the {@link ResourceManager}. */
	public ResourceManager resources() {
		return mResourceManager;
	}

	public SharedResources sharedResources() {
		return mSharedResources;
	}

	public MasterConfig config() {
		return mMasterConfig;
	}

	public DataManager dataManager() {
		return mDataManager;
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
		if (mGameCamera == null)
			return ICamera.EMPTY;

		return mGameCamera;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	protected LintfordCore(GameInfo gameInfo) {
		this(gameInfo, null);
	}

	protected LintfordCore(GameInfo gameInfo, String[] args) {
		this(gameInfo, args, false);
	}

	protected LintfordCore(GameInfo gameInfo, String[] args, boolean isHeadlessMode) {
		mGameInfo = gameInfo;
		mIsHeadlessMode = isHeadlessMode;

		var lNewLogLevel = gameInfo.debugLogLevel();

		if (args != null) {
			final int lArgsCount = args.length;
			for (int i = 0; i < lArgsCount; i++) {
				if (args[i].contains("debug")) {
					try {
						String lRightSide = args[i].substring(args[i].lastIndexOf("=") + 1);
						lNewLogLevel = DebugLogLevel.valueOf(lRightSide);
					} catch (IllegalArgumentException e) {
						System.err.println("Unable to process the command line argument: " + args[i]);
					}
				}
			}
		}

		Debug.debugManager(lNewLogLevel);

		mMasterConfig = new MasterConfig(mGameInfo);

		// Set a default workspace property to user.file
		var defaultWorkspaceLocation = System.getProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME);
		if (defaultWorkspaceLocation == null) {
			defaultWorkspaceLocation = System.getProperty("user.dir");
			Debug.debugManager().logger().i(getClass().getSimpleName(), "Setting 'user.dir' to: " + defaultWorkspaceLocation);
			System.setProperty(ConstantsApp.WORKSPACE_PROPERTY_NAME, defaultWorkspaceLocation);
		}

		mSharedResources = new SharedResources(ResourceGroupProvider.getRollingEntityNumber());

		// The target simulation speed can be set in the CoreTime.targetElapsedTimeMilli. Examples:
		// 30Hz is 33.33 ms
		// 60Hz is 16.66 ms
		// 90Hz is 11.111 ms
		// 120Hz is 8.33 ms

		registerGameInfoConstants(gameInfo);

		printSystemInformationToConsole();

		mShowLogoTimer = System.currentTimeMillis();
	}

	private void printSystemInformationToConsole() {
		DebugMemory.dumpMemoryToLog();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Working directory: " + System.getProperty("user.dir"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.vendor"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.vendor.url"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.version"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.home"));

		Debug.debugManager().logger().i(getClass().getSimpleName(), "LWJGL Version: " + org.lwjgl.Version.getVersion());
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Creates a new OpenGL window, instantiates all auxiliary classes and starts the main game loop.
	 */
	public void createWindow() {
		mInputState = new InputManager();

		var lWindowID = initializeGLFWWindow();

		createCoreManagers();
		createCoreControllers();

		oninitializeGL();

		showStartUpLogo(lWindowID);

		onInitializeApp();

		onLoadResources();

		finializeAppSetup();

		long lDiff = (long) (mShowLogoTimeInMilli - (System.currentTimeMillis() - mShowLogoTimer));
		if (lDiff > 0) {
			try {
				Thread.sleep(lDiff);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		onRunGameLoop();
	}

	private void createCoreManagers() {
		mDataManager = new DataManager(this);
		mControllerManager = new ControllerManager(this);

		mResourceManager = new ResourceManager(mMasterConfig);
		mResourceManager.addProtectedEntityGroupUid(CORE_ENTITY_GROUP_ID);

		mHUD = new HUD(mMasterConfig.display());
		mHUD.update(this);
	}

	private void createCoreControllers() {
		mResourceController = new ResourceController(mControllerManager, mResourceManager, CORE_ENTITY_GROUP_ID);
		mCoreTimeController = new CoreTimeController(mControllerManager, mCoreTime, mGameTime, CORE_ENTITY_GROUP_ID);
		mCameraHUDController = new CameraHUDController(mControllerManager, mHUD, CORE_ENTITY_GROUP_ID);
	}

	protected void showStartUpLogo(long windowHandle) {
		// by default just clear the window background to black and swap out the
		// back-buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		glfwSwapBuffers(windowHandle);
	}

	/**
	 * Implemented in sub-classes. Sets the default OpenGL state for the game.
	 */
	protected void oninitializeGL() {
		glClearColor(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);

		// Enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// The default context state is DEPTH_TEST disabled
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);

		// 2D Game - no face culling required (no 3d meshes)
		GL11.glFrontFace(GL11.GL_CCW);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glDisable(GL13.GL_MULTISAMPLE);
	}

	/**
	 * Implemented in the sub-class. Sets the default state of the application (note. OpenGL context is not available at this point).
	 */
	protected void onInitializeApp() {
		new HudLayoutController(mMasterConfig.display(), mControllerManager, CORE_ENTITY_GROUP_ID);

		mInputState.initialize(this);

		onInitializeInputActions(mInputState.eventActionManager());

		onInitializeBitmapFontSources(mResourceManager.fontManager());
	}

	private void onLoadBitmapFonts() {
		final var lFontManager = mResourceManager.fontManager();

		lFontManager.loadBitmapFontDefinitionsFromMetaData(BitmapFontManager.CoreFonts);

		lFontManager.loadBitmapFontDefinitionsFromMetaData(ScreenManager.ScreenManagerFonts);
		lFontManager.loadBitmapFontDefinitionsFromMetaData(SharedResources.RendererManagerFonts);
	}

	/**
	 * Provides an opportunity before the bitmapfonts are loaded into memory, to change the default locations that the core/library loads fonts from for rendering in the 'standard' Ui components.
	 */
	protected void onInitializeBitmapFontSources(BitmapFontManager fontManager) {

	}

	/**
	 * Allows the registration of game input actions and the respective key bindings.
	 */
	protected void onInitializeInputActions(KeyEventActionManager eventActionManager) {
		eventActionManager.loadConfig();
	}

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadResources() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading GL content");

		onLoadBitmapFonts();

		mResourceManager.loadResources();
		mSharedResources.loadResources(mResourceManager);

		Debug.debugManager().loadResources(mResourceManager);

		GLDebug.checkGLErrorsException("LintfordCore onLoadResources");
	}

	/**
	 * Called after the app initialization and resource have been loaded. This is a good time to instantiate any screens or game components that relie on core resources.
	 */
	protected void finializeAppSetup() {

	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadResources() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading GL content");

		mSharedResources.unloadResources();

		Debug.debugManager().unloadResources();

		mResourceManager.unloadContent();
	}

	/** The main game loop. */
	protected void onRunGameLoop() {
		int lRenderingIsSlow = 0;

		final var lDisplayConfig = mMasterConfig.display();

		while (!glfwWindowShouldClose(lDisplayConfig.windowID())) {
			lRenderingIsSlow = 0;
			mCoreTime.accumulatedElapsedTimeMilli += mCoreTime.getDelta();

			if (mIsFixedTimeStep && mCoreTime.accumulatedElapsedTimeMilli < mCoreTime.targetElapsedTimeMilli) {
				long lSleepTime = (long) (mCoreTime.targetElapsedTimeMilli - mCoreTime.accumulatedElapsedTimeMilli);
				if (lSleepTime == 0)
					continue;

				try {
					Thread.sleep(lSleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			if (mCoreTime.accumulatedElapsedTimeMilli > mCoreTime.maxElapsedTimeMilli)
				mCoreTime.accumulatedElapsedTimeMilli = mCoreTime.maxElapsedTimeMilli;

			if (mIsFixedTimeStep) {
				mCoreTime.elapsedTimeMilli = mCoreTime.targetElapsedTimeMilli;

				// Each step should perform at the target elapsed framerate
				mGameTime.elapsedTimeMilli = mCoreTime.targetElapsedTimeMilli;
				int lStepCount = 0;

				debugState.update((float) mGameTime.elapsedTimeMilli);

				if (!debugState.isPaused()) {
					while (mCoreTime.accumulatedElapsedTimeMilli >= mCoreTime.targetElapsedTimeMilli) {
						if (!mGameTime.isTimePaused)
							mGameTime.totalTimeMilli += mCoreTime.targetElapsedTimeMilli * mGameTime.timeModifier;

						mCoreTime.totalTimeMilli += mCoreTime.targetElapsedTimeMilli;
						mCoreTime.accumulatedElapsedTimeMilli -= mCoreTime.targetElapsedTimeMilli;

						onHandleInput();

						onUpdate();

						lStepCount++;
					}

				}

				// Every update after the first accumulates lag
				lRenderingIsSlow += Math.max(0, lStepCount - 1);

				if (mCoreTime.isRunningSlowly()) {
					if (lRenderingIsSlow == 0)
						mCoreTime.isRenderingRunningSlowly = false;
				} else if (lRenderingIsSlow >= 5) {
					mCoreTime.isRenderingRunningSlowly = true;
				}

				// Inform the (next) draw about how much time was actually simulated this iteration
				if (!mGameTime.isTimePaused) {
					mGameTime.elapsedTimeMilli = (mCoreTime.targetElapsedTimeMilli * lStepCount) * mGameTime.timeModifier;
				} else
					mGameTime.elapsedTimeMilli = .0f;

			} else {
				// Variable time - single step (consumes all accumulated time)
				if (!mGameTime.isTimePaused)
					mGameTime.elapsedTimeMilli = mCoreTime.accumulatedElapsedTimeMilli * mGameTime.timeModifier;
				else
					mGameTime.elapsedTimeMilli = 0.f;

				if (!mGameTime.isTimePaused)
					mGameTime.totalTimeMilli += mCoreTime.accumulatedElapsedTimeMilli * mGameTime.timeModifier;

				mCoreTime.elapsedTimeMilli = mCoreTime.accumulatedElapsedTimeMilli;
				mCoreTime.totalTimeMilli += mCoreTime.accumulatedElapsedTimeMilli;

				mCoreTime.accumulatedElapsedTimeMilli = 0.0;

				debugState.update((float) mGameTime.elapsedTimeMilli);

				if (!debugState.isPaused()) {
					onHandleInput();
					onUpdate();
				}
			}

			if (!debugState.isPaused())
				onDraw();

			Debug.debugManager().draw(this);

			mInputState.endUpdate();

			glfwSwapBuffers(lDisplayConfig.windowID());

			glfwPollEvents();
		}

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Closing down");

		onUnloadResources();

		System.exit(0);
	}

	/**
	 * called automatically within the gameloop.
	 */
	protected void onHandleInput() {
		mInputState.update(this);
		Debug.debugManager().handleInput(this);
		mHUD.handleInput(this);
		mControllerManager.handleInput(this, CORE_ENTITY_GROUP_ID);

	}

	/**
	 * called automatically within the gameloop.
	 */
	protected void onUpdate() {
		Debug.debugManager().preUpdate(this);

		mMasterConfig.update(this);
		mResourceManager.update(this);
		mHUD.update(this);

		if (mGameCamera != null)
			mGameCamera.update(this);

		mControllerManager.update(this, CORE_ENTITY_GROUP_ID);

		Debug.debugManager().update(this);
	}

	/** Implemented in sub-class. Draws the game components. */
	protected void onDraw() {
		if (mIsHeadlessMode)
			return;

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/** When called, sends the glfwWindowShouldClose message to GLFW. */
	public void closeApp() {
		glfwSetWindowShouldClose(mMasterConfig.display().windowID(), true);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public long initializeGLFWWindow() {
		long lWindowID = mMasterConfig.onCreateWindow();

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.keyboard().mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.keyboard().mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mouse().mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mouse().mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mouse().mMouseScrollCallback);

		/**
		 * This function sets the joystick configuration callback, or removes the currently set callback. This is called when a joystick is connected to or disconnected from the system.
		 */
		glfwSetJoystickCallback(mInputState.gamepads());

		/**
		 * This function sets the user-defined pointer of the specified joystick. The current value is retained until the joystick is disconnected. The initial value is NULL.
		 */
		// glfwSetJoystickUserPointer(CORE_ENTITY_GROUP_ID, lWindowID);

		mInputState.resetFlags();

		return lWindowID;
	}

	public ICamera createNewGameCamera() {
		return setNewGameCamera(null);
	}

	public ICamera setNewGameCamera(ICamera camera) {
		mGameCamera = camera;

		if (mMasterConfig == null)
			throw new RuntimeException("mMasterConfig not initialized. You must call setNewGameCamera from LintfordCore.initialize (or later)!");

		if (camera == null)
			mGameCamera = new Camera(mMasterConfig.display());

		mCameraController = new CameraController(mControllerManager, mGameCamera, CORE_ENTITY_GROUP_ID);
		return mGameCamera;
	}

	public void setActiveGameCamera(ICamera newCamera) {
		mGameCamera = newCamera;
	}

	public void removeGameCamera() {
		if (mCameraController != null) {
			mControllerManager.removeController(mCameraController, CORE_ENTITY_GROUP_ID);
			mCameraController = null;
		}

		mGameCamera = new Camera(mMasterConfig.display());
	}

	protected void registerGameInfoConstants(GameInfo gameInfo) {
		ConstantsApp.registerValue(ConstantsApp.CONSTANT_APP_NAME_TAG, gameInfo.applicationName());
	}
}
