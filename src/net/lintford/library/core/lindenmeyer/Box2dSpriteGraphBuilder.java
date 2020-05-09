package net.lintford.library.core.lindenmeyer;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jbox2d.common.Vec2;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entity.Box2dBodyInstance;
import net.lintford.library.core.box2d.entity.Box2dFixtureInstance;
import net.lintford.library.core.box2d.entity.Box2dPolygonInstance;
import net.lintford.library.core.box2d.entity.Box2dRevoluteInstance;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.maths.RandomNumbers;

/** Builds SpriteGraphDefintions based on L-Systems. */
public class Box2dSpriteGraphBuilder {

	// --------------------------------------
	// Inner Classes
	// --------------------------------------

	public class JBox2dLCursor extends LCursor {

		// --------------------------------------
		// Variables
		// --------------------------------------

		public Box2dBodyInstance parentBodyInstance;
		public float segmentBaseWidth;

		// --------------------------------------
		// Constructors
		// --------------------------------------

		public JBox2dLCursor() {
			super(0);
		}

		public JBox2dLCursor(JBox2dLCursor pJBox2dLCursor, int pDepth) {
			super(pJBox2dLCursor, pDepth);

			parentBodyInstance = pJBox2dLCursor.parentBodyInstance;
			segmentBaseWidth = pJBox2dLCursor.segmentBaseWidth;

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
				segmentBaseWidth = lSavedState.segmentBaseWidth;

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

	int uidCounter = 0;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance createSpriteGraphFromLSystem(String pLSystemString, LSystemDefinition pLSystemDef) {
		// Create a new instance of a SpriteGraph
		// pLSystemDef.onGraphCreation(lNewInst);

		pLSystemString = "F-F[+F+F+F+F+F+F]-F-F-F-F-F-F";

		JBox2dEntityInstance lJBox2dEntityInstance = new JBox2dEntityInstance(0);
		lJBox2dEntityInstance.spriteSheetName = "TreeSpriteSheet";

		// Now we need to 'draw' the L-System (i.e. create the sprites and the graph)
		JBox2dLCursor lCursor = new JBox2dLCursor();
		lCursor.curRotation = 0;
		lCursor.x = 0;
		lCursor.y = 0;
		lCursor.segmentBaseWidth = 16f;

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':

				// BODY

				Box2dBodyInstance lBodyInst = new Box2dBodyInstance();
				// lBodyInst.name = "Body";
				lBodyInst.uid = uidCounter++;
				lBodyInst.bodyTypeIndex = i == 0 ? PObjectDefinition.BODY_TYPE_STATIC : PObjectDefinition.BODY_TYPE_DYNAMIC;

				// FIXTURE

				Box2dFixtureInstance lFixInst = new Box2dFixtureInstance(lBodyInst);

				lFixInst.density = 1f - (float) (1f / i);
				lFixInst.categoryBits = 0b00110000;
				lFixInst.maskBits = 0x00;
				lFixInst.spriteName = "TreeTrunk";

				Box2dPolygonInstance lBox2dPolygonInstance = new Box2dPolygonInstance();
				lBox2dPolygonInstance.vertexCount = 4;

				float lSegmentWidthB = lCursor.segmentBaseWidth;
				float lSegmentWidthT = lCursor.segmentBaseWidth * 0.8f;
				float lSegmentHeight = 64f;

				float lTop = -lSegmentHeight;
				float lBottom = 0;

				final var lToUnits = ConstantsPhysics.PixelsToUnits();
				
				lBox2dPolygonInstance.vertices = new Vec2[] { 
						new Vec2(+lSegmentWidthT / 2f * lToUnits, lTop * lToUnits),
						new Vec2(+lSegmentWidthB / 2f * lToUnits, lBottom * lToUnits),
						new Vec2(-lSegmentWidthB / 2f * lToUnits, lBottom * lToUnits),
						new Vec2(-lSegmentWidthT / 2f * lToUnits, lTop * lToUnits) 
					};

				lFixInst.shape = lBox2dPolygonInstance;

				lBodyInst.mFixtures = new Box2dFixtureInstance[1];
				lBodyInst.mFixtures[0] = lFixInst;
				lBodyInst.localAngle = 0;// i == 0 ? 0 : lCursor.curRotation;
				lBodyInst.gravityScale = 1;

				lBodyInst.localPosition.x = lCursor.x;
				lBodyInst.localPosition.y = lCursor.y;

				// JOINT TO CONNECT TO TREE
				if (i > 0) {
					Box2dRevoluteInstance lJoint = new Box2dRevoluteInstance();
					lJoint.bodyAUID = lCursor.parentBodyInstance.uid;
					lJoint.bodyBUID = lBodyInst.uid;

					lJoint.localAnchorA.set(0, -lSegmentHeight * lToUnits);
					lJoint.localAnchorB.set(0, 0); // joints added to base of new component piece

					lJoint.referenceAngle = lCursor.curRotation;// (float) Math.toRadians(-45);
					lJoint.enableLimit = true;
					lJoint.lowerAngle = 0;
					lJoint.upperAngle = 0;

					lJoint.enableMotor = false;
					lJoint.maxMotorTorque = 100;
					lJoint.motorSpeed = 0.25f;

					lJoint.collidesConnected = false;
					lJBox2dEntityInstance.joints().add(lJoint);
				}

				lJBox2dEntityInstance.bodies().add(lBodyInst);

				// Check for creation of leaf
				float lChanceOfLeafs = lCursor.depth * 10f;
				if (RandomNumbers.getRandomChance(lChanceOfLeafs)) {
					int lSides = RandomNumbers.random(0, 3);

					if (lSides == 0 || lSides == 2)
						createleaf(lJBox2dEntityInstance, lBodyInst, lBodyInst.localPosition.x, lBodyInst.localPosition.y, lChanceOfLeafs, true); // left of nook

					if (lSides == 1 || lSides == 2)
						createleaf(lJBox2dEntityInstance, lBodyInst, lBodyInst.localPosition.x, lBodyInst.localPosition.y, lChanceOfLeafs, false); // right of nook

				}

				lCursor.x += (float) Math.cos(lCursor.curRotation + Math.toRadians(-90)) * lSegmentHeight * lToUnits;
				lCursor.y += (float) Math.sin(lCursor.curRotation + Math.toRadians(-90)) * lSegmentHeight * lToUnits;
				lCursor.parentBodyInstance = lBodyInst;
				lCursor.segmentBaseWidth = lSegmentWidthT;
				lCursor.depth++;
				break;

			case '-':
				lCursor.curRotation = -RandomNumbers.random(pLSystemDef.minAngle, pLSystemDef.maxAngle);
				break;

			case '+':
				lCursor.curRotation = RandomNumbers.random(pLSystemDef.minAngle, pLSystemDef.maxAngle);
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

	private void createleaf(JBox2dEntityInstance pInst, Box2dBodyInstance pBody, float pX, float pY, float pChance, boolean pLeft) {
		// BODY
		final var lBox2dBodyInstance = new Box2dBodyInstance();
		lBox2dBodyInstance.uid = uidCounter++;
		lBox2dBodyInstance.name = "Leaf";
		lBox2dBodyInstance.bodyTypeIndex = PObjectDefinition.BODY_TYPE_DYNAMIC;

		// FIXTURE
		final var lBox2dFixtureInstance = new Box2dFixtureInstance(lBox2dBodyInstance);

		lBox2dFixtureInstance.density = 0.02f;
		lBox2dFixtureInstance.categoryBits = 0b00110000;
		lBox2dFixtureInstance.maskBits = 0x00;
		lBox2dFixtureInstance.spriteName = "TreeLeaf";

		final var lBox2dPolygonInstance = new Box2dPolygonInstance();
		lBox2dPolygonInstance.vertexCount = 4;

		float lWidth = 64f;
		float lHeight = 64f;
		
		final var lToUnits = ConstantsPhysics.PixelsToUnits();

		lBox2dPolygonInstance.vertices = new Vec2[] { 
				new Vec2(+lWidth / 2f * lToUnits, lHeight * lToUnits),
				new Vec2(+lWidth / 2f * lToUnits, 0 * lToUnits),
				new Vec2(-lWidth / 2f * lToUnits, 0 * lToUnits),
				new Vec2(-lWidth / 2f * lToUnits, lHeight * lToUnits) 
			};

		lBox2dFixtureInstance.shape = lBox2dPolygonInstance;

		lBox2dBodyInstance.mFixtures = new Box2dFixtureInstance[1];
		lBox2dBodyInstance.mFixtures[0] = lBox2dFixtureInstance;
		lBox2dBodyInstance.localAngle = 0;
		lBox2dBodyInstance.gravityScale = 1;

		lBox2dBodyInstance.localPosition.x = pX;
		lBox2dBodyInstance.localPosition.y = pY;

		// JOINT TO CONNECT TO TREE
		final var lBox2dRevoluteInstance = new Box2dRevoluteInstance();
		lBox2dRevoluteInstance.bodyAUID = pBody.uid;
		lBox2dRevoluteInstance.bodyBUID = lBox2dBodyInstance.uid;

		lBox2dRevoluteInstance.localAnchorA.set(0, 0);
		lBox2dRevoluteInstance.localAnchorB.set(0, 0); // joints added to base of new component piece

		final float lSign = pLeft ? -1f : 1f;
		lBox2dRevoluteInstance.referenceAngle = (float) Math.toRadians(90) * lSign;
		lBox2dRevoluteInstance.enableLimit = true;
		lBox2dRevoluteInstance.lowerAngle = 0;
		lBox2dRevoluteInstance.upperAngle = 0;

		lBox2dRevoluteInstance.enableMotor = false;
		lBox2dRevoluteInstance.maxMotorTorque = 100;
		lBox2dRevoluteInstance.motorSpeed = 0.25f;

		lBox2dRevoluteInstance.collidesConnected = false;

		pInst.joints().add(lBox2dRevoluteInstance);
		pInst.bodies().add(lBox2dBodyInstance);

	}

}
