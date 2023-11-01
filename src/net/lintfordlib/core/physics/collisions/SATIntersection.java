package net.lintfordlib.core.physics.collisions;

import java.util.List;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.shapes.BaseShape;

public class SATIntersection {

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	private static class SatCollisionProjectionResult {
		public float min;
		public float max;

		public void set(float min, float max) {
			this.min = min;
			this.max = max;
		}
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final SatCollisionProjectionResult projectionResult1 = new SatCollisionProjectionResult();
	private static final SatCollisionProjectionResult projectionResult2 = new SatCollisionProjectionResult();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public static boolean checkCollides(final ContactManifold manifold) {
		final var lShapeA = manifold.shapeA;
		final var lShapeAType = lShapeA.shapeType();

		switch (lShapeAType) {
		case Polygon:
			return intersectionsPolygonShape(manifold);

		case LineWidth:
			return intersectionsLineWidthShape(manifold);

		case Circle:
			return intersectionsCircleShape(manifold);

		}

		return false;
	}

	// ---

	private static boolean intersectionsPolygonShape(ContactManifold contactManifold) {
		final var lShapeB = contactManifold.shapeB;
		final var lShapeBType = lShapeB.shapeType();

		final var lPolygonShape_A = contactManifold.shapeA;
		final var lOtherShape_B = contactManifold.shapeB;

		switch (lShapeBType) {
		case Polygon:
			return intersectsPolygons(lPolygonShape_A, lOtherShape_B, contactManifold);

		case LineWidth:
			return intersectsLinePolygon(lOtherShape_B, lPolygonShape_A, contactManifold);

		case Circle:
			return intersectsCirclePolygon(lOtherShape_B, lPolygonShape_A, contactManifold);
		}

		return false;
	}

	private static boolean intersectionsLineWidthShape(ContactManifold contactManifold) {
		final var lShapeB = contactManifold.shapeB;
		final var lShapeBType = lShapeB.shapeType();

		final var lLineWidthShape_A = contactManifold.shapeA;
		final var lOtherShape_B = contactManifold.shapeB;

		switch (lShapeBType) {
		case Polygon:
			return intersectsLinePolygon(lLineWidthShape_A, lOtherShape_B, contactManifold);

		case LineWidth:
			return intersectsLineLine(lLineWidthShape_A, lOtherShape_B, contactManifold);

		case Circle:
			return intersectsLineCircle(lLineWidthShape_A, lOtherShape_B, contactManifold);
		}

		return false;
	}

	private static boolean intersectionsCircleShape(ContactManifold contactManifold) {
		final var lCircleShape_A = contactManifold.shapeA;
		final var lOtherShape_B = contactManifold.shapeB;

		final var lShapeBType = lOtherShape_B.shapeType();

		switch (lShapeBType) {
		case Polygon:
			return intersectsCirclePolygon(lCircleShape_A, lOtherShape_B, contactManifold);

		case LineWidth:
			return intersectsLineCircle(lOtherShape_B, lCircleShape_A, contactManifold);

		case Circle:
			return intersectsCircles(lCircleShape_A, lOtherShape_B, contactManifold);

		default:
			return false;
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	// Polygons

	private static boolean intersectsPolygons(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		result.shapeA = shapeA;
		result.shapeB = shapeB;

		final var lPolygonAVertices = shapeA.getTransformedVertices();
		final var lPolygonBVertices = shapeB.getTransformedVertices();

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = lPolygonAVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = lPolygonAVertices.get(i);
			final var vb = lPolygonAVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			final var axisX = edgeY / edgeLength;
			final var axisY = -edgeX / edgeLength;

			projectVertices(lPolygonAVertices, axisX, axisY, projectionResult1);
			projectVertices(lPolygonBVertices, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}
		}

		final var lNumVertsB = lPolygonBVertices.size();
		for (int i = 0; i < lNumVertsB; i++) {
			final var va = lPolygonBVertices.get(i);
			final var vb = lPolygonBVertices.get((i + 1) % lNumVertsB);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			final var axisX = edgeY / edgeLength;
			final var axisY = -edgeX / edgeLength;

			projectVertices(lPolygonAVertices, axisX, axisY, projectionResult1);
			projectVertices(lPolygonBVertices, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}
		}

		return true;
	}

	// Circles

	private static boolean intersectsCirclePolygon(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		final var lCircleShape = shapeA;
		final var lPolygonShape = shapeB;

		result.shapeA = lCircleShape;
		result.shapeB = lPolygonShape;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lCircleVertices = lCircleShape.getTransformedVertices();
		final var lCircleX = lCircleVertices.get(0).x;
		final var lCircleY = lCircleVertices.get(0).y;
		final var lCircleRadius = lCircleShape.radius();

		// TODO: Calculate centroid of polygon
		final var lPolygonCenterX = lPolygonShape.localCenter.x;
		final var lPolygonCenterY = lPolygonShape.localCenter.y;
		final var lPolygonVertices = lPolygonShape.getTransformedVertices();

		final var lNumVertsA = lPolygonVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = lPolygonVertices.get(i);
			final var vb = lPolygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			final var axisX = edgeY / edgeLength;
			final var axisY = -edgeX / edgeLength;

			projectCircle(lCircleX, lCircleY, lCircleRadius, axisX, axisY, projectionResult1);
			projectVertices(lPolygonVertices, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}
		}

		float axisX = lPolygonCenterX - lCircleX;
		float axisY = lPolygonCenterY - lCircleY;

		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		axisX /= axisLength;
		axisY /= axisLength;

		projectCircle(lCircleX, lCircleY, lCircleRadius, axisX, axisY, projectionResult1);
		projectVertices(lPolygonVertices, axisX, axisY, projectionResult2);

		if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
			result.isIntersecting = false;
			return false; // early out
		}

		final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
		if (minimumDepthValueA < result.depth) {
			result.depth = minimumDepthValueA;
			result.normal.x = axisX;
			result.normal.y = axisY;
		}

		final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
		if (minimumDepthValueB < result.depth) {
			result.depth = minimumDepthValueB;
			result.normal.x = -axisX;
			result.normal.y = -axisY;
		}

		return true;
	}

