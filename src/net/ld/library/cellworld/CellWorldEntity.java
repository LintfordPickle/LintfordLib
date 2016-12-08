package net.ld.library.cellworld;

import net.ld.library.core.maths.Rectangle;
import net.ld.library.core.rendering.RenderState;
import net.ld.library.core.time.GameTime;

/**
 * A simple entity class for a cell grid based world.
 * 
 * @ref http://deepnight.net/a-simple-platformer-engine-part-1-basics/
 */
public class CellWorldEntity extends Rectangle {

	// =============================================
	// Constants
	// =============================================

	// =============================================
	// Variables
	// =============================================

	private CellGridWorld mParent;

	// base grid coords
	public int cx;
	public int cy;
	public float rx;
	public float ry;

	// Resulting coords
	public float xx;
	public float yy;

	// Movement
	public float dx;
	public float dy;

	// =============================================
	// Properties
	// =============================================

	public boolean isInUse() {
		return mParent != null;
	}

	public boolean isOnGround() {
		if (mParent != null) {
			return (mParent.hasCollisionAt(cx, cy + 1) && ry > 0.5f);
		}

		return false;
	}

	// =============================================
	// Constructor
	// =============================================

	public CellWorldEntity() {

	}

	// =============================================
	// Core-Methods
	// =============================================

	public void update(GameTime pGameTime) {
		if(!isInUse()) return;
		
		rx += dx * pGameTime.elapseGameTime() / 1000.0f;
		ry += dy * pGameTime.elapseGameTime() / 1000.0f;

		dx *= 0.96f;
		dy *= 0.96f;

		// COLLISION
		// Check collisions to the right
		if (mParent.hasCollisionAt(cx + 1, cy) && rx > 0.7f) {
			rx = 0.7f; // limit ratio
			dx = 0; // kill vel
		}

		// Check collision to the left
		if (mParent.hasCollisionAt(cx - 1, cy) && rx <= 0.3f) {
			rx = 0.3f; // limit ratio
			dx = 0; // kill vel
		}

		if (mParent.hasCollisionAt(cx, cy + 1) && ry > 0.7f) {
			ry = 0.7f; // limit ratio
			dy = 0; // kill vel
		}

		// Check collision to the left
		if (mParent.hasCollisionAt(cx, cy - 1) && ry <= 0.3f) {
			ry = 0.3f; // limit ratio
			dy = 0; // kill vel
		}


		while (rx < 0) {
			rx++;
			cx--;
		}

		while (rx > 1) {
			rx--;
			cx++;
		}

		while (ry < 0) {
			ry++;
			cy--;
		}
		
		while (ry > 1) {
			ry--;
			cy++;
		}


		xx = (cx + rx) * mParent.cellSize;
		yy = (cy + ry) * mParent.cellSize;

		// update the underlying world coords
		x = xx;
		y = yy;

	}

	public void draw(RenderState pRenderState) {
		
	}

	// =============================================
	// Methods
	// =============================================

	public void setCoordinate(float pX, float pY) {
		xx = x;
		yy = y;
		cx = (int) (xx / 16f);
		cy = (int) (yy / 16f);
		rx = (xx - cx * 16) / 16;
		ry = (yy - cy * 16) / 16;

	}

	public void attachParent(CellGridWorld pParent) {
		mParent = pParent;

	}

	public void detachParent() {
		mParent = null;

	}

}
