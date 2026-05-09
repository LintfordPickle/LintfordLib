package net.lintfordlib.controllers;

import net.lintfordlib.screenmanager.toast.ToastManager;

public class ToastController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Toast Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ToastManager mToastManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return mToastManager != null;
	}

	public ToastManager toastManager() {
		return mToastManager;
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ToastController(ControllerManager controllerManager, ToastManager toastManager, int entityGroupID) {
		super(controllerManager, CONTROLLER_NAME, entityGroupID);

		mToastManager = toastManager;

		isActive(false);
	}

}
