package net.lintfordlib.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.textures.Texture;

public class ContentRectangle {

	// --------------------------------------
	// ctor
	// --------------------------------------

	private ContentRectangle() {

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, float depthPadding, int componentUid) {
		preDraw(core, spriteBatch, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), depthPadding, componentUid);
	}

	public static void preDraw(LintfordCore core, SpriteBatch spriteBatch, float rx, float ry, float rw, float rh, float depthPadding, int componentUid) {

		// We need to use a stencil buffer to clip the listbox items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, componentUid, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values

		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 0.f);
		spriteBatch.draw((Texture) null, 0, 0, 1, 1, rx + depthPadding, ry + depthPadding, rw - depthPadding * 2, rh - depthPadding * 2, -10.f);
		spriteBatch.end();

		/*
		 * GL_EQUAL: Passes if ( ref & mask ) = ( stencil & mask ).
		 */

		// Start the stencil buffer test
		GL11.glStencilFunc(GL11.GL_EQUAL, componentUid, 0xFF); // Pass test if stencil value is 1

	}

	public static void postDraw(LintfordCore pCore) {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
