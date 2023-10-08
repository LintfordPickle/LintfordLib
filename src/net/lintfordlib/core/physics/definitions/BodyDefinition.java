package net.lintfordlib.core.physics.definitions;

import net.lintfordlib.core.maths.Vector2f;

public class BodyDefinition {

	public final Vector2f position = new Vector2f();
	public float angle = 0.0f;
	public final Vector2f linearVelocity = new Vector2f();
	public float angularVelocity = 0.0f;
	public float linearDamping = 1.0f;
	public float angularDamping = 0.0f;
	public boolean isStatic;

}
