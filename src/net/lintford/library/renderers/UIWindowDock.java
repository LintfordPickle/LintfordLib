package net.lintford.library.renderers;

import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;

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

		List<UIWindow> lUIWindows = mRendererManager.windows();

		float lPosX = -lLeftEdge / 2;
		float lPosY = -lTopEdge / 2;

		final TextureBatch lTextureBatch = mRendererManager.uiTextureBatch();
		lTextureBatch.begin(pCore.HUD());

		for (int i = 0; i < lUIWindows.size(); i++) {
			UIWindow lWindow = lUIWindows.get(i);

			if (!lWindow.isDebugWindow())
				continue;
			{

				// Draw the button background
				lTextureBatch.draw(mCoreTexture, 320, 64, 64, 64, lPosX, lPosY, 64, 64, -0.1f, 1f, 1f, 1f, 1f);
				if (lWindow.iconSrcRectangle() != null) {
					Rectangle lSrcRect = lWindow.iconSrcRectangle();
					float lMargin = 12;

					lTextureBatch.draw(mCoreTexture, lSrcRect.x, lSrcRect.y, lSrcRect.w, lSrcRect.h, lPosX + lMargin, lPosY + lMargin, 64 - lMargin * 2, 64 - lMargin * 2, -0.1f, 1f, 1f, 1f, 1f);

				}

				lPosY += 64 + 3;

			}

		}

		lTextureBatch.end();

	}

}
