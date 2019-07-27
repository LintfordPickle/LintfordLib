package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.ConstantsTable;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public class ParticleDragModifier implements IParticleModifier {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private final static float DRAG_CONSTANT = 0.75f;

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
