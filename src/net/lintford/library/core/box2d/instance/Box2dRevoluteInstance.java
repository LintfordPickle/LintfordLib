package net.lintford.library.core.box2d.instance;

import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import net.lintford.library.core.LintfordCore;

public class Box2dRevoluteInstance extends Box2dJointInstance {

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
	public float maxMotorTorque;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dRevoluteInstance(int poolUid) {
		super(poolUid);
	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void savePhysics() {
		super.savePhysics();

		if (joint == null)
			return;

		final var lRevJoint = (RevoluteJoint) joint;

		referenceAngle = lRevJoint.getReferenceAngle();
		enableLimit = lRevJoint.isLimitEnabled();
		lowerAngle = lRevJoint.getLowerLimit();
		upperAngle = lRevJoint.getUpperLimit();
		enableMotor = lRevJoint.isMotorEnabled();
		motorSpeed = lRevJoint.getJointSpeed();
		maxMotorTorque = lRevJoint.getMaxMotorTorque();
	}

	public void loadPhysics(World box2dWorld) {

	}

	public void unloadPhysics(World box2dWorld) {

	}

	public void update(LintfordCore core) {

	}
}
