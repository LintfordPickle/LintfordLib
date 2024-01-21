package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.ConstantsMath;
import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterCircle extends ParticleEmitterShape {

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
		super(EmitterType.Circle);

		this.radius = radius;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {
		final var lRandomHeading = RandomNumbers.nextFloat() * ConstantsMath.Pi * 2;
		float xx = worldX + (float) Math.cos(lRandomHeading) * radius;
		float yy = worldY + (float) Math.sin(lRandomHeading) * radius;

		particleSystem.spawnParticle(xx, yy, -0.02f, .0f, .0f);
	}
}
