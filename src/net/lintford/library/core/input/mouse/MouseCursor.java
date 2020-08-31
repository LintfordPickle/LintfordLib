package net.lintford.library.core.input.mouse;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

public class MouseCursor {

	// --------------------------------------
	// Constants / Statics
	// --------------------------------------

	// TODO: Put this elsewhere
	private static GLFWImage imageToGLFWImage(BufferedImage image) {
		if (image.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
			final BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			final Graphics2D graphics = convertedImage.createGraphics();
			final int targetWidth = image.getWidth();
			final int targetHeight = image.getHeight();
			graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
			graphics.dispose();
			image = convertedImage;
		}

		final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int col = image.getRGB(j, i);
				buffer.put((byte) ((col << 8) >> 24));
				buffer.put((byte) ((col << 16) >> 24));
				buffer.put((byte) ((col << 24) >> 24));
				buffer.put((byte) (col >> 24));
			}
		}
		buffer.flip();

		final GLFWImage result = GLFWImage.create();
		result.set(image.getWidth(), image.getHeight(), buffer);

		return result;
	}

	public static final long CURSOR_NOT_LOADED = -1;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private long mCursorId;
	private String mCursorName;
	private String mCursorFilename;

	private int mHotX;
	private int mHotY;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public long cursorUid() {
		return mCursorId;
	}

	public int hotX() {
		return mHotX;
	}

	public int hotY() {
		return mHotY;
	}

	public String cursorName() {
		return mCursorName;
	}

	public String filename() {
		return mCursorFilename;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	private MouseCursor(String pCursorName, String pFilename, int pHotX, int pHotY) {
		mCursorId = CURSOR_NOT_LOADED;
		mCursorName = pCursorName;
		mCursorFilename = pFilename;
		mHotX = pHotX;
		mHotY = pHotY;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean isLoaded() {
		return mCursorId != CURSOR_NOT_LOADED;
	}

	private void loadCursorFromGLFWImage(GLFWImage pImage, int pHotX, int pHotY) {
		mCursorId = GLFW.glfwCreateCursor(pImage, pHotX, pHotY);

	}

	public static MouseCursor loadCursorFromResource(String pCursorName, String pResourceName, int pHotX, int pHotY) {
		var lNewMouseCursorInstance = new MouseCursor(pCursorName, pResourceName, pHotX, pHotY);
		try {
			var glfwImage = imageToGLFWImage(ImageIO.read(MouseCursor.class.getResourceAsStream(pResourceName)));

			lNewMouseCursorInstance.loadCursorFromGLFWImage(glfwImage, pHotX, pHotY);

			return lNewMouseCursorInstance;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static MouseCursor loadCursorFromFile(String pCursorName, String pFilename, int pHotX, int pHotY) {
		var lNewMouseCursorInstance = new MouseCursor(pCursorName, pFilename, pHotX, pHotY);
		try {
			var glfwImage = imageToGLFWImage(ImageIO.read(new File(pFilename)));

			lNewMouseCursorInstance.loadCursorFromGLFWImage(glfwImage, pHotX, pHotY);

			return lNewMouseCursorInstance;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

}
