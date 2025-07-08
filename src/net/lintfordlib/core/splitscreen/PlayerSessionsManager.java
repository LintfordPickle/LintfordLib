package net.lintfordlib.core.splitscreen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.graphics.rendertarget.RTCamera;
import net.lintfordlib.renderers.RendererManagerBase;

public abstract class PlayerSessionsManager<T extends IPlayerSession> {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final int MAX_PLAYERS = 4;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mNumberActivePlayers = 1;
	private final List<T> mPlayerSessions = new ArrayList<>();

	private RendererManagerBase mRendererManager;
	private boolean mIsResourcesLoaded;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public boolean isResourcesLoaded() {
		return mIsResourcesLoaded;
	}

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

	public T mainPlayerSession() {
		return mPlayerSessions.get(0);
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	// ref BaseGameSplitScreen
	protected PlayerSessionsManager() {
		mPlayerSessions.add(createNewPlayerSession(false));
		mPlayerSessions.add(createNewPlayerSession(true));
		mPlayerSessions.add(createNewPlayerSession(true));
		mPlayerSessions.add(createNewPlayerSession(true));

		mPlayerSessions.get(0).isPlayerEnabled(true);

		mIsResourcesLoaded = false;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	public void reset() {

		// this really depends on the type of sessions the player/game is currently engaged in (e.g. head-to-head or single player)

		mPlayerSessions.get(0).isPlayerEnabled(true);
		mPlayerSessions.get(1).isPlayerEnabled(false);
		mPlayerSessions.get(2).isPlayerEnabled(false);
		mPlayerSessions.get(3).isPlayerEnabled(false);
	}

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

	public void loadResource(RendererManagerBase rendererManager, ResourceManager resourceManager) {
		if (mIsResourcesLoaded)
			return;

		if (mRendererManager != null)
			throw new RuntimeException("PlayerSessionManager was already loaded!");

		mRendererManager = rendererManager;

		final var lDisplaySettings = resourceManager.config().display();
		final var lCanvasWidth = lDisplaySettings.gameResolutionWidth();
		final var lCanvasHeight = lDisplaySettings.gameResolutionHeight();

		// TODO: only instantiate RTs for active players

		final var lNumPlayerSessions = mPlayerSessions.size();
		for (int i = 0; i < lNumPlayerSessions; i++) {
			final var lPlayerSession = mPlayerSessions.get(i);
			final var lPlayerViewContainer = lPlayerSession.getViewContainer();

			final var lRenderTarget = mRendererManager.createRenderTarget("Game Canvas P" + i, lCanvasWidth, lCanvasHeight, 1, GL11.GL_NEAREST, false, null);
			final var lRTCamera = new RTCamera(lCanvasWidth, lCanvasHeight);

			lPlayerViewContainer.init(lRTCamera, lRenderTarget);
		}

		mIsResourcesLoaded = true;
	}

	public void unloadResources() {
		final int lNumPlayerSessions = mPlayerSessions.size();
		for (int i = 0; i < lNumPlayerSessions; i++) {
			final var lPlayerSession = mPlayerSessions.get(i);
			final var lPlayerViewContainer = lPlayerSession.getViewContainer();

			mRendererManager.unloadRenderTarget(lPlayerViewContainer.renderTarget());
			lPlayerViewContainer.reset();
		}

		mRendererManager = null;
		mIsResourcesLoaded = false;
	}

	public void update(LintfordCore core) {
		updatePlayerViewports(core);
	}

	private void updatePlayerViewports(LintfordCore core) {
		final var displayConfig = core.config().display();

		final var gameWindowWidth = displayConfig.gameResolutionWidth();
		final var gameWindowHeight = displayConfig.gameResolutionHeight();
		final var halfGameWindowWidth = gameWindowWidth * .5f;
		final var halfGameWindowHeight = gameWindowHeight * .5f;

		final var hudWindowWidth = displayConfig.uiResolutionWidth();
		final var hudWindowHeight = displayConfig.uiResolutionHeight();
		final var halfHudWindowWidth = hudWindowWidth * .5f;
		final var halfHudWindowHeight = hudWindowHeight * .5f;

		//@formatter:off
		switch (numActivePlayers()) {
		default:
		case 1:
			// game render viewport
			mPlayerSessions.get(0).getViewContainer().gameViewport().set(-gameWindowWidth * .5f, 	-gameWindowHeight * .5f, 	gameWindowWidth, 		gameWindowHeight);
			
			// huds
			mPlayerSessions.get(0).getViewContainer().hudViewport().set(-hudWindowWidth * .5f, 		-hudWindowHeight * .5f, 	hudWindowWidth, 		hudWindowHeight);

			break;

		case 2:
			
			// game render viewports
			mPlayerSessions.get(0).getViewContainer().gameViewport().set(-halfGameWindowWidth, 		-halfGameWindowHeight, 		halfGameWindowWidth, 	halfGameWindowHeight * 2);
			mPlayerSessions.get(1).getViewContainer().gameViewport().set(0, 						-halfGameWindowHeight, 		halfGameWindowWidth, 	halfGameWindowHeight * 2);

			// huds
			mPlayerSessions.get(0).getViewContainer().hudViewport().set(-hudWindowWidth * .5f, 		-hudWindowHeight * .5f, 	hudWindowWidth, 		hudWindowHeight);
			mPlayerSessions.get(1).getViewContainer().hudViewport().set(-hudWindowWidth * .5f, 		-hudWindowHeight * .5f, 	hudWindowWidth, 		hudWindowHeight);

			break;

		case 3:
			
			// TODO: Enable viewport minimap
			
		case 4:

			// game render viewports
			mPlayerSessions.get(0).getViewContainer().gameViewport().set(-halfGameWindowWidth, 		-halfGameWindowHeight, 		halfGameWindowWidth, 	halfGameWindowHeight);
			mPlayerSessions.get(1).getViewContainer().gameViewport().set(0, 						-halfGameWindowHeight, 		halfGameWindowWidth, 	halfGameWindowHeight);
			mPlayerSessions.get(2).getViewContainer().gameViewport().set(-halfGameWindowWidth, 		0, 							halfGameWindowWidth, 	halfGameWindowHeight);
			mPlayerSessions.get(3).getViewContainer().gameViewport().set(0, 						0, 							halfGameWindowWidth, 	halfGameWindowHeight);

			// huds
			mPlayerSessions.get(0).getViewContainer().hudViewport().set(-halfHudWindowWidth, 		-halfHudWindowHeight, 		halfHudWindowWidth, 	halfHudWindowHeight);
			mPlayerSessions.get(1).getViewContainer().hudViewport().set(0, 							-halfHudWindowHeight, 		halfHudWindowWidth, 	halfHudWindowHeight);
			mPlayerSessions.get(2).getViewContainer().hudViewport().set(-halfHudWindowWidth, 		0, 							halfHudWindowWidth, 	halfHudWindowHeight);
			mPlayerSessions.get(3).getViewContainer().hudViewport().set(0, 							0, 							halfHudWindowWidth, 	halfHudWindowHeight);

			break;

		}
		//@formatter:on
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
			mPlayerSessions.get(3).isPlayerEnabled(true);
			break;
		case 3:
			mPlayerSessions.get(2).isPlayerEnabled(true);
			break;
		case 2:
			mPlayerSessions.get(1).isPlayerEnabled(true);
			break;

		default:
			// ignore
		}
	}

	public void disablePlayer() {
		if (mNumberActivePlayers <= 1)
			return;

		switch (mNumberActivePlayers) {
		case 4:
			mPlayerSessions.get(3).isPlayerEnabled(false);
			break;
		case 3:
			mPlayerSessions.get(2).isPlayerEnabled(false);
			break;
		case 2:
			mPlayerSessions.get(1).isPlayerEnabled(false);
			break;
		}

		mNumberActivePlayers--;
	}
}
