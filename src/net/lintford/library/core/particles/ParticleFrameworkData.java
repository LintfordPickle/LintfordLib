package net.lintford.library.core.particles;

import net.lintford.library.core.particles.particleemitters.ParticleEmitterManager;
import net.lintford.library.core.particles.particlesystems.ParticleSystemManager;

public class ParticleFrameworkData {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static String PARTICLE_SYSTEM_META_FILE = "res/def/particles/systems/_meta.json";
	public static String EMITTER_META_FILE = "res/def/particles/emitters/_meta.json";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ParticleSystemManager mParticleSystemManager;
	private ParticleEmitterManager mParticleEmitterManager;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleSystemManager particleSystemManager() {
		return mParticleSystemManager;
	}

	public ParticleEmitterManager emitterManager() {
		return mParticleEmitterManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkData() {
		mParticleSystemManager = new ParticleSystemManager(this);
		mParticleEmitterManager = new ParticleEmitterManager(this);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadSystemMetaDefinitionFile(String filepath) {
		mParticleSystemManager.definitionManager().loadDefinitionsFromMetaFile(filepath);
	}

	public void loadEmitterMetaDefinitionFile(String filepath) {
		mParticleEmitterManager.definitionManager().loadDefinitionsFromMetaFile(filepath);
	}
}
