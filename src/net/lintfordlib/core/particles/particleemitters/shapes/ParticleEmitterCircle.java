package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.ConstantsMath;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterCircle extends ParticleEmitterShape {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1423935026127420797L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterCircle() {
		this(5.f);
	}

	public ParticleEmitterCircle(float radius) {
		super(ParticleEmitterCircle.class.getSimpleName());

		this.radius = radius;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float zDepth, float heading, float force) {
		final var lRandomHeading = RandomNumbers.nextFloat() * ConstantsMath.Pi * 2;
		final var xx = worldX + (float) Math.cos(lRandomHeading) * radius;
		final var yy = worldY + (float) Math.sin(lRandomHeading) * radius;

		final var lVelX = (float) Math.cos(heading) * force;
		final var lVelY = (float) Math.sin(heading) * force;

		particleSystem.spawnParticle(xx, yy, zDepth, lVelX, lVelY);
	}
}
