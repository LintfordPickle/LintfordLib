package net.lintford.library.core.debug;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.particle.ParticleGroup;
import org.lwjgl.opengl.GL11;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.geometry.Rectangle;
import net.lintford.library.core.maths.Vector2f;

public class JBox2dDebugDrawer {

	// --------------------------------------
	// Constants
	// --------------------------------------

	static final int MAX_VERTS = 10;
	static List<Vector2f> verts;
	static Vec2 vertex = new Vec2();
	static Vec2 tempVec = new Vec2();

	static int bodyCount;

	private static class RenderOfPolyFixture {
		public static void draw(LintfordCore pCore, Body pBody, Fixture pFixture) {

			if (verts == null) {
				verts = new ArrayList<Vector2f>(MAX_VERTS);
				for (int i = 0; i < MAX_VERTS; i++) {
					verts.add(new Vector2f());
				}
			}

			PolygonShape fixtureShape = (PolygonShape) pFixture.getShape();
			int vSize = Math.min(fixtureShape.getVertexCount(), MAX_VERTS);

			for (int i = 0; i < vSize; i++) {
				vertex = fixtureShape.getVertex(i);

				Vec2 worldPoint = pBody.getWorldPoint(vertex);

				verts.get(i).set(worldPoint.x * (32f), worldPoint.y * (32f));

			}

			float lR = 1f;
			float lG = 1f;
			float lB = 1f;

			if (pBody.m_type == BodyType.DYNAMIC) {
				lR = 0.05f;
				lG = 0.09f;
				lB = pBody.isAwake() ? 0.87f : 0.04f;
			} else if (pBody.m_type == BodyType.STATIC) {
				lR = 0.05f;
				lG = 1f;
				lB = 0.09f;
			} else if (pBody.m_type == BodyType.KINEMATIC) {
				lR = 0.87f;
				lG = 0.05f;
				lB = 0.09f;
			}

			if (pFixture.isSensor()) {
				lR = 255f / 255f;
				lG = 106f / 255f;
				lB = 0f / 255f;

			}

			Debug.debugManager().drawers().drawPoly(verts, vSize, lR, lG, lB, true);

		}

	}

	private static class RenderOfCircleFixture {
		public static void draw(LintfordCore pCore, Body pBody, Fixture pFixture) {
			Fixture lFixture = pBody.getFixtureList();

			float lBodyX = pBody.getPosition().x * 32f;
			float lBodyY = pBody.getPosition().y * 32f;

			while (lFixture != null) {
				Shape lCircleShape = pFixture.getShape();
				final float lRadius = lCircleShape.getRadius() * 32f;
				final float lAngle = pBody.getAngle();

				Debug.debugManager().drawers().drawCircle(lBodyX, lBodyY, lRadius, lAngle, 10, GL11.GL_LINE_STRIP);

				lFixture = lFixture.getNext();

			}

		}

	}

	private static class DebugRenderBody {
		public static void draw(LintfordCore pCore, Body pBody) {
			Fixture lFixture = pBody.getFixtureList();

			float lBodyX = pBody.getPosition().x * 32f;
			float lBodyY = pBody.getPosition().y * 32f;

			Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera(), GL11.GL_LINES);
			Debug.debugManager().drawers().drawLine(lBodyX - 10, lBodyY, lBodyX + 10, lBodyY);
			Debug.debugManager().drawers().drawLine(lBodyX, lBodyY - 10, lBodyX, lBodyY + 10);
			Debug.debugManager().drawers().endLineRenderer();

			Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera(), GL11.GL_LINES);

