package net.lintfordlib.core.particles.particleemitters.shapes;

import net.lintfordlib.core.maths.RandomNumbers;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterBox extends ParticleEmitterShape {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float width;
	public float height;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterBox() {
		this(10.f, 10.f);
	}

	public ParticleEmitterBox(float width, float height) {
		super(EmitterType.Point);

		this.width = width;
		this.height = height;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void spawn(ParticleSystemInstance particleSystem, float worldX, float worldY, float heading, float force) {
		float xx = worldX;
		float yy = worldY;

		if (width > 0.f) {
			final var width2 = width / 2.f;
			xx = worldX + RandomNumbers.random(-width2, width2);
		}

		if (height > 0.f) {
			final var height2 = height / 2.f;
			yy = worldY + RandomNumbers.random(-height2, height2);
		}

		particleSystem.spawnParticle(xx, yy, -0.02f, .0f, .0f);
	}
}
