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

	public CircleEntity(final int pPoolUid) {
		super(pPoolUid);

		radius = 0.5f;

	}

}
