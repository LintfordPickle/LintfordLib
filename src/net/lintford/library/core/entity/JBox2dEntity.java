package net.lintford.library.core.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entity.Box2dBodyInstance;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;

public class JBox2dEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public JBox2dEntityInstance mJBox2dEntityInstance;
	protected transient Vec2 mVelocity = new Vec2();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vec2 velocity() {
		return mVelocity;
	}

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

	@Override
	public int getPoolID() {
		return 0;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntity() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void setPhysicsObject(JBox2dEntityInstance pJBox2dEntity) {
		mJBox2dEntityInstance = pJBox2dEntity;
		
		// Any properties previously set on this instance which should be applied to the Box2d world
		// (Object->Box2D) should be set here. After this point, properties will be set from Box2D->Object

		if (mJBox2dEntityInstance.mainBody() != null) {
			mJBox2dEntityInstance.mainBody().position.x = x * Box2dWorldController.PIXELS_TO_UNITS;
			mJBox2dEntityInstance.mainBody().position.y = y * Box2dWorldController.PIXELS_TO_UNITS;
		}

	}

	public void savePhysics() {
		if (mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.savePhysics();

	}

	public void loadPhysics(World pWorld) {
		if (!isPhysicsLoaded())
			return;

		mVelocity = new Vec2();

		mJBox2dEntityInstance.loadPhysics(pWorld);

	}

	public void unloadPhysics() {
		if (isPhysicsLoaded()) {
			mJBox2dEntityInstance.unloadPhysics();

		}

	}

	public void updatePhyics(LintfordCore pCore) {
		if (isPhysicsLoaded()) {
			Box2dBodyInstance lMainBody = mJBox2dEntityInstance.mainBody();
			if (lMainBody != null) {
				// Update the position of this character
				x = lMainBody.mBody.getPosition().x * Box2dWorldController.UNITS_TO_PIXELS;
				y = lMainBody.mBody.getPosition().y * Box2dWorldController.UNITS_TO_PIXELS;

			}

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