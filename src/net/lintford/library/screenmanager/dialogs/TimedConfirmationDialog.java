package net.lintford.library.screenmanager.dialogs;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.screenmanager.MenuEntry;
import net.lintford.library.screenmanager.MenuScreen;
import net.lintford.library.screenmanager.ScreenManager;
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

	public static final int DEFAULT_WAIT_TIME = 15; // def Seconds

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

		mConfirmEntry = new MenuEntry(pScreenManager, lListLayout, "Okay");
		mConfirmEntry.registerClickListener(pParentScreen, BUTTON_TIMED_CONFIRM_YES);

		mCancelEntry = new MenuEntry(pScreenManager, lListLayout, "Cancel");
		mCancelEntry.registerClickListener(pParentScreen, BUTTON_TIMED_CONFIRM_NO);

		lListLayout.menuEntries().add(mCancelEntry);
		lListLayout.menuEntries().add(mConfirmEntry);

		layouts().add(lListLayout);

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

		super.draw(pCore);

		final String lTimeMessage = "[" + (int) ((mTimeToWait - mTimer) / 1000f) + " Sec(s)]";
		mCancelEntry.entryText("Revert " + lTimeMessage);

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
