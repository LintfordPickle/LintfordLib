package net.lintford.library.data.entities;

public class CircleEntity extends WorldEntity {

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

	@Override
	public boolean intersects(WorldEntity pOther) {
		// Poly
		if (pOther instanceof PolyEntity) {
			// TODO: Rectangle <-> Poly collision
		}

		// Rect
		else if (pOther instanceof RectangleEntity) {
			// TODO: Rectangle <-> Rectangle collision
		}

		// Circle
		else if (pOther instanceof CircleEntity) {
			// TODO: Rectangle <-> Circle collision
		}

		// no collision
		return false;
	}

}
