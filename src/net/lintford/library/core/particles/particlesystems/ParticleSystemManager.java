package net.lintford.library.core.particles.particlesystems;

import java.util.List;

import com.google.gson.GsonBuilder;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.EntityLocationProvider;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.entity.instances.InstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particlesystems.deserializer.ParticleSystemDeserializer;

public class ParticleSystemManager extends InstanceManager<ParticleSystemInstance> {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -5013183501163339554L;

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

	public ParticleSystemManager(ParticleFrameworkData pParticleFrameworkData) {
		super();

		mParticleFrameworkData = pParticleFrameworkData;
		mParticleSystemDefinitionManager = new ParticleSystemDefinitionManager();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	/** Returns the {@link ParticleController} whose {@link ParticleSystemInstance}'s name matches the given {@link String}. null is returned if the ParticleController is not found. */
	public ParticleSystemInstance getParticleSystemByName(final String pParticleSystemName) {
		// If the named particle system has already been created, then return the instance
		final var lNumParticleSystems = mInstances.size();
		for (var i = 0; i < lNumParticleSystems; i++) {
			final var lParticleSystemInstance = mInstances.get(i);
			if (!lParticleSystemInstance.isInitialized())
				continue;

			if (lParticleSystemInstance.definition().name.equals(pParticleSystemName)) {
				return mInstances.get(i);

			}

		}

		// Otherwise create a new instance of the particle system.
		final var lParticleSystemDefinition = mParticleSystemDefinitionManager.getDefinitionByName(pParticleSystemName);
		if (lParticleSystemDefinition != null) {
			final var lNewParticleSystem = new ParticleSystemInstance();
			lNewParticleSystem.initialize(getNewInstanceUID(), lParticleSystemDefinition);

			mInstances.add(lNewParticleSystem);

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Created new ParticleSystemInstance for ParticleSystemDefinition '%s'", lParticleSystemDefinition.name));

			return lNewParticleSystem;
		}

		Debug.debugManager().logger().w(getClass().getSimpleName(), String.format("Couldn't find ParticleSystemDefinition '%s'", pParticleSystemName));

		return null;

	}

}
