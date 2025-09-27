package net.lintfordlib.renderers.windows.components;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.geometry.Rectangle;
import net.lintfordlib.core.graphics.ColorConstants;
import net.lintfordlib.core.graphics.fonts.FontUnit;
import net.lintfordlib.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.InputManager;
import net.lintfordlib.core.input.keyboard.IUiInputKeyPressCallback;
import net.lintfordlib.core.rendering.SharedResources;

public class UiCheckBox extends UIWidget {

	// --------------------------------------
	// Constants
	// --------------------------------------

	private static final long serialVersionUID = 3637330515154931480L;

	private static final String NO_LABEL_TEXT = "unlabled";
	
	// --------------------------------------
	// Variables
	// --------------------------------------

	private transient boolean mHasFocus;
	private boolean mIsChecked;
	private transient Rectangle mBoxRectangle;
	private boolean mIsReadonly;
	private float mTextScale;
	private String mLabelText;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public boolean isChecked() {
		return mIsChecked;
	}

	public void isChecked(boolean newValue) {
		mIsChecked = newValue;
	}

	public String label() {
		return mLabelText;
	}

	public void label(String newLabel) {
		mLabelText = newLabel;
	}

	public void textScale(float newTextScale) {
		mTextScale = newTextScale;
	}

	public float textScale() {
		return mTextScale;
	}

	public boolean isReadonly() {
		return mIsReadonly;
	}

	public void isReadonly(boolean newValue) {
		mIsReadonly = newValue;
	}

	public boolean hasFocus() {
		return mHasFocus;
	}

	public void hasFocus(boolean v) {
		mHasFocus = v;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public UiCheckBox() {
		this(NO_LABEL_TEXT);
	}

	public UiCheckBox(String labelText) {
		mBoxRectangle = new Rectangle();

		mTextScale = 1.f;
		mW = 100;
		mH = 25.f;

		mLabelText = labelText;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public boolean handleInput(LintfordCore core) {
		if (mIsReadonly)
			return false;

		if (mBoxRectangle.intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this, 30)) {

				mIsChecked = !mIsChecked;

				if (mUiWidgetListenerCallback != null)
					mUiWidgetListenerCallback.widgetOnDataChanged(core.input(), mUiWidgetListenerUid);

			}
		}

		else if (intersectsAA(core.HUD().getMouseCameraSpace())) {
			if (core.input().mouse().tryAcquireMouseLeftClickTimed(hashCode(), this)) {

				onClick(core.input(), true);

				return true;
			}
		}

		return false;
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		final var lRectSize = 25;
		var xx = mX + mW * .75f - lRectSize / 2.f;

		mBoxRectangle.set(xx, mY + mH / 2 - lRectSize / 2.f, lRectSize, lRectSize);
	}

	@Override
	public void draw(LintfordCore core, SharedResources sharedResources, SpriteSheetDefinition coreSpritesheetDefinition, FontUnit textFont, float componentZDepth) {
		final float lTextHeight = textFont.fontHeight();

		if (mLabelText != null) {
			textFont.begin(core.HUD());
			textFont.setTextColor(ColorConstants.TextEntryColor);
			textFont.drawText(mLabelText, mX, mY + mH * .5f - lTextHeight * .5f * mTextScale, componentZDepth, mTextScale);
			textFont.end();
		}

		final var lSpriteBatch = sharedResources.uiSpriteBatch();

		lSpriteBatch.begin(core.HUD());
		lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
		lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_CHECKBOX_UNCHECKED, mBoxRectangle, componentZDepth);
		if (mIsChecked)
			lSpriteBatch.draw(coreSpritesheetDefinition, CoreTextureNames.TEXTURE_CHECKBOX_CHECKED, mBoxRectangle, componentZDepth);

		lSpriteBatch.end();
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setKeyUpdateListener(IUiInputKeyPressCallback keyUpdateListener, int keyListenerUid) {
		// ignore
	}

	public void onClick(InputManager inputState, boolean newFocus) {
		if (mIsReadonly)
			return;

		mHasFocus = newFocus;
	}

	@Override
	public void resetCoolDownTimer(float cooldownInMs) {
		mInputTimer = cooldownInMs;
	}

}
