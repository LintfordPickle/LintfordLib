package net.lintfordlib.core.particles.particlesystems;

import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.entities.EntityLocationProvider;
import net.lintfordlib.core.entities.definitions.DefinitionManager;
import net.lintfordlib.core.entities.instances.InstanceManager;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.particles.particlesystems.deserializer.ParticleSystemDeserializer;

public class ParticleSystemManager extends InstanceManager<ParticleSystemInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public class ParticleSystemDefinitionManager extends DefinitionManager<ParticleSystemDefinition> {

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ParticleSystemDefinitionManager() {

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
		public void loadDefinitionsFromMetaFile(String pMetaFilepath) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			loadDefinitionsFromMetaFileItems(pMetaFilepath, lGson, ParticleSystemDefinition.class);
		}

		@Override
		public void loadDefinitionFromFile(String pFilepath) {
			final var gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(ParticleSystemDefinition.class, new ParticleSystemDeserializer());
			final var lGson = gsonBuilder.create();

			loadDefinitionFromFile(pFilepath, lGson, ParticleSystemDefinition.class);
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
		final var lNumParticleSystems = mInstances.size();
		for (var i = 0; i < lNumParticleSystems; i++) {
			final var lParticleSystemInstance = mInstances.get(i);
			if (!lParticleSystemInstance.isInitialized())
				continue;

			if (lParticleSystemInstance.definition().equals(particleSystemDef.name)) {
				return mInstances.get(i);
			}
		}

		return createNewParticleSystemFromDefinition(particleSystemDef);
	}

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(String particleSystemName) {
		final var lNumParticleSystems = mInstances.size();
		for (var i = 0; i < lNumParticleSystems; i++) {
			final var lParticleSystemInstance = mInstances.get(i);
			if (!lParticleSystemInstance.isInitialized())
				continue;

			if (lParticleSystemInstance.definition().name.equals(particleSystemName)) {
				return mInstances.get(i);
			}
		}

		createNewParticleSystemFromDefinitionName(particleSystemName);

		return null;
	}

	public ParticleSystemInstance createNewParticleSystemFromDefinition(ParticleSystemDefinition particleSystemDef) {
		if (particleSystemDef != null) {
			final var lNewParticleSystem = new ParticleSystemInstance();
			lNewParticleSystem.initialize(ParticleSystemUidCounter++, particleSystemDef);

			mInstances.add(lNewParticleSystem);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", particleSystemDef.name));

			return lNewParticleSystem;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't resolve particle system by definition name '%s'", particleSystemDef));

		return null;
	}

	public ParticleSystemInstance createNewParticleSystemFromDefinitionName(String particleSystemName) {
		final var lParticleSystemDefinition = mParticleSystemDefinitionManager.getByName(particleSystemName);
		return createNewParticleSystemFromDefinition(lParticleSystemDefinition);
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

}
