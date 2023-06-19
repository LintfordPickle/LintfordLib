package net.lintford.library.core.splitscreen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.graphics.rendertarget.RTCamera;
import net.lintford.library.renderers.RendererManager;

public abstract class PlayerSessionsManager<T extends IPlayerSession> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int MAX_PLAYERS = 4;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private RendererManager mRendererManager;
	private int mNumberActivePlayers = 1;
	private final List<T> mPlayerSessions = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int numActivePlayers() {
		return mNumberActivePlayers;
	}

	public List<T> playerSessions() {
		return mPlayerSessions;
	}

	public T getPlayerSession(int sessionIndex) {
		if (sessionIndex < 0 || sessionIndex >= mPlayerSessions.size())
			return null;
		return mPlayerSessions.get(sessionIndex);
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// ref BaseGameSplitScreen
	public PlayerSessionsManager(RendererManager rendererManager) {
		mPlayerSessions.add(createNewPlayerSession(false));
		mPlayerSessions.add(createNewPlayerSession(true));
		mPlayerSessions.add(createNewPlayerSession(true));
		mPlayerSessions.add(createNewPlayerSession(true));

		mPlayerSessions.get(0).enablePlayer(true);

		mRendererManager = rendererManager;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	protected abstract T createNewPlayerSession(boolean canBeDeactivated);

	public void initialize(LintfordCore core) {
		updatePlayerViewports(core);
	}

	public void loadResource(ResourceManager resourceManager) {
		final var lDisplaySettings = resourceManager.config().display();
		final var lCanvasWidth = lDisplaySettings.gameResolutionWidth();
		final var lCanvasHeight = lDisplaySettings.gameResolutionHeight();

		final var lNumPlayerSessions = mPlayerSessions.size();
		for (int i = 0; i < lNumPlayerSessions; i++) {
			final var lPlayerSession = mPlayerSessions.get(i);
			final var lPlayerViewContainer = lPlayerSession.getViewContainer();

			final var lRenderTarget = mRendererManager.createRenderTarget("Game Canvas P" + i, lCanvasWidth, lCanvasHeight, 1, GL11.GL_NEAREST, false);
			final var lRTCamera = new RTCamera(lCanvasWidth, lCanvasHeight);

			lPlayerViewContainer.init(lRTCamera, lRenderTarget);
		}
	}

	public void unloadResources() {
		final int lNumPlayerSessions = mPlayerSessions.size();
		for (int i = 0; i < lNumPlayerSessions; i++) {
			final var lPlayerSession = mPlayerSessions.get(i);
			final var lPlayerViewContainer = lPlayerSession.getViewContainer();

			mRendererManager.unloadRenderTarget(lPlayerViewContainer.renderTarget());
			lPlayerViewContainer.reset();
		}
	}

	public void update(LintfordCore core) {
		// TODO: Viewports only need updating when the window is resized
		updatePlayerViewports(core);
	}

	private void updatePlayerViewports(LintfordCore core) {
		final var lDisplayConfig = core.config().display();
		final float lWindowWidth = lDisplayConfig.windowWidth();
		final float lWindowHeight = lDisplayConfig.windowHeight();

		// Create the viewports depending on how many players joined this game
		int numPlayers = numActivePlayers();

		switch (numPlayers) {
		default:
		case 1:
			mPlayerSessions.get(0).getViewContainer().viewport().set(-lWindowWidth * .5f, -lWindowHeight * .5f, lWindowWidth, lWindowHeight);
			break;

		case 2:
			mPlayerSessions.get(0).getViewContainer().viewport().set(-lWindowWidth * .5f, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight);
			mPlayerSessions.get(1).getViewContainer().viewport().set(0, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight);
			break;

		case 3:
			mPlayerSessions.get(0).getViewContainer().viewport().set(-lWindowWidth * .5f, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight * .5f);
			mPlayerSessions.get(1).getViewContainer().viewport().set(0, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight * .5f);
			mPlayerSessions.get(2).getViewContainer().viewport().set(-lWindowWidth * .5f, 0, lWindowWidth * .5f, lWindowHeight * .5f);
			break;

		case 4:
			mPlayerSessions.get(0).getViewContainer().viewport().set(-lWindowWidth * .5f, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight * .5f);
			mPlayerSessions.get(1).getViewContainer().viewport().set(0, -lWindowHeight * .5f, lWindowWidth * .5f, lWindowHeight * .5f);
			mPlayerSessions.get(2).getViewContainer().viewport().set(-lWindowWidth * .5f, 0, lWindowWidth * .5f, lWindowHeight * .5f);
			mPlayerSessions.get(3).getViewContainer().viewport().set(0, 0, lWindowWidth * .5f, lWindowHeight * .5f);
			break;
		}
	}

	public boolean isPlayerActive(int playerNumber) {
		return playerNumber < mNumberActivePlayers;
	}

	public T getPlayer(int playerIndex) {
		if (playerIndex < 0 || playerIndex >= MAX_PLAYERS)
			return null;

		return mPlayerSessions.get(playerIndex);
	}

	public void addPlayer() {
		if (mNumberActivePlayers >= MAX_PLAYERS)
			return;

		mNumberActivePlayers++;

		switch (mNumberActivePlayers) {
		case 4:
			mPlayerSessions.get(3).enablePlayer(true);
			break;
		case 3:
			mPlayerSessions.get(2).enablePlayer(true);
			break;
		case 2:
			mPlayerSessions.get(1).enablePlayer(true);
			break;
		}
	}

	public void disablePlayer() {
		if (mNumberActivePlayers <= 1)
			return;

		switch (mNumberActivePlayers) {
		case 4:
			mPlayerSessions.get(3).enablePlayer(false);
			break;
		case 3:
			mPlayerSessions.get(2).enablePlayer(false);
			break;
		case 2:
			mPlayerSessions.get(1).enablePlayer(false);
			break;
		}

		mNumberActivePlayers--;
	}

}
