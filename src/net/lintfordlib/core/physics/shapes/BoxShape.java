package net.lintfordlib.core.physics.shapes;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Transform;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;

public class BoxShape extends BaseShape {

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private BoxShape() {
		mShapeType = ShapeType.Box;
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
		area = width * height;
		mass = area * density;

		// Thin rectangular plate of height h, width w and mass m (Axis of rotation at the center)
		inertia = (1.f / 12.f) * mass * (height * height + width * width);
	}

	private void set(float unitPositionX, float unitPositionY, float unitWidth, float unitHeight, float rotRadians) {
		width = unitWidth;
		height = unitHeight;
		radius = (float) Math.sqrt(width * width + height * height) * .5f;

		computeMass();

		final var s = (float) Math.sin(rotRadians);
		final var c = (float) Math.cos(rotRadians);

		final var l = -width * .5f;
		final var b = -height * .5f;
		final var r = width * .5f;
		final var t = height * .5f;

		final var local_tl = new Vector2f(l * c - t * s, l * s + t * c);
		final var local_tr = new Vector2f(r * c - t * s, r * s + t * c);
		final var local_br = new Vector2f(r * c - b * s, r * s + b * c);
		final var local_bl = new Vector2f(l * c - b * s, l * s + b * c);

		// CCW winding order
		mLocalVertices.clear();
		mLocalVertices.add(local_tl.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_tr.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_br.add(unitPositionX, unitPositionY));
		mLocalVertices.add(local_bl.add(unitPositionX, unitPositionY));

		mTransformedVertices.clear();
		mTransformedVertices.add(new Vector2f(local_tl));
		mTransformedVertices.add(new Vector2f(local_tr));
		mTransformedVertices.add(new Vector2f(local_br));
		mTransformedVertices.add(new Vector2f(local_bl));
	}

	// --------------------------------------
	// Factory-Methods
	// --------------------------------------

	public static BoxShape createBoxShape(float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		return createBoxShape(0.f, 0.f, unitWidth, unitHeight, rotRadians, density, restitution, staticFriction, dynamicFriction);
	}

	public static BoxShape createBoxShape(float unitPositionX, float unitPositionY, float unitWidth, float unitHeight, float rotRadians, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewBoxShape = new BoxShape();

		lNewBoxShape.density = Math.abs(density);
		lNewBoxShape.staticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewBoxShape.dynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewBoxShape.restitution = MathHelper.clamp(restitution, 0f, 1f);
		lNewBoxShape.set(unitPositionX, unitPositionY, unitWidth, unitHeight, rotRadians);

		return lNewBoxShape;
	}

}
