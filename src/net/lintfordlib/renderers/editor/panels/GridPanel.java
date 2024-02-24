package net.lintfordlib.renderers.editor.panels;

import net.lintfordlib.controllers.editor.EditorHashGridController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.renderers.editor.EditorHashGridRenderer;
import net.lintfordlib.renderers.windows.UiWindow;
import net.lintfordlib.renderers.windows.components.UiButton;
import net.lintfordlib.renderers.windows.components.UiInputInteger;

public class GridPanel extends UiPanel {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_APPLY = 10;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private UiInputInteger mGridWidth;
	private UiInputInteger mGridHeight;
	private UiInputInteger mGridTilesWide;
	private UiInputInteger mGridTilesHigh;

	private UiButton mApplyButton;

	private EditorHashGridController mHashGridController;
	private EditorHashGridRenderer mEditorHashGridRenderer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int layerOwnerHashCode() {
		return mEditorHashGridRenderer.hashCode();
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GridPanel(UiWindow parentWindow, int entityGroupUid) {
		super(parentWindow, "File Info Panel", entityGroupUid);

		mPanelTitle = "Hash Grid";
		mRenderPanelTitle = true;

		mShowActiveLayerButton = false;

		mGridWidth = new UiInputInteger(parentWindow);
		mGridWidth.label("Grid Width");
		mGridWidth.setMinMax(500, 10000);
		mGridWidth.stepSize(50);

		mGridHeight = new UiInputInteger(parentWindow);
		mGridHeight.label("Grid Height");
		mGridHeight.setMinMax(500, 10000);
		mGridHeight.stepSize(50);

		mGridTilesWide = new UiInputInteger(parentWindow);
		mGridTilesWide.label("Tiles Wide");
		mGridTilesWide.setMinMax(5, 20);

		mGridTilesHigh = new UiInputInteger(parentWindow);
		mGridTilesHigh.label("Tiles High");
		mGridTilesHigh.setMinMax(5, 20);

		mApplyButton = new UiButton(parentWindow);
		mApplyButton.setUiWidgetListener(this, BUTTON_APPLY);
		mApplyButton.buttonLabel("Apply");

		addWidget(mGridWidth);
		addWidget(mGridHeight);
		addWidget(mGridTilesWide);
		addWidget(mGridTilesHigh);
		addWidget(mApplyButton);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		final var lControllMnanager = core.controllerManager();
		mHashGridController = (EditorHashGridController) lControllMnanager.getControllerByNameRequired(EditorHashGridController.CONTROLLER_NAME, mEntityGroupUid);

		{
			final var lHashGrid = mHashGridController.hashGrid();

			mGridWidth.currentValue(lHashGrid.boundaryWidth());
			mGridHeight.currentValue(lHashGrid.boundaryHeight());

			mGridTilesWide.currentValue(lHashGrid.numTilesWide());
			mGridTilesHigh.currentValue(lHashGrid.numTilesHigh());
		}

		mEditorHashGridRenderer = (EditorHashGridRenderer) mParentWindow.rendererManager().getRenderer(EditorHashGridRenderer.RENDERER_NAME);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);
	}

	@Override
	public void draw(LintfordCore core) {
		super.draw(core);
	}

	// --------------------------------------

	@Override
	public void widgetOnClick(InputManager inputManager, int entryUid) {
		switch (entryUid) {
		case BUTTON_APPLY:
			updateHashGridSizes();
			break;

		case BUTTON_SHOW_LAYER:
			if (mEditorHashGridRenderer != null) {
				final var lCurentVisibility = mEditorHashGridRenderer.renderHashGrid();
				mEditorHashGridRenderer.renderHashGrid(!lCurentVisibility);
			}
			break;
		}
	}

	@Override
	public void widgetOnDataChanged(InputManager inputManager, int entryUid) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void updateHashGridSizes() {
		final var lNewWidth = mGridWidth.currentValue();
		final var lNewHeight = mGridHeight.currentValue();
		final var lNewNumTilesWide = mGridTilesWide.currentValue();
		final var lNewNumTilesHigh = mGridTilesHigh.currentValue();

		mHashGridController.resizeGrid(lNewWidth, lNewHeight, lNewNumTilesWide, lNewNumTilesHigh);
	}
}