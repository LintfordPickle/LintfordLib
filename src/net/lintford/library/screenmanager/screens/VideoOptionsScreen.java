package net.lintford.library.screenmanager.screens;

import static org.lwjgl.glfw.GLFW.GLFW_DECORATED;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.options.DisplayConfig;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.BUTTON_SIZE;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.dialogs.ConfirmationDialog;
import net.lintford.library.screenmanager.dialogs.TimedConfirmationDialog;
import net.lintford.library.screenmanager.dialogs.TimedDialogInterface;
import net.lintford.library.screenmanager.entries.EntryInteractions;
import net.lintford.library.screenmanager.entries.HorizontalEntryGroup;
import net.lintford.library.screenmanager.entries.MenuDropDownEntry;
import net.lintford.library.screenmanager.entries.MenuEnumEntryIndexed;
import net.lintford.library.screenmanager.entries.MenuLabelEntry;
import net.lintford.library.screenmanager.entries.MenuToggleEntry;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;

// TODO: Monitor and Aspect Ratio are only considered in fullscreen mode
// TODO: Need to add a 15 second cooldown when applying settings for the first time
public class VideoOptionsScreen extends MenuScreen implements EntryInteractions, TimedDialogInterface {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_APPLY_CHANGES = 0;
	public static final int BUTTON_CANCEL_CHANGES = 1;

	public static final int BUTTON_MONITOR = 10;
	public static final int BUTTON_RESOLUTION = 11;
	public static final int BUTTON_VSYNC = 12;
	public static final int BUTTON_FULLSCREEN = 13;
	public static final int BUTTON_ASPECTRATIO = 14;

	private static final int ASPECTRATIO_1 = 0; // 16:9
	private static final int ASPECTRATIO_2 = 1; // 4:3

	private static final String FULLSCREEN_YES = "Yes";
	private static final String FULLSCREEN_NO = "No";
	// private static final String FULLSCREEN_NO_MAX = "Window Borderless";

	private static final int FULLSCREEN_YES_INDEX = 0;
	private static final int FULLSCREEN_NO_INDEX = 1;
	private static final int FULLSCREEN_NO_MAX_INDEX = 2;

	public class VideoOptionsConfig {
		public int fullScreenIndex;
		public int windowWidth;
		public int windowHeight;
		public int aspectRatioIndex;
		public long monitorHandle;
		public boolean vSyncEnabled;

		public VideoOptionsConfig() {

		}

		public VideoOptionsConfig(VideoOptionsConfig pCopy) {
			this.copy(pCopy);

		}

		public void copy(VideoOptionsConfig pCopy) {
			this.fullScreenIndex = pCopy.fullScreenIndex;
			this.windowWidth = pCopy.windowWidth;
			this.windowHeight = pCopy.windowHeight;
			this.aspectRatioIndex = pCopy.aspectRatioIndex;
			this.monitorHandle = pCopy.monitorHandle;
			this.vSyncEnabled = pCopy.vSyncEnabled;
		}

