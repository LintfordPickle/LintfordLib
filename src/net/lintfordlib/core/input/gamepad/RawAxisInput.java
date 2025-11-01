package net.lintfordlib.core.input.gamepad;

public class RawAxisInput {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float value;
	public int rawAxisId;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public RawAxisInput(int rawAxisId, float value) {
		this.value = value;
		this.rawAxisId = rawAxisId;
	}
}
