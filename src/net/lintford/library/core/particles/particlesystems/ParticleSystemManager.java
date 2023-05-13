package net.lintford.library.core.particles.particlesystems;

import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entities.EntityLocationProvider;
import net.lintford.library.core.entities.definitions.DefinitionManager;
import net.lintford.library.core.entities.instances.InstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particlesystems.deserializer.ParticleSystemDeserializer;

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
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected ParticleFrameworkData mParticleFrameworkData;
	protected ParticleSystemDefinitionManager mParticleSystemDefinitionManager;
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
		mParticleSystemDefinitionManager = new ParticleSystemDefinitionManager();
		ParticleSystemUidCounter = 0;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(final String particleSystemName) {
		final var lNumParticleSystems = mInstances.size();
		for (var i = 0; i < lNumParticleSystems; i++) {
			final var lParticleSystemInstance = mInstances.get(i);
			if (!lParticleSystemInstance.isInitialized())
				continue;

			if (lParticleSystemInstance.definition().name.equals(particleSystemName)) {
				return mInstances.get(i);
			}
		}

		final var lParticleSystemDefinition = mParticleSystemDefinitionManager.getByName(particleSystemName);
		if (lParticleSystemDefinition != null) {
			final var lNewParticleSystem = new ParticleSystemInstance();
			lNewParticleSystem.initialize(ParticleSystemUidCounter++, lParticleSystemDefinition);

			mInstances.add(lNewParticleSystem);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", lParticleSystemDefinition.name));

			return lNewParticleSystem;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't find ParticleSystemDefinition '%s'", particleSystemName));

		return null;
	}
}
