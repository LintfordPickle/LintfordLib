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

	@Override
	public boolean isinitialized() {
		return false;
	}

	public List<BaseControllerWidget> treeComponents() {
		return mDebugTreeComponents;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugControllerTreeController(ControllerManager pControllerManager, int pControllerGroup) {
		super(pControllerManager, CONTROLLER_NAME, pControllerGroup);

		mDebugTreeComponents = new ArrayList<BaseControllerWidget>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void unload() {
		if (mDebugTreeComponents != null) {
			mDebugTreeComponents.clear();
			mDebugTreeComponents = null;

		}

	}

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

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
	private void maintainControllerWidgetList(final Map<Integer, List<BaseController>> lControllers) {

//		final int lControllerCount = lControllers.size();
//		if (mCountAtLastUpdate == lControllerCount)
//			return;
//
//		mCountAtLastUpdate = lControllerCount;

		// Check for updates to the structure of the Controller Manager:
		// 1.. Check the parent containers
		int lPositionCounter = 0;
		for (final var lEntry : lControllers.entrySet()) {
			final var lControllerManager = lEntry.getValue();

			// ---> Assigned the ControllerManagerId
			if (!debugTreeContainsControllerId(lEntry.getKey())) {
				addBaseControllerToDebugTree(lEntry.getKey(), lPositionCounter, 0);
				lPositionCounter++;
			}

			// 2.. Check the children containers
			final var lNumChildControllers = lControllerManager.size();
			for (var j = 0; j < lNumChildControllers; j++) {
				final var lBaseController = lControllerManager.get(j);
				if (!debugTreeContainsControllerId(lBaseController.controllerId())) {
					addBaseControllerToDebugTree(lBaseController, lPositionCounter, 1);

				}

				lPositionCounter++;

			}

		}
	}

	private boolean debugTreeContainsControllerId(int pControllerId) {
		final int lNumBaseControllerAreas = mDebugTreeComponents.size();
		for (var i = 0; i < lNumBaseControllerAreas; i++) {
			if (mDebugTreeComponents.get(i).controllerId == pControllerId)
				return true;
		}
		return false;

	}

	private void addBaseControllerToDebugTree(BaseController pController, int pAtIndex, int pIndentation) {
		if (pController == null)
			return;

		final var lNewDebugArea = new BaseControllerWidget();
		lNewDebugArea.controllerId = pController.controllerId();
		lNewDebugArea.baseController = pController;
		lNewDebugArea.displayName = pController.controllerName();
		lNewDebugArea.isExpanded = false;
		lNewDebugArea.controllerLevel = pIndentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	private void addBaseControllerToDebugTree(int pControllerId, int pAtIndex, int pIndentation) {
		final var lNewDebugArea = new BaseControllerWidget();
		lNewDebugArea.displayName = Integer.toString(pControllerId);
		lNewDebugArea.controllerId = pControllerId;
		lNewDebugArea.baseController = null;
		lNewDebugArea.isExpanded = false;
		lNewDebugArea.controllerLevel = pIndentation;

		mDebugTreeComponents.add(lNewDebugArea);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void addDebugComponent() {

	}

}
