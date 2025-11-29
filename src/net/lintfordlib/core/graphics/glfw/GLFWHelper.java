package net.lintfordlib.core.graphics.glfw;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

public class GLFWHelper {

	/**
	 * Loads an image from a file path and converts it to a GLFWImage.
	 * 
	 * @param imagePath The file path to the image
	 * @return A GLFWImage containing the loaded image data
	 * @throws RuntimeException if the image cannot be loaded
	 */
	public static GLFWImage imageToGLFWImage(String imagePath) {
		ByteBuffer imageBuffer;
		int width, height;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			// Load image with 4 channels (RGBA)
			imageBuffer = STBImage.stbi_load(imagePath, w, h, channels, 4);

			if (imageBuffer == null) {
				throw new RuntimeException("Failed to load image: " + imagePath + " - " + STBImage.stbi_failure_reason());
			}

			width = w.get(0);
			height = h.get(0);
		}

		// Create a new buffer to store the image data
		// (we need to copy because STB's buffer needs to be freed)
		final var lByteBuffer = BufferUtils.createByteBuffer(width * height * 4);
		lByteBuffer.put(imageBuffer);
		lByteBuffer.flip();

		// Free the STB image buffer
		STBImage.stbi_image_free(imageBuffer);

		// Create and configure GLFWImage
		final var result = GLFWImage.create();
		result.set(width, height, lByteBuffer);

		return result;
	}

	/**
	 * Loads an image from a ByteBuffer and converts it to a GLFWImage.
	 * Useful when loading from memory or embedded resources.
	 * 
	 * @param imageData ByteBuffer containing the encoded image data (PNG, JPG, etc.)
	 * @return A GLFWImage containing the loaded image data
	 * @throws RuntimeException if the image cannot be loaded
	 */
	public static GLFWImage imageToGLFWImage(ByteBuffer imageData) {
		ByteBuffer imageBuffer;
		int width, height;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer w = stack.mallocInt(1);
			IntBuffer h = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			// Load image from memory with 4 channels (RGBA)
			imageBuffer = STBImage.stbi_load_from_memory(imageData, w, h, channels, 4);

			if (imageBuffer == null) {
				throw new RuntimeException("Failed to load image from memory - " + STBImage.stbi_failure_reason());
			}

			width = w.get(0);
			height = h.get(0);
		}

		// Create a new buffer to store the image data
		final var lByteBuffer = BufferUtils.createByteBuffer(width * height * 4);
		lByteBuffer.put(imageBuffer);
		lByteBuffer.flip();

		// Free the STB image buffer
		STBImage.stbi_image_free(imageBuffer);

		// Create and configure GLFWImage
		final var result = GLFWImage.create();
		result.set(width, height, lByteBuffer);

		return result;
	}
}
