package net.lintford.library;

import net.lintford.library.core.LintfordCore;

public class LintfordMain extends LintfordCore {

	public LintfordMain(GameInfo pGameInfo, String[] pArgs) {
		super(pGameInfo, pArgs);

	}

	public static void main(String[] pArgs) {
		GameInfo lGameInfo = new GameInfo() {
			@Override
			public String applicationName() {
				return "LDLibrary";
			}

			@Override
			public String windowTitle() {
				return "LDLibrary";
			}

			@Override
			public int windowWidth() {
				return 320;
			}

			@Override
			public int windowHeight() {
				return 240;
			}

			@Override
			public boolean windowResizeable() {
				return true;
			}

		};

		// ExcavationClient def constructor will automatically create a window and load the previous
		// settings (if they exist).
		LintfordMain lClient = new LintfordMain(lGameInfo, pArgs);
		lClient.createWindow();

	}

}
