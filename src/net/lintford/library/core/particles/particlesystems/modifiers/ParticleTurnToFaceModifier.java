package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

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
