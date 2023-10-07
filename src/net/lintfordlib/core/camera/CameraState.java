package net.lintfordlib.core.camera;

import net.lintfordlib.core.maths.Vector2f;

public class CameraState {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Vector2f acceleration;
	public Vector2f velocity;
	public Vector2f targetPosition;
	public Vector2f offsetPosition;
	public float zoomFactor = 1.0f;
	public float rotation = 0.0f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CameraState() {
		acceleration = new Vector2f();
		velocity = new Vector2f();
		targetPosition = new Vector2f();
		offsetPosition = new Vector2f();

	}

}
