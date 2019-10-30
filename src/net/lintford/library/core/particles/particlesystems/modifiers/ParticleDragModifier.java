package net.lintford.library.core.particles.particlesystems.modifiers;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public class ParticleDragModifier extends ParticleModifierBase {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2114770442744280867L;

	public static final String MODIFIER_NAME = "ParticleDragModifier";

	// FIXME: Need to contralize this physics
	private final static float DRAG_CONSTANT = 0.75f;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleDragModifier() {
		super(MODIFIER_NAME);

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
		pParticle.dx *= DRAG_CONSTANT;
		pParticle.dy *= DRAG_CONSTANT;

		if (Math.abs(pParticle.dx) < ConstantsTable.EPSILON)
			pParticle.dx = 0;
		if (Math.abs(pParticle.dy) < 0.001f)
			pParticle.dy = 0;
	}

}
