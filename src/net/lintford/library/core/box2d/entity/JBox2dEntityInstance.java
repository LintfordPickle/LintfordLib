package net.lintford.library.core.box2d.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.definition.Box2dFixtureDefinition;
import net.lintford.library.core.box2d.definition.Box2dJointDefinition;
import net.lintford.library.core.box2d.definition.PObjectDefinition;

/**
 * The {@link JBox2dEntityInstance} class can be loaded from a PObject file and then serialized and restored with the game. JBox2dEntitys can also be pooled in the PObjectManager.
 */
public class JBox2dEntityInstance implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5280466036279609596L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient PObjectDefinition mPObjectDefinition;

	protected List<Box2dBodyInstance> mBodies = new ArrayList<>();
	protected List<Box2dJointInstance> mJoints = new ArrayList<>();

	protected boolean mIsFree;
	protected boolean mPhysicsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isFree() {
		return mIsFree;
	}

	public boolean isPhysicsLoaded() {
		return mPhysicsLoaded;
	}

	/** Returns the main {@link Box2dBodyInstance} of this {@link JBox2dEntityInstance} instance. The main body is the body at index 0. */
	public Box2dBodyInstance mainBody() {
		return mBodies.get(0);
	}

	public List<Box2dBodyInstance> bodies() {
		return mBodies;
	}

	public List<Box2dJointInstance> joints() {
		return mJoints;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntityInstance() {
		mPhysicsLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadPhysics(World pWorld) {
		// Go through and create instances for each definition
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInstance = mBodies.get(i);

			lBodyInstance.loadPhysics(pWorld);

		}

		// need two passes for joints because gear joints reference other joints
//		final int lJointCount = pDefinition.joints().size();
//		for (int i = 0; i < lJointCount; i++) {
//			Box2dJointDefinition lJointDefinition = pDefinition.joints().get(i);
//			Box2dJointInstance lJointInstance = new Box2dJointInstance();
//
//			lJointInstance.jointDef = lJointDefinition.jointDef;
//
//			// Resolve the body references
//			// TODO: This might break in instances of the same object because all jointDef instances are referencing the same object in the joint definition.
//			lJointInstance.jointDef.bodyA = mBodies.get(lJointDefinition.bodyAIndex).mBody;
//			lJointInstance.jointDef.bodyB = mBodies.get(lJointDefinition.bodyBIndex).mBody;
//			lJointInstance.jointDef.collideConnected = lJointDefinition.collideConnected;
//
//			// DEBUG
//
//			lJointInstance.joint = pWorld.createJoint(lJointInstance.jointDef);
//
//		}

		mPhysicsLoaded = true;

	}

	public void savePhysics() {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			mBodies.get(i).savePhysics();

			if (mBodies.get(i).mFixtures != null) {
				final int lFixtureCount = mBodies.get(i).mFixtures.length;
				for (int j = 0; j < lFixtureCount; j++) {
					mBodies.get(i).mFixtures[j].savePhysics();

				}

			}
		}
	}

	public void unloadPhysics() {
		mPhysicsLoaded = false;

	}

	/**
	 * This loads the LintfordCore representation of a JBox2d PObject into memory, it doesn't create the object in the Box2d world though (this is done later in Box2dBodyInstance.loadPhysics())
	 */
	public void loadPObjectFromDefinition(PObjectDefinition pDefinition) {
		mPObjectDefinition = pDefinition;

		if (mBodies == null)
			mBodies = new ArrayList<>();
		if (mJoints == null)
			mJoints = new ArrayList<>();

		// Go through and create instances for each body in the definition
		final int lBodyCount = pDefinition.bodies().size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyDefinition lBodyDef = pDefinition.bodies().get(i);

			Box2dBodyInstance lBox2dBody = new Box2dBodyInstance();

			lBox2dBody.name = lBodyDef.name;
			lBox2dBody.bodyTypeIndex = lBodyDef.bodyTypeIndex;

			lBox2dBody.position.x = lBodyDef.bodyDefinition.position.x;
			lBox2dBody.position.y = lBodyDef.bodyDefinition.position.y;

			lBox2dBody.linearVelocity.x = lBodyDef.bodyDefinition.linearVelocity.x;
			lBox2dBody.linearVelocity.y = lBodyDef.bodyDefinition.linearVelocity.y;

			lBox2dBody.angle = lBodyDef.bodyDefinition.angle;
			lBox2dBody.angularVelocity = lBodyDef.bodyDefinition.angularVelocity;
			lBox2dBody.linearDamping = lBodyDef.bodyDefinition.linearDamping;
			lBox2dBody.angularDamping = lBodyDef.bodyDefinition.angularDamping;
			lBox2dBody.gravityScale = lBodyDef.bodyDefinition.gravityScale;

			lBox2dBody.allowSleep = lBodyDef.bodyDefinition.allowSleep;
			lBox2dBody.awake = lBodyDef.bodyDefinition.awake;
			lBox2dBody.fixedRotation = lBodyDef.bodyDefinition.fixedRotation;
			lBox2dBody.bullet = lBodyDef.bodyDefinition.bullet;
			lBox2dBody.active = lBodyDef.bodyDefinition.active;

			lBox2dBody.mass = lBodyDef.mass;
			lBox2dBody.massCenter.x = lBodyDef.massCenter.x;
			lBox2dBody.massCenter.y = lBodyDef.massCenter.y;
			lBox2dBody.massI = lBodyDef.massI;

			// lBox2dBody.mBody = pWorld.createBody(lBodyDef.bodyDefinition);

			mBodies.add(lBox2dBody);

			// iterate over the needed fixtures
			final int lFixtureCount = lBodyDef.fixtureList.size();

			lBox2dBody.mFixtures = new Box2dFixtureInstance[lFixtureCount];

			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureDefinition lFixtureDef = lBodyDef.fixtureList.get(j);
				Box2dFixtureInstance lFixtureInstance = new Box2dFixtureInstance(lBox2dBody);

				lBox2dBody.mFixtures[j] = lFixtureInstance;

				// Set the instance values here so that something can be serialized later
				// lFixtureInstance.mFixture = lBox2dBody.mBody.createFixture(lFixtureDef.fixtureDef);

				lFixtureInstance.name = lFixtureDef.name;
				lFixtureInstance.density = lFixtureDef.fixtureDef.density;
				lFixtureInstance.restitution = lFixtureDef.fixtureDef.restitution;
				lFixtureInstance.friction = lFixtureDef.fixtureDef.friction;
				lFixtureInstance.isSensor = lFixtureDef.fixtureDef.isSensor;

				lFixtureInstance.shape = lFixtureDef.shape.getCopy();

				Filter lFilter = new Filter();
				lFilter.categoryBits = lFixtureDef.fixtureDef.filter.categoryBits;
				lFilter.groupIndex = lFixtureDef.fixtureDef.filter.groupIndex;
				lFilter.maskBits = lFixtureDef.fixtureDef.filter.maskBits;

				// TODO : Do something with the lFilter

			}

		}

		// need two passes for joints because gear joints reference other joints
		final int lJointCount = pDefinition.joints().size();
		for (int i = 0; i < lJointCount; i++) {
			Box2dJointDefinition lJointDefinition = pDefinition.joints().get(i);
			Box2dJointInstance lJointInstance = new Box2dJointInstance();

			lJointInstance.jointDef = lJointDefinition.jointDef;

			// Resolve the body references
			// TODO: This might break in instances of the same object because all jointDef instances are referencing the same object in the joint definition.
			lJointInstance.jointDef.bodyA = mBodies.get(lJointDefinition.bodyAIndex).mBody;
			lJointInstance.jointDef.bodyB = mBodies.get(lJointDefinition.bodyBIndex).mBody;
			lJointInstance.jointDef.collideConnected = lJointDefinition.collideConnected;

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void assignInstance() {
		mIsFree = false;
	}

	public void releaseInstance() {
		mIsFree = true;
	}

	public void setPosition(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);

			lBodyInst.mBody.setTransform(new Vec2(pX, pY), lBodyInst.mBody.getAngle());
			lBodyInst.mBody.setLinearVelocity(new Vec2(1, 1));

		}

	}

	public void applyForce(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);

			lBodyInst.mBody.applyForce(new Vec2(pX, pY), lBodyInst.mBody.getWorldCenter());

		}

	}

	public void applyLinearImpulse(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);

			lBodyInst.mBody.applyLinearImpulse(new Vec2(pX, pY), lBodyInst.mBody.getWorldCenter(), true);

		}

	}

	public void setFixtureCategory(int pNewCategory) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];
				lFixInst.categoryBits = pNewCategory;

			}

		}

	}

	public void setFixtureBitMask(int pNewBitmask) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];
				lFixInst.maskBits = pNewBitmask;

			}

		}

	}

	// Attempts to set the radius of a fixture shape
	public void setFixtureRadius(String pFixtureName, float pNewRadius) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];

				if (lFixInst != null && lFixInst.name.contentEquals(pFixtureName)) {
					if (lFixInst.shape != null && lFixInst.shape instanceof Box2dCircleInstance) {
						((Box2dCircleInstance) lFixInst.shape).radius = pNewRadius;

					}

				}

			}

		}

	}

	public void setFixtureDimensions(String pFixtureName, float pNewWidth, float pNewHeight) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];

				if (lFixInst != null && lFixInst.name.contentEquals(pFixtureName)) {
					if (lFixInst.shape != null && lFixInst.shape instanceof Box2dPolygonInstance) {
						Box2dPolygonInstance lShape = (Box2dPolygonInstance) lFixInst.shape;

						final float lHalfWidth = pNewWidth / 2f;
						final float lHalfHeight = pNewHeight / 2f;

						// Set the dimensions of this polygon
						lShape.vertexCount = 4;
						lShape.vertices[0].x = lHalfWidth;
						lShape.vertices[0].y = -lHalfHeight;

						lShape.vertices[1].x = lHalfWidth;
						lShape.vertices[1].y = lHalfHeight;

						lShape.vertices[2].x = -lHalfWidth;
						lShape.vertices[2].y = lHalfHeight;

						lShape.vertices[3].x = -lHalfWidth;
						lShape.vertices[3].y = -lHalfHeight;

					}

				}

			}

		}

	}

}