			// polygon fixtures
			while (lFixture != null) {
				// TODO: update the color depending on the state

				if (lFixture.getShape().getType() == ShapeType.POLYGON) {
					RenderOfPolyFixture.draw(pCore, pBody, lFixture);

				}

				lFixture = lFixture.getNext();

			}
			Debug.debugManager().drawers().endLineRenderer();

			Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera(), GL11.GL_LINE_STRIP);
			// Circle fixtures
			lFixture = pBody.getFixtureList();
			while (lFixture != null) {
				// TODO: update the color depending on the state

				if (lFixture.getShape().getType() == ShapeType.CIRCLE) {
					RenderOfCircleFixture.draw(pCore, pBody, lFixture);

				}

				lFixture = lFixture.getNext();

			}
			Debug.debugManager().drawers().endLineRenderer();

		}

	}

	private static class DebugRenderJoint {
		public static void draw(LintfordCore pCore, Joint pJoint) {

			if (pJoint == null)
				return;

			Debug.debugManager().drawers().beginLineRenderer(pCore.gameCamera());
			Debug.debugManager().drawers().beginPointRenderer(pCore.gameCamera());

			if (pJoint instanceof RevoluteJoint) {
				RevoluteJoint lRevoluteJoint = (RevoluteJoint) pJoint;
				lRevoluteJoint.getAnchorA(tempVec);

				GL11.glPointSize(10f);
				float lAnchorAX = tempVec.x * Box2dWorldController.UNITS_TO_PIXELS;
				float lAnchorAY = tempVec.y * Box2dWorldController.UNITS_TO_PIXELS;
				Debug.debugManager().drawers().drawPoint(lAnchorAX, lAnchorAY, 255f / 255f, 117f / 255f, 104f / 255f, 1f);

				lRevoluteJoint.getAnchorB(tempVec);
				float lAnchorBX = tempVec.x * Box2dWorldController.UNITS_TO_PIXELS;
				float lAnchorBY = tempVec.y * Box2dWorldController.UNITS_TO_PIXELS;
				Debug.debugManager().drawers().drawPoint(lAnchorBX, lAnchorBY, 255f / 255f, 117f / 255f, 104f / 255f, 1f);

				// Render reference angle
				float lRefAngle = lRevoluteJoint.getReferenceAngle();
				float lRefPointEndX = (float) Math.cos(lRefAngle) * 15f;
				float lRefPointEndY = (float) Math.sin(lRefAngle) * 15f;
				Debug.debugManager().drawers().drawLine(lAnchorBX, lAnchorBY, lAnchorBX + lRefPointEndX, lAnchorBY + lRefPointEndY, 0f, 1f, 0f);

				// Render angle
				float lAngle = lRevoluteJoint.getJointAngle();
				float lAngleEndX = (float) Math.cos(lAngle) * 10f;
				float lAngleEndY = (float) Math.sin(lAngle) * 10f;
				Debug.debugManager().drawers().drawLine(lAnchorBX, lAnchorBY, lAnchorBX + lAngleEndX, lAnchorBY + lAngleEndY, 1f, 1f, 0f);
				
			}

			Debug.debugManager().drawers().endPointRenderer();
			Debug.debugManager().drawers().endLineRenderer();

		}

	}

	private static class DebugRenderParticles {
		public static void draw(LintfordCore pCore, ParticleGroup pParticleSystem) {

			if (pParticleSystem == null)
				return;

			Debug.debugManager().drawers().beginPointRenderer(pCore.gameCamera());

			Debug.debugManager().drawers().endPointRenderer();

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

		final int lLineHeight = -20;
		int lLinePos = 100;

		Debug.debugManager().drawers().beginTextRenderer(pCore.HUD());
		Debug.debugManager().drawers().drawText("# Contacts: " + mWorld.getContactCount(), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().drawText(String.format("# Fixtures: ??"), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().drawText(String.format("# Bodies: %d", mWorld.getBodyCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().drawText(String.format("# Bodies: %d", mWorld.getBodyCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().drawText(String.format("# Joints: %d", mWorld.getJointCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().drawText(String.format("# Particles: %d", mWorld.getParticleCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
		Debug.debugManager().drawers().endTextRenderer();

		Debug.debugManager().drawers().beginPolyRenderer(pCore.gameCamera());
		Body lBody = mWorld.getBodyList();
		while (lBody != null) {

			DebugRenderBody.draw(pCore, lBody);

			lBody = lBody.getNext();

		}
		Debug.debugManager().drawers().endPolyRenderer();

		Joint lJoint = mWorld.getJointList();
		while (lJoint != null) {

			DebugRenderJoint.draw(pCore, lJoint);

			lJoint = lJoint.getNext();

		}

		final var lParticleSystems = mWorld.getParticleGroupList();

		if (lParticleSystems != null) {
			final int lParticleGroupCount = lParticleSystems.length;
			for (int i = 0; i < lParticleGroupCount; i++) {
				ParticleGroup lPG = lParticleSystems[i];
				if (lPG == null)
					continue;

				DebugRenderParticles.draw(pCore, lPG);

			}

		}

	}

}