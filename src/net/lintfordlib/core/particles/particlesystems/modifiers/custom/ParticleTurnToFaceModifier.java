package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleTurnToFaceModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -1511765721627415767L;

	public static final String INITIALIZER_NAME = "ParticleTurnToFaceModifier";

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleTurnToFaceModifier() {
		super(INITIALIZER_NAME);

	}

	// --------------------------------------
	// Inherited-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {

	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		particle.rotationInRadians = (float) Math.atan2(particle.dx, -particle.dy) + (float) Math.toRadians(90.f);
	}
}
