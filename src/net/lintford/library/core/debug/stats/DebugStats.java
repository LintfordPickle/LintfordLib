package net.lintford.library.core.debug.stats;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.ColorConstants;
import net.lintford.library.core.graphics.fonts.BitmapFontManager;
import net.lintford.library.core.graphics.fonts.FontUnit;
import net.lintford.library.core.graphics.sprites.spritebatch.SpriteBatch;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.CoreTextureNames;
import net.lintford.library.core.input.IProcessMouseInput;
import net.lintford.library.renderers.windows.components.IScrollBarArea;
import net.lintford.library.renderers.windows.components.ScrollBar;
import net.lintford.library.renderers.windows.components.ScrollBarContentRectangle;

public class DebugStats extends Rectangle implements IScrollBarArea, IProcessMouseInput {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 8609937429906072627L;

	private static int sTagIDCounter = 0;

	public static final int TAG_ID_DRAWCALLS = 0;
	public static final int TAG_ID_VERTS = 1;
	public static final int TAG_ID_TRIS = 2;
	public static final int TAG_ID_BATCH_OBJECTS = 3;
	public static final int TAG_ID_FPS = 4;
	public static final int TAG_ID_TIMESTEP = 13;
	public static final int TAG_ID_TIMING = 12;
	public static final int TAG_ID_TEXTURES = 5;
	public static final int TAG_ID_RENDERTEXTURES = 6;
	public static final int TAG_ID_RES = 7;
	public static final int TAG_ID_VRAM = 8;
	public static final int TAG_ID_VBO = 9;
	public static final int TAG_ID_VB_UPLOADS = 10;
	public static final int TAG_ID_IB_UPLOADS = 11;

	private static final float WINDOW_SIZE_WIDTH = 350.f;
	private static final float WINDOW_SIZE_HEIGHT = 500.f;
	private static final float INNER_CONTENT_MARGIN = 5.f;
	private static final float INNER_CONTENT_PADDING = 5.f;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private final Debug mDebugManager;
	private double mLastUpdateElapsed;
	private double mLastDrawElapsed;

	private SpriteSheetDefinition mCoreSpritesheet;
	private SpriteBatch mSpriteBatch;
	private StringBuilder mStringBuilder;

	private int deltaFrameCount;
	private int frameCount;
	private double timer;
	private List<DebugStatTag<?>> mTags;
	private transient FontUnit mConsoleFont;
	private boolean mIsOpen;

	private float mTagLineHeight = 20.f;
	private transient ScrollBarContentRectangle mContentRectangle;
	private transient ScrollBar mScrollBar;
	private float mMouseTimer;

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
		mSpriteBatch = new SpriteBatch();

		mContentRectangle = new ScrollBarContentRectangle(this);
		mScrollBar = new ScrollBar(this, mContentRectangle);