	private static boolean intersectsCircles(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		final var lCircleAShape = shapeA;
		final var lCircleBShape = shapeB;

		result.shapeA = lCircleAShape;
		result.shapeB = lCircleBShape;

		final var lCircleAVertices = lCircleAShape.getTransformedVertices();
		final var lCircleAPositionX = lCircleAVertices.get(0).x;
		final var lCircleAPositionY = lCircleAVertices.get(0).y;
		final var lCircleARadius = lCircleAShape.radius();

		final var lCircleBVertices = lCircleBShape.getTransformedVertices();
		final var lCircleBPositionX = lCircleBVertices.get(0).x;
		final var lCircleBPositionY = lCircleBVertices.get(0).y;
		final var lCircleBRadius = lCircleBShape.radius();

		final var lDist = Vector2f.dst(lCircleAPositionX, lCircleAPositionY, lCircleBPositionX, lCircleBPositionY);
		final var rLengthRadii = lCircleARadius + lCircleBRadius;

		result.isIntersecting = false;

		if (lDist >= rLengthRadii)
			return false;

		result.normal.x = lCircleBPositionX - lCircleAPositionX;
		result.normal.y = lCircleBPositionY - lCircleAPositionY;
		result.normal.nor();
		result.depth = rLengthRadii - lDist;

		result.isIntersecting = true;

		return true;
	}

	// Lines

	private static boolean intersectsLinePolygon(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		final var lLineShape = shapeA;
		final var lPolygonShape = shapeB;

		result.shapeA = lLineShape;
		result.shapeB = lPolygonShape;

		final var lineVertices = lLineShape.getTransformedVertices();
		final var lineRadius = lLineShape.height() * .5f;

		final var polygonVertices = lPolygonShape.getTransformedVertices();

		final var lsx = lineVertices.get(0).x;
		final var lsy = lineVertices.get(0).y;
		final var lex = lineVertices.get(1).x;
		final var ley = lineVertices.get(1).y;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		// project on perp vector (normal) from line-center
		{
			float edgeX = (lex - lsx) * .5f;
			float edgeY = (ley - lsy) * .5f;

			final float axisLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			final var axisX = edgeY / axisLength;
			final var axisY = -edgeX / axisLength;

			projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult1);
			projectVertices(polygonVertices, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}
		}

