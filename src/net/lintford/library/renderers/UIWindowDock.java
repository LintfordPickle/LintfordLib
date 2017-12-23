package net.lintford.library.renderers;

import java.util.List;

import net.lintford.library.core.LintfordCore;
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
	// Properties
	// --------------------------------------

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
			
			if(!lWindow.isDebugWindow()) continue;

			{

				// Draw the button background
				mTextureBatch.draw(32, 0, 32, 32, lPosX, lPosY, -0.1f, 64, 64, 1f, TextureManager.TEXTURE_CORE_UI);
				if(lWindow.iconSrcRectangle() != null)
					mTextureBatch.draw(lWindow.iconSrcRectangle().x, lWindow.iconSrcRectangle().y, lWindow.iconSrcRectangle().width, lWindow.iconSrcRectangle().height, lPosX, lPosY, -0.1f, 64, 64, 1f, TextureManager.TEXTURE_CORE_UI);

				lPosY += 64 + 3;

			}

		}

		mTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

}
