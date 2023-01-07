package net.lintford.library.core.collisions;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;

public class RigidBody {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	private class BodyState {
		private float x, y, radius, width, height, angle;

		private boolean isBodyDirty(RigidBody body) {
			return x != body.x || y != body.y || angle != body.angle || radius != body.radius || width != body.width || height != body.height;
		}

		private void update(RigidBody body) {
			x = body.x;
			y = body.y;
			radius = body.radius;
			width = body.width;
			height = body.height;
			angle = body.angle;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final BodyState state = new BodyState();
	private transient final Rectangle mAABB = new Rectangle();

	@SerializedName(value = "x")
	public float x;

	@SerializedName(value = "y")
	public float y;

	@SerializedName(value = "vx")
	public float vx;

	@SerializedName(value = "vy")
	public float vy;

	@SerializedName(value = "ax")
	public float accX;

	@SerializedName(value = "ay")
	public float accY;

	@SerializedName(value = "torque")
	public float torque;

	@SerializedName(value = "rot")
	public float angle;

	@SerializedName(value = "rotVel")
	public float angularVelocity;

	@SerializedName(value = "mass")
	private float mMass;
	@SerializedName(value = "restitution")
	private float mRestitution;

	private transient float mArea;
	private transient float mDensity;
	private transient float mInvMass;

	private transient float mStaticFriction;
	private transient float mDynamicFriction;

	private transient float mInertia;
	private transient float mInvInertia;

	@SerializedName(value = "isStatic")
	private boolean mIsStatic;

	@SerializedName(value = "radius")
	public float radius;

	@SerializedName(value = "width")
	public float width;

	@SerializedName(value = "height")
	public float height;

	@SerializedName(value = "vertices")
	private final List<Vector2f> mLocalVertices;
	private transient List<Vector2f> mTransformedVertices;
	private boolean mManualIsDirty;

	@SerializedName(value = "shapeType")
	private ShapeType mShapeType;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float dynamicFriction() {
		return mDynamicFriction;
	}

	public float staticFriction() {
		return mStaticFriction;
	}

	public boolean isManualDirty() {
		return mManualIsDirty;
	}

	public void setManualDirty() {
		mManualIsDirty = true;
	}

	public boolean isStatic() {
		return mIsStatic;
	}

	public ShapeType shapeType() {
		return mShapeType;
	}

	public List<Vector2f> getVertices() {
		return mLocalVertices;
	}

	public Rectangle aabb() {
		getTransformedVertices();

		return mAABB;
	}

	public List<Vector2f> getTransformedVertices() {
		if (mManualIsDirty || state.isBodyDirty(this)) {
			rebuildTransformedVertices();
			rebuildAABB();

			state.update(this);
			mManualIsDirty = false;
		}

		return mTransformedVertices;
	}

	public float mass() {
		return mMass;
	}

	public float invMass() {
		return mInvMass;
	}

	public float inertia() {
		return mInertia;
	}

	public float invInertia() {
		return mInvInertia;
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

	public RigidBody(float density, float restitution, float staticFriction, float dynamicFriction, float mass, float inertia, float area, boolean isStatic, float width, float height, float radius, ShapeType shapeType) {
		this.radius = radius;
		this.width = width;
		this.height = height;

		this.mDensity = density;
		this.mMass = mass;
		this.mRestitution = restitution;
		this.mArea = area;
		this.mShapeType = shapeType;
		this.mIsStatic = isStatic;

		this.mStaticFriction = staticFriction;
		this.mDynamicFriction = dynamicFriction;

		this.mInertia = inertia;

		if (this.mIsStatic) {
			this.mInvMass = 0.f;
			this.mInertia = 0.f;
		} else {
			this.mInvMass = 1.f / this.mMass;
			this.mInvInertia = 1.f / this.mInertia;
		}

		if (this.mShapeType == ShapeType.Polygon || this.mShapeType == ShapeType.Box) {
			mLocalVertices = createBoxVertices(width, height);
			mTransformedVertices = new ArrayList<>(mLocalVertices.size());
		} else if (this.mShapeType == ShapeType.Line) {
			mLocalVertices = createLineVertices(width);
			mTransformedVertices = new ArrayList<>(mLocalVertices.size());
		} else {
			mLocalVertices = createCircleVertices();
		}

		mTransformedVertices = new ArrayList<>(mLocalVertices.size());
		final int lNumLocalVertices = mLocalVertices.size();
		for (int i = 0; i < lNumLocalVertices; i++) {
			mTransformedVertices.add(new Vector2f(mLocalVertices.get(i)));
		}
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

		x += vx * time;
		y += vy * time;

		angle += angularVelocity * time;
		angle = MathHelper.wrapAngle(angle);
		
		accX = 0.f;
		accY = 0.f;
		torque = 0.f;

		setManualDirty();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	private void rebuildAABB() {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;

		if (mShapeType == ShapeType.Box || mShapeType == ShapeType.Polygon) {
			float furthestVertDist = 0.f;

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

				final var lDist = Vector2f.dst(x, y, v.x, v.y);
				if (lDist > furthestVertDist)
					furthestVertDist = lDist;

			}

			radius = furthestVertDist;

		} else if (mShapeType == ShapeType.Line) {
			final var sin = (float) Math.sin(angle);
			final var cos = (float) Math.cos(angle);

			final var hwc = width * .5f * cos;
			final var hhc = radius * cos;
			final var hws = width * .5f * sin;
			final var hhs = radius * sin;

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

			minX = x + Math.min(Math.min(Math.min(x0, x1), x2), x3);
			minY = y + Math.min(Math.min(Math.min(y0, y1), y2), y3);

			maxX = x + Math.max(Math.max(Math.max(x0, x1), x2), x3);
			maxY = y + Math.max(Math.max(Math.max(y0, y1), y2), y3);

		} else {
			minX = x - radius;
			minY = y - radius;
			maxX = x + radius;
			maxY = y + radius;
		}

		mAABB.set(minX, minY, maxX - minX, maxY - minY);
	}

	private void rebuildTransformedVertices() {
		switch (mShapeType) {
		case Polygon: {

			// vertex-centric implementation (polygons)
			final var c = (float) Math.cos(angle);
			final var s = (float) Math.sin(angle);

			// local vertices have neither world position nor rotation
			final var la = mLocalVertices.get(0);
			final var lb = mLocalVertices.get(1);
			final var lc = mLocalVertices.get(2);
			final var ld = mLocalVertices.get(3);

			float r_lax = la.x * c - la.y * s;
			float r_lay = la.x * s + la.y * c;

			final var wax = x + r_lax;
			final var way = y + r_lay;

			float r_lbx = lb.x * c - lb.y * s;
			float r_lby = lb.x * s + lb.y * c;

			final var wbx = x + r_lbx;
			final var wby = y + r_lby;

			float r_lcx = lc.x * c - lc.y * s;
			float r_lcy = lc.x * s + lc.y * c;

			final var wcx = x + r_lcx;
			final var wcy = y + r_lcy;

			float r_ldx = ld.x * c - ld.y * s;
			float r_ldy = ld.x * s + ld.y * c;

			final var wdx = x + r_ldx;
			final var wdy = y + r_ldy;

			mTransformedVertices.get(0).set(wax, way);
			mTransformedVertices.get(1).set(wbx, wby);
			mTransformedVertices.get(2).set(wcx, wcy);
			mTransformedVertices.get(3).set(wdx, wdy);

			break;
		}
		case Box: {
			// vertex-centric implementation (polygons)
			final var cos = (float) Math.cos(angle);
			final var sin = (float) Math.sin(angle);

			float lHalfW = width / 2f;
			float lHalfH = height / 2f;

			float x0 = -lHalfW * cos - lHalfH * sin;
			float y0 = -lHalfW * sin + lHalfH * cos;

			float x1 = -lHalfW * cos - -lHalfH * sin;
			float y1 = -lHalfW * sin + -lHalfH * cos;

			float x2 = lHalfW * cos - -lHalfH * sin;
			float y2 = lHalfW * sin + -lHalfH * cos;

			float x3 = lHalfW * cos - lHalfH * sin;
			float y3 = lHalfW * sin + lHalfH * cos;

			mTransformedVertices.get(0).set(x + x0, y + y0);
			mTransformedVertices.get(1).set(x + x1, y + y1);
			mTransformedVertices.get(2).set(x + x2, y + y2);
			mTransformedVertices.get(3).set(x + x3, y + y3);

			break;
		}

		case Line: {
			final var lWorldX = x;
			final var lWorldY = y;

			// note: for lines, we don't actually use local vertices, we directly calculate the world/transformed vertices
			// using world position, the length and width of the wall and its rotation.

			final var s = mTransformedVertices.get(0);
			final var e = mTransformedVertices.get(1);

			s.x = lWorldX - (float) Math.cos(angle) * width * .5f;
			s.y = lWorldY - (float) Math.sin(angle) * width * .5f;

			e.x = lWorldX + (float) Math.cos(angle) * width * .5f;
			e.y = lWorldY + (float) Math.sin(angle) * width * .5f;

			break;
		}

		default:
		case Circle:

			// note: for circles, there is only a single vertex, which is centered on the body centroid (x,y)

			mTransformedVertices.get(0).set(x, y);
			break;
		}
	}

	// --------------------------------------
	// Helper-Methods
	// --------------------------------------

	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void move(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void angle(float a) {
		this.angle = a;
	}

	public void addForce(float x, float y) {
		accY += y * invMass();
		accX += x * invMass();
	}

	public void setLocalVertices(Vector2f... verts) {
		if (verts == null || verts.length != mLocalVertices.size())
			return;

		switch (mShapeType) {
		case Box:
			if (verts == null || verts.length != 4) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Box - vertex count incorrect!");
				return;
			}

			mLocalVertices.get(0).set(verts[0]);
			mLocalVertices.get(1).set(verts[1]);
			mLocalVertices.get(2).set(verts[2]);
			mLocalVertices.get(3).set(verts[3]);

			break;

		case Line:
			if (verts == null || verts.length != 2) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Line - vertex count incorrect!");
				return;
			}

			mLocalVertices.get(0).set(verts[0]);
			mLocalVertices.get(1).set(verts[1]);

			break;

		case Circle:
			if (verts == null || verts.length != 1) {
				Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot set RigidBody vertices on Circle - vertex count incorrect!");
				return;
			}

			mLocalVertices.get(0).set(verts[0]);

			break;

		default:
			return;
		}

		rebuildTransformedVertices();
		rebuildAABB();

		// Need to update the
		mTransformedVertices = new ArrayList<>(mLocalVertices.size());
		final int lNumLocalVertices = mLocalVertices.size();
		for (int i = 0; i < lNumLocalVertices; i++) {
			mTransformedVertices.add(new Vector2f(mLocalVertices.get(i)));
		}

	}

	public void recalculateBoxCentroidAndRadius() {
		final var verts = mLocalVertices;

		final var localx = (verts.get(0).x + verts.get(1).x + verts.get(2).x + verts.get(3).x) / 4f;
		final var localy = (verts.get(0).y + verts.get(1).y + verts.get(2).y + verts.get(3).y) / 4f;

		x += localx;
		y += localy;

		mLocalVertices.get(0).x -= localx;
		mLocalVertices.get(0).y -= localy;

		mLocalVertices.get(1).x -= localx;
		mLocalVertices.get(1).y -= localy;

		mLocalVertices.get(2).x -= localx;
		mLocalVertices.get(2).y -= localy;

		mLocalVertices.get(3).x -= localx;
		mLocalVertices.get(3).y -= localy;

		final var lExtendMargin = 5.f;
		radius = 0.f;
		radius = Math.max(radius, Vector2f.dst(x, y, verts.get(0).x, verts.get(0).y));
		radius = Math.max(radius, Vector2f.dst(x, y, verts.get(1).x, verts.get(1).y));
		radius = Math.max(radius, Vector2f.dst(x, y, verts.get(2).x, verts.get(2).y));
		radius = Math.max(radius, Vector2f.dst(x, y, verts.get(3).x, verts.get(3).y));
		radius += lExtendMargin;
	}

	private static List<Vector2f> createBoxVertices(float width, float height) {
		var newVertices = new ArrayList<Vector2f>(4);

		float left = -width / 2f;
		float right = left + width;
		float bottom = -height / 2f;
		float top = bottom + height;

		// clock-wise
		newVertices.add(new Vector2f(left, top));
		newVertices.add(new Vector2f(right, top));
		newVertices.add(new Vector2f(right, bottom));
		newVertices.add(new Vector2f(left, bottom));

		return newVertices;
	}

	private static List<Vector2f> createLineVertices(float lineLength) {
		var newVertices = new ArrayList<Vector2f>(2);

		newVertices.add(new Vector2f(-lineLength * .5f, 0.f)); // 0
		newVertices.add(new Vector2f(+lineLength * .5f, 0.f)); // 1

		return newVertices;
	}

	private static List<Vector2f> createCircleVertices() {
		var newVertices = new ArrayList<Vector2f>(1);

		// TODO: circles don't require any vertices (neither local nor world)

		newVertices.add(new Vector2f(0.f, 0.f));

		return newVertices;
	}

	// --------------------------------------
	// Factory-Methods
	// --------------------------------------

	public static RigidBody createCircleBody(float radius, float density, float restitution, float staticFriction, float dynamicFriction, boolean isStatic) {
		final float lArea = radius * radius * (float) Math.PI;
		final float lMass = lArea * density;
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		// I = (1/2)mr^2
		final float lInertia = .5f * lMass * radius * radius;

		return new RigidBody(density, restitution, staticFriction, dynamicFriction, lMass, lInertia, lArea, isStatic, 0.f, 0.f, radius, ShapeType.Circle);
	}

	public static RigidBody createLineBody(float width, float height, float density, float restitution, float staticFriction, float dynamicFriction, boolean isStatic) {
		final float lArea = width * height;
		final float lMass = lArea * density;
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final float lRadius = (float) Math.sqrt(width * width + height * height);

		// I = (1/12)m(h^2+w^2)
		final float lInertia = (1.f / 12.f) * lMass * (height * height + width * width);

		return new RigidBody(density, restitution, staticFriction, dynamicFriction, lMass, lInertia, lArea, isStatic, width, height, lRadius, ShapeType.Line);
	}

	public static RigidBody createBoxBody(float width, float height, float density, float restitution, float staticFriction, float dynamicFriction, boolean isStatic) {
		final float lArea = width * height;
		final float lMass = lArea * density;
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final float lRadius = (float) Math.sqrt(width * width + height * height);

		// I = (1/12)m(h^2+w^2)
		final float lInertia = (1.f / 12.f) * lMass * (height * height + width * width);

		return new RigidBody(density, restitution, staticFriction, dynamicFriction, lMass, lInertia, lArea, isStatic, width, height, lRadius, ShapeType.Box);
	}

	public static RigidBody createPolygonBody(float width, float height, float density, float restitution, float staticFriction, float dynamicFriction, boolean isStatic) {
		final float lArea = width * height;
		final float lMass = lArea * density;
		restitution = MathHelper.clamp(restitution, 0f, 1f);

		final float lRadius = (float) Math.sqrt(width * width + height * height);

		// I = (1/12)m(h^2+w^2)
		final float lInertia = (1.f / 12.f) * lMass * (height * height + width * width);

		return new RigidBody(density, restitution, staticFriction, dynamicFriction, lMass, lInertia, lArea, isStatic, width, height, lRadius, ShapeType.Polygon);
	}
}
