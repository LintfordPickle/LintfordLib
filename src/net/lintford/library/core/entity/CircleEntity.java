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

	public CircleEntity() {
		super();

		radius = 0.5f;
	}
}
