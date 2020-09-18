package net.lintford.library.screenmanager.entries;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatchPCT;
import net.lintford.library.core.input.InputManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout;

public class MenuEnumEntry extends MenuEntry {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 2194989174357016245L;

	// --------------------------------------
	// Variables
	// --------------------------------------

	private String mLabel;
	private boolean mIsChecked;
	private final String mSeparator = " : ";
	private List<String> mItems;
	private int mSelectedIndex;
	private Texture mUITexture;
	private boolean mEnableScaleTextToWidth;

	private boolean mButtonsEnabled;
	private Rectangle mLeftButtonRectangle;
	private Rectangle mRightButtonRectangle;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean scaleTextToWidth() {
		return mEnableScaleTextToWidth;
	}

	public void scaleTextToWidth(boolean pNewValue) {
		mEnableScaleTextToWidth = pNewValue;
	}

	public void setListener(EntryInteractions pListener) {
		mClickListener = pListener;
	}

	public void setButtonsEnabled(boolean pNewValue) {
		mButtonsEnabled = pNewValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public int selectedEntry() {
		return mSelectedIndex;
	}

	public String selectedEntryName() {
		return mItems.get(mSelectedIndex);

	}

	public void setSelectedEntry(int pIndex) {
		if (pIndex < 0)
			pIndex = 0;
		if (pIndex >= mItems.size())
			pIndex = mItems.size() - 1;
		mSelectedIndex = pIndex;
	}

	public void setSelectedEntry(String pName) {
		final int COUNT = mItems.size();
		for (int i = 0; i < COUNT; i++) {
			if (mItems.get(i).equals(pName)) {
				mSelectedIndex = i;
				return;
			}
		}

	}

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean pNewValue) {
		mIsChecked = pNewValue;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuEnumEntry(ScreenManager pScreenManager, BaseLayout pParentLayout, String pLabel) {
		super(pScreenManager, pParentLayout, "");

		mLabel = pLabel;
		mItems = new ArrayList<>();
		mSelectedIndex = 0;

		mLeftButtonRectangle = new Rectangle(0, 0, 32, 32);
		mRightButtonRectangle = new Rectangle(0, 0, 32, 32);

		mHighlightOnHover = false;
		mDrawBackground = false;

		mEnableScaleTextToWidth = true;

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mUITexture = pResourceManager.textureManager().textureCore();

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mUITexture = null;

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (mButtonsEnabled) { // left and right buttons
			if (mLeftButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
				if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
					mSelectedIndex--;
					if (mSelectedIndex < 0) {
						mSelectedIndex = mItems.size() - 1;
					}

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					return true;
				}

				else if (mRightButtonRectangle.intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					return true;
				}

			}

		}

		if (intersectsAA(pCore.HUD().getMouseCameraSpace()) && pCore.input().mouse().isMouseOverThisComponent(hashCode())) {
			if (pCore.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {
				if (mEnabled) {

					mSelectedIndex++;
					if (mSelectedIndex >= mItems.size()) {
						mSelectedIndex = 0;
					}

					// TODO: Play a menu click sound

					mParentLayout.parentScreen().setFocusOn(pCore, this, true);

					if (mClickListener != null) {
						mClickListener.menuEntryChanged(this);
					}

					mIsChecked = !mIsChecked;

				}

			} else {
				hasFocus(true);

			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.appTime().elapsedTimeMilli();
			}

			return true;

		} else {
			hoveredOver(false);
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void updateStructure() {
		super.updateStructure();

		w = Math.min(mParentLayout.w() - 50f, MENUENTRY_MAX_WIDTH);

	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		// Update the button positions to line up with this entry
		mLeftButtonRectangle.setPosition(x + w / 2 + 16, y);
		mRightButtonRectangle.setPosition(x + w - 32, y);

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		super.draw(pCore, pScreen, pIsSelected, pParentZDepth);

		MenuScreen lParentScreen = mParentLayout.parentScreen();
		FontUnit lFontBitmap = lParentScreen.font();
		final float luiTextScale = mScreenManager.UIHUDController().uiTextScaleFactor();
		if (lFontBitmap == null)
			return;

		final float lTextWidth = lFontBitmap.bitmap().getStringWidth(mLabel, luiTextScale);

		float lAdjustedScaleW = luiTextScale;
		if (mEnableScaleTextToWidth && w / 2 < lTextWidth && lTextWidth > 0)
			lAdjustedScaleW = (w / 2) / lTextWidth;

		final float lTextHeight = lFontBitmap.bitmap().getStringHeight(mLabel, luiTextScale);
		final float lSeparatorHalfWidth = lFontBitmap.bitmap().getStringWidth(mSeparator, luiTextScale) * 0.5f;

		final TextureBatchPCT lTextureBatch = mParentLayout.parentScreen().rendererManager().uiTextureBatch();

		// Draw the left/right buttons
		lTextureBatch.begin(pCore.HUD());
		final float ARROW_BUTTON_SIZE = 32;
		final float ARROW_PADDING_Y = mLeftButtonRectangle.h() - ARROW_BUTTON_SIZE;

		// Render the two arrows either side of the enumeration options
		if (mButtonsEnabled) {
			lTextureBatch.draw(mUITexture, 0, 224, 32, 32, mLeftButtonRectangle.x(), mLeftButtonRectangle.y() + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, 0f, 1f, 1f, 1f, 1f);
			lTextureBatch.draw(mUITexture, 32, 224, 32, 32, mRightButtonRectangle.x(), mRightButtonRectangle.y() + ARROW_PADDING_Y, ARROW_BUTTON_SIZE, ARROW_BUTTON_SIZE, 0f, 1f, 1f, 1f, 1f);

		}

		lTextureBatch.end();

		final float lR = lParentScreen.r();
		final float lG = lParentScreen.g();
		final float lB = lParentScreen.b();
		final float lA = lParentScreen.a();

		lFontBitmap.begin(pCore.HUD());
		lFontBitmap.draw(mLabel, x + w / 2 - 10 - (lTextWidth * lAdjustedScaleW) - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, lAdjustedScaleW, -1);
		lFontBitmap.draw(mSeparator, x + w / 2 - lSeparatorHalfWidth, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, luiTextScale, -1);

		// Render the items
		if (mItems.size() > 0) {
			String lCurItem = mItems.get(mSelectedIndex);

			final float EntryWidth = lFontBitmap.bitmap().getStringWidth(lCurItem, luiTextScale);

			lFontBitmap.draw(lCurItem, x + (w / 6 * 4.65f) - EntryWidth / 2, y + h / 2 - lTextHeight * 0.5f, pParentZDepth, lR, lG, lB, lA, luiTextScale, -1);
		}
		lFontBitmap.end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onClick(InputManager pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;

		} else {
			mFocusLocked = false; // no lock if not focused

		}
	}

	public void addItem(String pItem) {
		if (pItem == null)
			return;

		if (!mItems.contains(pItem)) {
			mItems.add(pItem);
		}
	}

	public void addItems(String... pItems) {
		if (pItems == null)
			return;

		int pSize = pItems.length;
		for (int i = 0; i < pSize; i++) {
			if (!mItems.contains(pItems[i])) {
				mItems.add(pItems[i]);
			}
		}
	}
}