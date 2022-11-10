package net.lintford.library.core.collisions;

import net.lintford.library.core.collisions.shapes.CircleShape;
import net.lintford.library.core.collisions.shapes.Shape;
import net.lintford.library.core.maths.Vector2f;

public class SAT {

	private static Vector2f mTempVector2f0 = new Vector2f();
	private static Vector2f mTempVector2f1 = new Vector2f();

	private static Vector2f[] mTempAxes0 = new Vector2f[] { new Vector2f(0, 0), new Vector2f(0, 0) };
	private static Vector2f[] mTempAxes1 = new Vector2f[] { new Vector2f(0, 0), new Vector2f(0, 0) };

	public static boolean intersects(Shape shape0, Shape shape1) {
		if (shape0 == null || shape1 == null)
			return false;

		var lAxes0 = shape0.getAxes();
		var lAxes1 = shape1.getAxes();

		if (lAxes0 == null && shape0 instanceof CircleShape) {
			lAxes0 = mTempAxes0;
			mTempVector2f0 = ((CircleShape) shape0).getNearestVertex(shape1, mTempVector2f0);
			lAxes0[0].x = mTempVector2f0.x;
			lAxes0[0].y = mTempVector2f0.y;
		}

		if (lAxes1 == null && shape1 instanceof CircleShape) {
			lAxes1 = mTempAxes1;
			mTempVector2f0 = ((CircleShape) shape1).getNearestVertex(shape0, mTempVector2f0);
			lAxes1[0].x = mTempVector2f0.x;
			lAxes1[0].y = mTempVector2f0.y;
		}

		// loop over the axes0
		if (lAxes0 != null) {
			for (int i = 0; i < lAxes0.length; i++) {
				Vector2f axis = lAxes0[i];

				// project both shapes onto the axis
				mTempVector2f0 = shape0.project(axis, mTempVector2f0);
				mTempVector2f1 = shape1.project(axis, mTempVector2f1);

				// do the projections overlap?
				if (!overlaps(mTempVector2f0, mTempVector2f1)) {
					// then we can guarantee that the shapes do not overlap
					return false;
				}
			}
		}

		// loop over the axes1
		if (lAxes1 != null) {
			for (int i = 0; i < lAxes1.length; i++) {
				Vector2f axis = lAxes1[i];

				// project both shapes onto the axis
				mTempVector2f0 = shape0.project(axis, mTempVector2f0);
				mTempVector2f1 = shape1.project(axis, mTempVector2f1);

				// do the projections overlap?
				if (!overlaps(mTempVector2f0, mTempVector2f1)) {
					// then we can guarantee that the shapes do not overlap
					return false;
				}
			}
		}

		// If we can get here then we know that every axis had overlap on it
		// so we can guarantee an intersection.
		return true;
	}

	public static boolean overlaps(Vector2f point0, Vector2f point1) {
		return !(point0.x > point1.y || point1.x > point0.y);
	}
}
