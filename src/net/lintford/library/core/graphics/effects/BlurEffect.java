package net.lintford.library.core.graphics.effects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.BlurShader;
import net.lintford.library.core.graphics.shaders.Shader;
import net.lintford.library.core.maths.Matrix4f;

public class BlurEffect {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String BLUR_EFFECT_VERT_SHADER = Shader.BASIC_VERT_FILENAME;
	private static final String BLUR_EFFECT_FRAG_SHADER = "res//shaders//blur.frag";

	public enum BLUR_DIRECTION {
		horizontal, vertical,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private BlurShader mBlurShader;
	private FullScreenTexturedQuad mFullScreenQuad;
	private float mRadius;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float radius() {
		return mRadius;
	}

	public void radius(float pNewRadius) {
		mRadius = pNewRadius;
	}

	public Shader shader() {
		return mBlurShader;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public BlurEffect() {
		mRadius = 3;

		mFullScreenQuad = new FullScreenTexturedQuad();
		mBlurShader = new BlurShader(BLUR_EFFECT_VERT_SHADER, BLUR_EFFECT_FRAG_SHADER);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mFullScreenQuad.loadGLContent(pResourceManager);
		mBlurShader.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		mFullScreenQuad.unloadGLContent();
		mBlurShader.unloadGLContent();
	}

	public void render(LintfordCore pCore, RenderTarget pTarget) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pTarget.colorTextureID());
		
		render(pCore, pTarget, BLUR_DIRECTION.horizontal);
		render(pCore, pTarget, BLUR_DIRECTION.vertical);
		
	}

	private void render(LintfordCore pCore, RenderTarget pTarget, BLUR_DIRECTION pDir) {
		final int lWindowWidth = pCore.config().display().windowSize().x / 2;
		
		mBlurShader.resolution(lWindowWidth);
		mBlurShader.radius(mRadius);
		mBlurShader.direction().x = pDir == BLUR_DIRECTION.horizontal ? 1f : 0f;
		mBlurShader.direction().y = pDir == BLUR_DIRECTION.horizontal ? 0f : 1f;

		mBlurShader.projectionMatrix(pCore.HUD().projection());
		mBlurShader.viewMatrix(Matrix4f.IDENTITY);
		mFullScreenQuad.createModelMatrix();
		mBlurShader.modelMatrix(mFullScreenQuad.modelMatrix());

		mBlurShader.bind();
		mFullScreenQuad.draw(pCore);
		mBlurShader.unbind();

	}

}
