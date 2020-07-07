package net.lintford.library.core.debug.stats;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;

public class DebugStats {

	/*
	 * TRI Count
	 * 
	 * @formatter:off
	 * 
	 * +-------------------+---------------------+ | Mode | Triangles | +-------------------+---------------------+ | GL_POINTS | count - first | +-------------------+---------------------+ | GL_TRIANGLES | (count -
	 * first) / 3 | +-------------------+---------------------+ | GL_TRIANGLE_STRIP | (count - 2 - first) | +-------------------+---------------------+ | GL_TRIANGLE_FAN | (count - 2 - first) |
	 * +-------------------+---------------------+
	 * 
	 * @formatter:on
	 */

	// --------------------------------------
	// Constants
	// --------------------------------------

	// TODO: The IDs should be managed internally to avoid collisions
	private static int sTagIDCounter = 0;

	public static final int TAG_ID_DRAWCALLS = 0;
	public static final int TAG_ID_VERTS = 1;
	public static final int TAG_ID_TRIS = 2;
	public static final int TAG_ID_BATCH_OBJECTS = 3;
	public static final int TAG_ID_FPS = 4;
	public static final int TAG_ID_TIMING = 12;
	public static final int TAG_ID_TEXTURES = 5;
	public static final int TAG_ID_RENDERTEXTURES = 6;
	public static final int TAG_ID_RES = 7;
	public static final int TAG_ID_VRAM = 8;
	public static final int TAG_ID_VBO = 9;
	public static final int TAG_ID_VB_UPLOADS = 10;
	public static final int TAG_ID_IB_UPLOADS = 11;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;
	private double mLastUpdateElapsed;
	private double mLastDrawElapsed;

	private Texture mCoreTexture;
	private TextureBatchPCT mTextureBatch;
	private StringBuilder mStringBuilder;

	private int deltaFrameCount;
	private int frameCount;
	private double timer;
	private List<DebugStatTag<?>> mTags;
	private transient FontUnit mConsoleFont;
	private boolean mIsOpen;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public static int getNewStatTagCounter() {
		return ++sTagIDCounter;
	}

