package net.lintford.library.core.box2d.instance;

import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJoint;

import net.lintford.library.core.LintfordCore;

public class Box2dPrismaticInstance extends Box2dJointInstance {

	private static final long serialVersionUID = -5442409523175676125L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float referenceAngle;
	public boolean enableLimit;
	public float lowerAngle;
	public float upperAngle;
	public boolean enableMotor;
	public float motorSpeed;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dPrismaticInstance(int poolUid) {
		super(poolUid);
	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void savePhysics() {
		super.savePhysics();

		if (joint == null)
			return;

		final var lPrisJoint = (PrismaticJoint) joint;

		referenceAngle = lPrisJoint.getReferenceAngle();
		enableLimit = lPrisJoint.isLimitEnabled();
		lowerAngle = lPrisJoint.getLowerLimit();
		upperAngle = lPrisJoint.getUpperLimit();
		enableMotor = lPrisJoint.isMotorEnabled();
		motorSpeed = lPrisJoint.getJointSpeed();
	}

	public void loadPhysics(World box2dWorld) {

	}

	public void unloadPhysics(World box2dWorld) {

	}

	public void update(LintfordCore core) {

	}
}
