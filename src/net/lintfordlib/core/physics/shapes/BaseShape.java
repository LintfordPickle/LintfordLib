package net.lintfordlib.core.physics.shapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.Rotation;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public abstract class BaseShape {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final Vector2f localCenter = new Vector2f();

	protected float mRestitution;
	protected float mDensity;
	protected float mStaticFriction;
	protected float mDynamicFriction;
	protected float mArea;

	protected float mMass;
	protected float mInertia;

	protected float mRadius;
	protected float mWidth;
	protected float mHeight;
	protected final List<Vector2f> mLocalVertices;
	protected final List<Vector2f> mTransformedVertices;
	protected boolean mManualIsDirty;
	protected final Transform mCachedTransform = new Transform();
	protected ShapeType mShapeType;
	protected final Rectangle mAABB;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float width() {
		return mWidth;
	}

	public float height() {
		return mHeight;
	}

	public float radius() {
		return mRadius;
	}

	public float restitution() {
		return mRestitution;
	}

	public float dynamicFriction() {
		return mDynamicFriction;
	}

	public float staticFriction() {
		return mStaticFriction;
	}

	public float mass() {
		return mMass;
	}

	public float inertia() {
		return mInertia;
	}

	public ShapeType shapeType() {
		return mShapeType;
	}

	public void setLocalVertices(Vector2f... vertices) {
		mLocalVertices.clear();
		mTransformedVertices.clear();

		final int lNumVectors = vertices.length;
		for (int i = 0; i < lNumVectors; i++) {
			mLocalVertices.add(new Vector2f(vertices[i]));
			mTransformedVertices.add(new Vector2f(vertices[i]));
		}

		mManualIsDirty = true;
	}

	public List<Vector2f> getReadOnlyVertices() {
		return Collections.unmodifiableList(mLocalVertices);
	}

	private boolean updateFrameNeeded(Transform t) {
		return mManualIsDirty || !mCachedTransform.compare(t);
	}

	public Rectangle aabb(Transform t) {
		if (updateFrameNeeded(t))
			updateFrame(t);

		return mAABB;
	}

	public List<Vector2f> getTransformedVertices(Transform t) {
		if (updateFrameNeeded(t))
			updateFrame(t);

		return mTransformedVertices;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	protected BaseShape() {
		mLocalVertices = new ArrayList<>();
		mTransformedVertices = new ArrayList<>();
		mAABB = new Rectangle();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

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
		mCachedTransform.set(t);
	}

	public abstract void rebuildAABB(Transform t);

	public abstract void computeMass();
}
