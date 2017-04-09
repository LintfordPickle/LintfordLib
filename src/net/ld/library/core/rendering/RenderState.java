package net.ld.library.core.rendering;

import net.ld.library.core.camera.Camera;
import net.ld.library.core.camera.HUD;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.time.GameTime;

/**
 * The {@link RenderState} class contains information and object references for
 * use when rendering in OpenGL.
 */
// TODO: it would make sense to improve this class to allow it to be easily
// extended for adding things like RENDER_PASSES for game specific cases
public class RenderState {

	// =============================================
	// Constants
	// =============================================

	public enum RENDER_PASS {
		diffuse,

	}

	// =============================================
	// Variables
	// =============================================

	private DisplayConfig mDisplayConfig;
	private HUD mHUDCamera;
	private Camera mGameCamera;
	private RENDER_PASS mRenderPass;
	private GameTime mGameTime;

	// =============================================
	// Properties
	// =============================================

	/**
	 * Returns the {@link DisplayConfig} instance used when creating the LWJGL
	 * window.
	 */
	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	/** Returns the {@link GameTime} instance. */
	public GameTime gameTime() {
		return mGameTime;
	}

	/**
	 * Returns the HUD camera instance, for use when rendering objects in HUD
	 * space.
	 */
	public HUD hudCamera() {
		return mHUDCamera;
	}

	/**
	 * Returns the {@link ICamera} instance, for use when rendering objects in
	 * game-world space.
	 */
	public Camera gameCamera() {
		return mGameCamera;
	}

	/** Sets the game {@link Camera}. */
	public void gameCamera(Camera pNewCamera) {
		mGameCamera = pNewCamera;
	}

	/**
	 * Returns the {@link RENDER_PASS} instance which indicates the current
	 * render pass (e.g. diffuse, normal etc.)
	 */
	public RENDER_PASS renderPass() {
		return mRenderPass;
	}

	// =============================================
	// Constructor
	// =============================================

	/**
	 * Creates a new instance of {@link RenderState}. Sets the current
	 * {@link RENDER_PASS} to diffuse.
	 */
	public RenderState() {
		mRenderPass = RENDER_PASS.diffuse;
	}

	// =============================================
	// Core-Methods
	// =============================================

	/** Initialises the {@link RenderState} instance. */
	public void initialise(HUD pHUD, Camera pCamera, GameTime pGameTime, DisplayConfig pDisplayConfig) {

		mDisplayConfig = pDisplayConfig;
		mHUDCamera = pHUD;
		mGameCamera = pCamera;
		mGameTime = pGameTime;

	}

	// =============================================
	// Methods
	// =============================================

	/**
	 * Sets the current {@link RENDER_PASS} enum. This is used by other objects
	 * when drawing.
	 */
	public void setRenderPass(final RENDER_PASS pNewRenderPass) {
		mRenderPass = pNewRenderPass;

	}

}
