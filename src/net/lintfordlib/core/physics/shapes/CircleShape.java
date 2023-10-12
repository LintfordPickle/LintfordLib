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
		area = (float) Math.PI * radius * radius;
		mass = area * density;

		// I = (1/2)mr^2
		inertia = .5f * mass * radius * radius;
	}

	@Override
	public void rebuildAABB(Transform t) {
		final var x = mTransformedVertices.get(0).x;
		final var y = mTransformedVertices.get(0).y;

		final var minX = x - radius;
		final var minY = y - radius;
		final var maxX = x + radius;
		final var maxY = y + radius;

		mAABB.set(minX, minY, maxX - minX, maxY - minY);
	}

	private void set(List<Vector2f> vertices, float density, float unitRadius) {
		if (vertices.size() != 1) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "CircleShape expects 1 vertex for creation");
			throw new IllegalArgumentException("CircleShape expects 1 vertex for creation");
		}

		mLocalVertices.add(vertices.get(0));
		mTransformedVertices.add(new Vector2f(vertices.get(0)));
		this.density = density;
		radius = unitRadius;

		rebuildAABB(Transform.Identity);
		computeMass();
	}

	// --------------------------------------
	// Factory Methods
	// --------------------------------------

	public static CircleShape createCircleShape(float unitPositionX, float unitPositionY, float unitRadius, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewCircleShape = new CircleShape();

		lNewCircleShape.set(Arrays.asList(new Vector2f(unitPositionX, unitPositionY)), Math.abs(density), unitRadius);
		lNewCircleShape.staticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewCircleShape.dynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewCircleShape.restitution = MathHelper.clamp(restitution, 0f, 1f);

		return lNewCircleShape;
	}
}
