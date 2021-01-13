package net.lintford.library.core.box2d.instance;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;
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
	public float referenceAngle;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dWeldInstance(int pPoolUid) {
		super(pPoolUid);

	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public void createWeldJoint(World pWorld, Body pBodyA, Body pBodyB, Vec2 pLocalAnchorA, Vec2 pLocalAnchorB, float pRefAngle) {
		final var lWeldJointDef = new WeldJointDef();

		mWorld = pWorld;
		lWeldJointDef.bodyA = pBodyA;
		lWeldJointDef.bodyB = pBodyB;

		lWeldJointDef.referenceAngle = pRefAngle;

		lWeldJointDef.localAnchorA.set(pLocalAnchorA);
		lWeldJointDef.localAnchorB.set(pLocalAnchorB);

		lWeldJointDef.collideConnected = true;

		joint = pWorld.createJoint(lWeldJointDef);

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

	public void savePhysics() {
		super.savePhysics();

		if (joint == null) {
			return;

		}

		RevoluteJoint lRevJoint = (RevoluteJoint) joint;

		referenceAngle = lRevJoint.getReferenceAngle();

	}

	public void loadPhysics(World pWorld) {

	}

	public void unloadPhysics(World pWorld) {
		if (joint == null)
			return;

		mWorld.destroyJoint(joint);
		mWorld = null;

	}

	public void update(LintfordCore pCore) {

	}

}
