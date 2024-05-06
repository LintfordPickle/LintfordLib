package net.lintfordlib.core.physics.dynamics;

import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.shapes.BaseShape;
import net.lintfordlib.core.physics.spatial.PhysicsGridEntity;
import net.lintfordlib.core.physics.spatial.PhysicsHashGrid;

public class RigidBody extends PhysicsGridEntity {

	private static int uidCounter;

	public static int getNewRigidBodyUid() {
		return uidCounter++;
	}

	public static final Vector2f Forward = new Vector2f(1.f, 0.f);
	public static final Vector2f Left = new Vector2f(0.f, -1.f);

	// --------------------------------------
	// Variables
	// --------------------------------------

	public boolean _isActive = true;
	public int _updateCounter = 0;

	private BaseShape mShape;
	private Object userData;

	public final Transform transform = new Transform();

	public float vx;
	public float vy;

	public float accX;
	public float accY;

	public float linearDampingX;
	public float linearDampingY;
	public float angularDamping;

	protected float mass;
	protected float inertia;
	protected float invMass;
	protected float invInertia;

	public float torque;
	public float angularVelocity;

	private int mCategoryBit; // I'm a ..
	private int mMaskBit; // I collide with ...

	private boolean mIsStatic;
	private boolean mIsSensor;

	public boolean debugIsSelected;
	public boolean debugIsColliding;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setAngularVelocity(float angularVelocity) {
		if (isStatic())
			return;

		this.angularVelocity = angularVelocity;
	}

	public void applyAngularVelocity(float angularVelocity) {
		if (isStatic())
			return;

		this.angularVelocity += angularVelocity;
	}

	public float angularVelocity() {
		return angularVelocity;
	}

	public float mass() {
		return mass;
	}

	public float invMass() {
		return invMass;
	}

	public float inertia() {
		return inertia;
	}

	public float invInertia() {
		return invInertia;
	}

//	public List<Vector2f> getLocalVertices() {
//		return mShape.getReadOnlyVertices();
//	}

	public List<Vector2f> getWorldVertices() {
		return mShape.getTransformedVertices(transform);
	}

	public Rectangle aabb() {
		if (mShape == null)
			return null;
		return mShape.aabb(transform);
	}

	public Object userData() {
		return userData;
	}

	public void userData(Object userData) {
		this.userData = userData;
	}

	/***
	 * @returns true if this {@link RigidBody} is a sensor, otherwise false.
	 */
	public boolean isSensor() {
		return mIsSensor;
	}

	/***
	 * Sets the sensor state of this {@link RigidBody}. Sensor bodies still trigger collision callbacks in the narrow phase.
	 * 
	 * @param isSensor The value to set for the isSensor flag.
	 */
	public void isSensor(boolean isSensor) {
		mIsSensor = isSensor;
	}

	public boolean isStatic() {
		return mIsStatic;
	}

	public BaseShape shape() {
		return mShape;
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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RigidBody(boolean isStatic) {
		this(isStatic, 0, 0, 0);
	}

	public RigidBody(boolean isStatic, float unitPositionX, float unitPositionY, float angle) {
		super(getNewRigidBodyUid());

		this.mIsStatic = isStatic;

		this.linearDampingX = 1.f;
		this.linearDampingY = 1.f;
		this.angularDamping = 1.f;

		this.transform.p.set(unitPositionX, unitPositionY);
		this.transform.setAngle(angle);
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

		transform.p.x += vx * time;
		transform.p.y += vy * time;

		// TODO: Check the linear damping
		// vx *= 1.f / (1.f + time * linearDampingX);
		// vy *= 1.f / (1.f + time * linearDampingY);

		vx *= linearDampingX;
		vy *= linearDampingY;

		transform.setAngle(transform.angle + angularVelocity * time);

		accX = 0.f;
		accY = 0.f;
		torque = 0.f;

		angularVelocity *= (float) Math.exp(-.97f * time);
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	public void addShape(BaseShape shape) {
		this.mShape = shape;

		resetMassData();
	}

	private void resetMassData() {
		mass = 0.f;
		invMass = 0.f;
		inertia = 0.f;
		invInertia = 0.f;

		if (isStatic()) {
			return;
		}

		mass += shape().mass();
		inertia += shape().inertia();

		if (mass > 0.f) {
			invMass = 1.f / mass;
		}

		if (inertia > 0.f) {
			invInertia = 1.f / inertia;
		}
	}

	public void moveTo(float x, float y) {
		transform.setPosition(x, y);
	}

	public void move(float x, float y) {
		transform.p.x += x;
		transform.p.y += y;
	}

	public void angle(float a) {
		this.transform.setAngle(a);
	}

	// -- Impulses: Change a body's velocity / angular velocity immediately

	public void addAngularImpulse(float ai) {
		angularVelocity += ai * invMass;
	}

	public void addImpulse(float ix, float iy) {
		vx += ix * invMass;
		vy += iy * invMass;
	}

	public void addImpulse(float ix, float iy, float px, float py) {
		vx += ix * invMass;
		vy += iy * invMass;

		angularVelocity += invMass * Vector2f.cross(px - transform.p.x, py - transform.p.y, ix, iy);
	}

	// -- Forces: act gradually over time (should be applied as needed per update)

	public void addTorque(float torque) {
		torque += torque;
	}

	public void addForce(float fx, float fy) {
		accX += fx * invMass;
		accY += fy * invMass;
	}

	public void addForceAtPoint(float fx, float fy, float px, float py) {
		accX += fx * invMass;
		accY += fy * invMass;

		torque += Vector2f.cross(px - transform.p.x, py - transform.p.y, fx, fy);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void fillEntityBounds(PhysicsHashGrid<?> grid) {
		final var aabb = aabb();

		minUnitX = grid.getColumnAtX(aabb.left());
		minUnitY = grid.getRowAtY(aabb.top());

		maxUnitX = grid.getColumnAtX(aabb.right());
		maxUnitY = grid.getRowAtY(aabb.bottom());
	}

	@Override
	public boolean isGridCacheOld(PhysicsHashGrid<?> grid) {
		final var aabb = aabb();

		final var newMinX = grid.getColumnAtX(aabb.left());
		final var newMinY = grid.getRowAtY(aabb.top());

		final var newMaxX = grid.getColumnAtX(aabb.right());
		final var newMaxY = grid.getRowAtY(aabb.bottom());

		if (newMinX == minUnitX && newMinY == minUnitY && newMaxX == maxUnitX && newMaxY == maxUnitY)
			return false; // early out

		return true;
	}

}
