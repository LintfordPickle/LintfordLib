package net.lintford.library.options;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetVersionString;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwMaximizeWindow;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsApp;
import net.lintford.library.GameInfo;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.GLDebug;
import net.lintford.library.core.debug.stats.DebugStatTagString;
import net.lintford.library.core.debug.stats.DebugStats;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.options.reader.IniFile;

public class DisplayManager extends IniFile {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	public static final String SECTION_NAME_SETTINGS = "Settings";
	public static final String SECTION_NAME_GRAPHICS = "Graphics";
	public static final String SECTION_NAME_UI = "UI";
	public static final String SECTION_NAME_MISC = "Misc";

	/** With a 'hard'refresh rate, we limit the selection of resolutions to 60Hz */
	public static final boolean HARD_REFRESH_RATE = false;
	public static final int MIN_REFRESH_RATE = 40;
	public static final int MAX_REFRESH_RATE = 61;

	public boolean VBOS_ENABLED = true;
	public boolean FBOS_ENABLED = true;

	public boolean RENDER_NODE_COLLISION_OUTLINE = false;

	public boolean RENDER_NODE_SHADOWS = false;
	public boolean RENDER_CELL_LIGHTING = true;

	public static final int ASPECT_RATIO_STANDARD_MON_INDEX = 0; // 4:3
	public static final int ASPECT_RATIO_STANDARD_TV_INDEX = 1; // 3:2
	public static final int ASPECT_RATIO_STANDARD_WS_INDEX = 2; // 16:10
	public static final int ASPECT_RATIO_STANDARD_HD_INDEX = 3; // 16:9

	public static final float ASPECT_RATIO_STANDARD_MON = 1.33f; // 4:3
	public static final float ASPECT_RATIO_STANDARD_TV = 1.5f; // 3:2
	public static final float ASPECT_RATIO_STANDARD_WS = 1.6f; // 16:10
	public static final float ASPECT_RATIO_STANDARD_HD = 1.7f; // 16:9

	// --------------------------------------
	// Class Variables
	// --------------------------------------

	private VideoSettings mDisplaySettings;
	private GraphicsSettings mGraphicsSettings;
	private long mOpenGlMainThreadId = -1;

	private GLFWFramebufferSizeCallback mFrameBufferSizeCallback;
	private GLFWVidMode mDesktopVideoMode;

	private int mDesktopWidth;
	private int mDesktopHeight;

	private long mMasterWindowId;
	private long mOffscreenWindowId;

	private boolean mStretchGameScreen = false;
	private boolean mRecompileShaders = false;

	private final int mGameResolutionWidth;
	private final int mGameResolutionHeight;

	private final int mUiResolutionWidth;
	private final int mUiResolutionHeight;

	private boolean mWindowWasResized;
	boolean mWindowResolutionChanged;
	boolean mWaitForMouseRelease;
	/**
	 * We lock the local listeners while traversing the Listeners list after a resolution change is detected. This prevents other developers from changing the size of the list while its being iterated on.
	 */
	private boolean mLockedListeners;
	private boolean mIsWindowFocused;

	private List<IResizeListener> mWindowResizeListeners;
	private List<IResizeListener> mGameResizeListeners;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long mainOpenGlThreadId() {
		return mOpenGlMainThreadId;
	}

	public VideoSettings currentOptionsConfig() {
		return mDisplaySettings;
	}

	public GraphicsSettings graphicsSettings() {
		return mGraphicsSettings;
	}

	public GLFWVidMode desktopVideoMode() {
		return mDesktopVideoMode;
	}

	public boolean isWindowFocused() {
		return mIsWindowFocused;
	}

	public long windowID() {
		return mMasterWindowId;
	}

	public long sharedContextId() {
		return mOffscreenWindowId;
	}

	public boolean recompileShaders() {
		return mRecompileShaders;
	}

	public boolean vsyncEnabled() {
		return mDisplaySettings.vSyncEnabled();
	}

