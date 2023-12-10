package net.lintfordlib.core.physics;

public final class PhysicsSettings {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final PhysicsSettings DefaultSettings = new PhysicsSettings();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	public boolean enable_collision_resolver;
	public boolean enable_mtv_separation;

	public float hashGridWidthInUnits;
	public float hashGridHeightInUnits;
	public int hashGridCellsWide;
	public int hashGridCellsHigh;

	public float gravityX;
	public float gravityY;

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PhysicsSettings() {
		enable_collision_resolver = true;
		enable_mtv_separation = true;

		hashGridWidthInUnits = 20;
		hashGridHeightInUnits = 20;
		hashGridCellsWide = 5;
		hashGridCellsHigh = 5;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void initializeGrid(float withInUnits, float heightInUnits, int numCellsWide, int numCellsHigh) {
		hashGridWidthInUnits = withInUnits;
		hashGridHeightInUnits = heightInUnits;
		hashGridCellsWide = numCellsWide;
		hashGridCellsHigh = numCellsHigh;
	}
}
