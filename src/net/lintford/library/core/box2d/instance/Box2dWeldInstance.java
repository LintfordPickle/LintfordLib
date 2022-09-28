package net.lintford.library.core.box2d.instance;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.WeldJointDef;

import net.lintford.library.core.LintfordCore;

public class Box2dWeldInstance extends Box2dJointInstance {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -536400766749047526L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private World mWorld;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dWeldInstance(int poolUid) {
		super(poolUid);
	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void createWeldJoint(World box2dWorld, Body bodyA, Body bodyB, Vec2 localAnchorA, Vec2 localAnchorB, float refAngle) {
		final var lWeldJointDef = new WeldJointDef();

		mWorld = box2dWorld;
		lWeldJointDef.bodyA = bodyA;
		lWeldJointDef.bodyB = bodyB;

		lWeldJointDef.referenceAngle = refAngle;

		lWeldJointDef.localAnchorA.set(localAnchorA);
		lWeldJointDef.localAnchorB.set(localAnchorB);

		lWeldJointDef.collideConnected = true;

		joint = box2dWorld.createJoint(lWeldJointDef);
	}

	public void destroy() {
		if (joint == null)
			return;

		if (mWorld == null) {
			if (joint.getBodyA() != null) {
				mWorld = joint.getBodyA().getWorld();
			}
		}

		if (mWorld == null) {
			if (joint.getBodyB() != null) {
				mWorld = joint.getBodyB().getWorld();
			}
		}

		mWorld.destroyJoint(joint);
		mWorld = null;
	}

	public void loadPhysics(World box2dWorld) {

	}

	public void unloadPhysics(World box2dWorld) {
		if (joint == null)
			return;

		mWorld.destroyJoint(joint);
		mWorld = null;
	}

	public void update(LintfordCore core) {

	}
}
