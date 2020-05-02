package net.lintford.library.core.box2d.entity;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import net.lintford.library.core.box2d.definition.PObjectDefinition;
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

	private List<Box2dBodyInstance> mBodies = new ArrayList<>();
	private List<Box2dJointInstance> mJoints = new ArrayList<>();

	protected Object userDataObject;
	public String spriteSheetName;
	protected Box2dBodyInstance mMainBody;

	protected boolean mIsFree;
	protected transient boolean mPhysicsLoaded = false;

	protected Vec2 mWorldPosition;
	protected float mWorldRotation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setWorldPosition(float pX, float pY) {
		mWorldPosition.set(pX, pY);
	}

	public void setWorldRotation(float pRotation) {
		mWorldRotation = pRotation;
	}

	public Object userDataObject() {
		return userDataObject;

	}

	public void userDataObject(Object pNewUserDataObject) {
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
		mWorldPosition = new Vec2();

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

	public void loadPhysics(World pWorld) {
		// Go through and create instances for each definition
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInstance = mBodies.get(i);

			lBodyInstance.worldPosition.x = mWorldPosition.x + lBodyInstance.localPosition.x;
			lBodyInstance.worldPosition.y = mWorldPosition.y + lBodyInstance.localPosition.y;
			lBodyInstance.worldAngle = mWorldRotation + lBodyInstance.localAngle;

			lBodyInstance.loadPhysics(pWorld);

		}

		// Need two passes for joints because gear joints reference other joints
		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			Box2dRevoluteInstance lJointInstance = (Box2dRevoluteInstance) mJoints.get(i);
			RevoluteJointDef lJointDef = new RevoluteJointDef();

			final var lBodyA = getBodyByIndex(lJointInstance.bodyAUID);
			if (lBodyA == null)
				continue;
			lJointDef.bodyA = lBodyA.mBody;

			final var lBodyB = getBodyByIndex(lJointInstance.bodyBUID);
			if (lBodyB == null)
				continue;
			lJointDef.bodyB = lBodyB.mBody;

			lJointDef.referenceAngle = lJointInstance.referenceAngle;
			lJointDef.enableLimit = lJointInstance.enableLimit;
			lJointDef.lowerAngle = lJointInstance.lowerAngle;
			lJointDef.upperAngle = lJointInstance.upperAngle;

			lJointDef.enableMotor = lJointInstance.enableMotor;
			lJointDef.motorSpeed = lJointInstance.motorSpeed;
			lJointDef.maxMotorTorque = lJointInstance.maxMotorTorque;

			lJointDef.localAnchorA.set(lJointInstance.localAnchorA);
			lJointDef.localAnchorB.set(lJointInstance.localAnchorB);

			lJointDef.collideConnected = lJointInstance.collidesConnected;

			// DEBUG
			lJointInstance.joint = pWorld.createJoint(lJointDef);

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

		setTransform(mWorldPosition.x, mWorldPosition.y, mWorldRotation);

		// Look for a body named 'MainBody'.
		// This wll will be the origin body for translations etc.

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
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			final var lBox2dBodyInstance = mBodies.get(i);

			if (lBox2dBodyInstance.mBody != null) {
				lBox2dBodyInstance.mBody.setUserData(null);
				lBox2dBodyInstance.unloadPhysics();

			}

		}

		final int lJointCount = mJoints.size();
		for (int i = 0; i < lJointCount; i++) {
			final var lBox2dJointInstance = mJoints.get(i);

			if (lBox2dJointInstance.joint != null) {
				lBox2dJointInstance.unloadPhysics();

			}

		}

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

	public void setTransform(float pX, float pY, float pR) {
		final int lBodyCount = mBodies.size();

		// TODO: check when not tired
		// mWorldPosition.x = pX;
		// mWorldPosition.x = pY;

		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			lBodyInst.worldPosition.set(pX, pY);
			lBodyInst.worldAngle = pR;

			if (lBodyInst.mBody != null) {
				rotatePointAroundPoint(lBodyInst.localPosition.x, lBodyInst.localPosition.y, lBodyInst.localPosition.x, lBodyInst.localPosition.y, pR);

				lBodyInst.mBody.setTransform(new Vec2(pX, pY), pR);

			}

		}

	}

	Vec2 temp = new Vec2();

	private Vec2 rotatePointAroundPoint(float cx, float cy, float px, float py, float angle) {

		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);

		px -= cx;
		py -= cy;

		float xnew = px * c - py * s;
		float ynew = px * s + py * c;

		temp.set(xnew + cx, ynew + cy);
		return temp;
	}

	public void setPosition(float pX, float pY) {
		mWorldPosition.set(pX, pY);

		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

			lBodyInst.worldPosition.set(pX, pY);

			if (lBodyInst.mBody != null) {
				lBodyInst.mBody.setTransform(new Vec2(pX, pY), lBodyInst.mBody.getAngle());

			}

		}

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

	public void setBullet(boolean pNewValue) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null || lBodyInst.mBody == null)
				continue;

			lBodyInst.mBody.setBullet(pNewValue);

		}

	}

	public void setFixtureCategory(int pNewCategory) {
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

			}

		}

	}

	public void setFixtureBitMask(int pNewBitmask) {
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

			}

		}

	}

	public void setFixtureIsSensor(boolean pIsSensor) {
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

				lFixInst.isSensor = pIsSensor;

			}

		}
	}

	public void setAllFixtureDensity(float pNewDensity) {
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

				lFixInst.density = pNewDensity;

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

	public void setAllFixtureFriction(float pNewFrictionValue) {
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

				lFixInst.friction = pNewFrictionValue;

			}

		}

	}

	public void setAllFixtureRestitution(float pNewRestitution) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBox2dBodyInstance = mBodies.get(i);
			if (lBox2dBodyInstance == null)
				continue;

			final int lFixtureCount = lBox2dBodyInstance.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lBox2dFixtureInstance = lBox2dBodyInstance.mFixtures[j];
				if (lBox2dFixtureInstance == null)
					continue;

				lBox2dFixtureInstance.restitution = pNewRestitution;

			}

		}
	}

	// Attempts to set the radius of a fixture shape
	public void setFixtureRadius(String pFixtureName, float pNewRadius) {
		final int lBodyCount = mBodies.size();
		for (int i = 0; i < lBodyCount; i++) {

			Box2dBodyInstance lBodyInst = mBodies.get(i);
			if (lBodyInst == null)
				continue;

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
