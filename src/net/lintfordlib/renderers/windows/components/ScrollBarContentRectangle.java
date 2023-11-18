package net.lintfordlib.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.renderers.windows.components.interfaces.IScrollBarArea;

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

	public void depthPadding(float newDepthPadding) {
		mDepthPadding = newDepthPadding;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public ScrollBarContentRectangle(IScrollBarArea parentArea) {
		super();

		mParentArea = parentArea;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {

	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch) {
		preDraw(core, spriteBatch, mParentArea.contentDisplayArea(), 1);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilValue) {
		if (mPreDrawing)
			return;

		mPreDrawing = true;

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, stencilValue, 0xFFFFFFFF); // Set any stencil to stencilValue
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values

		// GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		spriteBatch.begin(core.HUD());
		spriteBatch.draw(null, 0, 0, 1, 1, rectangle.x() + mDepthPadding, rectangle.y() + mDepthPadding, rectangle.width() - mDepthPadding * 2 - ScrollBar.BAR_WIDTH, rectangle.height() - mDepthPadding * 2, -10.f, ColorConstants.getWhiteWithAlpha(0.f));
		spriteBatch.end();

		/*
		 * GL_EQUAL: Passes if ( ref & mask ) = ( stencil & mask ).
		 */

		GL11.glStencilFunc(GL11.GL_EQUAL, stencilValue, 0xFFFFFFFF); // Pass test if stencil value is stencilValue
		GL11.glEnable(GL11.GL_DEPTH_TEST);

	}

	public void postDraw(LintfordCore core) {
		if (!mPreDrawing)
			return;

		mPreDrawing = false;
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
}
