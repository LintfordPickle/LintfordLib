package net.lintfordlib.core.input.mouse;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import net.lintfordlib.core.graphics.glfw.GLFWHelper;

public class MouseCursor {

	// --------------------------------------
	// Constants / Statics
	// --------------------------------------

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
			var inputStream = MouseCursor.class.getResourceAsStream(resourceName);
			if (inputStream == null) {
				throw new FileNotFoundException("Cursor resource not found: " + resourceName);
			}

			// Read all bytes from the input stream
			final var imageBytes = inputStream.readAllBytes();
			inputStream.close();

			// We need a native buffer for GLFW data
			final var imageBuffer = BufferUtils.createByteBuffer(imageBytes.length);
			imageBuffer.put(imageBytes);
			imageBuffer.flip();

			var glfwImage = GLFWHelper.imageToGLFWImage(imageBuffer);
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
			var glfwImage = GLFWHelper.imageToGLFWImage(filename);
			lNewMouseCursorInstance.loadCursorFromGLFWImage(glfwImage, hotspotX, hotspotY);

			return lNewMouseCursorInstance;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return null;
	}
}
