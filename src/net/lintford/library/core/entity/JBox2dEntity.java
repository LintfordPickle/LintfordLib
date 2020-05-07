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

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean hasPhysicsEntity() {
		return mJBox2dEntityInstance != null;
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

		transformPObject(mWorldPositionX, mWorldPositionY, mRotationRadians);

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

		// Initially we should set the new instance (loaded from a reference defintion) to the WorldEntity SRT.
		if (isPhysicsLoaded()) {
			transformPObject(mWorldPositionX, mWorldPositionY, mRotationRadians);
		}

	}

	@Override
	public void setPosition(float pWorldX, float pWorldY) {
		super.setPosition(pWorldX, pWorldY);

		if (hasPhysicsEntity()) {
			mJBox2dEntityInstance.transformEntityInstance(mWorldPositionX, mWorldPositionY, mRotationRadians);

		}

	}

	public void transformPObject(float pWorldPositionX, float pWorldPositionY, float pRotationInRadians) {
		if (hasPhysicsEntity()) {
			mJBox2dEntityInstance.transformEntityInstance(pWorldPositionX, pWorldPositionY, pRotationInRadians);

		}

	}

	public void reset() {
		mWorldPositionX = 0.f;
		mWorldPositionY = 0.f;
		mRotationRadians = 0.f;

		if (hasPhysicsEntity()) {
			mJBox2dEntityInstance.resetEntityInstance();

		}

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
				mWorldPositionX = lBox2dBodyInstance.mBody.getPosition().x * Box2dWorldController.UNITS_TO_PIXELS;
				mWorldPositionY = lBox2dBodyInstance.mBody.getPosition().y * Box2dWorldController.UNITS_TO_PIXELS;
				mRotationRadians = lBox2dBodyInstance.mBody.getAngle();

			}

		}

	}

}