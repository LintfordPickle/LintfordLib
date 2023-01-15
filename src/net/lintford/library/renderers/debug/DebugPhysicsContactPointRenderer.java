package net.lintford.library.renderers.debug;

import java.util.ArrayList;
import java.util.List;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.core.physics.PhysicsWorld;
import net.lintford.library.core.physics.collisions.ContactManifold;
import net.lintford.library.core.physics.interfaces.ICollisionCallback;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugPhysicsContactPointRenderer extends BaseRenderer implements ICollisionCallback {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics World Contact Points Debug Renderer";

	public static final boolean ScaleToScreenCoords = true;
	public static final boolean RenderAABB = false;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PhysicsWorld mWorld;
	public final List<Vector2f> debugContactPoints = new ArrayList<>();

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mWorld != null;

	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DebugPhysicsContactPointRenderer(RendererManager rendererManager, PhysicsWorld world, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mWorld = world;
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {

	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

		debugContactPoints.clear();
	}

	@Override
	public void draw(LintfordCore core) {
		final var lNumContactPoints = debugContactPoints.size();
		if (lNumContactPoints == 0)
			return;

		Debug.debugManager().drawers().beginPointRenderer(core.gameCamera());

		for (int i = 0; i < lNumContactPoints; i++) {
			final var lContactPoint = debugContactPoints.get(i);
			Debug.debugManager().drawers().drawPoint(lContactPoint.x, lContactPoint.y, 1.f, 0.f, 0.f, 1.f);
		}

		Debug.debugManager().drawers().endPointRenderer();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void preContact(ContactManifold manifold) {

	}

	@Override
	public void postContact(ContactManifold manifold) {
		// TODO: Garbage
		for (int i = 0; i < manifold.contactCount; i++) {
			if (i == 0)
				debugContactPoints.add(new Vector2f(manifold.contact1));
			else
				debugContactPoints.add(new Vector2f(manifold.contact2));
		}
	}

	@Override
	public void preSolve(ContactManifold manifold) {

	}

	@Override
	public void postSolve(ContactManifold manifold) {

	}
}
