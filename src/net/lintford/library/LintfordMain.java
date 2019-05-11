package net.lintford.library;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;

public class LintfordMain extends LintfordCore {

	// ---------------------------------------------
	// Entry-Point
	// ---------------------------------------------

	public static void main(String[] pArgs) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return "LDLibrary";
			}

			@Override
			public String windowTitle() {
				return "LDLibrary";
			}

			@Override
			public int defaultWindowWidth() {
				return 320;
			}

			@Override
			public int defaultWindowHeight() {
				return 240;
			}

			@Override
			public boolean windowResizeable() {
				return true;
			}

		};

		// ExcavationClient def constructor will automatically create a window and load the previous
		// settings (if they exist).
		LintfordMain lClient = new LintfordMain(lGameInfo, pArgs);
		lClient.createWindow();

	}

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------
	
	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public LintfordMain(GameInfo pGameInfo, String[] pArgs) {
		super(pGameInfo, pArgs);

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected void showStartUpLogo(long pWindowHandle) {
		super.showStartUpLogo(pWindowHandle);

		// Show a mini-splash screen
		Texture lTexture = mResourceManager.textureManager().loadTexture("LOGO", "/res/textures/core/logo.png", LintfordCore.CORE_ENTITY_GROUP_ID);

		glClearColor(0f, 0f, 0f, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		mShowLogoTimer = System.currentTimeMillis();
		
		TextureBatch lTB = new TextureBatch();
		lTB.loadGLContent(mResourceManager);
		lTB.begin(mHUD);
		lTB.draw(lTexture, 0, 0, 256, 256, -128, -128, 256, 256, -0.1f, 1, 1, 1, 1);
		lTB.end();

		glfwSwapBuffers(pWindowHandle);

	};

}
