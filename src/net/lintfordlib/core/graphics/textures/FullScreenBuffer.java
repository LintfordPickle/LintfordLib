package net.lintfordlib.core.graphics.textures;

import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.geometry.FullScreenTexturedQuad;
import net.lintfordlib.core.graphics.shaders.ShaderMVP_PT;

// TODO: Post jam refactor and split class

/**
 * Renders a fullscreen quad and exposes the pixel data. The pixel buffer is in ARGB 32-bit format. It is held within a Java int array and copied to a native buffer and then uploaded to OpenGL once per frame.
 * 
 * SCREEN_HEIGHT | | | L________ SCREEN_WIDTH 0,0
 */
public class FullScreenBuffer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum BlendMode {
		NORMAL, // Standard overdraw
		ADDITIVE, // glowing / lasers/ fires
		MULTIPLY, // darkens
		SCREEN, // brightening / light effects
	}

	public enum StencilMode {
		Less, LessEqual, Equal, GreaterEqual, Greater,
	}

	public enum DepthMode {
		Less, Equal, Greater
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private ShaderMVP_PT mShader;
	private FullScreenTexturedQuad mFullScreenQuad;
	private int[] rawPixels;
	private int[] mStencil;

	public BlendMode blendMode = BlendMode.NORMAL;
	public DepthMode mDepthMode = DepthMode.Less; // pass if new value lower than stored
	private float[] mDepthBuffer;
	public boolean mEnableDepth;

	public boolean writeOnceLock;
	private byte[] mWriteLock;

	// TODO: rendering walls :(
	public boolean mStencilEnabled;
	public boolean mStencilReadonly = false;
	public StencilMode mStencilMode = StencilMode.Equal;
	public int mStencilValue;

	private int mTextureId;
	private IntBuffer mARGBColorData;
	private boolean mResourcesLoaded;

	private final int mResolutionW;
	private final int mResolutionH;

	// --------------------------------------
	// Getters
	// --------------------------------------

	public int getWidth() {
		return mResolutionW;
	}

	public int getHeight() {
		return mResolutionH;
	}

	public int[] getPixels() {
		return rawPixels;
	}

	public int[] getStencilBuffer() {
		return mStencil;
	}

	public float[] getDepthBuffer() {
		return mDepthBuffer;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public FullScreenBuffer(int width, int height) {
		mResolutionW = width;
		mResolutionH = height;

		rawPixels = new int[mResolutionW * mResolutionH];
		mStencil = new int[mResolutionW * mResolutionH];
		mWriteLock = new byte[mResolutionW * mResolutionH];
		mDepthBuffer = new float[mResolutionW * mResolutionH];

		mShader = new ShaderMVP_PT("SHADER_BASIC", ShaderMVP_PT.BASIC_VERT_FILENAME, ShaderMVP_PT.BASIC_FRAG_FILENAME);
		mFullScreenQuad = new FullScreenTexturedQuad();
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		if (mResourcesLoaded)
			return;

		mFullScreenQuad.loadResources(resourceManager);
		mShader.loadResources(resourceManager);

		mARGBColorData = MemoryUtil.memAllocInt(mResolutionW * mResolutionH);

		mTextureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		mResourcesLoaded = true;
	}

	public void unloadResources() {
		if (!mResourcesLoaded)
			return;

		mFullScreenQuad.unloadResources();
		mShader.unloadResources();

		MemoryUtil.memFree(mARGBColorData);

		mResourcesLoaded = false;
	}

	public void draw(LintfordCore core) {
		final var gameCamera = core.gameCamera();

		mARGBColorData.clear();
		mARGBColorData.put(rawPixels);
		mARGBColorData.flip();

		mShader.projectionMatrix(gameCamera.projection());
		mShader.viewMatrix(gameCamera.view());

		final var scaledCameraWidth = gameCamera.getWidth();
		final var scaledCameraHeight = gameCamera.getHeight();

		mFullScreenQuad.zDepth(1f);
		mFullScreenQuad.onResize((int) scaledCameraWidth, (int) scaledCameraHeight);
		mFullScreenQuad.createModelMatrix();
		mShader.modelMatrix(mFullScreenQuad.modelMatrix());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, mResolutionW, mResolutionH, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, mARGBColorData);

		mShader.bind();
		mFullScreenQuad.draw(core);
		mShader.unbind();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clear(int color) {
		Arrays.fill(rawPixels, color);
		Arrays.fill(mStencil, 0);
		Arrays.fill(mWriteLock, (byte) 0);
		Arrays.fill(mDepthBuffer, Float.MAX_VALUE);
	}

	/*
	 * Bresenham's algorithm
	 */
	public void drawLine(int x0, int y0, int x1, int y1, float zDepth, int color) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;
		int err = dx - dy;

		if ((x0 < 0 && x1 < 0) || (x0 >= mResolutionW && x1 >= mResolutionW) || (y0 < 0 && y1 < 0) || (y0 >= mResolutionH && y1 >= mResolutionH)) {
			return;
		}

		while (true) {
			setPixel(x0, y0, zDepth, color);

			if (x0 == x1 && y0 == y1)
				break;

			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public void drawCircle(int cx, int cy, int radius, int color, boolean filled) {

		if (filled) {
			// Filled circle using midpoint algorithm
			for (int y = -radius; y <= radius; y++) {
				for (int x = -radius; x <= radius; x++) {
					if (x * x + y * y <= radius * radius) {
						setPixel(cx + x, cy + y, color);
					}
				}
			}
		} else {
			// Circle outline using midpoint circle algorithm
			int x = radius;
			int y = 0;
			int radiusError = 1 - x;

			while (x >= y) {
				setPixel(cx + x, cy + y, color);
				setPixel(cx + y, cy + x, color);
				setPixel(cx - y, cy + x, color);
				setPixel(cx - x, cy + y, color);
				setPixel(cx - x, cy - y, color);
				setPixel(cx - y, cy - x, color);
				setPixel(cx + y, cy - x, color);
				setPixel(cx + x, cy - y, color);

				y++;
				if (radiusError < 0) {
					radiusError += 2 * y + 1;
				} else {
					x--;
					radiusError += 2 * (y - x + 1);
				}
			}
		}
	}

	public void drawRect(int x, int y, int width, int height, int color, boolean filled) {
		if (filled) {
			for (int dy = 0; dy < height; dy++) {
				for (int dx = 0; dx < width; dx++) {
					setPixel(x + dx, y + dy, color);
				}
			}
		} else {
			for (int dx = 0; dx < width; dx++) {
				setPixel(x + dx, y, color);
				setPixel(x + dx, y + height - 1, color);
			}
			for (int dy = 0; dy < height; dy++) {
				setPixel(x, y + dy, color);
				setPixel(x + width - 1, y + dy, color);
			}
		}
	}

	public void setPixel(int x, int y, int color) {
		setPixel(x, y, 0.f, color);
	}

	public void setPixel(int x, int y, float z, int color) {
		if (x < 0 || x >= mResolutionW || y < 0 || y >= mResolutionH)
			return;

		blendPixel(x, y, z, color);
	}

	public void copyPixels(int[] srcPixels, int srcPosX, int srcPosY, int srcW, int srcH, int destPosX, int destPosY, int tint, boolean flip, float scale) {
		for (int y = 0; y < srcH * scale; y++) {
			final var dPixY = y + destPosY;
			if (dPixY < 0 || dPixY >= mResolutionH)
				continue;

			for (int x = 0; x < srcW * scale; x++) {
				final var dPixX = x + destPosX;

				final int destIndex = dPixX + dPixY * mResolutionW;
				if (!stencilAllow(destIndex))
					continue;

				var sPixX = (int) (x / scale + srcPosX);
				var sPixY = (int) (y / scale + srcPosY);

				if (flip)
					sPixX = (int) ((int) (srcPosX + srcW) - (x / scale) - 1);

				if (dPixX < 0 || dPixX >= mResolutionW)
					continue;

				final var coord = sPixX + sPixY * srcW;
				if (coord >= srcPixels.length)
					break;

				int srcCol = srcPixels[coord];
				if ((srcCol & 0xff000000) == 0)
					continue;

				// Extract and apply tint to source color
				int srcA = (srcCol >> 24) & 0xFF;
				int srcR = ((srcCol >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
				int srcG = ((srcCol >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
				int srcB = (srcCol & 0xFF) * (tint & 0xFF) / 255;

				if (srcA == 255) {
					// Fully opaque - no blending needed
					rawPixels[destIndex] = (0xFF << 24) | (srcR << 16) | (srcG << 8) | srcB;
				} else {
					// Alpha blend with existing destination pixel
					int destCol = rawPixels[destIndex];
					int destR = (destCol >> 16) & 0xFF;
					int destG = (destCol >> 8) & 0xFF;
					int destB = destCol & 0xFF;

					int invAlpha = 255 - srcA;
					int blendR = (srcR * srcA + destR * invAlpha) / 255;
					int blendG = (srcG * srcA + destG * invAlpha) / 255;
					int blendB = (srcB * srcA + destB * invAlpha) / 255;

					rawPixels[destIndex] = (0xFF << 24) | (blendR << 16) | (blendG << 8) | blendB;
				}
			}
		}
	}

	// this could be cool, but needs fleshing out
	private boolean stencilAllow(int coord) {

		if (mStencilEnabled) {
			switch (mStencilMode) {
			default:
			case Equal:
				if (mStencil[coord] != mStencilValue) {

					if (!mStencilReadonly)
						mStencil[coord] = mStencilValue;

					return true; // assume true == pass
				}
				return false;

			case LessEqual:
				if (mStencil[coord] <= mStencilValue) {

					if (!mStencilReadonly)
						mStencil[coord] = mStencilValue;

					return true; // assume true == pass
				}

				return false;

			case Less:
				if (mStencil[coord] <= mStencilValue) {

					if (!mStencilReadonly)
						mStencil[coord] = mStencilValue;

					return true; // assume true == pass
				}
				return false;

			case GreaterEqual:
				if (mStencil[coord] >= mStencilValue) {

					if (!mStencilReadonly)
						mStencil[coord] = mStencilValue;

					return true; // assume true == pass
				}
				return false;

			case Greater:
				if (mStencil[coord] > mStencilValue) {

					if (!mStencilReadonly)
						mStencil[coord] = mStencilValue;

					return true; // assume true == pass
				}
				return false;

			// finish
			}
		}

		return true; // default true
	}

	private boolean depthAllow(int coord, float zDepth) {
		if (mEnableDepth) {
			switch (mDepthMode) {
			default:
			case Equal:
				if (mDepthBuffer[coord] != zDepth)
					return false;

				break;
			case Less:
				if (mDepthBuffer[coord] > zDepth) {
					mDepthBuffer[coord] = zDepth;
				} else
					return false;
				break;
			case Greater:
				if (mDepthBuffer[coord] < zDepth) {
					mDepthBuffer[coord] = zDepth;
				} else
					return false;
			}

		}

		return true;
	}

	/**
	 * Copies pixels from the src buffer, at the given region, to the destination region of this texture buffer. If the src/dest dimensions are not the same, the copy will scale.
	 */
	public void copyPixelsScale(int[] srcPixels, int srcPosX, int srcPosY, int srcW, int srcH, int destPosX, int destPosY, int destW, int destH, int tint, boolean flip) {
		if (destW > getWidth())
			destW = getWidth();

		if (destH > getHeight())
			destH = getHeight();

		for (int y = 0; y < destH; y++) {
			final var dPixY = y + destPosY;
			if (dPixY < 0 || dPixY >= mResolutionH)
				continue;

			for (int x = 0; x < destW; x++) {
				final var dPixX = x + destPosX;

				if (dPixX < 0 || dPixX >= mResolutionW)
					continue;

				final int destIndex = dPixX + dPixY * mResolutionW;
				if (!stencilAllow(destIndex))
					continue;

				if (writeOnceLock) {
					if (mWriteLock[destIndex] != (byte) 0)
						continue;
					mWriteLock[destIndex] = (byte) 1;
				}

				// Map destination coordinates back to source coordinates
				var sPixX = (int) ((float) x / destW * srcW + srcPosX);
				var sPixY = (int) ((float) y / destH * srcH + srcPosY);

				if (flip)
					sPixX = (int) (srcPosX + srcW - ((float) x / destW * srcW) - 1);

				// Bounds check for source coordinates
				if (sPixX < srcPosX || sPixX >= srcPosX + srcW || sPixY < srcPosY || sPixY >= srcPosY + srcH)
					continue;

				final var coord = sPixX + sPixY * srcW;
				if (coord < 0 || coord >= srcPixels.length)
					continue;

				int srcCol = srcPixels[coord];

				if ((srcCol & 0xff000000) == 0)
					continue;

				// Extract and apply tint to source color
				int srcA = (srcCol >> 24) & 0xFF;
				int srcR = ((srcCol >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
				int srcG = ((srcCol >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
				int srcB = (srcCol & 0xFF) * (tint & 0xFF) / 255;

				if (srcA == 255) {
					// Fully opaque - no blending needed
					rawPixels[destIndex] = (0xFF << 24) | (srcR << 16) | (srcG << 8) | srcB;
				} else {
					// Alpha blend with existing destination pixel
					int destCol = rawPixels[destIndex];
					int destR = (destCol >> 16) & 0xFF;
					int destG = (destCol >> 8) & 0xFF;
					int destB = destCol & 0xFF;

					int invAlpha = 255 - srcA;
					int blendR = (srcR * srcA + destR * invAlpha) / 255;
					int blendG = (srcG * srcA + destG * invAlpha) / 255;
					int blendB = (srcB * srcA + destB * invAlpha) / 255;

					rawPixels[destIndex] = (0xFF << 24) | (blendR << 16) | (blendG << 8) | blendB;
				}
			}
		}
	}

	/**
	 * Copies pixels from the src buffer, at the given region, to the destination region of this texture buffer. This works with texture atlases.
	 */
	public void copyPixelsAtlas(int[] srcPixels, int srcPosX, int srcPosY, int srcW, int srcH, int srcAtlasW, int destPosX, int destPosY, int destW, int destH, float zDepth, int tint, boolean flip) {
		if (destW > getWidth())
			destW = getWidth();

		if (destH > getHeight())
			destH = getHeight();

		for (int y = 0; y < destH; y++) {
			final var dPixY = y + destPosY;
			if (dPixY < 0 || dPixY >= mResolutionH)
				continue;

			for (int x = 0; x < destW; x++) {
				final var dPixX = x + destPosX;

				if (dPixX < 0 || dPixX >= mResolutionW)
					continue;

				final int destIndex = dPixX + dPixY * mResolutionW;
				if (!stencilAllow(destIndex))
					continue;

				if (writeOnceLock) {
					if (mWriteLock[destIndex] != (byte) 0)
						continue;
					mWriteLock[destIndex] = (byte) 1;
				}

				// Map destination coordinates back to source coordinates
				var sPixX = (int) ((float) x / destW * srcW + srcPosX);
				var sPixY = (int) ((float) y / destH * srcH + srcPosY);

				if (flip)
					sPixX = (int) (srcPosX + srcW - ((float) x / destW * srcW) - 1);

				// Bounds check for source coordinates
				if (sPixX < srcPosX || sPixX >= srcPosX + srcW || sPixY < srcPosY || sPixY >= srcPosY + srcH)
					continue;

				// Use atlas width for coordinate calculation
				final var coord = sPixX + sPixY * srcAtlasW;
				if (coord < 0 || coord >= srcPixels.length)
					continue;

				int srcCol = srcPixels[coord];

				if ((srcCol & 0xff000000) == 0)
					continue;

				if (!depthAllow(destIndex, zDepth))
					continue;

				// Extract and apply tint to source color
				int srcA = (srcCol >> 24) & 0xFF;
				int srcR = ((srcCol >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
				int srcG = ((srcCol >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
				int srcB = (srcCol & 0xFF) * (tint & 0xFF) / 255;

				if (srcA == 255) {
					// Fully opaque - no blending needed
					rawPixels[destIndex] = (0xFF << 24) | (srcR << 16) | (srcG << 8) | srcB;
				} else {
					// Alpha blend with existing destination pixel
					int destCol = rawPixels[destIndex];
					int destR = (destCol >> 16) & 0xFF;
					int destG = (destCol >> 8) & 0xFF;
					int destB = destCol & 0xFF;

					int invAlpha = 255 - srcA;
					int blendR = (srcR * srcA + destR * invAlpha) / 255;
					int blendG = (srcG * srcA + destG * invAlpha) / 255;
					int blendB = (srcB * srcA + destB * invAlpha) / 255;

					rawPixels[destIndex] = (0xFF << 24) | (blendR << 16) | (blendG << 8) | blendB;
				}
			}
		}
	}

	public void drawToTextureScaleCropH(int[] srcPixels, int srcPosX, int srcPosY, int srcW, int srcH, int destPosX, int destPosY, int destW, int destH, int tint, boolean flip) {
		for (int y = 0; y < destH; y++) {
			// Invert Y coordinate - start from top instead of bottom
			final var dPixY = destPosY + destH - 1 - y;
			if (dPixY < 0 || dPixY >= mResolutionH)
				continue;

			for (int x = 0; x < destW; x++) {
				// Invert X coordinate - start from right instead of left
				final var dPixX = destPosX + destW - 1 - x;

				if (dPixX < 0 || dPixX >= mResolutionW)
					continue;

				final int destIndex = dPixX + dPixY * mResolutionW;
				if (!stencilAllow(destIndex))
					continue;

				if (writeOnceLock) {
					if (mWriteLock[destIndex] != (byte) 0)
						continue;
					mWriteLock[destIndex] = (byte) 1;
				}

				// Map destination coordinates back to source coordinates
				var sPixX = (int) (srcPosX + srcW - ((float) x / destW * srcW) - 1);
				var sPixY = (int) (srcPosY + srcH - ((float) y / destH * srcH) - 1);

				if (flip)
					sPixX = (int) (srcPosX + srcW - ((float) x / destW * srcW) - 1);

				// Bounds check for source coordinates
				if (sPixX < srcPosX || sPixX >= srcPosX + srcW || sPixY < srcPosY || sPixY >= srcPosY + srcH)
					continue;

				final var coord = sPixX + sPixY * srcW;
				if (coord < 0 || coord >= srcPixels.length)
					continue;

				int srcCol = srcPixels[coord];

				if ((srcCol & 0xff000000) == 0)
					continue;

				int r = ((srcCol >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
				int g = ((srcCol >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
				int b = (srcCol & 0xFF) * (tint & 0xFF) / 255;

				rawPixels[dPixX + dPixY * mResolutionW] = (0xFF << 24) | (r << 16) | (g << 8) | b;
			}
		}
	}

	// --------------------------------------
	// Color Methods
	// --------------------------------------

	public void blendPixel(int x, int y, float z, int srcColor) {
		int idx = x + y * mResolutionW;
		if (idx < 0 || idx >= rawPixels.length)
			return;

		if (!stencilAllow(idx))
			return;

		if (writeOnceLock) {
			if (mWriteLock[idx] != (byte) 0)
				return;
			mWriteLock[idx] = (byte) 1;
		}

		if (!depthAllow(idx, z))
			return;

		switch (blendMode)

		{
		case NORMAL:

			int srcA = (srcColor >> 24) & 0xFF;
			int srcR = (srcColor >> 16) & 0xFF;
			int srcG = (srcColor >> 8) & 0xFF;
			int srcB = srcColor & 0xFF;

			if (srcA == 255) {
				rawPixels[idx] = (0xFF << 24) | (srcR << 16) | (srcG << 8) | srcB;
			} else if (srcA == 0) {
				// Fully transparent, keep destination
				return;
			} else {

				int destCol = rawPixels[idx];
				int destA = (destCol >> 24) & 0xFF;
				int destR = (destCol >> 16) & 0xFF;
				int destG = (destCol >> 8) & 0xFF;
				int destB = destCol & 0xFF;

				// Calculate output alpha
				int outA = srcA + destA * (255 - srcA) / 255;

				if (outA == 0) {
					rawPixels[idx] = 0;
				} else {
					// Blend colors using proper alpha compositing
					int outR = (srcR * srcA + destR * destA * (255 - srcA) / 255) / outA;
					int outG = (srcG * srcA + destG * destA * (255 - srcA) / 255) / outA;
					int outB = (srcB * srcA + destB * destA * (255 - srcA) / 255) / outA;

					rawPixels[idx] = (outA << 24) | (outR << 16) | (outG << 8) | outB;
				}

			}

			break;
		case ADDITIVE:
			int destCol = rawPixels[idx];
			int r = Math.min(255, ((srcColor >> 16) & 0xFF) + ((destCol >> 16) & 0xFF));
			int g = Math.min(255, ((srcColor >> 8) & 0xFF) + ((destCol >> 8) & 0xFF));
			int b = Math.min(255, (srcColor & 0xFF) + (destCol & 0xFF));
			rawPixels[idx] = (0xFF << 24) | (r << 16) | (g << 8) | b;
			break;
		case MULTIPLY:
			destCol = rawPixels[idx];
			r = ((srcColor >> 16) & 0xFF) * ((destCol >> 16) & 0xFF) / 255;
			g = ((srcColor >> 8) & 0xFF) * ((destCol >> 8) & 0xFF) / 255;
			b = (srcColor & 0xFF) * (destCol & 0xFF) / 255;
			rawPixels[idx] = (0xFF << 24) | (r << 16) | (g << 8) | b;
			break;
		case SCREEN:
			destCol = rawPixels[idx];
			r = 255 - ((255 - ((srcColor >> 16) & 0xFF)) * (255 - ((destCol >> 16) & 0xFF)) / 255);
			g = 255 - ((255 - ((srcColor >> 8) & 0xFF)) * (255 - ((destCol >> 8) & 0xFF)) / 255);
			b = 255 - ((255 - (srcColor & 0xFF)) * (255 - (destCol & 0xFF)) / 255);
			rawPixels[idx] = (0xFF << 24) | (r << 16) | (g << 8) | b;
			break;
		}
	}

	/**
	 * Cycles all the colors in the 'oldColors' for those in the 'newColors'.
	 */
	public void paletteSwap(int[] oldColors, int[] newColors) {
		if (oldColors.length != newColors.length)
			return;

		for (int i = 0; i < rawPixels.length; i++) {
			int pixel = rawPixels[i];
			for (int j = 0; j < oldColors.length; j++) {
				if ((pixel & 0x00FFFFFF) == (oldColors[j] & 0x00FFFFFF)) {
					rawPixels[i] = (pixel & 0xFF000000) | (newColors[j] & 0x00FFFFFF);
					break;
				}
			}
		}
	}

	public void drawPolygon(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, float zDepth, int color, boolean filled) {

		if (filled) {
			// Scanline fill algorithm
			// GARBAGE:
			int[] xPoints = { x1, x2, x3, x4 };
			int[] yPoints = { y1, y2, y3, y4 };
			fillPolygon(xPoints, yPoints, zDepth, 4, color);
		} else {
			drawLine(x1, y1, x2, y2, zDepth, color);
			drawLine(x2, y2, x3, y3, zDepth, color);
			drawLine(x3, y3, x4, y4, zDepth, color);
			drawLine(x4, y4, x1, y1, zDepth, color);
		}
	}

	private void fillPolygon(int[] xPoints, int[] yPoints, float zDepth, int nPoints, int color) {
		if (nPoints < 3)
			return;

		// Find bounds
		int minY = yPoints[0];
		int maxY = yPoints[0];
		for (int i = 1; i < nPoints; i++) {
			if (yPoints[i] < minY)
				minY = yPoints[i];
			if (yPoints[i] > maxY)
				maxY = yPoints[i];
		}

		// Clamp to screen bounds
		minY = Math.max(0, minY);
		maxY = Math.min(mResolutionH - 1, maxY);

		// Scanline fill
		for (int y = minY; y <= maxY; y++) {
			int[] intersections = new int[nPoints];
			int intersectionCount = 0;

			// Find all edge intersections with this scanline
			for (int i = 0; i < nPoints; i++) {
				int next = (i + 1) % nPoints;
				int y1 = yPoints[i];
				int y2 = yPoints[next];

				if (y1 == y2)
					continue; // Skip horizontal edges

				if (y >= Math.min(y1, y2) && y < Math.max(y1, y2)) {
					int x1 = xPoints[i];
					int x2 = xPoints[next];
					int x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
					intersections[intersectionCount++] = x;
				}
			}

			// Sort intersections
			Arrays.sort(intersections, 0, intersectionCount);

			// Fill between pairs of intersections
			for (int i = 0; i < intersectionCount - 1; i += 2) {
				int startX = Math.max(0, intersections[i]);
				int endX = Math.min(mResolutionW - 1, intersections[i + 1]);
				for (int x = startX; x <= endX; x++) {
					setPixel(x, y, zDepth, color);
				}
			}
		}
	}

	// --

	public void drawTexturedPolygon(int[] srcPixels, int srcW, int srcH, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, float zDepth, int tint) {
		// TODO: GARBAGE
		fillTexturedPolygon(srcPixels, srcW, srcH, new int[] { x1, x2, x3, x4 }, new int[] { y1, y2, y3, y4 }, zDepth, 4, tint);
	}

	private void fillTexturedPolygon(int[] srcPixels, int srcW, int srcH, int[] xPoints, int[] yPoints, float zDepth, int nPoints, int tint) {
		if (nPoints < 3)
			return;

		// Find bounds
		int minY = yPoints[0];
		int maxY = yPoints[0];
		for (int i = 1; i < nPoints; i++) {
			if (yPoints[i] < minY)
				minY = yPoints[i];
			if (yPoints[i] > maxY)
				maxY = yPoints[i];
		}

		// Clamp to screen bounds
		minY = Math.max(0, minY);
		maxY = Math.min(mResolutionH - 1, maxY);

		// For quad texture mapping, we need to interpolate UV coordinates
		// Assuming the quad vertices map to texture corners in order:
		// (x1,y1) -> (0,0), (x2,y2) -> (1,0), (x3,y3) -> (1,1), (x4,y4) -> (0,1)

		// Scanline fill with texture mapping
		for (int y = minY; y <= maxY; y++) {
			int[] intersections = new int[nPoints * 2]; // Store both x and vertex index
			int intersectionCount = 0;

			// Find all edge intersections with this scanline
			for (int i = 0; i < nPoints; i++) {
				int next = (i + 1) % nPoints;
				int y1 = yPoints[i];
				int y2 = yPoints[next];

				if (y1 == y2)
					continue; // Skip horizontal edges

				if (y >= Math.min(y1, y2) && y < Math.max(y1, y2)) {
					int x1 = xPoints[i];
					int x2 = xPoints[next];
					int x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);

					// Calculate interpolation factor along this edge
					float t = (float) (y - y1) / (y2 - y1);

					intersections[intersectionCount * 2] = x;
					intersections[intersectionCount * 2 + 1] = (int) (t * 1000); // Store t scaled
					intersectionCount++;
				}
			}

			// Sort intersections by x coordinate
			for (int i = 0; i < intersectionCount - 1; i++) {
				for (int j = i + 1; j < intersectionCount; j++) {
					if (intersections[i * 2] > intersections[j * 2]) {
						int tempX = intersections[i * 2];
						int tempT = intersections[i * 2 + 1];
						intersections[i * 2] = intersections[j * 2];
						intersections[i * 2 + 1] = intersections[j * 2 + 1];
						intersections[j * 2] = tempX;
						intersections[j * 2 + 1] = tempT;
					}
				}
			}

			// Fill between pairs of intersections with texture mapping
			for (int i = 0; i < intersectionCount - 1; i += 2) {
				int startX = Math.max(0, intersections[i * 2]);
				int endX = Math.min(mResolutionW - 1, intersections[(i + 1) * 2]);

				if (endX <= startX)
					continue;

				// Simple bilinear interpolation for quad texture mapping
				// Calculate normalized position in the quad
				float normY = (float) (y - yPoints[0]) / (yPoints[2] - yPoints[0]);
				normY = Math.max(0, Math.min(1, normY));

				for (int x = startX; x <= endX; x++) {
					// Calculate normalized X position across the scanline
					float normX = (float) (x - startX) / (endX - startX);

					// Map to texture coordinates
					int texX = (int) (normX * (srcW - 1));
					int texY = (int) (normY * (srcH - 1));

					// Clamp texture coordinates
					texX = Math.max(0, Math.min(srcW - 1, texX));
					texY = Math.max(0, Math.min(srcH - 1, texY));

					// Sample from source texture
					int srcCoord = texX + texY * srcW;
					if (srcCoord < 0 || srcCoord >= srcPixels.length)
						continue;

					int srcCol = srcPixels[srcCoord];

					// Skip fully transparent pixels
					if ((srcCol & 0xff000000) == 0)
						continue;

					// Check destination index
					final int destIndex = x + y * mResolutionW;
					if (!stencilAllow(destIndex))
						continue;

					if (writeOnceLock) {
						if (mWriteLock[destIndex] != (byte) 0)
							continue;
						mWriteLock[destIndex] = (byte) 1;
					}

					if (!depthAllow(destIndex, zDepth))
						continue;

					// Apply tint and blend
					int srcA = (srcCol >> 24) & 0xFF;
					int srcR = ((srcCol >> 16) & 0xFF) * ((tint >> 16) & 0xFF) / 255;
					int srcG = ((srcCol >> 8) & 0xFF) * ((tint >> 8) & 0xFF) / 255;
					int srcB = (srcCol & 0xFF) * (tint & 0xFF) / 255;

					if (srcA == 255) {
						// Fully opaque - no blending needed
						rawPixels[destIndex] = (0xFF << 24) | (srcR << 16) | (srcG << 8) | srcB;
					} else {
						// Alpha blend with existing destination pixel
						int destCol = rawPixels[destIndex];
						int destR = (destCol >> 16) & 0xFF;
						int destG = (destCol >> 8) & 0xFF;
						int destB = destCol & 0xFF;

						int invAlpha = 255 - srcA;
						int blendR = (srcR * srcA + destR * invAlpha) / 255;
						int blendG = (srcG * srcA + destG * invAlpha) / 255;
						int blendB = (srcB * srcA + destB * invAlpha) / 255;

						rawPixels[destIndex] = (0xFF << 24) | (blendR << 16) | (blendG << 8) | blendB;
					}
				}
			}
		}
	}

}
