package net.lintfordlib.core.camera;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.stats.DebugStatTagCaption;
import net.lintfordlib.core.debug.stats.DebugStatTagFloat;
import net.lintfordlib.core.debug.stats.DebugStatTagString;

public class CameraDebugStatsTracker {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private ICamera mCamera;

	private DebugStatTagCaption mCameraCaption;
	private DebugStatTagString mCameraPosition;
	private DebugStatTagString mCameraDimensions;
	private DebugStatTagFloat mCameraZoomFactor;
	private DebugStatTagString mMouseCordsInCamSpace;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public CameraDebugStatsTracker(ICamera cameraToTrack) {
		mCamera = cameraToTrack;

		// register the tags to be tracked
		final var lDebugStats = Debug.debugManager().stats();
		if (lDebugStats != null) {

			mCameraCaption = new DebugStatTagCaption(mCamera.getClass().getSimpleName());
			mCameraPosition = new DebugStatTagString("Position");
			mCameraDimensions = new DebugStatTagString("Size");
			mMouseCordsInCamSpace = new DebugStatTagString("Mouse");
			mCameraZoomFactor = new DebugStatTagFloat("Zoom Factor", 1.f);

			lDebugStats.addCustomStatTag(mCameraCaption);
			lDebugStats.addCustomStatTag(mCameraPosition);
			lDebugStats.addCustomStatTag(mCameraDimensions);
			lDebugStats.addCustomStatTag(mCameraZoomFactor);
			lDebugStats.addCustomStatTag(mMouseCordsInCamSpace);
		}
	}

	public void update(LintfordCore core) {
		final var lCameraPosition = mCamera.getPosition();
		final float lMouseX = mCamera.getMouseWorldSpaceX();
		final float lMouseY = mCamera.getMouseWorldSpaceY();

		mMouseCordsInCamSpace.setValue("x:" + roundFloatToTwoDps(lMouseX) + " y:" + roundFloatToTwoDps(lMouseY));
		mCameraPosition.setValue("x:" + roundFloatToTwoDps(lCameraPosition.x) + " y:" + roundFloatToTwoDps(lCameraPosition.y));
		mCameraDimensions.setValue((int) mCamera.getWidth() + "x" + (int) mCamera.getHeight());
		mCameraZoomFactor.setValue(mCamera.getZoomFactor());
	}

	private float roundFloatToTwoDps(float v) {
		return Math.round(v * 100.f) / 100.f;
	}

}
