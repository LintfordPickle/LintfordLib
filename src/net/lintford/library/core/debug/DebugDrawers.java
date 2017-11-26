package net.lintford.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.geometry.TexturedQuad;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.rendering.RenderState;

public class DebugDrawers {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final String VERT_FILENAME = "//res//shaders//shader_basic_pt.vert";
	protected static final String FRAG_FILENAME = "//res//shaders//shader_basic_pt.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatch mTextureBatch;
	private TexturedQuad mTexturedQuad;
	private ShaderMVP_PT mBasicShader;

	// --------------------------------------
	// Properties
	// --------------------------------------

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugDrawers() {
		mTextureBatch = new TextureBatch();

		mTexturedQuad = new TexturedQuad();

		mBasicShader = new ShaderMVP_PT(VERT_FILENAME, FRAG_FILENAME) {
			@Override
			protected void bindAtrributeLocations(int pShaderID) {
				GL20.glBindAttribLocation(pShaderID, 0, "inPosition");
				GL20.glBindAttribLocation(pShaderID, 1, "inTexCoord");
			}
		};

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		mTextureBatch.loadGLContent(pResourceManager);
		mBasicShader.loadGLContent(pResourceManager);
		mTexturedQuad.loadGLContent(pResourceManager);
	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();
		mBasicShader.unloadGLContent();
		mTexturedQuad.unloadGLContent();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void startTextureRenderer(ICamera pCamera) {
		mTextureBatch.begin(pCamera);
	}

	public void drawLine(float pSX, float pSY, float pEX, float pEY) {
		drawTexture(0, 0, 16, 16, pSX, pSY, pEX - pSX, pEY - pSY, -0.01f, TextureManager.TEXTURE_CORE_UI);
	}

	public void drawTexture(float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, Texture pTexture) {
		drawTexture(0, 0, pTexture.getTextureWidth(), pTexture.getTextureHeight(), pDestinationPositionX, pDestinationPositionY, pDestinationWidth, pDestinationHeight, pDestinationZ, pTexture);
	}

	public void drawTexture(float pSourceX, float pSourceY, float pSourceWidth, float pSourceHeight, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, Texture pTexture) {
		mTextureBatch.draw(pSourceX, pSourceY, pSourceWidth, pSourceHeight, pDestinationPositionX, pDestinationPositionY, pDestinationZ, pDestinationWidth, pDestinationHeight, 1f, pTexture);
	}

	public void drawRenderTarget(RenderState pRenderState, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, RenderTarget pRenderTarget) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0); // add scene texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pRenderTarget.colorTextureID());

		mBasicShader.projectionMatrix(pRenderState.HUDCamera().projection());
		mBasicShader.viewMatrix(pRenderState.HUDCamera().view());

		mTexturedQuad.createModelMatrix(pDestinationPositionX, pDestinationPositionY, 200, 200, -1f);
		mBasicShader.modelMatrix(mTexturedQuad.modelMatrix());

		mBasicShader.bind();
		mTexturedQuad.draw(pRenderState);
		mBasicShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0); //
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void endTextureRenderer() {
		mTextureBatch.end();
	}

}
