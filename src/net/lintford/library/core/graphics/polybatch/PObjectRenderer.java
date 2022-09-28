package net.lintford.library.core.graphics.polybatch;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;

import net.lintford.library.core.LintfordCore;
import net.lintford.library.core.ResourceManager;
import net.lintford.library.core.entity.JBox2dEntity;
import net.lintford.library.core.maths.Vector2f;

public class PObjectRenderer {

	// ---------------------------------------------
	// Variables
	// ---------------------------------------------

	private int mEntityGroupUid;
	private ResourceManager mResourceManager;
	private JBox2dPolyBatch mTextureBatch;

	static final int MAX_VERTS = 10;
	static Vector2f[] verts;
	static Vec2 vertex = new Vec2();
	static Vec2 tempVec = new Vec2();

	// ---------------------------------------------
	// Constructor
	// ---------------------------------------------

	public PObjectRenderer(int entityGroupUid) {
		mEntityGroupUid = entityGroupUid;
		mTextureBatch = new JBox2dPolyBatch();
	}

	// ---------------------------------------------
	// Core-Methods
	// ---------------------------------------------

	public void loadResources(ResourceManager resourceManager) {
		mResourceManager = resourceManager;
		mTextureBatch.loadResources(resourceManager);
	}

	public void unloadResources() {
		mTextureBatch.unloadResources();
	}

	public void handleInput(LintfordCore core) {

	}

	public void update(LintfordCore core) {

	}

	public void draw(LintfordCore core, JBox2dEntity pObject) {
		if (!pObject.hasPhysicsEntity())
			return;

		final var lSpriteSheetDefName = pObject.box2dEntityInstance().spriteSheetName;
		if (lSpriteSheetDefName == null)
			return;

		final var lSpriteSheetDef = mResourceManager.spriteSheetManager().getSpriteSheet(lSpriteSheetDefName, mEntityGroupUid);

		if (lSpriteSheetDef == null)
			return;

		if (verts == null) {
			verts = new Vector2f[MAX_VERTS];
			for (int i = 0; i < MAX_VERTS; i++) {
				verts[i] = new Vector2f();
			}
		}

		final var lEntityInst = pObject.box2dEntityInstance();
		final int lBodyCount = lEntityInst.bodies().size();
		if (lBodyCount == 0)
			return;

		mTextureBatch.begin(core.gameCamera());

		for (int i = 0; i < lBodyCount; i++) {
			final var lBodyInst = lEntityInst.bodies().get(i);
			final var lBody = lBodyInst.mBody;
			if (lBody == null)
				continue;

			final var lTexture = lSpriteSheetDef.texture();

			final int lFixtureCount = lBodyInst.mFixtures.length;
			for (int j = 0; j < lFixtureCount; j++) {
				final var lFixtureInst = lBodyInst.mFixtures[j];

				if (lFixtureInst.spriteIndex == -1) {
					String lSpriteFrameName = lFixtureInst.spriteName;
					lFixtureInst.spriteIndex = lSpriteSheetDef.getSpriteFrameIndexByName(lSpriteFrameName);
				}

				final var lSpriteFrame = lSpriteSheetDef.getSpriteFrame(lFixtureInst.spriteIndex);

				if (lSpriteFrame == null)
					continue;

				final var lFixture = lFixtureInst.mFixture;
				if (lFixture == null)
					continue;

				if (lFixture.getShape() instanceof PolygonShape) {
					final PolygonShape fixtureShape = (PolygonShape) lFixture.getShape();
					mTextureBatch.drawPolygon(lTexture, lBody, fixtureShape.getVertices(), lSpriteFrame, -0.2f, 1f, 1f, 1f, 1f);
				}
			}
		}

		mTextureBatch.end();
	}
}