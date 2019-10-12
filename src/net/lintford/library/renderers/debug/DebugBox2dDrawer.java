package net.lintford.library.renderers.debug;

import org.jbox2d.dynamics.World;
import org.lwjgl.glfw.GLFW;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.debug.JBox2dDebugDrawer;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugBox2dDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DebugBox2dDrawer";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private JBox2dDebugDrawer mJBox2dDebugDrawer;
	private boolean mShowDebugInformation;

	// --------------------------------------
	// Properties
	// --------------------------------------

	public void debugDrawEnable(boolean pNewValue) {
		mShowDebugInformation = pNewValue;
	}

	public boolean debugDrawEnable() {
		return mShowDebugInformation;
	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugBox2dDrawer(RendererManager pRendererManager, World pWorld, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mJBox2dDebugDrawer = new JBox2dDebugDrawer(pWorld);

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {
		// TODO Auto-generated method stub

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

		if (pCore.input().keyDownTimed(GLFW.GLFW_KEY_F5)) {
			mShowDebugInformation = !mShowDebugInformation;
		}

		return super.handleInput(pCore);
	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!mShowDebugInformation)
			return;

		mJBox2dDebugDrawer.draw(pCore);

	}

}
