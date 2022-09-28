package net.lintford.library.core.box2d.instance;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.box2d.definition.Box2dBodyDefinition;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.instances.IndexedPooledBaseData;

public class Box2dBodyInstance extends IndexedPooledBaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -7217114568668036265L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public transient Body mBody;
	public IndexedPooledBaseData bodyPhysicsData;

	public String name;
	public int entityBodyIndex;
	public int bodyTypeIndex;
	public final Vec2 massCenter;
	public final Vec2 objectPositionInUnits;
	public final Vec2 linearVelocity;
	public float objectAngleInRadians;
	public float angularVelocity;
	public float linearDamping;
	public float angularDamping;
	public float gravityScale = 1f;
	public float mass;
	public float massI;
	public boolean allowSleep = true;
	public boolean awake = true;
	public boolean fixedRotation = false;
	public boolean bullet = true;
	public boolean active = true;
	public Box2dFixtureInstance[] mFixtures;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public Box2dBodyInstance(int poolUid) {
		super(poolUid);

		objectPositionInUnits = new Vec2();
		linearVelocity = new Vec2();
		massCenter = new Vec2();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void savePhysics(JBox2dEntityInstance parentInst) {
		if (mBody == null)
			return; // nothing to save

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

		float lParentAngle = 0.f;
		if (parentInst != null)
			lParentAngle = parentInst.entityAngle();

		this.objectAngleInRadians = mBody.getAngle() - lParentAngle;

		this.linearVelocity.set(mBody.getLinearVelocity());
		this.angularVelocity = mBody.getAngularVelocity();
		this.linearDamping = mBody.getLinearDamping();
		this.angularDamping = mBody.getAngularDamping();
		this.gravityScale = mBody.getGravityScale();

		this.allowSleep = mBody.isSleepingAllowed();
		this.awake = mBody.isAwake();
		this.fixedRotation = mBody.isFixedRotation();
		this.bullet = mBody.isBullet();
		this.active = mBody.isActive();

		final int lFixtureCount = mFixtures.length;
		for (int j = 0; j < lFixtureCount; j++) {
			final var lFixtureInstance = mFixtures[j];

			if (lFixtureInstance != null)
				lFixtureInstance.savePhysics();
		}
	}

	public void loadPhysics(World box2dWorld, JBox2dEntityInstance parentInst) {
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

		float lParentPositionX = 0.f;
		float lParentPositionY = 0.f;
		float lParentAngle = 0.f;
		if (parentInst != null) {
			lParentPositionX = parentInst.entityPosition().x;
			lParentPositionY = parentInst.entityPosition().y;
			lParentAngle = parentInst.entityAngle();
		}

		lBodyDef.position.x = lParentPositionX + objectPositionInUnits.x;
		lBodyDef.position.y = lParentPositionY + objectPositionInUnits.y;
		lBodyDef.angle = lParentAngle + objectAngleInRadians;

		lBodyDef.linearVelocity.x = linearVelocity.x;
		lBodyDef.linearVelocity.y = linearVelocity.y;
		lBodyDef.angularVelocity = angularVelocity;
		lBodyDef.linearDamping = linearDamping;
		lBodyDef.angularDamping = angularDamping;
		lBodyDef.gravityScale = gravityScale;

		lBodyDef.allowSleep = false;
		lBodyDef.awake = true;
		lBodyDef.fixedRotation = fixedRotation;
		lBodyDef.bullet = bullet;
		lBodyDef.active = true;

		mBody = box2dWorld.createBody(lBodyDef);

		if (bodyPhysicsData != null)
			mBody.setUserData(bodyPhysicsData);

		final int lFixtureCount = mFixtures.length;
		for (int i = 0; i < lFixtureCount; i++) {
			final var lFixtureInstance = mFixtures[i];
			if (lFixtureInstance != null)
				lFixtureInstance.loadPhysics(box2dWorld, mBody);
		}
	}

	public void unloadPhysics() {
		if (mBody == null)
			return;

		final var lBox2dWorld = mBody.m_world;
		if (lBox2dWorld == null)
			return;

		// Destroy all fixtures on this body
		final int lFixtureCount = mFixtures.length;
		for (int i = 0; i < lFixtureCount; i++) {
			final var lBox2dFixtureInstance = mFixtures[i];
			if (lBox2dFixtureInstance.mFixture != null)
				mBody.destroyFixture(lBox2dFixtureInstance.mFixture);
			lBox2dFixtureInstance.mFixture = null;
		}

		if (mBody.getUserData() != null) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "JBox2dBodyInstance was unloaded without removing the userdata. Typeof (" + mBody.getUserData().toString() + ")");
			mBody.setUserData(null);
		}

		lBox2dWorld.destroyBody(mBody);
		mBody = null;
	}

	public void update(LintfordCore core) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setActive(boolean isActive) {
		active = isActive;

		if (mBody != null) {
			mBody.setActive(isActive);
		}
	}

	public void setFixedRotation(boolean isFixedRotation) {
		fixedRotation = isFixedRotation;

		if (mBody != null) {
			mBody.setFixedRotation(isFixedRotation);
		}
	}

	public void setAngularDamping(float newValue) {
		angularDamping = newValue;

		if (mBody != null) {
			mBody.setAngularDamping(newValue);
		}
	}

	public void setIsBullet(boolean iIsBullet) {
		bullet = iIsBullet;

		if (mBody != null) {
			mBody.setBullet(iIsBullet);
		}
	}
}
