
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
import net.lintford.library.core.graphics.batching.TextureBatchPCT;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.geometry.TexturedQuad;
import net.lintford.library.core.graphics.linebatch.LineBatch;
import net.lintford.library.core.graphics.pointbatch.PointBatch;
import net.lintford.library.core.graphics.polybatch.PolyBatchPC;
import net.lintford.library.core.graphics.rendertarget.RenderTarget;
import net.lintford.library.core.graphics.shaders.ShaderMVP_PT;
import net.lintford.library.core.graphics.textures.Texture;
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
	private FontUnit mSystemFontUnit;
	private PointBatch mImmediatePointBatch;
	private LineBatch mImmediateLineBatch;
	private PolyBatchPC mImmediatePolyBatch;
	private PointBatch mPointBatch;
	private LineBatch mLineBatch;
	private PolyBatchPC mPolyBatch;
	private TextureBatchPCT mTextureBatch;
	private TexturedQuad mTexturedQuad;
	private ShaderMVP_PT mBasicShader;

	public LineBatch immediateLineBatch() {
		return mImmediateLineBatch;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugDrawers(final Debug debugManager) {
		mDebugManager = debugManager;

		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePointBatch = new PointBatch();
		mImmediateLineBatch = new LineBatch();
		mImmediatePolyBatch = new PolyBatchPC();

		mPointBatch = new PointBatch();
		mLineBatch = new LineBatch();
		mPolyBatch = new PolyBatchPC();

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

	public void loadResources(ResourceManager resourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mSystemFontUnit = resourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CORE_TEXT_NAME);

		mTextureBatch.loadResources(resourceManager);
		mBasicShader.loadResources(resourceManager);
		mTexturedQuad.loadResources(resourceManager);

		mImmediatePointBatch.loadResources(resourceManager);
		mImmediateLineBatch.loadResources(resourceManager);
		mImmediatePolyBatch.loadResources(resourceManager);

		mPointBatch.loadResources(resourceManager);
		mLineBatch.loadResources(resourceManager);
		mPolyBatch.loadResources(resourceManager);
	}

	public void unloadResources() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.unloadResources();
		mBasicShader.unloadResources();
		mTexturedQuad.unloadResources();

		mImmediatePointBatch.unloadResources();
		mImmediateLineBatch.unloadResources();
		mImmediatePolyBatch.unloadResources();

		mPointBatch.unloadResources();
		mLineBatch.unloadResources();
		mPolyBatch.unloadResources();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// 'Immediate' mode renderers

	public void drawPointImmediate(ICamera camera, float x, float y) {
		drawPointImmediate(camera, x, y, -0.01f);
	}

	public void drawPointImmediate(ICamera camera, float x, float y, float z) {
		drawPointImmediate(camera, x, y, z, 1f, 1f, 1f, 1f);
	}

	public void drawPointImmediate(ICamera camera, float x, float y, float z, float red, float green, float blue, float alpha) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePointBatch.begin(camera);
		mImmediatePointBatch.draw(x, y, z, red, green, blue, alpha);
		mImmediatePointBatch.end();
	}

	public void drawLineImmediate(ICamera camera, float startX, float startY, float endX, float endY) {
		drawLineImmediate(camera, startX, startY, endX, endY, -0.01f);
	}

	public void drawLineImmediate(ICamera camera, float startX, float startY, float endX, float endY, float z) {
		drawLineImmediate(camera, startX, startY, endX, endY, z, 1f, 1f, 1f);
	}

	public void drawLineImmediate(ICamera camera, float startX, float startY, float endX, float endY, float z, float red, float green, float blue) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.begin(camera);
		mLineBatch.draw(startX, startY, endX, endY, -0.01f, red, green, blue);
		mLineBatch.end();
	}

	public void drawRectImmediate(ICamera camera, Rectangle destRectangle) {
		if (destRectangle == null)
			return;

		drawRectImmediate(camera, destRectangle.left(), destRectangle.top(), destRectangle.width(), destRectangle.height());
	}

	public void drawRectImmediate(ICamera camera, float x, float y, float width, float height) {
		drawRectImmediate(camera, x, y, width, height, 1f, 1f, 1f);
	}

	public void drawRectImmediate(ICamera camera, Rectangle destRectangle, float red, float green, float blue) {
		drawRectImmediate(camera, destRectangle.left(), destRectangle.top(), destRectangle.width(), destRectangle.height(), red, green, blue);
	}

	public void drawRectImmediate(ICamera camera, Rectangle destRectangle, float lineWidth, float red, float green, float blue) {
		drawRectImmediate(camera, destRectangle.left(), destRectangle.top(), destRectangle.width(), destRectangle.height(), lineWidth, red, green, blue);
	}

	public void drawRectImmediate(ICamera camera, float x, float y, float width, float height, float red, float green, float blue) {
		drawRectImmediate(camera, x, y, width, height, 1f, red, green, blue);
	}

	public void drawRectImmediate(ICamera camera, float x, float y, float width, float height, float lineWidth, float red, float green, float blue) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediateLineBatch.lineType(GL11.GL_LINES);
		mImmediateLineBatch.lineWidth(lineWidth);
		mImmediateLineBatch.begin(camera);
		mImmediateLineBatch.drawRect(x, y, width, height, -.01f, red, green, blue);
		mImmediateLineBatch.end();
	}

	public void drawPolyImmediate(ICamera camera, Rectangle rectangle) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediatePolyBatch.begin(camera);
		// mImmediatePolyBatch.drawRect(rectangle, -.1f, true, 1.f, 1.f, 1.f);
		mImmediatePolyBatch.end();
	}

	public void drawCircleImmediate(ICamera camera, float x, float y, float radius) {
		drawCircleImmediate(camera, x, y, radius, 32);
	}

	public void drawCircleImmediate(ICamera camera, float x, float y, float radius, int segCount) {
		drawCircleImmediate(camera, x, y, radius, segCount, GL11.GL_LINE_STRIP);
	}

	public void drawCircleImmediate(ICamera camera, float x, float y, float radius, int segCount, int glLineType) {
		drawCircleImmediate(camera, x, y, radius, segCount, glLineType, 1.0f);
	}

	public void drawCircleImmediate(ICamera camera, float x, float y, float radius, int segCount, int glLineType, float glLineWidth) {
		drawCircleImmediate(camera, x, y, radius, segCount, glLineType, glLineWidth, 1.f, 1.f, 1.f);
	}

	public void drawCircleImmediate(ICamera camera, float x, float y, float radius, int segCount, int glLineType, float glLineWidth, float r, float g, float b) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mImmediateLineBatch.lineWidth(glLineWidth);
		mImmediateLineBatch.lineType(glLineType);
		mImmediateLineBatch.begin(camera);

		final int lNumSegments = segCount / 2;
		for (float i = 0; i < 2 * Math.PI; i += Math.PI / lNumSegments) {

			float xx = x + (float) (radius * Math.cos(i));
			float yy = y + (float) (radius * Math.sin(i));

			mImmediateLineBatch.draw(xx, yy, -0.01f, r, g, b, 1f);
		}

		mImmediateLineBatch.draw(x + (float) (radius * Math.cos(0)), y + (float) (radius * Math.sin(0)), -0.01f, 1f, 1f, 1f, 1f);
		mImmediateLineBatch.end();
	}

	public void drawRenderTargetImmediate(LintfordCore core, Rectangle dest, float zDepth, RenderTarget renderTarget) {
		if (dest == null)
			return;

		drawRenderTargetImmediate(core, dest.x(), dest.y(), dest.width(), dest.height(), zDepth, renderTarget);
	}

	public void drawRenderTargetImmediate(LintfordCore core, float destinationX, float destinationY, float destinationWidth, float destinationHeight, float zDepth, RenderTarget renderTarget) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (renderTarget == null)
			return;

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, renderTarget.colorTextureID());

		mBasicShader.projectionMatrix(core.HUD().projection());
		mBasicShader.viewMatrix(core.HUD().view());

		mTexturedQuad.createModelMatrix(destinationX, destinationY, zDepth, destinationWidth, destinationHeight);
		mBasicShader.modelMatrix(mTexturedQuad.modelMatrix());

		mBasicShader.bind();
		mTexturedQuad.draw(core);
		mBasicShader.unbind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	// 'Batched' mode renderers

	public void beginTextureRenderer(ICamera camera) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.begin(camera);
	}

	public void drawTexture(Texture texture, float destX, float destY, float destW, float destH, float zDepth) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		drawTexture(texture, 0, 0, texture.getTextureWidth(), texture.getTextureHeight(), destX, destY, destW, destH, zDepth);
	}

	public void drawTexture(Texture texture, float sourceX, float sourceY, float sourceWidth, float sourceHeight, float destX, float destY, float destW, float destH, float zDepth) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mTextureBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw texture (cached): the TextureBatch has not been started (beginTextureRenderer())");
			return;
		}

		mTextureBatch.draw(texture, sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, destW, destH, zDepth, ColorConstants.WHITE);
	}

	public void endTextureRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mTextureBatch.end();
	}

	public void beginPointRenderer(ICamera camera) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.begin(camera);
	}

	public void beginLineRenderer(ICamera camera) {
		beginLineRenderer(camera, GL11.GL_LINE_STRIP);

	}

	public void beginLineRenderer(ICamera camera, int glLineType) {
		beginLineRenderer(camera, glLineType, 1f);
	}

	public void beginLineRenderer(ICamera camera, int glLineType, float lineWidth) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(glLineType);
		mLineBatch.lineWidth(lineWidth);
		mLineBatch.begin(camera);
	}

	public void drawPoint(float x, float y) {
		drawPoint(x, y, 1f, 1f, 1f, 1f);
	}

	public void drawPoint(float x, float y, float red, float green, float blue, float alpha) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.draw(x, y, -0.01f, red, green, blue, alpha);
	}

	public void drawLine(float startX, float startY, float endX, float endY) {
		drawLine(startX, startY, endX, endY, 1f, 1f, 1f);
	}

	public void drawLine(float startX, float startY, float endX, float endY, float red, float green, float blue) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing())
			return;

		mLineBatch.draw(startX, startY, endX, endY, -0.01f, red, green, blue);
	}

	public void drawRect(Rectangle rectangle, float red, float green, float blue) {
		if (rectangle == null)
			return;

		drawRect(rectangle.left(), rectangle.top(), rectangle.width(), rectangle.height(), red, green, blue);
	}

	public void drawRect(float x, float y, float width, float height, float red, float green, float blue) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.lineType(GL11.GL_LINES);
		mLineBatch.drawRect(x, y, width, height, -.01f, red, green, blue);
	}

	public void drawCircle(float x, float y, float radius) {
		drawCircle(x, y, radius, 32);
	}

	public void drawCircle(float x, float y, float radius, int segCount) {
		drawCircle(x, y, radius, 0.f, segCount, GL11.GL_LINE_STRIP);
	}

	public void drawCircle(float x, float y, float radius, float initialAngle, int segCount, int glLineType) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mLineBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw circle (cached): the lineRenderer has not been started (beeginLineRenderer())");
			return;
		}

		final int lNumSegments = segCount / 2;
		for (float i = -initialAngle; i < 2 * Math.PI - initialAngle; i += Math.PI / lNumSegments) {

			float xx = x + (float) (radius * Math.cos(i));
			float yy = y + (float) (radius * Math.sin(i));

			mLineBatch.draw(xx, yy, -0.01f, 1f, 1f, 1f, 1f);
		}

		mLineBatch.draw(x + (float) (radius * Math.cos(-initialAngle)), y + (float) (radius * Math.sin(-initialAngle)), -0.01f, 1f, 1f, 1f, 1f);
	}

	public void endLineRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mLineBatch.end();
	}

	public void beginTextRenderer(ICamera camera) {
		if (mDebugManager.debugManagerEnabled())
			mSystemFontUnit.begin(camera);
	}

	public void drawText(String text, float x, float y) {
		drawText(text, x, y, 1.f);
	}

	public void drawText(String text, float x, float y, float scale) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mSystemFontUnit.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw text (cached): the FontRenderer has not been started (have you called beginTextRenderer())");
			return;
		}

		mSystemFontUnit.drawText(text, x, y, -.01f, ColorConstants.WHITE, scale);
	}

	public void endTextRenderer() {
		if (mDebugManager.debugManagerEnabled())
			mSystemFontUnit.end();
	}

	public void endPointRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mPointBatch.end();
	}

	public void beginPolyRenderer(ICamera camera) {
		mPolyBatch.begin(camera);
	}

	public void drawPoly(List<Vector2f> vertices, boolean closePolygon) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		drawPoly(vertices, 1.f, 1.f, 1.f, closePolygon);
	}

	public void drawPoly(List<Vector2f> vertices, float red, float green, float blue, boolean closePolygon) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		drawPoly(vertices, vertices.size(), red, green, blue, closePolygon);
	}

	public void drawPoly(List<Vector2f> vertices, int numberVerts, float red, float green, float blue, boolean closePolygon) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing()) {
			Debug.debugManager().logger().w(getClass().getSimpleName(), "Cannot draw poly (cached): the PolyRenderer has not been started (must call beginPolyRenderer() first)");
			return;
		}

		mPolyBatch.drawVertices(vertices, numberVerts, -.1f, closePolygon, red, green, blue);
	}

	public void endPolyRenderer() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (!mPolyBatch.isDrawing())
			return;

		mPolyBatch.end();
	}
}
