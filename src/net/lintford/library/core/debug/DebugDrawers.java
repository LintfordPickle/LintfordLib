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

	private final Debug mDebugManager;

	private LineBatch mLineBatch;
	private PolyBatch mPolyBatch;
	private TextureBatch mTextureBatch;
	private TexturedQuad mTexturedQuad;
	private ShaderMVP_PT mBasicShader;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugDrawers(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

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
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.loadGLContent(pResourceManager);
		mBasicShader.loadGLContent(pResourceManager);
		mTexturedQuad.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);
		mPolyBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

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
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.begin(pCamera);
	}

	public void drawRect(ICamera pCamera, Rectangle pDstRect) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawRect(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height());
	}

	public void drawRect(ICamera pCamera, Rectangle pDstRect, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawRect(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height(), pR, pG, pB);
	}

	public void drawRect(ICamera pCamera, float pX, float pY, float pW, float pH) {
		drawRect(pCamera, pX, pY, pW, pH, 1f, 1f, 1f);
	}

	public void drawRect(ICamera pCamera, float pX, float pY, float pW, float pH, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.begin(pCamera);
		mLineBatch.drawRect(pX, pY, pW, pH, -.01f, pR, pG, pB);
		mLineBatch.end();
	}

	public void drawPoly(ICamera pCamera, Rectangle pRect) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPolyBatch.begin(pCamera);
		mPolyBatch.drawRect(pRect, -0.1f, 1f, 1f, 1f);
		mPolyBatch.end();
	}

	public void drawPoly(ICamera pCamera, Vector2f[] pVertices, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPolyBatch.begin(pCamera);
		mPolyBatch.drawRect(pVertices, -0.1f, pClose, 1f, 1f, 1f);
		mPolyBatch.end();
	}

	public void drawCircle(ICamera pCamera, float pX, float pY, float pRadius) {
		drawCircle(pCamera, pX, pY, pRadius, 50);

	}

	public void drawCircle(ICamera pCamera, float pX, float pY, float pRadius, int pSegCount) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.setLineType(GL11.GL_LINE_STRIP);
		mLineBatch.begin(pCamera);

		final int lNumSegments = pSegCount / 2;
		for (float i = 0; i < 2 * Math.PI; i += Math.PI / lNumSegments) {

			float xx = pX + (float) (pRadius * Math.cos(i));
			float yy = pY + (float) (pRadius * Math.sin(i));

			mLineBatch.draw(xx, yy, -0.01f, 1f, 1f, 1f);

		}

		// Add the first vert again
		mLineBatch.draw(pX + (float) (pRadius * Math.cos(0)), pY + (float) (pRadius * Math.sin(0)), -0.01f, 1f, 1f, 1f);

		mLineBatch.end();
	}

	public void drawTexture(Texture pTexture, float pDX, float pDY, float pDW, float pDH, float pDZ) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawTexture(pTexture, 0, 0, pTexture.getTextureWidth(), pTexture.getTextureHeight(), pDX, pDY, pDW, pDH, pDZ);
	}

	public void drawTexture(Texture pTexture, float pSourceX, float pSourceY, float pSourceWidth, float pSourceHeight, float pDX, float pDY, float pDW, float pDH, float pDZ) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.draw(pTexture, pSourceX, pSourceY, pSourceWidth, pSourceHeight, pDX, pDY, pDW, pDH, pDZ, 1f, 1f, 1f, 1f);
	}

	public void drawRenderTarget(LintfordCore pCore, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ, RenderTarget pRenderTarget) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (pRenderTarget == null)
			return;

		GL13.glActiveTexture(GL13.GL_TEXTURE0); // add scene texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pRenderTarget.colorTextureID());

		mBasicShader.projectionMatrix(pCore.HUD().projection());
		mBasicShader.viewMatrix(pCore.HUD().view());

		mTexturedQuad.createModelMatrix(pDestinationPositionX, pDestinationPositionY, pDestinationWidth, pDestinationHeight, -1f);
		mBasicShader.modelMatrix(mTexturedQuad.modelMatrix());

		mBasicShader.bind();
		mTexturedQuad.draw(pCore);
		mBasicShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0); //
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void endTextureRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.end();
	}

	public void startLineRenderer(ICamera pCamera) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.begin(pCamera);
	}

	public void drawLine(float pSX, float pSY, float pEX, float pEY) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing())
			return;

		mLineBatch.draw(pSX, pSY, pEX, pEY, -0.01f, 1f, 1f, 1f);
	}

	public void drawLine(float pSX, float pSY, float pEX, float pEY, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing())
			return;

		mLineBatch.draw(pSX, pSY, pEX, pEY, -0.01f, pR, pG, pB);
	}

	public void endLineRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.end();
	}

}
