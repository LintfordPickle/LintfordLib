package net.lintford.library.core.camera;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.debug.stats.DebugStatTagCaption;
import net.lintford.library.core.debug.stats.DebugStatTagFloat;
import net.lintford.library.core.debug.stats.DebugStatTagString;

public class CameraDebugStatsTracker {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

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
		final var lMousePosition = core.input().mouse().mouseWindowCoords();
		final float lMouseX = mCamera.getWorldPositionXInCameraSpace(lMousePosition.x);
		final float lMouseY = mCamera.getWorldPositionXInCameraSpace(lMousePosition.y);

		mMouseCordsInCamSpace.setValue("x:" + lMouseX + " y:" + lMouseY);
		mCameraPosition.setValue("x:" + lCameraPosition.x + " y:" + lCameraPosition.y);
		mCameraDimensions.setValue((int) mCamera.getWidth() + "x" + (int) mCamera.getHeight());
		mCameraZoomFactor.setValue(mCamera.getZoomFactor());
	}

}
