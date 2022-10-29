package net.lintford.library;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug.DebugLogLevel;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.batching.TextureBatchPCT;

public class LintfordMain extends LintfordCore {

	// ---------------------------------------------
	// Entry-Point
	// ---------------------------------------------

	public static void main(String[] args) {
		final var lGameInfo = new GameInfo() {
			@Override
			public DebugLogLevel debugLogLevel() {
				return DebugLogLevel.info;
			}

			@Override
			public String applicationName() {
				return "LDLibrary";
			}

			@Override
			public String windowTitle() {
				return "LDLibrary";
			}

			@Override
			public int baseGameResolutionWidth() {
				return 640;
			}

			@Override
			public int baseGameResolutionHeight() {
				return 480;
			}

			@Override
			public int minimumWindowWidth() {
				return baseGameResolutionWidth();
			}

			@Override
			public int minimumWindowHeight() {
				return baseGameResolutionHeight();
			}

			@Override
			public boolean windowResizeable() {
				return true;
			}

		};

		var lClient = new LintfordMain(lGameInfo, args);
		lClient.createWindow();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LintfordMain(GameInfo gameInfo, String[] args) {
		super(gameInfo, args);
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected void showStartUpLogo(long windowHandle) {
		super.showStartUpLogo(windowHandle);

		final var lTexture = mResourceManager.textureManager().loadTexture("LOGO", "/res/textures/core/textureLogo.png", LintfordCore.CORE_ENTITY_GROUP_ID);

		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		mShowLogoTimer = System.currentTimeMillis();

		final var lStretchLogoToFit = false;
		final var lSrcWidth = lTexture.getTextureWidth();
		final var lSrcHeight = lTexture.getTextureHeight();

		final var lDstWidth = lStretchLogoToFit ? mHUD.getWidth() : lSrcWidth;
		final var lDstHeight = lStretchLogoToFit ? mHUD.getHeight() : lSrcHeight;

		final var lTextureBatch = new TextureBatchPCT();
		lTextureBatch.loadResources(mResourceManager);
		lTextureBatch.begin(mHUD);
		lTextureBatch.draw(lTexture, 0, 0, lSrcWidth, lSrcHeight, -lDstWidth * .5f, -lDstHeight * .5f, lDstWidth, lDstHeight, -0.1f, ColorConstants.WHITE);
		lTextureBatch.end();

		glfwSwapBuffers(windowHandle);
	};
}
