package net.lintfordlib.core.physics.dynamics;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.entities.Entity;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.definitions.BodyDefinition;

public class RigidBody extends Entity {

	private static int uidCounter;

	public static int getNewRigidBodyUid() {
		return uidCounter++;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: implement body sleeping
	public boolean _isActive = true;
	public int _updateCounter = 0;

	private final PhysicsWorld physicsWorld;
	public final Transform transform = new Transform();
	private final Rectangle mAABB = new Rectangle();
	private Object userData;

	public List<Fixture> fixtures = new ArrayList<>();

	public float vx;
	public float vy;

	public float accX;
	public float accY;

	public float linearDampingX;
	public float linearDampingY;

	public float torque;

	public float angularVelocity;

	private float mMass;
	private float mInvMass;

	private float mInertia;
	private float mInvInertia;

	private boolean mIsStatic;

	public float width;
	public float height;

	private boolean mManualIsDirty;

	private int mCategoryBit; // I'm a ..
	private int mMaskBit; // I collide with ...

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Rectangle aabb() {
		updateAABB();
		return mAABB;
	}

	/** I collide with */
	public int maskBits() {
		return mMaskBit;
	}

	/** I collide with */
	public void maskBits(int maskBits) {
		mMaskBit = maskBits;
	}

	/** I'm a */
	public int categoryBits() {
		return mCategoryBit;
	}

	/** I'm a */
	public void categoryBits(int categoryBits) {
		mCategoryBit = categoryBits;
	}

	public Object userData() {
		return userData;
	}

	public void userData(Object userData) {
		this.userData = userData;
	}

	public boolean isManualDirty() {
		return mManualIsDirty;
	}

	public void setManualDirty() {
		mManualIsDirty = true;
	}

	public boolean isStatic() {
		return mIsStatic;
	}

	public float mass() {
		return mMass;
	}

	public float invMass() {
		return mInvMass;
	}

	public float inertia() {
		return mInertia;
	}

	public float invInertia() {
		return mInvInertia;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RigidBody(PhysicsWorld physicsWorld, int uid, BodyDefinition def) {
		super(uid);

		this.physicsWorld = physicsWorld;
		this.mIsStatic = def.isStatic;

		this.transform.position.x = def.position.x;
		this.transform.position.y = def.position.y;
		this.transform.rotation.set(def.angle);

		this.vx = def.linearVelocity.x;
		this.vy = def.linearVelocity.y;

		this.linearDampingX = def.linearDamping;
		this.linearDampingY = def.linearDamping;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void step(float time, float gravityX, float gravityY) {
		if (isStatic())
			return;

		vx += accX * time;
		vy += accY * time;
		angularVelocity += torque * time;

		vx += gravityX * time;
		vy += gravityY * time;

		transform.position.x += vx * time;
		transform.position.y += vy * time;
		transform.rotation.set(gravityY);

		vx *= linearDampingX;
		vy *= linearDampingY;

		accX = 0.f;
		accY = 0.f;
		torque = 0.f;

		angularVelocity *= (float) Math.exp(-.97f * time);

		setManualDirty();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void angle(float a) {
		this.angle = a;
	}

	public void addForce(float fx, float fy) {
		accY += fy * invMass();
		accX += fx * invMass();
	}

	public void addForceAtPoint(float fx, float fy, float px, float py) {
		accY += fy * invMass();
		accX += fx * invMass();

		torque += Vector2f.cross(px - x, py - y, fx, fy);
	}

	public void resetMassData() {
		// Compute the mass from the fixtures - each fixture has its own density
		mMass = 0.f;
		mInvMass = 0.f;
		mInertia = 0.f;
		mInvInertia = 0.f;

		if (mIsStatic) {
			return;
		}

		// Accumalate mass over all fixtures
		float localCenterX = 0.f;
		float localCenterY = 0.f;

		final var lNumFixtures = fixtures.size();
		for (int i = 0; i < lNumFixtures; i++) {
			final var lFixture = fixtures.get(i);

			final var lMassData = lFixture.massData;
			mMass += lMassData.mass;
			localCenterX += lMassData.mass * lMassData.center.x;
			localCenterY += lMassData.mass * lMassData.center.y;

			mInertia += lMassData.inertia;
		}

		// compute the center of mass
		if (mMass > 0.f) {
			mInvMass = 1.f / mMass;

			localCenterX *= mInvMass;
			localCenterY *= mInvMass;
		}

		if (mInertia > 0.f /* fixed rotation != false */) {
			// center the inertia about the center of mass
			mInertia -= mMass * (localCenterX * localCenterX + localCenterY * localCenterY);
			mInvInertia = 1.f / mInertia;
		} else {
			mInertia = 0.f;
			mInvInertia = 0.f;
		}
	}

	// TODO: Cache the area (only changes when fixture count changes)
	public float calculateArea() {
		float totalArea = 0.f;

		final int lNumFixtures = fixtures.size();
		for (int i = 0; i < lNumFixtures; i++) {
			totalArea += fixtures.get(i).area();
		}

		return totalArea;
	}

	public static RigidBody createRigidBody(PhysicsWorld physicsWorld, BodyDefinition definition) {
		return new RigidBody(physicsWorld, getNewRigidBodyUid(), definition);
	}

	public void addFixture(Fixture fixture) {
		if (fixtures.contains(fixture))
			return;

		final var hashGrid = physicsWorld.grid();
		hashGrid.addEntity(fixture);
		fixtures.add(fixture);

		resetMassData();
	}

	private void updateAABB() {
		float l = Float.MAX_VALUE;
		float r = -Float.MAX_VALUE;
		float t = Float.MAX_VALUE;
		float b = -Float.MAX_VALUE;

		final int lNumFixtures = fixtures.size();
		for (int i = 0; i < lNumFixtures; i++) {
			final var lFixture = fixtures.get(i);
			final var lFAABB = lFixture.aabb();
			if (lFAABB.left() < l)
				l = lFAABB.left();

			if (lFAABB.right() > r)
				r = lFAABB.right();

			if (lFAABB.top() < t)
				t = lFAABB.top();

			if (lFAABB.bottom() > b)
				b = lFAABB.bottom();

		}

		mAABB.set(l, t, (r - l), (b - t));
	}
}
