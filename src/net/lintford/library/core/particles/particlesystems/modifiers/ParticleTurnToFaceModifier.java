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
	public void initialize(Particle pParticle) {

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		pParticle.rotationInRadians = (float) Math.atan2(pParticle.dx, -pParticle.dy) + (float)Math.toRadians(90.f);

	}

}
