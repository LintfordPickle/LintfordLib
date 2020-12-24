
package net.lintford.library.core.debug;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.FontManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.geometry.TexturedQuad;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.pointbatch.PointBatch;
import net.lintford.library.core.graphics.polybatch.PolyBatch;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
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

	private FontUnit mSystemFont;

	private PointBatch mImmediatePointBatch;
	private LineBatch mImmediateLineBatch;
	private PolyBatch mImmediatePolyBatch;

	private PointBatch mPointBatch;
	private LineBatch mLineBatch;
	private PolyBatch mPolyBatch;

	private TextureBatchPCT mTextureBatch;
	private TexturedQuad mTexturedQuad;
	private ShaderMVP_PT mBasicShader;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugDrawers(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePointBatch = new PointBatch();
		mImmediateLineBatch = new LineBatch();
		mImmediatePolyBatch = new PolyBatch();

		mPointBatch = new PointBatch();
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatch();

		mTextureBatch = new TextureBatchPCT();
		mTexturedQuad = new TexturedQuad();

		mBasicShader = new ShaderMVP_PT("BasicShaderPT", VERT_FILENAME, FRAG_FILENAME) {
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

		mSystemFont = pResourceManager.fontManager().getFont(FontManager.SYSTEM_FONT_NAME);

		mTextureBatch.loadGLContent(pResourceManager);
		mBasicShader.loadGLContent(pResourceManager);
		mTexturedQuad.loadGLContent(pResourceManager);

		mImmediatePointBatch.loadGLContent(pResourceManager);
		mImmediateLineBatch.loadGLContent(pResourceManager);
		mImmediatePolyBatch.loadGLContent(pResourceManager);

		mPointBatch.loadGLContent(pResourceManager);
		mLineBatch.loadGLContent(pResourceManager);
		mPolyBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.unloadGLContent();
		mBasicShader.unloadGLContent();
		mTexturedQuad.unloadGLContent();

		mImmediatePointBatch.unloadGLContent();
		mImmediateLineBatch.unloadGLContent();
		mImmediatePolyBatch.unloadGLContent();

		mPointBatch.unloadGLContent();
		mLineBatch.unloadGLContent();
		mPolyBatch.unloadGLContent();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// 'Immediate' mode renderers

	public void drawPointImmediate(ICamera pCamera, float pX, float pY) {
		drawPointImmediate(pCamera, pX, pY, -0.01f);
	}

	public void drawPointImmediate(ICamera pCamera, float pX, float pY, float pZ) {
		drawPointImmediate(pCamera, pX, pY, pZ, 1f, 1f, 1f, 1f);
	}

	public void drawPointImmediate(ICamera pCamera, float pX, float pY, float pZ, float pR, float pG, float pB, float pA) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePointBatch.begin(pCamera);
		mImmediatePointBatch.draw(pX, pY, pZ, pR, pG, pB, pA);
		mImmediatePointBatch.end();

	}

	public void drawLineImmediate(ICamera pCamera, float pSX, float pSY, float pEX, float pEY) {
		drawLineImmediate(pCamera, pSX, pSY, pEX, pEY, -0.01f);

	}

	public void drawLineImmediate(ICamera pCamera, float pSX, float pSY, float pEX, float pEY, float pZ) {
		drawLineImmediate(pCamera, pSX, pSY, pEX, pEY, pZ, 1f, 1f, 1f);

	}

	public void drawLineImmediate(ICamera pCamera, float pSX, float pSY, float pEX, float pEY, float pZ, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.begin(pCamera);
		mLineBatch.draw(pSX, pSY, pEX, pEY, -0.01f, pR, pG, pB);
		mLineBatch.end();
	}

	public void drawRectImmediate(ICamera pCamera, Rectangle pDstRect) {
		if (pDstRect == null)
			return;
		drawRectImmediate(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height());

	}

	public void drawRectImmediate(ICamera pCamera, float pX, float pY, float pW, float pH) {
		drawRectImmediate(pCamera, pX, pY, pW, pH, 1f, 1f, 1f);
	}

	public void drawRectImmediate(ICamera pCamera, Rectangle pDstRect, float pR, float pG, float pB) {
		drawRectImmediate(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height(), pR, pG, pB);

	}

	public void drawRectImmediate(ICamera pCamera, Rectangle pDstRect, float pLineWidth, float pR, float pG, float pB) {
		drawRectImmediate(pCamera, pDstRect.left(), pDstRect.top(), pDstRect.width(), pDstRect.height(), pLineWidth, pR, pG, pB);

	}

	public void drawRectImmediate(ICamera pCamera, float pX, float pY, float pW, float pH, float pR, float pG, float pB) {
		drawRectImmediate(pCamera, pX, pY, pW, pH, 1f, pR, pG, pB);

	}

	public void drawRectImmediate(ICamera pCamera, float pX, float pY, float pW, float pH, float pLineWidth, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediateLineBatch.lineType(GL11.GL_LINES);
		mImmediateLineBatch.lineWidth(pLineWidth);
		mImmediateLineBatch.begin(pCamera);
		mImmediateLineBatch.drawRect(pX, pY, pW, pH, -.01f, pR, pG, pB);
		mImmediateLineBatch.end();
	}

	public void drawPolyImmediate(ICamera pCamera, Rectangle pRect) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePolyBatch.begin(pCamera);
		mImmediatePolyBatch.drawRect(pRect, -0.1f, 1f, 1f, 1f);
		mImmediatePolyBatch.end();
	}

	public void drawPolyImmediate(ICamera pCamera, List<Vector2f> pVertices, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawPolyImmediate(pCamera, pVertices, 1f, 1f, 1f, pClose);

	}

	public void drawPolyImmediate(ICamera pCamera, List<Vector2f> pVertices, float pR, float pG, float pB, boolean pClose) {
		drawPolyImmediate(pCamera, pVertices, pVertices.size(), pR, pG, pB, pClose);

	}

	public void drawPolyImmediate(ICamera pCamera, List<Vector2f> pVertices, int pAmt, float pR, float pG, float pB, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePolyBatch.begin(pCamera);
		mImmediatePolyBatch.drawRect(pVertices, pAmt, -0.1f, pClose, pR, pG, pB);
		mImmediatePolyBatch.end();

	}

	public void drawCircleImmediate(ICamera pCamera, float pX, float pY, float pRadius) {
		drawCircleImmediate(pCamera, pX, pY, pRadius, 32);

	}

	public void drawCircleImmediate(ICamera pCamera, float pX, float pY, float pRadius, int pSegCount) {
		drawCircleImmediate(pCamera, pX, pY, pRadius, pSegCount, GL11.GL_LINE_STRIP);

	}

	public void drawCircleImmediate(ICamera pCamera, float pX, float pY, float pRadius, int pSegCount, int pGLLineType) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediateLineBatch.lineType(pGLLineType);
		mImmediateLineBatch.begin(pCamera);

		final int lNumSegments = pSegCount / 2;
		for (float i = 0; i < 2 * Math.PI; i += Math.PI / lNumSegments) {

			float xx = pX + (float) (pRadius * Math.cos(i));
			float yy = pY + (float) (pRadius * Math.sin(i));

			mImmediateLineBatch.draw(xx, yy, -0.01f, 1f, 1f, 1f, 1f);

		}

		// Add the first vert again
		mImmediateLineBatch.draw(pX + (float) (pRadius * Math.cos(0)), pY + (float) (pRadius * Math.sin(0)), -0.01f, 1f, 1f, 1f, 1f);

		mImmediateLineBatch.end();

	}

	public void drawRenderTargetImmediate(LintfordCore pCore, float pDestinationPositionX, float pDestinationPositionY, float pDestinationWidth, float pDestinationHeight, float pDestinationZ,
			RenderTarget pRenderTarget) {
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

	// 'Batched' mode renderers

	public void beginTextureRenderer(ICamera pCamera) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.begin(pCamera);
	}

	public void drawTexture(Texture pTexture, float pDX, float pDY, float pDW, float pDH, float pDZ) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawTexture(pTexture, 0, 0, pTexture.getTextureWidth(), pTexture.getTextureHeight(), pDX, pDY, pDW, pDH, pDZ);
	}

	public void drawTexture(Texture pTexture, float pSourceX, float pSourceY, float pSourceWidth, float pSourceHeight, float pDX, float pDY, float pDW, float pDH, float pDZ) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mTextureBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw texture (cached): the TextureBatch has not been started (beginTextureRenderer())");
			return;
		}

		mTextureBatch.draw(pTexture, pSourceX, pSourceY, pSourceWidth, pSourceHeight, pDX, pDY, pDW, pDH, pDZ, ColorConstants.WHITE);

	}

	public void endTextureRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.end();
	}

	public void beginPointRenderer(ICamera pCamera) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.begin(pCamera);

	}

	public void beginLineRenderer(ICamera pCamera) {
		beginLineRenderer(pCamera, GL11.GL_LINE_STRIP);

	}

	public void beginLineRenderer(ICamera pCamera, int pGLLineType) {
		beginLineRenderer(pCamera, pGLLineType, 1f);

	}

	public void beginLineRenderer(ICamera pCamera, int pGLLineType, float pLineWidth) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(pGLLineType);
		mLineBatch.lineWidth(pLineWidth);
		mLineBatch.begin(pCamera);

	}

	public void drawPoint(float pX, float pY) {
		drawPoint(pX, pY, 1f, 1f, 1f, 1f);

	}

	public void drawPoint(float pX, float pY, float pR, float pG, float pB, float pA) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.draw(pX, pY, -0.01f, pR, pG, pB, pA);

	}

	public void drawLine(float pSX, float pSY, float pEX, float pEY) {
		drawLine(pSX, pSY, pEX, pEY, 1f, 1f, 1f);

	}

	public void drawLine(float pSX, float pSY, float pEX, float pEY, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing())
			return;

		mLineBatch.draw(pSX, pSY, pEX, pEY, -0.01f, pR, pG, pB);
	}

	public void drawRect(Rectangle pRect, float pR, float pG, float pB) {
		if (pRect == null)
			return;

		drawRect(pRect.left(), pRect.top(), pRect.width(), pRect.height(), pR, pG, pB);
	}

	public void drawRect(float pX, float pY, float pW, float pH, float pR, float pG, float pB) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.drawRect(pX, pY, pW, pH, -.01f, pR, pG, pB);
	}

	public void drawCircle(float pX, float pY, float pRadius) {
		drawCircle(pX, pY, pRadius, 32);

	}

	public void drawCircle(float pX, float pY, float pRadius, int pSegCount) {
		drawCircle(pX, pY, pRadius, 0.f, pSegCount, GL11.GL_LINE_STRIP);

	}

	public void drawCircle(float pX, float pY, float pRadius, float pInitialAngle, int pSegCount, int pGLLineType) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw circle (cached): the lineRenderer has not been started (beeginLineRenderer())");
			return;
		}

		//		if (mLineBatch.lineType() != pGLLineType) {
		//			Debug.debugManager().logger().w(getClass().getSimpleName(), "Forced flush of LineBatch: different lineType");
		//			mLineBatch.forceFlush();
		//		}
		//
		//		mLineBatch.lineType(pGLLineType);

		final int lNumSegments = pSegCount / 2;
		for (float i = -pInitialAngle; i < 2 * Math.PI - pInitialAngle; i += Math.PI / lNumSegments) {

			float xx = pX + (float) (pRadius * Math.cos(i));
			float yy = pY + (float) (pRadius * Math.sin(i));

			mLineBatch.draw(xx, yy, -0.01f, 1f, 1f, 1f, 1f);

		}

		// Add the first vert again
		mLineBatch.draw(pX + (float) (pRadius * Math.cos(-pInitialAngle)), pY + (float) (pRadius * Math.sin(-pInitialAngle)), -0.01f, 1f, 1f, 1f, 1f);

	}

	public void endLineRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.end();
	}

	public void beginTextRenderer(ICamera pCamera) {
		if (mDebugManager.debugManagerEnabled())
			mSystemFont.begin(pCamera);

	}

	public void drawText(String pText, float pX, float pY) {
		drawText(pText, pX, pY, 1f);

	}

	public void drawText(String pText, float pX, float pY, float pScale) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mSystemFont.mFontSpriteBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw text (cached): the FontRenderer has not been started (have you called beginTextRenderer())");
			return;
		}

		mSystemFont.draw(pText, pX, pY, -0.01f, ColorConstants.WHITE, pScale, -1);

	}

	public void endTextRenderer() {
		if (mDebugManager.debugManagerEnabled())
			mSystemFont.end();

	}

	public void endPointRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.end();

	}

	public void beginPolyRenderer(ICamera pCamera) {
		beginPolyRenderer(pCamera, GL11.GL_LINES);

	}

	public void beginPolyRenderer(ICamera pCamera, int pDrawMode) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPolyBatch.lineMode(pDrawMode);
		mPolyBatch.begin(pCamera);

	}

	public void drawPoly(Rectangle pRect) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		drawPoly(pRect.getVertices(), 1f, 1f, 1f, true);
	}

	public void drawPoly(List<Vector2f> pVertices, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		drawPoly(pVertices, 1f, 1f, 1f, pClose);

	}

	public void drawPoly(List<Vector2f> pVertices, float pR, float pG, float pB, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		drawPoly(pVertices, pVertices.size(), pR, pG, pB, pClose);

	}

	public void drawPoly(List<Vector2f> pVertices, int pAmt, float pR, float pG, float pB, boolean pClose) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		mPolyBatch.drawRect(pVertices, pAmt, -0.1f, pClose, pR, pG, pB);

	}

	public void endPolyRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			return;
		}

		mPolyBatch.end();

	}

}
