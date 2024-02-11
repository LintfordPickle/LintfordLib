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

	public String spritesheetName;
	public String spritesheetFilepath;
	public String spriteName;

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

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemDefinition() {
		initializers = new ArrayList<>();
		modifiers = new ArrayList<>();
	}
}
