package net.lintfordlib.core.physics.dynamics;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.spatial.PhysicsGridEntity;
import net.lintfordlib.core.physics.spatial.PhysicsHashGrid;

public class Fixture extends PhysicsGridEntity {

	private static int uidCounter;

	public static int getNewFixtureUid() {
		return uidCounter++;
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	public final RigidBody parent;
	public final Rectangle mAABB = new Rectangle();
	public final MassData massData = new MassData();

	private ShapeType mShapeType;

	private transient float mRestitution;
	private transient float mArea;
	private transient float mDensity;
	private transient float mStaticFriction;
	private transient float mDynamicFriction;

	public float x, y; // the position of the fixture's centroid relative to the fixture's origin.
	public float width, height;
	public float radius;

	private List<Vector2f> mLocalVertices;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ShapeType shapeType() {
		return mShapeType;
	}

	public Rectangle aabb() {
		rebuildAABBAroundTransformedVertices();
		return mAABB;
	}

	public List<Vector2f> getVertices() {
		return mLocalVertices;
	}

	public float dynamicFriction() {
		return mDynamicFriction;
	}

	public float staticFriction() {
		return mStaticFriction;
	}

	public float restitution() {
		return mRestitution;
	}

	public float density() {
		return mDensity;
	}

	public float area() {
		return mArea;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private Fixture(RigidBody parentBody, float density, float restitution, float staticFriction, float dynamicFriction) {
		super(getNewFixtureUid());

		parent = parentBody;

		this.mDensity = density;

		this.mStaticFriction = staticFriction;
		this.mDynamicFriction = dynamicFriction;
		this.mRestitution = restitution;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// TODO: Just implment the sub-classes as Shapes (like in Box2d)

	public static Fixture createPolygonFixture(RigidBody parent, float density, float restitution, float staticFriction, float dynamicFriction) {
		density = MathHelper.clamp(density, 0, density);
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final var lFixture = new Fixture(parent, density, restitution, staticFriction, dynamicFriction);

		return lFixture;
	}

	public static Fixture createBoxFixture(RigidBody parent, float density, float restitution, float staticFriction, float dynamicFriction) {
		density = MathHelper.clamp(density, 0, density);
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final var lFixture = new Fixture(parent, density, restitution, staticFriction, dynamicFriction);

		return lFixture;
	}

	public static Fixture createLineWidthFixture(RigidBody parent, float density, float restitution, float staticFriction, float dynamicFriction) {
		density = MathHelper.clamp(density, 0, density);
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final var lFixture = new Fixture(parent, density, restitution, staticFriction, dynamicFriction);

		return lFixture;
	}

	public static Fixture createCircleFixture(RigidBody parent, float radius, float density, float restitution, float staticFriction, float dynamicFriction) {
		density = MathHelper.clamp(density, 0, density);
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final var lFixture = new Fixture(parent, density, restitution, staticFriction, dynamicFriction);

		return lFixture;
	}

	/**
	 * Copies the passed vertices into thi {@link RigidBodys} vertex arrays. References to the original list are not retained.
	 */
	public void setVertices(List<Vector2f> vertices, ShapeType shapeType, float w, float h, float r) {
		if (vertices == null)
			return;

		mShapeType = shapeType;

		// Check the vert count matches the shape type
		switch (mShapeType) {
		default:
		case Polygon:

			break;
		case Box:
			if (vertices == null || vertices.size() != 4) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Box - vertex count incorrect!");
				return;
			}

			break;

		case LineWidth:
			if (vertices == null || vertices.size() != 2) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Line - vertex count incorrect!");
				return;
			}

			break;

		case Circle:
			if (vertices == null || vertices.size() != 1) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Circle - vertex count incorrect!");
				return;
			}

			break;
		}

		if (mLocalVertices == null) {
			mLocalVertices = new ArrayList<>();
		} else {
			mLocalVertices.clear();
		}

		final var lVertexCount = vertices.size();
		for (int i = 0; i < lVertexCount; i++) {
			mLocalVertices.add(new Vector2f(vertices.get(i)));
		}

		width = w;
		height = h;
		radius = r;

		computeMass();
	}

