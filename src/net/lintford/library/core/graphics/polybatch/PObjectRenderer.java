package net.lintford.library.core.graphics.polybatch;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.box2d.entity.Box2dBodyInstance;
import net.lintford.library.core.box2d.entity.Box2dFixtureInstance;
import net.lintford.library.core.box2d.entity.JBox2dEntityInstance;
import net.lintford.library.core.graphics.sprites.SpriteFrame;
import net.lintford.library.core.graphics.sprites.spritesheet.SpriteSheetDef;
import net.lintford.library.core.graphics.textures.Texture;
import net.lintford.library.core.maths.Vector2f;
import net.lintford.library.data.entities.JBox2dEntity;

public class PObjectRenderer {

	// ---------------------------------------------
	// Constants
	// ---------------------------------------------

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mEntityGroupID;
	private ResourceManager mResourceManager;
	private JBox2dPolyBatch mTextureBatch;

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

	public void loadGLContent(ResourceManager pResourceManager) {
		mResourceManager = pResourceManager;
		mTextureBatch.loadGLContent(pResourceManager);

	}

	public void unloadGLContent() {
		mTextureBatch.unloadGLContent();

	}

	public void handleInput(LintfordCore pCore) {

	}

	public void update(LintfordCore pCore) {

	}

	static final int MAX_VERTS = 10;
	static Vector2f[] verts;
	static Vec2 vertex = new Vec2();
	static Vec2 tempVec = new Vec2();

	public void draw(LintfordCore pCore, JBox2dEntity pPObject) {

		if (!pPObject.hasPhysicsEntity())
			return;

		if (verts == null) {
			verts = new Vector2f[MAX_VERTS];
			for (int i = 0; i < MAX_VERTS; i++) {
				verts[i] = new Vector2f();
			}
		}

		String lSpriteSheetDefName = pPObject.mJBox2dEntityInstance.spriteSheetName;
		if (lSpriteSheetDefName == null)
			return;

		SpriteSheetDef lSpriteSheetDef = mResourceManager.spriteSheetManager().getSpriteSheet(lSpriteSheetDefName, mEntityGroupID);

		if (lSpriteSheetDef == null)
			return;

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

				String lSpriteFrameName = lFixtureInst.spriteName;
				SpriteFrame lFrame = lSpriteSheetDef.getSpriteFrame(lSpriteFrameName);

				Fixture lFixt = lFixtureInst.mFixture;
				if (lFixt == null)
					continue;

				if (lFixt.getShape() instanceof PolygonShape) {
					PolygonShape fixtureShape = (PolygonShape) lFixt.getShape();
					int vSize = Math.min(fixtureShape.getVertexCount(), MAX_VERTS);

					for (int k = 0; k < vSize; k++) {
						vertex = fixtureShape.getVertex(k);

						Vec2 worldPoint = lBody.getWorldPoint(vertex);

						verts[k].x = worldPoint.x * (32f);
						verts[k].y = worldPoint.y * (32f);

					}

					mTextureBatch.drawPolygon(lTexture, verts, lFrame.x, lFrame.y, lFrame.w, lFrame.h, -0.01f, 1f, 1f, 1f, 1f);

				}

			}

		}

		mTextureBatch.end();

	}

	// ---------------------------------------------
	// Methods
	// ---------------------------------------------

}