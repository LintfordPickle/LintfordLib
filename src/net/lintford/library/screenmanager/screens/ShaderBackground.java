package net.lintford.library.screenmanager.screens;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public abstract class ShaderBackground extends Screen {

	public class BackgroundShader extends ShaderMVP_PT {

		// --------------------------------------
		// Variables
		// --------------------------------------

		private int mTimeLocationID;
		private int mScreenResolutionLocationID;
		private int mCameraResolutionLocationID;
		private int mCameraZoomFactorLocationId;
		private int mv2MouseWindowCoordsLocationID;

		private float mTime;
		private float mScreenResolutionW, mScreenResolutionH;
		private float mCameraResolutionW, mCameraResolutionH;
		private float mCameraZoomFactor;
		private float mMouseWindowCoordsX, mMouseWindowCoordsY;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public void time(float pTime) {
			mTime = pTime;
		}

		public void cameraZoomFactor(float pCameraZoomFactor) {
			mCameraZoomFactor = pCameraZoomFactor;
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

		public void mouseWindowPositionX(float pX) {
			mMouseWindowCoordsX = pX;
		}

		public void mouseWindowPositionY(float pY) {
			mMouseWindowCoordsY = pY;
		}

		// --------------------------------------
		// Constructor
		// --------------------------------------

		public BackgroundShader(String pVertFilepath, String pFragFilepath) {
			super("", pVertFilepath, pFragFilepath);

		}

		// -------------------------------------
		// Core-Methods
		// --------------------------------------

		@Override
		public void updateUniforms() {
			super.updateUniforms();

			if (mTimeLocationID != -1) {
				GL20.glUniform1f(mTimeLocationID, mTime);
			}

			if (mCameraZoomFactorLocationId != -1) {
				GL20.glUniform1f(mCameraZoomFactorLocationId, mCameraZoomFactor);
			}

			if (mScreenResolutionLocationID != -1) {
				GL20.glUniform2f(mScreenResolutionLocationID, mScreenResolutionW, mScreenResolutionH);
			}

			if (mCameraResolutionLocationID != -1) {
				GL20.glUniform2f(mCameraResolutionLocationID, mCameraResolutionW, mCameraResolutionH);
			}

			if (mv2MouseWindowCoordsLocationID != -1) {
				GL20.glUniform2f(mv2MouseWindowCoordsLocationID, mMouseWindowCoordsX, mMouseWindowCoordsY);
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

			mTimeLocationID = GL20.glGetUniformLocation(shaderID(), "fGlobalTime");
			mCameraZoomFactorLocationId = GL20.glGetUniformLocation(shaderID(), "fCameraZoomFactor");

			mScreenResolutionLocationID = GL20.glGetUniformLocation(shaderID(), "v2ScreenResolution");
			mCameraResolutionLocationID = GL20.glGetUniformLocation(shaderID(), "v2CameraResolution");
			mv2MouseWindowCoordsLocationID = GL20.glGetUniformLocation(shaderID(), "v2MouseWindowCoords");

			int lSampler0 = GL20.glGetUniformLocation(shaderID(), "textureSampler0");
			int lSampler1 = GL20.glGetUniformLocation(shaderID(), "textureSampler1");
			int lSampler2 = GL20.glGetUniformLocation(shaderID(), "textureSampler2");

			GL20.glUniform1i(lSampler0, 0);
			GL20.glUniform1i(lSampler1, 1);
			GL20.glUniform1i(lSampler2, 2);

		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BackgroundShader mBackgroundShader;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ShaderBackground(ScreenManager pScreenManager, String pVertFilepath, String pFragFilepath) {
		super(pScreenManager);

		mBackgroundShader = new BackgroundShader(pVertFilepath, pFragFilepath);

		mShowInBackground = true;
		mInputInBackground = true;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mBackgroundShader.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mBackgroundShader.unloadGLContent();

	}

	@Override
	public void handleInput(LintfordCore pCore, boolean pAcceptMouse, boolean pAcceptKeyboard) {
		super.handleInput(pCore, pAcceptMouse, pAcceptKeyboard);

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_U)) {
			mBackgroundShader.recompile();

		}

	}

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		mBackgroundShader.time((float) pCore.time().totalGameTimeSeconds());

		mBackgroundShader.screenResolutionWidth(pCore.config().display().windowWidth());
		mBackgroundShader.screenResolutionHeight(pCore.config().display().windowHeight());

		final var lCamera = pCore.HUD();
		mBackgroundShader.cameraResolutionWidth(lCamera.getWidth());
		mBackgroundShader.cameraResolutionHeight(lCamera.getHeight());
		mBackgroundShader.cameraZoomFactor(1f); // Hud

		final var lMouseX = lCamera.getMouseWorldSpaceX();
		final var lMouseY = lCamera.getMouseWorldSpaceY();

		mBackgroundShader.mouseWindowPositionX(lMouseX);
		mBackgroundShader.mouseWindowPositionY(lMouseY);

	}

	@Override
	public void draw(LintfordCore pCore) {
		super.draw(pCore);

		final var lTextureBatch = mRendererManager.uiTextureBatch();

		final var lHudBoundingRectangle = pCore.HUD().boundingRectangle();

		final var lX = lHudBoundingRectangle.left();
		final var lY = lHudBoundingRectangle.top();
		final var lWidth = lHudBoundingRectangle.w();
		final var lHeight = lHudBoundingRectangle.h();

		lTextureBatch.begin(pCore.HUD(), mBackgroundShader);
		lTextureBatch.draw(null, 0, 0, 1, 1, lX, lY, lWidth, lHeight, -0.01f, 1, 1, 1, 1);
		lTextureBatch.end();

	}

}
