package net.lintford.library.core.graphics.particles.modifiers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.particles.Particle;

public interface IParticleModifier {

	/** A method to initialize the state of a {@link Particle} on creation */
	public abstract void initialize(Particle pParticle);

	/** A method to update the state of the modifier itself */
	public abstract void update(LintfordCore pCore);

	/** A method to update the state of a {@link Particle} instance. */
	public abstract void updateParticle(LintfordCore pCore, Particle pParticle);

}
