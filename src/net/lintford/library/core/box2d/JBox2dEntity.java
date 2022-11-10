package net.lintford.library.core.box2d;

import org.jbox2d.dynamics.World;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.entity.WorldEntity;

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntity() {
		super();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void afterLoaded(Object parent) {
		super.afterLoaded(parent);

		mJBox2dEntityInstance.afterLoaded(this);
	}

	public void setPhysicsObject(JBox2dEntityInstance jbox2dEntity) {
		mJBox2dEntityInstance = jbox2dEntity;

		transformPObject(x, y, rotationRadians);
	}

	public void savePhysics() {
		if (!isPhysicsLoaded() || mJBox2dEntityInstance == null)
			return;

		mJBox2dEntityInstance.savePhysics();
	}

	public void loadPhysics(World pWorld) {
		loadPhysics(pWorld, true);
	}

	public void loadPhysics(World world, boolean updateWorldTranformation) {
		if (isPhysicsLoaded())
			return;

		mJBox2dEntityInstance.loadPhysics(world);

		if (updateWorldTranformation && isPhysicsLoaded())
			transformPObject(x, y, rotationRadians);
	}

	public void setPosition(float worldX, float worldY) {
		x = worldX;
		y = worldY;

		if (hasPhysicsEntity())
			mJBox2dEntityInstance.transformEntityInstance(x, y, rotationRadians);
	}

	public void transformPObject(float worldX, float worldY, float rotation) {
		if (hasPhysicsEntity())
			mJBox2dEntityInstance.transformEntityInstance(worldX, worldY, rotation);

		x = worldX;
		y = worldY;
		rotationRadians = rotation;
	}

	public void reset() {
		x = 0.f;
		y = 0.f;
		rotationRadians = 0.f;

		if (hasPhysicsEntity())
			mJBox2dEntityInstance.resetEntityInstance();

	}

	public void unloadPhysics() {
		if (isPhysicsLoaded())
			mJBox2dEntityInstance.unloadPhysics();

	}

	public void setNewPhysicsProperties() {
		if (!hasPhysicsEntity())
			return;

	}

	public void updatePhysics(LintfordCore core) {
		if (isPhysicsLoaded()) {
			final var lBox2dBodyInstance = mJBox2dEntityInstance.mainBody();
			if (lBox2dBodyInstance != null) {
				x = ConstantsPhysics.toPixels(lBox2dBodyInstance.mBody.getPosition().x);
				y = ConstantsPhysics.toPixels(lBox2dBodyInstance.mBody.getPosition().y);
				rotationRadians = lBox2dBodyInstance.mBody.getAngle();
			}
		}
	}
}