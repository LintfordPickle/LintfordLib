package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.Color;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.UiWindow;

public abstract class UIWidget extends Rectangle implements IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8734195273392955490L;
	
	protected static final float lHorizontalPadding = 5.0f;
	protected static final float lVerticalPadding = 5.0f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	public Color entityColor = new Color(1.f, 1.f, 1.f, 1.f);
	protected UiWindow mParentWindow;

	protected boolean mIsEnabled;
	protected boolean mIsVisible;

	protected float mMouseTimer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isEnabled() {
		return mIsEnabled;
	}

	public void isEnabled(boolean pNewValue) {
		mIsEnabled = pNewValue;
	}

	public boolean isVisible() {
		return mIsVisible;
	}

	public void isVisible(boolean pNewValue) {
		mIsVisible = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWidget(final UiWindow pParentWindow) {
		this(pParentWindow, 0, 0, 0, 0);

	}

	public UIWidget(final UiWindow pParentWindow, final Rectangle pBounds) {
		this(pParentWindow, pBounds.x(), pBounds.y(), pBounds.w(), pBounds.h());

	}

	public UIWidget(final UiWindow pParentWindow, float x, float y, float w, float h) {
		super(x, y, w, h);

		setDimensions(100.f, 25.f);

		mIsVisible = true;
		mIsEnabled = true;

		mParentWindow = pParentWindow;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialize() {

	}

	public void loadGLContent(final ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		return false;
	}

	public void update(LintfordCore pCore) {
		if (mMouseTimer >= 0) {
			mMouseTimer -= pCore.appTime().elapsedTimeMilli();

		}

	}

	/** Everything for rendering should be provided by the {@link UiWindow} container. */
	public abstract void draw(LintfordCore pCore, TextureBatchPCT pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth);

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = 200;
	}

}
