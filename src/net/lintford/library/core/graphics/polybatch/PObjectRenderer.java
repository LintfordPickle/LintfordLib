package net.lintford.library.core.graphics.polybatch;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.entities.JBox2dEntityInstance;
import net.lintford.library.core.box2d.instance.Box2dBodyInstance;
import net.lintford.library.core.box2d.instance.Box2dFixtureInstance;
import net.lintford.library.core.entity.JBox2dEntity;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDefinition;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.maths.Vector2f;

public class PObjectRenderer {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mEntityGroupID;
	private ResourceManager mResourceManager;
	private JBox2dPolyBatch mTextureBatch;

	static final int MAX_VERTS = 10;
	static Vector2f[] verts;
	static Vec2 vertex = new Vec2();
	static Vec2 tempVec = new Vec2();

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PObjectRenderer(int pEntityGroupID) {
		mEntityGroupID = pEntityGroupID;
		mTextureBatch = new JBox2dPolyBatch();

	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResources(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;
		mTextureBatch.loadResources(pResourceManager);
	}

	public void unloadResources() {
		mTextureBatch.unloadResources();
	}

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {

	}

	public void draw(LintfordCore pCore, JBox2dEntity pPObject) {
		if (!pPObject.hasPhysicsEntity())
			return;

		String lSpriteSheetDefName = pPObject.box2dEntityInstance().spriteSheetName;
		if (lSpriteSheetDefName == null)
			return;

		SpriteSheetDefinition lSpriteSheetDef = mResourceManager.spriteSheetManager().getSpriteSheet(lSpriteSheetDefName, mEntityGroupID);

		if (lSpriteSheetDef == null)
			return;

		if (verts == null) {
			verts = new Vector2f[MAX_VERTS];
			for (int i = 0; i < MAX_VERTS; i++) {
				verts[i] = new Vector2f();
			}
		}

		mTextureBatch.begin(pCore.gameCamera());

		JBox2dEntityInstance lInst = pPObject.box2dEntityInstance();
		final int lBodyCount = lInst.bodies().size();
		for (int i = 0; i < lBodyCount; i++) {
			Box2dBodyInstance lBodyInst = lInst.bodies().get(i);
			Body lBody = lBodyInst.mBody;
			if (lBody == null)
				continue;

			Texture lTexture = lSpriteSheetDef.texture();

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				Box2dFixtureInstance lFixtureInst = lBodyInst.mFixtures[j];

				if (lFixtureInst.spriteIndex == -1) {
					String lSpriteFrameName = lFixtureInst.spriteName;
					lFixtureInst.spriteIndex = lSpriteSheetDef.getSpriteFrameIndexByName(lSpriteFrameName);
				}

				SpriteFrame lFrame = lSpriteSheetDef.getSpriteFrame(lFixtureInst.spriteIndex);

				if (lFrame == null)
					continue;

				Fixture lFixt = lFixtureInst.mFixture;
				if (lFixt == null)
					continue;

				if (lFixt.getShape() instanceof PolygonShape) {
					final PolygonShape fixtureShape = (PolygonShape) lFixt.getShape();
					mTextureBatch.drawPolygon(lTexture, lBody, fixtureShape.getVertices(), lFrame, -0.2f, 1f, 1f, 1f, 1f);

				}
			}
		}

		mTextureBatch.end();

	}
}