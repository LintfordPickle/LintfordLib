package net.lintford.library.core.graphics.shaders;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.maths.MathUtil;
import net.lintford.library.core.maths.Matrix4f;

public class ShaderSubPixel extends Shader {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public final static String SHADER_NAME = "BasicShader";

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

	private int mTimeLocationID;
	private int mScreenResolutionLocationID;
	private int mCameraResolutionLocationID;
	private int mCameraZoomFactorLocationID;

	private float mTime;
	private float mScreenResolutionW, mScreenResolutionH;
	private float mCameraResolutionW, mCameraResolutionH;
	private float mCameraZoomFactor;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void time(float pTime) {
		mTime = pTime;
	}

	public void screenResolutionWidth(float pWidth) {
		mScreenResolutionW = pWidth;
	}

	public void screenResolutionHeight(float pHeight) {
		mScreenResolutionH = pHeight;
	}

	public void cameraResolutionWidth(float pWidth) {
		mCameraResolutionW = pWidth;
	}

	public void cameraResolutionHeight(float pHeight) {
		mCameraResolutionH = pHeight;
	}

	public void cameraZoomFactor(float pZoomFactor) {
		mCameraZoomFactor = pZoomFactor;
	}

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

	public ShaderSubPixel(String pName, String pVertPath, String pFragPath) {
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

		if (mTimeLocationID != -1) {
			GL20.glUniform1f(mTimeLocationID, mTime);
		}

		if (mScreenResolutionLocationID != -1) {
			GL20.glUniform2f(mScreenResolutionLocationID, mScreenResolutionW, mScreenResolutionH);
		}

		if (mCameraResolutionLocationID != -1) {
			GL20.glUniform2f(mCameraResolutionLocationID, mCameraResolutionW, mCameraResolutionH);
		}

		if (mCameraZoomFactorLocationID != -1) {
			GL20.glUniform1f(mCameraZoomFactorLocationID, mCameraZoomFactor);
		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void bindAtrributeLocations(int pShaderID) {
		GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
		GL20.glBindAttribLocation(pShaderID, 1, "inColor");
		GL20.glBindAttribLocation(pShaderID, 2, "inTexCoord");

	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mProjectionMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_PROJECTION_NAME);
		mViewMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_VIEW_NAME);
		mModelMatrixLocation = GL20.glGetUniformLocation(shaderID(), SHADER_UNIFORM_MODEL_NAME);

		mTimeLocationID = GL20.glGetUniformLocation(shaderID(), "fGlobalTime");
		mScreenResolutionLocationID = GL20.glGetUniformLocation(shaderID(), "v2ScreenResolution");
		mCameraResolutionLocationID = GL20.glGetUniformLocation(shaderID(), "v2CameraResolution");
		mCameraZoomFactorLocationID = GL20.glGetUniformLocation(shaderID(), "fCameraZoomFactor");

	}

}