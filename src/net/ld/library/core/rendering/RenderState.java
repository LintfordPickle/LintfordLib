package net.ld.library.core.rendering;

import net.ld.library.core.camera.ICamera;
import net.ld.library.core.config.DisplayConfig;
import net.ld.library.core.time.GameTime;

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
	private ICamera mHUDCamera;
	private ICamera mGameCamera;
	private RENDER_PASS mRenderPass;
	private GameTime mGameTime;

	// =============================================
	// Properties
	// =============================================

	public DisplayConfig displayConfig() {
		return mDisplayConfig;
	}

	public GameTime gameTime() {
		return mGameTime;
	}

	public ICamera hudCamera() {
		return mHUDCamera;
	}

	public ICamera gameCamera() {
		return mGameCamera;
	}

	public void gameCamera(ICamera pNewCamera) {
		mGameCamera = pNewCamera;
	}

	public RENDER_PASS renderPass() {
		return mRenderPass;
	}

	// =============================================
	// Constructor
	// =============================================

	public RenderState() {
		mRenderPass = RENDER_PASS.diffuse;
	}

	// =============================================
	// Core-Methods
	// =============================================

	public void initialise(ICamera pHUD, ICamera pCamera, GameTime pGameTime, DisplayConfig pDisplayConfig) {

		mDisplayConfig = pDisplayConfig;
		mHUDCamera = pHUD;
		mGameCamera = pCamera;
		mGameTime = pGameTime;

	}

	public void update(GameTime pGameTime) {

		mHUDCamera.update(pGameTime);
		mGameCamera.update(pGameTime);

	}

	// =============================================
	// Methods
	// =============================================

	public void setRenderPass(RENDER_PASS pNewRenderPass) {
		mRenderPass = pNewRenderPass;
	}

}
