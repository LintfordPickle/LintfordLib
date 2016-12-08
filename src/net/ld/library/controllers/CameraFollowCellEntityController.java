package net.ld.library.controllers;

import net.ld.library.cellworld.CellWorldEntity;
import net.ld.library.core.camera.ICamera;
import net.ld.library.core.time.GameTime;

public class CameraFollowCellEntityController {

	// -----------------------------------
	// Variables
	// -----------------------------------

	private CellWorldEntity mTarget;
	private ICamera mCamera;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setFollowCharacter(CellWorldEntity pTarget) {
		mTarget = pTarget;
	}

	public void setCamera(ICamera pCamera) {
		mCamera = pCamera;
	}

	// -----------------------------------
	// Constructor
	// -----------------------------------

	public CameraFollowCellEntityController() {

	}

	// -----------------------------------
	// Core-Methods
	// -----------------------------------

	public void initialise(ICamera pCamera, CellWorldEntity pTarget) {
		mCamera = pCamera;
		mTarget = pTarget;

	}

	public void update(GameTime pGameTime) {
		if (mCamera == null)
			return;
		if (mTarget == null)
			return;

		mCamera.setPosition(-mTarget.x, -mTarget.y);

	}

}
