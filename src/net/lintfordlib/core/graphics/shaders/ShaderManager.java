package net.lintfordlib.core.graphics.shaders;

import java.util.HashMap;
import java.util.Map;

import net.lintfordlib.core.ResourceManager;
import net.lintfordlib.core.debug.Debug;

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
	private ShaderSubPixel mSystemShader;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ShaderManager() {
		mShaderMap = new HashMap<>();

		mSystemShader = new ShaderSubPixel(SYSTEM_SHADER_PCT_NAME, SYSTEM_VERT_FILENAME, SYSTEM_FRAG_FILENAME);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;

		mSystemShader.loadResources(mResourceManager);

		for (final var lShader : mShaderMap.values()) {
			lShader.loadResources(resourceManager);
		}
	}

	public void unloadResources() {
		for (final var lShader : mShaderMap.entrySet()) {
			lShader.getValue().unloadResources();
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

	public void unloadShader(Shader shader, int entityGroupUid) {
		if (shader == null)
			return; // already lost reference

		if (mShaderMap.containsValue(shader)) {
			Debug.debugManager().logger().i(getClass().getSimpleName(), String.format("unloading texture: %s from texture group %d\n", shader.name(), entityGroupUid));

			shader.unloadResources();

			mShaderMap.remove(shader.name());
			shader = null;
		}

		return;
	}

	public Shader getShader(String shaderName) {
		if (mShaderMap.containsKey(shaderName)) {
			return mShaderMap.get(shaderName);
		}

		return mSystemShader;
	}
}
