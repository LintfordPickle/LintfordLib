package net.lintford.library.core.lindenmeyer;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jbox2d.common.Vec2;

import net.lintford.library.ConstantsPhysics;
import net.lintford.library.core.box2d.definition.PObjectDefinition;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.box2d.instance.Box2dFixtureInstance;
import net.lintford.library.core.box2d.instance.Box2dInstanceManager;
import net.lintford.library.core.box2d.instance.Box2dPolygonInstance;
import net.lintford.library.core.box2d.instance.Box2dRevoluteInstance;
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
				parentRotation = lSavedState.parentRotation;
				parentSegmentLength = lSavedState.parentSegmentLength;
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
		public float parentRotation;
		public float parentSegmentLength;
		public int depth;
		public Deque<LCursor> cursorStack = new ArrayDeque<>();

		// --------------------------------------
		// Constructors
		// --------------------------------------

		public LCursor() {
			x = 0;
			y = 0;
			curRotation = 0;
			parentRotation = 0;
			parentSegmentLength = 0;
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
			parentRotation = pCurCursor.parentRotation;
			parentSegmentLength = pCurCursor.parentSegmentLength;

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
				parentRotation = lSavedState.parentRotation;
				parentSegmentLength = lSavedState.parentSegmentLength;

			}

		}

	}

	int uidCounter = 0;

	enum SegmentLength {
		small, // 16 px
		medium, // 32 px
		large, // 64 px
	}

	private static final boolean LEAF_ENABLED = true;

	// --------------------------------------
	// Methods
	// --------------------------------------

	public JBox2dEntityInstance createSpriteGraphFromLSystem(Box2dInstanceManager pInstanceManager, String pLSystemString, LSystemDefinition pLSystemDef) {
		JBox2dEntityInstance lJBox2dEntityInstance = new JBox2dEntityInstance(0);
		lJBox2dEntityInstance.spriteSheetName = pLSystemDef.spriteSheetName;

		// Now we need to 'draw' the L-System (i.e. create the sprites and the graph)
		JBox2dLCursor lCursor = new JBox2dLCursor();
		lCursor.parentRotation = 0.f;
		lCursor.curRotation = 0;
		lCursor.x = 0;
		lCursor.y = 0;
		lCursor.segmentBaseWidth = 16.f;

		createRoot(pInstanceManager, lJBox2dEntityInstance, lCursor, 0, 0);

		for (int i = 0; i < pLSystemString.length(); i++) {
			char lCurrentInstruction = pLSystemString.charAt(i);

			// Update the cursor position ready for the next node in the tree
			switch (lCurrentInstruction) {
			case 'F':

				// BODY

				Box2dBodyInstance lBox2dBodyInstance = null;
				if (pInstanceManager != null) {
					lBox2dBodyInstance = pInstanceManager.box2dBodyInstanceRepository().getFreePooledItem();
				} else {
					lBox2dBodyInstance = new Box2dBodyInstance(0);
				}

				lBox2dBodyInstance.uid = uidCounter++;
				lBox2dBodyInstance.bodyTypeIndex = PObjectDefinition.BODY_TYPE_DYNAMIC;//i == 0 ? PObjectDefinition.BODY_TYPE_STATIC : PObjectDefinition.BODY_TYPE_DYNAMIC;

				// FIXTURE

				Box2dFixtureInstance lBox2dFixtureInstance = null;
				if (pInstanceManager != null) {
					lBox2dFixtureInstance = pInstanceManager.box2dFixtureInstanceRepository().getFreePooledItem();
				} else {
					lBox2dFixtureInstance = new Box2dFixtureInstance(0);
				}

				float lSegmentHeight = 64.f;
				lBox2dFixtureInstance.density = 1f - (float) (1f / (float) i + 1);
				lBox2dFixtureInstance.categoryBits = 0b0000000000000010;
				lBox2dFixtureInstance.maskBits = 0b1111111111111101;

				lBox2dFixtureInstance.spriteName = "TreeTrunk_64";

				Box2dPolygonInstance lBox2dPolygonInstance = new Box2dPolygonInstance();
				lBox2dPolygonInstance.vertexCount = 4;

				final var lToUnits = ConstantsPhysics.PixelsToUnits();
				float lSegmentWidthB = (lCursor.segmentBaseWidth);
				float lSegmentWidthT = (lCursor.segmentBaseWidth * 0.8f);

				float lRotation = lCursor.curRotation;
				float sin = (float) (Math.sin(lRotation));
				float cos = (float) (Math.cos(lRotation));

				float lHalfBW = lSegmentWidthB / 2f;
				float lHalfTW = lSegmentWidthT / 2f;
				float lHalfH = lSegmentHeight / 2f;

				float originX = -0;
				float originY = -lSegmentHeight / 2.f;

				// Vertex 0 (bottom left)
				float x0 = (originX - lHalfBW) * cos - (originY + lHalfH) * sin;
				float y0 = (originX - lHalfBW) * sin + (originY + lHalfH) * cos;

				// Vertex 1 (top left)
				float x1 = (originX - lHalfTW) * cos - (originY - lHalfH) * sin;
				float y1 = (originX - lHalfTW) * sin + (originY - lHalfH) * cos;

				// Vertex 2 (top right)
				float x2 = (originX + lHalfTW) * cos - (originY - lHalfH) * sin;
				float y2 = (originX + lHalfTW) * sin + (originY - lHalfH) * cos;

				// Vertex 3 (bottom right)
				float x3 = (originX + lHalfBW) * cos - (originY + lHalfH) * sin;
				float y3 = (originX + lHalfBW) * sin + (originY + lHalfH) * cos;

				lBox2dPolygonInstance.vertices = new Vec2[] { new Vec2((x1) * lToUnits, (y1) * lToUnits), new Vec2((x2) * lToUnits, (y2) * lToUnits), new Vec2((x0) * lToUnits, (y0) * lToUnits),
						new Vec2((x3) * lToUnits, (y3) * lToUnits) };

				lBox2dFixtureInstance.shape = lBox2dPolygonInstance;

				// lBox2dBodyInstance.bodyTypeIndex = 0;
				lBox2dBodyInstance.mFixtures = new Box2dFixtureInstance[1];
				lBox2dBodyInstance.mFixtures[0] = lBox2dFixtureInstance;
				lBox2dBodyInstance.gravityScale = 1;

				lBox2dBodyInstance.localPosition.x = lCursor.x * lToUnits;
				lBox2dBodyInstance.localPosition.y = lCursor.y * lToUnits;

				// Joint to connext to previous block
				Box2dRevoluteInstance lBox2dRevoluteInstance = null;
				if (pInstanceManager != null) {
					lBox2dRevoluteInstance = pInstanceManager.box2dJointInstanceRepository().getFreePooledItem();
				} else {
					lBox2dRevoluteInstance = new Box2dRevoluteInstance(0);
				}

				lBox2dRevoluteInstance.bodyAUID = lCursor.parentBodyInstance.uid;
				lBox2dRevoluteInstance.bodyBUID = lBox2dBodyInstance.uid;

				float lParentSegmentLength = -lCursor.parentSegmentLength * lToUnits;
				float lPX = (float) Math.cos(lCursor.parentRotation + Math.toRadians(90)) * lParentSegmentLength;
				float lPY = (float) Math.sin(lCursor.parentRotation + Math.toRadians(90)) * lParentSegmentLength;

				lBox2dRevoluteInstance.localAnchorA.set(lPX, lPY);
				lBox2dRevoluteInstance.localAnchorB.set(0, 0); // joints added to base of new component piece

				lBox2dRevoluteInstance.referenceAngle = -lRotation;
				lBox2dRevoluteInstance.lowerAngle = lRotation;
				lBox2dRevoluteInstance.upperAngle = lRotation;
				lBox2dRevoluteInstance.enableLimit = true;

				lBox2dRevoluteInstance.enableMotor = false;
				lBox2dRevoluteInstance.maxMotorTorque = 0;
				lBox2dRevoluteInstance.motorSpeed = 0.f;

				lBox2dRevoluteInstance.collidesConnected = false;
				lJBox2dEntityInstance.joints().add(lBox2dRevoluteInstance);

				lJBox2dEntityInstance.bodies().add(lBox2dBodyInstance);

				// Check for creation of leaf
				float lChanceOfLeafs = lCursor.depth * pLSystemDef.leafChanceDepthMultiplier;
				if (lCursor.depth < 3) {
					lChanceOfLeafs = 0.f;
				}

				if (RandomNumbers.getRandomChance(lChanceOfLeafs)) {
					int lSides = RandomNumbers.random(0, 3);

					if (lSides == 0 || lSides == 2)
						createleaf(pInstanceManager, lJBox2dEntityInstance, lBox2dBodyInstance, lBox2dBodyInstance.localPosition.x, lBox2dBodyInstance.localPosition.y, lChanceOfLeafs, (float) Math.toRadians(-90)); // left of nook

					if (lSides == 1 || lSides == 2)
						createleaf(pInstanceManager, lJBox2dEntityInstance, lBox2dBodyInstance, lBox2dBodyInstance.localPosition.x, lBox2dBodyInstance.localPosition.y, lChanceOfLeafs, (float) Math.toRadians(90)); // right of nook

				}

				lCursor.x += (float) Math.cos(lRotation + Math.toRadians(-90)) * lSegmentHeight;
				lCursor.y += (float) Math.sin(lRotation + Math.toRadians(-90)) * lSegmentHeight;
				lCursor.parentBodyInstance = lBox2dBodyInstance;
				lCursor.parentRotation = lCursor.curRotation;
				lCursor.parentSegmentLength = lSegmentHeight;
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

	private void createRoot(Box2dInstanceManager pInstanceManager, JBox2dEntityInstance pInst, JBox2dLCursor pCursor, float pX, float pY) {
		Box2dBodyInstance lBox2dBodyInstance = null;
		if (pInstanceManager != null) {
			lBox2dBodyInstance = pInstanceManager.box2dBodyInstanceRepository().getFreePooledItem();
		} else {
			lBox2dBodyInstance = new Box2dBodyInstance(0);
		}

		lBox2dBodyInstance.uid = uidCounter++;
		lBox2dBodyInstance.bodyTypeIndex = PObjectDefinition.BODY_TYPE_DYNAMIC;//i == 0 ? PObjectDefinition.BODY_TYPE_STATIC : PObjectDefinition.BODY_TYPE_DYNAMIC;

		// FIXTURE

		Box2dFixtureInstance lBox2dFixtureInstance = null;
		if (pInstanceManager != null) {
			lBox2dFixtureInstance = pInstanceManager.box2dFixtureInstanceRepository().getFreePooledItem();
		} else {
			lBox2dFixtureInstance = new Box2dFixtureInstance(0);
		}

		float lSegmentHeight = 16.f;
		lBox2dFixtureInstance.density = 2.f;
		lBox2dFixtureInstance.categoryBits = 0b0000000000000010;
		lBox2dFixtureInstance.maskBits = 0b1111111111111101;

		lBox2dFixtureInstance.spriteName = "TreeRoot";

		Box2dPolygonInstance lBox2dPolygonInstance = new Box2dPolygonInstance();
		lBox2dPolygonInstance.vertexCount = 4;

		final var lToUnits = ConstantsPhysics.PixelsToUnits();
		float lSegmentWidthB = (pCursor.segmentBaseWidth);
		float lSegmentWidthT = (pCursor.segmentBaseWidth);

		float lRotation = (float) Math.toRadians(0);
		float sin = (float) (Math.sin(lRotation));
		float cos = (float) (Math.cos(lRotation));

		float lHalfBW = lSegmentWidthB / 2f;
		float lHalfTW = lSegmentWidthT / 2f;
		float lHalfH = lSegmentHeight / 2f;

		float originX = -0;
		float originY = -lSegmentHeight / 2.f;

		// Vertex 0 (bottom left)
		float x0 = (originX - lHalfBW) * cos - (originY + lHalfH) * sin;
		float y0 = (originX - lHalfBW) * sin + (originY + lHalfH) * cos;

		// Vertex 1 (top left)
		float x1 = (originX - lHalfTW) * cos - (originY - lHalfH) * sin;
		float y1 = (originX - lHalfTW) * sin + (originY - lHalfH) * cos;

		// Vertex 2 (top right)
		float x2 = (originX + lHalfTW) * cos - (originY - lHalfH) * sin;
		float y2 = (originX + lHalfTW) * sin + (originY - lHalfH) * cos;

		// Vertex 3 (bottom right)
		float x3 = (originX + lHalfBW) * cos - (originY + lHalfH) * sin;
		float y3 = (originX + lHalfBW) * sin + (originY + lHalfH) * cos;

		lBox2dPolygonInstance.vertices = new Vec2[] { new Vec2((x1) * lToUnits, (y1) * lToUnits), new Vec2((x2) * lToUnits, (y2) * lToUnits), new Vec2((x0) * lToUnits, (y0) * lToUnits),
				new Vec2((x3) * lToUnits, (y3) * lToUnits) };

		lBox2dFixtureInstance.shape = lBox2dPolygonInstance;

		lBox2dBodyInstance.mFixtures = new Box2dFixtureInstance[1];
		lBox2dBodyInstance.mFixtures[0] = lBox2dFixtureInstance;
		lBox2dBodyInstance.gravityScale = 1;

		lBox2dBodyInstance.localPosition.x = pCursor.x * lToUnits;
		lBox2dBodyInstance.localPosition.y = pCursor.y * lToUnits;

		pInst.bodies().add(lBox2dBodyInstance);

		pCursor.x += (float) Math.cos(lRotation + Math.toRadians(-90)) * lSegmentHeight;
		pCursor.y += (float) Math.sin(lRotation + Math.toRadians(-90)) * lSegmentHeight;
		pCursor.parentBodyInstance = lBox2dBodyInstance;
		pCursor.parentRotation = pCursor.curRotation;
		pCursor.parentSegmentLength = lSegmentHeight;
		pCursor.segmentBaseWidth = lSegmentWidthT;
		pCursor.depth++;

	}

	private void createleaf(Box2dInstanceManager pInstanceManager, JBox2dEntityInstance pInst, Box2dBodyInstance pBody, float pX, float pY, float pChance, float pAngleInRadians) {
		if (!LEAF_ENABLED)
			return;

		// BODY
		Box2dBodyInstance lBox2dBodyInstance = null;
		if (pInstanceManager != null) {
			lBox2dBodyInstance = pInstanceManager.box2dBodyInstanceRepository().getFreePooledItem();
		} else {
			lBox2dBodyInstance = new Box2dBodyInstance(0);
		}

		lBox2dBodyInstance.uid = uidCounter++;
		lBox2dBodyInstance.name = "Leaf";
		lBox2dBodyInstance.bodyTypeIndex = PObjectDefinition.BODY_TYPE_DYNAMIC;

		// FIXTURE
		Box2dFixtureInstance lBox2dFixtureInstance = null;
		if (pInstanceManager != null) {
			lBox2dFixtureInstance = pInstanceManager.box2dFixtureInstanceRepository().getFreePooledItem();
		} else {
			lBox2dFixtureInstance = new Box2dFixtureInstance(0);
		}

		lBox2dFixtureInstance.density = 0.002f;
		lBox2dFixtureInstance.categoryBits = 0b00110000;
		lBox2dFixtureInstance.maskBits = 0x00;
		lBox2dFixtureInstance.spriteName = "TreeLeaf";

		final var lBox2dPolygonInstance = new Box2dPolygonInstance();
		lBox2dPolygonInstance.vertexCount = 4;

		float lWidth = 64f;
		float lHeight = 64f;

		final var lToUnits = ConstantsPhysics.PixelsToUnits();

		lBox2dPolygonInstance.vertices = new Vec2[] { new Vec2(+lWidth / 2f * lToUnits, lHeight * lToUnits), new Vec2(+lWidth / 2f * lToUnits, 0 * lToUnits), new Vec2(-lWidth / 2f * lToUnits, 0 * lToUnits),
				new Vec2(-lWidth / 2f * lToUnits, lHeight * lToUnits) };

		lBox2dFixtureInstance.shape = lBox2dPolygonInstance;

		lBox2dBodyInstance.mFixtures = new Box2dFixtureInstance[1];
		lBox2dBodyInstance.mFixtures[0] = lBox2dFixtureInstance;
		lBox2dBodyInstance.localAngle = 0;
		lBox2dBodyInstance.gravityScale = 1;

		lBox2dBodyInstance.localPosition.x = pX;
		lBox2dBodyInstance.localPosition.y = pY;

		// JOINT TO CONNECT TO TREE
		Box2dRevoluteInstance lBox2dRevoluteInstance = null;
		if (pInstanceManager != null) {
			lBox2dRevoluteInstance = pInstanceManager.box2dJointInstanceRepository().getFreePooledItem();
		} else {
			lBox2dRevoluteInstance = new Box2dRevoluteInstance(0);
		}

		lBox2dRevoluteInstance.bodyAUID = pBody.uid;
		lBox2dRevoluteInstance.bodyBUID = lBox2dBodyInstance.uid;

		lBox2dRevoluteInstance.localAnchorA.set(0, 0);
		lBox2dRevoluteInstance.localAnchorB.set(0, 0); // joints added to base of new component piece

		lBox2dRevoluteInstance.referenceAngle = pAngleInRadians;
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
