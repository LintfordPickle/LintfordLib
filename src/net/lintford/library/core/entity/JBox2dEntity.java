package net.lintford.library.core.entity;

import org.jbox2d.dynamics.World;

import net.lintford.library.ConstantsPhysics;
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

		transformPObject(worldPositionX, worldPositionY, rotationInRadians);

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
			transformPObject(worldPositionX, worldPositionY, rotationInRadians);
		}

	}

	@Override
	public void setPosition(float pWorldX, float pWorldY) {
		super.setPosition(pWorldX, pWorldY);

		if (hasPhysicsEntity()) {
			mJBox2dEntityInstance.transformEntityInstance(worldPositionX, worldPositionY, rotationInRadians);

		}

	}

	public void transformPObject(float pWorldPositionX, float pWorldPositionY, float pRotationInRadians) {
		if (hasPhysicsEntity()) {
			mJBox2dEntityInstance.transformEntityInstance(pWorldPositionX, pWorldPositionY, pRotationInRadians);

		}

		worldPositionX = pWorldPositionX;
		worldPositionY = pWorldPositionY;
		rotationInRadians = pRotationInRadians;

	}

	public void reset() {
		worldPositionX = 0.f;
		worldPositionY = 0.f;
		rotationInRadians = 0.f;

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
				worldPositionX = ConstantsPhysics.toPixels(lBox2dBodyInstance.mBody.getPosition().x);
				worldPositionY = ConstantsPhysics.toPixels(lBox2dBodyInstance.mBody.getPosition().y);
				rotationInRadians = lBox2dBodyInstance.mBody.getAngle();

			}

		}

	}

}