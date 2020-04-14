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
		private int mResolutionLocationID;

		private float mTime;
		private float mResolutionW, mResolutionH;

		// --------------------------------------
		// Properties
		// --------------------------------------

		public void time(float pTime) {
			mTime = pTime;
		}

		public void resolutionWidth(float pWidth) {
			mResolutionW = pWidth;
		}

		public void resolutionHeight(float pHeight) {
			mResolutionH = pHeight;
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

		public void update() {
			super.update();

			if (mTimeLocationID != -1) {
				GL20.glUniform1f(mTimeLocationID, mTime);
			}

			if (mResolutionLocationID != -1) {
				GL20.glUniform2f(mResolutionLocationID, mResolutionW, mResolutionH);
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
			mResolutionLocationID = GL20.glGetUniformLocation(shaderID(), "v2Resolution");

			// Bind the sampler locations
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

		mBackgroundShader.resolutionWidth(pCore.config().display().windowWidth());
		mBackgroundShader.resolutionHeight(pCore.config().display().windowHeight());

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

	// --------------------------------------
	// Methods
	// --------------------------------------

}
