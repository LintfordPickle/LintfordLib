package net.lintford.library.renderers;

import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.windows.UIWindow;

public class UIWindowDock extends BaseRenderer {

	public static final String RENDERER_NAME = "UIWindowDock";

	public enum DOCK_POSITION {
		left,
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private TextureBatch mTextureBatch;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UIWindowDock(RendererManager pRendererMnaager, int pRendererGroup) {
		super(pRendererMnaager, RENDERER_NAME, pRendererGroup);

		mTextureBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void initialise(LintfordCore pCore) {
		
	}
	
	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		return super.handleInput(pCore);

	}

	@Override
	public void draw(LintfordCore pCore) {
		float lLeftEdge = pCore.config().display().gameViewportSize().x;
		float lTopEdge = pCore.config().display().gameViewportSize().y;

		List<UIWindow> lUIWindows = mRendererManager.windows();

		float lPosX = -lLeftEdge / 2;
		float lPosY = -lTopEdge / 2;

		mTextureBatch.begin(pCore.HUD());

		for (int i = 0; i < lUIWindows.size(); i++) {
			UIWindow lWindow = lUIWindows.get(i);

			if (!lWindow.isDebugWindow())
				continue;
			{

				// Draw the button background
				mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, 320, 64, 64, 64, lPosX, lPosY, 64, 64, -0.1f, 1f, 1f, 1f, 1f);
				if (lWindow.iconSrcRectangle() != null) {
					AARectangle lSrcRect = lWindow.iconSrcRectangle();
					float lMargin = 12;

					mTextureBatch.draw(TextureManager.TEXTURE_CORE_UI, lSrcRect.x, lSrcRect.y, lSrcRect.w, lSrcRect.h, lPosX + lMargin, lPosY + lMargin, 64 - lMargin * 2, 64 - lMargin * 2, -0.1f, 1f, 1f, 1f, 1f);

				}

				lPosY += 64 + 3;

			}

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
