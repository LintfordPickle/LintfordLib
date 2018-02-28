package net.lintford.library.core.debug;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.geometry.TexturedQuad;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.polybatch.PolyBatch;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.maths.Vector2f;

public class DebugDrawers {

	// --------------------------------------
	// Constants
	// --------------------------------------

	protected static final String VERT_FILENAME = "/res/shaders/shader_basic_pt.vert";
	protected static final String FRAG_FILENAME = "/res/shaders/shader_basic_pt.frag";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private LineBatch mLineBatch;
	private PolyBatch mPolyBatch;
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
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatch();
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
		mLineBatch.loadGLContent(pResourceManager);
		mPolyBatch.loadGLContent(pResourceManager);
	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();
		mBasicShader.unloadGLContent();
		mTexturedQuad.unloadGLContent();
		mLineBatch.unloadGLContent();
		mPolyBatch.unloadGLContent();
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

	public void drawRect(ICamera pCamera, Rectangle pDstRect) {
		drawRect(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height());
	}

	public void drawRect(ICamera pCamera, Rectangle pDstRect, float pR, float pG, float pB) {
		drawRect(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height(), pR, pG, pB);
	}

	public void drawRect(ICamera pCamera, float pX, float pY, float pW, float pH) {
		drawRect(pCamera, pX, pY, pW, pH, 1f, 1f, 1f);
	}

	public void drawRect(ICamera pCamera, float pX, float pY, float pW, float pH, float pR, float pG, float pB) {
		mLineBatch.begin(pCamera);
		mLineBatch.drawRect(pX, pY, pW, pH, -0.1f, pR, pG, pB);
		mLineBatch.end();
	}
	
	public void drawPoly(ICamera pCamera, Rectangle pRect) {
		mPolyBatch.begin(pCamera);
		mPolyBatch.drawRect(pRect, -0.1f, 1f, 1f, 1f);
		mPolyBatch.end();
	}
	
	public void drawPoly(ICamera pCamera, Vector2f[] pVertices, boolean pClose) {
		mPolyBatch.begin(pCamera);
		mPolyBatch.drawRect(pVertices, -0.1f, pClose, 1f, 1f, 1f);
		mPolyBatch.end();
	}

	public void drawTexture(float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, Texture pTexture) {
		drawTexture(0, 0, pTexture.getTextureWidth(), pTexture.getTextureHeight(), pDestinationPositionX, pDestinationPositionY, pDestinationWidth, pDestinationHeight, pDestinationZ, pTexture);
	}

	public void drawTexture(float pSourceX, float pSourceY, float pSourceWidth, float pSourceHeight, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, Texture pTexture) {
		mTextureBatch.draw(pSourceX, pSourceY, pSourceWidth, pSourceHeight, pDestinationPositionX, pDestinationPositionY, pDestinationZ, pDestinationWidth, pDestinationHeight, 1f, pTexture);
	}

	public void drawRenderTarget(LintfordCore pCore, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, RenderTarget pRenderTarget) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0); // add scene texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pRenderTarget.colorTextureID());

		mBasicShader.projectionMatrix(pCore.HUD().projection());
		mBasicShader.viewMatrix(pCore.HUD().view());

		mTexturedQuad.createModelMatrix(pDestinationPositionX, pDestinationPositionY, 200, 200, -1f);
		mBasicShader.modelMatrix(mTexturedQuad.modelMatrix());

		mBasicShader.bind();
		mTexturedQuad.draw(pCore);
		mBasicShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0); //
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void endTextureRenderer() {
		mTextureBatch.end();
	}

}
