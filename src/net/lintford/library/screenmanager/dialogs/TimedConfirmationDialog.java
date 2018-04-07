package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.AARectangle;
import net.lintford.library.core.graphics.textures.TextureManager;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuEntry.BUTTON_SIZE;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
import net.lintford.library.screenmanager.layouts.BaseLayout.ANCHOR;
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
	public void draw(LintfordCore pCore) {
		if (mScreenState != ScreenState.Active && mScreenState != ScreenState.TransitionOn && mScreenState != ScreenState.TransitionOff)
			return;

		final float TEXT_HORIZONTAL_PADDING = 20;
		mDialogWidth = font().bitmap().getStringWidth(mMessageString) + TEXT_HORIZONTAL_PADDING * 2;

		final float lDialogWidth = mDialogWidth;
		final float lDialogHeight = 250 + font().bitmap().getStringHeight(mMessageString);

		if (mDrawBackground) {
			mSpriteBatch.begin(pCore.HUD());
			mSpriteBatch.draw(TextureManager.TEXTURE_CORE_UI, 64, 0, 32, 32, -lDialogWidth * 0.5f, -lDialogHeight * 0.5f, lDialogWidth, lDialogHeight, -1.5f, mR, mG, mB, mA);
			mSpriteBatch.end();
		}

		font().begin(pCore.HUD());

		/* Render title and message */
		font().draw(mMessageString, -lDialogWidth * 0.5f + TEXT_HORIZONTAL_PADDING, -lDialogHeight * 0.5f + 30, -1.5f, 1f, lDialogWidth);

		font().end();

		AARectangle lHUDRect = pCore.HUD().boundingRectangle();

		mMenuHeaderFont.begin(pCore.HUD());
		mMenuHeaderFont.draw(mMenuTitle, lHUDRect.left() + TITLE_PADDING_X, lHUDRect.top(), -0f, mR, mG, mB, mA, 1f);
		mMenuHeaderFont.end();

		// Draw each layout in turn.
		final int lCount = layouts().size();
		for (int i = 0; i < lCount; i++) {
			mLayouts.get(i).draw(pCore, Z_DEPTH);

		}

		final String lTimeMessage = "[" + (int) ((mTimeToWait - mTimer) / 1000f) + " Sec(s)]";
		final float lTimeDialogWidth = mDialogWidth;

		font().begin(pCore.HUD());

		/* Render title and message */
		font().draw(lTimeMessage, -lTimeDialogWidth * 0.5f + TEXT_HORIZONTAL_PADDING, -lDialogHeight * 0.5f + 65, -1.5f, 1f, lTimeDialogWidth);

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
