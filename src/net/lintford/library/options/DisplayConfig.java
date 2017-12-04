package net.lintford.library.options;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
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

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsTable;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.DebugManager;
import net.lintford.library.core.debug.DebugManager.DebugLogLevel;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.maths.Vector2i;

/** FIXME: Need to correct toggle between fullscreen, borderless window etc. FIXME: Need to add a dirty flag to confirm options */

/** TODO: Implement GameResizeListener for all GLContent reloading */
public class DisplayConfig extends BaseConfig {

	public class DisplayResolution {
		public final String displayName;
		public final int width;
		public final int height;

		public DisplayResolution(String pDisplayName, int pWidth, int pHeight) {
			displayName = pDisplayName;
			width = pWidth;
			height = pHeight;

		}

	}

	public void fillFillResolutionList() {

		List<DisplayResolution> mResolutions = new ArrayList<>();
		mResolutions.add(new DisplayResolution("800x600 (4:3)", 800, 600));
		mResolutions.add(new DisplayResolution("1024x768 (4:3)", 1024, 768));
		mResolutions.add(new DisplayResolution("1280x720 (16:9)", 1280, 720));
		mResolutions.add(new DisplayResolution("1280x768 (5:3)", 1280, 768));
		mResolutions.add(new DisplayResolution("1280x800 (8:5)", 1280, 800));
		mResolutions.add(new DisplayResolution("1280x1024 (5:4)", 1280, 1024));
		mResolutions.add(new DisplayResolution("1360x768 (16:9)", 1360, 768)); // Not sure about this
		mResolutions.add(new DisplayResolution("1366x768 (16:9)", 1366, 768));
		mResolutions.add(new DisplayResolution("1440x900 (8:5)", 1440, 900));
		mResolutions.add(new DisplayResolution("1536x864 (16:9)", 1536, 864));
		mResolutions.add(new DisplayResolution("1600x900 (16:9)", 1600, 900));
		mResolutions.add(new DisplayResolution("1600x1200 (4:3)", 1600, 1200));
		mResolutions.add(new DisplayResolution("1680x1050 (8:5)", 1680, 1050));
		mResolutions.add(new DisplayResolution("1920x1080 (16:9)", 1920, 1080));
		mResolutions.add(new DisplayResolution("1920x1200 (8:5)", 1920, 1200));

	}

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

	private boolean mFullScreen;
	private Vector2i mWindowSize;
	private Vector2i mHUDSize;
	private Vector2i mGameViewportSize;

	private boolean mVSYNCEnabled;
	private boolean mWindowIsResizable;
	private boolean mWindowWasResized;

	private TARGET_FPS mTargetFPS;

	private GLFWFramebufferSizeCallback mFrameBufferSizeCallback;
	private long mWindowID = NULL;
	private boolean mRecompileShaders = false;

	List<IResizeListener> mWindowResizeListeners;
	List<IResizeListener> mGameResizeListeners;

	List<Vector2i> mGameResolutions; // Resolutions supported by the GAME
	boolean mWindowResolutionChanged;
	boolean mWaitForMouseRelease;

	/** We lock the local listeners while traversing the Listeners list after a resolution change is detected. This prevents the programmer's from changing the size of the list. */
	private boolean mLockedListeners;

	private boolean mIsWindowFocused;

	// --------------------------------------
	// Properties
	// --------------------------------------

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

	public Vector2i HUDSize() {
		return mHUDSize;
	}

	public Vector2i windowSize() {
		return mWindowSize;
	}

