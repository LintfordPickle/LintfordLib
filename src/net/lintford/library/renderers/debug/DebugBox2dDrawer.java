package net.lintford.library.renderers.debug;

import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.JBox2dDebugDrawer;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugBox2dDrawer extends BaseRenderer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	public static final String RENDERER_NAME = "DEBUG Box2d Outlines";

	// --------------------------------------
	// Variables
	// --------------------------------------

	private JBox2dDebugDrawer mJBox2dDebugDrawer;

	// --------------------------------------
	// Properties
	// --------------------------------------

	@Override
	public int ZDepth() {
		return 100;
	}
	
	@Override
	public boolean isInitialized() {
		return true;

	}

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public DebugBox2dDrawer(RendererManager pRendererManager, World pWorld, int pEntityGroupID) {
		super(pRendererManager, RENDERER_NAME, pEntityGroupID);

		mJBox2dDebugDrawer = new JBox2dDebugDrawer(pWorld);
		mIsActive = false;

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	@Override
	public void initialize(LintfordCore pCore) {

	}

	@Override
	public void draw(LintfordCore pCore) {
		if (!isActive() || !isInitialized())
			return;

		mJBox2dDebugDrawer.draw(pCore);

	}

}
