package net.lintfordlib.screenmanager.dialogs;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.screenmanager.MenuEntry;
import net.lintfordlib.screenmanager.MenuScreen;
import net.lintfordlib.screenmanager.ScreenManager;
import net.lintfordlib.screenmanager.layouts.ListLayout;
import net.lintfordlib.screenmanager.screens.VideoOptionsScreen;

/**
 * Used in the {@link VideoOptionsScreen} to revert the changes of a monitor change if the user doesn't interact with the dialog after the alloted amount of time.
 */
public class TimedConfirmationDialog extends BaseDialog {

	// --------------------------------------==============
	// Constants
	// --------------------------------------==============

	public static final int BUTTON_TIMED_CONFIRM_YES = 200;
	public static final int BUTTON_TIMED_CONFIRM_NO = 201;

	public static final int DEFAULT_WAIT_TIME = 15; // def Seconds

	// --------------------------------------
	// Variables
	// --------------------------------------

	private MenuEntry mConfirmEntry;
	private MenuEntry mCancelEntry;

	private float mTimeToWait;
	private float mTimer;
	private boolean mActive;

	private ITimedDialog mCallbackListener;

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

	public TimedConfirmationDialog(ScreenManager screenManager, MenuScreen parentScreen, String dialogMessage) {
		super(screenManager, parentScreen, dialogMessage);

		final var lListLayout = new ListLayout(this);

		mConfirmEntry = new MenuEntry(screenManager, this, "Okay");
		mConfirmEntry.registerClickListener(parentScreen, BUTTON_TIMED_CONFIRM_YES);

		mCancelEntry = new MenuEntry(screenManager, this, "Cancel");
		mCancelEntry.registerClickListener(parentScreen, BUTTON_TIMED_CONFIRM_NO);

		lListLayout.addMenuEntry(mCancelEntry);
		lListLayout.addMenuEntry(mConfirmEntry);

		addLayout(lListLayout);
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	@Override
	public void update(LintfordCore core, boolean otherScreenHasFocus, boolean coveredByOtherScreen) {
		super.update(core, otherScreenHasFocus, coveredByOtherScreen);

		if (mActive) {
			mTimer += core.appTime().elapsedTimeMilli();

			if (mTimer >= mTimeToWait) {
				if (mCallbackListener != null)
					mCallbackListener.timeExpired();

				stop();
			}
		}
	}

	@Override
	public void draw(LintfordCore core) {
		if (mScreenState != ScreenState.ACTIVE && mScreenState != ScreenState.TRANSITION_STARTING && mScreenState != ScreenState.TRANSITION_SLEEPING)
			return;

		super.draw(core);

		final var lTimeMessage = "[" + (int) ((mTimeToWait - mTimer) / 1000f) + " Sec(s)]";
		mCancelEntry.entryText("Revert " + lTimeMessage);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setListener(ITimedDialog listener) {
		mCallbackListener = listener;
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

	public void start(float timeToWait) {
		resetTime();

		mTimeToWait = timeToWait;
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
