package net.lintford.library.core.graphics.shaders;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.maths.MathUtil;
import net.lintford.library.core.maths.Matrix4f;

public class ShaderMVP_PC extends Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public final static String SHADER_NAME = "ShaderMVP_PC";

	public final static String SHADER_DEFAULT_VERT = "/res/shaders/shader_basic_pc.vert";
	public final static String SHADER_DEFAULT_FRAG = "/res/shaders/shader_basic_pc.frag";

	public final static String SHADER_UNIFORM_PROJECTION_NAME = "projectionMatrix";
	public final static String SHADER_UNIFORM_VIEW_NAME = "viewMatrix";
	public final static String SHADER_UNIFORM_MODEL_NAME = "modelMatrix";

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

	public void projectionMatrix(Matrix4f pProjMat) {
		mProjectionMatrix = pProjMat;
	}

	public Matrix4f viewMatrix() {
		return mViewMatrix;
	}

	public void viewMatrix(Matrix4f pViewMat) {
		mViewMatrix = pViewMat;
	}

	public Matrix4f modelMatrix() {
		return mModelMatrix;
	}

	public void modelMatrix(Matrix4f pModelMat) {
		mModelMatrix = pModelMat;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ShaderMVP_PC(String pName, String pVertPath, String pFragPath) {
		super(pName, pVertPath, pFragPath);

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
	protected void bindAtrributeLocations(int pShaderID) {
		GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
		GL20.glBindAttribLocation(pShaderID, 1, "inColor");
	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mProjectionMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_PROJECTION_NAME);
		mViewMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_VIEW_NAME);
		mModelMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_MODEL_NAME);
	}

}