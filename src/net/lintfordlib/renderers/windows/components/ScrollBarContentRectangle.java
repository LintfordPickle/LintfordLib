package net.lintfordlib.renderers.windows.components;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.textures.Texture;
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

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch) {
		preDraw(core, spriteBatch, mParentArea.contentDisplayArea(), 1);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilValue) {
		preDraw(core, spriteBatch, rectangle, 1, false);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilValue, boolean scrollBarEnabled) {
		if (mPreDrawing)
			return;

		mPreDrawing = true;

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);

		GL11.glStencilFunc(GL11.GL_ALWAYS, stencilValue, 0xFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values

		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 0.f);
		final var lScrolbarWidth = scrollBarEnabled ? ScrollBar.BAR_WIDTH : 0.f;
		spriteBatch.draw((Texture) null, 0, 0, 1, 1, rectangle.x() + mDepthPadding, rectangle.y() + mDepthPadding, rectangle.width() - mDepthPadding * 2 - lScrolbarWidth, rectangle.height() - mDepthPadding * 2, 10.f);
		spriteBatch.end();

		// GL_EQUAL: Passes if ( ref & mask ) = ( stencil & mask ).

		GL11.glStencilFunc(GL11.GL_EQUAL, stencilValue, 0xFFFFFFFF); // Pass test if stencil value is stencilValue

	}

	public void postDraw(LintfordCore core) {
		if (!mPreDrawing)
			return;

		mPreDrawing = false;
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
