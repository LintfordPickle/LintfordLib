package net.lintfordlib.controllers.debug.physics;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.debug.DebugLogger;
import net.lintfordlib.core.debug.stats.DebugStatTagCaption;
import net.lintfordlib.core.debug.stats.DebugStatTagFloat;
import net.lintfordlib.core.debug.stats.DebugStatTagInt;
import net.lintfordlib.core.physics.PhysicsWorld;

/***
 * Attaches debug information from an instance of {@link PhysicsWorld} into the {@link DebugLogger}.
 * 
 * @author John Hampson 2023
 *
 */
public class DebugPhysicsWorldWatcher extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Physics World Debug Watcher";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private PhysicsWorld mPhysicsWorld;

	private DebugStatTagCaption mDebugStatPhysicsCaption;
	private DebugStatTagInt mDebugStatsNumBodies;
	private DebugStatTagFloat mDebugStepTimeInMm;
	private DebugStatTagInt mDebugNumIterations;
	private DebugStatTagInt mNumSpatialCells;
	private DebugStatTagInt mNumActiveCells;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public PhysicsWorld physicsWorld() {
		return mPhysicsWorld;
	}

	/***
	 * Attaches an instance of {@link PhysicsWorld} to this {@link DebugPhysicsWorldWatcher}. The {@link PhysicsWorld} can only be attached once.
	 * 
	 * @param physicsWorld The instance of {@link PhysicsWorld} to attached.
	 */
	public void physicsWorld(PhysicsWorld physicsWorld) {
		if (mPhysicsWorld != null)
			return;

		mPhysicsWorld = physicsWorld;
		if (mDebugStatPhysicsCaption != null)
			mDebugStatPhysicsCaption.setLabel("Physics: ");

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugPhysicsWorldWatcher(ControllerManager controllerManager, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		super.initialize(core);

		if (Debug.debugManager().debugModeEnabled()) {
			mDebugStatPhysicsCaption = new DebugStatTagCaption("Physics");
			if (mPhysicsWorld == null) {
				mDebugStatPhysicsCaption.setLabel("Physics: (World Not Set)");
			} else {
				mDebugStatPhysicsCaption.setLabel("Physics: ");
			}
			mDebugStatsNumBodies = new DebugStatTagInt("Num Bodies", 0, false);
			mDebugStepTimeInMm = new DebugStatTagFloat("step", 0.0f, false);
			mDebugNumIterations = new DebugStatTagInt("Num Iterations", 0, false);
			mNumSpatialCells = new DebugStatTagInt("Num Cells", 0, false);
			mNumActiveCells = new DebugStatTagInt("Active Cells", 0, false);

			Debug.debugManager().stats().addCustomStatTag(mDebugStatPhysicsCaption);
			Debug.debugManager().stats().addCustomStatTag(mDebugStatsNumBodies);
			Debug.debugManager().stats().addCustomStatTag(mDebugStepTimeInMm);
			Debug.debugManager().stats().addCustomStatTag(mDebugNumIterations);
			Debug.debugManager().stats().addCustomStatTag(mNumSpatialCells);
			Debug.debugManager().stats().addCustomStatTag(mNumActiveCells);
		}
	}

	@Override
	public void unloadController() {
		super.unloadController();

		Debug.debugManager().stats().removeCustomStatTag(mDebugStatPhysicsCaption);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStatsNumBodies);
		Debug.debugManager().stats().removeCustomStatTag(mDebugStepTimeInMm);
		Debug.debugManager().stats().removeCustomStatTag(mDebugNumIterations);
		Debug.debugManager().stats().removeCustomStatTag(mNumSpatialCells);
		Debug.debugManager().stats().removeCustomStatTag(mNumActiveCells);

		mDebugStatPhysicsCaption = null;
		mDebugStatsNumBodies = null;
		mDebugStepTimeInMm = null;
		mDebugNumIterations = null;
		mNumSpatialCells = null;
		mNumActiveCells = null;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (Debug.debugManager().debugModeEnabled() == false)
			return;

		if (mPhysicsWorld == null)
			return;

		mDebugNumIterations.setValue(mPhysicsWorld.numIterations());
		mDebugStatsNumBodies.setValue(mPhysicsWorld.numBodies());
		final var lHashgrid = mPhysicsWorld.grid();
		mNumSpatialCells.setValue(lHashgrid.getTotalCellCount());
		mNumActiveCells.setValue(lHashgrid.getActiveCellKeys().size());
		mDebugStepTimeInMm.setValue((float) mPhysicsWorld.stepTime());
	}
}
