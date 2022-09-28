package net.lintford.library.core.input.mouse;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
			final var lConvertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			final var lGraphics2d = lConvertedImage.createGraphics();
			final int lTargetWidth = image.getWidth();
			final int lTargetHeight = image.getHeight();
			lGraphics2d.drawImage(image, 0, 0, lTargetWidth, lTargetHeight, null);
			lGraphics2d.dispose();
			image = lConvertedImage;
		}

		final var lByteBuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				int col = image.getRGB(j, i);
				lByteBuffer.put((byte) ((col << 8) >> 24));
				lByteBuffer.put((byte) ((col << 16) >> 24));
				lByteBuffer.put((byte) ((col << 24) >> 24));
				lByteBuffer.put((byte) (col >> 24));
			}
		}
		lByteBuffer.flip();

		final var result = GLFWImage.create();
		result.set(image.getWidth(), image.getHeight(), lByteBuffer);

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

	private MouseCursor(String cursorName, String filename, int hotspotX, int hotspotY) {
		mCursorId = CURSOR_NOT_LOADED;
		mCursorName = cursorName;
		mCursorFilename = filename;
		mHotX = hotspotX;
		mHotY = hotspotY;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean isLoaded() {
		return mCursorId != CURSOR_NOT_LOADED;
	}

	private void loadCursorFromGLFWImage(GLFWImage image, int hotspotX, int hotspotY) {
		mCursorId = GLFW.glfwCreateCursor(image, hotspotX, hotspotY);
	}

	public static MouseCursor loadCursorFromResource(String cursorName, String resourceName, int hotspotX, int hotspotY) {
		var lNewMouseCursorInstance = new MouseCursor(cursorName, resourceName, hotspotX, hotspotY);
		try {
			var glfwImage = imageToGLFWImage(ImageIO.read(MouseCursor.class.getResourceAsStream(resourceName)));
			lNewMouseCursorInstance.loadCursorFromGLFWImage(glfwImage, hotspotX, hotspotY);

			return lNewMouseCursorInstance;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static MouseCursor loadCursorFromFile(String cursorName, String filename, int hotspotX, int hotspotY) {
		var lNewMouseCursorInstance = new MouseCursor(cursorName, filename, hotspotX, hotspotY);
		try {
			var glfwImage = imageToGLFWImage(ImageIO.read(new File(filename)));
			lNewMouseCursorInstance.loadCursorFromGLFWImage(glfwImage, hotspotX, hotspotY);

			return lNewMouseCursorInstance;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
