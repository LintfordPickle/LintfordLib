package net.lintfordlib.core.splitscreen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.rendertarget.RTCamera;
import net.lintfordlib.renderers.RendererManager;

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

		float lOffsetX = 0.f;
		float lOffsetY = 0.f;
		final var lGameCamera = core.gameCamera();

		int numPlayers = numActivePlayers();
		switch (numPlayers) {
		default:
		case 1:
			mPlayerSessions.get(0).getViewContainer().viewportOffset.set(lOffsetX, lOffsetY);
			break;

		case 2:
			lOffsetX = lGameCamera.getWidth() * .5f;
			lOffsetX = lGameCamera.getHeight() * .5f;
			mPlayerSessions.get(0).getViewContainer().viewportOffset.set(lOffsetX, lOffsetY);
			mPlayerSessions.get(1).getViewContainer().viewportOffset.set(lOffsetX, lOffsetY);
			break;

		// 3/4 players have no offset and thus zoomed out view
		}
	}

	public void loadResource(ResourceManager resourceManager) {
		final var lDisplaySettings = resourceManager.config().display();
		final var lCanvasWidth = lDisplaySettings.gameResolutionWidth();
		final var lCanvasHeight = lDisplaySettings.gameResolutionHeight();

		// TODO: only instantiate RTs for active players

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
		updatePlayerViewports(core);
	}

	private void updatePlayerViewports(LintfordCore core) {
		final var lDisplayConfig = core.config().display();
		final float lGameWindowWidth = lDisplayConfig.gameResolutionWidth();
		final float lGameWindowHeight = lDisplayConfig.gameResolutionHeight();

		final float lHudWindowWidth = lDisplayConfig.windowWidth();
		final float lHudWindowHeight = lDisplayConfig.windowHeight();

		// Create the viewports depending on how many players joined this game
		int numPlayers = numActivePlayers();

		switch (numPlayers) {
		default:
		case 1:
			mPlayerSessions.get(0).getViewContainer().gameViewport().set(-lGameWindowWidth * .5f, -lGameWindowHeight * .5f, lGameWindowWidth, lGameWindowHeight);
			mPlayerSessions.get(0).getViewContainer().hudViewport().set(-lHudWindowWidth * .5f, -lHudWindowHeight * .5f, lHudWindowWidth, lHudWindowHeight);

			break;

		case 2:

			final float lHalfWidth = lGameWindowWidth * .5f;
			final float lHalfHeight = lGameWindowHeight * .5f;

			mPlayerSessions.get(0).getViewContainer().gameViewport().set(-lHalfWidth, -lHalfHeight, lHalfWidth, lHalfHeight * 2);
			mPlayerSessions.get(1).getViewContainer().gameViewport().set(0, -lHalfHeight, lHalfWidth, lHalfHeight * 2);
			break;

		case 3:

			break;

		case 4:

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