	private void computeMass() {
		if (parent.isStatic()) {
			massData.mass = 0.f;
			massData.inertia = 0.f;
			return;
		}

		switch (mShapeType) {
		case Polygon:
		case Box: {
			// lifted from box2d: https://github.com/erincatto/box2d/blob/main/src/collision/b2_polygon_shape.cpp
			// Polygon mass, centroid, and inertia.
			// Let rho be the polygon density in mass per unit area.
			// Then:
			// mass = rho * int(dA)
			// centroid.x = (1/mass) * rho * int(x * dA)
			// centroid.y = (1/mass) * rho * int(y * dA)
			// I = rho * int((x*x + y*y) * dA)
			//
			// We can compute these integrals by summing all the integrals
			// for each triangle of the polygon. To evaluate the integral
			// for a single triangle, we make a change of variables to
			// the (u,v) coordinates of the triangle:
			// x = x0 + e1x * u + e2x * v
			// y = y0 + e1y * u + e2y * v
			// where 0 <= u && 0 <= v && u + v <= 1.
			//
			// We integrate u from [0,1-v] and then v from [0,1].
			// We also need to use the Jacobian of the transformation:
			// D = cross(e1, e2)
			//
			// Simplification: triangle centroid = (1/3) * (p1 + p2 + p3)
			massData.mass = 0.f;
			float I = 0.f;
			mArea = 0.f;

			float centerX = 0.f;
			float centerY = 0.f;

			final var s = mLocalVertices.get(0);
			final var vertexCount = mLocalVertices.size();
			final var k_inv3 = 1.f / 3.f;

			for (int i = 0; i < vertexCount; i++) {
				final var e1x = mLocalVertices.get(i).x - s.x;
				final var e1y = mLocalVertices.get(i).y - s.y;
				final var e2x = i + 1 < vertexCount ? mLocalVertices.get(i + 1).x - s.x : mLocalVertices.get(0).x - s.x;
				final var e2y = i + 1 < vertexCount ? mLocalVertices.get(i + 1).y - s.y : mLocalVertices.get(0).y - s.y;

				float cross = Vector2f.cross(e1x, e1y, e2x, e2y);
				float triangleArea = 0.5f * cross;
				mArea += Math.abs(triangleArea);

				// Area weighted centroid
				centerX += triangleArea * k_inv3 * (e1x + e2x);
				centerY += triangleArea * k_inv3 * (e1x + e2y);

				final var intx2 = e1x * e1x + e2x * e1x + e2x * e2x;
				final var inty2 = e1y * e1y + e2y * e1y + e2y * e2y;

				I += (0.25f * k_inv3 * cross) * (intx2 + inty2);
			}

			massData.mass = mDensity * mArea;

			// Center of mass
			centerX *= 1.f / mArea;
			centerY *= 1.f / mArea;
			massData.center.x = centerX + s.x;
			massData.center.y = centerY + s.y;

			// Inertia tensor relative to the local origin (point s).
			massData.inertia = mDensity * I;

			// Shift to center of mass then to original body origin.
			massData.inertia += massData.mass * (massData.center.x * x + massData.center.y * y) - (centerX * centerX + centerY * centerY);
			break;
		}

		case Line: {
			massData.mass = 0.f;
			massData.center.x = (mLocalVertices.get(0).x + mLocalVertices.get(1).x) * .5f;
			massData.center.y = (mLocalVertices.get(0).y + mLocalVertices.get(1).y) * .5f;
			massData.inertia = 0.f;
			break;
		}

		case LineWidth: {
			mArea = width * height;

			massData.mass = 0.f;
			massData.center.x = (mLocalVertices.get(0).x + mLocalVertices.get(1).x) * .5f;
			massData.center.y = (mLocalVertices.get(0).y + mLocalVertices.get(1).y) * .5f;
			massData.inertia = 0.f;
			break;
		}

		default:
		case Circle: {
			mArea = (float) Math.PI * radius * radius;
			massData.mass = mDensity * mArea;
			massData.center.x = x;
			massData.center.y = y;

			// inertia about the local origin
			massData.inertia = massData.mass * (.5f * radius * radius + (x * x + y * y));
			break;
		}

		}
	}

	private void rebuildAABBAroundTransformedVertices() {
		final var lTransformedVertices = getVertices();

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;

		if (mShapeType == ShapeType.Box || mShapeType == ShapeType.Polygon) {
			float furthestVertDist = 0.f;

			var vertices = lTransformedVertices;
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

				final var lDist = Vector2f.dst(x, y, v.x, v.y);
				if (lDist > furthestVertDist)
					furthestVertDist = lDist;

			}

			// TODO: Check this radius calc - there is a dedicated method for the radius calculation.
			radius = furthestVertDist;

		} else if (mShapeType == ShapeType.LineWidth) {
			// TODO: This whole thing can be done directly by multiplying with the transform
			final var cos = parent.transform.rotation.cos();
			final var sin = parent.transform.rotation.sin();

			final var hwc = width * .5f * cos;
			final var hhc = height * cos;
			final var hws = width * .5f * sin;
			final var hhs = height * sin;

			// bl
			final var x0 = -hwc - hhs;
			final var y0 = -hws + hhc;

			// tl
			final var x1 = -hwc - -hhs;
			final var y1 = -hws + -hhc;

			// tr
			final var x2 = hwc - -hhs;
			final var y2 = hws + -hhc;

			// br
			final var x3 = hwc - hhs;
			final var y3 = hws + hhc;

			minX = parent.transform.position.x + Math.min(Math.min(Math.min(x0, x1), x2), x3);
			minY = parent.transform.position.y + Math.min(Math.min(Math.min(y0, y1), y2), y3);

			maxX = parent.transform.position.x + Math.max(Math.max(Math.max(x0, x1), x2), x3);
			maxY = parent.transform.position.y + Math.max(Math.max(Math.max(y0, y1), y2), y3);

		} else {
			minX = parent.transform.position.x - radius;
			minY = parent.transform.position.y - radius;
			maxX = parent.transform.position.x + radius;
			maxY = parent.transform.position.y + radius;
		}

		mAABB.set(minX, minY, maxX - minX, maxY - minY);
	}

	public void recalculateBoxCentroidAndRadius() {
		final var verts = mLocalVertices;

		final var lExtendMarginInPxs = 5.f;
		final var lExtendMargin = ConstantsPhysics.toUnits(lExtendMarginInPxs);
		radius = 0.f;
		radius = Math.max(radius, Vector2f.dst(verts.get(2).x, verts.get(2).y, verts.get(0).x, verts.get(0).y));
		radius = Math.max(radius, Vector2f.dst(verts.get(3).x, verts.get(3).y, verts.get(1).x, verts.get(1).y));
		radius += lExtendMargin;
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
