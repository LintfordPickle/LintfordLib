package net.lintford.library.core.graphics.sprites.spritebatch;

import org.lwjgl.opengl.GL20;

import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.maths.Vector2f;

// FIXME: NormalBatchShader create objects per frame (FloatBuffer)
public class NormalBatchShader extends ShaderMVP_PT {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String VERT_FILENAME = "res/shaders/normal2d.vert";
	private static final String FRAG_FILENAME = "res/shaders/normal2d.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private int mTextureLocationDiffuse;
	private int mTextureLocationNormal;
	private int mDimensionsLocation;
	private int mSunPositionLocation;

	private Vector2f mSunPosition;
	private Vector2f mScreenDimension;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float screenDimensionsX() {
		return mScreenDimension.x;
	}

	public float screenDimensionsY() {
		return mScreenDimension.y;
	}

	public void screenDimensions(float pX, float pY) {
		mScreenDimension.x = pX;
		mScreenDimension.y = pY;
	}

	public float sunPositionX() {
		return mSunPosition.x;
	}

	public float sunPositionY() {
		return mSunPosition.y;
	}

	public void sunPosition(float pX, float pY) {
		mSunPosition.x = pX;
		mSunPosition.y = pY;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public NormalBatchShader() {
		super("NormalBatchShader", VERT_FILENAME, FRAG_FILENAME);

		mSunPosition = new Vector2f();
		mScreenDimension = new Vector2f();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	protected void bindAtrributeLocations(int pShaderID) {
		GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
		GL20.glBindAttribLocation(pShaderID, 1, "inTexCoord");

	}

	@Override
	protected void getUniformLocations() {
		super.getUniformLocations();

		mTextureLocationDiffuse = GL20.glGetUniformLocation(shaderID(), "diffuseSampler");
		mTextureLocationNormal = GL20.glGetUniformLocation(shaderID(), "normalSampler");
		mSunPositionLocation = GL20.glGetUniformLocation(shaderID(), "sunPosition");
		mDimensionsLocation = GL20.glGetUniformLocation(shaderID(), "dimensions");

		GL20.glUniform1i(mTextureLocationDiffuse, 0);
		GL20.glUniform1i(mTextureLocationNormal, 1);
	}

	@Override
	protected void updateUniforms() {
		super.updateUniforms();

		if (mSunPositionLocation != -1 && mSunPosition != null) {
			GL20.glUniform2f(mSunPositionLocation, mSunPosition.x, mSunPosition.y);
		}

		// TODO: needs protection
		GL20.glUniform2f(mDimensionsLocation, mScreenDimension.x, mScreenDimension.y);

	}

}
