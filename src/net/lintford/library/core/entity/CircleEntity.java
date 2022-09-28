package net.lintford.library.core.entity;

public abstract class CircleEntity extends WorldEntity {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -8926247832433941959L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected float mRadius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float radius() {
		return mRadius;
	}

	public void radius(float radius) {
		mRadius = radius;
	}

	// --------------------------------------
	// Properties
	// --------------------------------------

	public CircleEntity() {
		super();

		mRadius = 0.5f;
	}
}
