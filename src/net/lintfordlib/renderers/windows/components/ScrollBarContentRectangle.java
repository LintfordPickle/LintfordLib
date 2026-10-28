package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
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

	public void stencilClear() {
		StencilHelper.clear();
	}

	public void stencilClear(int value) {
		StencilHelper.clear();
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
		preDraw(core, spriteBatch, rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height(), stencilRefValue, scrollBarEnabled, mask);
	}

	public void preDraw(LintfordCore core, SpriteBatch spriteBatch, float x, float y, float w, float h, int stencilRefValue, boolean scrollBarEnabled, int mask) {
		if (mPreDrawing)
			return;

		mPreDrawing = true;

		final var lScrolbarWidth = scrollBarEnabled ? ScrollBar.BAR_WIDTH : 0.f;
		final var actualX = x + mDepthPadding;
		final var actualY = y + mDepthPadding;
		final var actualW = Math.max(0.f, w - mDepthPadding * 2.f - lScrolbarWidth);
		final var actualH = Math.max(0.f, h - mDepthPadding * 2.f);

		StencilHelper.preDraw(core, spriteBatch, actualX, actualY, actualW, actualH);
	}

	public void restoreRef(int stencilRefValue) {
		// Handled automatically by StencilHelper's hierarchical stack
	}

	public void postDraw(LintfordCore core) {
		postDraw(core, null);
	}

	public void postDraw(LintfordCore core, SpriteBatch spriteBatch) {
		if (!mPreDrawing)
			return;

		mPreDrawing = false;
		StencilHelper.postDraw(core, spriteBatch);
	}
}
