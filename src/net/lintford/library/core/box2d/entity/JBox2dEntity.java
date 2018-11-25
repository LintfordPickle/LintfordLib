package net.lintford.library.core.box2d.entity;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.definition.Box2dFixtureDefinition;
import net.lintford.library.core.box2d.definition.Box2dJointDefinition;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.data.entities.WorldEntity;

public class JBox2dEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5280466036279609596L;

	// TODO: Put the physics conversion units somewhere *nicer*
	public static final float UNITS_TO_PIXELS = 32f;
	public static final float PIXELS_TO_UNITS = 1f / UNITS_TO_PIXELS;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient PObjectDefinition mPObjectDefinition;
	protected List<Box2dBodyInstance> mBodies = new ArrayList<>();
	protected List<Box2dJointInstance> mJoints = new ArrayList<>();

	protected boolean mPhysicsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isPhysicsLoaded() {
		return mPhysicsLoaded;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dEntity() {

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

	public void loadPhysicsFromDefinition(PObjectDefinition pDefinition, World pWorld) {
		mPObjectDefinition = pDefinition;

		if (mBodies == null)
			mBodies = new ArrayList<>();
		if (mJoints == null)
			mJoints = new ArrayList<>();

		// Go through and create instances for each definition
		final int lBodyCount = pDefinition.bodies().size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyDefinition lBodyDef = pDefinition.bodies().get(i); // Vector isn't the fastest for random access (I guess)

			Box2dBodyInstance lBox2dBody = new Box2dBodyInstance(pWorld);

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

			lBox2dBody.mBody = pWorld.createBody(lBodyDef.bodyDefinition);

			mBodies.add(lBox2dBody);

			// iterate over the needed fixtures
			final int lFixtureCount = lBodyDef.fixtureList.size();

			lBox2dBody.mFixtures = new Box2dFixtureInstance[lFixtureCount];

			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureDefinition lFixtureDef = lBodyDef.fixtureList.get(j);
				Box2dFixtureInstance lFixtureInstance = new Box2dFixtureInstance(lBox2dBody);

				lBox2dBody.mFixtures[j] = lFixtureInstance;

				// Set the instance values here so that something can be serialized later
				lFixtureInstance.mFixture = lBox2dBody.mBody.createFixture(lFixtureDef.fixtureDef);

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

			// DEBUG

			lJointInstance.joint = pWorld.createJoint(lJointInstance.jointDef);

		}

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

	@Override
	public void update(LintfordCore pCore) {
		super.update(pCore);

		if (mBodies != null && mBodies.size() > 0) {
			Box2dBodyInstance lBody = mBodies.get(0);
			x = lBody.mBody.getPosition().x * UNITS_TO_PIXELS;
			y = lBody.mBody.getPosition().y * UNITS_TO_PIXELS;

		}

	}

	public void unloadPhysics() {
		mPhysicsLoaded = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setPosition(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);

			lBodyInst.mBody.setTransform(new Vec2(pX * PIXELS_TO_UNITS, pY * PIXELS_TO_UNITS), lBodyInst.mBody.getAngle());
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

}
