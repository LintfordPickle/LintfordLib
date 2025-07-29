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

	public void stencilClear() {
		stencilClear(0x00);
	}

	public void stencilClear(int value) {
		GL11.glClearStencil(value);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Clear the stencil buffer
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch) {
		preDraw(core, spriteBatch, mParentArea.contentDisplayArea(), 1);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilValue) {
		preDraw(core, spriteBatch, rectangle, stencilValue, false);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilRefValue, boolean scrollBarEnabled) {
		preDraw(core, spriteBatch, rectangle, stencilRefValue, scrollBarEnabled, 0xFFFFFFFF);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, Rectangle rectangle, int stencilRefValue, boolean scrollBarEnabled, int mask) {
		if (mPreDrawing)
			return;

		mPreDrawing = true;

		GL11.glEnable(GL11.GL_STENCIL_TEST);

		// GL_GEQUAL: Passes if ( ref & mask ) >= ( stencil & mask ).
		GL11.glStencilFunc(GL11.GL_ALWAYS, stencilRefValue, mask);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE); // What should happen to stencil values

		spriteBatch.begin(core.HUD());
		spriteBatch.setColorRGBA(1.f, 1.f, 1.f, 0.f);
		final var lScrolbarWidth = scrollBarEnabled ? ScrollBar.BAR_WIDTH : 0.f;
		spriteBatch.draw((Texture) null, 0, 0, 1, 1, rectangle.x() + mDepthPadding, rectangle.y() + mDepthPadding, rectangle.width() - mDepthPadding * 2 - lScrolbarWidth, rectangle.height() - mDepthPadding * 2, 10.f);
		spriteBatch.end();

		// (ref & mask) <func> (stencil_value & mask)
		// <stencilRefValue> EQUAL <buffervalue>

		GL11.glStencilFunc(GL11.GL_EQUAL, stencilRefValue, mask);
	}

	public void restoreRef(int stencilRefValue) {
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilFunc(GL11.GL_EQUAL, stencilRefValue, 0xFF);
	}

	public void postDraw(LintfordCore core) {
		if (!mPreDrawing)
			return;

		mPreDrawing = false;
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
