package net.lintford.library.core.collisions;

import net.lintford.library.core.geometry.Circle;
import net.lintford.library.core.geometry.Shape;
import net.lintford.library.core.maths.Vector2f;

public class SAT {

	public static boolean intersects(Shape pShape0, Shape pShape1) {
		Vector2f[] axes0 = pShape0.getAxes();
		Vector2f[] axes1 = pShape1.getAxes();

		if(axes0 == null && pShape0 instanceof Circle) {
			axes0 = new Vector2f[1];
			axes0[0] = ((Circle)pShape0).getNearestVertex(pShape1, new Vector2f());
		}
		
		if(axes1 == null && pShape1 instanceof Circle) {
			axes1 = new Vector2f[1];
			axes1[0] = ((Circle)pShape1).getNearestVertex(pShape0, new Vector2f());
		}
		
		// loop over the axes0
		if(axes0 != null) {
			for (int i = 0; i < axes0.length; i++) {
				Vector2f axis = axes0[i];
				
				// project both shapes onto the axis
				Vector2f p1 = pShape0.project(axis, new Vector2f());
				Vector2f p2 = pShape1.project(axis, new Vector2f());
				
				// do the projections overlap?
				if (!overlaps(p1, p2)) {
					// then we can guarantee that the shapes do not overlap
					return false;
				}
			}
			
		}

		// loop over the axes1
		if(axes1 != null) {
			for (int i = 0; i < axes1.length; i++) {
				Vector2f axis = axes1[i];
				
				// project both shapes onto the axis
				Vector2f p1 = pShape0.project(axis, new Vector2f());
				Vector2f p2 = pShape1.project(axis, new Vector2f());
				
				// do the projections overlap?
				if (!overlaps(p1, p2)) {
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
