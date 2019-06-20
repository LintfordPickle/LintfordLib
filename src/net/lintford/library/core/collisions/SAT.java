package net.lintford.library.core.collisions;

import net.lintford.library.core.geometry.Circle;
import net.lintford.library.core.geometry.Shape;
import net.lintford.library.core.maths.Vector2f;

public class SAT {

	private static Vector2f mTempVector2f0 = new Vector2f();
	private static Vector2f mTempVector2f1 = new Vector2f();

	private static Vector2f[] mTempAxes0 = new Vector2f[] { new Vector2f(0, 0), new Vector2f(0, 0) };
	private static Vector2f[] mTempAxes1 = new Vector2f[] { new Vector2f(0, 0), new Vector2f(0, 0) };

	public static boolean intersects(Shape pShape0, Shape pShape1) {
		if (pShape0 == null || pShape1 == null)
			return false;

		Vector2f[] axes0 = pShape0.getAxes();
		Vector2f[] axes1 = pShape1.getAxes();

		if (axes0 == null && pShape0 instanceof Circle) {
			axes0 = mTempAxes0;
			mTempVector2f0 = ((Circle) pShape0).getNearestVertex(pShape1, mTempVector2f0);
			axes0[0].x = mTempVector2f0.x;
			axes0[0].y = mTempVector2f0.y;
		}

		if (axes1 == null && pShape1 instanceof Circle) {
			axes1 = mTempAxes1;
			mTempVector2f0 = ((Circle) pShape1).getNearestVertex(pShape0, mTempVector2f0);
			axes1[0].x = mTempVector2f0.x;
			axes1[0].y = mTempVector2f0.y;
		}

		// loop over the axes0
		if (axes0 != null) {
			for (int i = 0; i < axes0.length; i++) {
				Vector2f axis = axes0[i];

				// project both shapes onto the axis
				mTempVector2f0 = pShape0.project(axis, mTempVector2f0);
				mTempVector2f1 = pShape1.project(axis, mTempVector2f1);

				// do the projections overlap?
				if (!overlaps(mTempVector2f0, mTempVector2f1)) {
					// then we can guarantee that the shapes do not overlap
					return false;
				}
			}

		}

		// loop over the axes1
		if (axes1 != null) {
			for (int i = 0; i < axes1.length; i++) {
				Vector2f axis = axes1[i];

				// project both shapes onto the axis
				mTempVector2f0 = pShape0.project(axis, mTempVector2f0);
				mTempVector2f1 = pShape1.project(axis, mTempVector2f1);

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

	public static boolean overlaps(Vector2f p1, Vector2f p2) {
		return !(p1.x > p2.y || p2.x > p1.y);

	}

}
