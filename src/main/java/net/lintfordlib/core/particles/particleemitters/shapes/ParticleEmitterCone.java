package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterCone extends ParticleEmitterShape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1621931192666348699L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;
	public float heading;
	public float maxAngle;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterCone() {
		super(ParticleEmitterCone.class.getSimpleName());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {

		final var nh = (float) Math.toRadians(this.heading);
		final var nma2 = (float) Math.toRadians(maxAngle);

		final var lRandomHeading = nh + RandomNumbers.random(0, nma2) - nma2 / 2.f;

		final var lVelX = (float) Math.cos(lRandomHeading) * force;
		final var lVelY = (float) Math.sin(lRandomHeading) * force;

		particleSystem.spawnParticle(worldX, worldY, -0.02f, lVelX, lVelY);

	}
}