		final var lNumPolygonVertices = polygonVertices.size();
		for (int i = 0; i < lNumPolygonVertices; i++) {
			final var va = polygonVertices.get(i);
			final var vb = polygonVertices.get((i + 1) % lNumPolygonVertices);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			var axisX = edgeY / edgeLength;
			var axisY = -edgeX / edgeLength;

			projectVertices(polygonVertices, axisX, axisY, projectionResult2);
			projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult1);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}

		}

		return true;
	}

	private static boolean intersectsLineCircle(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		final var lLineShape = shapeA;
		final var lCircleShape = shapeB;

		result.shapeA = lLineShape;
		result.shapeB = lCircleShape;

		final var lLineVertices = lLineShape.getTransformedVertices();
		final var lLineRadius = lLineShape.height() * .5f;

		final var lCircleVertices = lCircleShape.getTransformedVertices();
		final var lCirclePositionX = lCircleVertices.get(0).x;
		final var lCirclePositionY = lCircleVertices.get(0).y;
		final var lCircleRadius = lCircleShape.radius();

		final var sx = lLineVertices.get(0).x;
		final var sy = lLineVertices.get(0).y;

		final var ex = lLineVertices.get(1).x;
		final var ey = lLineVertices.get(1).y;

		final var lLineX1 = ex - sx;
		final var lLineY1 = ey - sy;

		final var lLineX2 = lCirclePositionX - sx;
		final var lLineY2 = lCirclePositionY - sy;

		final var lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;
		final var v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
		final var t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

		final var lClosestPointX = sx + t * lLineX1;
		final var lClosestPointY = sy + t * lLineY1;

		final var lCirleLineDistance2 = (float) (lCirclePositionX - lClosestPointX) * (lCirclePositionX - lClosestPointX) + (lCirclePositionY - lClosestPointY) * (lCirclePositionY - lClosestPointY);
		final var lWidthCircleAndLine2 = (lLineRadius + lCircleRadius) * (lLineRadius + lCircleRadius);

		if (lCirleLineDistance2 <= lWidthCircleAndLine2) {
			final var minimumDepthValueB = lWidthCircleAndLine2 - lCirleLineDistance2;

			result.depth = minimumDepthValueB;

			result.normal.x = lCirclePositionX - lClosestPointX;
			result.normal.y = lCirclePositionY - lClosestPointY;

			result.normal.nor();

			return true;
		}

		return false;
	}

	private static boolean intersectsLineLine(final BaseShape shapeA, final BaseShape shapeB, final ContactManifold result) {
		final var lLineAShape = result.shapeA;
		final var lLineBShape = result.shapeB;

		result.shapeA = lLineAShape;
		result.shapeB = lLineBShape;

		final var lLineAVertices = lLineAShape.getTransformedVertices();
		final var lLineARadius = lLineAShape.height() * .5f;

		final var lLineBVertices = lLineBShape.getTransformedVertices();
		final var lLineBRadius = lLineBShape.height() * .5f;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var ax = lLineAVertices.get(0).x;
		final var ay = lLineAVertices.get(0).y;
		final var bx = lLineAVertices.get(1).x;
		final var by = lLineAVertices.get(1).y;
		final var px = lLineBVertices.get(0).x;
		final var py = lLineBVertices.get(0).y;
		final var qx = lLineBVertices.get(1).x;
		final var qy = lLineBVertices.get(1).y;

		{ // project line 2 onto line 1
			float tx = (bx - ax) * .5f;
			float ty = (by - ay) * .5f;

			var axis01X = -ty;
			var axis01Y = tx;
			final var axis01Length = Math.sqrt(tx * tx + ty * ty);

			axis01X /= axis01Length;
			axis01Y /= axis01Length;

			projectLineWidth(ax, ay, bx, by, lLineARadius, axis01X, axis01Y, projectionResult1);
			projectLineWidth(px, py, qx, qy, lLineBRadius, axis01X, axis01Y, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = Math.abs(projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axis01X;
				result.normal.y = axis01Y;
			}

			final float minimumDepthValueB = Math.abs(projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axis01X;
				result.normal.y = -axis01Y;
			}
		}

		{ // project line 1 onto line 2
			var axis02X = -(qy - py) * .5f;
			var axis02Y = (qx - px) * .5f;
			final var axis02Length = Math.sqrt(axis02X * axis02X + axis02Y * axis02Y);

			axis02X /= axis02Length;
			axis02Y /= axis02Length;

			projectLineWidth(ax, ay, bx, by, lLineBRadius, axis02X, axis02Y, projectionResult1);
			projectLineWidth(px, py, qx, qy, lLineARadius, axis02X, axis02Y, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			final float minimumDepthValueA = Math.abs(projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axis02X;
				result.normal.y = axis02Y;
			}

			final float minimumDepthValueB = Math.abs(projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axis02X;
				result.normal.y = -axis02Y;
			}
		}

		return true;
	}

	// Projections

	private static void projectVertices(List<Vector2f> vertices, float axisX, float axisY, SatCollisionProjectionResult result) {
		var min = Float.MAX_VALUE;
		var max = -Float.MAX_VALUE;

		final var lNumVerts = vertices.size();
		for (int i = 0; i < lNumVerts; i++) {
			final var v = vertices.get(i);
			final var proj = Vector2f.dot(v.x, v.y, axisX, axisY);

			if (proj < min)
				min = proj;

			if (proj > max)
				max = proj;
		}

		result.set(min, max);
	}

	private static void projectCircle(float centerX, float centerY, float radius, float axisX, float axisY, SatCollisionProjectionResult result) {
		final var axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		final var directionX = axisX / axisLength;
		final var directionY = axisY / axisLength;

		final var directionAndDirX = directionX * radius;
		final var directionAndDirY = directionY * radius;

		final var point1X = centerX + directionAndDirX;
		final var point1Y = centerY + directionAndDirY;

		final var point2X = centerX - directionAndDirX;
		final var point2Y = centerY - directionAndDirY;

		result.min = Vector2f.dot(point1X, point1Y, axisX, axisY);
		result.max = Vector2f.dot(point2X, point2Y, axisX, axisY);

		if (result.min > result.max) {
			final float t = result.min;
			result.min = result.max;
			result.max = t;
		}
	}

	@SuppressWarnings("unused")
	private static void projectLine(float sx, float sy, float ex, float ey, float axisX, float axisY, SatCollisionProjectionResult toFill) {
		toFill.min = Vector2f.dot(sx, sy, axisX, axisY);
		toFill.max = Vector2f.dot(ex, ey, axisX, axisY);

		if (toFill.min > toFill.max) {
			final float t = toFill.min;
			toFill.min = toFill.max;
			toFill.max = t;
		}
	}

	private static void projectLineWidth(float sx, float sy, float ex, float ey, float radius, float axisX, float axisY, SatCollisionProjectionResult toFill) {
		toFill.min = Float.MAX_VALUE;
		toFill.max = Float.MIN_VALUE;

		var min = Float.MAX_VALUE;
		var max = -Float.MAX_VALUE;
		float proj = 0;

		// TODO: Check if we can pre-compute these (obviously without the axis')

		proj = Vector2f.dot(sx - radius * axisX, sy - radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(sx + radius * axisX, sy + radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(ex - radius * axisX, ey - radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		proj = Vector2f.dot(ex + radius * axisX, ey + radius * axisY, axisX, axisY);

		if (proj < min)
			min = proj;

		if (proj > max)
			max = proj;

		toFill.set(min, max);
	}
}