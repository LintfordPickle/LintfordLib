package net.lintford.library.core.box2d.definition;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class Box2dPrismaticJointDef extends Box2dJointDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public RevoluteJointDef revoluteJointDef = new RevoluteJointDef();

	public Vec2 localAnchorA = new Vec2();
	public Vec2 localAnchorB = new Vec2();
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

	public Box2dPrismaticJointDef() {

	}

}
