package net.lintford.library.core.particles.particleemitters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.entity.definitions.DefinitionManager;
import net.lintford.library.core.entity.instances.PooledInstanceManager;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.core.particles.particlesystems.ParticleSystemManager;

public class ParticleEmitterManager extends PooledInstanceManager<ParticleEmitterInstance> {

	// --------------------------------------
	// Inner-Classes
	// --------------------------------------

	// Definition

	public class EmitterDefinitionMetaData {
		public String rootDirectory;
		public String[] emitterFileLocations;

	}

	public class EmitterDefinitionManager extends DefinitionManager<ParticleEmitterDefinition> {

		// --------------------------------------
		// Constants
		// --------------------------------------

		private static final long serialVersionUID = -689777947903201955L;

		public ParticleEmitterDefinition getDefinitionByName(String pEmitterName) {
			if (pEmitterName == null || pEmitterName.isEmpty())
				return null;

			final int lNumEmitters = mDefinitions.size();
			for (int i = 0; i < lNumEmitters; i++) {
				if (mDefinitions.get(i).name.equals(pEmitterName))
					return mDefinitions.get(i);
			}

			return null;
		}

		public ParticleEmitterDefinition getDefinitionByDefId(int pEmitterDefId) {
			final int lNumEmitters = mDefinitions.size();

			if (pEmitterDefId < 0 || pEmitterDefId >= lNumEmitters)
				return null;

			for (int i = 0; i < lNumEmitters; i++) {
				if (mDefinitions.get(i).definitionID == pEmitterDefId)
					return mDefinitions.get(i);
			}

			return null;
		}

		public ParticleEmitterDefinition getDefinitionByIndex(int pEmitterIndex) {
			final int lNumEmitters = mDefinitions.size();

			if (pEmitterIndex < 0 || pEmitterIndex >= lNumEmitters)
				return null;

			return mDefinitions.get(pEmitterIndex);
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public EmitterDefinitionManager() {
			loadDefinitions(ParticleEmitterConstants.PARTICLE_EMITTER_META_FILENAME);

		}

		// --------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void initialize(Object pParent) {
			super.initialize(pParent);

			if (pParent instanceof ParticleFrameworkData) {
				ParticleFrameworkData lParticleFrameworkData = (ParticleFrameworkData) pParent;
				ParticleSystemManager lParticleSystemManager = lParticleFrameworkData.particleSystemManager();

				// FIXME: Restore particle emitters and referenced objects (by their Ids).

			}

		}

		@Override
		protected void loadDefinitions(String pMetaFilepath) {
			final Gson GSON = new GsonBuilder().create();

			// Load the ItemDefiniion meta data (file locations)
			String lFileContents = null;
			EmitterDefinitionMetaData lItemsFileLocations = null;
			try {
				lFileContents = new String(Files.readAllBytes(Paths.get(pMetaFilepath)));
				lItemsFileLocations = GSON.fromJson(lFileContents, EmitterDefinitionMetaData.class);

				if (lItemsFileLocations == null || lItemsFileLocations.emitterFileLocations == null || lItemsFileLocations.emitterFileLocations.length == 0) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Couldn't load ParticleEmitterDefinitions from the particle emitter definition metafile!");

					return;

				}

			} catch (IOException e) {
				Debug.debugManager().logger().e(getClass().getSimpleName(), "Error while loading particle emitter definition metafile.");
				Debug.debugManager().logger().printException(getClass().getSimpleName(), e);

				return;

			}

