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

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.controllers.camera.CameraController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.controllers.core.ResourceController;
import net.lintford.library.controllers.hud.UIHUDStructureController;
import net.lintford.library.core.camera.Camera;
import net.lintford.library.core.camera.HUD;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.Debug.DebugLogLevel;
import net.lintford.library.core.debug.DebugMemory;
import net.lintford.library.core.entity.BaseEntity;
import net.lintford.library.core.input.InputState;
import net.lintford.library.core.rendering.RenderState;
import net.lintford.library.core.time.TimeSpan;
import net.lintford.library.options.DisplayManager;
import net.lintford.library.options.MasterConfig;

/**
 * The LintfordCore tracks the core state of an LWJGL application including a {@link DisplayManager}, {@link ResourceManager}, {@link GameTime}, {@link Camera}, {@link HUD}, {@link InputState} and {@link RenderState}. It also
 * defines the behaviour for creating an OpenGL window.
 */
public abstract class LintfordCore {

	public class GameTime {

		// --------------------------------------
		// Variables
		// --------------------------------------

		long mLastFrame;
		double mTotalGameTimeMilli;
		double mElapsedGameTimeMilli;
		double mAccumulatedElapsedTimeMilli;
		double targetElapsedTimeMilli = 16.666;
		double maxElapsedTimeMilli = 500;
		boolean mIsGameRunningSlowly;

		// --------------------------------------
		// Properties
		// --------------------------------------

		/**
		 * This flags returns true if the game has recently been missing update calls due to the amount of time taken to perform each call.
		 */
		public boolean isGameRunningSlowly() {
			return mIsGameRunningSlowly;
		}

		/** @return The total game time in seconds. */
		public double totalGameTimeSeconds() {
			return mTotalGameTimeMilli / 1000.0f;
		}

		/** @return The total game time in milliseconds. */
		public double totalGameTime() {
			return mTotalGameTimeMilli;
		}

		/** @return The elapsed game time since the last frame in seconds. */
		public double elapseGameTimeSeconds() {
			return mElapsedGameTimeMilli / 1000f;
		}

