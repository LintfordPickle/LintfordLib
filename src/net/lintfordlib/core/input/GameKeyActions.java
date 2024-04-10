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

	private List<GameKeyBind> mGameKeyMap = new ArrayList<>();

	// --------------------------------------
	// Properties
	// --------------------------------------

	public List<GameKeyBind> gameKeyMap() {
		return mGameKeyMap;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public GameKeyActions() {
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	void registerKeyActions(KeyEventActionManager keyEventActionManager) {
		final int lNumActions = mGameKeyMap.size();
		for (int i = 0; i < lNumActions; i++) {
			final var n = mGameKeyMap.get(i);
			keyEventActionManager.registerNewKeyboardEventAction(n.eventActionUid, n.defaultBoundKeyCode);
		}
	}

	protected void addNewKeyBinding(String name, int eventActionUid, int defaultBoundKeyCode) {
		mGameKeyMap.add(new GameKeyBind(name, eventActionUid, defaultBoundKeyCode));
	}
}
