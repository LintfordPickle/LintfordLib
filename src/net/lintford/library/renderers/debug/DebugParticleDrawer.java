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

	public void debugDrawEnable(boolean pNewValue) {
		mShowDebugInformation = pNewValue;
	}

	public boolean debugDrawEnable() {
		return mShowDebugInformation;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugParticleDrawer(RendererManager pRendererManager, ParticleFrameworkData pParticleFramework, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mParticleFrameworkData = pParticleFramework;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void loadGLContent(ResourceManager pResourceManager) {
		super.loadGLContent(pResourceManager);

	}

	@Override
	public void unloadGLContent() {
		super.unloadGLContent();

	}

	@Override
	public boolean handleInput(LintfordCore pCore) {
		if (pCore.input().keyboard().isKeyDownTimed(GLFW.GLFW_KEY_F11)) {
			mShowDebugInformation = !mShowDebugInformation;

		}

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mShowDebugInformation)
			return;

		Debug.debugManager().drawers().beginTextRenderer(pCore.HUD());

		int lEmitterCount = mParticleFrameworkData.emitterManager().emitterInstances().size();
		Debug.debugManager().drawers().drawText(String.format("Num Emitters: %d", lEmitterCount), 0, 0);

		int lSystemCount = mParticleFrameworkData.particleSystemManager().getNumParticleSystems();
		Debug.debugManager().drawers().drawText(String.format("Num Systems: %d", lSystemCount), 0, 25);

		Debug.debugManager().drawers().endTextRenderer();

	}

}
