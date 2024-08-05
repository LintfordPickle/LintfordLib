package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.maths.CollisionExtensions;
import net.lintfordlib.core.physics.dynamics.RigidBody;

public class RigidBodyCollisionExtensions {

	private RigidBodyCollisionExtensions() {
	}

	public static boolean pointIntersectsBody(RigidBody body, float x, float y) {
		if (!pointIntersectsBodyRadius(body, x, y))
			return false;

		switch (body.shape().shapeType()) {
		case Polygon: {
			return intersectsPointPolygonBody(body, x, y);
		}

		case LineWidth: {
			return pointIntersectsLineWidthBody(body, x, y);
		}

		default:
		case Circle: {
			return pointIntersectsCircleBody(body, x, y);
		}

		}
	}

	public static boolean pointIntersectsBodyRadius(RigidBody body, float x, float y) {
		final float xx = body.transform.p.x - x;
		final float yy = body.transform.p.y - y;
		return (xx * xx + yy * yy) < (body.shape().radius() * body.shape().radius());
	}

	public static final boolean pointIntersectsBoxBody(RigidBody body, float x, float y) {

		final var lVertices = body.getWorldVertices();

		assert (lVertices.size() == 4) : "pointIntersectsBoxBody requires 4 vertices for the quadrilateral.";

		return CollisionExtensions.pointIntersectsQuadrilateralPolygon(lVertices, x, y);
	}

	public static final boolean pointIntersectsLineWidthBody(RigidBody lineWidthBody, float x, float y) {
		final var lLineVertices = lineWidthBody.getWorldVertices();

		assert (lLineVertices.size() == 2) : "pointIntersectsLineWidthBody requires 2 vertices for the line.";

		final var lLineRadius = lineWidthBody.shape().height() * .5f;
		return CollisionExtensions.pointIntersectsLineWidth(lLineVertices, lLineRadius, x, y, 1.f * ConstantsPhysics.PixelsToUnits());
	}

	public static final boolean intersectsPointPolygonBody(RigidBody concavePolygonBody, float x, float y) {
		final var polygonVertices = concavePolygonBody.getWorldVertices();
		return CollisionExtensions.intersectsPointPolygon(polygonVertices, x, y);
	}

	public static final boolean pointIntersectsCircleBody(RigidBody circleBody, float pointX, float pointY) {
		final var circleX = circleBody.transform.p.x;
		final var circleY = circleBody.transform.p.y;
		final var circleRadius = circleBody.shape().radius();

		return CollisionExtensions.pointIntersectsCircle(circleX, circleY, circleRadius, pointX, pointY);
	}
}