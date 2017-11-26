package net.lintford.library.core.rendering;

import net.lintford.library.core.camera.ICamera;
import net.lintford.library.core.time.GameTime;
import net.lintford.library.options.DisplayConfig;

/**
 * The {@link RenderState} class contains information and object references for use when rendering in OpenGL.
 */
// TODO: it would make sense to improve this class to allow it to be easily
// extended for adding things like RENDER_PASSES for game specific cases
public class RenderState {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public enum RENDER_PASS {
		diffuse,

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private DisplayConfig mDisplayConfig;
	private ICamera mHUDCamera;
	private ICamera mGameCamera;
	private RENDER_PASS mRenderPass;
	private GameTime mGameTime;

	// --------------------------------------
	// Properties
	// --------------------------------------

	/**
	 * Returns the {@link DisplayConfig} instance used when creating the LWJGL window.
	 */
	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	/** Returns the {@link GameTime} instance. */
	public GameTime gameTime() {
		return mGameTime;
	}

	/**
	 * Returns the HUD {@link ICamera} instance assigned to this {@link RenderState}.
	 */
	public ICamera HUDCamera() {
		return mHUDCamera;
	}

	/**
	 * Returns the game {@link ICamera} instance assigned to this {@link RenderState}. This can return null if no game camera has been explicitly set!
	 */
	public ICamera gameCamera() {
		return mGameCamera;
	}

	/**
	 * Returns the {@link RENDER_PASS} instance which indicates the current render pass (e.g. diffuse, normal etc.)
	 */
	public RENDER_PASS renderPass() {
		return mRenderPass;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	/**
	 * Creates a new instance of {@link RenderState}. Sets the current {@link RENDER_PASS} to diffuse.
	 */
	public RenderState(DisplayConfig pDisplayConfig, ICamera pHUDCamera, GameTime pGameTime) {
		mRenderPass = RENDER_PASS.diffuse;

		mHUDCamera = pHUDCamera;
		mDisplayConfig = pDisplayConfig;
		mGameTime = pGameTime;

	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	// --------------------------------------
	// Methods
	// --------------------------------------

	public void setGameCamera(ICamera pGameCamera) {
		mGameCamera = pGameCamera;
	}

	/**
	 * Sets the current {@link RENDER_PASS} enum. This is used by other objects when drawing.
	 */
	public void setRenderPass(final RENDER_PASS pNewRenderPass) {
		mRenderPass = pNewRenderPass;

	}

}
