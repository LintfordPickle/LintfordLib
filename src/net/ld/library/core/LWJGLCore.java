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
import net.ld.library.core.audio.AudioManager;
import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.ResourceManager;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

/**
 * The LWJGLCore tracks the core state of an LWJGL application including a {@link DisplayConfig}, {@link ResourceManager}, {@link GameTime}, {@link Camera}, {@link HUD}, {@link InputState} and {@link RenderState}. It
 * also defines the behaviour for creating an OpenGL window.
 */
public abstract class LWJGLCore {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected GameInfo mGameInfo;
	protected DisplayConfig mDisplayConfig;
	protected ResourceManager mResourceManager;
	protected GameTime mGameTime;
	protected Camera mGameCamera;
	protected HUD mHUDCamera;
	protected InputState mInputState;
	protected RenderState mRenderState;
	protected AudioManager mAudioManager;

	/**
	 * Tracks if a window has been created or not. Because certain objects are tied to the creation of a window, we should only allow one window to be created per LWJGL instance.
	 */
	private boolean mWindowCreated;

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
	 * Returns the instance of {@link InputState} which was created when the LWJGL window was created. InputState is updated per-frame and tracks user input from the mouse and keyboard. null is returned if the LWJGL
	 * window has not yet been created.
	 */
	public InputState inputState() {
		return mInputState;
	}

	/**
	 * Returns the instance of {@link HUD} camera created when the LWJGL window was created. null is returned if the LWJGL window has not yet been created.
	 */
	public HUD hud() {
		return mHUDCamera;
	}

	/**
	 * Returns the instance of game {@link Camera} created when the LWJGL window was created. null is returned if the LWJGL window has not yet been created.
	 */
	public Camera gameCamera() {
		return mGameCamera;
	}

	/**
	 * Returns an instance of {@link RenderState} which contains state information about the current rendering pass.
	 */
	public RenderState renderState() {
		return mRenderState;
	}

	/** Returns an OpenAL {@link AudioManager} instance which can be used for playing back audio files. */
	public AudioManager audioManager() {
		return mAudioManager;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LWJGLCore(GameInfo pGameInfo) {
		// Load the configuration files saved previously by the user (else
		// create new ones)
		// FIXME: Load the configuration files (or just create new ones)
		mGameInfo = pGameInfo;
		mDisplayConfig = new DisplayConfig(mGameInfo);
		mResourceManager = new ResourceManager(mDisplayConfig);
		mRenderState = new RenderState();
		mAudioManager = new AudioManager();
		mAudioManager.initialise();
		
		// Print out the working directory
		System.out.println("working directory: " + System.getProperty("user.dir"));
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	/**
	 * Creates a new OpenGL window and instantiates all auxiliary classes needed for a LWJGL game.
	 */
	public boolean createWindow() {
		// Don't allow the user to create more than one window per LWJGL (this)
		// instance.
		if (mWindowCreated) {
			System.err.println("You can only created one window per LWJGL instance!");
			return false;

		}

		mGameCamera = new Camera(0, 0, DisplayConfig.WINDOW_WIDTH, DisplayConfig.WINDOW_HEIGHT);
		mHUDCamera = new HUD(-DisplayConfig.WINDOW_WIDTH / 2, -DisplayConfig.WINDOW_HEIGHT / 2, DisplayConfig.WINDOW_WIDTH / 2, DisplayConfig.WINDOW_HEIGHT / 2);

		mGameTime = new GameTime();
		mInputState = new InputState(mDisplayConfig, mGameCamera, mHUDCamera, mGameTime);

		mRenderState.initialise(mHUDCamera, mGameCamera, mGameTime, mDisplayConfig);

		long lWindowID = mDisplayConfig.onCreateWindow();
		mDisplayConfig.addResizeListener(mHUDCamera);
		mDisplayConfig.addResizeListener(mGameCamera);

		// set key callbacks
		// TODO: Add these to the InputState class
		glfwSetKeyCallback(lWindowID, mInputState.mKeyCallback);
		glfwSetCharModsCallback(lWindowID, mInputState.mTextCallback);
		glfwSetMouseButtonCallback(lWindowID, mInputState.mMouseButtonCallback);
		glfwSetCursorPosCallback(lWindowID, mInputState.mMousePositionCallback);
		glfwSetScrollCallback(lWindowID, mInputState.mMouseScrollCallback);

		onInitialiseGL();

		onInitialiseApp();

		// Lazy initialization of texture manager
		TextureManager.textureManager();

		onLoadGLContent();

		return true;
	}

	/**
	 * Implemented in the sub-classe. Sets the default OpenGL state for the game.
	 */
	public abstract void onInitialiseGL();

	/**
	 * Implemented in the sub-class. Sets the default state of the application (note. OpenGL context is not available at this point).
	 */
	public abstract void onInitialiseApp();

	/**
	 * Called automatically before entering the main game loop. OpenGL content can be setup.
	 */
	protected void onLoadGLContent() {
		mResourceManager.loadGLContent();

	}

	/**
	 * Called automatically after exiting the main game loop. OpenGL resources should be released.
	 */
	protected void onUnloadGLContent() {
		mResourceManager.unloadGLContent();

	}

	/** The main game loop. */
	protected void onRunGameLoop() {

		boolean lFocusRegained = false;
		
		// Game loop
		while (!glfwWindowShouldClose(mDisplayConfig.windowID())) {
			if (mDisplayConfig.hasFocus()) {
				if(!lFocusRegained){
					mGameTime.resetElapsed();
					lFocusRegained = true;
					
				}
				
				onHandleInput();
				
				mGameTime.update();
				
				onUpdate(mGameTime);

			}
			else{
				lFocusRegained = false;
				
			}

			onDraw(mRenderState);

			glfwSwapBuffers(mDisplayConfig.windowID());

			mDisplayConfig.resetFlags();
			mInputState.resetFlags();

			glfwPollEvents();
		}

		onUnloadGLContent();
		
		mAudioManager.cleanUp();

		System.exit(0);
	}

	/**
	 * handle input for auxiliary classes. If extended in sub-classes, ensure to handleInput on auxiliary classes!
	 */
	protected void onHandleInput() {
		mDisplayConfig.handleInput(mInputState);
		mHUDCamera.handleInput(mInputState);
		mGameCamera.handleInput(mInputState);

	}

	/**
	 * update auxiliary classes. If extended in sub-classes, ensure to update the auxiliary classes!
	 */
	protected void onUpdate(GameTime pGameTime) {
		mDisplayConfig.update(pGameTime);
		mInputState.update(pGameTime);
		mHUDCamera.update(pGameTime);
		mGameCamera.update(pGameTime);
		mResourceManager.update(pGameTime);
	}

	/** Implemented in sub-class. Draws the game components. */
	protected abstract void onDraw(final RenderState pRenderState);

	/** When called, sends the glfwWindowShouldClose message to GLFW. */
	public void closeApp() {
		// Close the GLFW window
		glfwSetWindowShouldClose(mDisplayConfig.windowID(), true);

	}

}
