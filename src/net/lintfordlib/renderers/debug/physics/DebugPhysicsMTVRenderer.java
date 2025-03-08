package net.lintfordlib.renderers.debug.physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.physics.PhysicsWorld;
import net.lintfordlib.core.physics.collisions.ContactManifold;
import net.lintfordlib.core.physics.interfaces.ICollisionCallback;
import net.lintfordlib.core.rendering.RenderPass;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugPhysicsMTVRenderer extends BaseRenderer implements ICollisionCallback {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics World MTV Debug Renderer";

	public static final boolean ScaleToScreenCoords = true;
	public static final boolean RenderAABB = false;

	private class DebugContact {
		public float x, y;
		public float nx, ny;
		public float depth;
	}

	private static final int NUM_DEBUG_POINTS = 20;
	private int numAllocations;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PhysicsWorld mWorld;
	public final List<DebugContact> debugContactManifolds = new ArrayList<>();

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

	public DebugPhysicsMTVRenderer(RendererManager rendererManager, PhysicsWorld world, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);

		mWorld = world;

		for (int i = 0; i < NUM_DEBUG_POINTS; i++) {
			debugContactManifolds.add(new DebugContact());
		}
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		mWorld.addCollisionCallback(this);
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
	public void draw(LintfordCore core, RenderPass renderPass) {
		if (numAllocations == 0)
			return;

		final var lFontUnit = core.sharedResources().uiTextFont();
		final var lLineBatch = core.sharedResources().uiLineBatch();

		lFontUnit.begin(core.gameCamera());

		lLineBatch.lineType(GL11.GL_LINES);
		lLineBatch.begin(core.gameCamera());

		final var lToPixels = ConstantsPhysics.UnitsToPixels();

		for (int i = 0; i < numAllocations; i++) {
			final var lContact = debugContactManifolds.get(i);

			final var lCP0x = lContact.x;
			final var lCP0y = lContact.y;

			final var lMtvX = lContact.nx * lContact.depth;
			final var lMtvY = lContact.ny * lContact.depth;

			lLineBatch.draw(lCP0x * lToPixels, lCP0y * lToPixels, (lCP0x + lMtvX) * lToPixels, (lCP0y + lMtvY) * lToPixels, .01f, 1.f, 1.f, 1.f);
		}

		lLineBatch.end();
		lFontUnit.end();

		numAllocations = 0;
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void recordNewContact(ContactManifold contact) {
		if (numAllocations >= NUM_DEBUG_POINTS)
			return; // limit for this frame reached

		final var lNext = debugContactManifolds.get(numAllocations);
		lNext.x = contact.contact1.x;
		lNext.y = contact.contact1.y;
		lNext.nx = contact.normal.x;
		lNext.ny = contact.normal.y;
		lNext.depth = contact.depth;

		numAllocations++;

	}

	@Override
	public void preContact(ContactManifold manifold) {

	}

	@Override
	public void postContact(ContactManifold manifold) {
		for (int i = 0; i < manifold.contactCount; i++) {
			if (manifold.enableResolveContact && manifold.contactCount > 0)
				recordNewContact(manifold);
		}
	}

	@Override
	public void preSolve(ContactManifold manifold) {

	}

	@Override
	public void postSolve(ContactManifold manifold) {

	}
}