		createStandardTags();
	}

	private void createStandardTags() {
		mTags.add(new DebugStatTagCaption(-1, "App:"));
		mTags.add(new DebugStatTagFloat(TAG_ID_FPS, "FPS", 0, false));
		mTags.add(new DebugStatTagString(TAG_ID_TIMING, "Timing", ""));
		mTags.add(new DebugStatTagString(TAG_ID_TIMESTEP, "Timestep", ""));
		mTags.add(new DebugStatTagString(TAG_ID_RES, "Resolution", ""));
		// mTags.add(new DebugStatTagFloat(-1, "Ram Used", 0, false));
		// mTags.add(new DebugStatTagFloat(-1, "Ram Free", 0, false));
		// mTags.add(new DebugStatTagFloat(TAG_ID_VRAM, "VRam", 0, false));

		mTags.add(new DebugStatTagCaption(-1, "Graphics:"));
		mTags.add(new DebugStatTagInt(TAG_ID_DRAWCALLS, "Draw Calls", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_VERTS, "Verts", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_TRIS, "Tris", 0));

		mTags.add(new DebugStatTagInt(TAG_ID_BATCH_OBJECTS, "Batch Objects", 0, false));
		// mTags.add(new DebugStatTagInt(TAG_ID_VBO, "VBOs", 0, false));
		// mTags.add(new DebugStatTagInt(TAG_ID_VB_UPLOADS, "VBs", 0));
		// mTags.add(new DebugStatTagInt(TAG_ID_IB_UPLOADS, "IBs", 0));
		mTags.add(new DebugStatTagInt(TAG_ID_TEXTURES, "Textures ", 0, false));
		mTags.add(new DebugStatTagInt(TAG_ID_RENDERTEXTURES, "Render Textures", 0, false));

		mTags.add(new DebugStatTagCaption(-1, "Audio:"));
		mTags.add(new DebugStatTagCaption(-1, "Custom:"));
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats loading GL content");

		mCoreSpritesheet = pResourceManager.spriteSheetManager().coreSpritesheet();
		mConsoleFont = pResourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME);

		mSpriteBatch.loadResources(pResourceManager);
	}

	public void unloadResources() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mSpriteBatch.unloadResources();

		mConsoleFont = null;
		mCoreSpritesheet = null;
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

		if (mIsOpen == false)
			return;

		final boolean lMouseOverWindow = intersectsAA(pCore.HUD().getMouseCameraSpace());
		if (lMouseOverWindow) {
			if (pCore.input().mouse().tryAcquireMouseOverThisComponent((hashCode()))) {
				final float scrollAccelerationAmt = pCore.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		if (mScrollBar.handleInput(pCore, null)) {
			return;
		}
	}

	public void update(LintfordCore pCore) {
		if (mIsOpen == false)
			return;

		final var lHUDRectangle = pCore.HUD().boundingRectangle();
		final var lHeightOffset = Debug.debugManager().console().isOpen() ? 200f : 10f;
		final var lWidthOffset = Debug.debugManager().console().isOpen() ? 360f : 0f;

		y(lHUDRectangle.top() + lHeightOffset + INNER_CONTENT_MARGIN);
		x(lHUDRectangle.right() - w() - lWidthOffset - INNER_CONTENT_MARGIN);
		width(WINDOW_SIZE_WIDTH);
		height(WINDOW_SIZE_HEIGHT);

		mLastUpdateElapsed = pCore.appTime().elapsedTimeMilli();

		float lContentHeight = 0.f;
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			DebugStatTag<?> lTag = mTags.get(i);
			if (lTag instanceof DebugStatTagCaption) {
				lContentHeight += 5f; // before
				lContentHeight += 5f; // after
			} else {
			}

			lContentHeight += mTagLineHeight;
		}

		lContentHeight += INNER_CONTENT_PADDING * 2.f;

		mContentRectangle.h(lContentHeight);

		final String lSpace = " ";
		final String lDelimiter = "|";

		String lUElapsed = String.format(java.util.Locale.US, "%.2f", mLastUpdateElapsed);
		String lDElapsed = String.format(java.util.Locale.US, "%.2f", mLastDrawElapsed);
		String lTotalElapsed = "(" + String.format(java.util.Locale.US, "%.1f", pCore.appTime().totalTimeSeconds()) + "s)";

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		mStringBuilder.append(lUElapsed).append("/").append(lDElapsed).append(lSpace).append(lTotalElapsed).append(lSpace);

		((DebugStatTagString) getTagByID(TAG_ID_TIMING)).value = mStringBuilder.toString();

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		String lIsFixed = (pCore.isFixedTimeStep() ? "fixed" : "variable");
		String lIsRunningSlowly = (pCore.appTime().isRunningSlowly() ? "slow" : "");

		mStringBuilder.append(lIsFixed).append(lDelimiter).append(lIsRunningSlowly);

		((DebugStatTagString) getTagByID(TAG_ID_TIMESTEP)).value = mStringBuilder.toString();

		mScrollBar.update(pCore);
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

		if (!mIsOpen)
			return;

		mSpriteBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());

		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, this, -0.01f, ColorConstants.getColor(.05f, .05f, .05f, .95f));

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mContentRectangle.h() - this.h() > 0) {
			mContentRectangle.preDraw(pCore, mSpriteBatch, mCoreSpritesheet);
		}

		mSpriteBatch.begin(pCore.HUD());
		mConsoleFont.begin(pCore.HUD());

		float lTagPosY = y() + mScrollBar.currentYPos();
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			DebugStatTag<?> lTag = mTags.get(i);
			if (lTag instanceof DebugStatTagCaption) {
				lTagPosY += 5f;
				lTag.draw(mConsoleFont, x + 5f, lTagPosY);
				lTagPosY += 5f;
			} else {
				lTag.draw(mConsoleFont, x + 15f, lTagPosY);
			}

			lTagPosY += mTagLineHeight;
		}

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mContentRectangle.h() - this.h() > 0) {
			mContentRectangle.postDraw(pCore);
		}
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

	// --------------------------------------
	// Inherited Methods
	// --------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseTimer < 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseTimer = IProcessMouseInput.MOUSE_COOL_TIME_TIME;
	};

	@Override
	public Rectangle contentDisplayArea() {
		return this;
	}

	@Override
	public ScrollBarContentRectangle fullContentArea() {
		return mContentRectangle;
	}
}
