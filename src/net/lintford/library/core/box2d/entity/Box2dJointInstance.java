package net.lintford.library.core.box2d.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.data.BaseData;

public class Box2dJointInstance extends BaseData {

	private static final long serialVersionUID = -5442409523175676125L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient JointDef jointDef;
	public transient Joint joint;

	public Vec2 localAnchorA = new Vec2();
	public Vec2 localAnchorB = new Vec2();
	public float referenceAngle;
	public boolean enableLimit;
	public float lowerAngle;
	public float upperAngle;
	public boolean enableMotor;
	public float motorSpeed;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dJointInstance() {

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
