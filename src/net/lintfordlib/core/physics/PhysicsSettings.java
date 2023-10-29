package net.lintfordlib.core.physics;

public final class PhysicsSettings {

	public static final PhysicsSettings DefaultSettings = new PhysicsSettings();

	public boolean enable_collision_resolver;
	public boolean enable_mtv_separation;

	public int hashGridWidthInUnits;
	public int hashGridHeightInUnits;
	public int hashGridCellsWide;
	public int hashGridCellsHigh;

	public PhysicsSettings() {
		enable_collision_resolver = true;
		enable_mtv_separation = true;

		hashGridWidthInUnits = 100;
		hashGridHeightInUnits = 100;
		hashGridCellsWide = 5;
		hashGridCellsHigh = 5;
	}
}
