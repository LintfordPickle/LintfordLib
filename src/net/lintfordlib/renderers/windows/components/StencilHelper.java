package net.lintfordlib.renderers.windows.components;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.textures.Texture;

public class StencilHelper {

	// --------------------------------------
	// Helper Structures
	// --------------------------------------

	private static class StencilRegion {
		final float x;
		final float y;
		final float w;
		final float h;

		StencilRegion(float x, float y, float w, float h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	// --------------------------------------
	// Static Variables
	// --------------------------------------

	private static int sStencilLevel = 0;
	private static final Deque<StencilRegion> sRegionStack = new ArrayDeque<>();

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private StencilHelper() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public static int currentLevel() {
		return sStencilLevel;
	}

	public static void clear() {
		reset();
	}

	public static void reset() {
		sStencilLevel = 0;
		sRegionStack.clear();
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glClearStencil(0x00);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle) {
		preDraw(core, spriteBatch, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, float depthPadding) {
		preDraw(core, spriteBatch, rectangle.x() + depthPadding, rectangle.y() + depthPadding, rectangle.width() - depthPadding * 2f, rectangle.height() - depthPadding * 2f);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, float depthPadding, int stencilRefValue) {
		preDraw(core, spriteBatch, rectangle.x() + depthPadding, rectangle.y() + depthPadding, rectangle.width() - depthPadding * 2f, rectangle.height() - depthPadding * 2f);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh, float padding, int stencilRefValue) {
		preDraw(core, spriteBatch, rx + padding, ry + padding, rw - padding * 2f, rh - padding * 2f);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh, float padding, int stencilRefValue, int mask) {
		preDraw(core, spriteBatch, rx + padding, ry + padding, rw - padding * 2f, rh - padding * 2f);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh) {
		final var lBatch = (spriteBatch != null) ? spriteBatch : core.sharedResources().uiSpriteBatch();

		if (lBatch.isDrawing()) {
			lBatch.end();
		}

		sRegionStack.push(new StencilRegion(rx, ry, rw, rh));

		final boolean depthTestWasEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColorMask(false, false, false, false);

		GL11.glEnable(GL11.GL_STENCIL_TEST);

		if (sStencilLevel == 0) {
			// Base level: stamp 1 unconditionally into the quad region
			GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		} else {
			// Nested level: increment only pixels where parent level passed
			GL11.glStencilFunc(GL11.GL_EQUAL, sStencilLevel, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
		}

		final Texture whiteTexture = core.resources().textureManager().textureWhite();
		lBatch.begin(core.HUD());
		lBatch.draw(whiteTexture, 0, 0, 2, 2, rx, ry, rw, rh, 0.5f);
		lBatch.end();

		sStencilLevel++;

		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		if (depthTestWasEnabled) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		// Children must pass when stencil == current level
		GL11.glStencilFunc(GL11.GL_EQUAL, sStencilLevel, 0xFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
	}

	public static void postDraw(LintfordCore core) {
		postDraw(core, null);
	}

	public static void postDraw(LintfordCore core, SpriteBatch spriteBatch) {
		final var lBatch = (spriteBatch != null) ? spriteBatch : core.sharedResources().uiSpriteBatch();

		if (sStencilLevel <= 0 || sRegionStack.isEmpty()) {
			sStencilLevel = 0;
			sRegionStack.clear();
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			return;
		}

		if (lBatch.isDrawing()) {
			lBatch.end();
		}

		final var region = sRegionStack.pop();

		final boolean depthTestWasEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColorMask(false, false, false, false);

		if (sStencilLevel == 1) {
			// Reset region back to 0
			GL11.glStencilFunc(GL11.GL_ALWAYS, 0, 0xFF);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		} else {
			// Decrement nested level back down to parent level
			GL11.glStencilFunc(GL11.GL_EQUAL, sStencilLevel, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);
		}

		final Texture whiteTexture = core.resources().textureManager().textureWhite();
		lBatch.begin(core.HUD());
		lBatch.draw(whiteTexture, 0, 0, 2, 2, region.x, region.y, region.w, region.h, 0.5f);
		lBatch.end();

		sStencilLevel--;

		GL11.glColorMask(true, true, true, true);
		GL11.glDepthMask(true);
		if (depthTestWasEnabled) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}

		if (sStencilLevel <= 0) {
			sStencilLevel = 0;
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		} else {
			// Restore parent level stencil test
			GL11.glStencilFunc(GL11.GL_EQUAL, sStencilLevel, 0xFF);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		}
	}
}
