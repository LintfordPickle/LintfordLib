package net.ld.library.core.graphics.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class Texture {

	// =============================================
	// Variables
	// =============================================

	private final int mTextureID;
	private final String mTextureLocation;
	private int mTextureWidth;
	private int mTextureHeight;
	private int mFilter;

	// =============================================
	// Properties
	// =============================================

	public int getTextureID() {
		return mTextureID;
	}

	public int getTextureWidth() {
		return mTextureWidth;
	}

	public int getTextureHeight() {
		return mTextureHeight;
	}

	// =============================================
	// Constructor
	// =============================================

	public Texture(int pTextureID, String pFilename, int pWidth, int pHeight, int pFilter) {
		mTextureID = pTextureID;
		mTextureLocation = pFilename;
		mTextureWidth = pWidth;
		mTextureHeight = pHeight;
		mFilter = pFilter;

	}

	// =============================================
	// Methods
	// =============================================

	// package access (textures should be loaded using the texture manager.
	static Texture loadTexture(String pFilename) {
		return loadTexture(pFilename, GL11.GL_NEAREST);
	}

	// package access (textures should be loaded using the texture manager.
	static Texture loadTexture(String pFilename, int pFilter) {
		System.out.println("Loading texture from : " + pFilename);

		BufferedImage lImage = null;
		int[] lPixels = null;
		int lTexWidth = 0;
		int lTexHeight = 0;

		// 1. load the image
		try {

			File lTextureFile = new File(pFilename);
			lImage = ImageIO.read(lTextureFile);

			lTexWidth = lImage.getWidth();
			lTexHeight = lImage.getHeight();

			lPixels = new int[lTexWidth * lTexHeight];
			lImage.getRGB(0, 0, lTexWidth, lTexHeight, lPixels, 0, lTexWidth);

		} catch (FileNotFoundException e) {
			System.out.println("Error loading texture (File not found at " + pFilename + " )");
			e.printStackTrace();

			// TODO: Errors with texture loading shouldn't throw a runtime exception. We should handle it gracefully and continue.
			throw new RuntimeException("Failed to load texture resource");

		} catch (IOException e) {
			System.out.println("Error loading texture from " + pFilename);
			e.printStackTrace();

			// TODO: Errors with texture loading shouldn't throw a runtime exception. We should handle it gracefully and continue.
			throw new RuntimeException("Failed to load texture resource");
		}

		// 2. change channel order
		int[] lTextureData = new int[lTexWidth * lTexHeight];
		for (int i = 0; i < lTexWidth * lTexHeight; i++) {
			int a = (lPixels[i] & 0xff000000) >> 24;
			int r = (lPixels[i] & 0xff0000) >> 16;
			int g = (lPixels[i] & 0xff00) >> 8;
			int b = (lPixels[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r; // backwards ??
		}

		// 3. Create OpenGL texture and return ID
		int lTexID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, lTexID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, pFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, pFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(lTextureData.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(lTextureData);
		lBuffer.flip();

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, lTexWidth, lTexHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return new Texture(lTexID, pFilename, lTexWidth, lTexHeight, pFilter);

	}

	public void reload() {
		System.out.println("Reloading texture from : " + mTextureLocation);

		BufferedImage lImage = null;
		int[] lPixels = null;
		int lTexWidth = 0;
		int lTexHeight = 0;

		// 1. load the image
		try {

			File lTextureFile = new File(mTextureLocation);
			lImage = ImageIO.read(lTextureFile);

			lTexWidth = lImage.getWidth();
			lTexHeight = lImage.getHeight();

			lPixels = new int[lTexWidth * lTexHeight];
			lImage.getRGB(0, 0, lTexWidth, lTexHeight, lPixels, 0, lTexWidth);

		} catch (FileNotFoundException e) {
			System.out.println("Error reloading texture (File not found at " + mTextureLocation + " )");
			e.printStackTrace();

		} catch (IOException e) {
			System.out.println("Error reloading texture from " + mTextureLocation);
			e.printStackTrace();
		}

		// 2. change channel order
		int[] lTextureData = new int[lTexWidth * lTexHeight];
		for (int i = 0; i < lTexWidth * lTexHeight; i++) {
			int a = (lPixels[i] & 0xff000000) >> 24;
			int r = (lPixels[i] & 0xff0000) >> 16;
			int g = (lPixels[i] & 0xff00) >> 8;
			int b = (lPixels[i] & 0xff);

			lTextureData[i] = a << 24 | b << 16 | g << 8 | r; // backwards ??
		}

		// 3. Create OpenGL texture and return ID
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureID);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mFilter);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mFilter);

		IntBuffer lBuffer = ByteBuffer.allocateDirect(lTextureData.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
		lBuffer.put(lTextureData);
		lBuffer.flip();

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, lTexWidth, lTexHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, lBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
