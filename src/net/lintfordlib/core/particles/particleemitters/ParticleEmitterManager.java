package net.lintfordlib.core.particles.particleemitters;

import java.io.File;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.entities.instances.ClosedPoolInstanceManager;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.ParticleSystemInstance;

public class ParticleEmitterManager extends ClosedPoolInstanceManager<ParticleEmitterInstance> {

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

	public ParticleEmitterInstance getParticleEmitterByDefiniton(ParticleEmitterDefinition emitterDefinition) {
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
			lNewEmitterInst.assignEmitterDefinitionAndResolveParticleSystem(emitterDefinition, mParticleFrameworkData);

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

	public void addParticleEmitterInstance(final ParticleEmitterInstance particleEmitterInstance) {
		if (!mInstances.contains(particleEmitterInstance)) {
			mInstances.add(particleEmitterInstance);
		}
	}

	public void removeParticleEmitterInstance(final ParticleEmitterInstance particleEmitterInstance) {
		particleEmitterInstance.reset();

		final var lChildInstances = particleEmitterInstance.childEmitters();
		final var lNumChildEmitters = lChildInstances.length;
		for (int i = 0; i < lNumChildEmitters; i++) {
			if (lChildInstances[i] != null) {
				lChildInstances[i].reset();

				if (mInstances.contains(lChildInstances[i])) {
					mInstances.remove(lChildInstances[i]);
				}

				lChildInstances[i] = null;
			}
		}

		if (mInstances.contains(particleEmitterInstance)) {
			mInstances.remove(particleEmitterInstance);
		}

		returnPooledItem(particleEmitterInstance);
	}

	@Override
	protected ParticleEmitterInstance createPoolObjectInstance() {
		return new ParticleEmitterInstance();
	}
}