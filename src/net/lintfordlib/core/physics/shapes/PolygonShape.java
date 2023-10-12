package net.lintfordlib.core.physics.shapes;

import java.util.List;

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
		// TODO Auto-generated method stub

	}

	private void set(List<Vector2f> vertices) {
		// TODO: Check: Expected winding order CCW

		final int lNumLocalVertices = vertices.size();
		for (int i = 0; i < lNumLocalVertices; i++) {
			mTransformedVertices.add(new Vector2f(vertices.get(i)));
		}

		// TODO: Set wdith/height/radius?

	}

	// --------------------------------------
	// Factory-Methods
	// --------------------------------------

	public static PolygonShape createPolygonShape(List<Vector2f> localVertices, float unitPositionX, float density, float restitution, float staticFriction, float dynamicFriction) {
		final var lNewBoxShape = new PolygonShape();

		lNewBoxShape.set(localVertices);
		lNewBoxShape.density = Math.abs(density);
		lNewBoxShape.staticFriction = MathHelper.clamp(staticFriction, 0.f, 1.f);
		lNewBoxShape.dynamicFriction = MathHelper.clamp(dynamicFriction, 0.f, 1.f);
		lNewBoxShape.restitution = MathHelper.clamp(restitution, 0f, 1f);

		return lNewBoxShape;
	}

}
