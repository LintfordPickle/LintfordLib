package net.lintfordlib.controllers.camera;

public interface ICameraChaseTarget {

	/**
	 * The x component of the target's world position.
	 */
	float worldX();

	/**
	 * The y component of the target's world position.
	 */
	float worldY();

	/**
	 * The x component of the target's lookahead position.
	 */
	float lookAheadX();

	/**
	 * The y component of the target's lookahead position.
	 */
	float lookAheadY();

	/**
	 * The speed of the target. Used to offset the camera in the 'lookAhead' direction.
	 */
	float speed2();

}
