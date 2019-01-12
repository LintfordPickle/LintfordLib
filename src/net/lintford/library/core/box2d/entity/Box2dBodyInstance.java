package net.lintford.library.core.box2d.entity;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.data.BaseData;

public class Box2dBodyInstance extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7217114568668036265L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient Body mBody;

	public String name;
	public int uid;
	public int bodyTypeIndex;
	public Vec2 position;
	public float angle;
	public Vec2 linearVelocity;
	public float angularVelocity;
	public float linearDamping;
	public float angularDamping;
	public float gravityScale;

	public boolean allowSleep;
	public boolean awake;
	public boolean fixedRotation;
	public boolean bullet;
	public boolean active;

	// Mass data
	public float mass;
	public Vec2 massCenter;
	public float massI;

	public Box2dFixtureInstance[] mFixtures;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dBodyInstance() {
		position = new Vec2();
		linearVelocity = new Vec2();
		massCenter = new Vec2();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void savePhysics() {
		// Get the state information so it can be serialized
		switch (mBody.m_type) {
		case STATIC:
			this.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC;
			break;
		case KINEMATIC:
			this.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_KINEMATIC;
			break;
		case DYNAMIC:
			this.bodyTypeIndex = Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC;
			break;
		}

		this.position.set(mBody.getPosition());
		this.linearVelocity.set(mBody.getLinearVelocity());

		this.angle = mBody.getAngle();
		this.angularVelocity = mBody.getAngularVelocity();
		this.linearDamping = mBody.getLinearDamping();
		this.angularDamping = mBody.getAngularDamping();
		this.gravityScale = mBody.getGravityScale();

		this.allowSleep = mBody.isSleepingAllowed();
		this.awake = mBody.isAwake();
		this.fixedRotation = mBody.isFixedRotation();
		this.bullet = mBody.isBullet();
		this.active = mBody.isActive();

		// iterate over the fixtures
		final int lFixtureCount = mFixtures.length;
		for (int j = 0; j < lFixtureCount; j++) {
			Box2dFixtureInstance lFixtureInstance = mFixtures[j];

			lFixtureInstance.savePhysics();

		}

	}

	public void loadPhysics(World pWorld) {
		BodyDef lBodyDef = new BodyDef();

		switch (bodyTypeIndex) {
		case Box2dBodyDefinition.BODY_TYPE_INDEX_STATIC:
			lBodyDef.type = BodyType.STATIC;
			break;
		case Box2dBodyDefinition.BODY_TYPE_INDEX_KINEMATIC:
			lBodyDef.type = BodyType.KINEMATIC;
			break;
		case Box2dBodyDefinition.BODY_TYPE_INDEX_DYNAMIC:
			lBodyDef.type = BodyType.DYNAMIC;
			break;
		}

		lBodyDef.setPosition(position);
		lBodyDef.setLinearVelocity(linearVelocity);

		lBodyDef.angle = angle;
		lBodyDef.angularVelocity = angularVelocity;
		lBodyDef.linearDamping = linearDamping;
		lBodyDef.angularDamping = angularDamping;
		lBodyDef.gravityScale = gravityScale;

		lBodyDef.allowSleep = allowSleep;
		lBodyDef.awake = awake;
		lBodyDef.fixedRotation = fixedRotation;
		lBodyDef.bullet = bullet;
		lBodyDef.active = active;

//		lBodyDef.mass = lBodyDef.mass;
//		lBodyDef.massCenter.x = lBodyDef.massCenter.x;
//		lBodyDef.massCenter.y = lBodyDef.massCenter.y;
//		lBodyDef.massI = lBodyDef.massI;

		mBody = pWorld.createBody(lBodyDef);

		// iterate over the fixtures
		final int lFixtureCount = mFixtures.length;
		for (int j = 0; j < lFixtureCount; j++) {
			Box2dFixtureInstance lFixtureInstance = mFixtures[j];

			lFixtureInstance.loadPhysics(pWorld, mBody);

		}
	}

	public void update(LintfordCore pCore) {

	}

}
