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
	public float headingDegs;
	public float maxAngleDegs;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterCone() {
		super(ParticleEmitterCone.class.getSimpleName());
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float zDepth, float glHeadingRads, float forceX, float forceY) {

		final var nh = glHeadingRads + (float) Math.toRadians(this.headingDegs);
		final var nma2 = (float) Math.toRadians(maxAngleDegs);

		final var lRandomHeading = nh + RandomNumbers.random(0, nma2) - nma2 / 2.f;

		final var lVelX = (float) Math.cos(lRandomHeading) * forceX;
		final var lVelY = (float) Math.sin(lRandomHeading) * forceY;

		particleSystem.spawnParticle(worldX, worldY, zDepth, lVelX, lVelY);

	}
}
