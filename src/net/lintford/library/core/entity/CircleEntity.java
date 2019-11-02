package net.lintford.library.core.entity;

public abstract class CircleEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8926247832433941959L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float rad() {
		return radius;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	public CircleEntity() {
		// Set the default to unit circle length
		radius = 1.0f;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
