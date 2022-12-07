package net.lintford.library.core.graphics.glfw;

import java.awt.image.BufferedImage;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

public class GLFWHelper {
	public static GLFWImage imageToGLFWImage(BufferedImage image) {
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
}
