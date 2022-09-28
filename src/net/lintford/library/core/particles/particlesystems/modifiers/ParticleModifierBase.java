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

	protected String mModifierName;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleModifierBase(final String modifierName) {
		mModifierName = modifierName;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** A method to initialize the state of a {@link Particle} on creation */
	public abstract void initialize(Particle particle);

	/** A method to update the state of the modifier itself */
	public abstract void update(LintfordCore core);

	/** A method to update the state of a {@link Particle} instance. */
	public abstract void updateParticle(LintfordCore core, Particle particle);

}
