package net.lintford.library.controllers.actionevents;

import java.nio.ByteBuffer;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.actionevents.ActionEventManager;
import net.lintford.library.core.actionevents.IActionFrame;
import net.lintford.library.core.actionevents.ActionEventManager.PlaybackMode;
import net.lintford.library.core.input.mouse.IProcessMouseInput;
import net.lintford.library.core.time.LogicialCounter;

public abstract class ActionEventController<T extends IActionFrame> extends BaseController implements IProcessMouseInput {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Action Event Controller";

	protected static final float MOUSE_CLICK_COOLDOWN_TIME = 200; // ms

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected final T mTempFrameInput; // last 'read' frame, not necessarily current yet
	protected final T mLastActionEvents; // last frame
	protected final T mCurrentActionEvents; // current frame

	protected boolean mIsTempFrameConsumed;

	private ActionEventManager mActionEventManager;
	protected LogicialCounter mLogicialCounter;
	protected float mMouseClickTimer;

	protected int mTotalTicks;
	protected int mCurrentTick;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ActionEventManager actionEventManager() {
		return mActionEventManager;
	}

	public T currentInput() {
		return mCurrentActionEvents;
	}

	/** this indicates that we have read the last set of bytes from the input file (not that the last control has been played back) */
	public boolean reachedLastFrame() {
		final var lRenderedLastFrame = mLogicialCounter.getCounter() >= mTotalTicks;
		return mActionEventManager.mode() == PlaybackMode.Read && lRenderedLastFrame;
	}

	public boolean reachedEndOfFile() {
		return mActionEventManager.endOfFileReached();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// TODO: Need to make sure this is added as the first controller in the list
	public ActionEventController(ControllerManager controllerManager, LogicialCounter frameCounter, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mIsTempFrameConsumed = true;

		mTempFrameInput = createActionFrameInstance();
		mLastActionEvents = createActionFrameInstance();
		mCurrentActionEvents = createActionFrameInstance();

		mLogicialCounter = frameCounter;
		mActionEventManager = createActionEventManager();

	}

	protected abstract ActionEventManager createActionEventManager();

	protected abstract T createActionFrameInstance();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core) {

		mMouseClickTimer -= core.gameTime().elapsedTimeMilli();

		switch (mActionEventManager.mode()) {
		case Read:
			if (mIsTempFrameConsumed) {
				// we need another frame, if available
				if (!mActionEventManager.endOfFileReached()) {
					readNextFrame(mActionEventManager.dataByteBuffer());
				}
			}

			// is it time to act upon the new frame's content?
			if (mLogicialCounter.getCounter() == mTempFrameInput.tickNumber()) { // TODO: or bigger maybe?
				mCurrentActionEvents.copy(mTempFrameInput);

				mCurrentTick = mCurrentActionEvents.tickNumber();

				mTempFrameInput.reset();
				mIsTempFrameConsumed = true;
			}
			break;

		case Record:

			mLastActionEvents.copy(mCurrentActionEvents);
			mCurrentActionEvents.reset();
			mCurrentActionEvents.tickNumber(mLogicialCounter.getCounter());

			updateInputActionEvents(core);

			if (mCurrentActionEvents.hasChanges())
				saveActionEvents(mCurrentActionEvents, mActionEventManager.dataByteBuffer());

			break;

		default:
		case Normal:

			mLastActionEvents.copy(mCurrentActionEvents);
			mCurrentActionEvents.reset();
			mCurrentActionEvents.tickNumber(mLogicialCounter.getCounter());

			updateInputActionEvents(core);

			break;
		}

		super.update(core);
	}

	public void onExitingGame() {
		if (mActionEventManager.mode() == PlaybackMode.Record) {
			mCurrentActionEvents.tickNumber(mLogicialCounter.getCounter());

			saveEndOfFile(mCurrentActionEvents, mActionEventManager.dataByteBuffer());
			saveHeaderToBuffer(mCurrentActionEvents, mActionEventManager.headerByteBuffer());
			mActionEventManager.saveToFile();
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected abstract void saveHeaderToBuffer(T currentFrame, ByteBuffer headerBuffer);

	protected abstract void readHeaderBuffer(ByteBuffer headerBuffer);

	protected abstract void saveActionEvents(T frame, ByteBuffer dataBuffer);

	protected abstract void saveEndOfFile(T frame, ByteBuffer dataBuffer);

	protected abstract void readNextFrame(ByteBuffer dataBuffer);

	protected abstract void updateInputActionEvents(LintfordCore core);

	public void setNormalMode() {
		mActionEventManager.setNormalMode();
	}

	public void setRecordingMode(String filename) {
		mActionEventManager.setRecordingMode(filename);
	}

	public void setPlaybackMode(String filename) {
		mActionEventManager.setPlaybackMode(filename);
		readHeaderBuffer(mActionEventManager.headerByteBuffer());
	}

	// ---------------------------------------------
	// Inherited-Methods (IProcessMouseInput)
	// ---------------------------------------------

	@Override
	public boolean isCoolDownElapsed() {
		return mMouseClickTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mMouseClickTimer = MOUSE_CLICK_COOLDOWN_TIME;

	}

}
