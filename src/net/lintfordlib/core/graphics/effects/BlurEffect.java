package net.lintfordlib.core.graphics.effects;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintfordlib.core.graphics.rendertarget.RenderTarget;
import net.lintfordlib.core.graphics.shaders.BlurShader;
import net.lintfordlib.core.graphics.shaders.Shader;
import net.lintfordlib.core.maths.Matrix4f;

public class BlurEffect {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final String BLUR_EFFECT_VERT_SHADER = "/res/shaders/shader_basic_pt.vert";
	private static final String BLUR_EFFECT_FRAG_SHADER = "/res/shaders/shaderBlur.frag";

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

	public void radius(float newRadius) {
		mRadius = newRadius;
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
		mBlurShader = new BlurShader(BLUR_EFFECT_VERT_SHADER, BLUR_EFFECT_FRAG_SHADER) {
			protected void getUniformLocations() {
				super.getUniformLocations();

				int lBackgroundSamplerLocation = GL20.glGetUniformLocation(shaderID(), "sceneSampler");

				GL20.glUniform1i(lBackgroundSamplerLocation, 0);
			};
		};
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mFullScreenQuad.loadResources(resourceManager);
		mBlurShader.loadResources(resourceManager);
	}

	public void unloadResources() {
		mFullScreenQuad.unloadResources();
		mBlurShader.unloadResources();
	}

	public void render(LintfordCore core, RenderTarget renderTarget) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderTarget.colorTextureID());

		renderTarget.bind();

		render(core, renderTarget, BLUR_DIRECTION.horizontal);
		render(core, renderTarget, BLUR_DIRECTION.vertical);

		renderTarget.unbind();
	}

	private void render(LintfordCore core, RenderTarget renderTarget, BLUR_DIRECTION blurDirecetion) {
		final int lWindowWidth = core.config().display().windowWidth() / 2;

		mBlurShader.resolution(lWindowWidth);
		mBlurShader.radius(mRadius);
		mBlurShader.direction().x = blurDirecetion == BLUR_DIRECTION.horizontal ? 1f : 0f;
		mBlurShader.direction().y = blurDirecetion == BLUR_DIRECTION.horizontal ? 0f : 1f;

		mBlurShader.projectionMatrix(core.gameCamera().projection());
		mBlurShader.viewMatrix(Matrix4f.IDENTITY);

		mFullScreenQuad.zDepth(-1f);
		mFullScreenQuad.createModelMatrix();
		mBlurShader.modelMatrix(mFullScreenQuad.modelMatrix());

		mBlurShader.bind();
		mFullScreenQuad.draw(core);
		mBlurShader.unbind();
	}
}
