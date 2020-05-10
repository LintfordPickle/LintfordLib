package net.lintford.library.core.box2d.instance;

import org.jbox2d.dynamics.World;

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

	public Box2dRevoluteInstance() {

	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void savePhysics() {

	}

	public void loadPhysics(World pWorld) {

	}

	public void unloadPhysics() {

	}

	public void update(LintfordCore pCore) {

	}

}