		public boolean isDifferent(VideoOptionsConfig pOther) {
			if (pOther == null)
				return true;

			return fullScreenIndex != pOther.fullScreenIndex || windowWidth != pOther.windowWidth || windowHeight != pOther.windowHeight || aspectRatioIndex != pOther.aspectRatioIndex || monitorHandle != pOther.monitorHandle
					|| vSyncEnabled != pOther.vSyncEnabled;

		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayConfig mDisplayConfig;

	private ConfirmationDialog mConfirmationDialog;
	private TimedConfirmationDialog m15SecConfirmationDialog;

	private MenuToggleEntry mVSync;

	private MenuEnumEntryIndexed<Integer> mFullScreenEntry;
	private MenuEnumEntryIndexed<Integer> mAspectRatio;
	private MenuEnumEntryIndexed<Long> mMonitorEntry;
	private MenuDropDownEntry<GLFWVidMode> mResolutionEntry;

	// Quality settings
	private MenuLabelEntry mChangesPendingWarning;

	private VideoOptionsConfig modifiedVideoConfig;
	private VideoOptionsConfig currentVideoConfig;
	private VideoOptionsConfig lastVideoConfig; // last known working config, in case we need to revert

	private ListLayout mVideoList;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public VideoOptionsScreen(ScreenManager pScreenManager) {
		super(pScreenManager, "VIDEO SETTINGS");

		// Get the config options
		mDisplayConfig = pScreenManager.core().config().display();

		mChildAlignment = ALIGNMENT.center;

		mVideoList = new ListLayout(this);
		mVideoList.setDrawBackground(true, 0f, 0f, 0f, 0.75f);
		mVideoList.setPadding(mVideoList.paddingTop(), mVideoList.paddingLeft(), mVideoList.paddingRight(), 25f);
		mVideoList.forceHeight(400);

		ListLayout lComfirmChangesMessageList = new ListLayout(this);
		ListLayout lNavList = new ListLayout(this);

		createVideoSection(mVideoList);

		currentVideoConfig = new VideoOptionsConfig();
		reflectCurrentStateInSettings(currentVideoConfig);

		lastVideoConfig = new VideoOptionsConfig(currentVideoConfig);
		modifiedVideoConfig = new VideoOptionsConfig(currentVideoConfig);

		mChangesPendingWarning = new MenuLabelEntry(mScreenManager, this);
		mChangesPendingWarning.label("Current changes have not yet been applied!");
		mChangesPendingWarning.labelColor(0.86f, 0.22f, 0.37f);
		mChangesPendingWarning.enabled(false);

		lComfirmChangesMessageList.menuEntries().add(mChangesPendingWarning);

		/* Screen control buttons */
		HorizontalEntryGroup lGroup = new HorizontalEntryGroup(pScreenManager, this);

		MenuEntry lButton1 = new MenuEntry(pScreenManager, this, "Back");
		lButton1.buttonSize(BUTTON_SIZE.narrow);
		lButton1.registerClickListener(this, BUTTON_CANCEL_CHANGES);
		MenuEntry lButton2 = new MenuEntry(pScreenManager, this, "Apply");
		lButton2.buttonSize(BUTTON_SIZE.narrow);
		lButton2.registerClickListener(this, BUTTON_APPLY_CHANGES);

		lGroup.addEntry(lButton1);
		lGroup.addEntry(lButton2);

		lNavList.menuEntries().add(lGroup);

		// Add the layouts to the screen
		layouts().add(mVideoList);
		layouts().add(lComfirmChangesMessageList);
		layouts().add(lNavList);

	}

	private void createVideoSection(BaseLayout lLayout) {
		mFullScreenEntry = new MenuEnumEntryIndexed<>(mScreenManager, this, "Fullscreen");
		mResolutionEntry = new MenuDropDownEntry<>(mScreenManager, this, "Resolution");
		mMonitorEntry = new MenuEnumEntryIndexed<>(mScreenManager, this, "Monitor");
		mAspectRatio = new MenuEnumEntryIndexed<>(mScreenManager, this, "Aspect Ratio");
		mVSync = new MenuToggleEntry(mScreenManager, this);

		// Setup buttons
		mFullScreenEntry.addItem(mFullScreenEntry.new MenuEnumEntryItem(FULLSCREEN_YES, FULLSCREEN_YES_INDEX));
		mFullScreenEntry.addItem(mFullScreenEntry.new MenuEnumEntryItem(FULLSCREEN_NO, FULLSCREEN_NO_INDEX));
		// mFullScreenEntry.addItem(mFullScreenEntry.new MenuEnumEntryItem(FULLSCREEN_NO_MAX, FULLSCREEN_NO_MAX_INDEX));
		mFullScreenEntry.setButtonsEnabled(true);
		mVSync.label("V-Sync");

		mAspectRatio.addItem(mAspectRatio.new MenuEnumEntryItem("16:9", ASPECTRATIO_1));
		mAspectRatio.addItem(mAspectRatio.new MenuEnumEntryItem("4:3", ASPECTRATIO_2));
		mAspectRatio.setSelectedEntry(0); // default to 16:9

		// Register listeners with this window
		mFullScreenEntry.registerClickListener(this, BUTTON_FULLSCREEN);
		mResolutionEntry.registerClickListener(this, BUTTON_RESOLUTION);
		mMonitorEntry.registerClickListener(this, BUTTON_MONITOR);
		mVSync.registerClickListener(this, BUTTON_VSYNC);
		mAspectRatio.registerClickListener(this, BUTTON_ASPECTRATIO);

		// TODO: Add ToolTips for all menu options

		// Add the menu entries to the window
		lLayout.menuEntries().add(mFullScreenEntry);
		lLayout.menuEntries().add(mMonitorEntry);
		lLayout.menuEntries().add(mAspectRatio);
		lLayout.menuEntries().add(mResolutionEntry);
		lLayout.menuEntries().add(mVSync);

		fillMonitorEntry(mMonitorEntry);
		fillResolutions(mResolutionEntry, GLFW.glfwGetPrimaryMonitor(), 16, 9);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		mChangesPendingWarning.enabled(modifiedVideoConfig.isDifferent(currentVideoConfig));

		float lWindowHeight = pCore.config().display().windowSize().y;
		mVideoList.forceHeight(lWindowHeight / 2);

	}

	@Override
	public void exitScreen() {
		// If the current settings are dirty then show a dialog to ask for confirmation (of losing changes) before leaving
		if (modifiedVideoConfig.isDifferent(currentVideoConfig)) {
			mConfirmationDialog = new ConfirmationDialog(mScreenManager, this, "You have some changes which have not\nbeen applied, are you sure you want to\nexit?");

			mConfirmationDialog.confirmEntry().entryText("Okay");
			mConfirmationDialog.confirmEntry().registerClickListener(this, ConfirmationDialog.BUTTON_CONFIRM_YES);

			mConfirmationDialog.cancelEntry().entryText("Cancel");
			mConfirmationDialog.cancelEntry().registerClickListener(this, ConfirmationDialog.BUTTON_CONFIRM_NO);

			mScreenManager.addScreen(mConfirmationDialog);

		} else {
			super.exitScreen();

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {

		case BUTTON_CANCEL_CHANGES:
			exitScreen();
			break;

		case BUTTON_APPLY_CHANGES:
			applyModifiedSettings();

			// if no changes, then quit out
			if (currentVideoConfig.equals(modifiedVideoConfig)) {
				exitScreen(); // shows

			}
			break;

		case ConfirmationDialog.BUTTON_CONFIRM_YES: // exit without saving
			if (mConfirmationDialog != null)
				mScreenManager.removeScreen(mConfirmationDialog);

			modifiedVideoConfig.copy(currentVideoConfig);

			exitScreen();
			break;

		case ConfirmationDialog.BUTTON_CONFIRM_NO: // go back and dont exit yet
			if (mConfirmationDialog != null)
				mScreenManager.removeScreen(mConfirmationDialog);

			break;

		case TimedConfirmationDialog.BUTTON_TIMED_CONFIRM_YES: // Revert to the last config
			if (m15SecConfirmationDialog != null)
				mScreenManager.removeScreen(m15SecConfirmationDialog);

			currentVideoConfig.copy(modifiedVideoConfig);

			// exitScreen();

			break;

		case TimedConfirmationDialog.BUTTON_TIMED_CONFIRM_NO: // Revert to the last config
			if (m15SecConfirmationDialog != null)
				mScreenManager.removeScreen(m15SecConfirmationDialog);

			revertSettings();

			break;

		}

	}

	@Override
	public void menuEntryChanged(MenuEntry e) {
		switch (e.entryID()) {
		case BUTTON_FULLSCREEN:
			modifiedVideoConfig.fullScreenIndex = mFullScreenEntry.selectedItem().value;
			mMonitorEntry.enabled(modifiedVideoConfig.fullScreenIndex == FULLSCREEN_YES_INDEX);
			break;

		case BUTTON_ASPECTRATIO:
			modifiedVideoConfig.aspectRatioIndex = mAspectRatio.selectedItem().value;
			long lWindowSelected = mMonitorEntry.selectedItem().value;
			if (mAspectRatio.selectedItem().value == ASPECTRATIO_1) {
				// 16:9
				fillResolutions(mResolutionEntry, lWindowSelected, 16, 9);
			} else {
				// 4:3
				fillResolutions(mResolutionEntry, lWindowSelected, 4, 3);
			}

			modifiedVideoConfig.windowWidth = mResolutionEntry.selectedItem().value.width();
			modifiedVideoConfig.windowHeight = mResolutionEntry.selectedItem().value.height();

			break;

		case BUTTON_VSYNC:
			modifiedVideoConfig.vSyncEnabled = mVSync.isChecked();
			break;

		case BUTTON_MONITOR:
			modifiedVideoConfig.monitorHandle = mMonitorEntry.selectedItem().value;

			long lSelectMonitorHandle = mMonitorEntry.selectedItem().value;
			if (mAspectRatio.selectedItem().value == ASPECTRATIO_1) {
				// 16:9
				fillResolutions(mResolutionEntry, lSelectMonitorHandle, 16, 9);

			} else {
				// 4:3
				fillResolutions(mResolutionEntry, lSelectMonitorHandle, 4, 3);

			}
			break;

		case BUTTON_RESOLUTION:
			modifiedVideoConfig.windowWidth = mResolutionEntry.selectedItem().value.width();
			modifiedVideoConfig.windowHeight = mResolutionEntry.selectedItem().value.height();
			break;

		}
	}

	private void reflectCurrentStateInSettings(VideoOptionsConfig pVideoConfig) {
		long lMonitorHandle = GLFW.glfwGetPrimaryMonitor();
		boolean lIsFullScreen = mDisplayConfig.fullscreen();

		pVideoConfig.monitorHandle = lMonitorHandle;
		pVideoConfig.fullScreenIndex = mDisplayConfig.fullscreen() ? FULLSCREEN_YES_INDEX : FULLSCREEN_NO_INDEX;
		pVideoConfig.vSyncEnabled = mDisplayConfig.vsyncEnabled();
		pVideoConfig.windowWidth = mDisplayConfig.windowSize().x;
		pVideoConfig.windowHeight = mDisplayConfig.windowSize().y;

		// Setup the values for the menuEntries on this screen
		if (lIsFullScreen) {
			mFullScreenEntry.setSelectedEntry(FULLSCREEN_YES_INDEX);
			mMonitorEntry.enabled(true);

		} else {
			mFullScreenEntry.setSelectedEntry(FULLSCREEN_NO_INDEX);
			mMonitorEntry.enabled(false);

		}

		int lWindowWidth = mDisplayConfig.windowSize().x;
		int lWindowHeight = mDisplayConfig.windowSize().y;

		if (lWindowWidth > 0 && (float) lWindowHeight / (float) lWindowWidth == 0.75f) {
			mAspectRatio.setSelectedEntry(ASPECTRATIO_2); // 4:3
		} else {
			mAspectRatio.setSelectedEntry(ASPECTRATIO_1); // 16:9
		}
		pVideoConfig.aspectRatioIndex = mAspectRatio.selectedItem().value;

		mVSync.isChecked(pVideoConfig.vSyncEnabled);

		setResolutionEntry(pVideoConfig.windowWidth, pVideoConfig.windowHeight, 60);

		// update the modified structure too (currently the same if we just set the UI elements).
		modifiedVideoConfig = new VideoOptionsConfig(pVideoConfig);

	}

	private void revertSettings() {
		boolean lFullScreen = lastVideoConfig.fullScreenIndex == FULLSCREEN_YES_INDEX;
		boolean lVSyncEnabled = lastVideoConfig.vSyncEnabled;
		long lMonitorHandle = lastVideoConfig.monitorHandle;

		if (lFullScreen) {
			// Update the initial values
			int lWidth = lastVideoConfig.windowWidth;
			int lHeight = lastVideoConfig.windowHeight;

			// Set full screen mode
			mDisplayConfig.setGLFWMonitor(lMonitorHandle, 0, 0, lWidth, lHeight, lVSyncEnabled);

		} else {
			// fullscreen borderless or just windowed?
			boolean lFullBorderless = lastVideoConfig.fullScreenIndex == FULLSCREEN_NO_MAX_INDEX;
			if (lFullBorderless) {
				glfwWindowHint(GLFW_DECORATED, GL_FALSE);
				GLFWVidMode lDesktop = mDisplayConfig.desktopVideoMode();
				if (lDesktop == null) {
					lDesktop = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
				}

				// Set a windowd mode, using top-left pos
				mDisplayConfig.setGLFWMonitor(NULL, 0, 0, lDesktop.width(), lDesktop.height(), lVSyncEnabled);

			} else {
				glfwWindowHint(GLFW_DECORATED, GL_FALSE);
				// windowed
				int lWidth = lastVideoConfig.windowWidth;
				int lHeight = lastVideoConfig.windowHeight;

				// Set a windowd mode, using top-left pos
				mDisplayConfig.setGLFWMonitor(NULL, 0, 0, lWidth, lHeight, lVSyncEnabled);

			}
		}

		currentVideoConfig.copy(lastVideoConfig);
		modifiedVideoConfig.copy(currentVideoConfig);

		reflectCurrentStateInSettings(currentVideoConfig);

	}

	// TODO: I think there is too much DISPLAY logic taking place in here. This is the UI ...
	private void applyModifiedSettings() {
		boolean lFullScreen = modifiedVideoConfig.fullScreenIndex == FULLSCREEN_YES_INDEX;
		boolean lVSyncEnabled = modifiedVideoConfig.vSyncEnabled;
		long lMonitorHandle = modifiedVideoConfig.monitorHandle;

		if (lFullScreen) {
			// Update the initial values
			int lWidth = modifiedVideoConfig.windowWidth;
			int lHeight = modifiedVideoConfig.windowHeight;

			// Set full screen mode
			mDisplayConfig.setGLFWMonitor(lMonitorHandle, 0, 0, lWidth, lHeight, lVSyncEnabled);

		} else {
			// fullscreen borderless or just windowed?
			boolean lFullBorderless = modifiedVideoConfig.fullScreenIndex == FULLSCREEN_NO_MAX_INDEX;
			if (lFullBorderless) {
				glfwWindowHint(GLFW_DECORATED, GL_FALSE);
				GLFWVidMode lDesktop = mDisplayConfig.desktopVideoMode();
				if (lDesktop == null) {
					lDesktop = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
				}

				// Set a windowd mode, using top-left pos
				mDisplayConfig.setGLFWMonitor(NULL, 0, 0, lDesktop.width(), lDesktop.height(), lVSyncEnabled);

			} else {
				glfwWindowHint(GLFW_DECORATED, GL_FALSE);
				// windowed
				int lWidth = modifiedVideoConfig.windowWidth;
				int lHeight = modifiedVideoConfig.windowHeight;

				// Set a windowd mode, using top-left pos
				mDisplayConfig.setGLFWMonitor(NULL, 0, 0, lWidth, lHeight, lVSyncEnabled);

			}

		}

		// TODO: In case anything goes wrong, in 15 seconds, revert to last ..
		lastVideoConfig.copy(currentVideoConfig);

		// Add a timed confirmation dialog to the
		m15SecConfirmationDialog = new TimedConfirmationDialog(mScreenManager, this, "Confirm changes?");

		m15SecConfirmationDialog.confirmEntry().entryText("Keep Settings");
		// m15SecConfirmationDialog.confirmEntry().registerClickListener(this, TimedConfirmationDialog.BUTTON_TIMED_CONFIRM_YES);

		m15SecConfirmationDialog.cancelEntry().entryText("Revert");
		// m15SecConfirmationDialog.cancelEntry().registerClickListener(this, TimedConfirmationDialog.BUTTON_TIMED_CONFIRM_NO);

		m15SecConfirmationDialog.setListener(this);

		m15SecConfirmationDialog.start(10000);

		mScreenManager.addScreen(m15SecConfirmationDialog);

	}

	private void fillMonitorEntry(MenuEnumEntryIndexed<Long> pEntry) {
		PointerBuffer lMonitorList = GLFW.glfwGetMonitors();

		final int COUNT = lMonitorList.limit();
		for (int i = 0; i < COUNT; i++) {
			long lMonitorHandle = lMonitorList.get();
			String lMonitorFullName = (i == 0 ? "[Primary]" : "") + GLFW.glfwGetMonitorName(lMonitorHandle);
			String lMonitorName = lMonitorFullName.substring(0, Math.min(lMonitorFullName.length(), 25));
			MenuEnumEntryIndexed<Long>.MenuEnumEntryItem lTest = pEntry.new MenuEnumEntryItem(lMonitorName, lMonitorHandle);

			mMonitorEntry.addItem(lTest);

		}

	}

	private void fillResolutions(MenuDropDownEntry<GLFWVidMode> pEntry, long pWindowHandle, float w, float h) {
		pEntry.clearItems();

		float lARResult = w / h;

		GLFWVidMode.Buffer modes = GLFW.glfwGetVideoModes(pWindowHandle);
		final int COUNT = modes.limit();
		for (int i = 0; i < COUNT; i++) {
			GLFWVidMode lVidMode = modes.get();

			// Ignore resolution entries based on low refresh rates
			if (lVidMode.refreshRate() < 40)
				continue;

			String lName = lVidMode.width() + "x" + lVidMode.height(); // + "@" + lVidMode.refreshRate();

			float lResult = ((float) lVidMode.width() / (float) lVidMode.height());

			// Only add resolutions which meet our minimum and the selected aspect ratio
			if (lResult == lARResult && lVidMode.width() > DisplayConfig.WINDOW_MINIMUM_WIDTH && lVidMode.height() > DisplayConfig.WINDOW_MINIMUM_HEIGHT) {
				MenuDropDownEntry<GLFWVidMode>.MenuEnumEntryItem lTest = pEntry.new MenuEnumEntryItem(lName, lVidMode);
				pEntry.addItem(lTest);

			}

		}

	}

	private void setResolutionEntry(int pWidth, int pHeight, int pRefresh) {
		final int COUNT = mResolutionEntry.items().size();
		GLFWVidMode lBestFit = null;
		for (int i = 0; i < COUNT; i++) {
			GLFWVidMode lMode = mResolutionEntry.items().get(i).value;
			int lW = lMode.width();
			int lH = lMode.height();
			if (lW == pWidth && lH == pHeight) {
				if (lMode.refreshRate() == pRefresh) {
					mResolutionEntry.setSelectEntry(lMode);
					return;
				} else {
					lBestFit = lMode;
				}
			}
		}

		// Worst case use best fit
		if (lBestFit != null)
			mResolutionEntry.setSelectEntry(lBestFit);

	}

	// --------------------------------------
	// Callback Methods
	// --------------------------------------

	@Override
	public void timeExpired() {
		if (m15SecConfirmationDialog != null)
			mScreenManager.removeScreen(m15SecConfirmationDialog);

		revertSettings();

	}

	@Override
	public void confirmation() {
		if (m15SecConfirmationDialog != null)
			mScreenManager.removeScreen(m15SecConfirmationDialog);

		currentVideoConfig.copy(modifiedVideoConfig);
		exitScreen();

	}

	@Override
	public void decline() {
		if (m15SecConfirmationDialog != null)
			mScreenManager.removeScreen(m15SecConfirmationDialog);

		modifiedVideoConfig.copy(lastVideoConfig);
		applyModifiedSettings();

	}

}