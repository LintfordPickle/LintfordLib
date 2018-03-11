package net.lintford.library.screenmanager.entries;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.ResourceManager;
import net.lintford.library.core.graphics.fonts.FontManager.FontUnit;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.core.graphics.textures.texturebatch.TextureBatch;
import net.lintford.library.core.input.InputState;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.Screen;
import net.lintford.library.screenmanager.ScreenManager;

public class MenuSliderEntry extends MenuEntry {

	// --------------------------------------
	// Variables
	// --------------------------------------

	private AARectangle mDownButton;
	private AARectangle mUpButton;
	private TextureBatch mTextureBatch;
	private String mLabel;
	private int mValue;
	private int mLowerBound;
	private int mUpperBound;
	private int mStep;
	private float mValuePosX;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void label(String pNewLabel) {
		mLabel = pNewLabel;
	}

	public String label() {
		return mLabel;
	}

	public int getCurrentValue() {
		return mValue;
	}

	public void setBounds(int pLow, int pHigh, int pStep) {
		mLowerBound = pLow;
		mUpperBound = pHigh;
		mStep = pStep;
		setValue(pHigh - pLow / 2);

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuSliderEntry(ScreenManager pScreenManager, MenuScreen pParentScreen) {
		super(pScreenManager, pParentScreen, "");

		mLabel = "Label:";

		mDownButton = new AARectangle(0, 0, 32, 32);
		mUpButton = new AARectangle(0, 0, 32, 32);

		mTextureBatch = new TextureBatch();

	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

		mTextureBatch.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

		mTextureBatch.unloadGLContent();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {

		if (mHasFocus) {

		} else {
			mFocusLocked = false; // no lock if not focused
		}

		if (intersects(pCore.HUD().getMouseCameraSpace())) {
			if (pCore.input().mouseTimedLeftClick()) {
				if (mEnabled) {

					// TODO: Play menu click sound
					if (mDownButton.intersects(pCore.HUD().getMouseCameraSpace())) {
						setValue(mValue - mStep);
					} else if (mUpButton.intersects(pCore.HUD().getMouseCameraSpace())) {
						setValue(mValue + mStep);
					}

					mParentScreen.setFocusOn(pCore.input(), this, true);
					mParentScreen.setHoveringOn(this);

					pCore.input().setLeftMouseClickHandled();

				}
			} else {
				// mParentScreen.setHoveringOn(this);
				hasFocus(true);
			}

			// Check if tool tips are enabled.
			if (mToolTipEnabled) {
				mToolTipTimer += pCore.time().elapseGameTimeMilli();
			}

			return true;

		} else {
			mToolTipTimer = 0;
		}

		return false;
	}

	@Override
	public void update(LintfordCore pCore, MenuScreen pScreen, boolean pIsSelected) {
		super.update(pCore, pScreen, pIsSelected);

		mValuePosX = w / 2 + w / 4;

		mDownButton.x = x + mValuePosX - 32 - 16;
		mDownButton.y = y;

		mUpButton.x = x + mValuePosX + 16;
		mUpButton.y = y;

	}

	@Override
	public void draw(LintfordCore pCore, Screen pScreen, boolean pIsSelected, float pParentZDepth) {
		FontUnit lFont = mParentScreen.font();
		Texture lTexture = TextureManager.TEXTURE_CORE_UI;

		float valueWith = lFont.bitmap().getStringWidth("" + mValue);

		// draw the label to the left //
		lFont.begin(pCore.HUD());
		lFont.draw(mLabel, x + 2, y + 2, -2f, 1f);
		lFont.draw("" + mValue, x + mValuePosX - valueWith / 2, y + 2, -2f, 1f);
		lFont.end();

		// TODO: This should be a slider as well ..
		mTextureBatch.begin(pCore.HUD());
		mTextureBatch.draw(lTexture, 160, 0, 32, 32, mDownButton.x, mDownButton.y, 32, 32, -2f, 1f, 1f, 1f, 1f);
		mTextureBatch.draw(lTexture, 224, 0, 32, 32, mUpButton.x,   mUpButton.y,   32, 32, -2f, 1f, 1f, 1f, 1f);
		mTextureBatch.end();

	}

	@Override
	public void onClick(InputState pInputState) {
		super.onClick(pInputState);

		mHasFocus = !mHasFocus;
		if (mHasFocus) {
			mFocusLocked = true;
			System.out.println("locking focus");

		} else {
			mFocusLocked = false; // no lock if not focused
		}
	}

	public void setValue(int pNewValue) {
		if (pNewValue < mLowerBound)
			pNewValue = mLowerBound;

		if (pNewValue > mUpperBound)
			pNewValue = mUpperBound;

		mValue = pNewValue;

	}
}
