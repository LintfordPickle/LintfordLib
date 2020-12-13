package net.lintford.library.core.box2d.definition;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.WeldJointDef;

public class Box2dWeldJointDef extends Box2dJointDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public WeldJointDef weldJointDef = new WeldJointDef();

	public final Vec2 localAnchorA = new Vec2();
	public final Vec2 localAnchorB = new Vec2();
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

	public Box2dWeldJointDef() {

	}

}
