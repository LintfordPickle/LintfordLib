package net.lintford.library.options;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeLimits;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.Debug.DebugLogLevel;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.maths.Vector2i;

// FIXME: Need to correct toggle between fullscreen, borderless window etc. 
// FIXME: Need to add a dirty flag to confirm options 
// TODO: Implement GameResizeListener for all GLContent reloading 
// TODO: Need to load the last display configuration from file
public class DisplayConfig extends BaseConfig {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	public enum AspectRatios {
		normal, wide, u_wide,
	}

	public static final int WINDOW_MINIMUM_WIDTH = 800;
	public static final int WINDOW_MINIMUM_HEIGHT = 600;

	// --------------------------------------
	// Enums
	// --------------------------------------

	public enum TARGET_FPS {
		fps50(50), fps60(60);

		public final int value;

		private TARGET_FPS(int pValue) {
			this.value = pValue;
		}
	}

	public enum CAMERA_SIZE {
		small, medium, large,
	}

	// --------------------------------------
	// Class Variables
	// --------------------------------------

	public boolean mFullScreen;
	private Vector2i mWindowSize;
	private Vector2i mAspectRatio;

	private boolean mStretchGameScreen = false;
	private int mBaseGameResolutionWidth = 640;
	private int mBaseGameResolutionHeight = 480;

	private boolean mVSYNCEnabled;
	private boolean mWindowIsResizable;
	private boolean mWindowWasResized;

	GLFWVidMode mDesktopVideoMode;

	private TARGET_FPS mTargetFPS;

	private GLFWFramebufferSizeCallback mFrameBufferSizeCallback;
	private long mWindowID = NULL;
	private boolean mRecompileShaders = false;

	List<IResizeListener> mWindowResizeListeners;
	List<IResizeListener> mGameResizeListeners;

	List<Vector2i> mGameResolutions; // Resolutions supported by the GAME
	boolean mWindowResolutionChanged;
	boolean mWaitForMouseRelease;

	/**
	 * We lock the local listeners while traversing the Listeners list after a resolution change is detected. This prevents the programmer's from changing the size of the list.
	 */
	private boolean mLockedListeners;

	private boolean mIsWindowFocused;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public GLFWVidMode desktopVideoMode() {
		return mDesktopVideoMode;
	}

	public boolean isWindowFocused() {
		return mIsWindowFocused;
	}

	public long windowID() {
		return mWindowID;
	}

	public TARGET_FPS targetFPS() {
		return mTargetFPS;
	}

	public boolean recompileShaders() {
		return mRecompileShaders;
	}

	public boolean fullscreen() {
		return mFullScreen;
	}

	public Vector2i windowSize() {
		return mWindowSize;
	}

	public boolean vsyncEnabled() {
		return mVSYNCEnabled;
	}

	public boolean windowResizable() {
		return mWindowIsResizable;
	}

	public boolean windowWasResized() {
		return mWindowWasResized;
	}

	public boolean stretchGameScreen() {
		return mStretchGameScreen;
	}

	public int baseGameResolutionWidth() {
		return mBaseGameResolutionWidth;
	}

	public int baseGameResolutionHeight() {
		return mBaseGameResolutionHeight;
	}

	public void resizeListeners(List<IResizeListener> pV) {
		mWindowResizeListeners = pV;
	}

