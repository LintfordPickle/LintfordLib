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
	public static final int TAG_ID_TOTAL_ELAPSED_TIME_MS = 14;
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
	private int mDeltaFrameCount;
	private int mFrameCount;
	private double mTimer;
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

	public void isOpen(boolean newValue) {
		mIsOpen = newValue;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugStats(final Debug debugManager) {
		mDebugManager = debugManager;

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
		mTags.add(new DebugStatTagString(TAG_ID_TOTAL_ELAPSED_TIME_MS, "Up (ms)", ""));
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

	public void loadResources(ResourceManager resourceManager) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats loading GL content");

		mCoreSpritesheet = resourceManager.spriteSheetManager().coreSpritesheet();
		mConsoleFont = resourceManager.fontManager().getFontUnit(BitmapFontManager.SYSTEM_FONT_CONSOLE_NAME);

		mSpriteBatch.loadResources(resourceManager);
	}

	public void unloadResources() {
		if (!mDebugManager.debugManagerEnabled())
			return;

		Debug.debugManager().logger().v(getClass().getSimpleName(), "DebugStats unloading GL content");

		mSpriteBatch.unloadResources();

		mConsoleFont = null;
		mCoreSpritesheet = null;
	}

	public void preUpdate(LintfordCore core) {
		// reset all tags
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			mTags.get(i).reset();
		}
	}

	public void handleInput(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F3)) {
			mIsOpen = !mIsOpen;
		}

		if (mIsOpen == false)
			return;

		final boolean lMouseOverWindow = intersectsAA(core.HUD().getMouseCameraSpace());
		if (lMouseOverWindow) {
			if (core.input().mouse().tryAcquireMouseOverThisComponent((hashCode()))) {
				final float scrollAccelerationAmt = core.input().mouse().mouseWheelYOffset() * 250.0f;
				mScrollBar.scrollRelAcceleration(scrollAccelerationAmt);
			}
		}

		if (mScrollBar.handleInput(core, null)) {
			return;
		}
	}

	public void update(LintfordCore core) {
		if (mIsOpen == false)
			return;

		final var lHUDRectangle = core.HUD().boundingRectangle();
		final var lHeightOffset = Debug.debugManager().console().isOpen() ? 200f : 10f;
		final var lWidthOffset = Debug.debugManager().console().isOpen() ? 360f : 0f;

		y(lHUDRectangle.top() + lHeightOffset + INNER_CONTENT_MARGIN);
		x(lHUDRectangle.right() - width() - lWidthOffset - INNER_CONTENT_MARGIN);
		width(WINDOW_SIZE_WIDTH);
		height(WINDOW_SIZE_HEIGHT);

		mLastUpdateElapsed = core.appTime().elapsedTimeMilli();

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

		mContentRectangle.height(lContentHeight);

		final String lDelimiter = "|";

		String lUpdateElapsed = String.format(java.util.Locale.US, "%.2f", mLastUpdateElapsed);
		String lDrawElapsed = String.format(java.util.Locale.US, "%.2f", mLastDrawElapsed);
		String lTotalElapsed = String.format(java.util.Locale.US, "%.2f", core.appTime().totalTimeSeconds());

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		mStringBuilder.append(lUpdateElapsed).append("/").append(lDrawElapsed);

		((DebugStatTagString) getTagByID(TAG_ID_TIMING)).mValue = mStringBuilder.toString();

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		mStringBuilder.append(lTotalElapsed);
		((DebugStatTagString) getTagByID(TAG_ID_TOTAL_ELAPSED_TIME_MS)).mValue = mStringBuilder.toString();

		if (mStringBuilder.length() > 0)
			mStringBuilder.delete(0, mStringBuilder.length());

		String lIsFixed = (core.isFixedTimeStep() ? "fixed" : "variable");
		String lIsRunningSlowly = (core.appTime().isRunningSlowly() ? lDelimiter + "slow" : "");

		mStringBuilder.append(lIsFixed).append(lIsRunningSlowly);

		((DebugStatTagString) getTagByID(TAG_ID_TIMESTEP)).mValue = mStringBuilder.toString();

		mScrollBar.update(core);
	}

	public void draw(LintfordCore core) {
		if (!mDebugManager.debugManagerEnabled())
			return;

		mDeltaFrameCount++;

		mTimer += core.appTime().elapsedTimeMilli();
		if (mTimer > 1000) {
			mFrameCount = mDeltaFrameCount;
			mDeltaFrameCount = 0;
			mTimer -= 1000;
		}

		Debug.debugManager().stats().setTagValue(DebugStats.TAG_ID_FPS, mFrameCount);

		mLastDrawElapsed = core.appTime().elapsedTimeMilli();

		if (!mIsOpen)
			return;

		mSpriteBatch.begin(core.HUD());
		mConsoleFont.begin(core.HUD());

		mSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, this, -0.01f, ColorConstants.getColor(.05f, .05f, .05f, .95f));

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mContentRectangle.height() - this.height() > 0) {
			mContentRectangle.preDraw(core, mSpriteBatch, mCoreSpritesheet);
		}

		mSpriteBatch.begin(core.HUD());
		mConsoleFont.begin(core.HUD());

		float lTagPosY = y() + mScrollBar.currentYPos();
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			DebugStatTag<?> lTag = mTags.get(i);
			if (lTag instanceof DebugStatTagCaption) {
				lTagPosY += 5f;
				lTag.draw(mConsoleFont, mX + 5f, lTagPosY);
				lTagPosY += 5f;
			} else {
				lTag.draw(mConsoleFont, mX + 15f, lTagPosY);
			}

			lTagPosY += mTagLineHeight;
		}

		mSpriteBatch.end();
		mConsoleFont.end();

		if (mContentRectangle.height() - this.height() > 0) {
			mContentRectangle.postDraw(core);
		}
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setTagValue(int tagUid, int pValue) {
		DebugStatTag<?> lTag = getTagByID(tagUid);
		if (lTag != null && lTag instanceof DebugStatTagInt) {
			((DebugStatTagInt) lTag).mValue = pValue;
		}
		if (lTag != null && lTag instanceof DebugStatTagFloat) {
			((DebugStatTagFloat) lTag).mValue = (float) pValue;
		}
	}

	public void setTagValue(int tagUid, float pValue) {
		DebugStatTag<?> lTag = getTagByID(tagUid);
		if (lTag != null && lTag instanceof DebugStatTagFloat) {
			((DebugStatTagFloat) lTag).mValue = pValue;
		}
	}

	public void setTagValue(int tagUid, String pValue) {
		DebugStatTag<?> lTag = getTagByID(tagUid);
		if (lTag != null && lTag instanceof DebugStatTagString) {
			((DebugStatTagString) lTag).mValue = pValue;
		}
	}

	public void incTag(int tagUid) {
		incTag(tagUid, 1);
	}

	public void incTag(int tagUid, float amount) {
		DebugStatTag<?> lTag = getTagByID(tagUid);
		if (lTag instanceof DebugStatTagInt) {
			DebugStatTagInt lIntTag = (DebugStatTagInt) lTag;
			lIntTag.mValue += (int) amount;
		} else if (lTag instanceof DebugStatTagFloat) {
			DebugStatTagFloat lIntTag = (DebugStatTagFloat) lTag;
			lIntTag.mValue += amount;
		}
	}

	public void decTag(int tagUid) {
		decTag(tagUid, 1);
	}

	public void decTag(int tagUid, int amount) {
		DebugStatTag<?> lTag = getTagByID(tagUid);
		if (lTag instanceof DebugStatTagInt) {
			DebugStatTagInt lIntTag = (DebugStatTagInt) lTag;
			lIntTag.mValue -= amount;
		} else if (lTag instanceof DebugStatTagFloat) {
			DebugStatTagFloat lIntTag = (DebugStatTagFloat) lTag;
			lIntTag.mValue -= amount;
		}
	}

	public DebugStatTag<?> getTagByID(int tagUid) {
		final int lTagCount = mTags.size();
		for (int i = 0; i < lTagCount; i++) {
			if (mTags.get(i).mUid == tagUid)
				return mTags.get(i);
		}

		return null;
	}

	public void addCustomStatTag(DebugStatTag<?> customTag) {
		if (customTag == null)
			return;

		if (!mTags.contains(customTag)) {
			mTags.add(customTag);
		}
	}

	public void removeCustomStatTag(DebugStatTag<?> customTag) {
		if (customTag == null)
			return;

		if (mTags.contains(customTag)) {
			mTags.remove(customTag);
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
