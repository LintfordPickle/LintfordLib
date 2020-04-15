package net.lintford.library.core.graphics.shaders;

import java.util.HashMap;
import java.util.Map;

import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;

public class ShaderManager {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SYSTEM_SHADER_PCT_NAME = "SHADER_SYSTEM_PCT";
	public static final String SYSTEM_VERT_FILENAME = "/res/shaders/shader_basic_pct.vert";
	public static final String SYSTEM_FRAG_FILENAME = "/res/shaders/shader_basic_pct.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ResourceManager mResourceManager;
	private Map<String, Shader> mShaderMap;

	private ShaderMVP_PCT mSystemShader;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ShaderManager() {
		mShaderMap = new HashMap<>();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;

		mSystemShader = new ShaderMVP_PCT(SYSTEM_SHADER_PCT_NAME, SYSTEM_VERT_FILENAME, SYSTEM_FRAG_FILENAME);
		mSystemShader.loadGLContent(mResourceManager);

		for (final var lShader : mShaderMap.values()) {
			lShader.loadGLContent(pResourceManager);

		}

	}

	public void unloadGLContent() {
		for (final var lShader : mShaderMap.entrySet()) {
			lShader.getValue().unloadGLContent();

		}

		mShaderMap.clear();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void reloadShaders() {
		Debug.debugManager().logger().v(getClass().getSimpleName(), "Reloading all modified shader files ... ");

		for (final var lShader : mShaderMap.values()) {
			if (lShader != null) {
				lShader.reloadShader();

			}

		}

	}

	public void unloadShader(Shader pShader, int pEntityGroupID) {
		if (pShader == null)
			return; // already lost reference

		if (mShaderMap.containsValue(pShader)) {
			String lShaderName = pShader.name();

			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d\n", lShaderName, pEntityGroupID));

			pShader.unloadGLContent();

			mShaderMap.remove(lShaderName);
			pShader = null;

		}

		return;

	}

	public Shader getShader(String pShaderName) {
		if (mShaderMap.containsKey(pShaderName)) {
			return mShaderMap.get(pShaderName);

		}

		return mSystemShader;
	}

}
