package net.lintford.library.controllers.actionevents;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.lintford.library.controllers.BaseController;
import net.lintford.library.controllers.core.ControllerManager;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.actionevents.ActionEventManager;
import net.lintford.library.core.actionevents.ActionEventManager.PlaybackMode;
import net.lintford.library.core.actionevents.IActionFrame;
import net.lintford.library.core.input.mouse.IProcessMouseInput;
import net.lintford.library.core.time.LogicialCounter;

public abstract class ActionEventController<T extends IActionFrame> extends BaseController implements IProcessMouseInput {

	// ---------------------------------------------
	// Inner-Class
	// ---------------------------------------------

	public class ActionEventPlayer {
		public final int entityUid;

		public final ActionEventManager actionEventManager;

		public final boolean playbackAvailable;

		public final T tempFrameInput; // last 'read' frame, not necessarily current yet
		public final T lastActionEvents; // last frame
		public final T currentActionEvents; // current frame

		public ActionEventPlayer(PlaybackMode mode, int headerSize, int inputSize) {
			entityUid = getNewActionManagerUid();
			playbackAvailable = headerSize >= 0 && inputSize >= 0;

			actionEventManager = new ActionEventManager(mode, headerSize, inputSize);

			tempFrameInput = createActionFrameInstance();
			lastActionEvents = createActionFrameInstance();
			currentActionEvents = createActionFrameInstance();
		}
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String CONTROLLER_NAME = "Action Event Controller";

	protected static final float MOUSE_CLICK_COOLDOWN_TIME = 200; // ms

	// This ActionEventPlayer is created by default and provides the action events without playback or recording enabled.
	public static final int DEFAULT_PLAYER_UID = 0;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	protected boolean mIsTempFrameConsumed;

	private final ActionEventPlayer mDefaultPlayer;
	private final List<ActionEventPlayer> mActionEventPlayers = new ArrayList<>();

	protected LogicialCounter mLogicialCounter;
	protected float mMouseClickTimer;

	protected int mTotalTicks;
	protected int mCurrentTick;

	private int mActionManagerCounter;

	public int getNewActionManagerUid() {
		return mActionManagerCounter++;
	}

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public ActionEventPlayer defaultPlayer() {
		return mDefaultPlayer;
	}

	public ActionEventPlayer actionEventPlayer(int uid) {
		final int numActionManagers = mActionEventPlayers.size();
		for (int i = 0; i < numActionManagers; i++) {
			final var actionManager = mActionEventPlayers.get(i);
			if (actionManager.entityUid == uid) {
				return actionManager;

			}
		}

		return null;
	}

	public boolean reachedEndOfFile(int actionManagerUid) {
		final var actionManager = actionEventPlayer(actionManagerUid);
		return actionManager.actionEventManager.endOfFileReached();
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// TODO: Need to make sure this is added as the first controller in the list
	public ActionEventController(ControllerManager controllerManager, LogicialCounter frameCounter, int entityGroupUid) {
		super(controllerManager, CONTROLLER_NAME, entityGroupUid);

		mDefaultPlayer = new ActionEventPlayer(PlaybackMode.Normal, 0, 0);
		mActionEventPlayers.add(mDefaultPlayer);
		mIsTempFrameConsumed = true;

		mLogicialCounter = frameCounter;
	}

	protected abstract int getHeaderSizeInBytes();

	protected abstract int getInputSizeInBytes();

	protected abstract T createActionFrameInstance();

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void update(LintfordCore core) {

		mMouseClickTimer -= core.gameTime().elapsedTimeMilli();

		final int numActionManagers = mActionEventPlayers.size();
		for (int i = 0; i < numActionManagers; i++) {
			final var actionPlayer = mActionEventPlayers.get(i);
			final var actionManager = actionPlayer.actionEventManager;

			switch (actionManager.mode()) {
			case Playback:
				if (mIsTempFrameConsumed) {
					// we need another frame, if available
					if (!actionManager.endOfFileReached()) {
						readNextFrame(actionManager.dataByteBuffer(), actionPlayer);
					}
				}

				// is it time to act upon the new frame's content?
				if (mLogicialCounter.getCounter() == actionPlayer.tempFrameInput.tickNumber()) {
					actionPlayer.currentActionEvents.copy(actionPlayer.tempFrameInput);

					mCurrentTick = actionPlayer.currentActionEvents.tickNumber();

					actionPlayer.tempFrameInput.reset();
					mIsTempFrameConsumed = true;
				}
				break;

			case Record:

				actionPlayer.lastActionEvents.copy(actionPlayer.currentActionEvents);
				actionPlayer.currentActionEvents.reset();
				actionPlayer.currentActionEvents.tickNumber(mLogicialCounter.getCounter());

				updateInputActionEvents(core, actionPlayer);

				if (actionPlayer.currentActionEvents.hasChanges())
					saveActionEvents(actionPlayer.currentActionEvents, actionManager.dataByteBuffer());

				break;

			default:
			case Normal:

				actionPlayer.lastActionEvents.copy(actionPlayer.currentActionEvents);
				actionPlayer.currentActionEvents.reset();
				actionPlayer.currentActionEvents.tickNumber(mLogicialCounter.getCounter());

				updateInputActionEvents(core, actionPlayer);

				break;
			}
		}

		super.update(core);
	}

	public void onExitingGame() {
		// save any recorders into their files
		final int numActionPlayers = mActionEventPlayers.size();
		for (int i = 0; i < numActionPlayers; i++) {
			final var actionPlayer = mActionEventPlayers.get(i);
			final var actionManager = actionPlayer.actionEventManager;

			if (actionPlayer.actionEventManager.mode() == PlaybackMode.Record) {
				actionPlayer.currentActionEvents.tickNumber(mLogicialCounter.getCounter());

				saveEndOfFile(actionPlayer.currentActionEvents, actionManager.dataByteBuffer());
				saveHeaderToBuffer(actionPlayer.currentActionEvents, actionManager.headerByteBuffer());

				actionManager.saveToFile();
			}
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected abstract void saveHeaderToBuffer(T currentFrame, ByteBuffer headerBuffer);

	protected abstract void readHeaderBuffer(ByteBuffer headerBuffer);

	protected abstract void saveActionEvents(T frame, ByteBuffer dataBuffer);

	protected abstract void saveEndOfFile(T frame, ByteBuffer dataBuffer);

	protected abstract void readNextFrame(ByteBuffer dataBuffer, ActionEventPlayer player);

	protected abstract void updateInputActionEvents(LintfordCore core, ActionEventPlayer player);

	public void setNormalMode() {

	}

	public int setActionRecorder(String filename) {
		final var lNewActionEventPlayer = new ActionEventPlayer(PlaybackMode.Record, getHeaderSizeInBytes(), getInputSizeInBytes());
		lNewActionEventPlayer.actionEventManager.filename(filename);
		mActionEventPlayers.add(lNewActionEventPlayer);

		return lNewActionEventPlayer.entityUid;
	}

	public int setActionPlayback(String filename) {
		final var lNewActionEventPlayer = new ActionEventPlayer(PlaybackMode.Playback, getHeaderSizeInBytes(), getInputSizeInBytes());
		lNewActionEventPlayer.actionEventManager.loadFromFile(filename);
		mActionEventPlayers.add(lNewActionEventPlayer);

		return lNewActionEventPlayer.entityUid;
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
