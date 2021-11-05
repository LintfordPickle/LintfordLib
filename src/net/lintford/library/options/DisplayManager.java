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
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
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
import net.lintford.library.core.debug.Debug.DebugLogLevel;
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

	private GLFWFramebufferSizeCallback mFrameBufferSizeCallback;
	private GLFWVidMode mDesktopVideoMode;

	private int mDesktopWidth;
	private int mDesktopHeight;

	private long mMasterWindowId;
	private long mOffscreenWindowId;

	private boolean mStretchGameScreen = false;
	private boolean mRecompileShaders = false;
	private final int mBaseGameResolutionWidth;
	private final int mBaseGameResolutionHeight;
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
		return mDisplaySettings.vSyncEnabled;
	}

	public int windowWidth() {
		return mDisplaySettings.windowWidth;
	}

	public int windowHeight() {
		return mDisplaySettings.windowHeight;
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

	public int desktopWidth() {
		return mDesktopWidth;
	}

	public int desktopHeight() {
		return mDesktopHeight;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DisplayManager(GameInfo pGameInfo, String pConfigFilename) {
		super(pConfigFilename);

		mBaseGameResolutionWidth = pGameInfo.baseGameResolutionWidth();
		mBaseGameResolutionHeight = pGameInfo.baseGameResolutionHeight();

		// Make sure we always have a solid base instances with valid values
		mDisplaySettings = VideoSettings.createBasicTemplate();
		mGraphicsSettings = GraphicsSettings.createBasicTemplate();

		mWindowResizeListeners = new ArrayList<>();
		mGameResizeListeners = new ArrayList<>();

		loadConfig(pGameInfo);
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

	public void update(LintfordCore pCore) {
		if (mWindowResolutionChanged /* && !pInputState.mouseLeftClick() */) {
			synchronized (this) {
				mLockedListeners = true;
				final int lNumWindowResizeListeners = mWindowResizeListeners.size();

				Debug.debugManager().logger().log(DebugLogLevel.info, getClass().getSimpleName(), String.format("Resolution changed: %dx%d", mDisplaySettings.windowWidth, mDisplaySettings.windowHeight));
				Debug.debugManager().logger().log(DebugLogLevel.info, "SYSTEM", "calling window resize listeners.");

				for (int i = 0; i < lNumWindowResizeListeners; i++) {
					if (mWindowResizeListeners.get(i) == null)
						continue;

					mWindowResizeListeners.get(i).onResize(mDisplaySettings.windowWidth, mDisplaySettings.windowHeight);
				}

				mLockedListeners = false;
				mWindowResolutionChanged = false;

				saveConfig();
			}
		}
	}

	public void loadConfig(GameInfo pGameInfo) {
		super.loadConfig();

		// if no file previously existed, the underlying config is empty, so we need to set some defaults
		if (isEmpty()) {
			// create default values from the GameInfo
			mDisplaySettings.monitorIndex = NULL; // default to primary monitor first time
			mDisplaySettings.fullScreenIndex = pGameInfo.defaultFullScreen() ? VideoSettings.FULLSCREEN_YES_INDEX : VideoSettings.FULLSCREEN_NO_INDEX;
			mDisplaySettings.windowWidth = pGameInfo.baseGameResolutionWidth();
			mDisplaySettings.windowHeight = pGameInfo.baseGameResolutionHeight();
			mDisplaySettings.resizable = pGameInfo.windowResizeable();

			saveConfig();
		} else {
			// Get the values we need
			final var lSavedMonitorIndex = getLong("Settings", "MonitorIndex", glfwGetPrimaryMonitor());
			// TODO: Verify the monitor index is (still?) valid

			mDisplaySettings.monitorIndex = lSavedMonitorIndex;
			mDisplaySettings.windowWidth = getInt(SECTION_NAME_SETTINGS, "WindowWidth", pGameInfo.baseGameResolutionWidth());
			mDisplaySettings.windowHeight = getInt(SECTION_NAME_SETTINGS, "WindowHeight", pGameInfo.baseGameResolutionHeight());
			mDisplaySettings.fullScreenIndex = getInt(SECTION_NAME_SETTINGS, "WindowFullscreen", VideoSettings.FULLSCREEN_NO_INDEX);
			mDisplaySettings.vSyncEnabled = getBoolean(SECTION_NAME_SETTINGS, "vSync", true);
			mDisplaySettings.resizable = pGameInfo.windowResizeable(); // not overridable from config file

			mGraphicsSettings.setUIScale((float) getInt(SECTION_NAME_UI, "uiScale", 100) / 100f);
			mGraphicsSettings.setUITextScale((float) getInt(SECTION_NAME_UI, "uiTextScale", 100) / 100f);
			mGraphicsSettings.setUITransparencyScale((float) getInt(SECTION_NAME_UI, "uiTransparencyScale", 80) / 100f);

			saveConfig();
		}
	}

	@Override
	public void saveConfig() {
		clearEntries();

		// Update the entries in the map
		setValue(SECTION_NAME_SETTINGS, "MonitorIndex", mDisplaySettings.monitorIndex);
		setValue(SECTION_NAME_SETTINGS, "WindowWidth", mDisplaySettings.windowWidth);
		setValue(SECTION_NAME_SETTINGS, "WindowHeight", mDisplaySettings.windowHeight);
		setValue(SECTION_NAME_SETTINGS, "WindowFullscreen", mDisplaySettings.fullScreenIndex);
		setValue(SECTION_NAME_SETTINGS, "vSync", mDisplaySettings.vSyncEnabled);

		setValue(SECTION_NAME_GRAPHICS, "VBOS_ENABLED", "true");
		setValue(SECTION_NAME_GRAPHICS, "FBOS_ENABLED", "true");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_NODE_COLLISION_OUTLINE", "false");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_NODE_SHADOWS", "false");
		setValue(SECTION_NAME_GRAPHICS, "RENDER_CELL_LIGHTING", "true");

		setValue(SECTION_NAME_MISC, "Created", DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDate.now()));

		setValue(SECTION_NAME_UI, "uiScale", (int) (mGraphicsSettings.UIScale() * 100f));
		setValue(SECTION_NAME_UI, "uiTextScale", (int) (mGraphicsSettings.UITextScale() * 100f));
		setValue(SECTION_NAME_UI, "uiTransparencyScale", (int) (mGraphicsSettings.UITransparencyScale() * 100f));

		// save the entries to file
		super.saveConfig();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public long createWindow(GameInfo pGameInfo) {
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
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, GL_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, mDisplaySettings.resizable ? GL_TRUE : GL_FALSE);

		// Get the current desktop video mode
		mDesktopVideoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		mDesktopWidth = mDesktopVideoMode.width();
		mDesktopHeight = mDesktopVideoMode.height();

		// Constraints check (don't allow changes in the INI file)
		if (!pGameInfo.windowResizeable()) {
			mDisplaySettings.windowWidth = pGameInfo.baseGameResolutionWidth();
			mDisplaySettings.windowHeight = pGameInfo.baseGameResolutionHeight();

		}

		validateResolution(pGameInfo);

		if (mDisplaySettings.fullscreen()) {
			// If the monitor index has not been set, then use the primary
			if (mDisplaySettings.monitorIndex == 0) {
				mDisplaySettings.monitorIndex = glfwGetPrimaryMonitor();
			}

			long lNewWindowID = glfwCreateWindow(mDisplaySettings.windowWidth, mDisplaySettings.windowHeight, pGameInfo.windowTitle(), mDisplaySettings.monitorIndex, mMasterWindowId);
			glfwDestroyWindow(mMasterWindowId);

			mMasterWindowId = lNewWindowID;

		}

		// Create a new windowed window
		else {
			long lMonitorID = NULL; // windowed
			long lNewWindowID = glfwCreateWindow(mDisplaySettings.windowWidth, mDisplaySettings.windowHeight, pGameInfo.windowTitle(), lMonitorID, mMasterWindowId);
			glfwDestroyWindow(mMasterWindowId);

			mMasterWindowId = lNewWindowID;

			// In the case the window is in windowed mode, and the desired size is that of the desktop
			if (mDisplaySettings.windowWidth == mDesktopWidth && mDisplaySettings.windowHeight == mDesktopHeight) {
				glfwMaximizeWindow(mMasterWindowId);

				// Adjust the position of the window based on the window size
				int[] w = new int[2];
				int[] h = new int[2];
				glfwGetWindowSize(mMasterWindowId, w, h);
				mDisplaySettings.windowWidth = w[0];
				mDisplaySettings.windowHeight = h[0];

			}

			glfwSetWindowPos(mMasterWindowId, (mDesktopWidth - mDisplaySettings.windowWidth) / 2, (mDesktopHeight - mDisplaySettings.windowHeight) / 2);

		}

		// Set a minimum window
		glfwSetWindowSizeLimits(mMasterWindowId, pGameInfo.minimumWindowWidth(), pGameInfo.minimumWindowHeight(), GLFW_DONT_CARE, GLFW_DONT_CARE);

		// Make the openGL the current context
		makeContextCurrent(mMasterWindowId);

		createGlCompatiblities();

		glfwSwapInterval(mDisplaySettings.vSyncEnabled ? 1 : 0); // cap to 60 (v-sync)

		glfwShowWindow(mMasterWindowId);
		mIsWindowFocused = glfwGetWindowAttrib(mMasterWindowId, GLFW_VISIBLE) != 0;

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

		glfwSetFramebufferSizeCallback(mMasterWindowId, mFrameBufferSizeCallback);
		glfwSetWindowFocusCallback(mMasterWindowId, new GLFWWindowFocusCallback() {

			@Override
			public void invoke(long pWindowID, boolean pIsFocused) {
				mIsWindowFocused = pIsFocused;

			}

		});

		// Set our default OpenGL variables
		oninitializeGL();

		mStretchGameScreen = pGameInfo.stretchGameResolution();

		// Setup the UI to match the new resolution
		changeResolution(mDisplaySettings.windowWidth, mDisplaySettings.windowHeight);

		// output debug information
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Window Position:   (" + mDisplaySettings.windowPositionX + "," + mDisplaySettings.windowPositionY + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Window Size:       (" + mDisplaySettings.windowWidth + "," + mDisplaySettings.windowHeight + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Desktop Size:      (" + mDesktopWidth + "," + mDesktopHeight + ")");
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Fullscreen Index:   " + mDisplaySettings.fullScreenIndex);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Monitor Index:      " + mDisplaySettings.monitorIndex);
		Debug.debugManager().logger().i(getClass().getSimpleName(), "   Aspect Radio Index: " + mDisplaySettings.aspectRatioIndex);

		createSharedContext();

		return mMasterWindowId;
	}

	public long createSharedContext() {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_DECORATED, GL_TRUE);

		// by specifing the masterwindow id, we create a openGL shared context
		mOffscreenWindowId = glfwCreateWindow(1, 1, "Shared Context", NULL, mMasterWindowId);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Creating shared context (offscreen window: " + mOffscreenWindowId + ")");

		return mOffscreenWindowId;
	}

	public void makeOffscreenContextCurrentOnThread() {
		makeContextCurrent(mOffscreenWindowId);
		createGlCompatiblities();
	}

	public void makeContextCurrent(long pWindowId) {
		glfwMakeContextCurrent(pWindowId);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Context made current on window: " + pWindowId);
	}

	/**
	 *  This line is critical for LWJGL's interoperation with GLFW's
		OpenGL context, or any context that is managed externally.
		LWJGL detects the context that is current in the current thread,
		creates the GLCapabilities instance and makes the OpenGL
		bindings available for use.
	 * */
	public void createGlCompatiblities() {
		GL.createCapabilities();
	}

	public void destroySharedContext() {
		glfwDestroyWindow(mOffscreenWindowId);
		mOffscreenWindowId = 0;

		glfwMakeContextCurrent(mMasterWindowId);
	}

	public void setGLFWMonitor(VideoSettings pDesiredSettings) {
		glfwSetWindowMonitor(mMasterWindowId, pDesiredSettings.fullscreen() ? pDesiredSettings.monitorIndex : NULL, pDesiredSettings.windowPositionX, pDesiredSettings.windowPositionY, pDesiredSettings.windowWidth, pDesiredSettings.windowHeight,
				pDesiredSettings.refreshRate);

		// In the case the window is in windowed mode, and the desired size is that of the desktop
		if (pDesiredSettings.windowWidth == mDesktopWidth && pDesiredSettings.windowHeight == mDesktopHeight) {
			glfwMaximizeWindow(mMasterWindowId);

			// Adjust the position of the window based on the window size
			int[] w = new int[2];
			int[] h = new int[2];
			glfwGetWindowSize(mMasterWindowId, w, h);
			pDesiredSettings.windowWidth = w[0];
			pDesiredSettings.windowHeight = h[0];

		}

		int[] w = new int[2];
		int[] h = new int[2];
		glfwGetWindowSize(mMasterWindowId, w, h);
		pDesiredSettings.windowWidth = w[0];
		pDesiredSettings.windowHeight = h[0];

		glfwSetWindowPos(mMasterWindowId, (mDesktopWidth - pDesiredSettings.windowWidth) / 2, (mDesktopHeight - pDesiredSettings.windowHeight) / 2);

		changeResolution(pDesiredSettings.windowWidth, pDesiredSettings.windowHeight);
		glfwSwapInterval(pDesiredSettings.vSyncEnabled ? 1 : 0);
		mDisplaySettings.copy(pDesiredSettings);
		saveConfig();

		Debug.debugManager().logger().i(getClass().getSimpleName(), "  actual: monitor:" + pDesiredSettings.monitorIndex + " (" + pDesiredSettings.windowWidth + "x" + pDesiredSettings.windowHeight + ")");

	}

	public void changeResolution(int pWidth, int pHeight) {
		if (pWidth == 0 || pHeight == 0)
			return;

		if (true) { // enforce x2 pixels

		}

		mWindowResolutionChanged = true;
		mWindowWasResized = true;

		mDisplaySettings.windowWidth = pWidth;
		mDisplaySettings.windowHeight = pHeight;

		DebugStatTagString lResTag = (DebugStatTagString) Debug.debugManager().stats().getTagByID(DebugStats.TAG_ID_RES);

		lResTag.value = String.format("%dx%d", pWidth, pHeight);

		GL11.glViewport(0, 0, pWidth, pHeight);

	}

	/**
	 * This method ensures that the resolution loaded from the INI matches one of the resolutions that the monitor the window is on supports. If not, that the game will resort to the 'default' resolution.
	 */
	private void validateResolution(GameInfo pGameInfo) {
		if (mDisplaySettings.monitorIndex == 0) {
			mDisplaySettings.monitorIndex = glfwGetPrimaryMonitor();
		}

		// If the game is not started in fullscreen mode, then just checked that the window is not larger than the desktop
		if (!mDisplaySettings.fullscreen()) {

			if (mDisplaySettings.windowWidth > mDesktopWidth)
				mDisplaySettings.windowWidth = mDesktopWidth;

			if (mDisplaySettings.windowHeight > mDesktopHeight)
				mDisplaySettings.windowHeight = mDesktopHeight;

			return;
		}

		// otherwise, make sure that a supported resolution is selected.
		GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes(mDisplaySettings.monitorIndex);

		int lLookingForWidth = mDisplaySettings.windowWidth;
		int lLookingForHeight = mDisplaySettings.windowHeight;

		final int COUNT = modes.limit();
		for (int i = 0; i < COUNT; i++) {
			GLFWVidMode lVidMode = modes.get();

			// Ignore resolution entries based on low refresh rates
			if (lVidMode.refreshRate() < 40)
				continue;

			if (lVidMode.width() == lLookingForWidth && lVidMode.height() == lLookingForHeight) {
				return;

			}

		}

		mDisplaySettings.windowWidth = pGameInfo.baseGameResolutionWidth();
		mDisplaySettings.windowHeight = pGameInfo.baseGameResolutionHeight();
		mDisplaySettings.fullScreenIndex = VideoSettings.FULLSCREEN_NO_INDEX;

		Debug.debugManager().logger().w(getClass().getSimpleName(), "Non-standard resolution found (" + lLookingForWidth + "," + lLookingForHeight + ")! Defaulting back to " + mDisplaySettings.windowWidth + "," + mDisplaySettings.windowHeight);

	}

	// --------------------------------------
	// Resize Listeners
	// --------------------------------------

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

	public void setDisplayMouse(boolean pShowMouse) {
		if (pShowMouse) {
			GLFW.glfwSetInputMode(mMasterWindowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);

		} else {
			GLFW.glfwSetInputMode(mMasterWindowId, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);

		}
	}

	/** returns the closest matching Aspect Ratio to the given resolution */
	public int getClosestAR(float pWidth, float pHeight) {
		float lAR = pWidth / pHeight;
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