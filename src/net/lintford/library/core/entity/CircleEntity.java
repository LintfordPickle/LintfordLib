package net.lintford.library.core.entity;

public abstract class CircleEntity extends Entity {

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

	public CircleEntity(int entityUid) {
		super(entityUid);

		radius = 0.5f;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setCircle(float centerX, float centerY, float radius) {
		this.x = centerX;
		this.y = centerY;
		this.radius = radius;
	}
}
