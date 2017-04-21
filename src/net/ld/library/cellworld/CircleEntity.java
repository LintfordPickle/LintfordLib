package net.ld.library.cellworld;

public class CircleEntity extends CellEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CircleEntity() {
		super();
		
		radius = 32.0f;

	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------
	
	public boolean intersects(CellEntity pOtherEntity) {
		if (pOtherEntity instanceof CircleEntity) {
			CircleEntity lCircle = (CircleEntity) pOtherEntity;

			final float MAX_DIST = radius + lCircle.radius;
			final float FX = lCircle.xx - xx;
			final float FY = lCircle.yy - yy;

			final float DIST = (FX * FX) + (FY * FY);
			if (DIST <= MAX_DIST * MAX_DIST)
				return true;

		}

		else if (pOtherEntity instanceof RectangleEntity) {
			RectangleEntity lRectangle = (RectangleEntity) pOtherEntity;
			return lRectangle.intersects(pOtherEntity);
		}

		return false;
	}

	/** Returns true if the point is within the {@link CircleEntity} bounds. */
	public boolean intersects(float pWorldX, float pWorldY) {
		float fx = (pWorldX - xx) * (pWorldX - yy);
		float fy = (pWorldY - yy) * (pWorldY - yy);

		return fx + fy <= radius * radius;
	}

}
