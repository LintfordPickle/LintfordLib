package net.lintford.library.core.collisions;

import java.util.List;

import net.lintford.library.core.maths.Vector2f;

public class SAT {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final ContactManifold tempResult = new ContactManifold();

	private static final Vector2f _vec2fResult00 = new Vector2f();
	private static final Vector2f _vec2fResult01 = new Vector2f();

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

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

	public static boolean intersectsCirclePolygon(float circleX, float circleY, float circleRadius, List<Vector2f> polygonVertices, ContactManifold result) {
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

		int lCpIndex = findClosestVertexIndexOnPolygon(circleX, circleY, polygonVertices);
		final var lClosestVertex = polygonVertices.get(lCpIndex);

		float axisX = lClosestVertex.x - circleX;
		float axisY = lClosestVertex.y - circleY;

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

	private static int findClosestVertexIndexOnPolygon(float centerX, float centerY, List<Vector2f> vertices) {
		int result = -1;
		float dist = Float.MAX_VALUE;

		final int lNumVertices = vertices.size();
		for (int i = 0; i < lNumVertices; i++) {
			final float lNewDist = Vector2f.distance2(centerX, centerY, vertices.get(i).x, vertices.get(i).y);
			if (lNewDist < dist) {
				dist = lNewDist;
				result = i;
			}
		}

		return result;
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
}