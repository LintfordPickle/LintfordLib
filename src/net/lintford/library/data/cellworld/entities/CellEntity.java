package net.lintford.library.data.cellworld.entities;

import net.lintford.library.core.time.GameTime;

/** A simple entity class for a cell grid based world. */
public abstract class CellEntity implements CircleCollider {

	// -------------------------------------
	// Variables
	// -------------------------------------

	// base grid coordinates
	public int cx;
	public int cy;
	public float rx;
	public float ry;

	public float radius;

	/** Assigns a precedence (of weight) to this {@link CellEntity}. On collisions, heavier objects cannot be moved by lighter objects. */
	public int coll_repel_precedence;

	// Resulting coordinates
	public float xx;
	public float yy;

	// Movement
	public float dx;
	public float dy;

	public boolean isOnGround;
	public boolean isLeftFacing;

	public int health;

	// -------------------------------------
	// Properties
	// -------------------------------------

	@Override
	public float getRadius() {
		return radius;
	}

	@Override
	public void setRadius(float pNewValue) {
		radius = pNewValue;

	}

	public int health() {
		return health;

	}

	public boolean isAlive() {
		return health > 0;

	}

	// -------------------------------------
	// Constructor
	// -------------------------------------

	public CellEntity() {

	}

	public CellEntity(CellEntity pCopy) {
		copy(pCopy);

	}

	// -------------------------------------
	// Core-Methods
	// -------------------------------------

	public abstract void init();

	public abstract void kill();

	public abstract void update(final GameTime pGameTime);

	// -------------------------------------
	// Methods
	// -------------------------------------

	public void setPosition(float pWorldX, float pWorldY, int pCellSize) {
		xx = pWorldX;
		yy = pWorldY;
		cx = (int) (xx / (float) pCellSize);
		cy = (int) (yy / (float) pCellSize);
		rx = (xx - cx * pCellSize) / pCellSize;
		ry = (yy - cy * pCellSize) / pCellSize;

	}

	public void setVelocity(float pVelX, float pVelY) {
		dx = pVelX;
		dy = pVelY;

	}

	public void copy(CellEntity pOtherRect) {
		cx = pOtherRect.cx;
		cy = pOtherRect.cy;
		rx = pOtherRect.rx;
		ry = pOtherRect.ry;
		xx = pOtherRect.xx;
		yy = pOtherRect.yy;

	}

}