	public int windowWidth() {
		return mDisplaySettings.windowWidth();
	}

	public int windowHeight() {
		return mDisplaySettings.windowHeight();
	}

	public boolean isSharedContextCreated() {
		return mOffscreenWindowId != 0;
	}

	public boolean windowWasResized() {
		return mWindowWasResized;
	}

	public boolean stretchGameScreen() {
		return mStretchGameScreen;
	}

	public int gameResolutionWidth() {
		return mGameResolutionWidth;
	}

	public int gameResolutionHeight() {
		return mGameResolutionHeight;
	}

	public int uiResolutionWidth() {
		return mUiResolutionWidth;
	}

	public int uiResolutionHeight() {
		return mUiResolutionHeight;
	}

	public void resizeListeners(List<IResizeListener> resizeListeners) {
		mWindowResizeListeners = resizeListeners;
	}

	public List<IResizeListener> resizeListeners() {
		return mWindowResizeListeners;
	}

	public int desktopWidth() {
		return mDesktopWidth;
	}

	public int desktopHeight() {
		return mDesktopHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DisplayManager(GameInfo gameInfo, String configFilename) {
		super(configFilename);

		mGameResolutionWidth = gameInfo.gameCanvasResolutionWidth();
		mGameResolutionHeight = gameInfo.gameCanvasResolutionHeight();

		mUiResolutionWidth = gameInfo.uiCanvasResolutionWidth();
		mUiResolutionHeight = gameInfo.uiCanvasResolutionHeight();

		mDisplaySettings = VideoSettings.createBasicTemplate();
		mGraphicsSettings = GraphicsSettings.createBasicTemplate();

		mWindowResizeListeners = new ArrayList<>();
		mGameResizeListeners = new ArrayList<>();

		loadConfig(gameInfo);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	private void oninitializeGL() {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
		Debug.debugManager().logger().i(getClass().getSimpleName(), "GLFW Version" + glfwGetVersionString());

		GL11.glClearStencil(0); // Specify the index used when stencil buffer is cleared

		if (ConstantsApp.getBooleanValueDef("DENBUG_APP", false)) {
			GL11.glClearColor(ColorConstants.BUFFER_CLEAR_DEBUG.r, ColorConstants.BUFFER_CLEAR_DEBUG.g, ColorConstants.BUFFER_CLEAR_DEBUG.b, 1.0f);
		} else {
			GL11.glClearColor(ColorConstants.BUFFER_CLEAR_RELEASE.r, ColorConstants.BUFFER_CLEAR_RELEASE.g, ColorConstants.BUFFER_CLEAR_RELEASE.b, 1.0f);
		}
	}

	public void update(LintfordCore core) {
		if (mWindowResolutionChanged) {
			synchronized (this) {
				if (stretchGameScreen() == false) {
					mLockedListeners = true;
					final int lNumWindowResizeListeners = mWindowResizeListeners.size();

					Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Resolution changed: %dx%d", mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight()));
					Debug.debugManager().logger().i("SYSTEM", "calling window resize listeners.");

					for (int i = 0; i < lNumWindowResizeListeners; i++) {
						if (mWindowResizeListeners.get(i) == null)
							continue;

						mWindowResizeListeners.get(i).onResize(mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight());
					}

					mLockedListeners = false;
				}

				mWindowResolutionChanged = false;

				saveConfig();
			}
		}
	}

	public void loadConfig(GameInfo gameInfo) {
		super.loadConfig();

		// if no file previously existed, the underlying config is empty, so we need to set some defaults
		if (isEmpty()) {
			// create default values from the GameInfo
			mDisplaySettings.monitorIndex(NULL); // default to primary monitor first time
			mDisplaySettings.fullScreenIndex(gameInfo.defaultFullScreen() ? VideoSettings.FULLSCREEN_YES_INDEX : VideoSettings.FULLSCREEN_NO_INDEX);
			mDisplaySettings.windowWidth(gameInfo.gameCanvasResolutionWidth());
			mDisplaySettings.windowHeight(gameInfo.gameCanvasResolutionHeight());
			mDisplaySettings.resizeable(gameInfo.windowResizeable());

			saveConfig();
		} else {
			// Get the values we need
			// final var lSavedMonitorIndex = getLong("Settings", "MonitorIndex", glfwGetPrimaryMonitor());
			// TODO: Verify the monitor index is (still?) valid

			// mDisplaySettings.monitorIndex(lSavedMonitorIndex);
			mDisplaySettings.windowWidth(getInt(SECTION_NAME_SETTINGS, "WindowWidth", gameInfo.gameCanvasResolutionWidth()));
			mDisplaySettings.windowHeight(getInt(SECTION_NAME_SETTINGS, "WindowHeight", gameInfo.gameCanvasResolutionHeight()));
			mDisplaySettings.fullScreenIndex(getInt(SECTION_NAME_SETTINGS, "WindowFullscreen", VideoSettings.FULLSCREEN_NO_INDEX));
			mDisplaySettings.vSyncEnabled(getBoolean(SECTION_NAME_SETTINGS, "vSync", true));
			mDisplaySettings.resizeable(gameInfo.windowResizeable()); // not overridable from config file

			mGraphicsSettings.setUiUserScale((float) getInt(SECTION_NAME_UI, "uiScale", 100) / 100f);
			mGraphicsSettings.setUiUserTextScale((float) getInt(SECTION_NAME_UI, "uiTextScale", 100) / 100f);
			mGraphicsSettings.setUiUserTransparencyScale((float) getInt(SECTION_NAME_UI, "uiTransparencyScale", 80) / 100f);

			saveConfig();
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		setValue(SECTION_NAME_SETTINGS, "MonitorIndex", mDisplaySettings.monitorIndex());
		setValue(SECTION_NAME_SETTINGS, "WindowWidth", mDisplaySettings.windowWidth());
		setValue(SECTION_NAME_SETTINGS, "WindowHeight", mDisplaySettings.windowHeight());
		setValue(SECTION_NAME_SETTINGS, "WindowFullscreen", mDisplaySettings.fullScreenIndex());
		setValue(SECTION_NAME_SETTINGS, "vSync", mDisplaySettings.vSyncEnabled());

		setValue(SECTION_NAME_GRAPHICS, "VBOS_ENABLED", "true");
		setValue(SECTION_NAME_GRAPHICS, "FBOS_ENABLED", "true");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_NODE_COLLISION_OUTLINE", "false");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_NODE_SHADOWS", "false");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_CELL_LIGHTING", "true");

		setValue(SECTION_NAME_MISC, "Created", DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now()));

		setValue(SECTION_NAME_UI, "uiScale", (int) (mGraphicsSettings.UiUserScale() * 100f));
		setValue(SECTION_NAME_UI, "uiTextScale", (int) (mGraphicsSettings.UiUserTextScale() * 100f));
		setValue(SECTION_NAME_UI, "uiTransparencyScale", (int) (mGraphicsSettings.UiUserTransparencyScale() * 100f));

		super.saveConfig();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long createWindow(GameInfo gameInfo) {
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating GLFWWindow");

		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.out));

		if (!glfwInit()) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to initialize GLFW");
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure the window
		glfwDefaultWindowHints(); // GL3.2 CORE
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		// Remove all functionality marked as deprecated from the OpenGl context
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, GL_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, mDisplaySettings.resizeable() ? GL_TRUE : GL_FALSE);

