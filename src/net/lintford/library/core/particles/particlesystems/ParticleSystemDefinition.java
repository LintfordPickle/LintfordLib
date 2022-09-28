package net.lintford.library.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.entity.definitions.BaseDefinition;
import net.lintford.library.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintford.library.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemDefinition extends BaseDefinition {

	// --------------------------------------
	// Variables
	// --------------------------------------

	public List<ParticleInitializerBase> initializers;
	public List<ParticleModifierBase> modifiers;

	public int maxParticleCount;
	public float particleLife;

	public String textureName;
	public String textureFilename;
	public int textureFilterMode;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<ParticleInitializerBase> initializers() {
		return initializers;
	}

	public List<ParticleModifierBase> modifiers() {
		return modifiers;
	}

	/** Returns the maximum ammount of particles which can be spawned by instances of this {@link ParticleSystemDefinition}. */
	public int maxParticleCount() {
		return maxParticleCount;
	}

	/** Returns the internal texture name. */
	public String textureName() {
		return textureName;
	}

	/** Returns the filename of the texture. */
	public String textureFilename() {
		return textureFilename;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemDefinition() {
		initializers = new ArrayList<>();
		modifiers = new ArrayList<>();
	}
}
