package net.lintford.library.renderers.debug;

import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.particles.ParticleFrameworkData;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugParticleDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Particle Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private boolean mShowDebugInformation;

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
		if (core.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F11)) {
			mShowDebugInformation = !mShowDebugInformation;
		}

		return super.handleInput(core);
	}

	@Override
	public void draw(LintfordCore core) {
		if (!mShowDebugInformation)
			return;

		Debug.debugManager().drawers().beginTextRenderer(core.HUD());

		int lEmitterCount = mParticleFrameworkData.emitterManager().emitterInstances().size();
		Debug.debugManager().drawers().drawText(String.format("Num Emitters: %d", lEmitterCount), 0, 0);

		int lSystemCount = mParticleFrameworkData.particleSystemManager().getNumParticleSystems();
		Debug.debugManager().drawers().drawText(String.format("Num Systems: %d", lSystemCount), 0, 25);

		Debug.debugManager().drawers().endTextRenderer();
	}
}
