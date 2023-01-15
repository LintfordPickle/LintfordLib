package net.lintford.library.core.physics.collisions;

import java.util.List;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.maths.MathHelper;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.physics.dynamics.RigidBody;
import net.lintford.library.core.physics.dynamics.ShapeType;

public class SAT {

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	private static class PointSegmentResult {
		public float dist2;
		public float contactX;
		public float contactY;
	}

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

	public static final ContactManifold tempResult = new ContactManifold();

	private static final PointSegmentResult pointSegmentResult = new PointSegmentResult();

	private static final SatCollisionProjectionResult projectionResult1 = new SatCollisionProjectionResult();
	private static final SatCollisionProjectionResult projectionResult2 = new SatCollisionProjectionResult();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public static boolean checkCollides(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		manifold.bodyA = bodyA;
		manifold.bodyB = bodyB;

		// TODO: Still missing intersection tests for Box shapes (same as polygon, just half the axis checks needed).

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon)
				return intersectsPolygons(bodyA.getTransformedVertices(), bodyB.getTransformedVertices(), manifold);
			else if (lShapeTypeB == ShapeType.Circle)
				return intersectsCirclePolygon(bodyB.x, bodyB.y, bodyB.radius, bodyA.getTransformedVertices(), bodyA.x, bodyA.y, manifold);
			else if (lShapeTypeB == ShapeType.LineWidth) {
				if (intersectsLinePolygon(bodyB.getTransformedVertices(), bodyA.getTransformedVertices(), bodyA.x, bodyA.y, manifold)) {
					return true;
				}
			}

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (intersectsCirclePolygon(bodyA.x, bodyA.y, bodyA.radius, bodyB.getTransformedVertices(), bodyB.x, bodyB.y, manifold)) {
					manifold.normal.x = -manifold.normal.x;
					manifold.normal.y = -manifold.normal.y;
					return true;
				}

			} else if (lShapeTypeB == ShapeType.Circle) {
				if (intersectsCircles(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.radius, manifold)) {
					return true;
				}
			} else if (lShapeTypeB == ShapeType.LineWidth) {
				if (intersectsLineCircle(bodyB.getTransformedVertices(), bodyB.height, bodyA.x, bodyA.y, bodyA.radius, manifold)) {
					return true;
				}
			}
		} else if (lShapeTypeA == ShapeType.LineWidth) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (intersectsLinePolygon(bodyA.getTransformedVertices(), bodyB.getTransformedVertices(), bodyB.x, bodyB.y, manifold)) {
					manifold.normal.x = -manifold.normal.x;
					manifold.normal.y = -manifold.normal.y;
					return true;
				}
			} else if (lShapeTypeB == ShapeType.Circle) {
				if (intersectsLineCircle(bodyA.getTransformedVertices(), bodyA.height, bodyB.x, bodyB.y, bodyB.radius, manifold)) {
					return true;
				}
			}

			// no line/line intersections for now (doesn't work)
		}

		return false;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static boolean intersectsPolygons(List<Vector2f> verticesA, List<Vector2f> verticesB, ContactManifold result) {
		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = verticesA.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = verticesA.get(i);
			final var vb = verticesA.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength2 = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			// note: the cross depends on the winding order of the triangle (this is cw)
			final var axisX = -edgeY / edgeLength2;
			final var axisY = edgeX / edgeLength2;

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
		for (int i = 0; i < lNumVertsB; i++) {
			final var va = verticesB.get(i);
			final var vb = verticesB.get((i + 1) % lNumVertsB);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			// note: the cross depends on the winding order of the triangle (this is cw)
			final var axisX = -edgeY / edgeLength;
			final var axisY = edgeX / edgeLength;

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

	public static boolean intersectsCirclePolygon(float circleX, float circleY, float circleRadius, List<Vector2f> polygonVertices, float polygonCenterX, float polygonCenterY, ContactManifold result) {
		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lNumVertsA = polygonVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = polygonVertices.get(i);
			final var vb = polygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			// note: the cross depends on the winding order of the triangle (this is cw)
			final var axisX = -edgeY / edgeLength;
			final var axisY = edgeX / edgeLength;

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

	public static boolean intersectsCircles(float circleAX, float circleAY, float radiusA, float circleBX, float circleBY, float radiusB, ContactManifold result) {
		final float dist = Vector2f.dst(circleAX, circleAY, circleBX, circleBY);
		final float radii = radiusA + radiusB;

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

	public static boolean intersectsLinePolygon(List<Vector2f> lineVertices, List<Vector2f> polygonVertices, float polygonCenterX, float polygonCenterY, ContactManifold result) {

		final float lsx = lineVertices.get(0).x;
		final float lsy = lineVertices.get(0).y;
		final float lex = lineVertices.get(1).x;
		final float ley = lineVertices.get(1).y;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		// first loop through polygons verts
		final var lNumVertsA = polygonVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = polygonVertices.get(i);
			final var vb = polygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength2 = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			// note: the cross depends on the winding order of the triangle (this is cw)
			final var axisX = -edgeY / edgeLength2;
			final var axisY = edgeX / edgeLength2;

			projectVertices(polygonVertices, axisX, axisY, projectionResult1);
			projectLine(lsx, lsy, lex, ley, axisX, axisY, projectionResult2);

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
		}

		// project on perp vector (normal) from line-center
		// ---

		float axisX = -(ley - lsy) * .5f;
		float axisY = (lex - lsx) * .5f;

		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		axisX /= axisLength;
		axisY /= axisLength;

		projectVertices(polygonVertices, axisX, axisY, projectionResult1);
		projectLine(lsx, lsy, lex, ley, axisX, axisY, projectionResult2);

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

			projectLine(ax, ay, bx, by, axis01X, axis01Y, projectionResult1);
			projectLine(px, py, qx, qy, axis01X, axis01Y, projectionResult2);

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
			projectLine(px, py, qx, qy, axis02X, axis02Y, projectionResult1);
			projectLine(ax, ay, bx, by, axis02X, axis02Y, projectionResult2);

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

	// Contacts

	public static void fillContactPoints(ContactManifold manifold) {
		final var bodyA = manifold.bodyA;
		final var bodyB = manifold.bodyB;

		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		manifold.contactCount = 0;

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon)
				findPolygonPolygonContactPoints(bodyA.getTransformedVertices(), bodyB.getTransformedVertices(), manifold);
			else if (lShapeTypeB == ShapeType.Circle)
				findCirclePolygonContactPoint(bodyB.x, bodyB.y, bodyB.radius, bodyA.x, bodyA.y, bodyA.getTransformedVertices(), manifold);
			else if (lShapeTypeB == ShapeType.LineWidth)
				findLinePolygonContactPoints(bodyB.getTransformedVertices(), bodyA.getTransformedVertices(), manifold);

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon)
				findCirclePolygonContactPoint(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.getTransformedVertices(), manifold);
			else if (lShapeTypeB == ShapeType.Circle)
				findCircleCircleContactPoint(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, manifold);
			else if (lShapeTypeB == ShapeType.LineWidth)
				findLineCircleContactPoint(bodyB.getTransformedVertices(), bodyB.height, bodyA.x, bodyA.y, bodyA.radius, manifold);

		} else if (lShapeTypeA == ShapeType.LineWidth) {
			if (lShapeTypeB == ShapeType.Polygon) {
				findLinePolygonContactPoints(bodyB.getTransformedVertices(), bodyA.getTransformedVertices(), manifold);
			} else if (lShapeTypeB == ShapeType.Circle) {
				findLineCircleContactPoint(bodyA.getTransformedVertices(), bodyA.height, bodyB.x, bodyB.y, bodyB.radius, manifold);
			} else if (lShapeTypeB == ShapeType.LineWidth)
				findLineLineContactPoints(bodyA.getTransformedVertices(), bodyA.height, bodyB.getTransformedVertices(), bodyB.height, manifold);

		}
	}

	private static void findPolygonPolygonContactPoints(List<Vector2f> polyAVerts, List<Vector2f> polyBVerts, ContactManifold contactManifold) {

		float minDist2 = Float.MAX_VALUE;

		final int lNumVertsA = polyAVerts.size();
		final int lNumVertsB = polyAVerts.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var p = polyAVerts.get(i);

			for (int j = 0; j < lNumVertsB; j++) {
				final var va = polyBVerts.get(j);
				final var vb = polyBVerts.get((j + 1) % polyBVerts.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
						contactManifold.contactCount = 2;
						contactManifold.contact2.x = lPointSegmentDist.contactX;
						contactManifold.contact2.y = lPointSegmentDist.contactY;
					}
				}

				// new minimum distance found
				else if (lPointSegmentDist.dist2 < minDist2) {
					minDist2 = lPointSegmentDist.dist2;

					contactManifold.contactCount = 1;
					contactManifold.contact1.x = lPointSegmentDist.contactX;
					contactManifold.contact1.y = lPointSegmentDist.contactY;
				}
			}
		}

		for (int i = 0; i < lNumVertsB; i++) {
			final var p = polyBVerts.get(i);

			for (int j = 0; j < lNumVertsA; j++) {
				final var va = polyAVerts.get(j);
				final var vb = polyAVerts.get((j + 1) % polyAVerts.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
						contactManifold.contactCount = 2;
						contactManifold.contact2.x = lPointSegmentDist.contactX;
						contactManifold.contact2.y = lPointSegmentDist.contactY;
					}
				}

				// new minimum distance found
				else if (lPointSegmentDist.dist2 < minDist2) {
					minDist2 = lPointSegmentDist.dist2;

					contactManifold.contactCount = 1;
					contactManifold.contact1.x = lPointSegmentDist.contactX;
					contactManifold.contact1.y = lPointSegmentDist.contactY;
				}
			}
		}
	}

	private static void findLinePolygonContactPoints(List<Vector2f> lineVerts, List<Vector2f> polyVerts, ContactManifold contactManifold) {
		float minDist2 = Float.MAX_VALUE;

		final int lNumVertsA = polyVerts.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var p = polyVerts.get(i);

			final var va = lineVerts.get(0);
			final var vb = lineVerts.get(1);

			final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

			// a second point with ~same distance
			if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
				if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
					contactManifold.contactCount = 2;
					contactManifold.contact2.x = lPointSegmentDist.contactX;
					contactManifold.contact2.y = lPointSegmentDist.contactY;
				}
			}

			// new minimum distance found
			else if (lPointSegmentDist.dist2 < minDist2) {
				minDist2 = lPointSegmentDist.dist2;

				contactManifold.contactCount = 1;
				contactManifold.contact1.x = lPointSegmentDist.contactX;
				contactManifold.contact1.y = lPointSegmentDist.contactY;
			}
		}

		final int lNumVertsB = lineVerts.size();
		for (int i = 0; i < lNumVertsB; i++) {
			final var p = lineVerts.get(i);

			for (int j = 0; j < lNumVertsA; j++) {
				final var va = polyVerts.get(j);
				final var vb = polyVerts.get((j + 1) % polyVerts.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
						contactManifold.contactCount = 2;
						contactManifold.contact2.x = lPointSegmentDist.contactX;
						contactManifold.contact2.y = lPointSegmentDist.contactY;
					}
				}

				// new minimum distance found
				else if (lPointSegmentDist.dist2 < minDist2) {
					minDist2 = lPointSegmentDist.dist2;

					contactManifold.contactCount = 1;
					contactManifold.contact1.x = lPointSegmentDist.contactX;
					contactManifold.contact1.y = lPointSegmentDist.contactY;
				}
			}
		}
	}

	private static void findCirclePolygonContactPoint(float circleAX, float circleAY, float radiusA, float polyCenterX, float polyCenterY, List<Vector2f> verts, ContactManifold contactManifold) {
		float minDist2 = Float.MAX_VALUE;

		final int lNumVerts = verts.size();
		for (int i = 0; i < lNumVerts; i++) {
			final var va = verts.get(i);
			final var vb = verts.get((i + 1) % verts.size());

			final var lPointSegmentDist = pointSegmentDistance2(circleAX, circleAY, va.x, va.y, vb.x, vb.y);
			if (lPointSegmentDist.dist2 < minDist2) {
				minDist2 = lPointSegmentDist.dist2;

				contactManifold.contact1.x = lPointSegmentDist.contactX;
				contactManifold.contact1.y = lPointSegmentDist.contactY;
				contactManifold.contactCount = 1;
			}
		}
	}

	private static void findCircleCircleContactPoint(float circleAX, float circleAY, float radiusA, float circleBX, float circleBY, ContactManifold contactManifold) {
		var abX = circleBX - circleAX;
		var abY = circleBY - circleAY;

		final var abLength = (float) Math.sqrt(abX * abX + abY * abY);

		abX /= abLength;
		abY /= abLength;

		contactManifold.contact1.x = circleAX + abX * radiusA;
		contactManifold.contact1.y = circleAY + abY * radiusA;
		contactManifold.contactCount = 1;

	}

	private static void findLineCircleContactPoint(List<Vector2f> lineVertices, float lineRadius, float circleX, float circleY, float radius, ContactManifold manifold) {
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

		// project out towards circle by line radius
		final float lClosestPointX = sx + t * lLineX1;
		final float lClosestPointY = sy + t * lLineY1;

		manifold.contact1.x = lClosestPointX - (lClosestPointX - circleX) * lineRadius;
		manifold.contact1.y = lClosestPointY - (lClosestPointY - circleY) * lineRadius;
		manifold.contactCount = 1;
	}

	private static void findLineLineContactPoints(List<Vector2f> lineAVertices, float lineARadius, List<Vector2f> lineBVertices, float lineBRadius, ContactManifold contactManifold) {

		float minDist2 = Float.MAX_VALUE;

		final int lNumVertsA = lineAVertices.size();
		final int lNumVertsB = lineBVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var p = lineAVertices.get(i);

			for (int j = 0; j < lNumVertsB; j++) {
				final var va = lineBVertices.get(j);
				final var vb = lineBVertices.get((j + 1) % lineBVertices.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
						contactManifold.contactCount = 2;
						contactManifold.contact2.x = lPointSegmentDist.contactX;
						contactManifold.contact2.y = lPointSegmentDist.contactY;
					}
				}

				// new minimum distance found
				else if (lPointSegmentDist.dist2 < minDist2) {
					minDist2 = lPointSegmentDist.dist2;

					contactManifold.contactCount = 1;
					contactManifold.contact1.x = lPointSegmentDist.contactX;
					contactManifold.contact1.y = lPointSegmentDist.contactY;
				}
			}
		}

		for (int i = 0; i < lNumVertsB; i++) {
			final var p = lineBVertices.get(i);

			for (int j = 0; j < lNumVertsA; j++) {
				final var va = lineAVertices.get(j);
				final var vb = lineAVertices.get((j + 1) % lineAVertices.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
						contactManifold.contactCount = 2;
						contactManifold.contact2.x = lPointSegmentDist.contactX;
						contactManifold.contact2.y = lPointSegmentDist.contactY;
					}
				}

				// new minimum distance found
				else if (lPointSegmentDist.dist2 < minDist2) {
					minDist2 = lPointSegmentDist.dist2;

					contactManifold.contactCount = 1;
					contactManifold.contact1.x = lPointSegmentDist.contactX;
					contactManifold.contact1.y = lPointSegmentDist.contactY;
				}
			}
		}

	}

	private static PointSegmentResult pointSegmentDistance2(float px, float py, float ax, float ay, float bx, float by) {
		final var abX = bx - ax;
		final var abY = by - ay;

		final var apX = px - ax;
		final var apY = py - ay;

		final var proj = Vector2f.dot(abX, abY, apX, apY);
		final var projLength = Vector2f.dst2(abX, abY);
		final var d = proj / projLength;

		if (d <= 0.f) {
			pointSegmentResult.contactX = ax;
			pointSegmentResult.contactY = ay;
		} else if (d >= 1.f) {
			pointSegmentResult.contactX = bx;
			pointSegmentResult.contactY = by;
		} else {
			pointSegmentResult.contactX = ax + abX * d;
			pointSegmentResult.contactY = ay + abY * d;
		}

		pointSegmentResult.dist2 = Vector2f.dst2(px, py, pointSegmentResult.contactX, pointSegmentResult.contactY);
		return pointSegmentResult;
	}

	// Helper Methods

	public static boolean equalWithinEpsilon(float a, float b) {
		return Math.abs(a - b) < ConstantsPhysics.EPSILON;
	}

	public static boolean equalWithinEpsilon(float p1x, float p1y, float p2x, float p2y) {
		final float xx = p1x - p2x;
		final float yy = p1y - p2y;
		return (xx * xx + yy * yy) < ConstantsPhysics.EPSILON * ConstantsPhysics.EPSILON;
	}
}