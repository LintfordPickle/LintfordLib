package net.lintford.library.core.debug;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public class JBox2dDebugDrawer {

	// --------------------------------------
	// Constants
	// --------------------------------------
	
	static final int MAX_VERTS = 10;
	static Vector2f[] verts;// = new Vector2f[MAX_VERTS];
	static Vec2 vertex = new Vec2();

	static int bodyCount;

	private static class RenderOfPolyFixture {
		public static void draw(LintfordCore pCore, Body pBody, Fixture pFixture) {

			if (verts == null) {
				verts = new Vector2f[MAX_VERTS];
				for (int i = 0; i < MAX_VERTS; i++) {
					verts[i] = new Vector2f();
				}
			}

			PolygonShape fixtureShape = (PolygonShape) pFixture.getShape();
			int vSize = Math.min(fixtureShape.getVertexCount(), MAX_VERTS);

			for (int i = 0; i < vSize; i++) {
				vertex = fixtureShape.getVertex(i);

				Vec2 worldPoint = pBody.getWorldPoint(vertex);

				verts[i].x = worldPoint.x * (32f);
				verts[i].y = worldPoint.y * (32f);

			}

			float lR = 1f;
			float lG = 1f;
			float lB = 1f;

			if (pBody.m_type == BodyType.DYNAMIC) {
				lR = 0.05f;
				lG = 0.09f;
				lB = 0.87f;
			} else if (pBody.m_type == BodyType.STATIC) {
				lR = 0.05f;
				lG = 1f;
				lB = 0.09f;
			} else if (pBody.m_type == BodyType.KINEMATIC) {
				lR = 0.87f;
				lG = 0.05f;
				lB = 0.09f;
			}

			Debug.debugManager().drawers().drawPoly(pCore.gameCamera(), verts, vSize, lR, lG, lB, true);

		}

	}

	private static class RenderOfCircleFixture {
		public static void draw(LintfordCore pCore, Body pBody, Fixture pFixture) {

		}

	}

	private static class DebugRenderBody {
		public static void draw(LintfordCore pCore, Body pBody) {
			Fixture lFixture = pBody.getFixtureList();

			// Draw the body here ?
			float lBodyX = pBody.getPosition().x * 32f;
			float lBodyY = pBody.getPosition().y * 32f;

			Debug.debugManager().drawers().startLineRenderer(pCore.gameCamera());
			Debug.debugManager().drawers().drawLine(lBodyX - 10, lBodyY, lBodyX + 10, lBodyY);
			Debug.debugManager().drawers().drawLine(lBodyX, lBodyY - 10, lBodyX, lBodyY + 10);
			Debug.debugManager().drawers().endLineRenderer();

			while (lFixture != null) {
				// TODO: update the color depending on the state

				if (lFixture.getShape().getType() == ShapeType.CIRCLE) {
					RenderOfCircleFixture.draw(pCore, pBody, lFixture);

				} else {
					RenderOfPolyFixture.draw(pCore, pBody, lFixture);

				}

				lFixture = lFixture.getNext();

			}

		}

	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private World mWorld;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dDebugDrawer(World pWorld) {
		mWorld = pWorld;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore pCore) {

		Rectangle lHUDrect = pCore.HUD().boundingRectangle();

		Debug.debugManager().drawers().startText(pCore.HUD());
		Debug.debugManager().drawers().drawText("# Bodies: " + mWorld.getBodyCount(), lHUDrect.left() + 5, lHUDrect.bottom() - 85);
		Debug.debugManager().drawers().drawText("# Contacts: " + mWorld.getContactCount(), lHUDrect.left() + 5, lHUDrect.bottom() - 65);
		Debug.debugManager().drawers().endText();

		Body lBody = mWorld.getBodyList();
		while (lBody != null) {

			DebugRenderBody.draw(pCore, lBody);

			lBody = lBody.getNext();

		}

	}

}