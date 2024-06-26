package net.lintfordlib.core.particles.particlesystems.modifiers.custom;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleDragModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2114770442744280867L;

	public static final float EPSILON = 0.001f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public float dragCoefficient;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDragModifier() {
		this(0.97f);
	}

	public ParticleDragModifier(float dragCoefficient) {
		super(ParticleDragModifier.class.getSimpleName());

		this.dragCoefficient = dragCoefficient;
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
		particle.vx *= dragCoefficient;
		particle.vy *= dragCoefficient;

		if (Math.abs(particle.vx) < EPSILON)
			particle.vx = 0;
		if (Math.abs(particle.vy) < EPSILON)
			particle.vy = 0;
	}
}
