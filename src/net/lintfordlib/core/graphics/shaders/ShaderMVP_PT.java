package net.lintfordlib.core.graphics.shaders;

import org.lwjgl.opengl.GL20;

import net.lintfordlib.core.maths.MathUtil;
import net.lintfordlib.core.maths.Matrix4f;

public class ShaderMVP_PT extends Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String SHADER_NAME = "ShaderMVP_PT";

	public static final String BASIC_VERT_FILENAME = "/res/shaders/shader_basic_pt.vert";
	public static final String BASIC_FRAG_FILENAME = "/res/shaders/shader_basic_pt.frag";

	public static final String SHADER_UNIFORM_PROJECTION_NAME = "projectionMatrix";
	public static final String SHADER_UNIFORM_VIEW_NAME = "viewMatrix";
	public static final String SHADER_UNIFORM_MODEL_NAME = "modelMatrix";

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected int mProjectionMatrixLocation;
	protected int mViewMatrixLocation;
	protected int mModelMatrixLocation;

	protected Matrix4f mProjectionMatrix;
	protected Matrix4f mViewMatrix;
	protected Matrix4f mModelMatrix;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public Matrix4f projectionMatrix() {
		return mProjectionMatrix;
	}

	public void projectionMatrix(Matrix4f projMatrix) {
		mProjectionMatrix = projMatrix;
	}

	public Matrix4f viewMatrix() {
		return mViewMatrix;
	}

	public void viewMatrix(Matrix4f viewMatrix) {
		mViewMatrix = viewMatrix;
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void modelMatrix(Matrix4f modelMatrix) {
		mModelMatrix = modelMatrix;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ShaderMVP_PT(String shaderName) {
		this(shaderName, BASIC_VERT_FILENAME, BASIC_FRAG_FILENAME);
	}

	public ShaderMVP_PT(String shaderName, String vertPath, String fragPath) {
		super(shaderName, vertPath, fragPath);

		mProjectionMatrix = new Matrix4f();
		mViewMatrix = new Matrix4f();
		mModelMatrix = new Matrix4f();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	protected void updateUniforms() {
		if (mProjectionMatrixLocation != -1 && mProjectionMatrix != null) {
			GL20.glUniformMatrix4fv(mProjectionMatrixLocation, false, MathUtil.getMatBufferColMaj(mProjectionMatrix));
		}

		if (mViewMatrixLocation != -1 && mViewMatrix != null) {
			GL20.glUniformMatrix4fv(mViewMatrixLocation, false, MathUtil.getMatBufferColMaj(mViewMatrix));
		}

		if (mModelMatrixLocation != -1 && mModelMatrix != null) {
			GL20.glUniformMatrix4fv(mModelMatrixLocation, false, MathUtil.getMatBufferColMaj(mModelMatrix));
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void bindAtrributeLocations(int shaderId) {
		GL20.glBindAttribLocation(shaderId, 0, "inPosition");
		GL20.glBindAttribLocation(shaderId, 1, "inTexCoord");
	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mProjectionMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_PROJECTION_NAME);
		mViewMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_VIEW_NAME);
		mModelMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_MODEL_NAME);
	}

}