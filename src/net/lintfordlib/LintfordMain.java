package net.lintfordlib;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug.DebugLogLevel;
import net.lintfordlib.core.graphics.batching.TextureBatchPCT;

public class LintfordMain extends LintfordCore {

	private static final String APPLICATION_NAME = "LintfordLib";

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
				return APPLICATION_NAME;
			}

			@Override
			public String windowTitle() {
				return APPLICATION_NAME;
			}

			@Override
			public int gameCanvasResolutionWidth() {
				return 640;
			}

			@Override
			public int gameCanvasResolutionHeight() {
				return 480;
			}

			@Override
			public int minimumWindowWidth() {
				return gameCanvasResolutionWidth();
			}

			@Override
			public int minimumWindowHeight() {
				return gameCanvasResolutionHeight();
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
	// Variables
	// ---------------------------------------------

	private boolean mStretchStartupLogo;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean stretchStartUpLogo() {
		return mStretchStartupLogo;
	}

	public void stretchStartUpLogo(boolean newValue) {
		mStretchStartupLogo = newValue;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LintfordMain(GameInfo gameInfo, String[] args) {
		super(gameInfo, args);

		mStretchStartupLogo = false;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	protected void showStartUpLogo(long windowHandle) {
		super.showStartUpLogo(windowHandle);

		final var lTexture = mResourceManager.textureManager().loadTexture("LOGO", "/res/textures/core/textureLogo.png", LintfordCore.CORE_ENTITY_GROUP_ID);

		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		mShowLogoTimer = System.currentTimeMillis();

		final var lSrcWidth = lTexture.getTextureWidth();
		final var lSrcHeight = lTexture.getTextureHeight();

		final var lDstWidth = mStretchStartupLogo ? mHUD.getWidth() : lSrcWidth;
		final var lDstHeight = mStretchStartupLogo ? mHUD.getHeight() : lSrcHeight;

		final var lTextureBatch = new TextureBatchPCT();
		lTextureBatch.loadResources(mResourceManager);
		lTextureBatch.begin(mHUD);
		lTextureBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
		lTextureBatch.draw(lTexture, 0, 0, lSrcWidth, lSrcHeight, -lDstWidth * .5f, -lDstHeight * .5f, lDstWidth, lDstHeight, -0.1f);
		lTextureBatch.end();

		glfwSwapBuffers(windowHandle);
	};
}
