package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.renderers.ZLayers;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.BUTTON_SIZE;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout.ANCHOR;
import net.lintford.library.screenmanager.layouts.BaseLayout;
import net.lintford.library.screenmanager.layouts.ListLayout;
import net.lintford.library.screenmanager.screens.VideoOptionsScreen;

/**
 * Used in the {@link VideoOptionsScreen} to revert the changes of a monitor change if the user doesn't interact with the dialog after the alloted amount of time.
 * 
 * @author Lintford
 *
 */
public class TimedConfirmationDialog extends BaseDialog {

	// --------------------------------------==============
	// Constants
	// --------------------------------------==============

	public static final int BUTTON_TIMED_CONFIRM_YES = 200;
	public static final int BUTTON_TIMED_CONFIRM_NO = 201;

	public static final int DEFAULT_WAIT_TIME = 15; // Seconds

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuEntry mConfirmEntry;
	private MenuEntry mCancelEntry;

	private float mTimeToWait;
	private float mTimer;
	private boolean mActive;

	private TimedDialogInterface mCallbackListener;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public MenuEntry confirmEntry() {
		return mConfirmEntry;
	}

	public MenuEntry cancelEntry() {
		return mCancelEntry;
	}

	// --------------------------------------
	// Constructors
	// --------------------------------------

	public TimedConfirmationDialog(ScreenManager pScreenManager, MenuScreen pParentScreen, String pDialogMessage) {
		super(pScreenManager, pParentScreen, pDialogMessage);

		ListLayout lListLayout = new ListLayout(this);
		lListLayout.anchor(ANCHOR.bottom);

		mConfirmEntry = new MenuEntry(pScreenManager, this, "Okay");
		mConfirmEntry.registerClickListener(pParentScreen, BUTTON_TIMED_CONFIRM_YES);
		mConfirmEntry.buttonSize(BUTTON_SIZE.narrow);
		mCancelEntry = new MenuEntry(pScreenManager, this, "Cancel");
		mCancelEntry.registerClickListener(pParentScreen, BUTTON_TIMED_CONFIRM_NO);
		mCancelEntry.buttonSize(BUTTON_SIZE.narrow);

		lListLayout.menuEntries().add(mCancelEntry);
		lListLayout.menuEntries().add(mConfirmEntry);

		layouts().add(lListLayout);

		// mEntryOffsetFromTop = 285f;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore pCore, boolean pOtherScreenHasFocus, boolean pCoveredByOtherScreen) {
		super.update(pCore, pOtherScreenHasFocus, pCoveredByOtherScreen);

		if (mActive) {
			mTimer += pCore.time().elapseGameTimeMilli();

			if (mTimer >= mTimeToWait) {
				// Trigger callback
				if (mCallbackListener != null) {
					mCallbackListener.timeExpired();

				}

				stop();
			}

		}
	}

	@Override
	public void updateStructure(LintfordCore pCore) {
		// Need to apply an offset on the y-axis to account for the timer.
		float lTextHeight = font().bitmap().getStringHeight(mMessageString) + 150;

		// Get the Y Start position of the menu entries
		float lYPos = -DIALOG_HEIGHT * 0.5f + font().bitmap().getStringHeight(mMessageString) + lTextHeight / 2;

		final int lLayoutCount = layouts().size();
		for (int i = 0; i < lLayoutCount; i++) {
			// TODO: Ignore floating layouts
			BaseLayout lLayout = layouts().get(i);

			lYPos += lLayout.paddingTop();

			switch (mChildAlignment) {
			case left:
				lLayout.x = lLayout.paddingLeft();
				break;
			case center:
				lLayout.x = -lLayout.w / 2;
				break;
			case right:
				lLayout.x = pCore.config().display().windowSize().x - lLayout.w - lLayout.paddingRight();
				break;
			}

			lLayout.y = lYPos;
			lYPos += lLayout.h + lLayout.paddingBottom();

			layouts().get(i).updateStructure();

		}

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		// TODO: Put the ZDEPTH somewhere where all Dialogs have access to it
		final float ZDEPTH = ZLayers.LAYER_SCREENMANAGER + 0.05f;

		final float TEXT_HORIZONTAL_PADDING = 20;

		if (mDrawBackground) {
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 64, 0, 32, 32, -DIALOG_WIDTH * 0.5f, -DIALOG_HEIGHT * 0.5f, DIALOG_WIDTH, DIALOG_HEIGHT, ZDEPTH, mR, mG, mB, mA);
			mSpriteBatch.end();
		}

		font().begin(pCore.HUD());

		/* Render title and message */
		font().draw(mMessageString, -DIALOG_WIDTH * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + 30, ZDEPTH, 1f, DIALOG_WIDTH);

		font().end();

		AARectangle lHUDRect = pCore.HUD().boundingRectangle();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left() + TITLE_PADDING_X, lHUDRect.top(), ZDEPTH, mR, mG, mB, mA, 1f);
		mMenuHeaderFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, ZDEPTH + (i * 0.001f));

		}

		final String lTimeMessage = "[" + (int) ((mTimeToWait - mTimer) / 1000f) + " Sec(s)]";
		final float lTimeDialogWidth = DIALOG_WIDTH;

		/* Render title and message */
		font().begin(pCore.HUD());
		font().draw(lTimeMessage, -lTimeDialogWidth * 0.5f + TEXT_HORIZONTAL_PADDING, -DIALOG_HEIGHT * 0.5f + 65, ZDEPTH, 1f, lTimeDialogWidth);
		font().end();

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setListener(TimedDialogInterface pListener) {
		mCallbackListener = pListener;

	}

	public void resetTime() {
		mTimer = 0;
	}

	public void stop() {
		resetTime();
		mActive = false;
	}

	public void start() {
		start(DEFAULT_WAIT_TIME);
	}

	public void start(float pTimeToWait) {
		resetTime();
		mTimeToWait = pTimeToWait;
		mActive = true;
	}

	public void pause() {
		mActive = false;
	}

	@Override
	protected void handleOnClick() {
		switch (mClickAction.consume()) {
		case BUTTON_TIMED_CONFIRM_YES:
			if (mCallbackListener != null) {

				mCallbackListener.confirmation();

			}

			stop();
			exitScreen();
			break;

		case BUTTON_TIMED_CONFIRM_NO:
			if (mCallbackListener != null) {
				mCallbackListener.decline();

			}

			stop();
			exitScreen();
			break;

		default:
			break;
		}
	}

}
