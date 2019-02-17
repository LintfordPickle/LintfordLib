package net.lintford.library.renderers.windows.components;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;

public abstract class UIWidget extends Rectangle {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8734195273392955490L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected UIWindow mParentWindow;

	protected boolean mIsEnabled;
	protected boolean mIsVisible;

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

	public UIWidget(final UIWindow pParentWindow) {
		super();

		mIsVisible = true;
		mIsEnabled = true;

		mParentWindow = pParentWindow;

	}

	public UIWidget(final UIWindow pParentWindow, final Rectangle pBounds) {
		super(pBounds);

		mIsVisible = true;
		mIsEnabled = true;

		mParentWindow = pParentWindow;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void initialise() {

	}

	public void loadGLContent(final ResourceManager pResourceManager) {

	}

	public void unloadGLContent() {

	}

	public boolean handleInput(LintfordCore pCore) {
		return false;
	}

	public void update(LintfordCore pCore) {

	}

	/** Everything for rendering should be provided by the {@link UIWindow} container. */
	public abstract void draw(LintfordCore pCore, TextureBatch pTextureBatch, Texture pUITexture, FontUnit pTextFont, float pComponentZDepth);

}
