package net.lintford.library.renderers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.textures.Texture;

// TODO: http://forum.lwjgl.org/index.php?topic=5757.0
public class MouseCursorRenderer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "Custom Mouse Renderer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mMouseTextureName;
	private String mMouseTextureFilename;
	private Texture mMouseTexture;
	private boolean mIsCustomMouseEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isCustomMouseEnabled() {
		return mIsCustomMouseEnabled;
	}

	@Override
	public boolean isInitialized() {
		return mMouseTexture != null;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MouseCursorRenderer(RendererManager pRendererManager, String pMouseTextureName, String pMouseTextureFilename, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mMouseTextureName = pMouseTextureName;
		mMouseTextureFilename = pMouseTextureFilename;

	}

	// --------------------------------------
	// Core-Methodss
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mMouseTexture = pResourceManager.textureManager().loadTexture(mMouseTextureName, mMouseTextureFilename, GL11.GL_NEAREST, LintfordCore.CORE_ENTITY_GROUP_ID);

	}

	public static ByteBuffer readToByteBuffer(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[4096];

		try {
			while (true) {
				int n = inputStream.read(buffer);

				if (n < 0)
					break;

				outputStream.write(buffer, 0, n);
			}

			inputStream.close();
		} catch (Exception e) {

		}

		byte[] bytes = outputStream.toByteArray();

		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
		byteBuffer.put(bytes).flip();

		return byteBuffer;
	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mMouseTexture = null;
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mIsCustomMouseEnabled)
			return;

		float lCursorX = pCore.HUD().getMouseWorldSpaceX();
		float lCursorY = pCore.HUD().getMouseWorldSpaceY();

		final var lTextureBatch = mRendererManager.uiTextureBatch();

		lTextureBatch.begin(pCore.HUD());
		lTextureBatch.draw(mMouseTexture, 0, 0, 32, 32, lCursorX, lCursorY, 32, 32, -0.01f, 1f, 1f, 1f, 1f);
		lTextureBatch.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void enableCustomMouse() {
		if (mMouseTexture == null) {
			return;
		}

		mIsCustomMouseEnabled = true;
		// mRendererManager.core().config().display().setDisplayMouse(false);

	}

	public void disableCustomMouse() {
		mIsCustomMouseEnabled = false;
		// mRendererManager.core().config().display().setDisplayMouse(true);

	}

}
