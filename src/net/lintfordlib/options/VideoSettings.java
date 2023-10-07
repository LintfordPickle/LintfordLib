package net.lintfordlib.options;

import static org.lwjgl.system.MemoryUtil.NULL;

public class VideoSettings {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	public static final int FULLSCREEN_NO_INDEX = 0;
	public static final int FULLSCREEN_YES_INDEX = 1;
	public static final int FULLSCREEN_BORDERLESS_INDEX = 2;

	static final VideoSettings createBasicTemplate() {
		final var lBasic = new VideoSettings();
		lBasic.mMonitorIndex = NULL;
		lBasic.mWindowWidth = 800;
		lBasic.mWindowHeight = 600;
		lBasic.mRefreshRate = 60;
		lBasic.mResizable = true;
		lBasic.mFullScreenIndex = FULLSCREEN_NO_INDEX;
		lBasic.mVSyncEnabled = true;

		return lBasic;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mWindowPositionX;
	private int mWindowPositionY;
	private int mWindowWidth;
	private int mWindowHeight;
	private int mRefreshRate;
	private int mFullScreenIndex;
	private int mAspectRatioIndex;
	private long mMonitorIndex;
	private boolean mResizable;
	private boolean mVSyncEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int windowPositionX() {
		return mWindowPositionX;
	}

	public void windowPositionX(int windowPositionX) {
		mWindowPositionX = windowPositionX;
	}

	public int windowPositionY() {
		return mWindowPositionY;
	}

	public void windowPositionY(int windowPositionY) {
		mWindowPositionY = windowPositionY;
	}

	public int windowWidth() {
		return mWindowWidth;
	}

	public void windowWidth(int windowWidth) {
		mWindowWidth = windowWidth;
	}

	public int windowHeight() {
		return mWindowHeight;
	}

	public void windowHeight(int windowHeight) {
		mWindowHeight = windowHeight;
	}

	public int refreshRate() {
		return mRefreshRate;
	}

	public void refreshRate(int refreshRate) {
		mRefreshRate = refreshRate;
	}

	public int fullScreenIndex() {
		return mFullScreenIndex;
	}

	public void fullScreenIndex(int fullscreenIndex) {
		mFullScreenIndex = fullscreenIndex;
	}

	public int aspectRatioIndex() {
		return mAspectRatioIndex;
	}

	public void aspectRatioIndex(int aspectRatioIndex) {
		mAspectRatioIndex = aspectRatioIndex;
	}

	public long monitorIndex() {
		return mMonitorIndex;
	}

	public void monitorIndex(long monitorIndex) {
		mMonitorIndex = monitorIndex;
	}

	public boolean resizeable() {
		return mResizable;
	}

	public void resizeable(boolean isResizable) {
		mResizable = isResizable;
	}

	public boolean vSyncEnabled() {
		return mVSyncEnabled;
	}

	public void vSyncEnabled(boolean vSyncEnabled) {
		mVSyncEnabled = vSyncEnabled;
	}

	public boolean fullscreen() {
		return mFullScreenIndex == FULLSCREEN_YES_INDEX;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public VideoSettings() {

	}

	public VideoSettings(VideoSettings videoSettingsToCopy) {
		this.copy(videoSettingsToCopy);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void copy(VideoSettings videoSettingsToCopy) {
		mFullScreenIndex = videoSettingsToCopy.fullScreenIndex();
		mWindowWidth = videoSettingsToCopy.windowWidth();
		mWindowHeight = videoSettingsToCopy.windowHeight();
		mRefreshRate = videoSettingsToCopy.refreshRate();
		mAspectRatioIndex = videoSettingsToCopy.aspectRatioIndex();
		mMonitorIndex = videoSettingsToCopy.monitorIndex();
		mVSyncEnabled = videoSettingsToCopy.vSyncEnabled();
	}

	public boolean isDifferent(VideoSettings videoSettingsToCheckAgainst) {
		if (videoSettingsToCheckAgainst == null)
			return true;

		// @formatter:off
		return mFullScreenIndex != videoSettingsToCheckAgainst.fullScreenIndex() 
				|| mWindowWidth != videoSettingsToCheckAgainst.windowWidth() 
				|| mWindowHeight != videoSettingsToCheckAgainst.windowHeight() 
				|| mRefreshRate != videoSettingsToCheckAgainst.refreshRate()
				|| mAspectRatioIndex != videoSettingsToCheckAgainst.aspectRatioIndex() 
				|| mMonitorIndex != videoSettingsToCheckAgainst.monitorIndex() 
				|| mVSyncEnabled != videoSettingsToCheckAgainst.vSyncEnabled();
		// @formatter:on
	}
}
