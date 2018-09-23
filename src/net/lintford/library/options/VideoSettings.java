package net.lintford.library.options;

import static org.lwjgl.system.MemoryUtil.NULL;

public class VideoSettings {

	// --------------------------------------
	// Constants / Enums
	// --------------------------------------

	public static final int FULLSCREEN_NO_INDEX = 0;
	public static final int FULLSCREEN_YES_INDEX = 1;
	public static final int FULLSCREEN_BORDERLESS_INDEX = 2;

	static final VideoSettings createBasicTemplate() {
		VideoSettings lBasic = new VideoSettings();
		lBasic.monitorIndex = NULL;
		lBasic.windowWidth = 800;
		lBasic.windowHeight = 600;
		lBasic.refreshRate = 60;
		lBasic.resizable = true;
		lBasic.fullScreenIndex = FULLSCREEN_NO_INDEX;
		lBasic.vSyncEnabled = true;
		return lBasic;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public int windowPositionX;
	public int windowPositionY;
	public int windowWidth;
	public int windowHeight;
	public int refreshRate;
	public int fullScreenIndex;
	public int aspectRatioIndex;
	public long monitorIndex;
	public boolean resizable;
	public boolean vSyncEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean fullscreen() {
		return fullScreenIndex == FULLSCREEN_YES_INDEX;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public VideoSettings() {

	}

	public VideoSettings(VideoSettings pCopy) {
		this.copy(pCopy);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void copy(VideoSettings pCopy) {
		this.fullScreenIndex = pCopy.fullScreenIndex;
		this.windowWidth = pCopy.windowWidth;
		this.windowHeight = pCopy.windowHeight;
		this.refreshRate = pCopy.refreshRate;
		this.aspectRatioIndex = pCopy.aspectRatioIndex;
		this.monitorIndex = pCopy.monitorIndex;
		this.vSyncEnabled = pCopy.vSyncEnabled;
	}

	public boolean isDifferent(VideoSettings pOther) {
		if (pOther == null)
			return true;

		return fullScreenIndex != pOther.fullScreenIndex || windowWidth != pOther.windowWidth || windowHeight != pOther.windowHeight || refreshRate != pOther.refreshRate || aspectRatioIndex != pOther.aspectRatioIndex
				|| monitorIndex != pOther.monitorIndex || vSyncEnabled != pOther.vSyncEnabled;

	}
}