		if (validateSavedMonitor(gameInfo, mDisplaySettings.monitorIndex()) == false) {
			mDisplaySettings.monitorIndex(GLFW.glfwGetPrimaryMonitor());
		}

		mDesktopVideoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		mDesktopWidth = mDesktopVideoMode.width();
		mDesktopHeight = mDesktopVideoMode.height();

		if (!gameInfo.windowResizeable()) {
			mDisplaySettings.windowWidth(gameInfo.gameCanvasResolutionWidth());
			mDisplaySettings.windowHeight(gameInfo.gameCanvasResolutionHeight());
		}

		validateFullScreenResolution(gameInfo);

		if (mDisplaySettings.fullscreen()) {
			long lNewWindowID = glfwCreateWindow(mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight(), gameInfo.windowTitle(), mDisplaySettings.monitorIndex(), mMasterWindowId);
			if (lNewWindowID == NULL) {
				throw new IllegalStateException("Failed to create the GLFW window");
			}

			glfwDestroyWindow(mMasterWindowId);

			mMasterWindowId = lNewWindowID;
		} else {
			long lMonitorID = NULL; // windowed
			long lNewWindowID = glfwCreateWindow(mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight(), gameInfo.windowTitle(), lMonitorID, mMasterWindowId);
			glfwDestroyWindow(mMasterWindowId);

			mMasterWindowId = lNewWindowID;

			if (mDisplaySettings.windowWidth() == mDesktopWidth && mDisplaySettings.windowHeight() == mDesktopHeight) {
				glfwMaximizeWindow(mMasterWindowId);

				int[] w = new int[2];
				int[] h = new int[2];
				glfwGetWindowSize(mMasterWindowId, w, h);
				mDisplaySettings.windowWidth(w[0]);
				mDisplaySettings.windowHeight(h[0]);
			}

			glfwSetWindowPos(mMasterWindowId, (mDesktopWidth - mDisplaySettings.windowWidth()) / 2, (mDesktopHeight - mDisplaySettings.windowHeight()) / 2);
		}

