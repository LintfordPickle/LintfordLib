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

	// base grid coordinates
	public int cx;
	public int cy;
	public float rx;
	public float ry;

	public float radius;

	// Resulting coordinates
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
		radius = 16;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void update(GameTime pGameTime) {
		if (!isInUse())
			return;

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

		// Check collisions with other entities
		int lEntCount = mParent.entities().size();
		for (int i = 0; i < lEntCount; i++) {
			CellWorldEntity e = mParent.entities().get(i);
			if (e == this || !e.isInUse())
				continue;

			// Fast distance check
			if (e != this && Math.abs(cx - e.cx) <= 2 && Math.abs(cy - e.cy) <= 2) {
				// Real distance check
				float dist = (float) Math.sqrt((e.xx - xx) * (e.xx - xx) + (e.yy - yy) * (e.yy - yy));
				if (dist <= radius + e.radius) {
					float ang = (float) Math.atan2(e.yy - yy, e.xx - xx);
					float force = 0.2f;
					float repelPower = (radius + e.radius - dist) / (radius + e.radius);
					dx -= Math.cos(ang) * repelPower * force;
					dy -= Math.sin(ang) * repelPower * force;
					e.dx += Math.cos(ang) * repelPower * force;
					e.dy += Math.sin(ang) * repelPower * force;
				}
			}

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

		// update the underlying world coordinates
		x = xx;
		y = yy;

		// kill the velocity if small enough
		if (Math.abs(dx) < 0.01f)
			dx = 0f;
		if (Math.abs(dy) < 0.01f)
			dy = 0f;

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
