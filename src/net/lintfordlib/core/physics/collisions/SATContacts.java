package net.lintfordlib.core.physics.collisions;

import java.util.List;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.core.physics.dynamics.ShapeType;

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

	public static void fillContactPoints(ContactManifold manifold) {
		final var bodyA = manifold.bodyA;
		final var bodyB = manifold.bodyB;

		final var lShapeTypeA = bodyA.shape().shapeType();

		manifold.contactCount = 0;

		if (lShapeTypeA == ShapeType.Polygon) {
			findContactsOnPolygonShape(bodyA, bodyB, manifold);
		} else if (lShapeTypeA == ShapeType.LineWidth) {
			findContactsOnLineShape(bodyA, bodyB, manifold);
		} else if (lShapeTypeA == ShapeType.Circle) {
			findContactsOnCircleShape(bodyA, bodyB, manifold);
		}
	}

	private static void findContactsOnPolygonShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeTypeB = bodyB.shape().shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findPolygonPolygonContactPoints(bodyA.getWorldVertices(), bodyB.getWorldVertices(), manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLinePolygonContactPoints(bodyB.getWorldVertices(), bodyA.getWorldVertices(), manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findCirclePolygonContactPoint(bodyB.transform.p.x, bodyB.transform.p.y, bodyB.shape().radius(), bodyA.transform.p.x, bodyA.transform.p.y, bodyA.getWorldVertices(), manifold);
	}

	private static void findContactsOnLineShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeTypeB = bodyB.shape().shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findLinePolygonContactPoints(bodyB.getWorldVertices(), bodyA.getWorldVertices(), manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLineLineContactPoints(bodyA.getWorldVertices(), bodyA.shape().height(), bodyB.getWorldVertices(), bodyB.shape().height(), manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findLineCircleContactPoint(bodyA.getWorldVertices(), bodyA.shape().height(), bodyB.transform.p.x, bodyB.transform.p.y, bodyB.shape().radius(), manifold);

	}

	private static void findContactsOnCircleShape(RigidBody bodyA, RigidBody bodyB, ContactManifold manifold) {
		final var lShapeTypeB = bodyB.shape().shapeType();

		if (lShapeTypeB == ShapeType.Polygon)
			findCirclePolygonContactPoint(bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), bodyB.transform.p.x, bodyB.transform.p.y, bodyB.getWorldVertices(), manifold);
		else if (lShapeTypeB == ShapeType.LineWidth)
			findLineCircleContactPoint(bodyB.getWorldVertices(), bodyB.shape().height(), bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), manifold);
		else if (lShapeTypeB == ShapeType.Circle)
			findCircleCircleContactPoint(bodyA.transform.p.x, bodyA.transform.p.y, bodyA.shape().radius(), bodyB.transform.p.x, bodyB.transform.p.y, manifold);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private static void findPolygonPolygonContactPoints(List<Vector2f> polyAVerts, List<Vector2f> polyBVerts, ContactManifold contactManifold) {

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

	private static void findLinePolygonContactPoints(List<Vector2f> lineVerts, List<Vector2f> polyVerts, ContactManifold contactManifold) {
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