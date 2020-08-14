package net.lintford.library.controllers.core;

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

import net.lintford.library.controllers.BaseController;
import net.lintford.library.core.LintfordCore;

public class MouseCursorController extends BaseController {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String CONTROLLER_NAME = "Custom Cursor Controller";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mIsCustomMouseEnabled;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isCustomMouseEnabled() {
		return mIsCustomMouseEnabled;
	}

	@Override
	public boolean isinitialized() {
		// TODO Auto-generated method stub
		return false;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MouseCursorController(ControllerManager pControllerManager, int pEntityGroupID) {
		super(pControllerManager, CONTROLLER_NAME, pEntityGroupID);

	}

	// --------------------------------------
	// Core-Methodss
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		try {
			var glfwImage = imageToGLFWImage(ImageIO.read(new File("res//cursors//cursorDefault.png")));

			long windowId = pCore.config().display().windowID();
			long cursorPointer = GLFW.glfwCreateCursor(glfwImage, 0, 0);
			GLFW.glfwSetCursor(windowId, cursorPointer);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unload() {

	}

	private GLFWImage imageToGLFWImage(BufferedImage image) {
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

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void enableCustomMouse() {
		mIsCustomMouseEnabled = true;

	}

	public void disableCustomMouse() {
		mIsCustomMouseEnabled = false;

	}

}
