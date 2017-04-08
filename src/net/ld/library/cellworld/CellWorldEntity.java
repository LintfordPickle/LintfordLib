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
	// Variables
	// =============================================

	protected CellGridWorld mParent;

	// base grid coordinates
	public int cx;
	public int cy;
	public float rx;
	public float ry;

	public boolean isAlive; // used for state of player in game world

	public float radius;
	public int coll_repel_precedence;

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
			return (mParent.hasLevelCollisionAt(cx, cy + 1) && ry > 0.5f);
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

		// COLLISION

		rx += dx * pGameTime.elapseGameTime() / 1000.0f;
		ry += dy * pGameTime.elapseGameTime() / 1000.0f;

		dx *= 0.96f;
		dy *= 0.96f;

		if (isAlive) {

			// Check collisions to the right
			if (mParent.hasLevelCollisionAt(cx + 1, cy) && rx > 0.7f) {
				rx = 0.7f; // limit ratio
				dx = 0; // kill velocity
			}

			// Check collision to the left
			if (mParent.hasLevelCollisionAt(cx - 1, cy) && rx <= 0.3f) {
				rx = 0.3f; // limit ratio
				dx = 0; // kill velocity
			}

			if (mParent.hasLevelCollisionAt(cx, cy + 1) && ry > 0.7f) {
				ry = 0.7f; // limit ratio
				dy = 0; // kill velocity
			}

			// Check collision to the left
			if (mParent.hasLevelCollisionAt(cx, cy - 1) && ry <= 0.3f) {
				ry = 0.3f; // limit ratio
				dy = 0; // kill velocity
			}

			// Check collisions with other entities
			int lEntCount = mParent.entities().size();
			for (int i = 0; i < lEntCount; i++) {
				CellWorldEntity e = mParent.entities().get(i);
				if (e == this || !e.isInUse())
					continue;

				// Fast distance check
				if (e != this && Math.abs(cx - e.cx) <= 12 && Math.abs(cy - e.cy) <= 12) {
					float exx = e.xx - xx;
					float eyy = e.yy - yy;

					float dist = (float) Math.sqrt(exx * exx + eyy * eyy);
					if (dist == 0) {
						dx -= 0.1f;
						dy -= 0.1f;
						e.dx += 0.1f;
						e.dy += 0.1f;
					}

					else if (dist <= radius + e.radius) {

						float force = 0.1f;

						// figure out who to repel ..
						if (coll_repel_precedence < e.coll_repel_precedence) {
							// I go
							float repelPower = (radius + e.radius - dist) / (radius + e.radius);

							dx -= (exx / dist) * repelPower * force * 2;
							dy -= (eyy / dist) * repelPower * force * 2;
						} else if (coll_repel_precedence > e.coll_repel_precedence) {
							// They go
							float repelPower = (radius + e.radius - dist) / (radius + e.radius);

							e.dx += (exx / dist) * repelPower * force * 2;
							e.dy += (eyy / dist) * repelPower * force * 2;
						} else {
							// We go
							float repelPower = (radius + e.radius - dist) / (radius + e.radius);

							dx -= (exx / dist) * repelPower * force;
							dy -= (eyy / dist) * repelPower * force;
							e.dx += (exx / dist) * repelPower * force;
							e.dy += (eyy / dist) * repelPower * force;
						}

					}

				}

			}

		}

		final float cap = 3;
		if (dx < -cap)
			dx = -cap;
		if (dy < -cap)
			dy = -cap;

		if (dx > cap)
			dx = cap;
		if (dy > cap)
			dy = cap;

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

	public void init() {
		isAlive = true;

	}

	public void kill() {
		isAlive = false;

	}

	public void setCoordinate(float pX, float pY, int pCellSize) {
		xx = pX;
		yy = pY;
		x = xx;
		y = yy;
		cx = (int) (xx / (float) pCellSize);
		cy = (int) (yy / (float) pCellSize);
		rx = (xx - cx * pCellSize) / pCellSize;
		ry = (yy - cy * pCellSize) / pCellSize;

	}

	public void attachParent(CellGridWorld pParent) {
		mParent = pParent;

	}

	public void detachParent() {
		mParent = null;

	}

	public boolean checkCollision(float xY, float pY, float pR) {
		float exx = xY - xx;
		float eyy = pY - yy;

		float dist = (float) Math.sqrt(exx * exx + eyy * eyy);

		if (dist <= radius + pR) {
			return true;

		}

		// nothing
		return false;

	}

}
