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
		}

		// Ensure winding order is CCW
		final var v0 = mLocalVertices.get(0);
		for (int i = 1; i < lNumLocalVertices - 1; i++) {

			final var v1 = mLocalVertices.get(i);
			final var lNextIndex = i + 1 < lNumLocalVertices ? i + 1 : 0;
			final var v2 = mLocalVertices.get(lNextIndex);

			assert MathHelper.isTriangleCcw(v0.x, v0.y, v1.x, v1.y, v2.x, v2.y) : "PolygonShape expects CCW winding order in local vertices.";
		}

		rebuildAABB(null);
		computeMass();
	}

	private void setAsBox(float unitPositionX, float unitPositionY, float unitWidth, float unitHeight, float rotRadians) {
		width = unitWidth;
		height = unitHeight;
		radius = (float) Math.sqrt(width * width + height * height) * .5f;

		final var s = (float) Math.sin(rotRadians);
		final var c = (float) Math.cos(rotRadians);

		final var l = -width * .5f;
		final var b = height * .5f;
		final var r = width * .5f;
		final var t = -height * .5f;

		final var local_tl = new Vector2f(l * c - t * s, l * s + t * c);
		final var local_tr = new Vector2f(r * c - t * s, r * s + t * c);
		final var local_br = new Vector2f(r * c - b * s, r * s + b * c);
		final var local_bl = new Vector2f(l * c - b * s, l * s + b * c);

		// CCW winding order
		mLocalVertices.clear();
		mLocalVertices.add(local_tl.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_bl.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_br.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_tr.add(unitPositionX, unitPositionY));

		assert MathHelper.isTriangleCcw(mLocalVertices.get(0).x, mLocalVertices.get(0).y, mLocalVertices.get(1).x, mLocalVertices.get(1).y, mLocalVertices.get(2).x, mLocalVertices.get(2).y) : "BoxShape triangle 0 not CCW";
		assert MathHelper.isTriangleCcw(mLocalVertices.get(0).x, mLocalVertices.get(0).y, mLocalVertices.get(2).x, mLocalVertices.get(2).y, mLocalVertices.get(3).x, mLocalVertices.get(3).y) : "BoxShape triangle 1 not CCW";

		mTransformedVertices.clear();
		mTransformedVertices.add(new Vector2f(local_tl));
		mTransformedVertices.add(new Vector2f(local_bl));
		mTransformedVertices.add(new Vector2f(local_br));
		mTransformedVertices.add(new Vector2f(local_tr));

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

	public static PolygonShape createBoxShape(float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		return createBoxShape(0.f, 0.f, unitWidth, unitHeight, rotRadians, density, restitution, staticFriction, dynamicFriction);
	}

	public static PolygonShape createBoxShape(float unitPositionX, float unitPositionY, float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewBoxShape = new PolygonShape();

		lNewBoxShape.density = Math.abs(density);
		lNewBoxShape.staticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewBoxShape.dynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewBoxShape.restitution = MathHelper.clamp(restitution, 0f, 1f);
		lNewBoxShape.setAsBox(unitPositionX, unitPositionY, unitWidth, unitHeight, rotRadians);

		return lNewBoxShape;
	}

}
