package net.lintford.library.controllers.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;

public class DebugControllerTreeController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "ControllerTreeController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected List<BaseControllerWidget> mDebugTreeComponents;
	protected int mCountAtLastUpdate = -1;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<BaseControllerWidget> treeComponents() {
		return mDebugTreeComponents;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugControllerTreeController(ControllerManager controllerManager, int controllerGroup) {
		super(controllerManager, CONTROLLER_NAME, controllerGroup);

		mDebugTreeComponents = new ArrayList<BaseControllerWidget>();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void unload() {
		if (mDebugTreeComponents != null) {
			mDebugTreeComponents.clear();
			mDebugTreeComponents = null;

		}

	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lControllers = mControllerManager.allControllers();

		maintainControllerWidgetList(lControllers);

		final var lControllerWidgetCount = mDebugTreeComponents.size();
		for (int i = 0; i < lControllerWidgetCount; i++) {
			final var lWidget = mDebugTreeComponents.get(i);

			if (lWidget.baseController == null) {
				// FIXME: Remove the ControllerWidget from the list, the BaseController attached has been destroyed.
			}
		}
	}

	// makes sure that every controller gets its own rendering widget
	private void maintainControllerWidgetList(final Map<Integer, List<BaseController>> controllers) {
		int lPositionCounter = 0;
		for (final var lEntry : controllers.entrySet()) {
			final var lControllerManager = lEntry.getValue();

			if (!debugTreeContainsControllerId(lEntry.getKey())) {
				addBaseControllerToDebugTree(lEntry.getKey(), lPositionCounter, 0);
				lPositionCounter++;
			}

			final var lNumChildControllers = lControllerManager.size();
			for (var j = 0; j < lNumChildControllers; j++) {
				final var lBaseController = lControllerManager.get(j);
				if (!debugTreeContainsControllerId(lBaseController.controllerId()))
					addBaseControllerToDebugTree(lBaseController, lPositionCounter, 1);

				lPositionCounter++;
			}
		}
	}

	private boolean debugTreeContainsControllerId(int controllerId) {
		final int lNumBaseControllerAreas = mDebugTreeComponents.size();
		for (var i = 0; i < lNumBaseControllerAreas; i++) {
			if (mDebugTreeComponents.get(i).controllerId == controllerId)
				return true;
		}

		return false;
	}

	private void addBaseControllerToDebugTree(BaseController controller, int atIndex, int indentation) {
		if (controller == null)
			return;

		final var lNewDebugArea = new BaseControllerWidget();
		lNewDebugArea.controllerId = controller.controllerId();
		lNewDebugArea.baseController = controller;
		lNewDebugArea.displayName = controller.controllerName();
		lNewDebugArea.isExpanded = false;
		lNewDebugArea.controllerLevel = indentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	private void addBaseControllerToDebugTree(int controllerId, int atIndex, int indentation) {
		final var lNewDebugArea = new BaseControllerWidget();
		lNewDebugArea.displayName = Integer.toString(controllerId);
		lNewDebugArea.controllerId = controllerId;
		lNewDebugArea.baseController = null;
		lNewDebugArea.isExpanded = false;
		lNewDebugArea.controllerLevel = indentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addDebugComponent() {

	}
}
