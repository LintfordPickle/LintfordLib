package net.lintfordlib.core.physics.collisions;

import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.core.maths.Projection;
import net.lintfordlib.core.maths.Projection.SatCollisionProjectionResult;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;

/***
 * A collection of routines for checking for intersections between {@link RigidBody}s consisting of various {@link BaseShape} types. Most of the intersection tests use the sepaarating axis theroem when appropriate.
 * 
 * @author John Hampson 2023
 *
 */
public class IntersectionTests {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	private static final SatCollisionProjectionResult projectionResult1 = new SatCollisionProjectionResult();
	private static final SatCollisionProjectionResult projectionResult2 = new SatCollisionProjectionResult();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public static final boolean checkCollides(final ContactManifold manifold) {
		final var lShapeA = manifold.bodyA.shape();
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

	private static final boolean intersectionsPolygonShape(ContactManifold contactManifold) {
		final var lShapeB = contactManifold.bodyB.shape();
		final var lShapeBType = lShapeB.shapeType();

		final var lPolygonBody_A = contactManifold.bodyA;
		final var lOtherBody_B = contactManifold.bodyB;

		switch (lShapeBType) {
		case Polygon:
			return intersectsPolygons(lPolygonBody_A, lOtherBody_B, contactManifold);

		case LineWidth:
			return intersectsLinePolygon(lOtherBody_B, lPolygonBody_A, contactManifold);

		case Circle:
			return intersectsCirclePolygon(lOtherBody_B, lPolygonBody_A, contactManifold);
		}

		return false;
	}

	private static final boolean intersectionsLineWidthShape(ContactManifold contactManifold) {
		final var lShapeB = contactManifold.bodyB.shape();
		final var lShapeBType = lShapeB.shapeType();

		final var lLineWidthBody_A = contactManifold.bodyA;
		final var lOtherBody_B = contactManifold.bodyB;

		switch (lShapeBType) {
		case Polygon:
			return intersectsLinePolygon(lLineWidthBody_A, lOtherBody_B, contactManifold);

		case LineWidth:
			return intersectsLineLine(lLineWidthBody_A, lOtherBody_B, contactManifold);

		case Circle:
			return intersectsLineCircle(lLineWidthBody_A, lOtherBody_B, contactManifold);
		}

		return false;
	}

	private static final boolean intersectionsCircleShape(ContactManifold contactManifold) {
		final var lCircleBody_A = contactManifold.bodyA;
		final var lOtherBody_B = contactManifold.bodyB;

		final var lShapeB = lOtherBody_B.shape();
		final var lShapeBType = lShapeB.shapeType();

		switch (lShapeBType) {
		case Polygon:
			return intersectsCirclePolygon(lCircleBody_A, lOtherBody_B, contactManifold);

		case LineWidth:
			return intersectsLineCircle(lOtherBody_B, lCircleBody_A, contactManifold);

		case Circle:
			return intersectsCircles(lCircleBody_A, lOtherBody_B, contactManifold);

		default:
			return false;
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	// Polygons

	private static final boolean intersectsPolygons(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lPolygonABody = bodyA;
		final var lPolygonBBody = bodyB;

		result.bodyA = lPolygonABody;
		result.bodyB = lPolygonBBody;

		final var lPolygonAVertices = lPolygonABody.getWorldVertices();
		final var lPolygonBVertices = lPolygonBBody.getWorldVertices();

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

			Projection.projectVertices(lPolygonAVertices, axisX, axisY, projectionResult1);
			Projection.projectVertices(lPolygonBVertices, axisX, axisY, projectionResult2);

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

			Projection.projectVertices(lPolygonAVertices, axisX, axisY, projectionResult1);
			Projection.projectVertices(lPolygonBVertices, axisX, axisY, projectionResult2);

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

	private static final boolean intersectsCirclePolygon(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lCircleBody = bodyA;
		final var lPolygonBody = bodyB;

		result.bodyA = lCircleBody;
		result.bodyB = lPolygonBody;

		result.isIntersecting = true;
		result.normal.x = 0.f;
		result.normal.y = 0.f;
		result.depth = Float.MAX_VALUE;

		final var lCircleX = lCircleBody.transform.p.x;
		final var lCircleY = lCircleBody.transform.p.y;
		final var lCircleRadius = lCircleBody.shape().radius();

		final var lPolygonCenterX = lPolygonBody.transform.p.x;
		final var lPolygonCenterY = lPolygonBody.transform.p.y;
		final var lPolygonVertices = lPolygonBody.getWorldVertices();

		final var lNumVertsA = lPolygonVertices.size();
		for (int i = 0; i < lNumVertsA; i++) {
			final var va = lPolygonVertices.get(i);
			final var vb = lPolygonVertices.get((i + 1) % lNumVertsA);

			final var edgeX = vb.x - va.x;
			final var edgeY = vb.y - va.y;

			final var edgeLength = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);

			final var axisX = edgeY / edgeLength;
			final var axisY = -edgeX / edgeLength;

			Projection.projectCircle(lCircleX, lCircleY, lCircleRadius, axisX, axisY, projectionResult1);
			Projection.projectVertices(lPolygonVertices, axisX, axisY, projectionResult2);

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

		Projection.projectCircle(lCircleX, lCircleY, lCircleRadius, axisX, axisY, projectionResult1);
		Projection.projectVertices(lPolygonVertices, axisX, axisY, projectionResult2);

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

	private static final boolean intersectsCircles(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lCircleABody = bodyA;
		final var lCircleBBody = bodyB;

		result.bodyA = lCircleABody;
		result.bodyB = lCircleBBody;

		final var lCircleAPositionX = lCircleABody.transform.p.x;
		final var lCircleAPositionY = lCircleABody.transform.p.y;
		final var lCircleARadius = lCircleABody.shape().radius();

		final var lCircleBPositionX = lCircleBBody.transform.p.x;
		final var lCircleBPositionY = lCircleBBody.transform.p.y;
		final var lCircleBRadius = lCircleBBody.shape().radius();

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

	private static final boolean intersectsLinePolygon(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lLineBody = bodyA;
		final var lPolygonBody = bodyB;

		result.bodyA = lLineBody;
		result.bodyB = lPolygonBody;

		final var polygonVertices = lPolygonBody.getWorldVertices();
		final var lineVertices = lLineBody.getWorldVertices();
		final var lineRadius = lLineBody.shape().height() * .5f;

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

			Projection.projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult1);
			Projection.projectVertices(polygonVertices, axisX, axisY, projectionResult2);

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

			Projection.projectVertices(polygonVertices, axisX, axisY, projectionResult2);
			Projection.projectLineWidth(lsx, lsy, lex, ley, lineRadius, axisX, axisY, projectionResult1);

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

	private static final boolean intersectsLineCircle(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lLineBody = bodyA;
		final var lCircleBody = bodyB;

		result.bodyA = lLineBody;
		result.bodyB = lCircleBody;

		final var lLineVertices = lLineBody.getWorldVertices();
		final var lLineRadius = lLineBody.shape().height() * .5f;

		final var lCirclePositionX = lCircleBody.transform.p.x;
		final var lCirclePositionY = lCircleBody.transform.p.y;
		final var lCircleRadius = lCircleBody.shape().radius();

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

	private static final boolean intersectsLineLine(final RigidBody bodyA, final RigidBody bodyB, final ContactManifold result) {
		final var lLineABody = result.bodyA;
		final var lLineBBody = result.bodyB;

		result.bodyA = lLineABody;
		result.bodyB = lLineBBody;

		final var lLineAVertices = lLineABody.getWorldVertices();
		final var lLineARadius = lLineABody.shape().height() * .5f;

		final var lLineBVertices = lLineBBody.getWorldVertices();
		final var lLineBRadius = lLineBBody.shape().height() * .5f;

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

			Projection.projectLineWidth(ax, ay, bx, by, lLineARadius, axis01X, axis01Y, projectionResult1);
			Projection.projectLineWidth(px, py, qx, qy, lLineBRadius, axis01X, axis01Y, projectionResult2);

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

			Projection.projectLineWidth(ax, ay, bx, by, lLineBRadius, axis02X, axis02Y, projectionResult1);
			Projection.projectLineWidth(px, py, qx, qy, lLineARadius, axis02X, axis02Y, projectionResult2);

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

}