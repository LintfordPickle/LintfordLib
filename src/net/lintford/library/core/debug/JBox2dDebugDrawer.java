package net.lintford.library.core.debug;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.particle.ParticleColor;
import org.lwjgl.opengl.GL11;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.maths.Vector2f;

public class JBox2dDebugDrawer {

	// --------------------------------------
	// Debug Constants
	// --------------------------------------

	public static boolean DEBUG_DRAW_WORLD_INFO = true;
	public static boolean DEBUG_DRAW_BODY_CENTER = true;

	public static boolean DEBUG_DRAW_JOINT_REVOLUTE = true;
	public static boolean DEBUG_DRAW_JOINT_PRISMATIC = true;

	public static boolean DEBUG_DRAW_PARTICLE_POINTS = true;

	public static float DEBUG_DRAW_PARTICLE_POINT_SIZE = 4.f;

	// --------------------------------------
	// Constants
	// --------------------------------------

	static final int NUM_POINTS_IN_CIRCLE = 12;
	static final int MAX_VERTS = 10;

	static List<Vector2f> verts;
	static Vec2 vertex = new Vec2();
	static Vec2 tempVec = new Vec2();

	private static int fixtureCountAtLastDraw;

	// --------------------------------------
	// Debug Draw Methods
	// --------------------------------------

	private static class RenderOfPolyFixture {
		public static void draw(LintfordCore core, Body body, Fixture fixture) {

			if (verts == null) {
				verts = new ArrayList<Vector2f>(MAX_VERTS);
				for (int i = 0; i < MAX_VERTS; i++) {
					verts.add(new Vector2f());
				}
			}

			final var fixtureShape = (PolygonShape) fixture.getShape();
			final int vSize = Math.min(fixtureShape.getVertexCount(), MAX_VERTS);

			for (int i = 0; i < vSize; i++) {
				vertex = fixtureShape.getVertex(i);

				final var lWorldPositionVec2 = body.getWorldPoint(vertex);

				final float lWorldPositionX = ConstantsPhysics.toPixels(lWorldPositionVec2.x);
				final float lWorldPositionY = ConstantsPhysics.toPixels(lWorldPositionVec2.y);

				verts.get(i).set(lWorldPositionX, lWorldPositionY);
			}

			float lR = 1f;
			float lG = 1f;
			float lB = 1f;

			if (body.isAwake()) {
				if (body.m_type == BodyType.DYNAMIC) {
					lR = 0.05f;
					lG = 0.09f;
					lB = body.isAwake() ? 0.87f : 0.04f;
				} else if (body.m_type == BodyType.STATIC) {
					lR = 0.05f;
					lG = 1f;
					lB = 0.09f;
				} else if (body.m_type == BodyType.KINEMATIC) {
					lR = 0.87f;
					lG = 0.05f;
					lB = 0.09f;
				}
			} else {
				lR = 0.07f;
				lG = 0.05f;
				lB = 0.09f;
			}

			if (fixture.isSensor()) {
				lR = 255f / 255f;
				lG = 106f / 255f;
				lB = 0f / 255f;
			}

			GL11.glLineWidth(2.f);

			Debug.debugManager().drawers().drawPoly(verts, vSize, lR, lG, lB, true);
		}
	}

	private static class RenderOfChainFixture {
		public static void draw(LintfordCore core, Body body, Fixture fixture) {
			if (fixture.getShape().getType() != ShapeType.CHAIN)
				return;

			final var lChainShape = (ChainShape) fixture.getShape();

			GL11.glLineWidth(2.f);

			final int lVertCount = lChainShape.m_count;
			final var lToPixels = ConstantsPhysics.UnitsToPixels();
			var lCurVert = lChainShape.m_vertices[0];
			for (int i = 1; i < lVertCount; i++) {
				final var lNextVert = lChainShape.m_vertices[i];

				Debug.debugManager().drawers().drawLine(lCurVert.x * lToPixels, lCurVert.y * lToPixels, lNextVert.x * lToPixels, lNextVert.y * lToPixels);

				lCurVert = lNextVert;
			}
		}
	}

