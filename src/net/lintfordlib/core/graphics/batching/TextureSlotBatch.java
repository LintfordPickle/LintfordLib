package net.lintfordlib.core.graphics.batching;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.lintfordlib.core.graphics.textures.Texture;

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

	private int mNumProtectedTextureIds;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void textureSlotOffset(int numProtectedTextureIndices) {
		mNumProtectedTextureIds = numProtectedTextureIndices;
	}

	public boolean hasFreeSlot() {
		return mTextureSlotIndex < MAX_TEXTURE_SLOTS;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public boolean containsAnyTextures() {
		return mTextureSlotIndex > 0;
	}

	public void clear() {
		mTextureSlots.clear();
		mTextureSlotIndex = 0;
	}

	public boolean containsTextureId(int textureId) {
		final int lNumTextures = mTextureSlots.size();
		for (int i = 0; i < lNumTextures; i++) {
			if (mTextureSlots.get(i) == textureId) {
				return true;
			}
		}
		return false;
	}

	public int getTextureSlotIndexFromUid(int textureUid) {
		if (textureUid == -1)
			return TEXTURE_SLOTS_TEXTURE_INVALID;

		final int lNumTextures = mTextureSlots.size();
		for (int i = 0; i < lNumTextures; i++) {
			if (mTextureSlots.get(i) == textureUid) {
				return i;
			}
		}

		if (mTextureSlotIndex < MAX_TEXTURE_SLOTS) {
			mTextureSlots.add(textureUid);
			return mTextureSlotIndex++;
		}

		return TEXTURE_SLOTS_FULL;
	}

	public int getTextureSlotIndex(Texture texture) {
		if (texture == null || texture.getTextureID() == -1)
			return TEXTURE_SLOTS_TEXTURE_INVALID;

		return getTextureSlotIndex(texture.getTextureID());
	}

	public int getTextureSlotIndex(int textureId) {
		if (textureId == -1)
			return TEXTURE_SLOTS_TEXTURE_INVALID;

		return getTextureSlotIndexFromUid(textureId);
	}

	public void bindTextures() {
		for (int i = 0; i < mTextureSlotIndex; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + mNumProtectedTextureIds + i);
			final int lTextureIdInSlot = mTextureSlots.get(i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, lTextureIdInSlot);
		}
	}

	public void unbindTextures() {
		for (int i = 0; i < mTextureSlotIndex; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}
	}
}
