package net.lintford.library.core.particles.particlesystems.modifiers;

import java.io.Serializable;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.particles.Particle;

public abstract class ParticleModifierBase implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2200502470690632635L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected String className;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleModifierBase(final String pName) {
		className = pName;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** A method to initialize the state of a {@link Particle} on creation */
	public abstract void initialize(Particle pParticle);

	/** A method to update the state of the modifier itself */
	public abstract void update(LintfordCore pCore);

	/** A method to update the state of a {@link Particle} instance. */
	public abstract void updateParticle(LintfordCore pCore, Particle pParticle);

}
