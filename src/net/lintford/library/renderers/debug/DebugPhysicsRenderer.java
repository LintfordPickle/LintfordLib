package net.lintford.library.renderers.debug;

import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.debug.Debug;
import net.lintford.library.core.physics.PhysicsWorld;
import net.lintford.library.core.physics.dynamics.RigidBody;
import net.lintford.library.renderers.BaseRenderer;
import net.lintford.library.renderers.RendererManager;

public class DebugPhysicsRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics World Debug Renderer";

	public static final boolean ScaleToScreenCoords = true;
	public static final boolean RenderAABB = false;

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PhysicsWorld mWorld;

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

	public DebugPhysicsRenderer(RendererManager rendererManager, PhysicsWorld world, int entityGroupID) {
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
	public void draw(LintfordCore core) {
		final var lLineBatch = rendererManager().uiLineBatch();

		final var lRigidBodies = mWorld.bodies();
		final int lNumOfBodies = lRigidBodies.size();

		lLineBatch.begin(core.gameCamera());
		for (int i = 0; i < lNumOfBodies; i++) {
			final var lBody = lRigidBodies.get(i);

			debugDrawRigidBody(core, lBody);
		}
		lLineBatch.end();
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void debugDrawRigidBody(LintfordCore core, RigidBody body) {
		final var lLineBatch = rendererManager().uiLineBatch();
		lLineBatch.lineType(GL11.GL_LINE_STRIP);

		float r = .6f;
		float g = .7f;
		float b = .2f;

		if (body.isStatic()) {
			r = .3f;
			g = .9f;
			b = .2f;
		}

		final var lUnitToPixels = ConstantsPhysics.UnitsToPixels();

		final var lVertices = body.getTransformedVertices();

		switch (body.shapeType()) {
		case Polygon:
		case Box: {
			if (!ScaleToScreenCoords) {
				lLineBatch.begin(core.gameCamera());
				lLineBatch.draw(lVertices.get(0).x, lVertices.get(0).y, lVertices.get(1).x, lVertices.get(1).y, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(1).x, lVertices.get(1).y, lVertices.get(2).x, lVertices.get(2).y, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(2).x, lVertices.get(2).y, lVertices.get(3).x, lVertices.get(3).y, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(3).x, lVertices.get(3).y, lVertices.get(0).x, lVertices.get(0).y, -0.01f, r, g, b, 1.f);
				lLineBatch.end();

			} else {
				lLineBatch.begin(core.gameCamera());
				lLineBatch.draw(lVertices.get(0).x * lUnitToPixels, lVertices.get(0).y * lUnitToPixels, lVertices.get(1).x * lUnitToPixels, lVertices.get(1).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(1).x * lUnitToPixels, lVertices.get(1).y * lUnitToPixels, lVertices.get(2).x * lUnitToPixels, lVertices.get(2).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(2).x * lUnitToPixels, lVertices.get(2).y * lUnitToPixels, lVertices.get(3).x * lUnitToPixels, lVertices.get(3).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
				lLineBatch.draw(lVertices.get(3).x * lUnitToPixels, lVertices.get(3).y * lUnitToPixels, lVertices.get(0).x * lUnitToPixels, lVertices.get(0).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
				lLineBatch.end();
			}

			break;
		}

		case LineWidth: {
			lLineBatch.begin(core.gameCamera());
			lLineBatch.draw(lVertices.get(0).x * lUnitToPixels, lVertices.get(0).y * lUnitToPixels, lVertices.get(1).x * lUnitToPixels, lVertices.get(1).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
			lLineBatch.draw(lVertices.get(1).x * lUnitToPixels, lVertices.get(1).y * lUnitToPixels, lVertices.get(2).x * lUnitToPixels, lVertices.get(2).y * lUnitToPixels, -0.01f, r, g, b, 1.f);
			lLineBatch.end();
			break;
		}

		case Circle: {

			if (!ScaleToScreenCoords)
				Debug.debugManager().drawers().drawCircleImmediate(core.gameCamera(), body.x, body.y, body.radius);

			else {
				lLineBatch.begin(core.gameCamera());
				lLineBatch.drawCircle(lVertices.get(0).x * lUnitToPixels, lVertices.get(0).y * lUnitToPixels, body.angle, body.radius * lUnitToPixels, 20, r, g, b, true);
				lLineBatch.end();
			}

			break;
		}

		}

		if (RenderAABB)
			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), body.aabb().x() * lUnitToPixels, body.aabb().y() * lUnitToPixels, body.aabb().width() * lUnitToPixels, body.aabb().height() * lUnitToPixels, .93f, .06f, .98f);
	}
}
