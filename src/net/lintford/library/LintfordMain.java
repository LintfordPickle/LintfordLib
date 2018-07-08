package net.lintford.library;

import net.lintford.library.core.LintfordCore;

public class LintfordMain extends LintfordCore {

	public LintfordMain(GameInfo pGameInfo) {
		super(pGameInfo);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
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
		LintfordMain lClient = new LintfordMain(lGameInfo);
		lClient.createWindow();
		
	}

}
