package net.lintfordlib.controllers.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;

public class DebugControllerTreeController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "ControllerTreeController";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected final List<BaseControllerWidget> mUpdateTreeList = new ArrayList<>();
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
	public void unloadController() {
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

		mUpdateTreeList.clear();
		mUpdateTreeList.addAll(mDebugTreeComponents);

		final var lControllerWidgetCount = mUpdateTreeList.size();
		for (int i = 0; i < lControllerWidgetCount; i++) {
			final var lWidget = mUpdateTreeList.get(i);

			if (lWidget.baseController == null) {
				mDebugTreeComponents.remove(lWidget);
			}
		}
	}

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
