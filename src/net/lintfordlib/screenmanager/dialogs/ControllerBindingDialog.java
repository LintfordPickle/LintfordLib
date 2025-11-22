package net.lintfordlib.screenmanager.dialogs;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.textures.CoreTextureNames;
import net.lintfordlib.core.input.gamepad.GamepadInputCodes;
import net.lintfordlib.renderers.ZLayers;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.Screen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.entries.input.MenuGamepadInputMapEntry;
import net.lintfordlib.screenmanager.layouts.FloatingLayout;

public class ControllerBindingDialog extends BaseDialog {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final int BUTTON_CANCEL = 101;

	// --------------------------------------
	// Variables
	// --------------------------------------

	protected FloatingLayout mFLoatingLayout;
	private MenuEntry mCancelEntry;
	private float mFlashTimer;
	private MenuGamepadInputMapEntry mBindingEntry;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public MenuEntry cancelEntry() {
		return mCancelEntry;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public ControllerBindingDialog(ScreenManager screenManager, Screen parentScreen, MenuGamepadInputMapEntry bindingEntry) {
		super(screenManager, parentScreen, "");

		mFLoatingLayout = new FloatingLayout(this);

		mCancelEntry = new MenuEntry(screenManager, this, "Cancel");
		mCancelEntry.registerClickListener(this, BUTTON_CANCEL);

		mCancelEntry.gamepadMenuIcon.focusHintEnabled(false);

		mCancelEntry.contextHintState.buttonA = false;
		mCancelEntry.contextHintState.buttonB = false;
		mCancelEntry.contextHintState.keyReturn = false;

		mFLoatingLayout.addMenuEntry(mCancelEntry);

		addLayout(mFLoatingLayout);

		mIsPopup = true;
		mShowBackgroundScreens = true;

		mShowContextualKeyHints = false;

		setDisplayAreaDimensions(200, 300);

		mBindingEntry = bindingEntry;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void handleInput(LintfordCore core) {
		super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		// Because we are using a floating layout for the dialog, we need to manually place the buttons.

		mFlashTimer += core.appTime().elapsedTimeMilli() * 0.001f;

		final var dialogArea = mDialogArea;

		mCancelEntry.width(150);
		mCancelEntry.setPosition(dialogArea.centerX() - mCancelEntry.width() / 2, dialogArea.centerY() + 40);
	}

	@Override
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.ACTIVE || mScreenState == ScreenState.TRANSITION_STARTING || mScreenState == ScreenState.TRANSITION_SLEEPING)
			return;

		if (!mResourcesLoaded)
			return;

		mMenuTitle = "Gamepad Button Binding";

		final var inputCodeName = GamepadInputCodes.getLintfordCodeName(mBindingEntry.inputCodeUid);

		final var gamepad = mBindingEntry.activeGamepad();
		final var gamepadName = gamepad.name();

		mMessageString = "Binding [" + inputCodeName + "] on gamepad '" + gamepadName + "'";

		setDisplayAreaDimensions(500, 200);

		final float lZDepth = ZLayers.LAYER_SCREENMANAGER + 0.05f;
		final float lWindowWidth = core.HUD().boundingRectangle().width();
		final float lWindowHeight = core.HUD().boundingRectangle().height();

		final var lSpriteBatch = mParentScreen.spriteBatch();

		if (mDarkenBackground) {
			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(0.f, 0.f, 0.f, .7f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_WHITE, -lWindowWidth * 0.5f, -lWindowHeight * 0.5f, lWindowWidth, lWindowHeight, ZLayers.LAYER_SCREENMANAGER);
			lSpriteBatch.end();
		}

		final float TILE_SIZE = 32f;
		if (mDrawBackground) {
			final float x = mDialogArea.left();
			final float y = mDialogArea.top();
			final float w = mDialogArea.width();
			final float h = mDialogArea.height();

			lSpriteBatch.begin(core.HUD());
			lSpriteBatch.setColorRGBA(1.f, 1.f, 1.f, 1.f);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_LEFT, x, y, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_MID, x + TILE_SIZE, y, w - 64, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_TOP_RIGHT, x + w - 32, y, TILE_SIZE, TILE_SIZE, lZDepth);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_LEFT, x, y + 32, TILE_SIZE, h - 64, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_CENTER, x + TILE_SIZE, y + 32, w - 64, h - 64, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_MID_RIGHT, x + w - 32, y + 32, TILE_SIZE, h - 64, lZDepth);

			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_LEFT, x, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_MID, x + TILE_SIZE, y + h - 32, w - 64, TILE_SIZE, lZDepth);
			lSpriteBatch.draw(mCoreSpritesheet, CoreTextureNames.TEXTURE_PANEL_3X3_00_BOTTOM_RIGHT, x + w - 32, y + h - 32, TILE_SIZE, TILE_SIZE, lZDepth);
			lSpriteBatch.end();
		}

		if (mMenuTitle != null && mMenuTitle.length() > 0) {
			mMenuHeaderFont.begin(core.HUD());
			mMenuHeaderFont.setTextColor(screenColor);
			mMenuHeaderFont.drawText(mMenuTitle, mDialogArea.left() + TEXT_HORIZONTAL_PADDING, mDialogArea.top() + mMenuFont.fontHeight(), lZDepth, 1.f);
			mMenuHeaderFont.end();
		}

		mMenuFont.begin(core.HUD());
		mMenuFont.setTextColorRGBA(1.f, 1.f, 1.f, 1.f);
		mMenuFont.drawText(mMessageString, mDialogArea.left() + TEXT_HORIZONTAL_PADDING + 5, mDialogArea.top() + 48f, lZDepth, 1f, DEFAULT_DIALOG_WIDTH - 64.f);
		if (mFlashTimer % 2.f > .5f) {
			final var boundKeyText = "<Press Gamepad " + inputCodeName + ">";
			final var boundKeyTextWidth = mMenuFont.getStringWidth(boundKeyText, 1f);
			mMenuFont.drawText(boundKeyText, -boundKeyTextWidth / 2, 0, 1f, 1f);

		}
		mMenuFont.end();

		final int lCount = mLayouts.size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(core, lZDepth + (i * 0.001f));
		}
	}

	@Override
	protected void handleOnClick() {
		exitScreen();
	}

	@Override
	public void exitScreen() {
		super.exitScreen();

		mBindingEntry.cancelBinding();

	}
}