	private static class RenderOfCircleFixture {
		public static void draw(LintfordCore core, Body body, Fixture fixture) {
			var lFixture = body.getFixtureList();

			final float lBodyX = ConstantsPhysics.toPixels(body.getPosition().x);
			final float lBodyY = ConstantsPhysics.toPixels(body.getPosition().y);

			while (lFixture != null) {
				if (fixture.getShape() instanceof CircleShape) {
					final var lCircleShape = (CircleShape) fixture.getShape();
					final float lRadius = ConstantsPhysics.toPixels(lCircleShape.getRadius());
					final float lAngle = body.getAngle();

					final float lWorldX = lBodyX + ConstantsPhysics.toPixels(lCircleShape.m_p.x);
					final float lWorldY = lBodyY + ConstantsPhysics.toPixels(lCircleShape.m_p.y);

					Debug.debugManager().drawers().drawCircle(lWorldX, lWorldY, lRadius, lAngle, NUM_POINTS_IN_CIRCLE, GL11.GL_LINE_STRIP);

					lFixture = lFixture.getNext();
				}
			}
		}
	}

	private static class DebugRenderBody {
		public static void draw(LintfordCore core, Body body) {
			Fixture lFixture = body.getFixtureList();

			final float lBodyX = ConstantsPhysics.toPixels(body.getPosition().x);
			final float lBodyY = ConstantsPhysics.toPixels(body.getPosition().y);

			if (DEBUG_DRAW_BODY_CENTER) {
				final float lCrossSize = 15.f * core.gameCamera().getZoomFactorOverOne();
				Debug.debugManager().drawers().beginLineRenderer(core.gameCamera(), GL11.GL_LINES);
				Debug.debugManager().drawers().drawLine(lBodyX - lCrossSize, lBodyY, lBodyX + lCrossSize, lBodyY);
				Debug.debugManager().drawers().drawLine(lBodyX, lBodyY - lCrossSize, lBodyX, lBodyY + lCrossSize);
				Debug.debugManager().drawers().endLineRenderer();
			}

			Debug.debugManager().drawers().beginLineRenderer(core.gameCamera(), GL11.GL_LINES);
			final float lAngle = body.getAngle();
			final float lLengthOfVector = 30.f * core.gameCamera().getZoomFactorOverOne();
			final float lAngleEndX = (float) Math.cos(lAngle) * lLengthOfVector;
			final float lAngleEndY = (float) Math.sin(lAngle) * lLengthOfVector;

			Debug.debugManager().drawers().drawLine(lBodyX, lBodyY, lBodyX + lAngleEndX, lBodyY + lAngleEndY, 1f, 0f, 1f);
			Debug.debugManager().drawers().endLineRenderer();

			Debug.debugManager().drawers().beginLineRenderer(core.gameCamera(), GL11.GL_LINES);

			// polygon fixtures
			while (lFixture != null) {
				// TODO: update the color depending on the state (active, sleep, static, kinetic)
				if (lFixture.m_shape == null) {
					lFixture = lFixture.getNext();
					continue;
				}

				if (lFixture.getShape().getType() == ShapeType.POLYGON) {
					fixtureCountAtLastDraw++;
					RenderOfPolyFixture.draw(core, body, lFixture);
				} else if (lFixture.getShape().getType() == ShapeType.CHAIN) {
					fixtureCountAtLastDraw++;
					RenderOfChainFixture.draw(core, body, lFixture);
				} else if (lFixture.getShape().getType() == ShapeType.CIRCLE) {
					fixtureCountAtLastDraw++;
					RenderOfCircleFixture.draw(core, body, lFixture);
				}

				lFixture = lFixture.getNext();
			}

			Debug.debugManager().drawers().endLineRenderer();
		}
	}

