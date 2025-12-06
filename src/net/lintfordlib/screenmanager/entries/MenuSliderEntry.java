package net.lintfordlib.screenmanager.entries;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;

public class MenuSliderEntry extends MenuEntry {

	private static final long serialVersionUID = -8125859270010821953L;

	private static final String SEPARATOR_STRING = ":";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private Rectangle mLeftButton;
	private Rectangle mRightButton;
	private String mLabel;
	private String mUnit = "%";
	private int mValue;
	private int mLowerBound;
	private int mUpperBound;
	private boolean mButtonsEnabled;
	private boolean mShowValueEnabled;
	private boolean mShowGuideValuesEnabled;
	private boolean mShowUnit;
	private float mBarPosX;
	private float mBarWidth;
	private int mStep;
	private boolean mTrackingClick;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public int step() {
		return mStep;
	}

	public void step(int step) {
		mStep = step;
	}

	public void showValueGuides(boolean newValue) {
		mShowGuideValuesEnabled = newValue;
	}

	public boolean showValueGuides() {
		return mShowGuideValuesEnabled;
	}

	public void showValueUnit(boolean newValue) {
		mShowUnit = newValue;
	}

	public boolean showValueUnit() {
		return mShowUnit;
	}

	public void setValueUnit(String valueUnit) {
		mUnit = valueUnit;
	}

	public void showValue(boolean newValue) {
		mShowValueEnabled = newValue;
	}

	public boolean showValue() {
		return mShowValueEnabled;
	}

	public void buttonsEnabled(boolean newValue) {
		mButtonsEnabled = newValue;
	}

	public boolean buttonsEnabled() {
		return mButtonsEnabled;
	}

	public void label(String newLabel) {
		mLabel = newLabel;
	}

	public String label() {
		return mLabel;
	}

	public int getCurrentValue() {
		return mValue;
	}

	public void setBounds(int lowBound, int highBound, int stepSize) {
		mLowerBound = lowBound;
		mUpperBound = highBound;

		mStep = stepSize;

		setValue(highBound - lowBound / 2);
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public MenuSliderEntry(ScreenManager screenManager, MenuScreen parentScreen) {
		this(screenManager, parentScreen, "Label");
	}

	public MenuSliderEntry(ScreenManager screenManager, MenuScreen parentScreen, String label) {
		super(screenManager, parentScreen, "");
		mLabel = label;

		mLeftButton = new Rectangle(0, 0, 32, 32);
		mRightButton = new Rectangle(0, 0, 32, 32);

		mStep = 1;
	}

	// --------------------------------------
	// Core Methods
	// --------------------------------------

	@Override
	public boolean onHandleMouseInput(LintfordCore core) {

		if (!intersectsAA(core.HUD().getMouseCameraSpace()) || !core.input().mouse().isMouseOverThisComponent(hashCode()))
			return false;

		if (core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
			if (mEnabled) {
				mStep = 1;
				if (mLeftButton.intersectsAA(core.HUD().getMouseCameraSpace())) {
					setValue(mValue - mStep);
				} else if (mRightButton.intersectsAA(core.HUD().getMouseCameraSpace())) {
					setValue(mValue + mStep);
				} else {
					mTrackingClick = true;
				}

				final var lScreenOffset = mParentScreen.screenPositionOffset();

				if (mTrackingClick && core.input().mouse().tryAcquireMouseLeftClick(hashCode())) {
					mValue = (int) MathHelper.scaleToRange(core.HUD().getMouseCameraSpace().x - (lScreenOffset.x + mBarPosX + 8), 0, mBarWidth - 32 - 16, mLowerBound, mUpperBound);
					mValue = MathHelper.clampi(mValue, mLowerBound, mUpperBound);

					onClick(core.input());

				} else {
					mTrackingClick = false;
				}

				return mTrackingClick;

			}

		} else {
			if (!mHasFocus)
				mParentScreen.setFocusOnEntry(this);

			mTrackingClick = false;
		}

		if (mToolTipEnabled)
			mToolTipTimer += core.appTime().elapsedTimeMilli();

		return false;
	}

	@Override
	public boolean onHandleKeyboardInput(LintfordCore core) {
		// slider left/right handled in oNNavigationLeft/Right methods
		return false;
	}

	@Override
	public boolean onHandleGamepadInput(LintfordCore core) {
		// slider left/right handled in oNNavigationLeft/Right methods
		return false;
	}

	@Override
	public void update(LintfordCore core, MenuScreen screen) {
		super.update(core, screen);

		mLeftButton.setPosition(mX + mW / 2 + 8, mY);
		mRightButton.setPosition(mX + mW - 32, mY);

		mBarPosX = mX + mW / 2 + mLeftButton.width() + 16;
		mBarWidth = mW / 2 - 48;
	}

	@Override
	public void draw(LintfordCore core, Screen screen, float parentZDepth) {
		final var textFont = mParentScreen.font();
		final var spriteBatch = mParentScreen.spriteBatch();
		final var uiTextScale = mParentScreen.uiTextScale();

		final var labelWidth = textFont.getStringWidth(mLabel, uiTextScale);
		final var separatorHalfWidth = textFont.getStringWidth(SEPARATOR_STRING, uiTextScale) * 0.5f;
		final var labelHeight = textFont.getStringHeight(mLabel, uiTextScale);

		final var screenOffset = screen.screenPositionOffset();
		final var parentScreenAlpha = screen.screenColor.a;

		if (mHasFocus && mEnabled)
			renderHighlight(core, screen, true, spriteBatch);

		final var tileSize = Math.min(32, mH);
		mButtonsEnabled = true;
		if (mButtonsEnabled) {
			spriteBatch.begin(core.HUD());
			final float lArrowButtonSize = tileSize;

			spriteBatch.setColor(entryColor);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_LEFT, screenOffset.x + mLeftButton.x(), screenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ);
			spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_RIGHT, screenOffset.x + mRightButton.x(), screenOffset.y + mY, lArrowButtonSize, lArrowButtonSize, mZ);

			spriteBatch.end();
		}

