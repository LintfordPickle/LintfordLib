package net.lintfordlib.core.particles.particlesystems;

import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.core.entities.definitions.BaseDefinition;
import net.lintfordlib.core.particles.particlesystems.initializers.ParticleInitializerBase;
import net.lintfordlib.core.particles.particlesystems.modifiers.ParticleModifierBase;

public class ParticleSystemDefinition extends BaseDefinition {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2707111867124189440L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public List<ParticleInitializerBase> initializers;
	public List<ParticleModifierBase> modifiers;

	public int maxParticleCount;
	public float particleLifeMin;
	public float particleLifeMax;

	public String textureName;
	public String textureFilename;
	public int textureFilterMode;

	public String onDeathEmitterName;
	
	public int glSrcBlendFactor;
	public int glDestBlendFactor;

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

	/** Returns the name of an emitter (in the current entityGroupUId) which should be invoked on each particles death. */
	public void onDeathEmitterName(String newOnDeathEmitterName) {
		onDeathEmitterName = newOnDeathEmitterName;
	}

	/** Returns the name of an emitter (in the current entityGroupUId) which should be invoked on each particles death. */
	public String onDeathEmitterName() {
		return onDeathEmitterName;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemDefinition() {
		initializers = new ArrayList<>();
		modifiers = new ArrayList<>();
	}
}
