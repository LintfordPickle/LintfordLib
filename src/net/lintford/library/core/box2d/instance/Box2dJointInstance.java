package net.lintford.library.core.box2d.instance;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.entity.BaseInstanceData;

public abstract class Box2dJointInstance extends BaseInstanceData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5442409523175676125L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String name;
	public transient Joint joint;

	public int bodyAUID;
	public int bodyBUID;

	public Vec2 localAnchorA = new Vec2();
	public Vec2 localAnchorB = new Vec2();

	public boolean collidesConnected;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dJointInstance() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		super.initialize(pParent);

		localAnchorA = new Vec2();
		localAnchorB = new Vec2();

	}

	// --------------------------------------
	// Method
	// --------------------------------------

	public abstract void savePhysics();

	public abstract void loadPhysics(World pWorld);

	public abstract void unloadPhysics();

	public abstract void update(LintfordCore pCore);

}