		glfwSetWindowSizeLimits(mMasterWindowId, gameInfo.minimumWindowWidth(), gameInfo.minimumWindowHeight(), GLFW_DONT_CARE, GLFW_DONT_CARE);

		mOpenGlMainThreadId = Thread.currentThread().getId();

		// The main opengl thread is important, because it allows us to ensure that we only initial OpenGl ocntainers
		// on the main context thread (as containers are not shared with shared contexts).
		Debug.debugManager().logger().i(getClass().getSimpleName(), "Main OpenGl thread id: " + mOpenGlMainThreadId);

		makeContextCurrent(mMasterWindowId);

		createGlCompatiblities();

		glfwSwapInterval(mDisplaySettings.vSyncEnabled() ? 1 : 0); // cap to 60 (v-sync)

		glfwShowWindow(mMasterWindowId);
		mIsWindowFocused = glfwGetWindowAttrib(mMasterWindowId, GLFW_VISIBLE) != 0;

		// Create only one size callback thing
		if (mFrameBufferSizeCallback == null) {
			mFrameBufferSizeCallback = new GLFWFramebufferSizeCallback() {
				@Override
				public void invoke(long window, int width, int height) {
					changeResolution(width, height);
				}
			};
		}

		glfwSetFramebufferSizeCallback(mMasterWindowId, mFrameBufferSizeCallback);
		glfwSetWindowFocusCallback(mMasterWindowId, new GLFWWindowFocusCallback() {
			@Override
			public void invoke(long pWindowID, boolean pIsFocused) {
				mIsWindowFocused = pIsFocused;
			}
		});

		oninitializeGL();

		mStretchGameScreen = gameInfo.stretchGameResolution();

		changeResolution(mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight());

		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Window Position:   (" + mDisplaySettings.windowPositionX() + "," + mDisplaySettings.windowPositionY() + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Window Size:       (" + mDisplaySettings.windowWidth() + "," + mDisplaySettings.windowHeight() + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Desktop Size:      (" + mDesktopWidth + "," + mDesktopHeight + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Fullscreen Index:   " + mDisplaySettings.fullScreenIndex());
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Monitor Index:      " + mDisplaySettings.monitorIndex());
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Aspect Radio Index: " + mDisplaySettings.aspectRatioIndex());

		createSharedContext();

		GLDebug.checkGLErrorsException();

		return mMasterWindowId;
	}