		/** @return The elapsed game time since the last frame in milliseconds. */
		public double elapseGameTimeMilli() {
			return mElapsedGameTimeMilli;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public GameTime() {
			getDelta(); // needs to be called once at least

			maxElapsedTimeMilli = 500; // 500 ms

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		private double getDelta() {
			long time = System.nanoTime();
			double lDelta = ((time - mLastFrame) / TimeSpan.NanoToMilli);
			mLastFrame = time;

			return lDelta;

		}

		public void resetElapsedTime() {
			mLastFrame = 0;
			mTotalGameTimeMilli = 0.0f;
			mElapsedGameTimeMilli = 0.0f;
			targetElapsedTimeMilli = 0.0f;
			maxElapsedTimeMilli = 0.0f;

		}

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public static final int CORE_ENTITY_GROUP_ID = BaseEntity.getEntityNumber();

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

	protected final float mShowLogoTime = 3000; // 3 seconds
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
	 * Returns the instance of {@link GameTime} which was created when the LWJGL window was created. GameTime tracks the application time. null is returned if the LWJGL window has not yet been created.
	 */
	public GameTime time() {
		return mGameTime;
	}

	/**
	 * Returns the instance of {@link InputState} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL window has not
	 * yet been created.
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

		// FIXME: Move this into the DebugManager
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

		mGameTime = new GameTime();
		mInputState = new InputState();

		long lWindowID = initialiseGLFWWindow();

		mControllerManager = new ControllerManager(this);

		mResourceManager = new ResourceManager(mMasterConfig);
		mResourceController = new ResourceController(mControllerManager, mResourceManager, CORE_ENTITY_GROUP_ID);

		mHUD = new HUD(mMasterConfig.display());
		mHUD.update(this);
		mRenderState = new RenderState();

		showStartUpLogo(lWindowID);

		onInitialiseGL();

		onInitialiseApp();

		onLoadGLContent();

		// If we get to this point before enough time has elapsed, then continue showing the timer some ...
		long lDiff = (long) (mShowLogoTime - (System.currentTimeMillis() - mShowLogoTimer));
		if(lDiff > 0) {
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
		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		glfwSwapBuffers(pWindowHandle);

	}

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
		new UIHUDStructureController(mMasterConfig.display(), mControllerManager, CORE_ENTITY_GROUP_ID);

	}

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadGLContent() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Loading GL content");

		mResourceManager.loadGLContent();

		Debug.debugManager().loadGLContent(mResourceManager);

	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadGLContent() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Unloading GL content");

		Debug.debugManager().unloadGLContent();

		mResourceManager.unloadContent();

	}

	/** The main game loop. */
	protected void onRunGameLoop() {

		int lUpdateFrameLag = 0;

		DisplayManager lDisplayConfig = mMasterConfig.display();

		// Game loop
		while (!glfwWindowShouldClose(lDisplayConfig.windowID())) {

			mGameTime.mAccumulatedElapsedTimeMilli += mGameTime.getDelta();

			// Check if for the fixed time step not enough time has passed to do another update
			if (mIsFixedTimeStep && mGameTime.mAccumulatedElapsedTimeMilli < mGameTime.targetElapsedTimeMilli) {
				long lSleepTime = (long) (mGameTime.targetElapsedTimeMilli - mGameTime.mAccumulatedElapsedTimeMilli);

				// Sleep
				try {
					// System.out.println("Sleeping: " + lSleepTime);
					Thread.sleep(lSleepTime);

				} catch (InterruptedException e) {
					e.printStackTrace();

				}

				continue;

			}

			onHandleInput();

			// Do not allow any update to take longer than our maximum allowed.
			if (mGameTime.mAccumulatedElapsedTimeMilli > mGameTime.maxElapsedTimeMilli)
				mGameTime.mAccumulatedElapsedTimeMilli = mGameTime.maxElapsedTimeMilli;

			if (mIsFixedTimeStep) {
				mGameTime.mElapsedGameTimeMilli = mGameTime.targetElapsedTimeMilli;
				int lStepCount = 0;

				while (mGameTime.mAccumulatedElapsedTimeMilli >= mGameTime.targetElapsedTimeMilli) {
					mGameTime.mTotalGameTimeMilli += mGameTime.targetElapsedTimeMilli;
					mGameTime.mAccumulatedElapsedTimeMilli -= mGameTime.targetElapsedTimeMilli;
					++lStepCount;

					onUpdate();

				}

				// Every update after the first accumulates lag
				lUpdateFrameLag += Math.max(0, lStepCount - 1);

				if (mGameTime.isGameRunningSlowly()) {
					if (lUpdateFrameLag == 0) {
						mGameTime.mIsGameRunningSlowly = false;

					}

				} else if (lUpdateFrameLag >= 5) {
					// If we lag more than 5 frames, start thinking we are running slowly
					mGameTime.mIsGameRunningSlowly = true;

				}

				// Draw needs to know the total elapsed time that occured for the fixed length updates.
				mGameTime.mElapsedGameTimeMilli = mGameTime.targetElapsedTimeMilli * lStepCount;

			} else { // Variable time step
				// Perform a single variable length update.
				mGameTime.mElapsedGameTimeMilli = mGameTime.mAccumulatedElapsedTimeMilli;
				mGameTime.mTotalGameTimeMilli += mGameTime.mAccumulatedElapsedTimeMilli;
				mGameTime.mAccumulatedElapsedTimeMilli = 0.0; // use all the time

				onUpdate();

			}

			onDraw();

			Debug.debugManager().draw(this);

			mInputState.endUpdate();

			glfwSwapBuffers(lDisplayConfig.windowID());

			glfwPollEvents();

		}

		onUnloadGLContent();

		System.exit(0);

	}

	/**
	 */
	protected void onHandleInput() {
		if (mInputState.keyDownTimed(GLFW.GLFW_KEY_F12)) {
			mResourceManager.mTextureManager.dumpTextureInformation();

		}

		mInputState.update(this);
		Debug.debugManager().handleInput(this);
		mHUD.handleInput(this);
		mControllerManager.handleInput(this, CORE_ENTITY_GROUP_ID);

	}

	/**
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
		// Close the GLFW window
		glfwSetWindowShouldClose(mMasterConfig.display().windowID(), true);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public long initialiseGLFWWindow() {
		long lWindowID = mMasterConfig.onCreateWindow();

		// set key callbacks
		glfwSetKeyCallback(lWindowID, mInputState.mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mMouseScrollCallback);

		mInputState.resetFlags();

		return lWindowID;

	}

	public void setNewGameCamera(Camera pCamera) {
		mGameCamera = pCamera;

		if (mMasterConfig == null)
			throw new RuntimeException("mMasterConfig not initialised. You must call setNewGameCamera from LintfordCore.initialise (or later)!");

		if (pCamera == null)
			mGameCamera = new Camera(mMasterConfig.display());

		mCameraController = new CameraController(mControllerManager, mGameCamera, CORE_ENTITY_GROUP_ID);

	}

	public void removeGameCamera() {
		mGameCamera = null;

		if (mCameraController != null) {
			mControllerManager.removeController(mCameraController, CORE_ENTITY_GROUP_ID);
			mCameraController = null;

		}

	}

	protected void registerGameInfoConstants(GameInfo pGameInfo) {
		ConstantsTable.registerValue("APPLICATION_NAME", pGameInfo.applicationName());
		ConstantsTable.registerValue("WINDOW_TITLE", pGameInfo.windowTitle());

	}

}
