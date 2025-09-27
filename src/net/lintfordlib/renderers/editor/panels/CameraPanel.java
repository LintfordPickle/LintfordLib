package net.lintfordlib.renderers.editor.panels;

import net.lintfordlib.controllers.camera.CameraBoundsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiButtonToggle;

public class CameraPanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int BUTTON_TOGGLE_RESET_POS = 10;
	private static final int BUTTON_TOGGLE_LIMIT_BOUNDS = 12;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private CameraBoundsController mCameraBoundsController;

	private UiButton mResetPositionButton;
	private UiButtonToggle mEnableBoundsButton;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CameraPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "Camera Panel", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = true;

		mRenderPanelTitle = true;
		mPanelTitle = "Camera";

		mResetPositionButton = new UiButton();
		mResetPositionButton.buttonLabel("Reset");
		mResetPositionButton.setUiWidgetListener(this, BUTTON_TOGGLE_RESET_POS);

		mEnableBoundsButton = new UiButtonToggle();
		mEnableBoundsButton.buttonLabel("Limit");
		mEnableBoundsButton.setUiWidgetListener(this, BUTTON_TOGGLE_LIMIT_BOUNDS);

		addWidget(mResetPositionButton);
		addWidget(mEnableBoundsButton);

		isLayerVisible(true);

		mShowShowLayerButton = false;
		mShowActiveLayerButton = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllerManager = core.controllerManager();
		mCameraBoundsController = (CameraBoundsController) lControllerManager.getControllerByNameRequired(CameraBoundsController.CONTROLLER_NAME, mEntityGroupUid);

		mEnableBoundsButton.isToggledOn(mCameraBoundsController.limitBounds());
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_TOGGLE_LIMIT_BOUNDS: {
			final var lBoundsLimited = mEnableBoundsButton.isToggledOn();
			mCameraBoundsController.limitBounds(lBoundsLimited);
			break;
		}

		case BUTTON_TOGGLE_RESET_POS: {
			mCameraBoundsController.resetCameraPosition();
			break;
		}
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {
		switch (entryUid) {

		}
	}

}