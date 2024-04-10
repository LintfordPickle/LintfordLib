package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.List;

public abstract class GameKeyActions {

	public class GameKeyBind {
		public final String eventActionName;
		public final int defaultBoundKeyCode;
		public final int eventActionUid;

		private GameKeyBind(String name, int eventActionUid, int defaultBoundKeyCode) {
			this.eventActionName = name;
			this.eventActionUid = eventActionUid;
			this.defaultBoundKeyCode = defaultBoundKeyCode;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<GameKeyBind> mGameKeyboardMap = new ArrayList<>();
	private List<GameKeyBind> mGameGamepadMap = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<GameKeyBind> gameKeyMap() {
		return mGameKeyboardMap;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameKeyActions() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	void registerEventActions(KeyEventActionManager keyEventActionManager) {
		final int lNumKbActions = mGameKeyboardMap.size();
		for (int i = 0; i < lNumKbActions; i++) {
			final var n = mGameKeyboardMap.get(i);
			keyEventActionManager.registerNewKeyboardEventAction(n.eventActionUid, n.defaultBoundKeyCode);
		}

		final int lNumGpActions = mGameGamepadMap.size();
		for (int i = 0; i < lNumGpActions; i++) {
			final var n = mGameGamepadMap.get(i);
			keyEventActionManager.registerNewGamepadEventAction(n.eventActionUid, n.defaultBoundKeyCode);
		}
	}

	protected void addNewGamepadBinding(String name, int eventActionUid, int defaultBoundKeyCode) {
		mGameGamepadMap.add(new GameKeyBind(name, eventActionUid, defaultBoundKeyCode));
	}

	protected void addNewKeyboardBinding(String name, int eventActionUid, int defaultBoundKeyCode) {
		mGameKeyboardMap.add(new GameKeyBind(name, eventActionUid, defaultBoundKeyCode));
	}
}