	public void isOpen(boolean pNewValue) {
		mIsOpen = pNewValue;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStats(final Debug pDebugManager) {
		mDebugManager = pDebugManager;

		mTags = new ArrayList<>();
		mStringBuilder = new StringBuilder();

		mTextureBatch = new TextureBatchPCT();

		createStandardTags();

	}

	private void createStandardTags() {
		mTags.add(new DebugStatTagCaption(-1, "App:"));
		mTags.add(new DebugStatTagFloat(TAG_ID_FPS, "FPS", 0, false));
		mTags.add(new DebugStatTagString(TAG_ID_TIMING, "Timing", ""));
		mTags.add(new DebugStatTagString(TAG_ID_RES, "Resolution", ""));
//		mTags.add(new DebugStatTagFloat(-1, "Ram Used", 0, false));
//		mTags.add(new DebugStatTagFloat(-1, "Ram Free", 0, false));
//		mTags.add(new DebugStatTagFloat(TAG_ID_VRAM, "VRam", 0, false));

		mTags.add(new DebugStatTagCaption(-1, "Graphics:"));
		mTags.add(new DebugStatTagInt(TAG_ID_DRAWCALLS, "Draw Calls", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_VERTS, "Verts", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_TRIS, "Tris", 0));

		mTags.add(new DebugStatTagInt(TAG_ID_BATCH_OBJECTS, "Batch Objects", 0, false));
//		mTags.add(new DebugStatTagInt(TAG_ID_VBO, "VBOs", 0, false));
//		mTags.add(new DebugStatTagInt(TAG_ID_VB_UPLOADS, "VBs", 0));
//		mTags.add(new DebugStatTagInt(TAG_ID_IB_UPLOADS, "IBs", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_TEXTURES, "Textures ", 0, false));
		mTags.add(new DebugStatTagInt(TAG_ID_RENDERTEXTURES, "Render Textures", 0, false));

		mTags.add(new DebugStatTagCaption(-1, "Audio:"));
		mTags.add(new DebugStatTagCaption(-1, "Custom:"));

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadGLContent(ResourceManager pResourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats loading GL content");

		mCoreTexture = pResourceManager.textureManager().textureCore();
		mConsoleFont = pResourceManager.fontManager().systemFont();

		mTextureBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mTextureBatch.unloadGLContent();

		mConsoleFont = null;
		mCoreTexture = null;

	}

	public void preUpdate(LintfordCore pCore) {
		// reset all tags
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			mTags.get(i).reset();

		}

	}

	public void handleInput(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F3)) {
			mIsOpen = !mIsOpen;

		}

	}

	public void update(LintfordCore pCore) {
		mLastUpdateElapsed = pCore.appTime().elapsedTimeMilli();

	}

	public void draw(LintfordCore pCore) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		deltaFrameCount++;

		timer += pCore.appTime().elapsedTimeMilli();
		if (timer > 1000) {
			frameCount = deltaFrameCount;
			deltaFrameCount = 0;
			timer -= 1000;

			Debug.debugManager().stats().setTagValue(DebugStats.TAG_ID_FPS, frameCount);

		}

		mLastDrawElapsed = pCore.appTime().elapsedTimeMilli();

		final String lSpace = " ";
		final String lDelimiter = "|";
		String lIsFixed = (pCore.isFixedTimeStep() ? "f" : "v");
		String lIsRunningSlowly = (pCore.appTime().isRunningSlowly() ? "t" : "f");
		String lUElapsed = String.format(java.util.Locale.US, "%.2f", mLastUpdateElapsed);
		String lDElapsed = String.format(java.util.Locale.US, "%.2f", mLastDrawElapsed);
		String lTotalElapsed = "(" + String.format(java.util.Locale.US, "%.1f", pCore.appTime().totalTimeSeconds()) + "s)";

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		mStringBuilder.append(frameCount).append("fps").append(lSpace);
		mStringBuilder.append(lUElapsed).append("/").append(lDElapsed).append(lSpace);
		mStringBuilder.append(lTotalElapsed).append(lSpace);
		mStringBuilder.append(lIsFixed).append(lDelimiter).append(lIsRunningSlowly);

		((DebugStatTagString) getTagByID(TAG_ID_TIMING)).value = mStringBuilder.toString();

		if (!mIsOpen)
			return;

		mTextureBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());

		Rectangle lHUDRectangle = pCore.HUD().boundingRectangle();

		final var lHeightOffset = Debug.debugManager().console().isOpen() ? 200f : 10f;
		final var lWidthOffset = Debug.debugManager().console().isOpen() ? 360f : 0f;

		float lTop = lHUDRectangle.top() + lHeightOffset + 5f;
		float lLeft = lHUDRectangle.right() - 240f - lWidthOffset - 5f;

		mTextureBatch.draw(mCoreTexture, 0, 0, 32, 32, lLeft, lTop, 240, 500, -0.01f, 0.05f, 0.05f, 0.05f, 0.95f);

		float lTagPosY = lTop + 5f;
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			DebugStatTag<?> lTag = mTags.get(i);
			if (lTag instanceof DebugStatTagCaption) {
				lTagPosY += 5f;
				lTag.draw(mConsoleFont, lLeft + 5f, lTagPosY);
				lTagPosY += 5f;
			} else {
				lTag.draw(mConsoleFont, lLeft + 15f, lTagPosY);
			}

			lTagPosY += 20f;
		}

		mTextureBatch.end();
		mConsoleFont.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTagValue(int pTagID, int pValue) {
		DebugStatTag<?> lTag = getTagByID(pTagID);
		if (lTag != null && lTag instanceof DebugStatTagInt) {
			((DebugStatTagInt) lTag).value = pValue;

		}
		if (lTag != null && lTag instanceof DebugStatTagFloat) {
			((DebugStatTagFloat) lTag).value = (float) pValue;

		}

	}

	public void setTagValue(int pTagID, float pValue) {
		DebugStatTag<?> lTag = getTagByID(pTagID);
		if (lTag != null && lTag instanceof DebugStatTagFloat) {
			((DebugStatTagFloat) lTag).value = pValue;

		}

	}

	public void setTagValue(int pTagID, String pValue) {
		DebugStatTag<?> lTag = getTagByID(pTagID);
		if (lTag != null && lTag instanceof DebugStatTagString) {
			((DebugStatTagString) lTag).value = pValue;

		}

	}

	public void incTag(int pTagID) {
		incTag(pTagID, 1);

	}

	public void incTag(int pTagID, float pAmt) {
		DebugStatTag<?> lTag = getTagByID(pTagID);
		if (lTag instanceof DebugStatTagInt) {
			DebugStatTagInt lIntTag = (DebugStatTagInt) lTag;
			lIntTag.value += (int) pAmt;

		} else if (lTag instanceof DebugStatTagFloat) {
			DebugStatTagFloat lIntTag = (DebugStatTagFloat) lTag;
			lIntTag.value += pAmt;

		}

	}

	public void decTag(int pTagID) {
		decTag(pTagID, 1);
	}

	public void decTag(int pTagID, int pAmt) {
		DebugStatTag<?> lTag = getTagByID(pTagID);
		if (lTag instanceof DebugStatTagInt) {
			DebugStatTagInt lIntTag = (DebugStatTagInt) lTag;
			lIntTag.value -= pAmt;

		} else if (lTag instanceof DebugStatTagFloat) {
			DebugStatTagFloat lIntTag = (DebugStatTagFloat) lTag;
			lIntTag.value -= pAmt;

		}

	}

	public DebugStatTag<?> getTagByID(int pTagId) {
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			if (mTags.get(i).id == pTagId)
				return mTags.get(i);

		}

		return null;

	}

	public void addCustomStatTag(DebugStatTag<?> pCustomTag) {
		if (pCustomTag == null)
			return;

		if (!mTags.contains(pCustomTag)) {
			mTags.add(pCustomTag);

		}

	}

	public void removeCustomStatTag(DebugStatTag<?> pCustomTag) {
		if (pCustomTag == null)
			return;

		if (mTags.contains(pCustomTag)) {
			mTags.remove(pCustomTag);

		}

	}

	public void removeAllCustomTags() {

	}

	// TODO: CustomStatTags

}
