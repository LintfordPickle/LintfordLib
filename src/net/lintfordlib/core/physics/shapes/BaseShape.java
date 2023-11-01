package net.lintfordlib.core.physics.shapes;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Rotation;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public abstract class BaseShape {

	// --------------------------------------
	// Variables
	// --------------------------------------

	// TODO: name properly
	protected float restitution;
	protected float density;
	protected float staticFriction;
	protected float dynamicFriction;
	protected float area;

	public final Vector2f localCenter = new Vector2f(); // TODO: Shape local centers
	protected float mass;
	protected float inertia;

	protected float radius;
	protected float width;
	protected float height;
	protected final List<Vector2f> mLocalVertices;
	protected final List<Vector2f> mTransformedVertices;
	protected boolean mManualIsDirty;
	protected final Transform cacheT = new Transform();
	protected ShapeType mShapeType;
	protected final Rectangle mAABB;

	private int mCategoryBit; // I'm a ..
	private int mMaskBit; // I collide with ...

	private RigidBody mParent;
	public Object userData;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public RigidBody parentBody() {
		return mParent;
	}

	public float width() {
		return width;
	}

	public float height() {
		return height;
	}

	public float radius() {
		return radius;
	}

	public float restitution() {
		return restitution;
	}

	public float dynamicFriction() {
		return dynamicFriction;
	}

	public float staticFriction() {
		return staticFriction;
	}

	public float mass() {
		return mass;
	}

	public float inertia() {
		return inertia;
	}

	public ShapeType shapeType() {
		return mShapeType;
	}

	public List<Vector2f> getVertices() {
		return mLocalVertices;
	}

	private boolean updateFrameNeeded(Transform t) {
		return mManualIsDirty || !cacheT.compare(t);
	}

	public Rectangle aabb() {
		if (updateFrameNeeded(parentBody().transform))
			updateFrame(parentBody().transform);

		return mAABB;
	}

	public Rectangle aabb(Transform t) {
		if (updateFrameNeeded(t))
			updateFrame(t);

		return mAABB;
	}

	public List<Vector2f> getTransformedVertices() {
		return getTransformedVertices(mParent.transform);
	}

	public List<Vector2f> getTransformedVertices(Transform t) {
		if (updateFrameNeeded(t))
			updateFrame(t);

		return mTransformedVertices;
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

	public BaseShape() {
		mLocalVertices = new ArrayList<>();
		mTransformedVertices = new ArrayList<>();
		mAABB = new Rectangle();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void attachShapeToBody(RigidBody parentBody) {
		assert (parentBody != null) : "BaseShape cannot be attached to a null-instance of RigidBody";
		assert (parentBody.transform != null) : "ParentBody.Transform of BaseShape instance cannot be null";

		mParent = parentBody;

		mParent.addShape(this);
	}

	public void detachShape() {
		mParent = null;
		mParent.removeShape(this);
	}

	/**
	 * Transforms the local vertices into world space and rebuilds the AABB.
	 */
	private void updateFrame(Transform t) {
		final var lNumVerts = mLocalVertices.size();
		final var lTestRotation = new Rotation();
		lTestRotation.set((float) Math.toRadians(90));

		for (int i = 0; i < lNumVerts; i++) {
			mTransformedVertices.get(i).set(mLocalVertices.get(i)).mul(t.q).add(t.p);
		}

		rebuildAABB(t);

		mManualIsDirty = false;
		cacheT.set(t);
	}

	public abstract void rebuildAABB(Transform t);

	public abstract void computeMass();
}
