package net.lintford.library.core.lindenmeyer;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jbox2d.common.Vec2;

import net.lintford.library.controllers.box2d.Box2dWorldController;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entity.Box2dBodyInstance;
import net.lintford.library.core.box2d.entity.Box2dFixtureInstance;
import net.lintford.library.core.box2d.entity.Box2dPolygonInstance;
import net.lintford.library.core.box2d.entity.Box2dRevoluteInstance;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;

/** Builds SpriteGraphDefintions based on L-Systems. */
public class Box2dSpriteGraphBuilder {

	// --------------------------------------
	// Inner Classes
	// --------------------------------------

	public class JBox2dLCursor extends LCursor {

		// --------------------------------------
		// Variables
		// --------------------------------------

		public JBox2dLCursor() {
			super(0);
		}

		public Box2dBodyInstance parentBodyInstance;

		// --------------------------------------
		// Constructors
		// --------------------------------------

		public JBox2dLCursor(JBox2dLCursor pJBox2dLCursor, int pDepth) {
			super(pJBox2dLCursor, pDepth);

			parentBodyInstance = pJBox2dLCursor.parentBodyInstance;

		}

		// --------------------------------------
		// Methods
		// --------------------------------------

		@Override
		public void saveState() {
			cursorStack.push(new JBox2dLCursor(this, depth));
		}

		@Override
		public void restoreState() {
			JBox2dLCursor lSavedState = (JBox2dLCursor) cursorStack.poll();

			if (lSavedState != null) {
				x = lSavedState.x;
				y = lSavedState.y;
				curRotation = lSavedState.curRotation;
				depth = lSavedState.depth;
				parentBodyInstance = lSavedState.parentBodyInstance;

			}

		}

	}

	public class LCursor {

		// --------------------------------------
		// Variables
		// --------------------------------------

		public float x, y;
		public float curRotation;
		public int depth;
		public Deque<LCursor> cursorStack = new ArrayDeque<>();

		// --------------------------------------
		// Constructors
		// --------------------------------------

		public LCursor() {
			x = 0;
			y = 0;
			curRotation = 0;
			depth = 0;

		}

		public LCursor(int pDepth) {
			this();

			depth = pDepth;

		}

		public LCursor(LCursor pCurCursor, int pDepth) {
			this(pDepth);
			x = pCurCursor.x;
			y = pCurCursor.y;
			curRotation = pCurCursor.curRotation;

		}

		// --------------------------------------
		// Method
		// --------------------------------------

		public void saveState() {
			cursorStack.push(new LCursor(this, depth));
		}

		public void restoreState() {
			LCursor lSavedState = cursorStack.poll();

			if (lSavedState != null) {
				x = lSavedState.x;
				y = lSavedState.y;
				curRotation = lSavedState.curRotation;
				depth = lSavedState.depth;

			}

		}

	}

	// --------------------------------------
	// Methods
	// --------------------------------------

	// TODO: This method needs to return a JBox2dEntity which can be added to the PObjectInstance and attached to
	// a JBox2dEntity (of which ObjectInstanceBase is derived).

