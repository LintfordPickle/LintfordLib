package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.maths.MathHelper;
import net.lintfordlib.options.reader.IniFile;

public class KeyEventActionManager extends IniFile {

	public class PlayerKeyActionEvents {

		public final int playerIndex;

		final Map<Integer, KeyEventAction> eventActionMap = new HashMap<>();

		public PlayerKeyActionEvents(int playerIndex) {
			this.playerIndex = playerIndex;
		}

	}

	// TODO: Make the number of players 'static' - i.e. defined by the game, not dynamically based on controllers connected
	// TODO: Need to create a 'default' player collection to avoid NREs.

	final List<KeyEventAction> mUpdateActionList = new ArrayList<>();

	// --------------------------------------
	// Variables
	// --------------------------------------

	private GameKeyActions mGameKeyActions;

	private int _numPlayers;
	private InputManager mInputManager;
	private IInputProcessor mInputProcessor;

	private final List<PlayerKeyActionEvents> playerEvents = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void setInputProcessor(IInputProcessor inputProcessor) {
		mInputProcessor = inputProcessor;
	}

	public void clearInputProcessor() {
		mInputProcessor = null;
	}

	private PlayerKeyActionEvents getPlayerEvents(int playerIndex) {
		return playerEvents.get(playerIndex);
	}

	public KeyEventAction getEventActionByUid(int playerIndex, int eventActionUid) {
		return playerEvents.get(playerIndex).eventActionMap.get(eventActionUid);
	}

	public GameKeyActions gameKeyActions() {
		return mGameKeyActions;
	}

	public Map<Integer, KeyEventAction> eventMapForPlayer(int playerIndex) {
		return getPlayerEvents(playerIndex).eventActionMap;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public KeyEventActionManager(InputManager inputManager, String configFilename) {
		super(configFilename);
		mInputManager = inputManager;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void update(LintfordCore core) {
		final var lDeltaTime = (float) core.appTime().elapsedTimeMilli();

		// TODO: The idea of the action list needs to be thought out a little more. I think we need to have an action list per player maybe (See (1) below))?
		// This is because, there could potentially be 2 or more controllers connected, each controlling a different player.

		// we poll the keyboard once for each of the registered key action events,
		// this way the individual action players don't separately poll the keyboard and consume the key timers.
		// we pass the optional input process, which controls if the event manager should be listening to (keyboard) events.
		final int lNumEventActions = mUpdateActionList.size();
		for (int i = 0; i < lNumEventActions; i++) {
			final var lAction = mUpdateActionList.get(i);

			boolean lActionTrigged = false;
			switch (lAction.boundToInputDevice()) {
			default:
			case KeyEventAction.INPUT_DEVICE_NOTHING:
				break;

			case KeyEventAction.INPUT_DEVICE_KEYBOARD:
				lActionTrigged = mInputManager.keyboard().isKeyDown(lAction.getBoundKeyCode(), mInputProcessor);
				break;

			case KeyEventAction.INPUT_DEVICE_GAMEPAD:
				lActionTrigged = mInputManager.gamepads().isGamepadButtonDown(lAction.getBoundKeyCode(), mInputProcessor);
				break;
			case KeyEventAction.INPUT_DEVICE_MOUSE:
				// TODO: Allow binding of mouse input
				break;
			}

			lAction.incDownTimer(lDeltaTime);
			lAction.isDown(lActionTrigged);
		}
	}

	public void setNumberPlayers(int numberPlayers) {
		_numPlayers = MathHelper.clampi(numberPlayers, 1, 4);
		playerEvents.clear();
		for (int i = 0; i < _numPlayers; i++) {
			playerEvents.add(new PlayerKeyActionEvents(i));
		}
	}

	public void addGameKeyActions(GameKeyActions gameKeyActions) {
		mGameKeyActions = gameKeyActions;
		mGameKeyActions.registerEventActions(this);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void registerNewKeyboardEventAction(int playerIndex, int eventActionUid, int inputDeviceType, int defaultKeyCode) {
		final var lPlayerKeys = getPlayerEvents(playerIndex);

		if (lPlayerKeys == null)
			return; // doesn't exist

		if (lPlayerKeys.eventActionMap.get(eventActionUid) != null)
			return; // already assigned to player

		final var lNewEventAction = new KeyEventAction(eventActionUid, inputDeviceType, defaultKeyCode);
		mUpdateActionList.add(lNewEventAction);

		lPlayerKeys.eventActionMap.put(eventActionUid, lNewEventAction);

		Debug.debugManager().logger().i(getClass().getSimpleName(), "Registered new event action " + eventActionUid + " for player " + playerIndex + " to key code [" + defaultKeyCode + "]");
	}

	public boolean getCurrentControlActionState(int playerIndex, int eventActionUid) {
		var actionState = false;
		final var lEventAction = getPlayerEvents(playerIndex).eventActionMap.get(eventActionUid);
		if (lEventAction != null) {
			actionState |= lEventAction.isDown();
		}

		return actionState;
	}

	public boolean getCurrentControlActionStateTimed(int playerIndex, int eventActionUid) {
		var actionState = false;
		final var lKeyboardEventAction = getPlayerEvents(playerIndex).eventActionMap.get(eventActionUid);
		if (lKeyboardEventAction != null) {
			actionState |= lKeyboardEventAction.isDownTimed();
		}

		return actionState;
	}

	// --------------------------------------
	// Ini-File
	// --------------------------------------

	final String lSectionName = "KEY_BINDING";

	@Override
	public void saveConfig() {
		clearEntries();

		// TODO: Finish saving (per player)

//		for (var lKeyBindingEntry : mEventActionMap.entrySet()) {
//			setValue(lSectionName, Integer.toString(lKeyBindingEntry.getKey()), Integer.toString(lKeyBindingEntry.getValue().getBoundKeyCode()));
//
//		}

		super.saveConfig();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		if (isEmpty())
			return;

		// TODO: Finish loading (per player)

//		for (var lKeyBindingEntry : mEventActionMap.entrySet()) {
//			final var lValue = getInt(lSectionName, Integer.toString(lKeyBindingEntry.getValue().eventActionUid()), lKeyBindingEntry.getValue().defaultBoundKeyCode());
//			lKeyBindingEntry.getValue().boundKeyCode(lValue);
//		}
	}
}
