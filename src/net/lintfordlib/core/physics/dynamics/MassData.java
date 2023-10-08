package net.lintfordlib.core.physics.dynamics;

import net.lintfordlib.core.maths.Vector2f;

/**
 * Holds the mass data computed for a shape.
 */
public class MassData {

	/**
	 * The mass of the shape (Kg).
	 */
	public float mass;

	/**
	 * The position of the shape's centroid relative to the shape's origin.
	 */
	public final Vector2f center = new Vector2f();

	/**
	 * The rotational inertia of the shape about the local origin.
	 */
	public float inertia;

}