	public Vector2i gameViewportSize() {
		return mGameViewportSize;
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
		mWindowSize.x = WINDOW_MINIMUM_WIDTH;
		mWindowSize.y = WINDOW_MINIMUM_HEIGHT;

		mHUDSize = new Vector2i();
		mHUDSize.x = WINDOW_MINIMUM_WIDTH;
		mHUDSize.y = WINDOW_MINIMUM_HEIGHT;

		mGameViewportSize = new Vector2i();
		mGameViewportSize.x = WINDOW_MINIMUM_WIDTH;
		mGameViewportSize.y = WINDOW_MINIMUM_HEIGHT;

		mFullScreen = true;
		mWindowIsResizable = true;
		mVSYNCEnabled = true;
		mTargetFPS = TARGET_FPS.fps60;

		mWindowResizeListeners = new ArrayList<>();
		mGameResizeListeners = new ArrayList<>();

		// DEBUG
		// TODO: Need to load the last display configuration from file
		mFullScreen = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {
		if (mWindowResolutionChanged /* && !pInputState.mouseLeftClick() */) {
			synchronized (this) {
				DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Change Resolution changed: " + mWindowSize.x + "," + mWindowSize.y);

				mLockedListeners = true;
				int lListenerCount = mWindowResizeListeners.size();

				for (int i = 0; i < lListenerCount; i++) {
					if (mWindowResizeListeners.get(i) == null)
						continue;

					DebugManager.DEBUG_MANAGER.logger().log(DebugLogLevel.info, "SYSTEM", "calling window resize listener: " + mWindowResizeListeners.get(i).getClass().getSimpleName());

					mWindowResizeListeners.get(i).onResize(mWindowSize.x, mWindowSize.y);

				}

				mLockedListeners = false;

				mWindowResolutionChanged = false;
			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void toggleFullScreenFlag() {
		mFullScreen = !mFullScreen;
	}

	public long onCreateWindow(GameInfo pGameInfo) {
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "Creating GLFWWindow");

		// All GLFW errors to the system err print stream
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		if (!glfwInit()) {
			DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure the window
		glfwDefaultWindowHints(); // GL3.2 CORE
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

		if (mFullScreen) {
			// Get the native resolution
			GLFWVidMode lVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			int lFullScreenWidth = lVidMode.width();
			int lFullScreenHeight = lVidMode.height();

			mWindowSize.x = lFullScreenWidth;
			mWindowSize.y = lFullScreenHeight;

			long lNewWindowID = glfwCreateWindow(lFullScreenWidth, lFullScreenHeight, pGameInfo.windowTitle(), glfwGetPrimaryMonitor(), mWindowID);
			if (mWindowID != NULL) {
				glfwDestroyWindow(mWindowID);
			}

			mWindowID = lNewWindowID;

			if (mWindowID == NULL) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Unable to create window!");

				throw new IllegalStateException("Unable to create GLFWWindow!");

			}
		}

		// Create a new windowed window
		else {

			GLFWVidMode lVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			mWindowSize.x = WINDOW_MINIMUM_WIDTH;
			mWindowSize.y = WINDOW_MINIMUM_HEIGHT;

			long lNewWindowID = glfwCreateWindow(mWindowSize.x, mWindowSize.y, pGameInfo.windowTitle(), NULL, mWindowID);
			if (mWindowID != NULL) {
				glfwDestroyWindow(mWindowID);
			}
			mWindowID = lNewWindowID;

			if (mWindowID == NULL) {
				DebugManager.DEBUG_MANAGER.logger().e(getClass().getSimpleName(), "Unable to create window!");

				throw new IllegalStateException("Unable to create GLFWWindow!");

			}

			// center the window

			glfwSetWindowPos(mWindowID, (lVidMode.width() - mWindowSize.x) / 2, (lVidMode.height() - mWindowSize.y) / 2);
		}

		// Set a minimum window
		glfwSetWindowSizeLimits(mWindowID, WINDOW_MINIMUM_WIDTH, WINDOW_MINIMUM_HEIGHT, GLFW_DONT_CARE, GLFW_DONT_CARE);

		// Make the openGL the current context
		glfwMakeContextCurrent(mWindowID);
		GL.createCapabilities();

		mVSYNCEnabled = true;
		glfwSwapInterval(mVSYNCEnabled ? 1 : 0); // cap to 60 (v-sync)

		glfwShowWindow(mWindowID);
		mIsWindowFocused = glfwGetWindowAttrib(mWindowID, GLFW_VISIBLE) != 0;

		GLDebug.checkGLErrors();

		// Create only one size callback thing
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

		// Setup the UI to match the new resolution
		changeResolution(mWindowSize.x, mWindowSize.y);

		return mWindowID;

	}

	private void onInitialiseGL() {
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		DebugManager.DEBUG_MANAGER.logger().i(getClass().getSimpleName(), "GLFW Version" + glfwGetVersionString());

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

		mHUDSize.x = pWidth;
		mHUDSize.y = pHeight;

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
