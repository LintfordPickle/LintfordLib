package net.lintfordlib.core.physics.dynamics;

import java.util.ArrayList;
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

	private final List<BaseShape> mShapes = new ArrayList<>();
	private Object userData;

	public final Transform transform = new Transform();
	private final Transform cacheT = new Transform();
	private final Rectangle mAABB = new Rectangle();

	// center of mass
	public float cx;
	public float cy;

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

	private boolean mIsStatic;

	// TODO: Add debug variables to UserData
	public boolean debugIsSelected;
	public boolean debugIsColliding;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Rectangle aabb() {
		if (!cacheT.compare(transform)) {
			rebuildAABB();
			cacheT.set(transform);
		}

		return mAABB;
	}

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

	public Object userData() {
		return userData;
	}

	public void userData(Object userData) {
		this.userData = userData;
	}

	public boolean isStatic() {
		return mIsStatic;
	}

	public List<BaseShape> shapes() {
		return mShapes;
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

		calculateMassData();
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	public void rebuildAABB() {
		// iterate all shapes and grow the aabb to encompass all
		final int lNumShapes = mShapes.size();
		for (int i = 0; i < lNumShapes; i++) {
			if (i == 0) {
				mAABB.set(mShapes.get(i).aabb(transform));
				continue;
			}

			mAABB.updateABBToEncloseRectangle(mShapes.get(i).aabb(transform));
		}
	}

	public void addShape(BaseShape shape) {
		if (mShapes.contains(shape) == false) {
			mShapes.add(shape);

			shape.attachShapeToBody(this);

			calculateMassData();
		}
	}

	public void removeShape(BaseShape shape) {
		if (mShapes.contains(shape)) {
			mShapes.remove(shape);

			shape.detachShape();
		}
	}

	private void calculateMassData() {
		mass = 0.f;
		invMass = 0.f;
		inertia = 0.f;
		invInertia = 0.f;

		cx = 0;
		cy = 0;

		float w_cx = 0.f;
		float w_cy = 0.f;

		if (isStatic()) {
			return;
		}

		// The mass for the body is the sum of the shape masses
		final int lNumShapes = mShapes.size();
		for (int i = 0; i < lNumShapes; i++) {
			final var lShape = mShapes.get(i);

			w_cx += lShape.mass() * lShape.localCenter.x;
			w_cy += lShape.mass() * lShape.localCenter.y;

			mass += lShape.mass();
			inertia += lShape.inertia();
		}

		if (mass > 0.f) {
			invMass = 1.f / mass;
			w_cx *= invMass;
			w_cy *= invMass;
		}

		if (inertia > 0.f) {
			inertia -= mass * Vector2f.dot(w_cx, w_cx, w_cy, w_cy);
			invInertia = 1.f / inertia;
		}

		// Move the center of mass
		cx = w_cx;
		cy = w_cy;
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
