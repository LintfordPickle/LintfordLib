package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomSingleValueInitializer;

public class ParticleRandomRotationInitializer extends ParticleRandomSingleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -208992042047147087L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitializer() {
		this(0.f, 1.f);
	}

	public ParticleRandomRotationInitializer(float minRotAmt, float maxRotAmt) {
		super(ParticleRandomRotationInitializer.class.getSimpleName());

		minValue = minRotAmt;
		maxValue = maxRotAmt;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float rotationAmount) {
		particle.rotationInRadians = rotationAmount;
		particle.dr = rotationAmount;
	}
}
