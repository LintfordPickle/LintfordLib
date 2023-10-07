package net.lintfordlib.core.particles.particlesystems.modifiers;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

public class ParticleDragModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2114770442744280867L;

	public static final String MODIFIER_NAME = "ParticleDragModifier";

	public static final float EPSILON = 0.001f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mDrag;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDragModifier() {
		this(0.75f);
	}

	public ParticleDragModifier(float dragValue) {
		super(MODIFIER_NAME);

		mDrag = 0.75f;
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
		particle.dx *= mDrag;
		particle.dy *= mDrag;

		if (Math.abs(particle.dx) < EPSILON)
			particle.dx = 0;
		if (Math.abs(particle.dy) < 0.001f)
			particle.dy = 0;
	}
}