		final var lCaretPos = MathHelper.scaleToRange(mValue, mLowerBound, mUpperBound, 0, mBarWidth - 32 - 32);
		// Draw the slider bar and caret
		spriteBatch.begin(core.HUD());
		spriteBatch.setColor(entryColor);

		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_LEFT, screenOffset.x + mBarPosX, screenOffset.y + mY, tileSize, tileSize, mZ);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_MID, screenOffset.x + mBarPosX + tileSize, screenOffset.y + mY, mBarWidth - 64 - tileSize, tileSize, mZ);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_RIGHT, screenOffset.x + mBarPosX + mBarWidth - 64, screenOffset.y + mY, tileSize, tileSize, mZ);
		spriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_CONTROL_SLIDER_HORIZONTAL_NUBBLE, screenOffset.x + mBarPosX + lCaretPos, screenOffset.y + mY, tileSize, tileSize, mZ);
		spriteBatch.end();

		// draw the label to the left and the value //
		textFont.begin(core.HUD());
		textFont.setTextColorRGBA(1.f, 1.f, 1.f, parentScreenAlpha);
		textFont.drawText(mLabel, screenOffset.x + mX + mW / 2 - labelWidth - 10 - separatorHalfWidth, screenOffset.y + mY + mH / 2f - labelHeight / 2f, mZ, uiTextScale, -1);
		textFont.drawText(SEPARATOR_STRING, screenOffset.x + mX + mW / 2 - separatorHalfWidth, screenOffset.y + mY + mH / 2f - labelHeight / 2f, mZ, uiTextScale, -1);

		if (mShowValueEnabled) {
			var lValueString = String.valueOf(mValue);
			if (mShowUnit && mUnit != null && lValueString.length() > 0) {
				lValueString += mUnit;
			}

			final var lValueStringWidth = textFont.getStringWidth(lValueString, uiTextScale);

			if (mShowGuideValuesEnabled) {
				textFont.setTextColorRGBA(1.f, 1.f, 1.f, parentScreenAlpha * .5f);
				textFont.drawText(Integer.toString(mLowerBound), screenOffset.x + mBarPosX + 24, screenOffset.y + mY + mH * .5f - textFont.fontHeight() * .5f, mZ, 1f);
			}

			textFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
			textFont.drawText(lValueString, screenOffset.x + mBarPosX + mBarWidth * .5f - lValueStringWidth * .5f, screenOffset.y + mY + mH * .5f - textFont.fontHeight() * .5f, mZ, uiTextScale);

			if (mShowGuideValuesEnabled) {
				final float lUpperBoundStringWidth = textFont.getStringWidth(Integer.toString(mUpperBound));
				textFont.setTextColorRGBA(1.f, 1.f, 1.f, parentScreenAlpha * .5f);
				textFont.drawText(Integer.toString(mUpperBound), screenOffset.x + mBarPosX + mBarWidth - lUpperBoundStringWidth - 48, screenOffset.y + mY + mH * .5f - textFont.fontHeight() * .5f, mZ, 1f);
			}
		}

		textFont.end();

		if (mShowInfoIcon)
			drawInfoIcon(core, spriteBatch, mInfoIconDstRectangle, entryColor.a);

		if (mShowWarnIcon)
			drawWarningIcon(core, spriteBatch, mWarnIconDstRectangle, entryColor.a);

		if (!mEnabled)
			drawdisabledBlackOverbar(core, spriteBatch, entryColor.a);

		drawDebugCollidableBounds(core, spriteBatch);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void onDeactivation(InputManager inputManager) {
		super.onDeactivation(inputManager);

	}

	@Override
	public void onClick(InputManager inputManager) {
		if (mClickListener != null)
			mClickListener.menuEntryOnClick(inputManager, mMenuEntryID);
	}

	public void setValue(int newValue) {
		if (newValue < mLowerBound)
			newValue = mLowerBound;

		if (newValue > mUpperBound)
			newValue = mUpperBound;

		mValue = newValue;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = 50;
	}

	@Override
	public boolean onNavigationLeft(LintfordCore core) {
		if (mValue - mStep < mLowerBound) {
			mValue = mLowerBound;
			return false; // let the nav left propergate (we didn
		} else {
			mValue -= mStep;
			return true;
		}
	}

	@Override
	public boolean onNavigationRight(LintfordCore core) {
		if (mValue + mStep > mUpperBound) {
			mValue = mUpperBound;
			return false; // let the nav right propergate (we didn
		} else {
			mValue += mStep;
			return true;
		}
	}

}
