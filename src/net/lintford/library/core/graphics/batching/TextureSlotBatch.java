package net.lintford.library.core.graphics.batching;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.lintford.library.core.graphics.textures.Texture;

public class TextureSlotBatch {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int TEXTURE_SLOTS_FULL = -1;
	public static final int TEXTURE_SLOTS_TEXTURE_INVALID = -2;

	// TODO: Need to poll the hardware for this
	private static final int MAX_TEXTURE_SLOTS = 8;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final List<Integer> mTextureSlots = new ArrayList<>();
	private int mTextureSlotIndex; // next free texture slot

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public TextureSlotBatch() {

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void clear() {
		mTextureSlots.clear();
		mTextureSlotIndex = 0;
	}

	public int getTextureSlotIndex(Texture texture) {
		if (texture == null || texture.getTextureID() == -1)
			return TEXTURE_SLOTS_TEXTURE_INVALID;

		final int lNumTextures = mTextureSlots.size();
		for (int i = 0; i < lNumTextures; i++) {
			if (mTextureSlots.get(i) == texture.getTextureID()) {
				return i;
			}
		}

		if (mTextureSlotIndex < MAX_TEXTURE_SLOTS) {
			mTextureSlots.add(texture.getTextureID());
			return mTextureSlotIndex++;
		}

		return TEXTURE_SLOTS_FULL;
	}

	public void bindTextures() {
		for (int i = 0; i < mTextureSlotIndex; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			final int lTextureIdInSlot = mTextureSlots.get(i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, lTextureIdInSlot);
		}
	}
}
