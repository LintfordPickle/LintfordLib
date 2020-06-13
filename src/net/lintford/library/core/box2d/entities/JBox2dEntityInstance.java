package net.lintford.library.core.box2d.entities;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.box2d.instance.Box2dCircleInstance;
import net.lintford.library.core.box2d.instance.Box2dFixtureInstance;
import net.lintford.library.core.box2d.instance.Box2dJointInstance;
import net.lintford.library.core.box2d.instance.Box2dPolygonInstance;
import net.lintford.library.core.box2d.instance.Box2dRevoluteInstance;
import net.lintford.library.core.entity.PooledBaseData;

/**
 * The {@link JBox2dEntityInstance} class can be loaded from a PObject file and then serialized and restored with the game. JBox2dEntitys can also be pooled in the PObjectManager.
 */
public class JBox2dEntityInstance extends PooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 5280466036279609596L;

	public static final String MAIN_BODY_NAME = "MainBody";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected transient PObjectDefinition mPObjectDefinition;

	protected List<Box2dBodyInstance> mBodies = new ArrayList<>();
	protected List<Box2dJointInstance> mJoints = new ArrayList<>();

	protected int userDataObjectPoolUid = -1;;
	protected transient Object userDataObject;
	protected Box2dBodyInstance mMainBody;

	protected boolean mIsFree;
	protected transient World mWorld;
	protected transient boolean mPhysicsLoaded = false;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Object userDataObject() {
		return userDataObject;

	}

	public void userDataObject(PooledBaseData pNewUserDataObject) {
		if (pNewUserDataObject == null) {
			userDataObjectPoolUid = -1;
			userDataObject = null;
			if (mPhysicsLoaded) {
				mainBody().mBody.setUserData(null);

			}

			return;

		}

		userDataObjectPoolUid = pNewUserDataObject.poolUid;
		userDataObject = pNewUserDataObject;

		if (mPhysicsLoaded) {
			mainBody().mBody.setUserData(userDataObject);

		}

	}

	@Override
	public boolean isAssigned() {
		return mIsFree;
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

	public JBox2dEntityInstance(final int pPoolUid) {
		super(pPoolUid);

		mPhysicsLoaded = false;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		super.initialize(pParent);

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			mBodies.get(i).initialize(this);

		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			mJoints.get(i).initialize(this);

		}

	}

	/** Loads a reference implementation of the PObject definition */
	public void loadPhysics(World pWorld) {
		if (pWorld == null)
			return;
		mWorld = pWorld;

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);

			lBox2dBodyInstance.worldPosition.x = ConstantsPhysics.toPixels(lBox2dBodyInstance.localPosition.x);
			lBox2dBodyInstance.worldPosition.y = ConstantsPhysics.toPixels(lBox2dBodyInstance.localPosition.y);
			lBox2dBodyInstance.worldAngle = lBox2dBodyInstance.localAngle;

			lBox2dBodyInstance.loadPhysics(pWorld);

		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dRevoluteInstance = (Box2dRevoluteInstance) mJoints.get(i);
			final var lRevoluteJointDef = new RevoluteJointDef();

			final var lBodyA = getBodyByIndex(lBox2dRevoluteInstance.bodyAUID);
			if (lBodyA == null)
				continue;
			lRevoluteJointDef.bodyA = lBodyA.mBody;

			final var lBodyB = getBodyByIndex(lBox2dRevoluteInstance.bodyBUID);
			if (lBodyB == null)
				continue;
			lRevoluteJointDef.bodyB = lBodyB.mBody;

			lRevoluteJointDef.referenceAngle = lBox2dRevoluteInstance.referenceAngle;
			lRevoluteJointDef.enableLimit = lBox2dRevoluteInstance.enableLimit;
			lRevoluteJointDef.lowerAngle = lBox2dRevoluteInstance.lowerAngle;
			lRevoluteJointDef.upperAngle = lBox2dRevoluteInstance.upperAngle;

			lRevoluteJointDef.enableMotor = lBox2dRevoluteInstance.enableMotor;
			lRevoluteJointDef.motorSpeed = lBox2dRevoluteInstance.motorSpeed;
			lRevoluteJointDef.maxMotorTorque = lBox2dRevoluteInstance.maxMotorTorque;

			lRevoluteJointDef.localAnchorA.set(lBox2dRevoluteInstance.localAnchorA);
			lRevoluteJointDef.localAnchorB.set(lBox2dRevoluteInstance.localAnchorB);

			lRevoluteJointDef.collideConnected = lBox2dRevoluteInstance.collidesConnected;

			lBox2dRevoluteInstance.joint = pWorld.createJoint(lRevoluteJointDef);

		}

		// Resolve the main body
		mMainBody = getBodyByName(MAIN_BODY_NAME);
		if (mMainBody == null) {
			mMainBody = getBodyByIndex(0);
		}

		if (userDataObject != null && mainBody() != null) {
			if (mainBody().mBody != null)
				mainBody().mBody.setUserData(userDataObject);

		}

		mPhysicsLoaded = true;

	}

	public Box2dBodyInstance getBodyByName(String pBodyName) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBody = mBodies.get(i);
			if (lBody == null || lBody.name == null) {
				continue;
			}

			if (lBody.name.contentEquals(pBodyName))
				return mBodies.get(i);

		}

		return null;

	}

	public Box2dBodyInstance getBodyByIndex(int pArrayIndex) {
		if (pArrayIndex >= mBodies.size()) {
			return null;
		}

		return mBodies.get(pArrayIndex);
	}

	public Box2dJointInstance getJointByName(String pJointName) {
		final int lNumJoints = mJoints.size();
		for (int i = 0; i < lNumJoints; i++) {
			if (mJoints.get(i).name.contentEquals(pJointName))
				return mJoints.get(i);

		}

		return null;

	}

	public Box2dJointInstance getJointByIndex(int pArrayIndex) {
		return mJoints.get(pArrayIndex);
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

		mBodies.clear();

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointInstance = mJoints.get(i);

			lBox2dJointInstance.unloadPhysics(mWorld);

		}

		mJoints.clear();

		mWorld = null;
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
		loadBodiesFromDefinition(mPObjectDefinition);
		loadJointsFromDefinition(mPObjectDefinition);

	}

	private void loadBodiesFromDefinition(PObjectDefinition pDefinition) {
		final int lBodyCount = pDefinition.bodies().size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyDefinition = pDefinition.bodies().get(i);
			//TODO: The Box2dBodyInstances and Box2dJointInstances should be taken from a pool (and returned there after use).
			final var lBox2dBodyInstance = new Box2dBodyInstance();

			lBox2dBodyInstance.name = lBox2dBodyDefinition.name;
			lBox2dBodyInstance.bodyTypeIndex = lBox2dBodyDefinition.bodyTypeIndex;

			lBox2dBodyInstance.localPosition.x = lBox2dBodyDefinition.bodyDefinition.position.x;
			lBox2dBodyInstance.localPosition.y = lBox2dBodyDefinition.bodyDefinition.position.y;

			lBox2dBodyInstance.linearVelocity.x = lBox2dBodyDefinition.bodyDefinition.linearVelocity.x;
			lBox2dBodyInstance.linearVelocity.y = lBox2dBodyDefinition.bodyDefinition.linearVelocity.y;

			lBox2dBodyInstance.localAngle = lBox2dBodyDefinition.bodyDefinition.angle;
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
				final var lBox2dFixtureInstance = new Box2dFixtureInstance(lBox2dBodyInstance);

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

	private void loadJointsFromDefinition(PObjectDefinition pDefinition) {
		final int lJointCount = pDefinition.joints().size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointDefinition = pDefinition.joints().get(i);

			if (lBox2dJointDefinition.jointDef instanceof RevoluteJointDef) {
				final var lBox2dJointInstance = new Box2dRevoluteInstance();
				final var lJointDefinition = (RevoluteJointDef) lBox2dJointDefinition.jointDef;

				lBox2dJointInstance.name = lBox2dJointDefinition.name;

				lBox2dJointInstance.bodyAUID = lBox2dJointDefinition.bodyAIndex;
				lBox2dJointInstance.bodyBUID = lBox2dJointDefinition.bodyBIndex;

				lBox2dJointInstance.localAnchorA.x = lJointDefinition.localAnchorA.x;
				lBox2dJointInstance.localAnchorA.y = lJointDefinition.localAnchorA.y;
				lBox2dJointInstance.localAnchorB.x = lJointDefinition.localAnchorB.x;
				lBox2dJointInstance.localAnchorB.y = lJointDefinition.localAnchorB.y;
				lBox2dJointInstance.bodyBUID = lBox2dJointDefinition.bodyBIndex;
				lBox2dJointInstance.referenceAngle = lJointDefinition.referenceAngle;

				lBox2dJointInstance.enableLimit = lJointDefinition.enableLimit;
				lBox2dJointInstance.lowerAngle = lJointDefinition.lowerAngle;
				lBox2dJointInstance.upperAngle = lJointDefinition.upperAngle;

				lBox2dJointInstance.enableMotor = lJointDefinition.enableMotor;
				lBox2dJointInstance.maxMotorTorque = lJointDefinition.maxMotorTorque;
				lBox2dJointInstance.motorSpeed = lJointDefinition.motorSpeed;

				mJoints.add(lBox2dJointInstance);

			}

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

	public void resetEntityInstance() {
		final int lBodyCount = mBodies.size();

		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			if (lBox2dBodyInstance.mBody != null) {
				final var lBox2dPosition = lBox2dBodyInstance.mBody.getPosition();

				lBox2dPosition.x = lBox2dBodyInstance.localPosition.x;
				lBox2dPosition.y = lBox2dBodyInstance.localPosition.y;
				float lLocalAngle = lBox2dBodyInstance.localAngle;

				lBox2dBodyInstance.mBody.setTransform(lBox2dPosition, lLocalAngle);

			}

		}

	}

	/**
	 * Tranforms the PObject as a whole to the desired position with the desired angle. First the the PObject is scaled, then rotations are applied, finally entire object is translated into final position.
	 * 
	 * @param pDesiredWorldX            The final absolute world position X.
	 * @param pDesiredWorldY            The final absolute world position Y.
	 * @param pDesiredRotationInRadians The rotation angle (in radians) to rotate the PObject.
	 */
	public void transformEntityInstance(float pDesiredWorldX, float pDesiredWorldY, float pDesiredRotationInRadians) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			transformBox2dBodyInstance(mBodies.get(i), pDesiredWorldX, pDesiredWorldY, pDesiredRotationInRadians);

		}

	}

	public void transformEntityInstance(float pDesiredWorldX, float pDesiredWorldY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			transformBox2dBodyInstance(lBox2dBodyInstance, pDesiredWorldX, pDesiredWorldY, lBox2dBodyInstance.localAngle);

		}

	}

	private void transformBox2dBodyInstance(final Box2dBodyInstance pBox2dBodyInstance, float pDesiredWorldX, float pDesiredWorldY, float pDesiredRotationInRadians) {
		if (pBox2dBodyInstance == null)
			return;

		if (pBox2dBodyInstance.mBody != null) {
			final var lBox2dPosition = pBox2dBodyInstance.mBody.getPosition();

			// translate bodies back to origin positions
			lBox2dPosition.x = pBox2dBodyInstance.localPosition.x;
			lBox2dPosition.y = pBox2dBodyInstance.localPosition.y;

			// rotate around origin
			rotationAroundOrigin(lBox2dPosition, pDesiredRotationInRadians);

			// translate out
			lBox2dPosition.x += ConstantsPhysics.toUnits(pDesiredWorldX);
			lBox2dPosition.y += ConstantsPhysics.toUnits(pDesiredWorldY);

			pBox2dBodyInstance.mBody.setTransform(lBox2dPosition, pDesiredRotationInRadians);

		}
	}

	public void rotationAroundOrigin(Vec2 pPosition, float pAngleInRadians) {
		float sin = (float) (Math.sin(pAngleInRadians));
		float cos = (float) (Math.cos(pAngleInRadians));

		float lNewX = (pPosition.x) * cos - (pPosition.y) * sin;
		float lNewY = (pPosition.x) * sin + (pPosition.y) * cos;

		pPosition.x = lNewX;
		pPosition.y = lNewY;

	}

	public void setLinearVelocity(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			lBodyInst.linearVelocity.set(pX, pY);

			if (lBodyInst.mBody != null) {
				lBodyInst.mBody.setLinearVelocity(new Vec2(pX, pY));

			}

		}

	}

	public void applyForce(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.applyForce(new Vec2(pX, pY), lBodyInst.mBody.getWorldCenter());

		}

	}

	public void setActive(boolean pNewValue) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.setActive(pNewValue);

		}

	}

	public void setGravityScale(float pNewValue) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null)
				lBodyInst.mBody.setGravityScale(pNewValue);

		}

	}

	public void applyLinearImpulse(float pX, float pY) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst != null) // TODO: Garbage
				lBodyInst.mBody.applyLinearImpulse(new Vec2(pX, pY), lBodyInst.mBody.getWorldCenter(), true);

		}

	}

	public void setBodyType(String pBodyName, int pBodyType) {
		if (pBodyType != 0 && pBodyType != 1 & pBodyType != 2)
			return;

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			lBox2dBodyInstance.bodyTypeIndex = pBodyType;

			if (lBox2dBodyInstance.mBody != null) {

				switch (pBodyType) {
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

	public void setAllBodiesIsBullet(boolean pNewValue) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			lBox2dBodyInstance.setIsBullet(pNewValue);

		}

	}

	public void setBodiesIsBullet(String pBodyName, boolean pNewValue) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			if (lBox2dBodyInstance.name.contentEquals(pBodyName)) {
				lBox2dBodyInstance.setIsBullet(pNewValue);

			}

		}

	}

	public void setAllFixturesCategory(int pNewCategory) {
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

				lFixInst.categoryBits = pNewCategory;

				// If the fixture is already loaded, then set it directly
				if (lFixInst.mFixture != null) {
					lFixInst.mFixture.m_filter.categoryBits = pNewCategory;
				}

			}

		}

	}

	public void setAllFixturesBitMask(int pNewBitmask) {
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

				lFixInst.maskBits = pNewBitmask;

				// If the fixture is already loaded, then set it directly
				if (lFixInst.mFixture != null) {
					lFixInst.mFixture.m_filter.maskBits = pNewBitmask;
				}

			}

		}

	}

	public void setFixtureDensity(String pFixtureName, float pNewDensity) {
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

				if (lBox2dFixtureInstance.name.contentEquals(pFixtureName)) {
					lBox2dFixtureInstance.density = pNewDensity;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_density = pNewDensity;

					}

				}

			}

		}

	}

	public void setAllFixtureDensity(float pNewDensity) {
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

				lBox2dFixtureInstance.density = pNewDensity;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_density = pNewDensity;

				}

			}

		}
	}

	public void setFixtureFriction(String pFixtureName, float pNewFrictionValue) {
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

				if (lBox2dFixtureInstance.name.contentEquals(pFixtureName)) {
					lBox2dFixtureInstance.friction = pNewFrictionValue;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_friction = pNewFrictionValue;

					}

				}

			}

		}

	}

	public void setAllFixtureFriction(float pNewFrictionValue) {
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

				lBox2dFixtureInstance.friction = pNewFrictionValue;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_friction = pNewFrictionValue;

				}

			}

		}

	}

	public void setFixtureRestitution(String pFixtureName, float pNewRestitution) {
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

				if (lBox2dFixtureInstance.name.contentEquals(pFixtureName)) {
					lBox2dFixtureInstance.restitution = pNewRestitution;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_restitution = pNewRestitution;

					}

				}

			}

		}

	}

	public void setAllFixtureRestitution(float pNewRestitution) {
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

				lBox2dFixtureInstance.restitution = pNewRestitution;

				if (lBox2dFixtureInstance.mFixture != null) {
					lBox2dFixtureInstance.mFixture.m_restitution = pNewRestitution;

				}

			}

		}

	}

	public void setAllFixtureProperties(float pNewFriction, float pNewRestitution, float pNewDensity) {
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

				lBox2dFixtureInstance.restitution = pNewRestitution;
				lBox2dFixtureInstance.density = pNewDensity;
				lBox2dFixtureInstance.friction = pNewFriction;

			}

		}

	}

	/**
	 * Sets the radius of the named fixture.
	 */
	public void setFixtureRadius(String pFixtureName, float pNewRadius) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];

				if (lBox2dFixtureInstance != null && lBox2dFixtureInstance.name != null && lBox2dFixtureInstance.name.contentEquals(pFixtureName)) {
					if (lBox2dFixtureInstance.shape != null && lBox2dFixtureInstance.shape instanceof Box2dCircleInstance) {
						((Box2dCircleInstance) lBox2dFixtureInstance.shape).radius = pNewRadius;

					}

				}

			}

		}

	}

	/**
	 * Sets the named fixture as a sensor or not
	 */
	public void setFixtureIsSensor(String pFixtureName, boolean pIsSensor) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			final var lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];

				if (lBox2dFixtureInstance != null) {
					lBox2dFixtureInstance.isSensor = pIsSensor;

					if (lBox2dFixtureInstance.mFixture != null) {
						lBox2dFixtureInstance.mFixture.m_isSensor = pIsSensor;

					}

				}

			}

		}

	}

	/**
	 * Sets the width and height of the vertices of the named fixture.
	 */
	public void setFixtureDimensions(String pFixtureName, float pNewWidth, float pNewHeight) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

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

					else if (lFixInst.shape != null && lFixInst.shape instanceof Box2dCircleInstance) {
						Box2dCircleInstance lShape = (Box2dCircleInstance) lFixInst.shape;

						final float lHalfWidth = pNewWidth / 2f;

						// Set the dimensions of this circle
						lShape.radius = lHalfWidth;

					}

				}

			}

		}

	}

}
