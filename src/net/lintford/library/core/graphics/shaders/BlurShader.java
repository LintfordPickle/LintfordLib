package net.lintford.library.core.graphics.shaders;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.maths.Vector2f;

public class BlurShader extends ShaderMVP_PT {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SHADER_UNIFORM_RESOLUTION_NAME = "resolution";
	public static final String SHADER_UNIFORM_RADIUS_NAME = "radius";
	public static final String SHADER_UNIFORM_DIRECTION_NAME = "dir";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private float mResolution;
	private float mRadius;
	private Vector2f mDirection;

	private int mResolutionLocation;
	private int mRadiusLocation;
	private int mDirectionLocation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void resolution(float newValue) {
		mResolution = newValue;
	}

	public float resolution() {
		return mResolution;
	}

	public void radius(float newValue) {
		mRadius = newValue;
	}

	public float radius() {
		return mRadius;
	}

	public void direction(Vector2f newValue) {
		mDirection.x = newValue.x;
		mDirection.y = newValue.y;
	}

	public Vector2f direction() {
		return mDirection;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public BlurShader(String vertPath, String fragPath) {
		super("BlurShader", vertPath, fragPath);

		mDirection = new Vector2f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mResolutionLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_RESOLUTION_NAME);
		mRadiusLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_RADIUS_NAME);
		mDirectionLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_DIRECTION_NAME);
	}

	@Override
	protected void updateUniforms() {
		super.updateUniforms();

		if (mResolutionLocation != -1) {
			GL20.glUniform1f(mResolutionLocation, mResolution);
		}

		if (mRadiusLocation != -1) {
			GL20.glUniform1f(mRadiusLocation, mRadius);
		}

		if (mDirectionLocation != -1) {
			GL20.glUniform2f(mDirectionLocation, mDirection.x, mDirection.y);
		}
	}

}
