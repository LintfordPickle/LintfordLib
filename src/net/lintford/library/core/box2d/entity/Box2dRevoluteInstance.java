package net.lintford.library.core.box2d.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.data.BaseData;

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

	public void loadPhysics() {

	}

	public void unloadPhysics() {

	}

	public void update(LintfordCore pCore) {

	}

}
