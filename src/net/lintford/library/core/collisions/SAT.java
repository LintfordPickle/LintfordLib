package net.lintford.library.core.collisions;

import java.util.List;

import net.lintford.library.core.maths.Vector2f;

public class SAT {

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	private static class PointSegmentResult {
		public float dist2;
		public float contactX;
		public float contactY;
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final ContactManifold tempResult = new ContactManifold();

	private static final Vector2f _vec2fResult00 = new Vector2f();
	private static final Vector2f _vec2fResult01 = new Vector2f();

	private static final PointSegmentResult pointSegmentResult = new PointSegmentResult();

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public static boolean checkCollides(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (intersectsPolygons(bodyA.getTransformedVertices(), bodyB.getTransformedVertices(), manifold)) {
					manifold.bodyA = bodyA;
					manifold.bodyB = bodyB;
					return true;
				}
			} else if (lShapeTypeB == ShapeType.Circle) {
				if (intersectsCirclePolygon(bodyB.x, bodyB.y, bodyB.radius, bodyA.getTransformedVertices(), bodyA.x, bodyA.y, manifold)) {
					manifold.bodyA = bodyA;
					manifold.bodyB = bodyB;
					return true;
				}
			}

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon) {
				if (intersectsCirclePolygon(bodyA.x, bodyA.y, bodyA.radius, bodyB.getTransformedVertices(), bodyB.x, bodyB.y, manifold)) {
					manifold.bodyA = bodyA;
					manifold.bodyB = bodyB;

					manifold.normal.x = -manifold.normal.x;
					manifold.normal.y = -manifold.normal.y;
					return true;
				}

			} else if (lShapeTypeB == ShapeType.Circle) {
				if (intersectsCircles(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.radius, manifold)) {
					manifold.bodyA = bodyA;
					manifold.bodyB = bodyB;
					return true;
				}
			}
		}

		return false;
	}

	public static boolean intersectsPolygons(List<Vector2f> verticesA, List<Vector2f> verticesB, ContactManifold result) {
		result.intersection = true;
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

			projectVertices(verticesA, axisX, axisY, _vec2fResult00);
			projectVertices(verticesB, axisX, axisY, _vec2fResult01);

			if (_vec2fResult00.x >= _vec2fResult01.y || _vec2fResult00.x >= _vec2fResult01.y) {
				result.intersection = false;
				return false; // early out
			}

			// overlap
			final float minimumDepthValueA = (_vec2fResult00.y - _vec2fResult01.x);

			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (_vec2fResult01.y - _vec2fResult00.x);
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

			projectVertices(verticesA, axisX, axisY, _vec2fResult00);
			projectVertices(verticesB, axisX, axisY, _vec2fResult01);

			if (_vec2fResult00.x >= _vec2fResult01.y || _vec2fResult00.x >= _vec2fResult01.y) {
				return false; // early out
			}

			// overlap
			final float minimumDepthValueA = (_vec2fResult00.y - _vec2fResult01.x);

			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (_vec2fResult01.y - _vec2fResult00.x);
			if (minimumDepthValueB < result.depth) {
				result.depth = minimumDepthValueB;
				result.normal.x = -axisX;
				result.normal.y = -axisY;
			}
		}

		return true;
	}

	public static boolean intersectsCirclePolygon(float circleX, float circleY, float circleRadius, List<Vector2f> polygonVertices, float polygonCenterX, float polygonCenterY, ContactManifold result) {
		result.intersection = true;
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

			projectVertices(polygonVertices, axisX, axisY, _vec2fResult00);
			projectCircle(circleX, circleY, circleRadius, axisX, axisY, _vec2fResult01);

			if (_vec2fResult00.x >= _vec2fResult01.y || _vec2fResult00.x >= _vec2fResult01.y) {
				result.intersection = false;
				return false; // early out
			}

			// overlap
			final float minimumDepthValueA = (_vec2fResult00.y - _vec2fResult01.x);

			if (minimumDepthValueA < result.depth) {
				result.depth = minimumDepthValueA;
				result.normal.x = axisX;
				result.normal.y = axisY;
			}

			final float minimumDepthValueB = (_vec2fResult01.y - _vec2fResult00.x);
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

		projectVertices(polygonVertices, axisX, axisY, _vec2fResult00);
		projectCircle(circleX, circleY, circleRadius, axisX, axisY, _vec2fResult01);

		if (_vec2fResult00.x >= _vec2fResult01.y || _vec2fResult00.x >= _vec2fResult01.y) {
			result.intersection = false;
			return false; // early out
		}

		// overlap
		final float minimumDepthValueA = (_vec2fResult00.y - _vec2fResult01.x);

		if (minimumDepthValueA < result.depth) {
			result.depth = minimumDepthValueA;
			result.normal.x = axisX;
			result.normal.y = axisY;
		}

		final float minimumDepthValueB = (_vec2fResult01.y - _vec2fResult00.x);
		if (minimumDepthValueB < result.depth) {
			result.depth = minimumDepthValueB;
			result.normal.x = -axisX;
			result.normal.y = -axisY;
		}

		return true;
	}

	public static boolean intersectsCircles(float circleAX, float circleAY, float radiusA, float circleBX, float circleBY, float radiusB, ContactManifold result) {
		final float dist = Vector2f.distance(circleAX, circleAY, circleBX, circleBY);
		final float radii = radiusA + radiusB;

		result.intersection = false;

		if (dist >= radii)
			return false;

		result.normal.x = circleBX - circleAX;
		result.normal.y = circleBY - circleAY;
		result.normal.nor();
		result.depth = radii - dist;

		result.intersection = true;

		return true;
	}

	private static void projectVertices(List<Vector2f> vertices, float axisX, float axisY, Vector2f toFill) {
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

		toFill.set(min, max);
	}

	private static void projectCircle(float centerX, float centerY, float radius, float axisX, float axisY, Vector2f toFill) {
		final float axisLength = (float) Math.sqrt(axisX * axisX + axisY * axisY);

		final var directionX = axisX / axisLength;
		final var directionY = axisY / axisLength;

		final var directionAndDirX = directionX * radius;
		final var directionAndDirY = directionY * radius;

		final var point1X = centerX + directionAndDirX;
		final var point1Y = centerY + directionAndDirY;

		final var point2X = centerX - directionAndDirX;
		final var point2Y = centerY - directionAndDirY;

		toFill.x = Vector2f.dot(point1X, point1Y, axisX, axisY);
		toFill.y = Vector2f.dot(point2X, point2Y, axisX, axisY);

		if (toFill.x > toFill.y) {
			final float t = toFill.x;
			toFill.x = toFill.y;
			toFill.y = t;
		}

	}

	public static void fillContactPoints(ContactManifold manifold) {
		final var bodyA = manifold.bodyA;
		final var bodyB = manifold.bodyB;

		final var lShapeTypeA = bodyA.shapeType();
		final var lShapeTypeB = bodyB.shapeType();

		manifold.contactCount = 0;

		if (lShapeTypeA == ShapeType.Polygon) {
			if (lShapeTypeB == ShapeType.Polygon) {

			} else if (lShapeTypeB == ShapeType.Circle) {
				findContactPoint(bodyB.x, bodyB.y, bodyB.radius, bodyA.x, bodyA.y, bodyA.getTransformedVertices(), manifold);
				manifold.contactCount = 1;

			}

		} else if (lShapeTypeA == ShapeType.Circle) {
			if (lShapeTypeB == ShapeType.Polygon) {
				findContactPoint(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, bodyB.getTransformedVertices(), manifold);
				manifold.contactCount = 1;

			} else if (lShapeTypeB == ShapeType.Circle) {
				findContactPoint(bodyA.x, bodyA.y, bodyA.radius, bodyB.x, bodyB.y, manifold);
				manifold.contactCount = 1;

			}
		}
	}

	private static void findContactPoint(float circleAX, float circleAY, float radiusA, float polyCenterX, float polyCenterY, List<Vector2f> verts, ContactManifold contactManifold) {
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
			}
		}
	}

	private static void findContactPoint(float circleAX, float circleAY, float radiusA, float circleBX, float circleBY, ContactManifold contactManifold) {
		var abX = circleBX - circleAX;
		var abY = circleBY - circleAY;

		final var abLength = (float) Math.sqrt(abX * abX + abY * abY);

		abX /= abLength;
		abY /= abLength;

		contactManifold.contact1.x = circleAX + abX * radiusA;
		contactManifold.contact1.y = circleAY + abY * radiusA;

	}

	private static PointSegmentResult pointSegmentDistance2(float px, float py, float ax, float ay, float bx, float by) {
		final var abX = bx - ax;
		final var abY = by - ay;

		final var apX = px - ax;
		final var apY = py - ay;

		final var proj = Vector2f.dot(abX, abY, apX, apY);
		final var projLength = Vector2f.distance2(abX, abY);
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

		pointSegmentResult.dist2 = Vector2f.distance2(px, py, pointSegmentResult.contactX, pointSegmentResult.contactY);
		return pointSegmentResult;
	}

}