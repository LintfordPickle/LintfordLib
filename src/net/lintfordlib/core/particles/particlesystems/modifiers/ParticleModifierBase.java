package net.lintfordlib.core.particles.particlesystems.modifiers;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.particles.Particle;

public abstract class ParticleModifierBase implements Serializable {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -2200502470690632635L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	@SerializedName(value = "className")
	protected String mModifierName;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public String name() {
		return mModifierName;
	}

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
