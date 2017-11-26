package net.lintford.library.core.collisions;

import java.io.Serializable;

import net.lintford.library.core.maths.Vector2f;

public class RaycastOrigins implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 6299314221181326655L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Vector2f topLeft, topRight;
	public Vector2f bottomLeft, bottomRight;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RaycastOrigins() {
		topLeft = new Vector2f();
		topRight = new Vector2f();
		bottomLeft = new Vector2f();
		bottomRight = new Vector2f();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
