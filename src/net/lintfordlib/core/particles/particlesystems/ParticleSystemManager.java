package net.lintfordlib.core.particles.particlesystems;

import java.io.File;
import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.entities.instances.PoolInstanceManager;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.deserializer.ParticleSystemDeserializer;

public class ParticleSystemManager extends PoolInstanceManager<ParticleSystemInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public class ParticleSystemDefinitionManager extends DefinitionManager<ParticleSystemDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ParticleSystemDefinitionManager() {
			loadDefinitionsFromMetaFile(new File(ParticleSystemConstants.PARTICLE_SYSTEM_META_FILENAME));
		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void loadDefinitionsFromFolderWatcher(EntityLocationProvider pEntityLocationProvider) {
			final var lGson = new GsonBuilder().create();
			loadDefinitionsFromFolderWatcherItems(pEntityLocationProvider, lGson, ParticleSystemDefinition.class);
		}

		@Override
		public void loadDefinitionsFromMetaFile(File filepath) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			loadDefinitionsFromMetaFileItems(filepath, lGson, ParticleSystemDefinition.class);
		}

		@Override
		public ParticleSystemDefinition loadDefinitionFromFile(File file) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			return loadDefinitionFromFile(file, lGson, ParticleSystemDefinition.class);
		}

		public void saveDefinitionEntriesToMetaFile(String metaDataFilename) {

		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ParticleFrameworkData mParticleFrameworkData;
	protected final ParticleSystemDefinitionManager mParticleSystemDefinitionManager = new ParticleSystemDefinitionManager();
	private int ParticleSystemUidCounter;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/** Returns the number of {@link ParticleSystemInstance}s in this {@link GameParticleSystem} instance. */
	public int getNumParticleSystems() {
		return mInstances.size();
	}

	public List<ParticleSystemInstance> particleSystems() {
		return mInstances;
	}

	public ParticleSystemDefinitionManager definitionManager() {
		return mParticleSystemDefinitionManager;
	}

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleSystemManager(ParticleFrameworkData particleFrameworkData) {
		super();

		mParticleFrameworkData = particleFrameworkData;
		ParticleSystemUidCounter = 0;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public ParticleSystemInstance getParticleSystemByDefiniton(ParticleSystemDefinition particleSystemDef) {
		return getParticleSystemByDefiniton(particleSystemDef, true);
	}

	public ParticleSystemInstance getParticleSystemByDefiniton(ParticleSystemDefinition particleSystemDef, boolean useShared) {
		if (useShared) {
			// If an instance already exists, then return it
			final var lNumParticleSystems = mInstances.size();
			for (var i = 0; i < lNumParticleSystems; i++) {
				final var lParticleSystemInstance = mInstances.get(i);
				if (!lParticleSystemInstance.isInitialized())
					continue;

				if (lParticleSystemInstance.definition().name.equals(particleSystemDef.name))
					return mInstances.get(i);

			}
		}

		return createNewParticleSystemFromDefinition(particleSystemDef);
	}

	public ParticleSystemInstance getParticleSystemByName(String particleSystemName) {
		return getParticleSystemByName(particleSystemName, true);
	}

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(String particleSystemName, boolean shared) {
		if (shared) {
			final var lNumParticleSystems = mInstances.size();
			for (var i = 0; i < lNumParticleSystems; i++) {
				final var lParticleSystemInstance = mInstances.get(i);
				if (!lParticleSystemInstance.isInitialized())
					continue;

				final var lParticleSystemDefName = lParticleSystemInstance.definition().name;
				final var lToFindName = particleSystemName;

				if (lParticleSystemDefName.equals(lToFindName)) {
					return mInstances.get(i);
				}
			}
		}

		return createNewParticleSystemFromDefinitionName(particleSystemName);
	}

	public ParticleSystemInstance createNewParticleSystemFromDefinitionName(String particleSystemName) {
		final var lParticleSystemDefinition = mParticleSystemDefinitionManager.getByName(particleSystemName);

		if (lParticleSystemDefinition == null) {
			Debug.debugManager().logger().e(getClass().getSimpleName(), "Could not resolve particle system by name: " + particleSystemName);
			return null;
		}

		return createNewParticleSystemFromDefinition(lParticleSystemDefinition);
	}

	public void assignSystemDefinitionAndResolveEmitters(ParticleSystemInstance particleSystemInstance, String particleSystemDefName) {
		final var particleSystemDef = mParticleSystemDefinitionManager.getByName(particleSystemDefName);
		if (particleSystemInstance != null && particleSystemDef != null) {
			particleSystemInstance.assignSystemDefinitionAndResolveEmitters(ParticleSystemUidCounter++, particleSystemDef, mParticleFrameworkData);
			mInstances.add(particleSystemInstance);
		}
	}

	public ParticleSystemInstance createNewParticleSystemFromDefinition(ParticleSystemDefinition particleSystemDef) {
		if (particleSystemDef != null) {
			final var lNewParticleSystem = getFreePooledItem();
			lNewParticleSystem.assignSystemDefinitionAndResolveEmitters(ParticleSystemUidCounter++, particleSystemDef, mParticleFrameworkData);

			mInstances.add(lNewParticleSystem);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", particleSystemDef.name));

			return lNewParticleSystem;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't resolve particle system by definition name '%s'", particleSystemDef));

		return null;
	}

	public void returnParticleSystem(ParticleSystemInstance particleSystem) {
		if (particleSystem == null)
			return;

		if (mInstances.contains(particleSystem)) {
			mInstances.remove(particleSystem);
		}

		particleSystem.unload();
		particleSystem = null;

	}

	@Override
	protected ParticleSystemInstance createNewInstance() {
		return new ParticleSystemInstance();
	}

}
