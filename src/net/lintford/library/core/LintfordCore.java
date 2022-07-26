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

import net.lintford.library.ConstantsApp;
import net.lintford.library.GameInfo;
import net.lintford.library.controllers.camera.CameraController;
import net.lintford.library.controllers.camera.CameraHUDController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.CoreTimeController;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.controllers.debug.DebugControllerTreeController;
import net.lintford.library.controllers.debug.DebugRendererTreeController;
import net.lintford.library.controllers.hud.UiStructureController;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.Debug.DebugLogLevel;
import net.lintford.library.core.debug.DebugMemory;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.input.EventActionManager;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.MasterConfig;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.screenmanager.ScreenManager;

/**
 * The LintfordCore tracks the core state of an LWJGL application including a {@link DisplayManager}, {@link ResourceManager}, {@link CoreTime}, {@link Camera}, {@link HUD}, {@link InputManager} and {@link RenderState}.
 * It also defines the behaviour for creating an OpenGL window.
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

		public void setPaused(boolean pNewPausedState) {
			isTimePaused = pNewPausedState;

		}

		public void setGameTimeModifier(float pNewModifier) {
			timeModifier = MathHelper.clamp(pNewModifier, 0.0f, 10.0f);

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
		double targetElapsedTimeMilli = 16;
		double maxElapsedTimeMilli = 500;
		boolean isRunningSlowly;

		// --------------------------------------
		// Properties
		// --------------------------------------

		/**
		 * This flags returns true we've been missing update calls due to the amount of time taken to perform each draw call.
		 */
		public boolean isRunningSlowly() {
			return isRunningSlowly;
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
			getDelta(); // needs to be called once at least

			maxElapsedTimeMilli = 500; // 500 ms

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		private double getDelta() {
			long time = System.nanoTime();
			double lDelta = ((time - lastFrameTime) / TimeSpan.NanoToMilli);
			lastFrameTime = time;

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

	public static final int CORE_ENTITY_GROUP_ID = BaseEntity.getEntityNumber();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected GameInfo mGameInfo;
	protected MasterConfig mMasterConfig;

	protected InputManager mInputState;
	protected final CoreTime mCoreTime = new CoreTime();
	protected final GameTime mGameTime = new GameTime();

	protected ControllerManager mControllerManager;
	protected ResourceManager mResourceManager;
	protected CoreTimeController mCoreTimeController;
	protected ResourceController mResourceController;
	protected CameraController mCameraController;
	protected CameraHUDController mCameraHUDController;

	protected ICamera mGameCamera;
	protected HUD mHUD;
	protected RenderState mRenderState;

	protected final float mShowLogoTimeInMilli = 3000;
	protected long mShowLogoTimer;

	protected boolean mIsHeadlessMode;
	protected boolean mIsFixedTimeStep;

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
	 * Returns the instance of {@link InputManager} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL
	 * window has not yet been created.
	 */
	public InputManager input() {
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
		this(pGameInfo, null);

	}

	public LintfordCore(GameInfo pGameInfo, String[] pArgs) {
		this(pGameInfo, pArgs, false);

	}

	public LintfordCore(GameInfo pGameInfo, String[] pArgs, boolean pHeadless) {
		mGameInfo = pGameInfo;
		mIsHeadlessMode = pHeadless;

		// initially take the DebugLogLevel defined at compile time
		DebugLogLevel lNewLogLevel = pGameInfo.debugLogLevel();

		if (pArgs != null) {
			final int lArgsCount = pArgs.length;
			for (int i = 0; i < lArgsCount; i++) {
				if (pArgs[i].contains("debug")) {
					try {
						String lRightSide = pArgs[i].substring(pArgs[i].lastIndexOf("=") + 1);
						lNewLogLevel = DebugLogLevel.valueOf(lRightSide);
					} catch (IllegalArgumentException e) {
						System.err.println("Unable to process the command line argument: " + pArgs[i]);
					}
				}
			}
		}

		Debug.debugManager(lNewLogLevel);

		registerGameInfoConstants(pGameInfo);

		DebugMemory.dumpMemoryToLog();

		// Print out the working directory
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Working directory: " + System.getProperty("user.dir"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.vendor"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.vendor.url"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.version"));
		Debug.debugManager().logger().i(getClass().getSimpleName(), System.getProperty("java.home"));

		Debug.debugManager().logger().i(getClass().getSimpleName(), "LWJGL Version: " + org.lwjgl.Version.getVersion());
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Steamworks Version" + com.codedisaster.steamworks.Version.getVersion());

		mShowLogoTimer = System.currentTimeMillis();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Creates a new OpenGL window, instantiates all auxiliary classes and starts the main game loop.
	 */
	public void createWindow() {
		// Load the configuration files saved previously by the user (or else create new ones)
		mMasterConfig = new MasterConfig(mGameInfo);

		mInputState = new InputManager();

		long lWindowID = initializeGLFWWindow();

		mControllerManager = new ControllerManager(this);
		if (Debug.debugManager().debugManagerEnabled()) {
			mControllerManager.addController(new DebugControllerTreeController(mControllerManager, CORE_ENTITY_GROUP_ID), CORE_ENTITY_GROUP_ID);
			mControllerManager.addController(new DebugRendererTreeController(mControllerManager, CORE_ENTITY_GROUP_ID), CORE_ENTITY_GROUP_ID);
		}

		mResourceManager = new ResourceManager(mMasterConfig);
		mResourceManager.addProtectedEntityGroupUid(CORE_ENTITY_GROUP_ID);
		
		mResourceController = new ResourceController(mControllerManager, mResourceManager, CORE_ENTITY_GROUP_ID);
		mCoreTimeController = new CoreTimeController(mControllerManager, mCoreTime, mGameTime, CORE_ENTITY_GROUP_ID);

		// Create the HUD camera (always available)
		mHUD = new HUD(mMasterConfig.display());
		mHUD.update(this);
		mCameraHUDController = new CameraHUDController(mControllerManager, mHUD, CORE_ENTITY_GROUP_ID);

		mRenderState = new RenderState();

		oninitializeGL();

		showStartUpLogo(lWindowID);

		onInitializeApp();

		onLoadResources();

		long lDiff = (long) (mShowLogoTimeInMilli - (System.currentTimeMillis() - mShowLogoTimer));
		if (lDiff > 0) {
			try {
				Thread.sleep(lDiff);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		onRunGameLoop();
	};

	protected void showStartUpLogo(long pWindowHandle) {
		// by default just clear the window background to black and swap out the back-buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		glfwSwapBuffers(pWindowHandle);
	}

	/**
	 * Implemented in sub-classes. Sets the default OpenGL state for the game.
	 */
	protected void oninitializeGL() {
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
	protected void onInitializeApp() {
		new UiStructureController(mMasterConfig.display(), mControllerManager, CORE_ENTITY_GROUP_ID);

		onInitializeInputActions(mInputState.eventActionManager());

		onInitializeBitmapFontSources(mResourceManager.fontManager());
	}

	private void onLoadBitmapFonts() {
		final var lFontManager = mResourceManager.fontManager();
		lFontManager.loadBitmapFontDefinitionsFromMetaData(BitmapFontManager.CoreFonts);
		lFontManager.loadBitmapFontDefinitionsFromMetaData(ScreenManager.ScreenManagerFonts);
		lFontManager.loadBitmapFontDefinitionsFromMetaData(RendererManager.RendererManagerFonts);
	}

	/**
	 * Provides an opportunity before the bitmapfonts are loaded into memory, to change the default locations that the core/library loads
	 * fonts from for rendering in the 'standard' Ui components. 
	 * */
	protected void onInitializeBitmapFontSources(BitmapFontManager pFontManager) {

	}

	/** Allows the registration of game input actions and the respective key bindings. */
	protected void onInitializeInputActions(EventActionManager pEventActionManager) {
		pEventActionManager.loadConfig();
	}

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadResources() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading GL content");

		onLoadBitmapFonts();
		mResourceManager.loadResources();

		Debug.debugManager().loadResources(mResourceManager);
	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadResources() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading GL content");

		Debug.debugManager().unloadResources();

		mResourceManager.unloadContent();

	}

	/** The main game loop. */
	protected void onRunGameLoop() {

		int lUpdateFrameLag = 0;

		DisplayManager lDisplayConfig = mMasterConfig.display();

		// Game loop
		while (!glfwWindowShouldClose(lDisplayConfig.windowID())) {
			mCoreTime.accumulatedElapsedTimeMilli += mCoreTime.getDelta();

			// If we are using a fixed time step, then make sure enough time has elapsed since the last frame
			// before performing another update & draw
			if (mIsFixedTimeStep && mCoreTime.accumulatedElapsedTimeMilli < mCoreTime.targetElapsedTimeMilli) {
				long lSleepTime = (long) (mCoreTime.targetElapsedTimeMilli - mCoreTime.accumulatedElapsedTimeMilli);

				try {
					Thread.sleep(lSleepTime);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

				continue;

			}

			onHandleInput();

			// Do not allow any update to take longer than our maximum allowed per frame.
			if (mCoreTime.accumulatedElapsedTimeMilli > mCoreTime.maxElapsedTimeMilli)
				mCoreTime.accumulatedElapsedTimeMilli = mCoreTime.maxElapsedTimeMilli;

			if (mIsFixedTimeStep) {
				mCoreTime.elapsedTimeMilli = mCoreTime.targetElapsedTimeMilli;
				int lStepCount = 0;

				while (mCoreTime.accumulatedElapsedTimeMilli >= mCoreTime.targetElapsedTimeMilli) {
					if (!mGameTime.isTimePaused) {
						mGameTime.totalTimeMilli += mCoreTime.targetElapsedTimeMilli * mGameTime.timeModifier;

					}

					mCoreTime.totalTimeMilli += mCoreTime.targetElapsedTimeMilli;
					mCoreTime.accumulatedElapsedTimeMilli -= mCoreTime.targetElapsedTimeMilli;
					++lStepCount;

					onUpdate();

				}

				// Every update after the first accumulates lag
				lUpdateFrameLag += Math.max(0, lStepCount - 1);

				if (mCoreTime.isRunningSlowly()) {
					if (lUpdateFrameLag == 0) {
						mCoreTime.isRunningSlowly = false;
					}

				} else if (lUpdateFrameLag >= 5) {
					// If we lag more than 5 frames, start thinking we are running slowly
					mCoreTime.isRunningSlowly = true;
				}

				// Draw needs to know the total elapsed time that occured for the fixed length updates.
				mCoreTime.elapsedTimeMilli = mCoreTime.targetElapsedTimeMilli * lStepCount;

				if (!mGameTime.isTimePaused) {
					mGameTime.elapsedTimeMilli = (mCoreTime.targetElapsedTimeMilli * lStepCount) * mGameTime.timeModifier;
				} else
					mGameTime.elapsedTimeMilli = .0f;

			} else { // Variable time step
				// Perform a single variable length update.
				if (!mGameTime.isTimePaused) {
					mGameTime.elapsedTimeMilli = mCoreTime.accumulatedElapsedTimeMilli * mGameTime.timeModifier;

				} else
					mGameTime.elapsedTimeMilli = 0.f;

				if (!mGameTime.isTimePaused) {
					mGameTime.totalTimeMilli += mCoreTime.targetElapsedTimeMilli * mGameTime.timeModifier;

				}

				mCoreTime.elapsedTimeMilli = mCoreTime.accumulatedElapsedTimeMilli;
				mCoreTime.totalTimeMilli += mCoreTime.accumulatedElapsedTimeMilli;

				mCoreTime.accumulatedElapsedTimeMilli = 0.0; // consume all the time in a variable length step

				onUpdate();

			}

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

	/** called automatically within the gameloop.
	 */
	protected void onHandleInput() {
		mInputState.update(this);
		Debug.debugManager().handleInput(this);
		mHUD.handleInput(this);
		mControllerManager.handleInput(this, CORE_ENTITY_GROUP_ID);

	}

	/** called automatically within the gameloop.
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

		glClearColor(100.0f / 255.0f, 149.0f / 255.0f, 237.0f / 255.0f, 1.0f);
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

	public long initializeGLFWWindow() {
		long lWindowID = mMasterConfig.onCreateWindow();

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.keyboard().mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.keyboard().mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mouse().mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mouse().mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mouse().mMouseScrollCallback);

		mInputState.resetFlags();

		return lWindowID;

	}

	public ICamera setNewGameCamera(ICamera pCamera) {
		mGameCamera = pCamera;

		if (mMasterConfig == null)
			throw new RuntimeException("mMasterConfig not initialized. You must call setNewGameCamera from LintfordCore.initialize (or later)!");

		if (pCamera == null)
			mGameCamera = new Camera(mMasterConfig.display());

		mCameraController = new CameraController(mControllerManager, mGameCamera, CORE_ENTITY_GROUP_ID);
		return mGameCamera;
	}

	public void removeGameCamera() {
		mGameCamera = null;

		if (mCameraController != null) {
			mControllerManager.removeController(mCameraController, CORE_ENTITY_GROUP_ID);
			mCameraController = null;

		}

	}

	protected void registerGameInfoConstants(GameInfo pGameInfo) {
		ConstantsApp.registerValue("APPLICATION_NAME", pGameInfo.applicationName());
		ConstantsApp.registerValue("WINDOW_TITLE", pGameInfo.windowTitle());

	}

}
