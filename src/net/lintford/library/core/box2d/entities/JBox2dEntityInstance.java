package net.lintford.library.core.box2d.entities;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WeldJointDef;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.box2d.BasePhysicsData;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.box2d.instance.Box2dCircleInstance;
import net.lintford.library.core.box2d.instance.Box2dFixtureInstance;
import net.lintford.library.core.box2d.instance.Box2dInstanceManager;
import net.lintford.library.core.box2d.instance.Box2dJointInstance;
import net.lintford.library.core.box2d.instance.Box2dPolygonInstance;
import net.lintford.library.core.box2d.instance.Box2dPrismaticInstance;
import net.lintford.library.core.box2d.instance.Box2dRevoluteInstance;
import net.lintford.library.core.box2d.instance.Box2dWeldInstance;
import net.lintford.library.core.entity.instances.IndexedPooledBaseData;

/**
 * The {@link JBox2dEntityInstance} class can be loaded from a PObject file and then serialized and restored with the game. JBox2dEntitys can also be pooled in the PObjectManager.
 */
public class JBox2dEntityInstance extends IndexedPooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5280466036279609596L;

	public static final String MAIN_BODY_NAME = "MainBody";
	public static final String MAIN_FIXTURE_NAME = "MainFixture";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public String spriteSheetName;

	protected transient PObjectDefinition mPObjectDefinition;
	protected final List<Box2dBodyInstance> mBodies = new ArrayList<>();
	protected final List<Box2dJointInstance> mJoints = new ArrayList<>();
	protected BasePhysicsData mMainBodyUserDataObject;
	protected Box2dBodyInstance mMainBody;
	protected Vec2 entityPosition = new Vec2();
	protected float entityAngle;
	protected boolean mIsFree;
	protected transient World mWorld;
	protected transient boolean mPhysicsLoaded = false;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Vec2 entityPosition() {
		return entityPosition;
	}

	public float entityAngle() {
		return entityAngle;
	}

	public BasePhysicsData userDataObject() {
		return mMainBodyUserDataObject;
	}

	public void userDataObject(BasePhysicsData newUserDataObject) {
		if (newUserDataObject == null) {
			mMainBodyUserDataObject = null;
			if (mPhysicsLoaded) {
				mainBody().mBody.setUserData(null);
			}

			return;
		}

		mMainBodyUserDataObject = newUserDataObject;

		if (mPhysicsLoaded) {
			mainBody().mBody.setUserData(mMainBodyUserDataObject);
		}
	}

	public boolean isPhysicsLoaded() {
		return mPhysicsLoaded;
	}

	/** Returns the main {@link Box2dBodyInstance} of this {@link JBox2dEntityInstance} instance. The main body is the body at index 0. */
	public Box2dBodyInstance mainBody() {
		return mMainBody;
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

	public JBox2dEntityInstance(final int poolUid) {
		super(poolUid);

		mPhysicsLoaded = false;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void afterLoaded(Object parent) {
		super.afterLoaded(parent);

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			mBodies.get(i).afterLoaded(this);
		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			mJoints.get(i).afterLoaded(this);
		}
	}

	/** Loads a reference implementation of the PObject definition */
	public void loadPhysics(World box2dWorld) {
		if (box2dWorld == null)
			return;
		mWorld = box2dWorld;

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);

			lBox2dBodyInstance.loadPhysics(box2dWorld, this);
		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointInstance = (Box2dJointInstance) mJoints.get(i);
			if (lBox2dJointInstance instanceof Box2dRevoluteInstance) {
				final var lBox2dRevoluteJointInsance = (Box2dRevoluteInstance) lBox2dJointInstance;

				final var lRevoluteJointDef = new RevoluteJointDef();

				final var lBodyA = getBodyByIndex(lBox2dJointInstance.bodyAUid);
				if (lBodyA == null)
					continue;
				lRevoluteJointDef.bodyA = lBodyA.mBody;

				final var lBodyB = getBodyByIndex(lBox2dJointInstance.bodyBUid);
				if (lBodyB == null)
					continue;
				lRevoluteJointDef.bodyB = lBodyB.mBody;

				lRevoluteJointDef.referenceAngle = lBox2dRevoluteJointInsance.referenceAngle;
				lRevoluteJointDef.enableLimit = lBox2dRevoluteJointInsance.enableLimit;
				lRevoluteJointDef.lowerAngle = lBox2dRevoluteJointInsance.lowerAngle;
				lRevoluteJointDef.upperAngle = lBox2dRevoluteJointInsance.upperAngle;

				lRevoluteJointDef.enableMotor = lBox2dRevoluteJointInsance.enableMotor;
				lRevoluteJointDef.motorSpeed = lBox2dRevoluteJointInsance.motorSpeed;
				lRevoluteJointDef.maxMotorTorque = lBox2dRevoluteJointInsance.maxMotorTorque;

				lRevoluteJointDef.localAnchorA.set(lBox2dRevoluteJointInsance.localAnchorA);
				lRevoluteJointDef.localAnchorB.set(lBox2dRevoluteJointInsance.localAnchorB);

				lRevoluteJointDef.collideConnected = lBox2dRevoluteJointInsance.collidesConnected;

				lBox2dJointInstance.joint = box2dWorld.createJoint(lRevoluteJointDef);

			} else if (lBox2dJointInstance instanceof Box2dPrismaticInstance) {
				final var lBox2dPrismaticJointInsance = (Box2dPrismaticInstance) lBox2dJointInstance;
				final var lPrismaticJointDef = new PrismaticJointDef();

				final var lBodyA = getBodyByIndex(lBox2dJointInstance.bodyAUid);
				if (lBodyA == null)
					continue;
				lPrismaticJointDef.bodyA = lBodyA.mBody;

				final var lBodyB = getBodyByIndex(lBox2dJointInstance.bodyBUid);
				if (lBodyB == null)
					continue;
				lPrismaticJointDef.bodyB = lBodyB.mBody;

				lPrismaticJointDef.referenceAngle = lBox2dPrismaticJointInsance.referenceAngle;
				lPrismaticJointDef.enableLimit = lBox2dPrismaticJointInsance.enableLimit;
				lPrismaticJointDef.lowerTranslation = lBox2dPrismaticJointInsance.lowerAngle;
				lPrismaticJointDef.upperTranslation = lBox2dPrismaticJointInsance.upperAngle;

				lPrismaticJointDef.enableMotor = lBox2dPrismaticJointInsance.enableMotor;
				lPrismaticJointDef.motorSpeed = lBox2dPrismaticJointInsance.motorSpeed;

				lPrismaticJointDef.localAnchorA.set(lBox2dPrismaticJointInsance.localAnchorA);
				lPrismaticJointDef.localAnchorB.set(lBox2dPrismaticJointInsance.localAnchorB);

				lPrismaticJointDef.collideConnected = lBox2dPrismaticJointInsance.collidesConnected;

				lBox2dJointInstance.joint = box2dWorld.createJoint(lPrismaticJointDef);

			} else if (lBox2dJointInstance instanceof Box2dWeldInstance) {
				final var lBox2dWeldJointInsance = (Box2dWeldInstance) lBox2dJointInstance;
				final var lWeldJointInstance = new WeldJointDef();

				final var lBodyA = getBodyByIndex(lBox2dJointInstance.bodyAUid);
				if (lBodyA == null)
					continue;

				final var lBodyB = getBodyByIndex(lBox2dJointInstance.bodyBUid);
				if (lBodyB == null)
					continue;

				lWeldJointInstance.collideConnected = lBox2dWeldJointInsance.collidesConnected;

				lBox2dWeldJointInsance.createWeldJoint(box2dWorld, lBodyA.mBody, lBodyB.mBody, lBox2dWeldJointInsance.localAnchorA, lBox2dWeldJointInsance.localAnchorB, 0.f);

			}

		}

		// Resolve the main body
		mMainBody = getBodyByName(MAIN_BODY_NAME);
		if (mMainBody == null) {
			mMainBody = getBodyByIndex(0);
		}

		if (mMainBodyUserDataObject != null && mainBody() != null) {
			if (mainBody().mBody != null)
				mainBody().mBody.setUserData(mMainBodyUserDataObject);

		}

		mPhysicsLoaded = true;

	}

	public void savePhysics() {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			mBodies.get(i).savePhysics(this);

			if (mBodies.get(i).mFixtures != null) {
				final int lFixtureCount = mBodies.get(i).mFixtures.length;
				for (int j = 0; j < lFixtureCount; j++) {
					mBodies.get(i).mFixtures[j].savePhysics();
				}
			}
		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			Box2dJointInstance lJointInstance = mJoints.get(i);

			if (lJointInstance.joint != null) {
				lJointInstance.savePhysics();
			}
		}
	}

	public void unloadPhysics() {
		if (!isPhysicsLoaded() || mWorld == null) {
			return;
		}

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);

			if (lBox2dBodyInstance.mBody != null) {
				lBox2dBodyInstance.mBody.setUserData(null);
			}

			lBox2dBodyInstance.unloadPhysics();
		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointInstance = mJoints.get(i);

			lBox2dJointInstance.unloadPhysics(mWorld);
		}

		mMainBody = null;
		mWorld = null;
		mPhysicsLoaded = false;
	}

	public Box2dBodyInstance getBodyByName(String bodyName) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBody = mBodies.get(i);
			if (lBody == null || lBody.name == null) {
				continue;
			}

			if (lBody.name.contentEquals(bodyName))
				return mBodies.get(i);
		}

		return null;
	}

	public Box2dBodyInstance getBodyByIndex(int arrayIndex) {
		if (arrayIndex >= mBodies.size()) {
			return null;
		}

		return mBodies.get(arrayIndex);
	}

	public Box2dJointInstance getJointByName(String jointName) {
		final int lNumJoints = mJoints.size();
		for (int i = 0; i < lNumJoints; i++) {
			if (mJoints.get(i).name.contentEquals(jointName))
				return mJoints.get(i);

		}

		return null;

	}

	public Box2dJointInstance getJointByIndex(int arrayIndex) {
		return mJoints.get(arrayIndex);
	}

	/**
	 * This loads the LintfordCore representation of a JBox2d PObject into memory, it doesn't create the object in the Box2d world though (this is done later in Box2dBodyInstance.loadPhysics())
	 */
	public void loadPObjectFromDefinition(Box2dInstanceManager box2dInstanceManager, PObjectDefinition definition) {
		mPObjectDefinition = definition;

		loadBodiesFromDefinition(box2dInstanceManager, mPObjectDefinition);
		loadJointsFromDefinition(box2dInstanceManager, mPObjectDefinition);
	}

	private void loadBodiesFromDefinition(Box2dInstanceManager box2dInstanceManager, PObjectDefinition definition) {
		final int lBodyCount = definition.bodies().size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyDefinition = definition.bodies().get(i);
			final var lBox2dBodyInstance = box2dInstanceManager.box2dBodyInstanceRepository().getFreePooledItem();

			lBox2dBodyInstance.name = lBox2dBodyDefinition.name;
			lBox2dBodyInstance.bodyTypeIndex = lBox2dBodyDefinition.bodyTypeIndex;

			lBox2dBodyInstance.objectPositionInUnits.x = lBox2dBodyDefinition.bodyDefinition.position.x;
			lBox2dBodyInstance.objectPositionInUnits.y = lBox2dBodyDefinition.bodyDefinition.position.y;

			lBox2dBodyInstance.linearVelocity.x = lBox2dBodyDefinition.bodyDefinition.linearVelocity.x;
			lBox2dBodyInstance.linearVelocity.y = lBox2dBodyDefinition.bodyDefinition.linearVelocity.y;

			lBox2dBodyInstance.objectAngleInRadians = lBox2dBodyDefinition.bodyDefinition.angle;
			lBox2dBodyInstance.angularVelocity = lBox2dBodyDefinition.bodyDefinition.angularVelocity;
			lBox2dBodyInstance.linearDamping = lBox2dBodyDefinition.bodyDefinition.linearDamping;
			lBox2dBodyInstance.angularDamping = lBox2dBodyDefinition.bodyDefinition.angularDamping;
			lBox2dBodyInstance.gravityScale = lBox2dBodyDefinition.bodyDefinition.gravityScale;

			lBox2dBodyInstance.allowSleep = lBox2dBodyDefinition.bodyDefinition.allowSleep;
			lBox2dBodyInstance.awake = lBox2dBodyDefinition.bodyDefinition.awake;
			lBox2dBodyInstance.fixedRotation = lBox2dBodyDefinition.bodyDefinition.fixedRotation;
			lBox2dBodyInstance.bullet = lBox2dBodyDefinition.bodyDefinition.bullet;
			lBox2dBodyInstance.active = lBox2dBodyDefinition.bodyDefinition.active;

			lBox2dBodyInstance.mass = lBox2dBodyDefinition.mass;
			lBox2dBodyInstance.massCenter.x = lBox2dBodyDefinition.massCenter.x;
			lBox2dBodyInstance.massCenter.y = lBox2dBodyDefinition.massCenter.y;
			lBox2dBodyInstance.massI = lBox2dBodyDefinition.massI;

			mBodies.add(lBox2dBodyInstance);

			// iterate over the needed fixtures
			final int lFixtureCount = lBox2dBodyDefinition.fixtureList.size();

			lBox2dBodyInstance.mFixtures = new Box2dFixtureInstance[lFixtureCount];

			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureDefinition = lBox2dBodyDefinition.fixtureList.get(j);
				final var lBox2dFixtureInstance = box2dInstanceManager.box2dFixtureInstanceRepository().getFreePooledItem();

				lBox2dBodyInstance.mFixtures[j] = lBox2dFixtureInstance;

				// Set the instance values here so that something can be serialized later
				// lFixtureInstance.mFixture = lBox2dBody.mBody.createFixture(lFixtureDef.fixtureDef);

				lBox2dFixtureInstance.name = lBox2dFixtureDefinition.name;
				lBox2dFixtureInstance.density = lBox2dFixtureDefinition.fixtureDef.density;
				lBox2dFixtureInstance.restitution = lBox2dFixtureDefinition.fixtureDef.restitution;
				lBox2dFixtureInstance.friction = lBox2dFixtureDefinition.fixtureDef.friction;
				lBox2dFixtureInstance.isSensor = lBox2dFixtureDefinition.fixtureDef.isSensor;

				lBox2dFixtureInstance.shape = lBox2dFixtureDefinition.shape.getCopy();

				Filter lFilter = new Filter();
				lFilter.categoryBits = lBox2dFixtureDefinition.fixtureDef.filter.categoryBits;
				lFilter.groupIndex = lBox2dFixtureDefinition.fixtureDef.filter.groupIndex;
				lFilter.maskBits = lBox2dFixtureDefinition.fixtureDef.filter.maskBits;
			}
		}
	}

	private void loadJointsFromDefinition(Box2dInstanceManager box2dInstanceManager, PObjectDefinition definition) {
		final int lJointCount = definition.joints().size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointDefinition = definition.joints().get(i);

			if (lBox2dJointDefinition.jointDef instanceof RevoluteJointDef) {
				final var lBox2dJointInstance = box2dInstanceManager.box2dJointInstanceRepository().getFreePooledItem();
				final var lJointDefinition = (RevoluteJointDef) lBox2dJointDefinition.jointDef;

				lBox2dJointInstance.name = lBox2dJointDefinition.name;

				lBox2dJointInstance.bodyAUid = lBox2dJointDefinition.bodyAIndex;
				lBox2dJointInstance.bodyBUid = lBox2dJointDefinition.bodyBIndex;

				lBox2dJointInstance.localAnchorA.x = lJointDefinition.localAnchorA.x;
				lBox2dJointInstance.localAnchorA.y = lJointDefinition.localAnchorA.y;
				lBox2dJointInstance.localAnchorB.x = lJointDefinition.localAnchorB.x;
				lBox2dJointInstance.localAnchorB.y = lJointDefinition.localAnchorB.y;
				lBox2dJointInstance.bodyBUid = lBox2dJointDefinition.bodyBIndex;
				lBox2dJointInstance.referenceAngle = lJointDefinition.referenceAngle;

				lBox2dJointInstance.enableLimit = lJointDefinition.enableLimit;
				lBox2dJointInstance.lowerAngle = lJointDefinition.lowerAngle;
				lBox2dJointInstance.upperAngle = lJointDefinition.upperAngle;

				lBox2dJointInstance.enableMotor = lJointDefinition.enableMotor;
				lBox2dJointInstance.maxMotorTorque = lJointDefinition.maxMotorTorque;
				lBox2dJointInstance.motorSpeed = lJointDefinition.motorSpeed;

				mJoints.add(lBox2dJointInstance);

			} else if (lBox2dJointDefinition.jointDef instanceof PrismaticJointDef) {
				final var lBox2dJointInstance = new Box2dPrismaticInstance(0);// pBox2dInstanceManager.box2dJointInstanceRepository().getFreePooledItem();
				final var lJointDefinition = (PrismaticJointDef) lBox2dJointDefinition.jointDef;

				lBox2dJointInstance.name = lBox2dJointDefinition.name;

				lBox2dJointInstance.bodyAUid = lBox2dJointDefinition.bodyAIndex;
				lBox2dJointInstance.bodyBUid = lBox2dJointDefinition.bodyBIndex;

				lBox2dJointInstance.localAnchorA.x = lJointDefinition.localAnchorA.x;
				lBox2dJointInstance.localAnchorA.y = lJointDefinition.localAnchorA.y;
				lBox2dJointInstance.localAnchorB.x = lJointDefinition.localAnchorB.x;
				lBox2dJointInstance.localAnchorB.y = lJointDefinition.localAnchorB.y;
				lBox2dJointInstance.bodyBUid = lBox2dJointDefinition.bodyBIndex;
				lBox2dJointInstance.referenceAngle = lJointDefinition.referenceAngle;

				lBox2dJointInstance.enableLimit = lJointDefinition.enableLimit;
				lBox2dJointInstance.lowerAngle = lJointDefinition.lowerTranslation;
				lBox2dJointInstance.upperAngle = lJointDefinition.upperTranslation;

				lBox2dJointInstance.enableMotor = lJointDefinition.enableMotor;
				lBox2dJointInstance.motorSpeed = lJointDefinition.motorSpeed;

				mJoints.add(lBox2dJointInstance);

			}

		}
	}

	/* returns all Box2dBodyInstance and Box2dJointInstances back into the pool */
	public void returnPooledInstances(Box2dInstanceManager box2dInstanceManager) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);

			if (lBox2dBodyInstance.mBody != null)
				lBox2dBodyInstance.mBody.setUserData(null);

			lBox2dBodyInstance.unloadPhysics();

			box2dInstanceManager.box2dBodyInstanceRepository().returnPooledItem(lBox2dBodyInstance);
		}

		mBodies.clear();

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointInstance = mJoints.get(i);

			lBox2dJointInstance.unloadPhysics(mWorld);

			if (lBox2dJointInstance instanceof Box2dRevoluteInstance)
				box2dInstanceManager.box2dJointInstanceRepository().returnPooledItem((Box2dRevoluteInstance) lBox2dJointInstance);

		}

		mJoints.clear();
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

	public void resetEntityInstance() {
		final int lBodyCount = mBodies.size();

		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			if (lBox2dBodyInstance.mBody != null) {
				final var lBox2dPosition = lBox2dBodyInstance.mBody.getPosition();

				lBox2dPosition.x = lBox2dBodyInstance.objectPositionInUnits.x;
				lBox2dPosition.y = lBox2dBodyInstance.objectPositionInUnits.y;
				float lLocalAngle = lBox2dBodyInstance.objectAngleInRadians;

				lBox2dBodyInstance.mBody.setTransform(lBox2dPosition, lLocalAngle);
			}
		}
	}

	/**
	 * Tranforms the PObject as a whole to the desired position with the desired angle. First the the PObject is scaled, then rotations are applied, finally entire object is translated into final position.
	 * 
	 * @param worldXInPixels    The final absolute world position X.
	 * @param worldYInPixels    The final absolute world position Y.
	 * @param rotationInRadians The rotation angle (in radians) to rotate the PObject.
	 */
	public void transformEntityInstance(float worldXInPixels, float worldYInPixels, float rotationInRadians) {

		entityPosition.x = ConstantsPhysics.toUnits(worldXInPixels);
		entityPosition.y = ConstantsPhysics.toUnits(worldYInPixels);

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			transformBox2dBodyInstance(mBodies.get(i), worldXInPixels, worldYInPixels, rotationInRadians);
		}
	}

	public void transformEntityInstance(float worldXInPixels, float worldYInPixels) {

		entityPosition.x = ConstantsPhysics.toUnits(worldXInPixels);
		entityPosition.y = ConstantsPhysics.toUnits(worldYInPixels);

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			transformBox2dBodyInstance(lBox2dBodyInstance, worldXInPixels, worldYInPixels, lBox2dBodyInstance.objectAngleInRadians);
		}
	}

	private void transformBox2dBodyInstance(final Box2dBodyInstance box2dBodyInstance, float desiredWorldXInPixels, float desiredWorldYInPixels, float desiredRotationInRadians) {
		if (box2dBodyInstance == null)
			return;

		entityPosition.x = ConstantsPhysics.toUnits(desiredWorldXInPixels);
		entityPosition.y = ConstantsPhysics.toUnits(desiredWorldYInPixels);

		if (box2dBodyInstance.mBody != null) {
			final var lBox2dPosition = box2dBodyInstance.mBody.getPosition();

			lBox2dPosition.x = box2dBodyInstance.objectPositionInUnits.x;
			lBox2dPosition.y = box2dBodyInstance.objectPositionInUnits.y;

			rotationAroundOrigin(lBox2dPosition, desiredRotationInRadians);

			lBox2dPosition.x += ConstantsPhysics.toUnits(desiredWorldXInPixels);
			lBox2dPosition.y += ConstantsPhysics.toUnits(desiredWorldYInPixels);

			box2dBodyInstance.mBody.setTransform(lBox2dPosition, desiredRotationInRadians);
		}
	}

	public void rotationAroundOrigin(Vec2 position, float angleInRadians) {
		float sin = (float) (Math.sin(angleInRadians));
		float cos = (float) (Math.cos(angleInRadians));

		float lNewX = (position.x) * cos - (position.y) * sin;
		float lNewY = (position.x) * sin + (position.y) * cos;

		position.x = lNewX;
		position.y = lNewY;
	}

	public void setLinearVelocity(float velocityX, float velocityY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			lBodyInst.linearVelocity.set(velocityX, velocityY);

			if (lBodyInst.mBody != null) {
				lBodyInst.mBody.setLinearVelocity(new Vec2(velocityX, velocityY));
			}
		}
	}

	public Vec2 getLinearVelocity() {
		if (mMainBody == null)
			return null;

		return mMainBody.linearVelocity;
	}

	public void applyForce(float forceX, float forceY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.applyForce(new Vec2(forceX, forceY), lBodyInst.mBody.getWorldCenter());
		}
	}

	public void setActive(boolean isActive) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.setActive(isActive);
		}
	}

	public void setGravityScale(float gravityScale) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.setGravityScale(gravityScale);
		}
	}

	public void applyLinearImpulse(float xAxis, float pyAxis) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null) // TODO: Garbage
				lBodyInst.mBody.applyLinearImpulse(new Vec2(xAxis, pyAxis), lBodyInst.mBody.getWorldCenter(), true);
		}
	}

	public void setBodyType(String bodyName, int bodyType) {
		if (bodyType != 0 && bodyType != 1 & bodyType != 2)
			return;

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			lBox2dBodyInstance.bodyTypeIndex = bodyType;

			if (lBox2dBodyInstance.mBody != null) {
				switch (bodyType) {
				case Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC:
					lBox2dBodyInstance.mBody.m_type = BodyType.STATIC;
					break;

				case Box2dBodyDefinition.BODY_TYPE_INDEX_KINEMATIC:
					lBox2dBodyInstance.mBody.m_type = BodyType.KINEMATIC;
					break;

				case Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC:
					lBox2dBodyInstance.mBody.m_type = BodyType.DYNAMIC;
					break;
				}
			}
		}
	}

	public void setAllBodiesAngularDamping(float angularDamping) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			lBox2dBodyInstance.setAngularDamping(angularDamping);
		}
	}

	public void setAllBodiesIsBullet(boolean isBullet) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			lBox2dBodyInstance.setIsBullet(isBullet);
		}
	}

	public void setBodiesIsBullet(String bodyName, boolean isBullet) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			if (lBox2dBodyInstance.name.contentEquals(bodyName)) {
				lBox2dBodyInstance.setIsBullet(isBullet);
			}
		}
	}

	public void setAllFixturesCategory(int newCategory) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];
				if (lFixInst == null)
					continue;

				lFixInst.categoryBits = newCategory;

				if (lFixInst.mFixture != null) {
					lFixInst.mFixture.m_filter.categoryBits = newCategory;
				}
			}
		}
	}

	public void setAllFixturesBitMask(int newBitmask) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];
				if (lFixInst == null)
					continue;

				lFixInst.maskBits = newBitmask;

				if (lFixInst.mFixture != null) {
					lFixInst.mFixture.m_filter.maskBits = newBitmask;
				}
			}
		}
	}

	public void setFixtureDensity(String fixtureName, float density) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				if (lBox2dFixtureInstance.name.contentEquals(fixtureName)) {
					lBox2dFixtureInstance.density = density;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_density = density;
					}
				}
			}
		}
	}

	public void setAllFixtureDensity(float density) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				lBox2dFixtureInstance.density = density;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_density = density;
				}
			}
		}
	}

	public void setFixtureFriction(String fixtureName, float friction) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				if (lBox2dFixtureInstance.name.contentEquals(fixtureName)) {
					lBox2dFixtureInstance.friction = friction;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_friction = friction;
					}
				}
			}
		}
	}

	public void setAllFixtureFriction(float friction) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				lBox2dFixtureInstance.friction = friction;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_friction = friction;
				}
			}
		}
	}

	public void setFixtureRestitution(String fixtureName, float restitution) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				if (lBox2dFixtureInstance.name.contentEquals(fixtureName)) {
					lBox2dFixtureInstance.restitution = restitution;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_restitution = restitution;
					}
				}
			}
		}
	}

	public void setAllFixtureRestitution(float restitution) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				lBox2dFixtureInstance.restitution = restitution;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_restitution = restitution;
				}
			}
		}
	}

	public void setAllFixtureProperties(float friction, float restitution, float density) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				lBox2dFixtureInstance.restitution = restitution;
				lBox2dFixtureInstance.density = density;
				lBox2dFixtureInstance.friction = friction;
			}
		}
	}

	/**
	 * Sets the radius of the named fixture.
	 */
	public void setFixtureRadius(String fixtureName, float newRadiusInPixels) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];

				if (lBox2dFixtureInstance != null && lBox2dFixtureInstance.name != null && lBox2dFixtureInstance.name.contentEquals(fixtureName)) {
					if (lBox2dFixtureInstance.shape != null && lBox2dFixtureInstance.shape instanceof Box2dCircleInstance) {
						((Box2dCircleInstance) lBox2dFixtureInstance.shape).radius = ConstantsPhysics.toUnits(newRadiusInPixels);

						// If the fixture has already been loaded into the Box2dWorld, then set the fixture's radius
						if (lBox2dFixtureInstance.mFixture != null) {
							lBox2dFixtureInstance.mFixture.m_shape.m_radius = ConstantsPhysics.toUnits(newRadiusInPixels);
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the named fixture as a sensor or not
	 */
	public void setFixtureIsSensor(String fixtureName, boolean isSensor) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];

				if (lBox2dFixtureInstance != null) {
					lBox2dFixtureInstance.isSensor = isSensor;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_isSensor = isSensor;
					}
				}
			}
		}
	}

	/**
	 * Sets the width and height of the vertices of the named fixture.
	 */
	public void setFixtureDimensions(String fixtureName, float newWidth, float newHeight) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixInst = lBodyInst.mFixtures[j];

				if (lFixInst != null && lFixInst.name.contentEquals(fixtureName)) {

					lFixInst.unloadPhysics(mWorld);

					if (lFixInst.shape != null && lFixInst.shape instanceof Box2dPolygonInstance) {
						Box2dPolygonInstance lShape = (Box2dPolygonInstance) lFixInst.shape;

						final float lHalfWidth = newWidth / 2f;
						final float lHalfHeight = newHeight / 2f;

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

					else if (lFixInst.shape != null && lFixInst.shape instanceof Box2dCircleInstance) {
						Box2dCircleInstance lShape = (Box2dCircleInstance) lFixInst.shape;
						final float lHalfWidth = newWidth / 2f;
						lShape.radius = lHalfWidth;
					}

					if (lBodyInst.mBody != null)
						lFixInst.loadPhysics(mWorld, lBodyInst.mBody);
				}
			}
		}
	}

}
