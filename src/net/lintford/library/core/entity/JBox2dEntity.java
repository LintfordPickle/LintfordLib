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

	private JBox2dEntityInstance mJBox2dEntityInstance;

	// for WorldEntities with an attached

	public float mWorldPositionX;
	public float mWorldPositionY;
	public float mWorldRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean hasPhysicsEntity() {
		return mJBox2dEntityInstance != null && mJBox2dEntityInstance.isPhysicsLoaded();
	}

	public JBox2dEntityInstance box2dEntityInstance() {
		return mJBox2dEntityInstance;
	}

	public boolean hasEntityAtached() {
		return mJBox2dEntityInstance != null;
	}
	
	public boolean isPhysicsLoaded() {
		return hasEntityAtached() && mJBox2dEntityInstance.isPhysicsLoaded();
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
		if (!isPhysicsLoaded() || mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.savePhysics();

	}

	public void loadPhysics(World pWorld) {
		if (isPhysicsLoaded())
			return;

		mJBox2dEntityInstance.loadPhysics(pWorld);
		mJBox2dEntityInstance.setWorldPosition(mWorldPositionX, mWorldPositionY);
		mJBox2dEntityInstance.setWorldRotation(mWorldRotation);

		final var lBox2dBodyInstance = mJBox2dEntityInstance.mainBody();
		if (lBox2dBodyInstance != null) {
			x = lBox2dBodyInstance.mBody.getPosition().x * Box2dWorldController.UNITS_TO_PIXELS;
			y = lBox2dBodyInstance.mBody.getPosition().y * Box2dWorldController.UNITS_TO_PIXELS;
			r = (float) Math.toDegrees(lBox2dBodyInstance.mBody.getAngle());

		}

	}

	@Override
	public void setPosition(float pWorldX, float pWorldY) {
		super.setPosition(pWorldX, pWorldY);

		mWorldPositionX = pWorldX;
		mWorldPositionY = pWorldY;

	}

	public void setTransform(float pX, float pY, float pR) {
		mWorldPositionX = pX;
		mWorldPositionY = pY;
		mWorldRotation = pR;

		mJBox2dEntityInstance.setTransform(pX, pY, pR);

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

	public void updatePhysics(LintfordCore pCore) {
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