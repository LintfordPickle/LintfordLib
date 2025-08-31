package net.lintfordlib.core.graphics.fonts;

import java.util.Locale;

import net.lintfordlib.core.graphics.batching.SpriteBatch;
import net.lintfordlib.core.graphics.textures.Texture;

public class CharAtlasRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	// this defines the order of characters as they appear in the underling texture.
	// 8x8
	private static final String defaultCharacterSequence = "0123456789:.,";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Texture mNumberTextureAtlas;
	private float mCharWidthPx;
	private float mCharHeightPx;

	private int mTilesWide;
	private int mTilesHigh;

	private String mCharacterSequence;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setCharacterSequence(String newCharSequence) {
		if (newCharSequence == null) {
			mCharacterSequence = defaultCharacterSequence;
			return;
		}

		mCharacterSequence = newCharSequence;
	}

	public float charWidth() {
		return mCharWidthPx;
	}

	public void charWidth(float newWidth) {
		mCharWidthPx = newWidth;
	}

	public float charHeight() {
		return mCharHeightPx;
	}

	public void charHeight(float newHeight) {
		mCharHeightPx = newHeight;
	}

	public Texture textureAtlas() {
		return mNumberTextureAtlas;
	}

	public void textureAtlas(Texture newTextureAtlas) {
		mNumberTextureAtlas = newTextureAtlas;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public CharAtlasRenderer() {

		mCharWidthPx = 16;
		mCharHeightPx = 16;

		mTilesWide = 8;
		mTilesHigh = 8;

		mCharacterSequence = defaultCharacterSequence;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void unloadResources() {
		mNumberTextureAtlas = null;
	}

	/**
	 * Requires that the first 0-9 characters in the atlas are the digits 0-9
	 */
	public void drawNumber(SpriteBatch spritebatch, int value, float worldX, float worldY, float zDepth, float scale) {
		if (mNumberTextureAtlas == null)
			throw new RuntimeException(CharAtlasRenderer.class.getSimpleName() + " does not have a valid texture atlas assigned");

		final var textureHeight = mNumberTextureAtlas.getTextureHeight();

		float dx = worldX;
		float dy = worldY;

		spritebatch.setColorRGBA(196f / 255f, 163f / 255f, 0f / 255f, 1.f);
		final var t = String.valueOf(value);
		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			int charIndex = mCharacterSequence.indexOf(c);
			if (charIndex == -1)
				continue;

			final int cox = charIndex % mTilesWide;
			final int coy = charIndex / mTilesWide;

			final var srcX = cox * mCharWidthPx;
			final var srcY = textureHeight - coy * mCharWidthPx - mCharWidthPx;

			final var lCharacterSize = mCharWidthPx * scale;
			spritebatch.draw(mNumberTextureAtlas, srcX, srcY, mCharWidthPx, mCharHeightPx, dx, dy, lCharacterSize, lCharacterSize, zDepth);
			dx += mCharWidthPx * scale;
		}
	}

	/**
	 * Requires that the first 0-9 characters in the atlas are the digits 0-9
	 */
	public void drawNumber(SpriteBatch spritebatch, float value, float worldX, float worldY, float zDepth, float scale, int decimalPlaces) {
		final var lTextureHeight = mNumberTextureAtlas.getTextureHeight();
		float dx = worldX;
		float dy = worldY;

		final var sts = 16.f;

		// Limit decimal precision
		final var t = String.format(Locale.US, "%." + decimalPlaces + "f", value); // avoids locale issues like ','

		for (int i = 0; i < t.length(); i++) {
			char c = t.charAt(i);
			int charIndex = mCharacterSequence.indexOf(c);
			if (charIndex == -1)
				continue; // skip any unsupported characters

			final float cox = charIndex % 8;
			final float coy = charIndex / 8;

			final var lX = cox * 16;
			final var lY = lTextureHeight - coy * sts - 16;

			final var lCharacterSize = sts * scale;
			spritebatch.setColorRGBA(196f / 255f, 163f / 255f, 0f / 255f, 1.f);
			spritebatch.draw(mNumberTextureAtlas, lX, lY, 16, 16, dx, dy, lCharacterSize, lCharacterSize, zDepth);

			dx += 16.f * scale;
		}
	}
}
