package net.lintfordlib.core.physics.shapes;

import java.util.Arrays;
import java.util.List;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class CircleShape extends BaseShape {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private CircleShape() {
		mShapeType = ShapeType.Circle;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void computeMass() {
		mArea = (float) Math.PI * mRadius * mRadius;
		mMass = mArea * mDensity;

		// I = (1/2)mr^2
		mInertia = .5f * mMass * mRadius * mRadius;
	}

	@Override
	public void rebuildAABB(Transform t) {
		final var x = mTransformedVertices.get(0).x;
		final var y = mTransformedVertices.get(0).y;

		final var minX = x - mRadius;
		final var minY = y - mRadius;
		final var maxX = x + mRadius;
		final var maxY = y + mRadius;

		mAABB.set(minX, minY, maxX - minX, maxY - minY);
	}

	private void set(List<Vector2f> vertices, float density, float unitRadius) {
		if (vertices.size() != 1) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "CircleShape expects 1 vertex for creation");
			throw new IllegalArgumentException("CircleShape expects 1 vertex for creation");
		}

		mLocalVertices.add(vertices.get(0));
		mTransformedVertices.add(new Vector2f(vertices.get(0)));
		this.mDensity = density;
		mRadius = unitRadius;

		rebuildAABB(Transform.Identity);
		computeMass();
	}

	// --------------------------------------
	// Factory Methods
	// --------------------------------------

	public static CircleShape createCircleShape(float unitRadius, float density, float restitution, float staticFriction, float dynamicFriction) {
		return createCircleShape(0.f, 0.f, unitRadius, density, restitution, staticFriction, dynamicFriction);
	}

	public static CircleShape createCircleShape(float unitPositionX, float unitPositionY, float unitRadius, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewCircleShape = new CircleShape();

		lNewCircleShape.mStaticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewCircleShape.mDynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewCircleShape.mRestitution = MathHelper.clamp(restitution, 0f, 1f);
		lNewCircleShape.set(Arrays.asList(new Vector2f(unitPositionX, unitPositionY)), Math.abs(density), unitRadius);

		return lNewCircleShape;
	}
}
