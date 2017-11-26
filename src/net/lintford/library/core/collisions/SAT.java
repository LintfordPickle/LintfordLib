package net.lintford.library.core.collisions;

import net.lintford.library.core.graphics.sprites.Sprite;
import net.lintford.library.core.maths.Vector2f;

// TODO: 
public class SAT {

	// TODO: Eventually either pool this class or pass the result out of a method.
	public class PolygonCollisionResult {
		// Arehte polygons going to intersect forward in time?
		public boolean willIntersect;

		// Are the polygons currently intersecting?
		public boolean intersects;

		// The translation to apply to the first polygon to push the polygons apart.
		public Vector2f minimumTranslationVector;

	}

	/**
	 * Calculate the projection of a polygon on an axis and return it as a [min, max] interval.
	 */
	static public void projectSprite(Vector2f pAxis, Sprite pSprite, float pMin, float pMax) {

	}

}
