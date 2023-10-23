package net.lintfordlib.core.physics.shapes;

import java.util.ArrayList;
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

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float width() {
		return width;
	}

	public float height() {
		return height;
	}

	public float radius() {
		return radius;
	}

	// ---

	public float restitution() {
		return restitution;
	}

	public float dynamicFriction() {
		return dynamicFriction;
	}

	public float staticFriction() {
		return staticFriction;
	}

	public float mass1() {
		return mass;
	}

	public float inertia1() {
		return inertia;
	}

	// ---

	public ShapeType shapeType() {
		return mShapeType;
	}

	public List<Vector2f> getVertices() {
		return mLocalVertices;
	}

	private boolean updateFrameNeeded(Transform t) {
		return mManualIsDirty || !cacheT.compare(t);
	}

	public Rectangle aabb(Transform t) {
		if (updateFrameNeeded(t))
			updateFrame(t);

		return mAABB;
	}

	public List<Vector2f> getTransformedVertices(Transform t) {
		// if (updateFrameNeeded(t))
			updateFrame(t);

		return mTransformedVertices;
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
