package net.lintfordlib.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintfordlib.assets.ResourceManager;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.input.mouse.IInputProcessor;
import net.lintfordlib.core.particles.ParticleFrameworkData;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugParticleDrawer extends BaseRenderer implements IInputProcessor {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Particle Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mShowDebugInformation;
	private float mInputTimer;
	private ParticleFrameworkData mParticleFrameworkData;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public boolean isInitialized() {
		return true;
	}

	public void debugDrawEnable(boolean newValue) {
		mShowDebugInformation = newValue;
	}

	public boolean debugDrawEnable() {
		return mShowDebugInformation;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugParticleDrawer(RendererManager rendererManager, ParticleFrameworkData particleFramework, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mParticleFrameworkData = particleFramework;

		isActive(false);
	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore core) {

	}

	@Override
	public void loadResources(ResourceManager resourceManager) {
		super.loadResources(resourceManager);
	}

	@Override
	public void unloadResources() {
		super.unloadResources();
	}

	@Override
	public boolean handleInput(LintfordCore core) {
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F11, this)) {
			mShowDebugInformation = !mShowDebugInformation;
		}

		return super.handleInput(core);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		if (mInputTimer > 0) {
			final var lDeltaTime = (float) core.gameTime().elapsedTimeMilli();
			mInputTimer -= lDeltaTime;
		}
	}

	@Override
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (!mShowDebugInformation)
			return;

		Debug.debugManager().drawers().beginTextRenderer(core.HUD());

		int lEmitterCount = mParticleFrameworkData.particleEmitterManager().emitterInstances().size();
		Debug.debugManager().drawers().drawText(String.format("Num Emitters: %d", lEmitterCount), 0, 0);

		int lSystemCount = mParticleFrameworkData.particleSystemManager().getNumParticleSystems();
		Debug.debugManager().drawers().drawText(String.format("Num Systems: %d", lSystemCount), 0, 25);

		Debug.debugManager().drawers().endTextRenderer();
	}

	@Override
	public boolean isCoolDownElapsed() {
		return mInputTimer <= 0;
	}

	@Override
	public void resetCoolDownTimer() {
		mInputTimer = IInputProcessor.INPUT_COOLDOWN_TIME;
	}

	@Override
	public boolean allowGamepadInput() {
		return false;
	}

	@Override
	public boolean allowKeyboardInput() {
		return true;
	}

	@Override
	public boolean allowMouseInput() {
		return false;
	}
}
