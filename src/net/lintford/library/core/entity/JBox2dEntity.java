package net.lintford.library.core.entity;

import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;

public abstract class JBox2dEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public JBox2dEntityInstance mJBox2dEntityInstance;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean hasPhysicsEntity() {
		return mJBox2dEntityInstance != null && mJBox2dEntityInstance.isPhysicsLoaded();
	}

	public JBox2dEntityInstance box2dEntityInstance() {
		return mJBox2dEntityInstance;
	}

	public boolean isPhysicsLoaded() {
		return mJBox2dEntityInstance != null;
	}

	@Override
	public boolean isAssigned() {
		return false;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntity(final int pPoolUid) {
		super(pPoolUid);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		super.initialize(pParent);

		mJBox2dEntityInstance.initialize(this);
		
	}

	public void setPhysicsObject(JBox2dEntityInstance pJBox2dEntity) {
		mJBox2dEntityInstance = pJBox2dEntity;
		
	}

	public void savePhysics() {
		if (mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.savePhysics();

	}

	public void loadPhysics(World pWorld) {
		if (!isPhysicsLoaded())
			return;

		mJBox2dEntityInstance.loadPhysics(pWorld);

	}

	public void unloadPhysics() {
		if (isPhysicsLoaded()) {
			mJBox2dEntityInstance.unloadPhysics();

		}

	}

	public void setNewPhysicsProperties() {
		if (!hasPhysicsEntity()) {
			return;

		}

	}

	public void updatePhyics(LintfordCore pCore) {
		if (isPhysicsLoaded()) {
			final var lBox2dBodyInstance = mJBox2dEntityInstance.mainBody();
			if (lBox2dBodyInstance != null) {
				x = lBox2dBodyInstance.mBody.getPosition().x * Box2dWorldController.UNITS_TO_PIXELS;
				y = lBox2dBodyInstance.mBody.getPosition().y * Box2dWorldController.UNITS_TO_PIXELS;
				r = lBox2dBodyInstance.mBody.getAngle();
			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}