	private static class DebugRenderJoint {
		public static void draw(LintfordCore core, Joint joint) {
			if (joint == null)
				return;

			if (joint instanceof PrismaticJoint) {
				debugDrawPrismaticJoint(core, (PrismaticJoint) joint);
			}

			if (joint instanceof RevoluteJoint) {
				debugDrawRevoluteJoint(core, (RevoluteJoint) joint);
			}
		}

		private static void debugDrawRevoluteJoint(LintfordCore core, Joint joint) {
			if (!DEBUG_DRAW_JOINT_REVOLUTE)
				return;

			GL11.glPointSize(6f);
			Debug.debugManager().drawers().beginPointRenderer(core.gameCamera());

			RevoluteJoint lRevoluteJoint = (RevoluteJoint) joint;

			final var lBodyA = lRevoluteJoint.getBodyA();
			final var lBodyB = lRevoluteJoint.getBodyB();

			final var lLocalAnchorA = lBodyA.getWorldPoint(lRevoluteJoint.getLocalAnchorA());
			final var lLocalAnchorB = lBodyB.getWorldPoint(lRevoluteJoint.getLocalAnchorB());

			final var lAnchorAX = ConstantsPhysics.toPixels(lLocalAnchorA.x);
			final var lAnchorAY = ConstantsPhysics.toPixels(lLocalAnchorA.y);
			final var lAnchorBX = ConstantsPhysics.toPixels(lLocalAnchorB.x);
			final var lAnchorBY = ConstantsPhysics.toPixels(lLocalAnchorB.y);

			Debug.debugManager().drawers().drawPoint(lAnchorAX, lAnchorAY, 200f / 255f, 217f / 255f, 204f / 255f, 1f);
			Debug.debugManager().drawers().drawPoint(lAnchorBX, lAnchorBY, 255f / 255f, 117f / 255f, 104f / 255f, 1f);

			Debug.debugManager().drawers().beginLineRenderer(core.gameCamera(), GL11.GL_LINES, 1.f);
			Debug.debugManager().drawers().drawLine(lAnchorAX, lAnchorAY, lAnchorBX, lAnchorBY, 1f, 0f, 1f);
			Debug.debugManager().drawers().endLineRenderer();

			Debug.debugManager().drawers().beginLineRenderer(core.gameCamera(), GL11.GL_LINES, 2.f);

			// Render reference angle
			float lRefAngle = lRevoluteJoint.getReferenceAngle();
			float lRefPointEndX = (float) Math.cos(lRefAngle) * 25f;
			float lRefPointEndY = (float) Math.sin(lRefAngle) * 25f;
			Debug.debugManager().drawers().drawLine(lAnchorAX, lAnchorAY, lAnchorAX + lRefPointEndX, lAnchorAY + lRefPointEndY, 1f, 1f, 0f);

			// Render joint angle
			float lAngle = lRevoluteJoint.getJointAngle();
			float lAngleEndX = (float) Math.cos(lAngle) * 35f;
			float lAngleEndY = (float) Math.sin(lAngle) * 35f;
			Debug.debugManager().drawers().drawLine(lAnchorAX, lAnchorAY, lAnchorAX + lAngleEndX, lAnchorAY + lAngleEndY, 0.5f, 0.82f, 0.5f);
			Debug.debugManager().drawers().endLineRenderer();

			Debug.debugManager().drawers().endPointRenderer();
		}

