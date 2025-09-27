package net.lintfordlib.renderers.editor.panels;

import net.lintfordlib.controllers.editor.EditorBrushController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.EditorBrushRenderer;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButtonToggle;

public class CursorPanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final int BUTTON_TOGGLE_POSITION = 10;
	private static final int BUTTON_TOGGLE_GRID_UID = 12;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private EditorBrushRenderer mEditorBrushRenderer;
	private EditorBrushController mEditorBrushController;

	private UiButtonToggle mShowCursorPosition;
	private UiButtonToggle mShowGridUid;

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

	public CursorPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "File Info Panel", entityGroupUid);

		mShowActiveLayerButton = false;
		mShowShowLayerButton = true;

		mRenderPanelTitle = true;
		mPanelTitle = "Cursor";

		mShowCursorPosition = new UiButtonToggle();
		mShowCursorPosition.setUiWidgetListener(this, BUTTON_TOGGLE_POSITION);
		mShowCursorPosition.buttonLabel("Show Position");

		mShowGridUid = new UiButtonToggle();
		mShowGridUid.setUiWidgetListener(this, BUTTON_TOGGLE_GRID_UID);
		mShowGridUid.buttonLabel("Show Grid Uid");

		addWidget(mShowCursorPosition);
		addWidget(mShowGridUid);

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

		mEditorBrushController = (EditorBrushController) lControllerManager.getControllerByNameRequired(EditorBrushController.CONTROLLER_NAME, mEntityGroupUid);

		mEditorBrushController.showPosition(true);
		mShowCursorPosition.isToggledOn(mEditorBrushController.showPosition());

		final var lRendererManager = mParentWindow.rendererManager();
		mEditorBrushRenderer = (EditorBrushRenderer) lRendererManager.getRenderer(EditorBrushRenderer.RENDERER_NAME);
		if (mEditorBrushRenderer != null)
			mEditorBrushRenderer.renderBrush(isLayerVisible());

	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_TOGGLE_POSITION:
			final var lIsShowPositionOn = mEditorBrushController.showPosition();
			mEditorBrushController.showPosition(!lIsShowPositionOn);

			mShowCursorPosition.isToggledOn(mEditorBrushController.showPosition());
			break;

		case BUTTON_TOGGLE_GRID_UID:
			final var lIsShowGridUid = mEditorBrushController.showGridUid();
			mEditorBrushController.showGridUid(!lIsShowGridUid);
			break;

		case BUTTON_SHOW_LAYER:
			if (mEditorBrushRenderer != null)
				mEditorBrushRenderer.renderBrush(isLayerVisible());
			break;
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {

	}

}