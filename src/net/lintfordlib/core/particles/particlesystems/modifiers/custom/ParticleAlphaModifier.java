package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleAlphaModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5569879927420463121L;

	public static final String MODIFIER_NAME = "ParticleAlphaModifier";

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float minAlphaAmount;
	public float maxAlphaAmount;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleAlphaModifier() {
		super(MODIFIER_NAME);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle particle) {

	}

	@Override
	public void update(LintfordCore core) {

	}

	@Override
	public void updateParticle(LintfordCore core, Particle particle) {
		final var lNormalizedLifetime = particle.timeSinceStart / particle.lifeTime();
		particle.color.a = minAlphaAmount + (maxAlphaAmount - minAlphaAmount) * lNormalizedLifetime;
	}
}
