package net.lintford.library.core.graphics.shaders;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.lintford.library.core.EntityGroupManager;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;

public class ShaderManager extends EntityGroupManager {

	public class ShaderGroup {

		// --------------------------------------
		// Variables
		// --------------------------------------

		Map<String, Shader> mShaderMap;

		boolean automaticUnload = true;
		int entityGroupID;
		String name = "";
		int referenceCount = 0;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public Map<String, Shader> shaderMap() {
			return mShaderMap;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public ShaderGroup(int pEntityGroupID) {
			mShaderMap = new HashMap<>();

			entityGroupID = pEntityGroupID;
			referenceCount = 0;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		public Shader getShaderByName(String pTextureName) {
			if (mShaderMap.containsKey(pTextureName)) {
				return mShaderMap.get(pTextureName);

			}

			return null;
		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceManager mResourceManager;
	private Map<Integer, ShaderGroup> mShaderGroupMap;

	private boolean mIsLoaded;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isLoaded() {
		return mIsLoaded;
	}

	public ResourceManager resourceManager() {
		return mResourceManager;
	}

	public Map<Integer, ShaderGroup> shadersGroups() {
		return mShaderGroupMap;
	}

	public ShaderGroup shaderGroup(int pEntityGroupID) {
		if (!mShaderGroupMap.containsKey(pEntityGroupID)) {
			ShaderGroup lNewShaderGroup = new ShaderGroup(pEntityGroupID);
			mShaderGroupMap.put(pEntityGroupID, lNewShaderGroup);

			return lNewShaderGroup;
		}

		return mShaderGroupMap.get(pEntityGroupID);
	}

	public int shaderGroupCount() {
		return mShaderGroupMap.size();
	}

	public void reloadShaders() {
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Reloading all modified shader files ... ");

		for (ShaderGroup lShaderGroup : mShaderGroupMap.values()) {
			for (Shader lShader : lShaderGroup.mShaderMap.values()) {
				if (lShader != null) {
					lShader.reloadShader();

				}

			}

		}

	}

	/** Unloads the speicifed texture in the texture group, if applicable. */
	public void unloadShader(Shader pShader, int pEntityGroupID) {
		if (pShader == null)
			return; // already lost reference

		ShaderGroup lShaderGroup = mShaderGroupMap.get(pEntityGroupID);
		if (lShaderGroup == null) {
			return;

		} else if (lShaderGroup.mShaderMap.containsValue(pShader)) {
			String lShaderName = pShader.name();

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d\n", lShaderName, pEntityGroupID));

			pShader.unloadGLContent();

			lShaderGroup.mShaderMap.remove(lShaderName);
			pShader = null;

		}

		return;

	}

	public void unloadEntityGroup(int pEntityGroupID) {
		ShaderGroup lShaderGroup = mShaderGroupMap.get(pEntityGroupID);

		if (lShaderGroup == null)
			return;

		final int lShaderCount = lShaderGroup.mShaderMap.size();
		Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("Unloading ShaderGroup %d (freeing total %d shaders)", pEntityGroupID, lShaderCount));

		if (lShaderGroup != null) {
			// Iterate over all the shader in the group and unload them
			Iterator<Entry<String, Shader>> it = lShaderGroup.mShaderMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Shader> lNextShader = it.next();

				// Unload the shader
				lNextShader.getValue().unloadGLContent();

				it.remove();

			}

		}

	}

	@Override
	public int increaseReferenceCounts(int pEntityGroupID) {
		ShaderGroup lShaderGroup = mShaderGroupMap.get(pEntityGroupID);

		// Create a new TextureGroup for this EntityGroupID if one doesn't exist
		if (lShaderGroup == null) {
			lShaderGroup = new ShaderGroup(pEntityGroupID);
			lShaderGroup.referenceCount = 1;

			mShaderGroupMap.put(pEntityGroupID, lShaderGroup);

		} else {
			lShaderGroup.referenceCount++;

		}

		return lShaderGroup.referenceCount;

	}

	@Override
	public int decreaseReferenceCounts(int pEntityGroupID) {
		// TODO Auto-generated method stub
		return 0;
	}

}
