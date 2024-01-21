package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.ConstantsMath;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterExplosion extends ParticleEmitterShape {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float radius;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterExplosion() {
		this(5.f);
	}

	public ParticleEmitterExplosion(float radius) {
		super(EmitterType.Point);

		this.radius = radius;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {
		final var lRandomHeading = RandomNumbers.nextFloat() * ConstantsMath.Pi * 2;
		float xx = (float) Math.cos(lRandomHeading) * radius;
		float yy = (float) Math.sin(lRandomHeading) * radius;

		force = 50.f;
		particleSystem.spawnParticle(worldX + xx, worldY + yy, -0.02f, xx * force, yy * force);
	}
}
