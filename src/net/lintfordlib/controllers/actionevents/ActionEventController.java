package net.lintfordlib.controllers.actionevents;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.lintfordlib.controllers.BaseController;
import net.lintfordlib.controllers.core.ControllerManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.actionevents.ActionEventManager;
import net.lintfordlib.core.actionevents.IActionFrame;
import net.lintfordlib.core.actionevents.ActionEventManager.PlaybackMode;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.time.LogicialCounter;

public abstract class ActionEventController<T extends IActionFrame> extends BaseController {

	// ---------------------------------------------
	// Inner-Class
	// ---------------------------------------------

	public class ActionEventPlayer implements IInputProcessor {
		public final int playerUid;

		public final ActionEventManager actionEventManager;

		protected float mInputTimer;
		public int gamepadUid;
		public final boolean playbackAvailable;
		public boolean isTempFrameConsumed;
		public boolean isPlayerControlled;

		public final T tempFrameInput; // last 'read' frame, not necessarily current yet
		public final T lastActionEvents; // last frame
		public final T currentActionEvents; // current frame

		//
		public boolean isActionRecordingEnded;

		public ActionEventPlayer(int playerUid, PlaybackMode mode, int headerSize, int inputSize) {
			this.playerUid = playerUid;
			playbackAvailable = headerSize >= 0 && inputSize >= 0;

			actionEventManager = new ActionEventManager(mode, headerSize, inputSize);

			tempFrameInput = createActionFrameInstance();
			lastActionEvents = createActionFrameInstance();
			currentActionEvents = createActionFrameInstance();

			gamepadUid = -1;

			isTempFrameConsumed = true; // force first read
			isActionRecordingEnded = false;
		}

		public void update(LintfordCore core) {
			final var lDeltaTime = core.gameTime().elapsedTimeMilli();
			mInputTimer -= lDeltaTime;
		}

		@Override
		public boolean isCoolDownElapsed() {
			return mInputTimer <= 0;
		}

		@Override
		public void resetCoolDownTimer() {
			resetCoolDownTimer(MOUSE_CLICK_COOLDOWN_TIME);
		}

		@Override
		public void resetCoolDownTimer(float cooldownInMs) {
			mInputTimer = cooldownInMs;
		}

		@Override
		public boolean allowGamepadInput() {
			return true;
		}

		@Override
		public boolean allowKeyboardInput() {
			return true;
		}

		@Override
		public boolean allowMouseInput() {
			return true;
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

	private final ActionEventPlayer mDefaultPlayer;
	private final List<ActionEventPlayer> mActionEventPlayers = new ArrayList<>();

	protected LogicialCounter mLogicialCounter;

	// TODO: move these into the ActionEventPlayer class (we can have multiple recorders and players - so no one total time)
	protected int mTotalTicks;
	protected int mCurrentTick;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public List<ActionEventPlayer> actionEventPlayers() {
		return mActionEventPlayers;
	}

	public ActionEventPlayer defaultPlayer() {
		return mDefaultPlayer;
	}

	public ActionEventPlayer actionEventPlayer(int uid) {
		final int numActionManagers = mActionEventPlayers.size();
		for (int i = 0; i < numActionManagers; i++) {
			final var actionManager = mActionEventPlayers.get(i);
			if (actionManager.playerUid == uid) {
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

		mDefaultPlayer = new ActionEventPlayer(0, PlaybackMode.Normal, 0, 0);
		mActionEventPlayers.add(mDefaultPlayer);

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
		final int numActionManagers = mActionEventPlayers.size();
		for (int i = 0; i < numActionManagers; i++) {
			final var actionPlayer = mActionEventPlayers.get(i);
			final var actionManager = actionPlayer.actionEventManager;

			if (actionPlayer.isActionRecordingEnded)
				continue;

			actionPlayer.update(core);

			switch (actionManager.mode()) {
			case Playback:

				// TODO: Handle the case of two updates per frame (custom + input)
				// boolean checkOneMoreFrame = true;

				if (actionPlayer.isTempFrameConsumed) {
					// we need another frame, if available
					if (!actionManager.endOfFileReached()) {
						readNextFrame(actionManager.dataByteBuffer(), actionPlayer);
					}

					actionPlayer.isTempFrameConsumed = false;
				}

				if (mLogicialCounter.getCounter() == actionPlayer.tempFrameInput.tickNumber()) {
					actionPlayer.currentActionEvents.copy(actionPlayer.tempFrameInput);

					mCurrentTick = actionPlayer.currentActionEvents.tickNumber();

					actionPlayer.tempFrameInput.reset();
					actionPlayer.isTempFrameConsumed = true;

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

	public void finalizeInputFile() {
		// save any recorders into their files
		final int numActionPlayers = mActionEventPlayers.size();
		for (int i = 0; i < numActionPlayers; i++) {
			final var actionPlayer = mActionEventPlayers.get(i);
			final var actionManager = actionPlayer.actionEventManager;

			if (actionPlayer.actionEventManager.mode() == PlaybackMode.Record) {
				actionPlayer.isActionRecordingEnded = true;
				actionPlayer.currentActionEvents.tickNumber(mLogicialCounter.getCounter());

				saveEndOfFile(actionPlayer.currentActionEvents, actionManager.dataByteBuffer());
				saveHeaderToBuffer(actionPlayer.currentActionEvents, actionManager.headerByteBuffer());

				actionManager.saveToFile();
			}
		}
	}

	public void saveCustomFrame(LintfordCore core, int playerUid) {
		final var actionPlayer = mActionEventPlayers.get(playerUid);
		final var actionManager = actionPlayer.actionEventManager;
		saveCustomActionEvents(actionPlayer.currentActionEvents, actionManager.dataByteBuffer());
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected abstract void saveHeaderToBuffer(T currentFrame, ByteBuffer headerBuffer);

	protected abstract void readHeaderBuffer(ByteBuffer headerBuffer);

	protected abstract void saveActionEvents(T frame, ByteBuffer dataBuffer);

	protected abstract void saveCustomActionEvents(T frame, ByteBuffer dataBuffer);

	protected abstract void saveEndOfFile(T frame, ByteBuffer dataBuffer);

	protected abstract void readNextFrame(ByteBuffer dataBuffer, ActionEventPlayer player);

	protected abstract void updateInputActionEvents(LintfordCore core, ActionEventPlayer player);

	public void setNormalMode() {

	}

	public int createActionRecorder(int playerUid, String filename) {
		final var lNewActionEventPlayer = new ActionEventPlayer(playerUid, PlaybackMode.Record, getHeaderSizeInBytes(), getInputSizeInBytes());
		lNewActionEventPlayer.actionEventManager.filename(filename);
		mActionEventPlayers.add(lNewActionEventPlayer);

		return lNewActionEventPlayer.playerUid;
	}

	public int createActionPlayback(int playerUid, String filename) {
		final var lNewActionEventPlayer = new ActionEventPlayer(playerUid, PlaybackMode.Playback, getHeaderSizeInBytes(), getInputSizeInBytes());
		lNewActionEventPlayer.actionEventManager.loadFromFile(filename);
		mActionEventPlayers.add(lNewActionEventPlayer);

		return lNewActionEventPlayer.playerUid;
	}

}
