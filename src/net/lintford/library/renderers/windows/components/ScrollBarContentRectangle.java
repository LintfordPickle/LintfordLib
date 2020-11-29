package net.lintford.library.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

public class ScrollBarContentRectangle extends Rectangle {

	private static final long serialVersionUID = -4159498990402852368L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient IScrollBarArea mParentArea;
	private boolean mPreDrawing;

	private float mDepthPadding = 0f;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public float depthPadding() {
		return mDepthPadding;
	}

	public void depthPadding(float pNewValue) {
		mDepthPadding = pNewValue;
	}

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

	public void preDraw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture) {
		preDraw(pCore, pTextureBatch, pUITexture, mParentArea.contentDisplayArea());

	}

	public void preDraw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, Rectangle pRectangle) {
		if (mPreDrawing)
			return;

		mPreDrawing = true;

		// We need to use a stencil buffer to clip the listbox items (which, when scrolling, could appear out-of-bounds of the listbox).
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Set any stencil to 1
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values
		GL11.glStencilMask(0x0F); // Write to stencil buffer

		// Make sure we are starting with a fresh stencil buffer
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		// Draw into the stencil buffer to mark the 'active' bits
		pTextureBatch.begin(pCore.HUD());
		pTextureBatch.draw(pUITexture, 0, 0, 32, 32, pRectangle.x() + mDepthPadding, pRectangle.y() + mDepthPadding, pRectangle.w() - mDepthPadding * 2 - ScrollBar.BAR_WIDTH, pRectangle.h() - mDepthPadding * 2, -10.0f,
				ColorConstants.getBlackWithAlpha(0.f));
		pTextureBatch.end();

		// Start the stencil buffer test to filter out everything outside of the scroll view
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Pass test if stencil value is 1
		GL11.glEnable(GL11.GL_DEPTH_TEST);

	}

	public void postDraw(LintfordCore pCore) {
		if (!mPreDrawing)
			return;

		mPreDrawing = false;
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

	}

}
