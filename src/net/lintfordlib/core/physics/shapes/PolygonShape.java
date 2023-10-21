package net.lintfordlib.core.physics.shapes;

import java.util.List;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class PolygonShape extends BaseShape {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private PolygonShape() {
		mShapeType = ShapeType.Polygon;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void rebuildAABB(Transform t) {
		var minX = Float.MAX_VALUE;
		var minY = Float.MAX_VALUE;
		var maxX = -Float.MAX_VALUE;
		var maxY = -Float.MAX_VALUE;

		var vertices = mTransformedVertices;
		final var lNumVertices = vertices.size();
		for (int i = 0; i < lNumVertices; i++) {
			var v = vertices.get(i);
			if (v.x < minX)
				minX = v.x;
			if (v.x > maxX)
				maxX = v.x;
			if (v.y < minY)
				minY = v.y;
			if (v.y > maxY)
				maxY = v.y;
		}

		mAABB.set(minX, minY, maxX - minX, maxY - minY);
	}

	@Override
	public void computeMass() {
		// mass, inertia and centroid

		area = 0.f;
		mass = 0.f;
		inertia = 0.f;
		localCenter.set(0, 0);

		float c_x = 0.f;
		float c_y = 0.f;

		float I = 0.f;
		final var k_inv3 = 1.f / 3.f;
		final var s = mLocalVertices.get(0);

		final var lNumVertices = mLocalVertices.size();
		for (int i = 0; i < lNumVertices; i++) {
			// tri-verts
			final var e1_x = mLocalVertices.get(i).x - s.x;
			final var e1_y = mLocalVertices.get(i).y - s.y;

			final var lNextIndex = i + 1 < lNumVertices ? i + 1 : 0;
			final var e2_x = mLocalVertices.get(lNextIndex).x - s.x;
			final var e2_y = mLocalVertices.get(lNextIndex).y - s.y;

			final var D = Vector2f.cross(e1_x, e1_y, e2_x, e2_y);
			float triangleArea = .5f * D;
			area += triangleArea;

			// area weighted centroid
			c_x += triangleArea * k_inv3 * (e1_x + e2_x);
			c_y += triangleArea * k_inv3 * (e1_y + e2_y);

			final var intx2 = e1_x * e1_x + e1_x * e2_x + e2_x * e2_x;
			final var inty2 = e1_y * e1_y + e1_y * e2_y + e2_y * e2_y;

			I += (0.25f * k_inv3 * D) * (intx2 + inty2);
		}

		// center of mass
		c_x *= 1.f / area;
		c_y *= 1.f / area;

		localCenter.x = c_x + s.x;
		localCenter.y = c_y + s.y;

		area = (float) Math.abs(area);

		// total mass
		mass = (float) Math.abs(density * area);

		// Inertia relative to local center (s)
		inertia = (float) Math.abs(density * I);
	}

	private void set(List<Vector2f> vertices) {
		mLocalVertices.clear();
		final int lNumLocalVertices = vertices.size();
		for (int i = 0; i < lNumLocalVertices; i++) {
			mLocalVertices.add(new Vector2f(vertices.get(i)));
			mTransformedVertices.add(new Vector2f(vertices.get(i)));

			System.out.println("(" + vertices.get(i).x + "," + vertices.get(i).y + ")");

		}

		// Ensure winding order is CCW
		final var v0 = mLocalVertices.get(0);
		for (int i = 1; i < lNumLocalVertices - 1; i++) {

			final var v1 = mLocalVertices.get(i);
			final var lNextIndex = i + 1 < lNumLocalVertices ? i + 1 : 0;
			final var v2 = mLocalVertices.get(lNextIndex);

			assert MathHelper.isTriangleCcw(v0.x, v0.y, v1.x, v1.y, v2.x, v2.y) : "PolygonShape expects CCW winding order in local vertices.";
		}

		// TODO: Set wdith/height/radius?

		rebuildAABB(null);
		computeMass();
	}

	// --------------------------------------
	// Factory-Methods
	// --------------------------------------

	/**
	 * The passed local vertices must form a convex polygon and be in CCW winding order.
	 */
	public static PolygonShape createPolygonShape(List<Vector2f> localVertices, float density, float restitution, float staticFriction, float dynamicFriction) {
		if (localVertices == null || localVertices.size() < 3) {
			Debug.debugManager().logger().w(PolygonShape.class.getSimpleName(), "PolygonShapes require at least 3 vertices");
			return null;
		}

		final var lNewPolygonShape = new PolygonShape();

		lNewPolygonShape.density = Math.abs(density);
		lNewPolygonShape.staticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewPolygonShape.dynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewPolygonShape.restitution = MathHelper.clamp(restitution, 0f, 1f);

		lNewPolygonShape.set(localVertices);

		return lNewPolygonShape;
	}

}