	public JBox2dEntityInstance createSpriteGraphFromLSystem(String pLSystemString, LSystemDefinition pLSystemDef) {
		// Create a new instance of a SpriteGraph
		// pLSystemDef.onGraphCreation(lNewInst);

		// pLSystemString = "F[-FF]+F";

		JBox2dEntityInstance lJBox2dEntityInstance = new JBox2dEntityInstance();

		int uidCounter = 1;

		// Now we need to 'draw' the L-System (i.e. create the sprites and the graph)
		JBox2dLCursor lCursor = new JBox2dLCursor();
		lCursor.curRotation = 0;
		lCursor.x = 0;
		lCursor.y = 0;

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':

				// BODY

				Box2dBodyInstance lBodyInst = new Box2dBodyInstance();
				lBodyInst.uid = uidCounter++;
				lBodyInst.bodyTypeIndex = i == 0 ? PObjectDefinition.BODY_TYPE_STATIC : PObjectDefinition.BODY_TYPE_DYNAMIC;

				// FIXTURE

				Box2dFixtureInstance lFixInst = new Box2dFixtureInstance(lBodyInst);

				lFixInst.density = 1;
				lFixInst.categoryBits = 0b00110000;
				lFixInst.maskBits = 0x00;

				Box2dPolygonInstance lBox2dPolygonInstance = new Box2dPolygonInstance();
				lBox2dPolygonInstance.vertexCount = 4;

				float lSegmentWidth = 16f;
				float lSegmentHeight = 64f;

				float lCenterX = 0;
				float lCenterY = 0;

				float lLeft = (lCenterX - lSegmentWidth / 2f) * Box2dWorldController.PIXELS_TO_UNITS;
				float lRight = (lCenterX + lSegmentWidth / 2f) * Box2dWorldController.PIXELS_TO_UNITS;
				float lTop = (lCenterY - lSegmentHeight) * Box2dWorldController.PIXELS_TO_UNITS;
				float lBottom = 0;// (lCenterY + lSegmentHeight / 2f) * Box2dWorldController.PIXELS_TO_UNITS;

				lBox2dPolygonInstance.vertices = new Vec2[] { new Vec2(lRight, lTop), new Vec2(lRight, lBottom), new Vec2(lLeft, lBottom), new Vec2(lLeft, lTop) };

				lFixInst.shape = lBox2dPolygonInstance;

				lBodyInst.mFixtures = new Box2dFixtureInstance[1];
				lBodyInst.mFixtures[0] = lFixInst;
				lBodyInst.angle = 0;// i == 0 ? 0 : lCursor.curRotation;
				lBodyInst.gravityScale = 1;

				lBodyInst.position.x = lCursor.x;
				lBodyInst.position.y = lCursor.y;

				// JOINT TO CONNECT TO TREE
				if (i > 0) {
					Box2dRevoluteInstance lJoint = new Box2dRevoluteInstance();
					lJoint.bodyAUID = lCursor.parentBodyInstance.uid;
					lJoint.bodyBUID = lBodyInst.uid;

					lJoint.localAnchorA.set(0, -lSegmentHeight * Box2dWorldController.PIXELS_TO_UNITS);
					lJoint.localAnchorB.set(0, 0); // joints added to base of new component piece

					lJoint.referenceAngle = lCursor.curRotation;// (float) Math.toRadians(-45);
					lJoint.enableLimit = true;
					lJoint.lowerAngle = 0;// lCursor.curRotation;
					lJoint.upperAngle = 0;// lCursor.curRotation;

					lJoint.enableMotor = false;
					lJoint.maxMotorTorque = 100;
					lJoint.motorSpeed = 0.25f;

					lJoint.collidesConnected = false;
					lJBox2dEntityInstance.joints().add(lJoint);
				}

				lJBox2dEntityInstance.bodies().add(lBodyInst);

				lCursor.x += (float) Math.cos(lCursor.curRotation + Math.toRadians(-90)) * lSegmentHeight * Box2dWorldController.PIXELS_TO_UNITS;
				lCursor.y += (float) Math.sin(lCursor.curRotation + Math.toRadians(-90)) * lSegmentHeight * Box2dWorldController.PIXELS_TO_UNITS;
				lCursor.parentBodyInstance = lBodyInst;
				lCursor.depth++;
				break;

			case '-':
				lCursor.curRotation -= (float) Math.toRadians(25);// RandomNumbers.random(pLSystemDef.minAngle, pLSystemDef.maxAngle);
				break;

			case '+':
				lCursor.curRotation += (float) Math.toRadians(25);// RandomNumbers.random(pLSystemDef.minAngle, pLSystemDef.maxAngle);
				break;

			case '[':
				lCursor.saveState();
				break;

			case ']':
				lCursor.restoreState();
				break;

			}

		}

		return lJBox2dEntityInstance;

	}

}
