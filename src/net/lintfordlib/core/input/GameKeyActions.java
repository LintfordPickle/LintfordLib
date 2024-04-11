package net.lintfordlib.core.input;

import java.util.ArrayList;
import java.util.List;

public abstract class GameKeyActions {

	public class GameKeyBind {
		public final String eventActionName;
		public final int playerIndex;
		public final int defaultBoundKeyCode;
		public final int eventActionUid;
		public final int inputDeviceType;

		private GameKeyBind(String name, int eventActionUid, int playerIndex, int inputDeviceType, int defaultBoundKeyCode) {
			this.eventActionName = name;
			this.eventActionUid = eventActionUid;
			this.playerIndex = playerIndex;
			this.inputDeviceType = inputDeviceType;
			this.defaultBoundKeyCode = defaultBoundKeyCode;
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private List<GameKeyBind> mGameKeyBindMap = new ArrayList<>();
	private List<GameKeyBind> mGameKeyBindAltMap = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<GameKeyBind> gameKeyMap() {
		return mGameKeyBindMap;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	final int mNumPlayers;

	public GameKeyActions(int numPlayers) {
		mNumPlayers = numPlayers;
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	void registerEventActions(KeyEventActionManager keyEventActionManager) {
		final int lNumKbActions = mGameKeyBindMap.size();
		for (int i = 0; i < lNumKbActions; i++) {
			final var n = mGameKeyBindMap.get(i);

			keyEventActionManager.registerNewKeyboardEventAction(n.playerIndex, n.eventActionUid, n.inputDeviceType, n.defaultBoundKeyCode);
		}
	}

	protected void addNewKeyBinding(String name, int eventActionUid, int playerIndex, int inputDeviceType, int defaultBoundKeyCode) {
		mGameKeyBindMap.add(new GameKeyBind(name, eventActionUid, playerIndex, inputDeviceType, defaultBoundKeyCode));

	}
}