			ParticleEmitterDefinition lEmitterDefinition = null;
			final int lEmitterDefinitionMetaCount = lItemsFileLocations.emitterFileLocations.length;
			for (int i = 0; i < lEmitterDefinitionMetaCount; i++) {
				String lEmitterDefinitionFilepath = lItemsFileLocations.rootDirectory + lItemsFileLocations.emitterFileLocations[i] + ".json";
				final File lEmitterDefinitionFile = new File(lEmitterDefinitionFilepath);

				if (!lEmitterDefinitionFile.exists()) {
					Debug.debugManager().logger().w(getClass().getSimpleName(), "Error loading ParticleEmitterDefinition from file " + lEmitterDefinitionFilepath + ". File doesn't exist!");

					continue;
				}

				try {
					lFileContents = new String(Files.readAllBytes(lEmitterDefinitionFile.toPath()));

					try {
						lEmitterDefinition = GSON.fromJson(lFileContents, ParticleEmitterDefinition.class);

					} catch (Exception e) {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse ParticleEmitterDefinition from file: " + lEmitterDefinitionFilepath);

					}

					if (lEmitterDefinition != null) {
						lEmitterDefinition.initialize(getNewDefinitionUID());
						mDefinitions.add(lEmitterDefinition);

					} else {
						Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse ParticleEmitterDefinition from file: " + lEmitterDefinitionFilepath);

					}

				} catch (JsonSyntaxException e) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON ParticleEmitterDefinition (Syntax): " + lEmitterDefinitionFilepath);
					Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				} catch (IOException e) {
					Debug.debugManager().logger().e(getClass().getSimpleName(), "Failed to parse JSON ParticleEmitterDefinition (IO): " + lEmitterDefinitionFilepath);
					Debug.debugManager().logger().e(getClass().getSimpleName(), e.getMessage());

					continue;

				}

			}

		}

	}

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = -831550615078707748L;

	public static final int PARTICLE_EMITTER_NO_ID = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected EmitterDefinitionManager mEmitterDefinitionManager;
	protected ParticleFrameworkData mParticleFrameworkData;
	protected transient List<ParticleEmitterInstance> mEmitters;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public ParticleFrameworkData particleFrameworkData() {
		return mParticleFrameworkData;
	}

	public EmitterDefinitionManager definitionManager() {
		return mEmitterDefinitionManager;
	}

	public List<ParticleEmitterInstance> emitterInstances() {
		return mEmitters;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ParticleEmitterManager(ParticleFrameworkData pParticleFrameworkData) {
		mParticleFrameworkData = pParticleFrameworkData;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(Object pParent) {
		mEmitters = new ArrayList<>();

		mEmitterDefinitionManager = new EmitterDefinitionManager();

		// Resolve all the ParticleSystems within the emitters to the ParticleSystem instances.
		if (pParent instanceof ParticleFrameworkData) {
			ParticleFrameworkData lFramework = (ParticleFrameworkData) pParent;

			mEmitterDefinitionManager.initialize(lFramework);

		}

	}

	public void loadDefinitionMetaFile(String pMetaFilename) {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// returns a new ParticleEmitterInstance attached to the given emitter definition (by name)
	public ParticleEmitterInstance getNewParticleEmitterInstanceByDefName(String pEmitterDefName) {
		// Find the specified definition
		ParticleEmitterDefinition lEmitterDef = definitionManager().getDefinitionByName(pEmitterDefName);

		if (lEmitterDef == null)
			return null;

		// Retrieve or create a new instance
		ParticleEmitterInstance lNewEmitterInst = getFreePooledItem();
		lNewEmitterInst.emitterInstanceId(getNewUID());
		lNewEmitterInst.assign(lEmitterDef, mParticleFrameworkData);

		mEmitters.add(lNewEmitterInst);

		return lNewEmitterInst;

	}

	// Returns a ParticleEmitterInstance, if one exists, based on the EmitterID.
	public ParticleEmitterInstance getParticleEmitterByIndex(int pEmitterIndex) {
		final int lNumParticleEmitterCount = mEmitters.size();
		for (int i = 0; i < lNumParticleEmitterCount; i++) {
			if (mEmitters.get(i).getPoolID() == pEmitterIndex)
				return mEmitters.get(i);

		}

		return null;
	}

	@Override
	protected ParticleEmitterInstance createPoolObjectInstance() {
		return new ParticleEmitterInstance();

	}

	public ParticleEmitterInstance getParticleEmitterInstance() {
		ParticleEmitterInstance lReturnItem = getFreePooledItem();
		lReturnItem.emitterInstanceId(getNewUID());

		return lReturnItem;

	}

	public void addCharacter(final ParticleEmitterInstance pParticleEmitterInstance) {
		if (!mEmitters.contains(pParticleEmitterInstance)) {
			mEmitters.add(pParticleEmitterInstance);

		}

	}

	public void removeCharacter(final ParticleEmitterInstance pParticleEmitterInstance) {
		returnPooledItem(pParticleEmitterInstance);

	}

}
