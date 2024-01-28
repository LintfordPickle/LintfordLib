package net.lintfordlib.core.particles.particleemitters;

import java.io.File;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.entities.instances.PoolInstanceManager;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterManager extends PoolInstanceManager<ParticleEmitterInstance> {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	public class ParticleEmitterDefinitionManager extends DefinitionManager<ParticleEmitterDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ParticleEmitterDefinitionManager() {
			loadDefinitionsFromMetaFile(new File(ParticleEmitterConstants.PARTICLE_EMITTER_META_FILENAME));
		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		public void initialize(Object parent) {

		}

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider entityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(entityLocationProvider, lGson, ParticleEmitterDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(File file) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromMetaFileItems(file, lGson, ParticleEmitterDefinition.class);
		}

		@Override
		public ParticleEmitterDefinition loadDefinitionFromFile(File file) {
			final var lGson = new GsonBuilder().create();

			return loadDefinitionFromFile(file, lGson, ParticleEmitterDefinition.class);
		}
	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int PARTICLE_EMIITER_NOT_ASSIGNED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ParticleFrameworkData mParticleFrameworkData;
	protected final ParticleEmitterDefinitionManager mEmitterDefinitionManager = new ParticleEmitterDefinitionManager();

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the number of {@link ParticleSystemInstance}s in this {@link GameParticleSystem} instance. */
	public int getNumParticleEmitters() {
		return mInstances.size();
	}

	public List<ParticleEmitterInstance> emitterInstances() {
		return mInstances;
	}

	public ParticleEmitterDefinitionManager definitionManager() {
		return mEmitterDefinitionManager;
	}

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterManager(ParticleFrameworkData particleFrameworkData) {
		mParticleFrameworkData = particleFrameworkData;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize(Object parent) {
		if (parent instanceof ParticleFrameworkData) {
			final var lFramework = (ParticleFrameworkData) parent;
			mEmitterDefinitionManager.initialize(lFramework);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public ParticleEmitterInstance getParticleEmitterInstanceByDefiniton(ParticleEmitterDefinition emitterDefinition) {
		// If an instance already exists, then return it
		final var lNumParticleEmitters = mInstances.size();
		for (var i = 0; i < lNumParticleEmitters; i++) {
			final var lParticleEmitterInstance = mInstances.get(i);
			if (!lParticleEmitterInstance.isInitialized())
				continue;

			final var lDefName = lParticleEmitterInstance.emitterDefinition().name;
			final var lToFindName = emitterDefinition.name;

			if (lDefName.equals(lToFindName))
				return mInstances.get(i);
		}

		return createNewParticleEmitterFromDefinition(emitterDefinition);
	}

	public ParticleEmitterInstance createNewParticleEmitterFromDefinition(ParticleEmitterDefinition emitterDefinition) {
		if (emitterDefinition != null) {
			final var lNewEmitterInst = getFreePooledItem();
			lNewEmitterInst.assignEmitterDefinition(emitterDefinition, mParticleFrameworkData);

			emitterDefinition.initialize(mParticleFrameworkData);

			mInstances.add(lNewEmitterInst);

			return lNewEmitterInst;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't resolve particle emitter by definition name '%s'", emitterDefinition));

		return null;
	}

	public ParticleEmitterInstance getParticleEmitterByIndex(int emitterIndex) {
		final var lNumParticleEmitterCount = mInstances.size();
		for (var i = 0; i < lNumParticleEmitterCount; i++) {
			if (mInstances.get(i).emitterInstanceId() == emitterIndex) {
				return mInstances.get(i);
			}
		}

		return null;
	}

	public void addParticleEmitterInstance(ParticleEmitterInstance particleEmitterInstance) {
		if (!mInstances.contains(particleEmitterInstance)) {
			mInstances.add(particleEmitterInstance);
		}
	}

	public void removeParticleEmitterInstance(ParticleEmitterInstance particleEmitterInstance) {
		particleEmitterInstance.reset();

		final var lChildInstances = particleEmitterInstance.childEmitters();
		final var lNumChildEmitters = lChildInstances.size();
		for (int i = 0; i < lNumChildEmitters; i++) {
			final var lEmitterInst = lChildInstances.get(i);

			if (lEmitterInst != null) {
				lEmitterInst.reset();

				if (mInstances.contains(lEmitterInst))
					mInstances.remove(lEmitterInst);

			}
		}

		if (mInstances.contains(particleEmitterInstance))
			mInstances.remove(particleEmitterInstance);

		returnInstance(particleEmitterInstance);
	}

	@Override
	protected ParticleEmitterInstance createNewInstance() {
		return new ParticleEmitterInstance();
	}

	public ParticleEmitterInstance getParticleEmitterByName(String particleEmitterName) {
		final var lDefinition = mEmitterDefinitionManager.getByName(particleEmitterName);
		if (lDefinition == null)
			return null;

		return createNewParticleEmitterFromDefinition(lDefinition);
	}
}