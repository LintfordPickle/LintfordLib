package net.lintfordlib.core.input.mouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

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
			var glfwImage = GLFWHelper.imageToGLFWImage(ImageIO.read(MouseCursor.class.getResourceAsStream(resourceName)));
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
			var glfwImage = GLFWHelper.imageToGLFWImage(ImageIO.read(new File(filename)));
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
