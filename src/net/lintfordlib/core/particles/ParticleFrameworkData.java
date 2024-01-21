package net.lintfordlib.core.particles;

import java.io.File;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.particles.particleemitters.ParticleEmitterManager;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemManager;
import net.lintfordlib.data.BaseDataManager;
import net.lintfordlib.data.DataManager;

public class ParticleFrameworkData extends BaseDataManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public final static String DATA_MANAGER_NAME = ParticleFrameworkData.class.getSimpleName();

	public static String PARTICLE_SYSTEM_META_FILE = "res/def/particles/systems/_meta.json";
	public static String PARTICLE_EMITTER_META_FILE = "res/def/particles/emitters/_meta.json";

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

	public ParticleEmitterManager particleEmitterManager() {
		return mParticleEmitterManager;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleFrameworkData(DataManager dataManager, int entityGroupUid) {
		super(dataManager, DATA_MANAGER_NAME, entityGroupUid);

		mParticleSystemManager = new ParticleSystemManager(this);
		mParticleEmitterManager = new ParticleEmitterManager(this);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void loadFromMetaFiles() {
		loadFromMetaFiles(PARTICLE_SYSTEM_META_FILE, PARTICLE_EMITTER_META_FILE);
	}

	public void loadParticleEmittersFromMetaFile(String particleEmitterMetaFile) {
		final var lFile = new File(particleEmitterMetaFile);
		if (lFile == null || lFile.exists() == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to load particle emitters from meta file. The passed file doesn't exist.");
			return;
		}

		mParticleEmitterManager.definitionManager().loadDefinitionsFromMetaFile(lFile);
	}

	public void loadParticleSystemsFromMetaFile(String particleSystemMetaFile) {
		final var lFile = new File(particleSystemMetaFile);
		if (lFile == null || lFile.exists() == false) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Unable to load particle systems from meta file. The passed file doesn't exist.");
			return;
		}

		mParticleSystemManager.definitionManager().loadDefinitionsFromMetaFile(lFile);
	}

	public void loadFromMetaFiles(String particleSystemMetaFile, String particleEmitterMetaFile) {
		loadParticleSystemsFromMetaFile(particleSystemMetaFile);
		loadParticleEmittersFromMetaFile(particleEmitterMetaFile);
	}

}
