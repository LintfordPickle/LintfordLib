package net.lintford.library.renderers;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.textures.Texture;

public class UIWindowDock extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "UIWindowDock";

	public enum DOCK_POSITION {
		left,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected Texture mCoreTexture;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWindowDock(RendererManager pRendererMnaager, int pRendererGroup) {
		super(pRendererMnaager, RENDERER_NAME, pRendererGroup);

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mCoreTexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		return super.handleInput(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {
		float lLeftEdge = pCore.config().display().windowWidth();
		float lTopEdge = pCore.config().display().windowHeight();

		final var lUiWindows = mRendererManager.windows();

		float lPosX = -lLeftEdge / 2;
		float lPosY = -lTopEdge / 2;

		final var lTextureBatch = mRendererManager.uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());

		for (int i = 0; i < lUiWindows.size(); i++) {
			final var lWindow = lUiWindows.get(i);

			if (!lWindow.isDebugWindow())
				continue;
			{

				// Draw the button background
				lTextureBatch.draw(mCoreTexture, 320, 64, 64, 64, lPosX, lPosY, 64, 64, -0.1f, ColorConstants.WHITE);
				if (lWindow.iconSrcRectangle() != null) {
					Rectangle lSrcRect = lWindow.iconSrcRectangle();
					float lMargin = 12;

					lTextureBatch.draw(mCoreTexture, lSrcRect, lPosX + lMargin, lPosY + lMargin, 64 - lMargin * 2, 64 - lMargin * 2, -0.1f, ColorConstants.WHITE);

				}

				lPosY += 64 + 3;

			}

		}

		lTextureBatch.end();

	}

}
