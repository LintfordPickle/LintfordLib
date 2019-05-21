package net.lintford.library.data.entities;

import net.lintford.library.data.BaseData;

/**
 * The {@link WorldEntity} class allows us to order an entity spatially within the world.
 */
public abstract class WorldEntity extends BaseData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3376631186484307065L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float x, y;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setPosition(float pWorldX, float pWorldY) {
		x = pWorldX;
		y = pWorldY;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public WorldEntity() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}