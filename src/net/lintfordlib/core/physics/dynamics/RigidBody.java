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

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: implement body sleeping
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
	private float angularVelocity;

	private int mCategoryBit; // I'm a ..
	private int mMaskBit; // I collide with ...

	private boolean mIsStatic;

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

	public List<Vector2f> getLocalVertices() {
		return mShape.getVertices();
	}

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

		vx *= linearDampingX;
		vy *= linearDampingY;

		transform.setAngle(transform.angle + angularVelocity * time);

		accX = 0.f;
		accY = 0.f;
		torque = 0.f;

		// angularVelocity *= (float) Math.exp(-.97f * time);
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	public void addShape(BaseShape shape) {
		this.mShape = shape;

		resetMassData();
	}

	private void resetMassData() {
		// compute mass data from shapes. Each shape has its own density.

		mass = 0.f;
		invMass = 0.f;
		inertia = 0.f;
		invInertia = 0.f;

		if (isStatic()) {
			return;
		}

		// accumulate mass over all shapes.
		mass += shape().mass1();
		inertia += shape().inertia1();

		if (mass > 0.f) {
			invMass = 1.f / mass;

			// TODO: adjust CoM

		}

		if (inertia > 0.f) {
			// TODO: adjust inertia around CoM
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

	public void addForce(float fx, float fy) {
		accY += fy * invMass;
		accX += fx * invMass;
	}

	public void addForceAtPoint(float fx, float fy, float px, float py) {
		accY += fy * invMass;
		accX += fx * invMass;

		torque += Vector2f.cross(px - transform.p.x, py - transform.p.y, fx, fy);
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void fillEntityBounds(PhysicsHashGrid<?> grid) {
		final var aabb = aabb();

		minX = grid.getColumnAtX(aabb.left());
		minY = grid.getRowAtY(aabb.top());

		maxX = grid.getColumnAtX(aabb.right());
		maxY = grid.getRowAtY(aabb.bottom());
	}

	@Override
	public boolean isGridCacheOld(PhysicsHashGrid<?> grid) {
		final var aabb = aabb();

		final var newMinX = grid.getColumnAtX(aabb.left());
		final var newMinY = grid.getRowAtY(aabb.top());

		final var newMaxX = grid.getColumnAtX(aabb.right());
		final var newMaxY = grid.getRowAtY(aabb.bottom());

		if (newMinX == minX && newMinY == minY && newMaxX == maxX && newMaxY == maxY)
			return false; // early out

		return true;
	}

}
