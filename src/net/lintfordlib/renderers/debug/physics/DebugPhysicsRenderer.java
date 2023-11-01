package net.lintfordlib.renderers.debug.physics;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.lintfordlib.ConstantsPhysics;
import net.lintfordlib.controllers.physics.PhysicsController;
import net.lintfordlib.core.LintfordCore;
import net.lintfordlib.core.debug.Debug;
import net.lintfordlib.core.maths.Vector2f;
import net.lintfordlib.core.physics.dynamics.RigidBody;
import net.lintfordlib.renderers.BaseRenderer;
import net.lintfordlib.renderers.RendererManager;

public class DebugPhysicsRenderer extends BaseRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	public static final String RENDERER_NAME = "Physics World Debug Renderer";

	public static final boolean RenderAABB = false;

	public static final boolean RENDER_CONTACT_POINTS = true;
	public static final List<Vector2f> DebugContactPoints = new ArrayList<>();

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private PhysicsController mPhysicsController;

	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	@Override
	public boolean isInitialized() {
		return mPhysicsController != null;
	}

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public DebugPhysicsRenderer(RendererManager rendererManager, int entityGroupID) {
		super(rendererManager, RENDERER_NAME, entityGroupID);
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	@Override
	public void initialize(LintfordCore core) {
		final var lControllerManager = core.controllerManager();
		mPhysicsController = (PhysicsController) lControllerManager.getControllerByNameRequired(PhysicsController.CONTROLLER_NAME, mEntityGroupUid);
	}

	@Override
	public void draw(LintfordCore core) {
		final var lLineBatch = rendererManager().uiLineBatch();

		final var lRigidBodies = mPhysicsController.world().bodies();
		final int lNumOfBodies = lRigidBodies.size();

		lLineBatch.begin(core.gameCamera());
		for (int i = 0; i < lNumOfBodies; i++) {
			final var lBody = lRigidBodies.get(i);

			debugDrawRigidBody(core, lBody);
		}
		lLineBatch.end();

		if (RENDER_CONTACT_POINTS) {
			drawDebugContactPoints(core);
			DebugContactPoints.clear();
		}
	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

	private void drawDebugContactPoints(LintfordCore core) {
		final int lNumDebugContactPoints = DebugContactPoints.size();
		if (lNumDebugContactPoints == 0)
			return;

		final var lToPixels = ConstantsPhysics.UnitsToPixels();

		Debug.debugManager().drawers().beginPointRenderer(core.gameCamera());
		for (int i = 0; i < lNumDebugContactPoints; i++) {
			GL11.glPointSize(5.f);
			final var lDebugContactPoint = DebugContactPoints.get(i);
			Debug.debugManager().drawers().drawPoint(lDebugContactPoint.x * lToPixels, lDebugContactPoint.y * lToPixels);
		}
		Debug.debugManager().drawers().endPointRenderer();
	}

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

		if (body.debugIsSelected) {
			r = .96f;
			g = .92f;
			b = .09f;
		}

		if (body.debugIsColliding) {
			r = .66f;
			g = .62f;
			b = .69f;
		}

		final var lUnitToPixels = ConstantsPhysics.UnitsToPixels();

		final var lVertices = body.getWorldVertices();
		final var lShape = body.shape();

		lLineBatch.lineWidth(2.f);
		switch (lShape.shapeType()) {
		case Polygon: {

			lLineBatch.begin(core.gameCamera());

			final int lNumVertices = lVertices.size();
			for (int i = 0; i < lNumVertices; i++) {
				final var v0 = lVertices.get(i);
				final var t = (i + 1) % lNumVertices;
				final var v1 = lVertices.get(t);

				lLineBatch.draw(v0.x * lUnitToPixels, v0.y * lUnitToPixels, v1.x * lUnitToPixels, v1.y * lUnitToPixels, -0.01f, r, g, b, 1.f);
			}

			lLineBatch.end();

			break;
		}

		case LineWidth: {
			final var lHeight = lShape.height() * lUnitToPixels * .5f;

			final var sx = lVertices.get(0).x * lUnitToPixels;
			final var sy = lVertices.get(0).y * lUnitToPixels;

			final var ex = lVertices.get(1).x * lUnitToPixels;
			final var ey = lVertices.get(1).y * lUnitToPixels;

			final var lDst = Vector2f.dst(sx, sy, ex, ey);
			final var linevx = (ex - sx) / lDst;
			final var linevy = (ey - sy) / lDst;

			final var tl_x = sx - -linevy * lHeight;
			final var tl_y = sy - linevx * lHeight;
			final var tr_x = ex - -linevy * lHeight;
			final var tr_y = ey - +linevx * lHeight;
			final var br_x = ex + -linevy * lHeight;
			final var br_y = ey + linevx * lHeight;
			final var bl_x = sx + -linevy * lHeight;
			final var bl_y = sy + linevx * lHeight;

			lLineBatch.begin(core.gameCamera());
			lLineBatch.draw(tl_x, tl_y, tr_x, tr_y, -0.01f, r, g, b, 1.f);
			lLineBatch.draw(tr_x, tr_y, br_x, br_y, -0.01f, r, g, b, 1.f);
			lLineBatch.draw(br_x, br_y, bl_x, bl_y, -0.01f, r, g, b, 1.f);
			lLineBatch.draw(bl_x, bl_y, tl_x, tl_y, -0.01f, r, g, b, 1.f);
			lLineBatch.end();

			lLineBatch.begin(core.gameCamera());
			lLineBatch.drawCircle(sx, sy, body.transform.angle, lHeight, 13, r, g, b, true);
			lLineBatch.drawCircle(ex, ey, body.transform.angle, lHeight, 13, r, g, b, true);
			lLineBatch.end();

			break;
		}

		case Circle: {
			lLineBatch.begin(core.gameCamera());
			lLineBatch.drawCircle(lVertices.get(0).x * lUnitToPixels, lVertices.get(0).y * lUnitToPixels, body.transform.angle, lShape.radius() * lUnitToPixels, 20, r, g, b, true);
			lLineBatch.end();

			break;
		}

		}

		// Render body center
		lLineBatch.begin(core.gameCamera());
		lLineBatch.drawCircle(body.transform.p.x * lUnitToPixels, body.transform.p.y * lUnitToPixels, body.transform.angle, 3, 20, 0.8f, .4f, .4f, true);
		lLineBatch.end();

		// Render centroid
		GL11.glPointSize(3.f);

		final var localCenterPoint = new Vector2f(lShape.localCenter).mul(body.transform.q).add(body.transform.p);
		Debug.debugManager().drawers().drawPointImmediate(core.gameCamera(), localCenterPoint.x * lUnitToPixels, localCenterPoint.y * lUnitToPixels);

		if (RenderAABB)
			Debug.debugManager().drawers().drawRectImmediate(core.gameCamera(), body.aabb().x() * lUnitToPixels, body.aabb().y() * lUnitToPixels, body.aabb().width() * lUnitToPixels, body.aabb().height() * lUnitToPixels, .93f, .06f, .98f);
	}
}