	public long createSharedContext() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, GL_TRUE);

		// by specifing the masterwindow id, we create a openGL shared context
		mOffscreenWindowId = glfwCreateWindow(1, 1, "Shared Context", NULL, mMasterWindowId);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Created shared context (master window: " + mMasterWindowId + ", offscreen window: " + mOffscreenWindowId + ")");

		return mOffscreenWindowId;
	}

	public void makeOffscreenContextCurrentOnThread() {
		makeContextCurrent(mOffscreenWindowId);
		createGlCompatiblities();
	}

	public void makeContextCurrent(long windowId) {
		glfwMakeContextCurrent(windowId);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Context made current on window: " + windowId);
	}

	/**
	 * This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities instance and makes the OpenGL bindings available for use.
	 */
	public void createGlCompatiblities() {
		GL.createCapabilities();
	}

	public void destroySharedContext() {
		glfwDestroyWindow(mOffscreenWindowId);
		mOffscreenWindowId = 0;

		glfwMakeContextCurrent(mMasterWindowId);
	}

	public void setGLFWMonitor(VideoSettings desiredSettings) {

		final long lMonitorIndex = desiredSettings.fullscreen() ? desiredSettings.monitorIndex() : NULL;
		final int lWindowPositionX = desiredSettings.windowPositionX();
		final int lWindowPositionY = desiredSettings.windowPositionY();
		final int lWindowWidth = desiredSettings.windowWidth();
		final int lWindowHeight = desiredSettings.windowHeight();
		final int lRefreshRate = desiredSettings.refreshRate();

		glfwSetWindowMonitor(mMasterWindowId, lMonitorIndex, lWindowPositionX, lWindowPositionY, lWindowWidth, lWindowHeight, lRefreshRate);

		// In the case the window is in windowed mode, and the desired size is that of the desktop
		if (desiredSettings.windowWidth() == mDesktopWidth && desiredSettings.windowHeight() == mDesktopHeight) {
			glfwMaximizeWindow(mMasterWindowId);

			int[] w = new int[2];
			int[] h = new int[2];

			glfwGetWindowSize(mMasterWindowId, w, h);
			desiredSettings.windowWidth(w[0]);
			desiredSettings.windowHeight(h[0]);
		}

		int[] w = new int[2];
		int[] h = new int[2];
		glfwGetWindowSize(mMasterWindowId, w, h);
		desiredSettings.windowWidth(w[0]);
		desiredSettings.windowHeight(h[0]);

		glfwSetWindowPos(mMasterWindowId, (mDesktopWidth - desiredSettings.windowWidth()) / 2, (mDesktopHeight - desiredSettings.windowHeight()) / 2);

		changeResolution(desiredSettings.windowWidth(), desiredSettings.windowHeight());
		glfwSwapInterval(desiredSettings.vSyncEnabled() ? 1 : 0);
		mDisplaySettings.copy(desiredSettings);

		saveConfig();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "  actual: monitor:" + desiredSettings.monitorIndex() + " (" + desiredSettings.windowWidth() + "x" + desiredSettings.windowHeight() + ")");
	}

	public void changeResolution(int width, int height) {
		if (width == 0 || height == 0)
			return;

		mWindowResolutionChanged = true;
		mWindowWasResized = true;

		mDisplaySettings.windowWidth(width);
		mDisplaySettings.windowHeight(height);

		GL11.glViewport(0, 0, width, height);
	}

	public void reapplyGlViewport() {
		GL11.glViewport(0, 0, mDisplaySettings.windowWidth(), mDisplaySettings.windowHeight());
	}

	private boolean validateSavedMonitor(GameInfo gamnfo, long monitorId) {
		if (monitorId <= 0) {
			return false;
		}

		// Don't think you can restore monitors

		return false;
	}

	/**
	 * This method ensures that the resolution loaded from the INI matches one of the resolutions that the monitor the window is on supports. If not, that the game will resort to the 'default' resolution.
	 */
	private void validateFullScreenResolution(GameInfo gameInfo) {
		if (mDisplaySettings.fullscreen() == false) {
			if (mDisplaySettings.windowWidth() > mDesktopWidth)
				mDisplaySettings.windowWidth(mDesktopWidth);

			if (mDisplaySettings.windowHeight() > mDesktopHeight)
				mDisplaySettings.windowHeight(mDesktopHeight);

			return;
		}

		var lVidModes = GLFW.glfwGetVideoModes(mDisplaySettings.monitorIndex());

		final int lLookingForWidth = mDisplaySettings.windowWidth();
		final int lLookingForHeight = mDisplaySettings.windowHeight();

		final int lNumVidModesFound = lVidModes.limit();
		for (int i = 0; i < lNumVidModesFound; i++) {
			final var lVidMode = lVidModes.get();

			// Ignore resolution entries based on low refresh rates
			if (lVidMode.refreshRate() < 40)
				continue;

			if (lVidMode.width() == lLookingForWidth && lVidMode.height() == lLookingForHeight) {
				return;
			}

		}

		// If we reach this point, then the saved resolution was not found on the monitor and we should revert back
		// to the base resolution of the game (in windowed mode)
		mDisplaySettings.windowWidth(gameInfo.gameCanvasResolutionWidth());
		mDisplaySettings.windowHeight(gameInfo.gameCanvasResolutionHeight());
		mDisplaySettings.fullScreenIndex(VideoSettings.FULLSCREEN_NO_INDEX);

		// TODO: Display toast to the user than an unsupported resolution was saved

		Debug.debugManager().logger().w(getClass().getSimpleName(), "Non-standard resolution found (" + lLookingForWidth + "," + lLookingForHeight + ")! Defaulting back to " + mDisplaySettings.windowWidth() + "," + mDisplaySettings.windowHeight());
	}

	// --------------------------------------
	// Resize Listeners
	// --------------------------------------

	public void addResizeListener(IResizeListener listener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot add window size listeners from within onResize() callback.");

		if (!mWindowResizeListeners.contains(listener)) {
			mWindowResizeListeners.add(listener);
		}
	}

	public void removeResizeListener(IResizeListener listener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot remove window size listeners from within onResize() callback.");

		if (mWindowResizeListeners.contains(listener)) {
			mWindowResizeListeners.remove(listener);
		}
	}

	public void addGameResizeListener(IResizeListener listener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot add game viewport size listeners from within onResize() callback.");

		if (!mGameResizeListeners.contains(listener)) {
			mGameResizeListeners.add(listener);
		}
	}

	public void removeGameResizeListener(IResizeListener listener) throws IllegalStateException {
		if (mLockedListeners)
			throw new IllegalStateException("Cannot remove game viewport size listeners from within onResize() callback.");

		if (mGameResizeListeners.contains(listener)) {
			mGameResizeListeners.remove(listener);
		}
	}

	public void setDisplayMouse(boolean showMouse) {
		if (showMouse) {
			GLFW.glfwSetInputMode(mMasterWindowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		} else {
			GLFW.glfwSetInputMode(mMasterWindowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
		}
	}

	/** returns the closest matching Aspect Ratio to the given resolution */
	public int getClosestAR(float width, float height) {
		float lAR = width / height;
		if (lAR < ASPECT_RATIO_STANDARD_MON) {
			return ASPECT_RATIO_STANDARD_MON_INDEX;
		} else if (lAR < ASPECT_RATIO_STANDARD_TV) {
			return ASPECT_RATIO_STANDARD_TV_INDEX;
		} else if (lAR < ASPECT_RATIO_STANDARD_WS) {
			return ASPECT_RATIO_STANDARD_WS_INDEX;
		} else {
			return ASPECT_RATIO_STANDARD_HD_INDEX;
		}
	}
}