		private static void debugDrawPrismaticJoint(LintfordCore core, PrismaticJoint prismaticJoint) {
			if (!DEBUG_DRAW_JOINT_PRISMATIC)
				return;

			GL11.glPointSize(6f);
			Debug.debugManager().drawers().beginPointRenderer(core.gameCamera());

			final var lBodyA = prismaticJoint.getBodyA();
			final var lBodyB = prismaticJoint.getBodyB();

			final var lLocalAnchorA = lBodyA.getWorldPoint(prismaticJoint.getLocalAnchorA());
			final var lLocalAnchorB = lBodyB.getWorldPoint(prismaticJoint.getLocalAnchorB());

			final var lAnchorAX = ConstantsPhysics.toPixels(lLocalAnchorA.x);
			final var lAnchorAY = ConstantsPhysics.toPixels(lLocalAnchorA.y);
			final var lAnchorBX = ConstantsPhysics.toPixels(lLocalAnchorB.x);
			final var lAnchorBY = ConstantsPhysics.toPixels(lLocalAnchorB.y);

			Debug.debugManager().drawers().drawPoint(lAnchorAX, lAnchorAY, 200f / 255f, 217f / 255f, 204f / 255f, 1f);
			Debug.debugManager().drawers().drawPoint(lAnchorBX, lAnchorBY, 255f / 255f, 117f / 255f, 104f / 255f, 1f);

			Debug.debugManager().drawers().endPointRenderer();
		}
	}

	private static class DebugRenderParticles {
		public static void draw(LintfordCore core, Vec2[] positionBuffer, ParticleColor[] colorBuffer, float radius, final int count) {
			if (!DEBUG_DRAW_PARTICLE_POINTS)
				return;

			GL11.glPointSize(DEBUG_DRAW_PARTICLE_POINT_SIZE);

			final var lDebugDrawer = Debug.debugManager().drawers();
			lDebugDrawer.beginPointRenderer(core.gameCamera());
			for (int i = 0; i < count; i++) {
				lDebugDrawer.drawPoint(ConstantsPhysics.toPixels(positionBuffer[i].x), ConstantsPhysics.toPixels(positionBuffer[i].y), 1.f, 0.f, 0.f, 1.0f);
			}

			lDebugDrawer.endPointRenderer();
		}
	}

	// --------------------------------------
	// Variables
	// --------------------------------------

	private World mWorld;

	// --------------------------------------
	// Constructor
	// --------------------------------------

	public JBox2dDebugDrawer(World box2dWorld) {
		mWorld = box2dWorld;
	}

	// --------------------------------------
	// Core-Methods
	// --------------------------------------

	public void draw(LintfordCore core) {
		fixtureCountAtLastDraw = 0;

		Debug.debugManager().drawers().beginPolyRenderer(core.gameCamera());
		Body lBody = mWorld.getBodyList();
		while (lBody != null) {

			DebugRenderBody.draw(core, lBody);

			lBody = lBody.getNext();
		}

		Debug.debugManager().drawers().endPolyRenderer();

		Joint lJoint = mWorld.getJointList();
		while (lJoint != null) {
			DebugRenderJoint.draw(core, lJoint);

			lJoint = lJoint.getNext();
		}

		final var lParticleCount = mWorld.getParticleCount();
		final var lParticleColorBuffer = mWorld.getParticleColorBuffer();
		final var lParticlePosBuffer = mWorld.getParticlePositionBuffer();

		DebugRenderParticles.draw(core, lParticlePosBuffer, lParticleColorBuffer, ConstantsPhysics.toUnits(16.f), lParticleCount);

		debugDrawWorldInfo(core);
	}

	private void debugDrawWorldInfo(LintfordCore core) {
		if (DEBUG_DRAW_WORLD_INFO) {
			final var lHUDrect = core.HUD().boundingRectangle();

			final int lLineHeight = -20;
			int lLinePos = 100;

			final var lDebugDrawer = Debug.debugManager().drawers();

			lDebugDrawer.beginTextRenderer(core.HUD());
			lDebugDrawer.drawText("# Contacts: " + mWorld.getContactCount(), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
			lDebugDrawer.drawText(String.format("# Fixtures: %d", fixtureCountAtLastDraw), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
			lDebugDrawer.drawText(String.format("# Bodies: %d", mWorld.getBodyCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
			lDebugDrawer.drawText(String.format("# Joints: %d", mWorld.getJointCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
			lDebugDrawer.drawText(String.format("# Particles: %d", mWorld.getParticleCount()), lHUDrect.left() + 5, lHUDrect.bottom() - (lLinePos -= lLineHeight));
			lDebugDrawer.endTextRenderer();
		}
	}
}