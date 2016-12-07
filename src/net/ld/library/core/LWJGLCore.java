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
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import net.ld.library.GameInfo;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.graphics.textures.TextureManager;
import net.ld.library.core.input.InputState;
import net.ld.library.core.time.GameTime;

public abstract class LWJGLCore {

	// =============================================
	// Variables
	// =============================================

	protected GameInfo mGameInfo;
	protected DisplayConfig mDisplayConfig;
	protected GameTime mGameTime;
	protected HUD mHUDCamera;
	protected InputState mInputState;

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

	// =============================================
	// Constructor(s)
	// =============================================

	public LWJGLCore(GameInfo pGameInfo) {
		// Load the configuration files saved previously by the user (else
		// create new ones)
		// FIXME: Load the configuration files (or just create new ones)
		mGameInfo = pGameInfo;
		mDisplayConfig = new DisplayConfig(mGameInfo);

		// Print out the working directory
		System.out.println("working directory: " + System.getProperty("user.dir"));
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void createWindow() {

		mHUDCamera = new HUD(mDisplayConfig, 0, 0, mGameInfo.windowWidth(), mGameInfo.windowHeight());
		mGameTime = new GameTime();
		mInputState = new InputState(mDisplayConfig, mGameTime);

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

		onLoadContent();

		// Starts the game loop.
		onRunGameLoop();
	}

	public abstract void onInitialiseGL();

	public abstract void onInitialiseApp();

	protected abstract void onLoadContent();

	protected void onRunGameLoop() {

		// Game loop
		while (glfwWindowShouldClose(mDisplayConfig.windowID()) == GL_FALSE) {
			mGameTime.update();

			onHandleInput();

			onUpdate(mGameTime);

			onDraw();

			glfwSwapBuffers(mDisplayConfig.windowID());

			mDisplayConfig.resetFlags();
			mInputState.resetFlags();

			glfwPollEvents();
		}

		System.exit(0);
	}

	protected abstract void onHandleInput();

	protected void onUpdate(GameTime pGameTime) {
		mInputState.update(pGameTime);
		mHUDCamera.update(pGameTime);
	}

	protected abstract void onDraw();

	public void closeApp() {
		// Close the glfw window
		glfwSetWindowShouldClose(mDisplayConfig.windowID(), GL_TRUE);
	}

	// =============================================
	// Methods
	// =============================================

}
