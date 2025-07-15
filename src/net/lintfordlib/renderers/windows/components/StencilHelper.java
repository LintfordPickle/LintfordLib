package net.lintfordlib.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.camera.Camera;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.textures.Texture;

public class StencilHelper {

	// --------------------------------------
	// ctor
	// --------------------------------------

	private StencilHelper() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public static void clear() {
		GL11.glClearStencil(0x00);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, float depthPadding, int stencilRefValue) {
		preDraw(core, spriteBatch, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), depthPadding, stencilRefValue, 0xffffffff);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh, float padding, int stencilRefValue) {
		preDraw(core, spriteBatch, rx, ry, rw, rh, padding, stencilRefValue, 0xffffffff);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh, float padding, int stencilRefValue, int mask) {
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		// GL_GEQUAL: Passes if ( ref & mask ) >= ( stencil & mask ).
		GL11.glStencilFunc(GL11.GL_GEQUAL, stencilRefValue, mask);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

		// Clear the stencil buffer

		// Draw the bounds of out stencil region
		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 0.f);
		spriteBatch.draw((Texture) null, 0, 0, 1, 1, rx + padding, ry + padding, rw - padding * 2, rh - padding * 2, Camera.Z_FAR);
		spriteBatch.end();

		// GL_EQUAL: Passes if ( ref & mask ) = ( stencil & mask ).
		GL11.glStencilFunc(GL11.GL_EQUAL, stencilRefValue, mask); // Pass test if stencil value our ref

	}

	public static void postDraw(LintfordCore pCore) {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
