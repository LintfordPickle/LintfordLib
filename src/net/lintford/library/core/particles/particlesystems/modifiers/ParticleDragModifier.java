package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

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

	public ParticleDragModifier(float pDragValue) {
		super(MODIFIER_NAME);

		mDrag = 0.75f;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Particle pParticle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(LintfordCore pCore) {

	}

	@Override
	public void updateParticle(LintfordCore pCore, Particle pParticle) {
		pParticle.dx *= mDrag;
		pParticle.dy *= mDrag;

		if (Math.abs(pParticle.dx) < EPSILON)
			pParticle.dx = 0;
		if (Math.abs(pParticle.dy) < 0.001f)
			pParticle.dy = 0;
	}

}
