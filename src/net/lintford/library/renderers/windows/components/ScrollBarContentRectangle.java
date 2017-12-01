package net.lintford.library.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIRectangle;

public class ScrollBarContentRectangle extends UIRectangle {

	// --------------------------------------
	// Contants
	// --------------------------------------

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient IScrollBarArea mParentArea;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBarContentRectangle(IScrollBarArea pParentArea) {
		super();

		mParentArea = pParentArea;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore pCore) {

	}

	public void preDraw(LintfordCore pCore, TextureBatch pTextureBatch) {

		// We need to use a stencil buffer to clip the listbox items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0xFF); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		// Draw into the stencil buffer to mark the 'active' bits
		pTextureBatch.begin(pCore.HUD());

		pTextureBatch.draw(0, 0, 32, 32, mParentArea.windowArea().x, mParentArea.windowArea().y, -8.0f, mParentArea.windowArea().width, mParentArea.windowArea().height, 1.0f, 0.23f, 0.12f, 0.0f, 0.0f, TextureManager.TEXTURE_CORE_UI);

		pTextureBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1

	}

	public void postDraw(LintfordCore pCore) {
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
