package net.lintfordlib.core.particles.particlesystems.initializers.custom;

import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleRandomDoubleValueInitializer;

public class ParticleRandomRotationInitializer extends ParticleRandomDoubleValueInitializer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2557012472077666152L;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleRandomRotationInitializer() {
		this(0.f, 0.f, 0.f, 0.f);
	}

	public ParticleRandomRotationInitializer(float minRotAmt, float maxRotAmt, float minAVAmt, float maxAVAmt) {
		super(ParticleRandomRotationInitializer.class.getSimpleName());

		minValue0 = minRotAmt;
		maxValue0 = maxRotAmt;
		minValue1 = minAVAmt;
		maxValue1 = maxAVAmt;
	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void onIntialiseParticle(Particle particle, float rotationAmount, float angVel) {
		particle.rotationInRadians = rotationAmount;
		particle.dr = rotationAmount;
	}
}
