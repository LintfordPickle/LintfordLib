package net.lintford.library.data.entities;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entity.Box2dBodyInstance;
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

	public transient boolean mIsPhysicsLoaded = false;
	protected transient Vec2 mVelocity = new Vec2();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean hasPhysicsEntity() {
		return mJBox2dEntityInstance != null;
	}

	public JBox2dEntityInstance box2dEntityInstance() {
		return mJBox2dEntityInstance;
	}

	public boolean isPhysicsLoaded() {
		return mIsPhysicsLoaded;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntity() {
		mIsPhysicsLoaded = false;
		mJBox2dEntityInstance = null;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void setPhysicsObject(World pWorld, JBox2dEntityInstance pJBox2dEntity) {
		mJBox2dEntityInstance = pJBox2dEntity;

		loadPhysics(pWorld);

		mIsPhysicsLoaded = true;
	}

	public void savePhysics() {
		if (mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.savePhysics();

	}

	public void loadPhysics(World pWorld) {
		if (mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.loadPhysics(pWorld);
		mIsPhysicsLoaded = true;

	}

	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mIsPhysicsLoaded && mJBox2dEntityInstance != null) {
			Box2dBodyInstance lMainBody = mJBox2dEntityInstance.mainBody();

			// Update the position of this character
			x = lMainBody.mBody.getPosition().x * Box2dWorldController.UNITS_TO_PIXELS;
			y = lMainBody.mBody.getPosition().y * Box2dWorldController.UNITS_TO_PIXELS;

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void setPosition(float pWorldX, float pWorldY) {
		if (mJBox2dEntityInstance != null) {
			Box2dBodyInstance lMainBody = mJBox2dEntityInstance.mainBody();

			// TODO: Remove the garbage
			lMainBody.mBody.setTransform(new Vec2(pWorldX * Box2dWorldController.PIXELS_TO_UNITS, pWorldY * Box2dWorldController.PIXELS_TO_UNITS), 0);
			lMainBody.mBody.setAwake(true);

		}

		// Update the position of this character
		x = pWorldX;
		y = pWorldY;

	}

}