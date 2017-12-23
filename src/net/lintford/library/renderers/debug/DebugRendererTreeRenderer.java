package net.lintford.library.renderers.debug;

import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.renderers.RendererManager;
import net.lintford.library.renderers.windows.UIWindow;

public class DebugRendererTreeRenderer extends UIWindow {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DebugRendererTreeRenderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private FontUnit mFontUnit;
	private TextureBatch mTextureBatch;
	
	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return 1;
	}
	
	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugRendererTreeRenderer(RendererManager pRendererManager, int pRendererGroup) {
		super(pRendererManager, RENDERER_NAME, pRendererGroup);

		mTextureBatch = new TextureBatch();
		
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);
		
		mTextureBatch.loadGLContent(pResourceManager);
		mFontUnit = pResourceManager.fontManager().loadNewFont("DebugFont", "res/fonts/system.ttf", 14, true);
		
	}
	
	@Override
	public void unloadGLContent() {
		super.unloadGLContent();
		
		mTextureBatch.unloadGLContent();
		mFontUnit.unloadGLContent();
		
	}
	
	@Override
	public void draw(LintfordCore pCore) {
		
		// Get a list of controllers to .. control
		List<BaseController> lControllers = mRendererManager.core().controllerManager().controllers();
		final int CONTROLLER_COUNT = lControllers.size();
		
		float lWindowWidth = pCore.config().display().windowSize().x / 2f;
		float lWindowHeight = pCore.config().display().windowSize().y / 2f;
		
		float lCurrentHeight = 0;
		
		mFontUnit.begin(pCore.HUD());
		mTextureBatch.begin(pCore.HUD());
		
		for(int i = 0; i < CONTROLLER_COUNT; i++) {
			BaseController lCurrentController = lControllers.get(i);
			mTextureBatch.draw(32, 0, 32, 32, -lWindowWidth, -lWindowHeight + lCurrentHeight, -0.1f, 256, lCurrentHeight + 25, 1f, TextureManager.TEXTURE_CORE_UI);
			mFontUnit.draw(lCurrentController.getClass().getSimpleName(), -lWindowWidth, -lWindowHeight + lCurrentHeight, 1.0f);
			
			lCurrentHeight+= 25;
			
		}
		
		mTextureBatch.end();
		mFontUnit.end();
		
	}
	
	// --------------------------------------
	// Methods
	// --------------------------------------

}
