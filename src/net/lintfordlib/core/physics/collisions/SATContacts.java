package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.ShapeType;
import net.lintfordlib.core.physics.shapes.BaseShape;

public class SATContacts {

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

	private static final PointSegmentResult pointSegmentResult = new PointSegmentResult();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public static final void fillContactPoints(ContactManifold manifold) {
		final var bodyA = manifold.shapeA;
		final var bodyB = manifold.shapeB;

		final var lShapeTypeA = bodyA.shapeType();

		manifold.contactCount = 0;

		if (lShapeTypeA == ShapeType.Polygon)
			findContactsOnPolygonShape(bodyA, bodyB, manifold);
		else if (lShapeTypeA == ShapeType.LineWidth)
			findContactsOnLineShape(bodyA, bodyB, manifold);
		else if (lShapeTypeA == ShapeType.Circle)
			findContactsOnCircleShape(bodyA, bodyB, manifold);

	}

	private static void findContactsOnPolygonShape(BaseShape polygonBody, BaseShape otherBody, ContactManifold manifold) {
		final var lShapeTypeB = otherBody.shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findPolygonPolygonContactPoints(polygonBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLinePolygonContactPoints(polygonBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findCirclePolygonContactPoint(polygonBody, otherBody, manifold);
	}

	private static void findContactsOnLineShape(BaseShape lineBody, BaseShape otherBody, ContactManifold manifold) {
		final var lShapeTypeB = otherBody.shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findLinePolygonContactPoints(lineBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLineLineContactPoints(lineBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findLineCircleContactPoint(lineBody, otherBody, manifold);

	}

	private static void findContactsOnCircleShape(BaseShape circleBody, BaseShape otherBody, ContactManifold manifold) {
		final var lShapeTypeB = otherBody.shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findCirclePolygonContactPoint(circleBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLineCircleContactPoint(circleBody, otherBody, manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findCircleCircleContactPoint(circleBody, otherBody, manifold);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private static void findPolygonPolygonContactPoints(BaseShape polygonABody, BaseShape polygonBBody, ContactManifold contactManifold) {
		final var polyAVerts = polygonABody.getTransformedVertices();
		final var polyBVerts = polygonBBody.getTransformedVertices();

		float minDist2 = Float.MAX_VALUE;

		final int lNumVertsA = polyAVerts.size();
		final int lNumVertsB = polyBVerts.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var p = polyAVerts.get(i);

			for (int j = 0; j < lNumVertsB; j++) {
				final var va = polyBVerts.get(j);
				final var vb = polyBVerts.get((j + 1) % polyBVerts.size());

				final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

				// a second point with ~same distance
				if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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
				if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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

	private static void findLinePolygonContactPoints(BaseShape lineBody, BaseShape bodyPolygonBody, ContactManifold contactManifold) {
		final var lineVerts = lineBody.getTransformedVertices();
		final var polyVerts = bodyPolygonBody.getTransformedVertices();

		float minDist2 = Float.MAX_VALUE;

		final int lNumVertsA = polyVerts.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var p = polyVerts.get(i);

			final var va = lineVerts.get(0);
			final var vb = lineVerts.get(1);

			final var lPointSegmentDist = pointSegmentDistance2(p.x, p.y, va.x, va.y, vb.x, vb.y);

			// a second point with ~same distance
			if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
				if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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
				if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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

	private static void findCirclePolygonContactPoint(BaseShape circleBody, BaseShape polygonBody, ContactManifold contactManifold) {
		final var lCircleAX = circleBody.parentBody().transform.p.x;
		final var lCircleAY = circleBody.parentBody().transform.p.y;

		final var lPolygonVertices = polygonBody.getTransformedVertices();

		float minDist2 = Float.MAX_VALUE;

		final int lNumVerts = lPolygonVertices.size();
		for (int i = 0; i < lNumVerts; i++) {
			final var va = lPolygonVertices.get(i);
			final var vb = lPolygonVertices.get((i + 1) % lPolygonVertices.size());

			final var lPointSegmentDist = pointSegmentDistance2(lCircleAX, lCircleAY, va.x, va.y, vb.x, vb.y);
			if (lPointSegmentDist.dist2 < minDist2) {
				minDist2 = lPointSegmentDist.dist2;

				contactManifold.contact1.x = lPointSegmentDist.contactX;
				contactManifold.contact1.y = lPointSegmentDist.contactY;
				contactManifold.contactCount = 1;
			}
		}
	}

	private static void findCircleCircleContactPoint(BaseShape circleABody, BaseShape circleBBody, ContactManifold contactManifold) {
		float lCircleAX = circleABody.parentBody().transform.p.x;
		float lCircleAY = circleABody.parentBody().transform.p.y;
		float lRadiusA = circleABody.radius();

		float lCircleBX = circleBBody.parentBody().transform.p.x;
		float lCircleBY = circleBBody.parentBody().transform.p.y;

		var abX = lCircleBX - lCircleAX;
		var abY = lCircleBY - lCircleAY;

		final var abLength = (float) Math.sqrt(abX * abX + abY * abY);

		abX /= abLength;
		abY /= abLength;

		contactManifold.contact1.x = lCircleAX + abX * lRadiusA;
		contactManifold.contact1.y = lCircleAY + abY * lRadiusA;
		contactManifold.contactCount = 1;

	}

	private static void findLineCircleContactPoint(BaseShape lineBody, BaseShape circleBody, ContactManifold manifold) {
		final var lLineVertices = lineBody.getTransformedVertices();
		final var lLineRadius = lineBody.height() * .5f;

		final var lCircleX = circleBody.parentBody().transform.p.x;
		final var lCircleY = circleBody.parentBody().transform.p.y;

		final var sx = lLineVertices.get(0).x;
		final var sy = lLineVertices.get(0).y;

		final var ex = lLineVertices.get(1).x;
		final var ey = lLineVertices.get(1).y;

		final var lLineX1 = ex - sx;
		final var lLineY1 = ey - sy;

		final var lLineX2 = lCircleX - sx;
		final var lLineY2 = lCircleY - sy;

		final var lEdgeLength = lLineX1 * lLineX1 + lLineY1 * lLineY1;
		final var v = lLineX1 * lLineX2 + lLineY1 * lLineY2;
		final var t = MathHelper.clamp(v, 0.f, lEdgeLength) / lEdgeLength;

		// project out towards circle by line radius
		final float lClosestPointX = sx + t * lLineX1;
		final float lClosestPointY = sy + t * lLineY1;

		manifold.contact1.x = lClosestPointX - (lClosestPointX - lCircleX) * lLineRadius;
		manifold.contact1.y = lClosestPointY - (lClosestPointY - lCircleY) * lLineRadius;
		manifold.contactCount = 1;
	}

	private static void findLineLineContactPoints(BaseShape lineABody, BaseShape lineBBody, ContactManifold contactManifold) {
		final var lineAVertices = lineABody.getTransformedVertices();
		final var lineBVertices = lineBBody.getTransformedVertices();

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
				if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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
				if (MathHelper.equalWithinEpsilon(lPointSegmentDist.dist2, minDist2)) {
					if (!MathHelper.equalWithinEpsilon(lPointSegmentDist.contactX, lPointSegmentDist.contactY, contactManifold.contact1.x, contactManifold.contact1.y)) {
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

}