	public List<IResizeListener> resizeListeners() {
		return mWindowResizeListeners;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DisplayConfig(String pConfigFilename) {
		super(pConfigFilename);

		/* set some defaults */
		mWindowSize = new Vector2i();
		mWindowSize.x = 1600;
		mWindowSize.y = 900;

		mAspectRatio = new Vector2i();
		mAspectRatio.x = 16;
		mAspectRatio.y = 9;

		mFullScreen = true;
		mWindowIsResizable = true;
		mVSYNCEnabled = true;
		mTargetFPS = TARGET_FPS.fps60;

		mWindowResizeListeners = new ArrayList<>();
		mGameResizeListeners = new ArrayList<>();

		mFullScreen = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		if (mWindowResolutionChanged /* && !pInputState.mouseLeftClick() */) {
			synchronized (this) {
				mLockedListeners = true;
				final int COUNT = mWindowResizeListeners.size();

				for (int i = 0; i < COUNT; i++) {
					if (mWindowResizeListeners.get(i) == null)
						continue;

					Debug.debugManager().logger().log(DebugLogLevel.info, "SYSTEM", "calling window resize listener: " + mWindowResizeListeners.get(i).getClass().getSimpleName());

					mWindowResizeListeners.get(i).onResize(mWindowSize.x, mWindowSize.y);

				}

				// changeResolution(mWindowSize.x, mWindowSize.y);

				mLockedListeners = false;

				mWindowResolutionChanged = false;
			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long createWindow(GameInfo pGameInfo, boolean pFullScreen, int pWidth, int pHeight, boolean pResizable) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating GLFWWindow");

		// All GLFW errors to the system err print stream
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		if (!glfwInit()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure the window
		glfwDefaultWindowHints(); // GL3.2 CORE
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, GL_TRUE);

		// mStretchToFit = pGameInfo.stretchGameViewportToWindow();
		// mMaintainAspectRatio = pGameInfo.maintainAspectRatio();

		// Get the current desktop video mode
		mDesktopVideoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

		if (mFullScreen) {
			// Get the native resolution
			GLFWVidMode lVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			int lFullScreenWidth = lVidMode.width();
			int lFullScreenHeight = lVidMode.height();

			mWindowSize.x = lFullScreenWidth;
			mWindowSize.y = lFullScreenHeight;

			glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

			long lNewWindowID = glfwCreateWindow(lFullScreenWidth, lFullScreenHeight, pGameInfo.windowTitle(), glfwGetPrimaryMonitor(), mWindowID);
			glfwDestroyWindow(mWindowID);

			mWindowID = lNewWindowID;

		}

		// Create a new windowed window
		else {

			GLFWVidMode lVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			mWindowSize.x = pWidth;
			mWindowSize.y = pHeight;

			glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
			glfwWindowHint(GLFW_RESIZABLE, pResizable ? GL_TRUE : GL_FALSE);

			long lNewWindowID = glfwCreateWindow(mWindowSize.x, mWindowSize.y, pGameInfo.windowTitle(), NULL, mWindowID);
			glfwDestroyWindow(mWindowID);

			mWindowID = lNewWindowID;

			// center the window
			glfwSetWindowPos(mWindowID, (lVidMode.width() - mWindowSize.x) / 2, (lVidMode.height() - mWindowSize.y) / 2);

		}

		// Set a minimum window
		glfwSetWindowSizeLimits(mWindowID, pWidth, pHeight, GLFW_DONT_CARE, GLFW_DONT_CARE);

		// Make the openGL the current context
		glfwMakeContextCurrent(mWindowID);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		mVSYNCEnabled = true;
		glfwSwapInterval(mVSYNCEnabled ? 1 : 0); // cap to 60 (v-sync)

		glfwShowWindow(mWindowID);
		mIsWindowFocused = glfwGetWindowAttrib(mWindowID, GLFW_VISIBLE) != 0;

		GLDebug.checkGLErrors();

		// TODO: JoH Fix this thing
		// // Create only one size callback thing
		if (mFrameBufferSizeCallback == null) {
			mFrameBufferSizeCallback = new GLFWFramebufferSizeCallback() {

				@Override
				public void invoke(long window, int width, int height) {
					changeResolution(width, height);

				}

			};

		}

		glfwSetFramebufferSizeCallback(mWindowID, mFrameBufferSizeCallback);
		glfwSetWindowFocusCallback(mWindowID, new GLFWWindowFocusCallback() {

			@Override
			public void invoke(long pWindowID, boolean pIsFocused) {
				mIsWindowFocused = pIsFocused;

			}

		});

		// Set our default OpenGL variables
		onInitialiseGL();

		mStretchGameScreen = pGameInfo.stretchGameResolution();
		mBaseGameResolutionWidth = pGameInfo.baseGameResolutionWidth();
		mBaseGameResolutionHeight = pGameInfo.baseGameResolutionHeight();

		// Setup the UI to match the new resolution
		changeResolution(mWindowSize.x, mWindowSize.y);

		return mWindowID;

	}

	public void toggleFullscreen(int pWidth, int pHeight) {
		if (glfwGetWindowMonitor(mWindowID) == NULL) { // Currently in windowed mode
			setGLFWMonitor(glfwGetPrimaryMonitor(), 0, 0, pWidth, pHeight, mVSYNCEnabled);

		} else {
			GLFWVidMode lVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			int lFullScreenWidth = lVidMode.width();
			int lFullScreenHeight = lVidMode.height();

			setGLFWMonitor(NULL, lFullScreenWidth / 2 - pWidth / 2, lFullScreenHeight / 2 - pHeight / 2, pWidth, pHeight, mVSYNCEnabled);

		}

	}

	public void setGLFWMonitor(long pMonitorHandle, int pX, int pY, int pWidth, int pHeight, boolean pVSync) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Changing videomode to Monitor(" + pMonitorHandle + ") " + pWidth + "x" + pHeight);
		
		glfwSetWindowMonitor(mWindowID, pMonitorHandle, pX, pY, pWidth, pHeight, 60);

		mFullScreen = pMonitorHandle != NULL;

		changeResolution(pWidth, pHeight);

		if (pMonitorHandle == NULL) { // windowed mode
			// The repoll the monitor to get the OS resolution
			if (mDesktopVideoMode == null)
				mDesktopVideoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			int lMonitorWidth = mDesktopVideoMode.width();
			int lMonitorHeight = mDesktopVideoMode.height();

			GLFW.glfwSetWindowPos(mWindowID, lMonitorWidth / 2 - pWidth / 2, lMonitorHeight / 2 - pHeight / 2);

		}

		mVSYNCEnabled = pVSync;
		glfwSwapInterval(mVSYNCEnabled ? 1 : 0); // cap to 60 (v-sync)

	}

	private void onInitialiseGL() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		Debug.debugManager().logger().i(getClass().getSimpleName(), "GLFW Version" + glfwGetVersionString());

		GL11.glClearStencil(0); // Specify the index used when stencil buffer is cleared

		if (ConstantsTable.getBooleanValueDef("DENBUG_APP", false)) {
			GL11.glClearColor(ColorConstants.BUFFER_CLEAR_DEBUG.x, ColorConstants.BUFFER_CLEAR_DEBUG.y, ColorConstants.BUFFER_CLEAR_DEBUG.z, 1.0f);
		} else {
			GL11.glClearColor(ColorConstants.BUFFER_CLEAR_RELEASE.x, ColorConstants.BUFFER_CLEAR_RELEASE.y, ColorConstants.BUFFER_CLEAR_RELEASE.z, 1.0f);
		}

	}

	public void changeResolution(int pWidth, int pHeight) {
		if (pWidth == 0 || pHeight == 0)
			return;

		mWindowResolutionChanged = true;
		mWindowWasResized = true;

		mWindowSize.x = pWidth;
		mWindowSize.y = pHeight;

		GL11.glViewport(0, 0, pWidth, pHeight);

	}

	public static void saveOptions() {

	}

	public static void loadOptions() {

	}

	public void addResizeListener(IResizeListener pListener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot add window size listeners from within onResize() callback.");

		if (!mWindowResizeListeners.contains(pListener)) {
			mWindowResizeListeners.add(pListener);
		}

	}

	public void removeResizeListener(IResizeListener pListener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot remove window size listeners from within onResize() callback.");

		if (mWindowResizeListeners.contains(pListener)) {
			mWindowResizeListeners.remove(pListener);
		}
	}

	public void addGameResizeListener(IResizeListener pListener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot add game viewport size listeners from within onResize() callback.");

		if (!mGameResizeListeners.contains(pListener)) {
			mGameResizeListeners.add(pListener);

		}
	}

	public void removeGameResizeListener(IResizeListener pListener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot remove game viewport size listeners from within onResize() callback.");

		if (mGameResizeListeners.contains(pListener)) {
			mGameResizeListeners.remove(pListener);
		}
	}

}
