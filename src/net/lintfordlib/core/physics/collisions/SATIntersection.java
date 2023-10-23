package net.lintfordlib.core.physics.collisions;

import java.util.List;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.ShapeType;

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

	public static final boolean checkCollides(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold manifold) {
		final var lShapeType = bodyA.shape().shapeType();

		manifold.bodyA = bodyA;
		manifold.bodyB = bodyB;

		if (lShapeType == ShapeType.Polygon) {
			return intersectionsPolygonShape(bodyA, bodyB, manifold);
		} else if (lShapeType == ShapeType.Box) {
			return intersectionsBoxShape(bodyA, bodyB, manifold);
		} else if (lShapeType == ShapeType.LineWidth) {
			return intersectionsLineWidthShape(bodyA, bodyB, manifold);
		} else if (lShapeType == ShapeType.Circle) {
			return intersectionsCircleShape(bodyA, bodyB, manifold);
		}

		return false;
	}

	private static boolean intersectionsPolygonShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeB = bodyB.shape();

		final var lShapeAWorldVertices = bodyA.getWorldVertices();
		final var lShapeBWorldVertices = bodyB.getWorldVertices();

		if (lShapeB.shapeType() == ShapeType.Polygon) {
			return intersectsPolygons(lShapeAWorldVertices, lShapeBWorldVertices, manifold);
		} else if (lShapeB.shapeType() == ShapeType.Box) {
			return intersectsPolygons(lShapeAWorldVertices, lShapeBWorldVertices, manifold);
		} else if (lShapeB.shapeType() == ShapeType.LineWidth) {
			final var lLineRadius = bodyB.shape().height() * .5f;
			return intersectsLinePolygon(lShapeBWorldVertices, lLineRadius, lShapeAWorldVertices, bodyA.transform.p.x, bodyA.transform.p.y, manifold);
		} else if (lShapeB.shapeType() == ShapeType.Circle) {
			return intersectsCirclePolygon(bodyB.transform.p.x, bodyB.transform.p.y, bodyB.shape().radius(), lShapeAWorldVertices, bodyA.transform.p.x, bodyA.transform.p.y, manifold);
		}

		return false;
	}

	private static boolean intersectionsBoxShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeB = bodyB.shape();

		final var lShapeAWorldVertices = bodyA.getWorldVertices();
		final var lShapeBWorldVertices = bodyB.getWorldVertices();

		// Shape A is a box

		if (lShapeB.shapeType() == ShapeType.Polygon) {
			return intersectsPolygons(lShapeAWorldVertices, lShapeBWorldVertices, manifold);
		} else if (lShapeB.shapeType() == ShapeType.Box) {
			return intersectsPolygons(lShapeAWorldVertices, lShapeBWorldVertices, manifold);
		} else if (lShapeB.shapeType() == ShapeType.LineWidth) {
			final var lLineRadius = bodyB.shape().height() * .5f;
			return intersectsLinePolygon(lShapeBWorldVertices, lLineRadius, lShapeAWorldVertices, bodyA.transform.p.x, bodyA.transform.p.y, manifold);
		} else if (lShapeB.shapeType() == ShapeType.Circle) {
			return intersectsCirclePolygon(bodyB.transform.p.x, bodyB.transform.p.y, bodyB.shape().radius(), lShapeAWorldVertices, bodyA.transform.p.x, bodyA.transform.p.y, manifold);
		}

		return false;
	}

	private static boolean intersectionsLineWidthShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lOtherShapeType = bodyB.shape().shapeType();

		final var lLineWorldVertices = bodyA.getWorldVertices();
		final var lLineRadius = bodyA.shape().height() * .5f;

		if (lOtherShapeType == ShapeType.Polygon) {
			if (intersectsLinePolygon(lLineWorldVertices, lLineRadius, bodyB.getWorldVertices(), bodyB.transform.p.x, bodyB.transform.p.y, manifold)) {
				manifold.normal.x = -manifold.normal.x;
				manifold.normal.y = -manifold.normal.y;
				return true;
			}
		} else if (lOtherShapeType == ShapeType.Box) {
			if (intersectsLinePolygon(lLineWorldVertices, lLineRadius, bodyB.getWorldVertices(), bodyB.transform.p.x, bodyB.transform.p.y, manifold)) {
				manifold.normal.x = -manifold.normal.x;
				manifold.normal.y = -manifold.normal.y;
				return true;
			}
		} else if (lOtherShapeType == ShapeType.LineWidth) {
			return false; // TODO: Line / Line intersections
		} else if (lOtherShapeType == ShapeType.Circle) {
			if (intersectsLineCircle(lLineWorldVertices, lLineRadius, bodyB.transform.p.x, bodyB.transform.p.y, bodyB.shape().radius(), manifold)) {
				return true;
			}
		}

		return false;
	}

	private static boolean intersectionsCircleShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeBType = bodyB.shape().shapeType();

		final var lShapeAWorldVertices = bodyA.getWorldVertices();
		final var lShapeBWorldVertices = bodyB.getWorldVertices();

		switch (lShapeBType) {
		case Polygon:
			if (intersectsCirclePolygon(bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), lShapeBWorldVertices, bodyB.transform.p.x, bodyB.transform.p.y, manifold)) {
				manifold.normal.x = -manifold.normal.x;
				manifold.normal.y = -manifold.normal.y;
				return true;
			}
			return false;

		case Box:
			if (intersectsCirclePolygon(bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), lShapeBWorldVertices, bodyB.transform.p.x, bodyB.transform.p.y, manifold)) {
				manifold.normal.x = -manifold.normal.x;
				manifold.normal.y = -manifold.normal.y;
				return true;
			}
			return false;

		case LineWidth:
			final var lLineRadius = bodyB.shape().height() * .5f;
			if (intersectsLineCircle(lShapeBWorldVertices, lLineRadius, bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), manifold)) {
				manifold.normal.x = -manifold.normal.x;
				manifold.normal.y = -manifold.normal.y;
				return true;
			}
			return false;

		case Circle:
			return intersectsCircles(lShapeAWorldVertices, bodyA.shape().radius(), lShapeBWorldVertices, bodyB.shape().radius(), manifold);

		default:
			return false;
		}

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	// Polygons

	public static boolean intersectsPolygons(List<Vector2f> verticesA, List<Vector2f> verticesB, ContactManifold result) {
		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = verticesA.size();
		final var lPolyAIsCwWinding = MathHelper.isCwWinding(verticesA.get(0), verticesA.get(1), verticesA.get(2));
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = verticesA.get(i);
			final var vb = verticesA.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			var axisX = -edgeY / edgeLength;
			var axisY = edgeX / edgeLength;

			if (lPolyAIsCwWinding == false) {
				axisX = edgeY / edgeLength;
				axisY = -edgeX / edgeLength;
			}

			projectVertices(verticesA, axisX, axisY, projectionResult1);
			projectVertices(verticesB, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			// overlap
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

		final var lNumVertsB = verticesB.size();
		final var lPolyBIsCwWinding = MathHelper.isCwWinding(verticesB.get(0), verticesB.get(1), verticesB.get(2));
		for (int i = 0; i < lNumVertsB; i++) {
			final var va = verticesB.get(i);
			final var vb = verticesB.get((i + 1) % lNumVertsB);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			var axisX = -edgeY / edgeLength;
			var axisY = edgeX / edgeLength;

			if (lPolyBIsCwWinding == false) {
				axisX = edgeY / edgeLength;
				axisY = -edgeX / edgeLength;
			}

			projectVertices(verticesA, axisX, axisY, projectionResult1);
			projectVertices(verticesB, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
				return false; // early out
			}

			// overlap
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

	// TODO: Boxes should only project half of the edges
	public static boolean intersectsPolygonBox() {
		return false;
	}

	// Circles

	public static boolean intersectsCirclePolygon(float circleX, float circleY, float circleRadius, List<Vector2f> polygonVertices, float polygonCenterX, float polygonCenterY, ContactManifold result) {
		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = polygonVertices.size();
		final var lPolyIsCwWinding = MathHelper.isCwWinding(polygonVertices.get(0), polygonVertices.get(1), polygonVertices.get(2));
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = polygonVertices.get(i);
			final var vb = polygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			var axisX = -edgeY / edgeLength;
			var axisY = edgeX / edgeLength;

			if (lPolyIsCwWinding == false) {
				axisX = edgeY / edgeLength;
				axisY = -edgeX / edgeLength;
			}

			projectVertices(polygonVertices, axisX, axisY, projectionResult1);
			projectCircle(circleX, circleY, circleRadius, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			// overlap
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

		float axisX = polygonCenterX - circleX;
		float axisY = polygonCenterY - circleY;

		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		axisX /= axisLength;
		axisY /= axisLength;

		projectVertices(polygonVertices, axisX, axisY, projectionResult1);
		projectCircle(circleX, circleY, circleRadius, axisX, axisY, projectionResult2);

		if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
			result.isIntersecting = false;
			return false; // early out
		}

		// overlap
		final float minimumDepthValueA = (projectionResult1.max - projectionResult2.min);
		if (minimumDepthValueA < result.depth) {
			// this never seems to be hit
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

	public static boolean intersectsCircles(List<Vector2f> circleAWorldVertices, float radiusA, List<Vector2f> circleBWorldVertices, float radiusB, ContactManifold result) {
		final var circleAX = circleAWorldVertices.get(0).x;
		final var circleAY = circleAWorldVertices.get(0).y;

		final var circleBX = circleBWorldVertices.get(0).x;
		final var circleBY = circleBWorldVertices.get(0).y;

		final var dist = Vector2f.dst(circleAX, circleAY, circleBX, circleBY);
		final var radii = radiusA + radiusB;

		result.isIntersecting = false;

		if (dist >= radii)
			return false;

		result.normal.x = circleBX - circleAX;
		result.normal.y = circleBY - circleAY;
		result.normal.nor();
		result.depth = radii - dist;

		result.isIntersecting = true;

		return true;
	}

	public static boolean intersectsCircleBox() {
		return false;
	}

	// Lines

	public static boolean intersectsLinePolygon(List<Vector2f> lineVertices, float lineRadius, List<Vector2f> polygonVertices, float polygonCenterX, float polygonCenterY, ContactManifold result) {

		final float lsx = lineVertices.get(0).x;
		final float lsy = lineVertices.get(0).y;
		final float lex = lineVertices.get(1).x;
		final float ley = lineVertices.get(1).y;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = polygonVertices.size();
		final var lPolyIsCwWinding = MathHelper.isCwWinding(polygonVertices.get(0), polygonVertices.get(1), polygonVertices.get(2));
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = polygonVertices.get(i);
			final var vb = polygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			var axisX = -edgeY / edgeLength;
			var axisY = edgeX / edgeLength;

			if (lPolyIsCwWinding == false) {
				axisX = edgeY / edgeLength;
				axisY = -edgeX / edgeLength;
			}

			projectVertices(polygonVertices, axisX, axisY, projectionResult1);
			projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}

			// overlap
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

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult1.max) {
				result.isIntersecting = false;
				return false; // early out
			}
		}

		// project on perp vector (normal) from line-center
		// ---

		float axisX = -(ley - lsy) * .5f;
		float axisY = (lex - lsx) * .5f;

		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		axisX /= axisLength;
		axisY /= axisLength;

		projectVertices(polygonVertices, axisX, axisY, projectionResult1);
		projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult2);

		if (projectionResult1.min >= projectionResult2.max || projectionResult2.min >= projectionResult1.max) {
			result.isIntersecting = false;
			return false; // early out
		}

		// overlap
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

		// ---

		return true;
	}

	// This isn't a SAT test - just a point/line intersection test
	public static boolean intersectsLineCircle(List<Vector2f> lineVertices, float lineRadius, float circleX, float circleY, float radius, ContactManifold result) {
		final var sx = lineVertices.get(0).x;
		final var sy = lineVertices.get(0).y;

		final var ex = lineVertices.get(1).x;
		final var ey = lineVertices.get(1).y;

		final var lLineX1 = ex - sx;
		final var lLineY1 = ey - sy;

		final var lLineX2 = circleX - sx;
		final var lLineY2 = circleY - sy;

		final var lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;
		final var v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
		final var t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

		final var lClosestPointX = sx + t * lLineX1;
		final var lClosestPointY = sy + t * lLineY1;

		final var lCirleLineDistance2 = (float) (circleX - lClosestPointX) * (circleX - lClosestPointX) + (circleY - lClosestPointY) * (circleY - lClosestPointY);
		final var lWidthCircleAndLine2 = (lineRadius + radius) * (lineRadius + radius);

		if (lCirleLineDistance2 <= lWidthCircleAndLine2) {
			final var minimumDepthValueB = lWidthCircleAndLine2 - lCirleLineDistance2;
			result.depth = minimumDepthValueB;
			result.normal.x = circleX - lClosestPointX;
			result.normal.y = circleY - lClosestPointY;
			result.normal.nor();

			return true;
		}

		return false;
	}

	public static boolean intersectsLineLine(List<Vector2f> lineAVertices, float lineARadius, List<Vector2f> lineBVertices, float lineBRadius, ContactManifold result) {
		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var ax = lineAVertices.get(0).x;
		final var ay = lineAVertices.get(0).y;
		final var bx = lineAVertices.get(1).x;
		final var by = lineAVertices.get(1).y;
		final var px = lineBVertices.get(0).x;
		final var py = lineBVertices.get(0).y;
		final var qx = lineBVertices.get(1).x;
		final var qy = lineBVertices.get(1).y;

		{// project line 2 onto line 1
			float tx = (bx - ax) * .5f;
			float ty = (by - ay) * .5f;

			var axis01X = -ty;
			var axis01Y = tx;
			final var axis01Length = Math.sqrt(tx * tx + ty * ty);

			axis01X /= axis01Length;
			axis01Y /= axis01Length;

			projectLineWidth(ax, ay, bx, by, lineARadius, axis01X, axis01Y, projectionResult1);
			projectLineWidth(px, py, qx, qy, lineBRadius, axis01X, axis01Y, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
				return false; // early out
			}

			// overlap
			final float minimumDepthValueA = Math.abs(projectionResult1.max - projectionResult2.min);
			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = -axis01X;
				result.normal.y = -axis01Y;
			}

			final float minimumDepthValueB = Math.abs(projectionResult2.max - projectionResult1.min);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = axis01X;
				result.normal.y = axis01Y;
			}
		}

		{// project line 1 onto line 2
			var axis02X = -(qy - py) * .5f;
			var axis02Y = (qx - px) * .5f;
			final var axis02Length = Math.sqrt(axis02X * axis02X + axis02Y * axis02Y);

			axis02X /= axis02Length;
			axis02Y /= axis02Length;

			projectionResult1.min = 0;
			projectionResult1.max = 0;
			projectLineWidth(px, py, qx, qy, lineARadius, axis02X, axis02Y, projectionResult1);
			projectLineWidth(ax, ay, bx, by, lineBRadius, axis02X, axis02Y, projectionResult2);

			if (projectionResult1.min >= projectionResult2.max || projectionResult1.min >= projectionResult2.max) {
				return false; // early out
			}

			// overlap
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

	public static boolean intersectsLineBox() {
		// TODO: Line / Box tests
		return false;
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
		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

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
		toFill.min = Math.min(Vector2f.dot(sx - radius * axisX, sy - radius * axisY, axisX, axisY), Vector2f.dot(sx + radius * axisX, sy + radius * axisY, axisX, axisY));
		toFill.max = Math.max(Vector2f.dot(ex - radius * axisX, ey - radius * axisY, axisX, axisY), Vector2f.dot(ex + radius * axisX, ey + radius * axisY, axisX, axisY));

		if (toFill.min > toFill.max) {
			final float t = toFill.min;
			toFill.min = toFill.max;
			toFill.max = t;
		}
	}
}