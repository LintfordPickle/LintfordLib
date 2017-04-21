package net.ld.library.cellworld;

import net.ld.library.core.maths.MathHelper;

/** A {@link RectangleEntity} defines an {@link CellEntity} which has a width and height. */
public class RectangleEntity extends CellEntity {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float width;
	public float height;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RectangleEntity() {
		width = 64;
		height = 64;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setDimensions(float pWidth, float pHeight) {
		width = pWidth;
		height = pHeight;
	}

	/** Checks collisions with another {@link CellEntity} object and returns true if a collision is detected, otherwise returns false. */
	public boolean intersects(CellEntity pOtherEntity) {
		if (pOtherEntity instanceof CircleEntity) {
			CircleEntity lCircle = (CircleEntity) pOtherEntity;

			// Find the closest point to the circle within the rectangle
			final float closestX = MathHelper.clamp(lCircle.xx, xx, xx + width);
			final float closestY = MathHelper.clamp(lCircle.yy, yy, yy + height);

			// Calculate the distance between the circle's center and this closest point
			final float distanceX = lCircle.xx - closestX;
			final float distanceY = lCircle.yy - closestY;

			// If the distance is less than the circle's radius, an intersection occurs
			final float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
			return distanceSquared < (lCircle.radius * lCircle.radius);

		}

		else if (pOtherEntity instanceof RectangleEntity) {
			RectangleEntity pOtherRect = (RectangleEntity) pOtherEntity;
			return ((((pOtherRect.xx < (xx + width)) && (xx < (pOtherRect.xx + pOtherRect.width))) && (pOtherRect.yy < (yy + height))) && (yy < (pOtherRect.yy + pOtherRect.height)));

		}

		return false;
	}

	/** Returns true if the point is within the {@link RectangleEntity} bounds. */
	public boolean intersects(float pWorldX, float pWorldY) {
		return pWorldX >= xx && pWorldX <= xx + width && pWorldY >= yy && pWorldY <= yy + height;
	}

}
