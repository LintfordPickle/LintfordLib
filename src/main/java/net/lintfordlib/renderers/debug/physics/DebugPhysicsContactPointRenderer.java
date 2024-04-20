package net.lintfordlib.renderers.debug.physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.collisions.ContactManifold;
import net.lintfordlib.core.physics.interfaces.ICollisionCallback;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugPhysicsContactPointRenderer extends BaseRenderer implements ICollisionCallback {

	// ---------------------------------------------
	// Inner-Classes
	// ---------------------------------------------

	public class DebugContactPoint {
		public final Vector2f point = new Vector2f();
		public boolean isAssigned;
	}

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics World Contact Points Debug Renderer";

	public static final boolean ScaleToScreenCoords = true;
	public static final boolean RenderAABB = false;

	private static final int MAX_NUM_CONTACT_POINTS = 30;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PhysicsWorld mWorld;
	private int mCurrentCount;
	public final List<DebugContactPoint> debugContactPoints = new ArrayList<>();

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
		mWorld.addCollisionCallback(this);

		for (int i = 0; i < MAX_NUM_CONTACT_POINTS; i++) {
			debugContactPoints.add(new DebugContactPoint());
		}
	}

	@Override
	public void unloadResources() {
		super.unloadResources();

		mWorld.removeCollisionCallback(this);
	}

	@Override
	public void update(LintfordCore core) {
		super.update(core);

	}

	@Override
	public void draw(LintfordCore core) {
		final int lNumContactPoints = Math.min(debugContactPoints.size(), mCurrentCount);
		if (lNumContactPoints == 0)
			return;

		Debug.debugManager().drawers().beginPointRenderer(core.gameCamera());

		final var lToPixels = ConstantsPhysics.UnitsToPixels();
		for (int i = 0; i < lNumContactPoints; i++) {
			final var lContactPoint = debugContactPoints.get(i);
			Debug.debugManager().drawers().drawPoint(lContactPoint.point.x * lToPixels, lContactPoint.point.y * lToPixels, 1.1f, 0.7f, 1.f, 1.f);
		}

		GL11.glPointSize(6.f);
		Debug.debugManager().drawers().endPointRenderer();

		mCurrentCount = 0;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	@Override
	public void preContact(ContactManifold manifold) {

	}

	@Override
	public void postContact(ContactManifold manifold) {
		if (mWorld.currentIterationNr() != 0)
			return;

		if (mCurrentCount >= MAX_NUM_CONTACT_POINTS - 1)
			return;

		for (int i = 0; i < manifold.contactCount; i++) {
			if (i == 0)
				debugContactPoints.get(mCurrentCount).point.set(manifold.contact1);
			else
				debugContactPoints.get(mCurrentCount).point.set(manifold.contact2);

			mCurrentCount++;
		}
	}

	@Override
	public void preSolve(ContactManifold manifold) {

	}

	@Override
	public void postSolve(ContactManifold manifold) {

	}

}
